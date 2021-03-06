package de.mareike.cityexplorer;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BadgesActivity extends ActionBarActivity{

    Context context = this;
    DbHelper dbh;
    Cursor cursor;
    Cursor c;
    int points;
    int uploadPoints;
    TextView pointsText;
    TextView uploadPointsText;
    TextView summary;

    List<Badge> myBadges = new ArrayList<Badge>();
    int sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.badges_layout);

        //Layout-Elemente verbinden
        pointsText = (TextView)findViewById(R.id.pointsText);
        uploadPointsText = (TextView) findViewById(R.id.uploadPointsText);
        summary = (TextView) findViewById(R.id.summary);

        //Anzahl der erfolgreich abgeschlossenen Quizzes aus der SQLite Datenbank beziehen
        dbh  = new DbHelper(context);
        cursor = dbh.getAllScores(dbh);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {;
            do {
                if (Integer.parseInt(cursor.getString(0)) == 5){
                    points++;
                }
            }
            while(cursor.moveToNext());
        }
        cursor.close();

        //ANzahl der Uploads aus der SQLite Datenbank beziehen
        c = dbh.getAllUploads(dbh);
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

        //Gesamtpunktzahl berechnen und mit entsprechenden Texten anzeigen
        sum = points + uploadPoints;
        pointsText.setText(getString(R.string.badesQuizText) + points);
        uploadPointsText.setText(getString(R.string.badesPinnwandText) + uploadPoints);
        summary.setText(getString(R.string.badgesSummaryText) + sum);

        populateBadgeList();
        populateListView();

    }

    //Orden mit Titeln und Bildern definieren und bei welchen Punktzahlen sie angezeigt werden sollen
    private void populateBadgeList() {
        if (sum >= 1) {
            myBadges.add(new Badge(getString(R.string.badge1_titel), getString(R.string.badge1_subtitle), R.drawable.badge1));
        }
        if (points >= 1 && uploadPoints >= 1) {
            myBadges.add(new Badge(getString(R.string.badge5_titel), getString(R.string.badge5_subtitle), R.drawable.badge5));
        }
        if (points >= 3) {
            myBadges.add(new Badge(getString(R.string.badge2_titel), getString(R.string.badge2_subtitle), R.drawable.badge2));
        }
        if (uploadPoints >= 3) {
            myBadges.add(new Badge(getString(R.string.badge3_titel), getString(R.string.badge3_subtitle), R.drawable.badge3));
        }
        if (sum >= 14) {
            myBadges.add(new Badge(getString(R.string.badge4_titel), getString(R.string.badge4_subtitle), R.drawable.badge4));
        }
    }

    //Custom List Adapter setzen
    private void populateListView() {
        ArrayAdapter<Badge> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);
    }
    private class MyListAdapter extends ArrayAdapter<Badge> {
        public MyListAdapter() {
            super(BadgesActivity.this, R.layout.badges_list_view_cell, myBadges);
        }

        //Die einzelnen List Items mit den Orden und ihren Texten bef�llen
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;
            if(itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.badges_list_view_cell, parent, false);
            }

            Badge currentBadge = myBadges.get(position);

            ImageView itemIcon = (ImageView) itemView.findViewById(R.id.item_icon);
            itemIcon.setImageResource(currentBadge.getIconID());

            TextView itemTitle = (TextView) itemView.findViewById(R.id.item_title);
            itemTitle.setText(currentBadge.getTitle());

            TextView itemSubtitle = (TextView) itemView.findViewById(R.id.item_subTitle);
            itemSubtitle.setText(currentBadge.getSubtitle());

            return itemView;
        }
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


}
