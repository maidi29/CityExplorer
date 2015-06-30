package de.mareike.cityexplorer;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import de.mareike.cityexplorer.R;

public class NoteActivity extends ActionBarActivity{
    Integer markerID;
    Button pinnenButton;
    EditText noteEditText;
    TextView creativeTaskText;
    String note;
    Context context = this;
    String calling = "activity";
    private SQLiteDatabase dbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_layout);

        pinnenButton = (Button) findViewById(R.id.pinnenButtonNote);
        noteEditText = (EditText) findViewById(R.id.noteEditText);
        creativeTaskText = (TextView) findViewById(R.id.creativeTaskText);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                markerID = null;
            } else {
                markerID = extras.getInt("MarkerID");

            }
        } else {
            markerID = (Integer) savedInstanceState.getSerializable("MarkerID");
        }

        if (markerID == 1) {
            creativeTaskText.setText(R.string.TaskText1);
        }
        else if (markerID == 3) {
            creativeTaskText.setText(getString(R.string.TaskText3));
        }
    }

    public void StartPinnen (View view) {
        calling = "upload";
        note = noteEditText.getText().toString();
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("note", note));
        params.add(new BasicNameValuePair("markerID", markerID.toString()));

        new AsyncTask<ApiConnector, Long, Boolean>() {
            @Override
            protected Boolean doInBackground(ApiConnector... apiConnectors) {
                return apiConnectors[0].uploadNoteToServer(params);

            }
        }.execute(new ApiConnector());

        DbHelper dbh = new DbHelper(context);
        Cursor cursor = getUpload(dbh);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                Log.d("Datenbank", "Inhalt: " + cursor.getString(0) + cursor.getString(1));
                if (Integer.parseInt(cursor.getString(0)) == 0) {
                    dbh.addUpload(dbh, 1,  markerID);
                    finish();
                }
                else if (Integer.parseInt(cursor.getString(0))== 1) {
                    finish();
                }
                else {
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
        intent.putExtra("calling", calling);
        startActivity(intent);
    }

    public void Pinnwand (View view) {
        Intent intent = new Intent(NoteActivity.this,PinboardActivity.class);
        intent.putExtra("MarkerID", markerID);
        intent.putExtra("calling", calling);
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

    public Cursor getUpload(DbHelper dbh) {
        String markerId = ""+markerID;
        dbase = dbh.getReadableDatabase();
        String columns[] = {dbh.UPLOAD, dbh.MARKERID};
        String args[] = {markerId};
        Cursor cursor = dbase.query(dbh.UPLOAD_TABLE, columns, dbh.MARKERID + " LIKE ?", args , null, null, null, null);
        return cursor;
    }
}

