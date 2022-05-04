package com.example.smokingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private static final boolean LOGIN = true;
    private static final boolean SIGNUP = false;

    private Context context;

    boolean isSignUpEmail, isSignUpName, isSignUpPassword, isLoginEmail, isLoginPassword;
    boolean isDuplicate;

    EditText loginEmail, loginPassword, find_pw;
    EditText signUpEmail, signUpPassword, signUpName;

    TextView loginEmailText, loginPasswordText, loginFindPasswordButton;
    TextView signUpEmailText, signUpNameText, signUpPasswordText;

    ImageView loginAction, signUpAction;

    ConstraintLayout loginSection, signupSection;

    ImageView loginEmailTrueImage, loginEmailFalseImage;
    ImageView signUpNameTrueImage, signUpNameFalseImage;
    ImageView signUpEmailTrueImage, signUpEmailFalseImage;
    ImageView signUpPasswordTrueImage, signUpPasswordFalseImage;

    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;

    MotionLayout ml;

    View v_d;

    // firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;

    // DB class
    UserAccount user_account = new UserAccount();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        context = getApplicationContext();
        viewCasting();

        // login_email.setText("w0995g@gmail.com");
        // login_password.setText("asdfasdf");

        // get FireBase Data
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SmokingApp");

        // auto check login email
        loginEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            // b : true -> select state  false -> non-select state
            @Override
            public void onFocusChange(View view, boolean b) {
                isLoginEmail = false;
                loginEmailTrueImage.setVisibility(View.GONE);
                loginEmailFalseImage.setVisibility(View.GONE);
                String mail = loginEmail.getText().toString();
                set_stroke(loginEmail, b, true, true);
                if(!mail.equals("")) {
                    Pattern pt = Patterns.EMAIL_ADDRESS;
                    // check login email success
                    if (pt.matcher(mail).matches()) {
                        isLoginEmail = true;
                        String stringValue = getResources().getString(R.string.email);
                        setBoxImage(loginEmailText, stringValue, loginEmail, b, loginEmailTrueImage, true, true);
                    }

                    // check login email failed
                    else {
                        String stringValue = getResources().getString(R.string.login_error_email);
                        setBoxImage(loginEmailText, stringValue, loginEmail, b, loginEmailFalseImage, false, true);
                    }
                }
                else{
                    loginEmailText.setText(getResources().getString(R.string.email));
                    loginEmailText.setTextColor(getResources().getColor(R.color.login_section_title));
                    set_stroke(loginEmail, b, true, true);
                }
            }
        });

        // auto check login password
        loginPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                isLoginPassword = false;
                if(!b){
                    if(!loginPassword.getText().toString().equals(""))
                        isLoginPassword = true;
                }
                set_stroke(loginPassword, b, true, true);
            }
        });

        // auto check sign up email
        signUpEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                isSignUpEmail = false;
                signUpEmailTrueImage.setVisibility(View.GONE);
                signUpEmailFalseImage.setVisibility(View.GONE);
                String su_email = signUpEmail.getText().toString();
                set_stroke(signUpEmail, b, false, true);
                if(!su_email.equals("")){
                    Pattern pt = Patterns.EMAIL_ADDRESS;
                    if(pt.matcher(su_email).matches()){
                        // duplicate email check
                        duplicateEmailCheck(signUpEmail.getText().toString(), b);
                    } else{
                        // invalid syntax error
                        String stringValue = getResources().getString(R.string.signup_error_email);
                        setBoxImage(signUpEmailText, stringValue, signUpEmail, b, signUpEmailFalseImage, false, false);
                    }
                }
                else{
                    signUpEmailText.setText(getResources().getString(R.string.signup));
                    signUpEmailText.setTextColor(getResources().getColor(R.color.login_section_title));
                    set_stroke(signUpEmail, b, false, true);
                }
            }
        });

        // auto check sign up name
        signUpName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                isSignUpName = false;
                signUpNameTrueImage.setVisibility(View.GONE);
                signUpNameFalseImage.setVisibility(View.GONE);
                String su_name = signUpName.getText().toString();
                set_stroke(signUpName, b, false, true);
                if (!su_name.equals("")) {
                    // 영어, 한글만 입력 가능
                    if(Pattern.matches("^[a-zA-Z]+[가-힣]*$", su_name)){
                        isSignUpName = true;
                        String stringValue = getResources().getString(R.string.name);
                        setBoxImage(signUpNameText, stringValue, signUpName, b, signUpNameTrueImage, true, false);
                    }else{
                        String stringValue = getResources().getString(R.string.signup_error_name);
                        setBoxImage(signUpNameText, stringValue, signUpName, b, signUpNameFalseImage, false, false);
                    }
                }
                else{
                    signUpNameText.setText(getResources().getString(R.string.name));
                    signUpNameText.setTextColor(getResources().getColor(R.color.login_section_title));
                    set_stroke(signUpName, b, false, true);
                }
            }

        });

        // auto check sign up password
        signUpPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                isSignUpPassword = false;
                signUpPasswordTrueImage.setVisibility(View.GONE);
                signUpPasswordFalseImage.setVisibility(View.GONE);
                String su_pass = signUpPassword.getText().toString();
                set_stroke(signUpPassword, b, false, true);
                if(!su_pass.equals("")){
                    // 길이 제한
                    if((su_pass.length()>5) && (su_pass.length()<21)){
                        // pw check success
                        isSignUpPassword = true;
                        String stringValue = getResources().getString(R.string.password);
                        setBoxImage(signUpPasswordText, stringValue, signUpPassword, b, signUpPasswordTrueImage, true, false);
                    }else{
                        // pw check fail
                        String stringValue = getResources().getString(R.string.signup_error_pw);
                        setBoxImage(signUpPasswordText, stringValue, signUpPassword, b, signUpPasswordFalseImage, false, false);
                    }
                }
                else{
                    signUpPasswordText.setText(getResources().getString(R.string.password));
                    signUpPasswordText.setTextColor(getResources().getColor(R.color.login_section_title));
                    set_stroke(signUpPassword, b, false, true);
                }
            }
        });

        // action login
        loginAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginEmail.clearFocus();
                loginPassword.clearFocus();

                // after 5 sec, start
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(isLoginEmail && isLoginPassword) {
                            String email = loginEmail.getText().toString();
                            String password = loginPassword.getText().toString();
                            loginUser(email, password);
                        }
                    }
                }, 500);
            }
        });

        // action sign up
        signUpAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpEmail.clearFocus();
                signUpName.clearFocus();
                signUpPassword.clearFocus();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(isSignUpEmail && isSignUpName && isSignUpPassword) {
                            String email = signUpEmail.getText().toString();
                            String password = signUpPassword.getText().toString();
                            String name = signUpName.getText().toString();

                            createUser(email, password,name);
                        }
                    }
                }, 500);
            }
        });
        
        // find password
        loginFindPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findPassword();
            }
        });
    }
    // find view by id
    private void viewCasting(){
        // view section
        loginSection = (ConstraintLayout) findViewById(R.id.login_section);
        signupSection = (ConstraintLayout) findViewById(R.id.signup_section);

        // login box & text
        loginEmail = (EditText) findViewById(R.id.login_email);
        loginPassword = (EditText) findViewById(R.id.login_pw);
        loginEmailText = (TextView) findViewById(R.id.login_email_title);
        loginPasswordText = (TextView) findViewById(R.id.login_pw_title);

        // find password
        find_pw = (EditText) findViewById(R.id.find_pw);
        loginFindPasswordButton = (TextView) findViewById(R.id.login_find);

        // sign up box & text
        signUpEmail = (EditText) findViewById(R.id.signup_email);
        signUpPassword = (EditText) findViewById(R.id.signup_pw);
        signUpName = (EditText) findViewById(R.id.signup_name);
        signUpEmailText = (TextView) findViewById(R.id.signup_email_title);
        signUpNameText = (TextView) findViewById(R.id.signup_name_title);
        signUpPasswordText = (TextView) findViewById(R.id.signup_pw_title);

        // check image
        loginEmailTrueImage = (ImageView) findViewById(R.id.login_email_ok);
        loginEmailFalseImage = (ImageView) findViewById(R.id.login_email_fail);
        signUpEmailTrueImage = (ImageView) findViewById(R.id.signup_email_ok);
        signUpEmailFalseImage = (ImageView) findViewById(R.id.signup_email_fail);
        signUpNameTrueImage = (ImageView) findViewById(R.id.signup_name_ok);
        signUpNameFalseImage = (ImageView) findViewById(R.id.signup_name_fail);
        signUpPasswordTrueImage = (ImageView) findViewById(R.id.signup_pw_ok);
        signUpPasswordFalseImage = (ImageView) findViewById(R.id.signup_pw_fail);

        // ok button
        loginAction = (ImageView) findViewById(R.id.login_go);
        signUpAction = (ImageView) findViewById(R.id.signup_go);

        // motionLayout
        ml = (MotionLayout) findViewById(R.id.login_container);
    }

    // check image animate
    private void animateCheckImage(Drawable drawable){
        if (drawable instanceof AnimatedVectorDrawableCompat) {
            avd = (AnimatedVectorDrawableCompat) drawable;
            avd.start();
        } else if (drawable instanceof AnimatedVectorDrawable) {
            avd2 = (AnimatedVectorDrawable) drawable;
            avd2.start();
        }
    }

    // animate editBox
    private void setBoxImage(TextView textView, String stringValue, EditText editText, boolean b, ImageView checkImage, boolean isSuccess, boolean isLogin){
        textView.setText(stringValue);
        set_stroke(editText, b, isLogin, isSuccess);

        if(isSuccess)
            textView.setTextColor(getResources().getColor(R.color.login_section_title));
        else
            textView.setTextColor(getResources().getColor(R.color.error));

        if(!editText.getText().toString().equals("")) {
            checkImage.setVisibility(View.VISIBLE);
            if (!b) {
                Drawable drawable = checkImage.getDrawable();
                animateCheckImage(drawable);
            }
        }
    }

    // editBox change (stroke, non-stroke)
    public void set_stroke(EditText e, boolean b, boolean isLogin, boolean isSuccess){
        int p = pxToDp(20);

        if(isSuccess){
            // select box
            if(b){
                if(isLogin) {
                    e.setBackgroundResource(R.drawable.login_edittext_background_stroke);
                    e.setPadding(p, p, p, p);
                }
                else{
                    e.setBackgroundResource(R.drawable.login_edittext_background_white_stroke);
                    e.setPadding(p, p, p, p);
                }
            }
            // non-select box
            else{
                if(isLogin) {
                    e.setBackgroundResource(R.drawable.login_edittext_background);
                    e.setPadding(p, p, p, p);
                }
                else{
                    e.setBackgroundResource(R.drawable.login_edittext_background_white);
                    e.setPadding(p, p, p, p);
                }
            }
        }
        else{
            if(isLogin) {
                e.setBackgroundResource(R.drawable.login_edittext_background_strokered);
                e.setPadding(p, p, p, p);
            }
            else{
                e.setBackgroundResource(R.drawable.login_edittext_background_white_strokered);
                e.setPadding(p, p, p, p);
            }
        }

    }
    
    // px -> dp
    int pxToDp(int value){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                context.getResources().getDisplayMetrics());
    }

    // firebase login
    private void loginUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user.isEmailVerified()) {
                        Intent t = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(t);
                        finish();

                        loginEmail.setText("");
                        loginPassword.setText("");
                    }
                    else{
                        // check generate-email
                        mAuth.signOut();
                        Toast.makeText(context, "인증메일을 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    // not password
                    Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "비밀번호를 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    // firebase sign up
    private void createUser(String email, String password, String name){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d("LoginActivity", "Email sent.");
                                // 현재 mAuth 객체에 담긴 정보 user에 저장
                                FirebaseUser user = mAuth.getCurrentUser();

                                user_account.setIdToken(user.getUid());
                                user_account.setEmailId(user.getEmail());
                                user_account.setName(name);

                                mDatabaseRef.child("UserAccount").child(user.getUid()).setValue(user_account);

                                signUpName.setText("");
                                signUpEmail.setText("");
                                signUpPassword.setText("");

                                Toast.makeText(LoginActivity.this, "Sign up Success!", Toast.LENGTH_SHORT).show();
                                ml.transitionToStart();
                            }
                        }
                    });

                } else{
                    // failed sign up
                    Toast.makeText(LoginActivity.this, "Sign up fail!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // sign up email duplicate check
    public void duplicateEmailCheck(String text, boolean b){
        mDatabaseRef.child("UserAccount").orderByChild("emailId").equalTo(text).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    isDuplicate = true;
                    if(isDuplicate){
                        // all check
                        isSignUpEmail = true;
                        String stringValue = getResources().getString(R.string.email);
                        setBoxImage(signUpEmailText, stringValue, signUpEmail, b, signUpEmailTrueImage, true, false);
                    }
                }
                else{
                    // duplicate email error
                    isDuplicate = false;
                    String stringValue = getResources().getString(R.string.signup_error_email_duplicate);
                    setBoxImage(signUpEmailText, stringValue, signUpEmail, b, signUpEmailFalseImage, false, false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // find password
    private void findPassword(){
        AlertDialog.Builder d = new AlertDialog.Builder(LoginActivity.this);

        d.setTitle("");

        v_d = (View) View.inflate(LoginActivity.this, R.layout.dialog_findpw, null);
        d.setView(v_d);

        d.setPositiveButton("Send",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        find_pw = (EditText) v_d.findViewById(R.id.find_pw);

                        if(find_pw.toString().length()!=0){
                            String tmp_find_email = find_pw.getText().toString();
                            mAuth.sendPasswordResetEmail(tmp_find_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(LoginActivity.this, "Check the your email.", Toast.LENGTH_LONG).show();
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Send failed!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }

                    }
                });

        d.show();
    }
}
