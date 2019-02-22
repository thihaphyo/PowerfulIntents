package com.padc.powerfulintents;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static int RQ_VIDEO = 1;
    private static int RQ_CONTACT = 2;
    private Snackbar snack;
    private VideoView videoView;
    private TextView tvName, tvPhone;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button btnTimer = findViewById(R.id.btn_timer);
        videoView = findViewById(R.id.vv);
        tvName = findViewById(R.id.tv_name2);
        tvPhone = findViewById(R.id.tv_ph2);
        imageView = findViewById(R.id.iv_profile);

        btnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer("Meditate", 20);
            }
        });
        Button btnEvent = findViewById(R.id.btn_calender);
        btnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                snack = Snackbar.make(v, "Successfully Set Event", Snackbar.LENGTH_LONG);

                Calendar beginTime = Calendar.getInstance();
                beginTime.set(2019, 1, 22, 10, 30);
                Calendar endTime = Calendar.getInstance();
                endTime.set(2019, 1, 23, 12, 30);

                addEvent("Test Event", "Yangon"
                        , beginTime.getTimeInMillis()
                        , endTime.getTimeInMillis());

            }
        });


        Button btnVideo = findViewById(R.id.btn_video);
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                captureVideo("MyVideo");
            }
        });

        Button btnPickContact = findViewById(R.id.btn_contact);
        btnPickContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectContact();
            }
        });

        Button btnWeb = findViewById(R.id.btn_web);
        btnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchWeb("https://www.youtube.com/PUBGMOBILE");
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startTimer(String message, int seconds) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void addEvent(String title, String location, long begin, long end) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void captureVideo(String targetFilename) {
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


    public void selectContact() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, RQ_CONTACT);
        }
    }

    public void searchWeb(String query) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == RQ_VIDEO) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Video saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
                videoView.setVideoURI(data.getData());
                videoView.start();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video",
                        Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == RQ_CONTACT) {

            Uri contactUri = data.getData();
            Log.d("Name", ContactsContract.CommonDataKinds.Phone.DATA);
            String id, name = "", phone = "";
            int idx;
            Cursor cursor = getContentResolver().query(contactUri, null
                    , null, null
                    , null);
            if (cursor.moveToFirst()) {
                idx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                id = cursor.getString(idx);

                idx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                name = cursor.getString(idx);

                idx = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);
                if (cursor.getString(idx) != null) {

                    Uri uri = Uri.parse(cursor.getString(idx));

                    imageView.setImageURI(uri);

                } else {

                    imageView.setImageDrawable(getDrawable(R.mipmap.ic_launcher));
                }

                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                        null,
                        null);

                while (phones.moveToNext()) {

                    phone = phones.getString(
                            phones.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                    );
                }

                phones.close();


                Log.d("Data", id + "," + name + ",");
            }
            cursor.close();

            tvName.setText(name);

            tvPhone.setText(phone);


        }
    }
}
