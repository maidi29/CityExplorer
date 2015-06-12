package de.marmor.discover;


import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class CreatePinboardActivity extends ActionBarActivity {

    String markerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        if (markerID.equals(getString(R.string.Title1))) {
            StartNoteActivity();
        }
        else if (markerID.equals(getString(R.string.Title2))) {
            StartDrawActivity();
        }
        else if (markerID.equals(getString(R.string.Title3))) {
            StartPictureActivity();
        }
    }


    public void StartDrawActivity () {
        Intent intent = new Intent(CreatePinboardActivity.this,DrawActivity.class);
        intent.putExtra("MarkerID", markerID);
        startActivity(intent);
    }

    public void StartNoteActivity() {
        Intent intent = new Intent(CreatePinboardActivity.this,NoteActivity.class);
        intent.putExtra("MarkerID", markerID);
        startActivity(intent);
    }

    public void StartPictureActivity() {
        Intent intent = new Intent(CreatePinboardActivity.this,PictureActivity.class);
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