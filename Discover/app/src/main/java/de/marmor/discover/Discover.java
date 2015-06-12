package de.marmor.discover;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

public class Discover extends ActionBarActivity {

    TextView InfoText;
    TextView InfoUeberschrift;
    ImageView ImageDone;
    ImageView InfoImage;
    ImageView pointQuiz;
    ImageView pointPinnwand;
    Button QuizButton;
    Button PinboardButton;
    Boolean upload = false;
    Boolean quiz = false;

    String markerID;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_layout);

        InfoText = (TextView)findViewById(R.id.InfoText);
        InfoUeberschrift = (TextView)findViewById(R.id.InfoUeberschrift);
        //InfoImage = (ImageView)findViewById(R.id.InfoImage);
        ImageDone =(ImageView)findViewById(R.id.imageDone);
        QuizButton = (Button)findViewById(R.id.QuizButton);
        PinboardButton = (Button)findViewById(R.id.zumPinboardButton);
        pointQuiz = (ImageView) findViewById(R.id.pointQuiz);
        pointPinnwand = (ImageView) findViewById(R.id.pointPinnwand);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                markerID= null;
            } else {
                markerID= extras.getString("MarkerID");
            }
        } else {
            markerID = (String) savedInstanceState.getSerializable("MarkerID");
        }

        InfoUeberschrift.setText(markerID);

        if (markerID.equals(getString(R.string.Title1))){
            InfoText.setText(getString(R.string.Text1));
        }

        else if (markerID.equals(getString(R.string.Title2))){
            InfoText.setText(getString(R.string.Text2));
        }

        else if (markerID.equals(getString(R.string.Title3))){
            InfoText.setText(getString(R.string.Text3));
        }

        DbHelper dbh  = new DbHelper(context);
        Cursor cursor = dbh.getScore(dbh);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                if (Integer.parseInt(cursor.getString(0)) == 5
                        && markerID.equals(cursor.getString(1))){
                    pointQuiz.setImageResource(R.drawable.point_blue);
                    quiz = true;
                }
            }
            while(cursor.moveToNext());
        }
        cursor.close();

        Cursor c = dbh.getUpload(dbh);
        c.moveToFirst();
        if (c.moveToFirst()) {
            do {
                if (Integer.parseInt(c.getString(0)) == 1 && markerID.equals(c.getString(1))) {
                    pointPinnwand.setImageResource(R.drawable.point_blue);
                    upload = true;
                }
            }while (cursor.moveToNext());
        }
        c.close();


        if (upload && quiz) {
            ImageDone.setImageResource(R.drawable.point_done);
        }
    }

    public void StartQuiz (View v) {
        Intent intent = new Intent(Discover.this,QuizActivity.class);
        intent.putExtra("MarkerID", markerID);
        startActivity(intent);
    }

    public void StartPinboard (View v) {
        Intent intent = new Intent(Discover.this,CreatePinboardActivity.class);
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

    @Override
    public void onBackPressed () {
        Intent intent = new Intent(Discover.this, MainActivity.class);
        intent.putExtra("MarkerID", markerID);
        startActivity(intent);
    }


}
