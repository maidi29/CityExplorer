package de.mareike.cityexplorer;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import de.mareike.cityexplorer.R;

public class CreatePinboardActivity extends ActionBarActivity {

    Integer markerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                markerID= null;
            } else {
                markerID= extras.getInt("MarkerID");
            }
        } else {
            markerID = (Integer) savedInstanceState.getSerializable("MarkerID");
        }

        if (markerID == 1) {
            StartNoteActivity();
        }
        else if (markerID == 2) {
            StartDrawActivity();
        }
        else if (markerID == 3) {
            StartNoteActivity();
        }
        else if (markerID ==4) {
            StartPictureActivity();
        }
        else if (markerID ==5) {
            StartDrawActivity();
        }
        else if (markerID ==6) {
            StartDrawActivity();
        }
        else if (markerID ==7) {
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