package com.example.smokingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.MediaController;
import android.widget.VideoView;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends AppCompatActivity{
    Context context;
    private int device_width = 0;
    private int device_height = 0;

    LibVLC vlc;
    MediaPlayer player;
    VLCVideoLayout layout;
    public static final Uri uri = Uri.parse("rtsp://192.168.137.202:8554/test");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        context = getApplicationContext();
        layout = (VLCVideoLayout) findViewById(R.id.data_player);

        DisplayMetrics display = getApplicationContext().getResources().getDisplayMetrics();
        device_width = display.widthPixels;
        device_height = display.heightPixels;

        final ArrayList<String> args = new ArrayList<>();

        args.add("--no-drop-late-frames");
        args.add("--no-skip-frames");
        args.add("--rtsp-tcp");
        args.add("-vvv");

        vlc = new LibVLC(context, args);
        player = new MediaPlayer(vlc);

    }

    @Override
    protected void onStart() {
        super.onStart();
        player.attachViews(layout, null, false, false);
        Media media = new Media(vlc, uri);
        media.setHWDecoderEnabled(true, false);
        media.addOption(":network-caching=600");

        player.setMedia(media);
        media.release();
        player.play();
    }

    @Override
    protected void onStop() {
        super.onStop();

        player.stop();
        player.detachViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        player.release();
        vlc.release();
    }
}