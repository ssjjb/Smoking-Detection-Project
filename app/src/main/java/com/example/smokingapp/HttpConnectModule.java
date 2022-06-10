package com.example.smokingapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;

public class HttpConnectModule extends AsyncTask<String, Void, String> {
    String clientKey = "####";
    private String str, receiveMsg;
    private String email, name, ipAddress, preName;
    private int flag;

    HttpConnectModule(String email, String name, String ipAddress, int flag, String preName){
        this.email = email;
        this.name = name;
        this.ipAddress = ipAddress;
        this.flag = flag;
        this.preName = preName;
    }

    @Override
    protected String doInBackground(String... params){
        HttpURLConnection conn = null;
        URL url = null;
        StringBuilder sb = new StringBuilder();
        try{
            url = new URL("http://10.1.4.122/moduleFunction");

            sb.append(URLEncoder.encode((String) "email", "UTF-8"));
            sb.append('=');
            sb.append(URLEncoder.encode((String) email, "UTF-8"));
            sb.append('&');
            sb.append(URLEncoder.encode((String) "name", "UTF-8"));
            sb.append('=');
            sb.append(URLEncoder.encode((String) name, "UTF-8"));
            sb.append('&');
            sb.append(URLEncoder.encode((String) "ip", "UTF-8"));
            sb.append('=');
            sb.append(URLEncoder.encode((String) ipAddress, "UTF-8"));
            sb.append('&');
            sb.append(URLEncoder.encode((String) "flag", "UTF-8"));
            sb.append('=');
            sb.append(URLEncoder.encode((String) Integer.toString(flag), "UTF-8"));
            sb.append('&');
            sb.append(URLEncoder.encode((String) "preName", "UTF-8"));
            sb.append('=');
            sb.append(URLEncoder.encode((String) preName, "UTF-8"));

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            os.write(sb.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            if(conn.getResponseCode() == conn.HTTP_OK){
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                receiveMsg = buffer.toString();
                reader.close();
                tmp.close();
            } else{
                Log.e("Connection Result", conn.getResponseCode() + "Error");
            }
        } catch (MalformedURLException e) {
            Log.e("http", "protocol invalid");
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            Log.e("http", "time out");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            conn.disconnect();
        }
        return receiveMsg;
    }
}