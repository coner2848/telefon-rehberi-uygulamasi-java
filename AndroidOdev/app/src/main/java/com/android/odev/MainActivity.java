package com.android.odev;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.odev.adaptor.CustomContactAdaptor;
import com.android.odev.model.CustomContact;
import com.android.odev.service.ContactDatabaseService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends Activity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ContactDatabaseService dbService = new ContactDatabaseService(getApplicationContext());
        dbService.deleteAllContacts();

        Bitmap defaultContactProfile = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.noimage);

        ListView listView = (ListView) findViewById(R.id.briefContactsListView);
        List<CustomContact> allContacts = getAllContacts(this.getContentResolver(), defaultContactProfile);

        CustomContactAdaptor contactAdaptor = new CustomContactAdaptor(this, allContacts);
        listView.setAdapter(contactAdaptor);

        Button buttonSwitchToMsgSender = (Button) findViewById(R.id.buttonSwitchToMsgSender);
        buttonSwitchToMsgSender.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ContactDatabaseService dbService = new ContactDatabaseService(getApplicationContext());
                ArrayList<CustomContact> contactList = dbService.getContactList();
                if (contactList.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Lütfen grup oluşturun!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent a = new Intent(MainActivity.this, MessageSenderActivity.class);
                    a.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(a);
                }
            }
        });

        RadioGroup rg = (RadioGroup) findViewById(R.id.radio_group);
        groupName = ((RadioButton) findViewById(rg.getCheckedRadioButtonId())).getText().toString();
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                groupName = ((RadioButton) findViewById(rg.getCheckedRadioButtonId())).getText().toString();
            }
        });

        Button buttonGrubaEkle = (Button) findViewById(R.id.buttonGrubaEkle);
        buttonGrubaEkle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                List<CustomContact> contacts = contactAdaptor.getContacts();
                List<CustomContact> selectedContacts = new ArrayList<>();
                for (CustomContact contact : contacts) {
                    if (contact.isSelected()) {
                        selectedContacts.add(contact);
                    }
                }
                if (!selectedContacts.isEmpty()) {
                    ArrayList<CustomContact> allContactsInDB = dbService.getContactList();
                    for (CustomContact contact : selectedContacts) {
                        if (!existIn(allContactsInDB, contact.getPhoneNumber(), groupName)) {
                            dbService.addContact(contact.getPhoneNumber(), contact.getName(), groupName);
                        }
                        contact.setSelected(false);
                    }
                    contactAdaptor.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Lütfen kişi seçin!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean existIn(ArrayList<CustomContact> allContactsInDB, String phone, String groupName) {
        for (CustomContact contactInDB : allContactsInDB) {
            if (contactInDB.getPhoneNumber().equalsIgnoreCase(phone) && contactInDB.getGroupName().equalsIgnoreCase(groupName))
                return true;
        }
        return false;
    }

    public List<CustomContact> getAllContacts(ContentResolver cr, Bitmap defaultContactProfile) {

        List<CustomContact> contacts = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (phones.moveToNext()) {
                @SuppressLint("Range") String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                @SuppressLint("Range") String image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));

                CustomContact contact = new CustomContact();
                contact.setName(name);
                contact.setPhoneNumber(phoneNumber);
                contact.setProfile(getProfile(image_thumb, defaultContactProfile));
                contacts.add(contact);
            }
            phones.close();
        }
        Collections.sort(contacts, new ContactComparator());
        return contacts;
    }

    private Bitmap getProfile(String image_thumb, Bitmap defaultContactProfile) {
        Bitmap bit_thumb = null;
        try {
            if (image_thumb != null) {
                bit_thumb = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(image_thumb));
            } else {
                bit_thumb = defaultContactProfile;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bit_thumb;
    }

    class ContactComparator implements Comparator<CustomContact> {

        @Override
        public int compare(CustomContact o1, CustomContact o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startActivity(new Intent(this, this.getClass()));
    }
}