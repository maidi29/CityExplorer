package de.marmor.discover;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class NoteActivity extends ActionBarActivity{
    String markerID;
    Button pinnenButton;
    EditText noteEditText;
    String note;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_layout);

        pinnenButton = (Button) findViewById(R.id.pinnenButtonNote);
        noteEditText = (EditText) findViewById(R.id.noteEditText);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                markerID = null;
            } else {
                markerID = extras.getString("MarkerID");
            }
        } else {
            markerID = (String) savedInstanceState.getSerializable("MarkerID");
        }
    }

    public void StartPinnen (View view) {
        note = noteEditText.getText().toString();
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("note", note));
        params.add(new BasicNameValuePair("markerID", markerID));

        new AsyncTask<ApiConnector, Long, Boolean>() {
            @Override
            protected Boolean doInBackground(ApiConnector... apiConnectors) {
                return apiConnectors[0].uploadNoteToServer(params);

            }
        }.execute(new ApiConnector());

        DbHelper dbh = new DbHelper(context);
        Cursor cursor = dbh.getUpload(dbh);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                Log.d("Datenbank", "Inhalt: " + cursor.getString(0) + cursor.getString(1));
                if (Integer.parseInt(cursor.getString(0)) == 0 && cursor.getString(1).equals(markerID)) {
                    dbh.addUpload(dbh, 1,  markerID);
                    finish();
                }
                else if (Integer.parseInt(cursor.getString(0))== 1 && cursor.getString(1).equals(markerID)) {
                    finish();
                }
                else if (!cursor.getString(1).equals(markerID)){
                    dbh.addUpload(dbh, 1, markerID);
                    finish();
                }
            }
            while (cursor.moveToNext());
        }
        else {
            dbh.addUpload(dbh, 1, markerID);
            finish();
        }
        cursor.close();

        Intent intent = new Intent(NoteActivity.this,PinboardActivity.class);
        intent.putExtra("MarkerID", markerID);
        startActivity(intent);
    }

    public void Pinnwand (View view) {
        Intent intent = new Intent(NoteActivity.this,PinboardActivity.class);
        intent.putExtra("MarkerID", markerID);
        startActivity(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        lockScreenRotation(Configuration.ORIENTATION_PORTRAIT);
    }

    private void lockScreenRotation(int orientation)
    {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}

