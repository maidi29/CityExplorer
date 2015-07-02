package de.mareike.cityexplorer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import de.mareike.cityexplorer.R;

public class Discover extends ActionBarActivity {

    TextView InfoText;
    TextView InfoUeberschrift;
    ImageView ImageDone;
    TouchImageView InfoImage;
    TextView InfoImageSource;
    TextView InfoImageText;
    ImageView pointQuiz;
    ImageView pointPinnwand;
    Button QuizButton;
    Button PinboardButton;
    Boolean upload = false;
    Boolean quiz = false;
    private SQLiteDatabase dbase;
    int markerID;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_layout);

        InfoText = (TextView)findViewById(R.id.InfoText);
        InfoUeberschrift = (TextView)findViewById(R.id.InfoUeberschrift);
        InfoImage = (TouchImageView)findViewById(R.id.InfoImage);
        InfoImageText = (TextView) findViewById(R.id.InfoImageText);
        InfoImageSource = (TextView)findViewById(R.id.InfoImageSource);
        ImageDone =(ImageView)findViewById(R.id.imageDone);
        QuizButton = (Button)findViewById(R.id.QuizButton);
        PinboardButton = (Button)findViewById(R.id.zumPinboardButton);
        pointQuiz = (ImageView) findViewById(R.id.pointQuiz);
        pointPinnwand = (ImageView) findViewById(R.id.pointPinnwand);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                markerID = 0;
            } else {
                markerID= extras.getInt("MarkerID");
            }
        } else {
            markerID = (Integer) savedInstanceState.getSerializable("MarkerID");
        }

        if (markerID == 1){
            InfoUeberschrift.setText(getString(R.string.Title1));
            InfoText.setText(getString(R.string.Text1));
            InfoImage.setImageResource(R.drawable.altemainbruecke);
            InfoImageText.setText(getString(R.string.image_text_1));
            InfoImageSource.setText(getString(R.string.image_source_1));
        }
        else if (markerID == 2){
            InfoUeberschrift.setText(getString(R.string.Title2));
            InfoText.setText(getString(R.string.Text2));
            InfoImage.setImageResource(R.drawable.residenz);
            InfoImageText.setText(getString(R.string.image_text_2));
            InfoImageSource.setText(getString(R.string.image_source_2));
        }
        else if (markerID == 3){
            InfoUeberschrift.setText(getString(R.string.Title3));
            InfoText.setText(getString(R.string.Text3));
            InfoImage.setImageResource(R.drawable.kiliansdom);
            InfoImageText.setText(getString(R.string.image_text_3));
            InfoImageSource.setText(getString(R.string.image_source_3));
        }
        else if (markerID == 4){
            InfoUeberschrift.setText(getString(R.string.Title4));
            InfoText.setText(getString(R.string.Text4));
            InfoImage.setImageResource(R.drawable.alterkranen);
            InfoImageText.setText(getString(R.string.image_text_4));
            InfoImageSource.setText(getString(R.string.image_source_4));
        }
        else if (markerID == 5){
            InfoUeberschrift.setText(getString(R.string.Title5));
            InfoText.setText(getString(R.string.Text5));
            InfoImage.setImageResource(R.drawable.marienkapelle);
            InfoImageText.setText(getString(R.string.image_text_5));
            InfoImageSource.setText(getString(R.string.image_source_5));
        }
        else if (markerID == 6){
            InfoUeberschrift.setText(getString(R.string.Title6));
            InfoText.setText(getString(R.string.Text6));
            InfoImage.setImageResource(R.drawable.grafeneckart);
            InfoImageText.setText(getString(R.string.image_text_6));
            InfoImageSource.setText(getString(R.string.image_source_6));
        }
        else if (markerID == 7){
            InfoUeberschrift.setText(getString(R.string.Title7));
            InfoText.setText(getString(R.string.Text7));
            InfoImage.setImageResource(R.drawable.festungmarienberg);
            InfoImageText.setText(getString(R.string.image_text_7));
            InfoImageSource.setText(getString(R.string.image_source_7));
        }
        Log.d ("Debug", "MarkerID: " + markerID);
        DbHelper dbh  = new DbHelper(context);
        Cursor cursor = getScore(dbh);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                if (Integer.parseInt(cursor.getString(0)) == 5){
                    pointQuiz.setImageResource(R.drawable.point_blue_quiz);
                    quiz = true;
                }
            }
            while(cursor.moveToNext());
        }
        cursor.close();

        Cursor c = getUpload(dbh);
        c.moveToFirst();
        if (c.moveToFirst()) {
            do {
                if (Integer.parseInt(c.getString(0)) == 1) {
                    pointPinnwand.setImageResource(R.drawable.point_blue_pinboard);
                    upload = true;
                }
            }while (cursor.moveToNext());
        }
        c.close();

        /*if (upload && quiz) {
            ImageDone.setImageResource(R.drawable.point_done);
        }*/

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

    public Cursor getScore(DbHelper dbh) {
        String markerId = ""+markerID;
        dbase = dbh.getReadableDatabase();
        String columns[] = {dbh.COLUMN_SCORE, dbh.COLUMN_MARKERID};
        String args[] = {markerId};
        Cursor cursor = dbase.query(dbh.SCORE_TABLE, columns, dbh.MARKERID + " LIKE ?", args , null, null, null, null);
        return cursor;
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
