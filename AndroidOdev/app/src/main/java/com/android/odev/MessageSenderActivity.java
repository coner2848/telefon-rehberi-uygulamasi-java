package com.android.odev;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.odev.adaptor.BriefCustomContactAdaptor;
import com.android.odev.adaptor.CustomContactAdaptor;
import com.android.odev.model.CustomContact;
import com.android.odev.service.ContactDatabaseService;

import java.util.ArrayList;
import java.util.List;

public class MessageSenderActivity extends Activity {

    private static final int PERMISSIONS_REQUEST_SEND_SMS = 200;

    List<CustomContact> contactListToView;
    String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_sender);

        EditText mesajBasligi = (EditText) findViewById(R.id.editTextMesajBasligi);
        EditText mesajIcerigi = (EditText) findViewById(R.id.editTextMesajIcerigi);
        Button buttonMesajGonder = (Button) findViewById(R.id.buttonMesajGonder);
        RadioGroup rg = (RadioGroup) findViewById(R.id.radio_group);
        groupName = ((RadioButton) findViewById(rg.getCheckedRadioButtonId())).getText().toString();

        ListView listView = (ListView) findViewById(R.id.briefContactsListView);
        ContactDatabaseService dbService = new ContactDatabaseService(getApplicationContext());
        List<CustomContact> contactList = dbService.getContactList();
        setAdapter(listView, contactList);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                groupName = ((RadioButton) findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                setAdapter(listView, contactList);
            }
        });

        buttonMesajGonder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
                } else {
                    SmsManager smsManager = SmsManager.getDefault();
                    for (CustomContact contact : contactListToView) {
                        smsManager.sendTextMessage(contact.getPhoneNumber(), null, mesajBasligi.getText().toString() + "\n" + mesajIcerigi.getText().toString(), null, null);
                    }
                    Toast.makeText(getApplicationContext(), "SMS " + groupName + " grubundaki tüm kişilere gönderildi!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setAdapter(ListView listView, List<CustomContact> contactList) {
        contactListToView = getContactsByGroupName(contactList, groupName);
        BriefCustomContactAdaptor contactAdaptor = new BriefCustomContactAdaptor(this, contactListToView);
        listView.setAdapter(contactAdaptor);
    }

    private List<CustomContact> getContactsByGroupName(List<CustomContact> contacts, String groupname) {
        List<CustomContact> list = new ArrayList<>();
        for (CustomContact contact : contacts) {
            if (groupname.equalsIgnoreCase(contact.getGroupName())) {
                list.add(contact);
            }
        }
        return list;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startActivity(new Intent(this, this.getClass()));
    }
}
