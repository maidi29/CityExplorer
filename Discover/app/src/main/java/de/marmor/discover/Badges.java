package de.marmor.discover;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

public class Badges extends ActionBarActivity{

    Context context = this;
    DbHelper dbh;
    Cursor cursor;
    Cursor c;
    int points;
    int uploadPoints;
    TextView pointsText;
    TextView uploadPointsText;
    TextView summary;
    int sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.badges_layout);

        pointsText = (TextView)findViewById(R.id.pointsText);
        uploadPointsText = (TextView) findViewById(R.id.uploadPointsText);
        summary = (TextView) findViewById(R.id.summary);

        dbh  = new DbHelper(context);
        cursor = dbh.getScore(dbh);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                if (Integer.parseInt(cursor.getString(0)) == 5){
                    points++;
                }
            }
            while(cursor.moveToNext());
        }
        cursor.close();

        c = dbh.getUpload(dbh);
        c.moveToFirst();
        if (c.moveToFirst()) {
            do {
                if (Integer.parseInt(c.getString(0)) == 1){
                    uploadPoints++;
                }
            }
            while(c.moveToNext());
        }
        c.close();

        sum = points + uploadPoints;
        pointsText.setText(getString(R.string.badesQuizText) + points);
        uploadPointsText.setText(getString(R.string.badesPinnwandText) + uploadPoints);
        summary.setText(getString(R.string.badgesSummaryText) + sum);
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
