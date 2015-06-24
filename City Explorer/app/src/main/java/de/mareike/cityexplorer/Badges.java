package de.mareike.cityexplorer;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.mareike.cityexplorer.R;

public class Badges extends ActionBarActivity{

    Context context = this;
    DbHelper dbh;
    Cursor cursor;
    BadgesListViewAdapter adapter;
    Cursor c;
    int points;
    int uploadPoints;
    TextView pointsText;
    TextView uploadPointsText;
    TextView summary;
    ListView lv;
    int sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.badges_layout);

        pointsText = (TextView)findViewById(R.id.pointsText);
        uploadPointsText = (TextView) findViewById(R.id.uploadPointsText);
        summary = (TextView) findViewById(R.id.summary);
        lv = (ListView) findViewById(R.id.listView);

        String[] values = new String[] { "Erster Punkt - Erster Schritt", "Quizzer - 3 Punkte durch Quizzes verdient", "Kreativer Kopf - 3 Punkte durch Pinnwand-Uploads verdient",
                "Würzburg-Kenner - alle Marker in Würzburg abgehakt" };

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);

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


    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }



}
