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

        //Layout-Elemente verbinden
        pinnenButton = (Button) findViewById(R.id.pinnenButtonNote);
        noteEditText = (EditText) findViewById(R.id.noteEditText);
        creativeTaskText = (TextView) findViewById(R.id.creativeTaskText);

        //Übergebene Marker ID entgegennehmen
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

        //Je nach Marker ID den Aufgabentext setzen
        if (markerID == 1) {
            creativeTaskText.setText(R.string.TaskText1);
        }
        else if (markerID == 3) {
            creativeTaskText.setText(getString(R.string.TaskText3));
        }
        else if (markerID == 6) {
            creativeTaskText.setText(getString(R.string.TaskText6));
        }
    }

    /*Wird beim Klick auf den Annpinnen-Button gestartet (XML), den ins Textfeld eingegebenen Text, sowie die marker ID an ApiConnector übergeben*/
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

        //Einen Eintrag zur Upload Tabelle der SQLite Datenbank hinzufügen oder nichts tun falls bereits einer besteht
        DbHelper dbh = new DbHelper(context);
        Cursor cursor = getUpload(dbh);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
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

        //Die Pinborad Activity starten
        Intent intent = new Intent(NoteActivity.this,PinboardActivity.class);
        intent.putExtra("MarkerID", markerID);
        //Mitgeben, welcher Button die Pinnwand aufgerufen hat
        intent.putExtra("calling", calling);
        startActivity(intent);
    }

    //Beim Klick auf den Pinnwand Button einfach nur die Pinborad Activity öffnen ohne etwas hochzuladen
    public void Pinnwand (View view) {
        Intent intent = new Intent(NoteActivity.this,PinboardActivity.class);
        intent.putExtra("MarkerID", markerID);
        intent.putExtra("calling", calling);
        startActivity(intent);
    }

    //Zeiger definieren um nur die Einträge der Upload Tabelle durchzugehen, die zu dieser Marker ID erstellt wurden
    public Cursor getUpload(DbHelper dbh) {
        String markerId = ""+markerID;
        dbase = dbh.getReadableDatabase();
        String columns[] = {dbh.UPLOAD, dbh.MARKERID};
        String args[] = {markerId};
        Cursor cursor = dbase.query(dbh.UPLOAD_TABLE, columns, dbh.MARKERID + " LIKE ?", args , null, null, null, null);
        return cursor;
    }

    //Rotation verhindern
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

    //Beim Zurück-Button die DiscoverActivity  mit der entasprechenden Marker ID öffnen
    @Override
    public void onBackPressed () {
        {
            Intent intent = new Intent(NoteActivity.this, DiscoverActivity.class);
            intent.putExtra("MarkerID", markerID);
            startActivity(intent);
        }
    }
}

