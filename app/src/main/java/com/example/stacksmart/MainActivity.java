package com.example.stacksmart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // General Activity setup and instantation

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.description);
        editText = findViewById((R.id.input));
        Button button = findViewById((R.id.button));

        textView.setText("Input the URL which you wish to program your NFC chip with.");
        editText.setHint("www.yourwebsite.com");
        button.setText("Program");

        // Hide for now as funcionality is complete
        button.setVisibility(View.INVISIBLE);

        // The button eventually will activate some of the funcionality currently stored in onNewIntent

        button.setOnClickListener(new View.OnClickListener() { // on click, we program the chip
            // no validation for now, this is a prototype after all and the nfc code is challenging
            // this doesn't block the device buzzing, but it wont open the current URL programmed in so we can program a new one
            @Override
            public void onClick(View v) {
                writeNFC(editText.getText().toString());
            }
        });

    }

    public void onResume() { // disable nfc while we are programming in a new one so we don't get focus taken away
        super.onResume();
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // I belive this is the actual line that intercepts NFC intents and prevents typical funcionality
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onNewIntent(Intent intent){

        super.onNewIntent(intent);

        // If the intent caught is a NFC tag, handle it
        Tag mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Ndef ndef = Ndef.get(mytag);

        try { // NFC has lots of cases where it can error out, catch all of them below and print

            // generate record for NFC via what is in the textbox
            NdefRecord recordNFC = NdefRecord.createUri(Uri.parse(editText.getText().toString()));

            // needs special object type NdefMessage in order to push to actual chip
            NdefMessage NDFUrl = new NdefMessage(recordNFC );

            try {
                ndef.connect();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            try {
                ndef.writeNdefMessage(NDFUrl);

                // If we've gotten this far we can consider this a success, inform the user
                Toast.makeText(this, "Added: " + editText.getText().toString() + " to Smart Stack", Toast.LENGTH_SHORT).show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (FormatException e) {
                e.printStackTrace();
            }
            try {
                ndef.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch(IllegalArgumentException e) {
            Toast.makeText(this, "Invalid URL format", Toast.LENGTH_SHORT).show();
        }


        // Some code we are saving for later:
        //ndef.writeNdefMessage(new NdefMessage(records));
        /*byte[] textBytes  = "test".getBytes();
        int textLength = textBytes.length;
        String lang       = "en";
        byte[] langBytes  = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        // Make an array of the proper size to hold the status byte, language, and text
        byte[] payload    = new byte[1 + langLength + textLength];
        // Set the status byte (which is only the length in our case of using UTF-8)
        payload[0] = (byte) langLength;
        // Copy langbytes and textbytes into the payload
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);
        // Create an Android NdefRecord with TNF=well known and RTD=text with the payload we created
        // There is a single record, so no ID is needed
         NdefRecord[] records = {new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload)};
        */


    }



    private void writeNFC(String url) {

    }


}