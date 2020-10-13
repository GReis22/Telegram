package com.gabriel.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ActivityTelegramMessage extends AppCompatActivity {
    private static final String TAG = "ActivityTelegramMessage";
    private Button btnMens;
    private Button btnCham;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telegram_message);

        btnMens = findViewById(R.id.btnMensagem);
        btnCham = findViewById(R.id.btnChamada);

        btnMens.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                entrarEmContatoTelegram("Olá. Envio esta mensagem");
            }
        });

        btnCham.setOnClickListener(new View.OnClickListener() {
            String numero = "+5592991900565"; /*Trocar o número*/

            @Override
            public void onClick(View view) {
                getContactIdByPhoneNumber(numero);
                getUriFromPhoneNumber(numero);
                сallToTelegramContact(numero);
            }
        });

    }

    private void entrarEmContatoTelegram(String message) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setPackage("org.telegram.messenger");
        if (intent != null) {
            intent.putExtra(Intent.EXTRA_TEXT, message);//
            startActivity(Intent.createChooser(intent, "Share with"));
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Telegram is not installed", Toast.LENGTH_SHORT).show();
        }
}

    private String getContactIdByPhoneNumber(String phoneNumber) {
        Log.e("-?",phoneNumber);
        ContentResolver contentResolver = getContentResolver();
        String contactId = "J";
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = new String[]{ContactsContract.PhoneLookup._ID};
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }
    Log.d("CREATE",contactId);
        return contactId;
    }

    private Uri getUriFromPhoneNumber(String phoneNumber) {
        Uri uri = null;
        String contactId = getContactIdByPhoneNumber(phoneNumber);
        String mimeTypeTelegram = "vnd.android.cursor.item/vnd.org.telegram.messenger.android.call";
        Cursor cursorTelegram = getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data._ID},
                ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                new String[]{contactId, mimeTypeTelegram}, null);
        if (cursorTelegram != null) {
            while (cursorTelegram.moveToNext()) {
                String id = cursorTelegram.getString(cursorTelegram.getColumnIndexOrThrow(ContactsContract.Data._ID));
                if (!TextUtils.isEmpty(id)) {
                    uri = Uri.parse(ContactsContract.Data.CONTENT_URI + "/" + id);
                    break;
                }
            }
            cursorTelegram.close();
        }
        return uri;
    }

    public void сallToTelegramContact(String phoneNumber) {
        Uri uri = getUriFromPhoneNumber(phoneNumber);
        Log.d(TAG, uri + "");
        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
