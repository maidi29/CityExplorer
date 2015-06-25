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

import de.mareike.cityexplorer.R;

public class ResultActivity extends Activity {

    String markerID;
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

        saveButton = (Button) findViewById(R.id.saveButton);
        RatingBar bar=(RatingBar)findViewById(R.id.ratingBar1);
        bar.setNumStars(5);
        bar.setStepSize(0.5f);
        textResult = (TextView)findViewById(R.id.textResult);

        Bundle b = getIntent().getExtras();
        score = b.getInt("score");
        markerID = b.getString("markerID");
        bar.setRating(score);
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


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHelper dbh = new DbHelper(context);

                Cursor cursor = getScore(dbh);
                cursor.moveToFirst();
                if (cursor.moveToFirst()) {
                    do {
                        Log.d("Database", "Inhalt: "+ cursor.getString(0) + cursor.getString(1));

                        if (Integer.parseInt(cursor.getString(0)) < 5 && cursor.getString(1).equals(markerID)) {
                        Log.d("Database", "Update");
                            dbh.updateScore(dbh, Integer.parseInt(cursor.getString(0)), markerID, score);
                            finish();
                        }
                        else if (Integer.parseInt(cursor.getString(0))== 5 &&cursor.getString(1).equals(markerID)) {
                            Log.d("Database", "Nothing");
                                finish();
                            }
                    } while (cursor.moveToNext());
               }
                else {
                    Log.d("Database", "Add Score");
                    dbh.addScore(dbh, score, markerID);
                    finish();
                }
                cursor.close();
                Intent intent = new Intent(ResultActivity.this, Discover.class);
                intent.putExtra("MarkerID", markerID);
                startActivity(intent);
            }
        });
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

    @Override
    public void onBackPressed () {
        Intent intent = new Intent(ResultActivity.this, Discover.class);
        intent.putExtra("MarkerID", markerID);
        startActivity(intent);
    }

    public Cursor getScore(DbHelper dbh) {
        dbase = dbh.getReadableDatabase();
        String columns[] = {dbh.COLUMN_SCORE, dbh.COLUMN_MARKERID};
        String args[] = {markerID};
        Cursor cursor = dbase.query(dbh.SCORE_TABLE, columns, dbh.MARKERID + " LIKE ?", args , null, null, null, null);
        return cursor;
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_result, menu);
        return true;
    }*/
}
