package com.padc.powerfulintents.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.padc.powerfulintents.R;

import java.util.Calendar;

public class ContactActivity extends AppCompatActivity {


    private static final int RQ_CONTACT = 2;
    private FloatingActionButton fab;
    private TextView tvName, tvPhone;
    private ImageView ivProfile;

    public static Intent newIntent(Context context) {

        return new Intent(context, ContactActivity.class);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_ph);
        ivProfile = findViewById(R.id.iv_clock);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectContact();

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void selectContact() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, RQ_CONTACT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RQ_CONTACT) {

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

                    ivProfile.setImageURI(uri);

                } else {

                    ivProfile.setImageDrawable(getDrawable(R.drawable.ic_phone_book));
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
