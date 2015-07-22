package de.mareike.cityexplorer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class ResultActivity extends Activity {

    Integer markerID;
    Boolean done;
    TextView textResult;
    int score;
    Button saveButton;
    Context context = this;
    private SQLiteDatabase dbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);

        //Layout-Elemente verbinden
        saveButton = (Button) findViewById(R.id.saveButton);
        RatingBar bar=(RatingBar)findViewById(R.id.ratingBar1);
        bar.setNumStars(5);
        bar.setStepSize(0.5f);
        textResult = (TextView)findViewById(R.id.textResult);

        //übergebenen Score und Marker ID entgegennehmen
        Bundle b = getIntent().getExtras();
        score = b.getInt("score");
        markerID = b.getInt("markerID");
        //Sterne das Ergebnis anzeigen lassen
        bar.setRating(score);
        //Text je nach Ergebnis snzeigen
        switch (score)
        {
            case 1:
            case 2:
            case 3:
            case 4:textResult.setText("Versuche es noch einmal!");
                break;
            case 5:textResult.setText("Sehr gut, du hast es geschafft!");
                done = true;
                break;
        }

        //Beim Klick auf Button "Speichern und Zurück": Score in die Score Tabelle der SQLite Datenbank eintragen oder aktualisieren und die DiscoverActivity öffnen
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHelper dbh = new DbHelper(context);
                Cursor cursor = getScore(dbh);
                cursor.moveToFirst();
                if (cursor.moveToFirst()) {
                    do {
                        if (Integer.parseInt(cursor.getString(0)) < 5 && Integer.parseInt(cursor.getString(1)) == markerID) {
                            dbh.updateScore(dbh, Integer.parseInt(cursor.getString(0)), markerID, score);
                            finish();
                        }
                        else if (Integer.parseInt(cursor.getString(0))== 5 && Integer.parseInt(cursor.getString(1))==markerID) {
                                finish();
                            }
                    } while (cursor.moveToNext());
               }
                else {
                    dbh.addScore(dbh, score, markerID);
                    finish();
                }
                cursor.close();
                Intent intent = new Intent(ResultActivity.this, DiscoverActivity.class);
                intent.putExtra("MarkerID", markerID);
                startActivity(intent);
            }
        });
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

    //Beim Klick auf den "Zurück" Button wird die Discover Activity gestartet
    @Override
    public void onBackPressed () {
        Intent intent = new Intent(ResultActivity.this, DiscoverActivity.class);
        intent.putExtra("MarkerID", markerID);
        startActivity(intent);
    }

    //Zeiger definieren, der nur Einträge aus der Score Tabelle mit dieser Marker ID anzeigt
    public Cursor getScore(DbHelper dbh) {
        dbase = dbh.getReadableDatabase();
        String columns[] = {dbh.COLUMN_SCORE, dbh.COLUMN_MARKERID};
        String args[] = {markerID.toString()};
        Cursor cursor = dbase.query(dbh.SCORE_TABLE, columns, dbh.MARKERID + " LIKE ?", args , null, null, null, null);
        return cursor;
    }

}
