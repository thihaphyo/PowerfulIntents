package com.padc.powerfulintents.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.padc.powerfulintents.R;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Calendar;

public class VideoActivity extends AppCompatActivity {


    private FloatingActionButton fab;
    private VideoView vv;
    private ImageView ivBack;
    private TextView tvText;
    private static int RQ_VIDEO = 1;

    public static Intent newIntent(Context context) {

        return new Intent(context, VideoActivity.class);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        vv = findViewById(R.id.vv);
        ivBack = findViewById(R.id.iv_clock);
        tvText = findViewById(R.id.tv_text);


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                captureVideo("MyVideo");

            }
        });


    }

    private void captureVideo(String targetFilename) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File videoFile =
                new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/" + targetFilename);
        if (videoFile.exists() && !videoFile.isDirectory()) {

            boolean deleted = videoFile.delete();
            if (deleted) {
                Log.e("MainActivity", "Deleted");
            }
        }
        Uri videoUri = Uri.fromFile(videoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.withAppendedPath(videoUri, targetFilename));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, RQ_VIDEO);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == RQ_VIDEO) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Video saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
                vv.setVideoURI(data.getData());
                vv.setVisibility(View.VISIBLE);
                ivBack.setVisibility(View.GONE);
                tvText.setVisibility(View.GONE);
                vv.start();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",
                        Toast.LENGTH_LONG).show();
                vv.setVisibility(View.GONE);
                ivBack.setVisibility(View.VISIBLE);
                tvText.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Failed to record video",
                        Toast.LENGTH_LONG).show();
                vv.setVisibility(View.GONE);
                ivBack.setVisibility(View.VISIBLE);
                tvText.setVisibility(View.VISIBLE);
            }
        }
    }
}
