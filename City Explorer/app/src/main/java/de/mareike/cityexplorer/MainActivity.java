package de.mareike.cityexplorer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.mareike.cityexplorer.R;


public class MainActivity extends ActionBarActivity {

    private GoogleMap googleMap;
    Context context = this;
    Cursor cursor;
    Cursor c;
    boolean mark1;
    boolean mark2;
    boolean mark3;
    boolean mark4;
    boolean mark5;
    boolean mark6;
    boolean mark7;
    int iconDone;
    int icon;
    int markerID;

    LocationManager locationManager;
    String provider;
    Criteria criteria;
    Location myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            if(googleMap == null) {
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            }
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            googleMap.setMyLocationEnabled(true);
            googleMap.setTrafficEnabled(true);
            googleMap.setIndoorEnabled(true);
            googleMap.setBuildingsEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, true);
            myLocation = locationManager.getLastKnownLocation(provider);
            double latitude = myLocation.getLatitude();
            double longitude = myLocation.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            iconDone = R.drawable.markerdone;
            icon = R.drawable.marker;
        }
        catch(Exception e) {
            e.printStackTrace();
        }



        Marker marker1 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(49.793012, 9.926201))
                .title(getString(R.string.Title1))
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(icon)));
        Marker marker2 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(49.792742, 9.939118))
                .title(getString(R.string.Title2))
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(icon)));
        Marker marker3 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(49.793349, 9.932558))
                .title(getString(R.string.Title3))
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(icon)));
        Marker marker4 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(49.796065, 9.925987))
                .title(getString(R.string.Title4))
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(icon)));
        Marker marker5 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(49.794958, 9.929248))
                .title(getString(R.string.Title5))
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(icon)));
        Marker marker6 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(49.793154, 9.928252))
                .title(getString(R.string.Title6))
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(icon)));
        Marker marker7 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(49.790160, 9.91847))
                .title(getString(R.string.Title7))
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(icon)));



        DbHelper dbh  = new DbHelper(context);
        cursor = dbh.getAllScores(dbh);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                Log.d("Get ALl Scores", ""+cursor.getString(1));
                if (Integer.parseInt(cursor.getString(0)) == 5
                        && Integer.parseInt(cursor.getString(1)) == 1){
                    mark1 = true;
                }
                if (Integer.parseInt(cursor.getString(0)) == 5
                        && Integer.parseInt(cursor.getString(1)) == 2){
                    mark2 = true;
                }
                if (Integer.parseInt(cursor.getString(0)) == 5
                        && Integer.parseInt(cursor.getString(1)) == 3) {
                    mark3 = true;
                }
                if (Integer.parseInt(cursor.getString(0)) == 5
                        && Integer.parseInt(cursor.getString(1)) == 4){
                    mark4 = true;
                }
                if (Integer.parseInt(cursor.getString(0)) == 5
                        && Integer.parseInt(cursor.getString(1)) == 5) {
                    mark5 = true;
                }
                if (Integer.parseInt(cursor.getString(0)) == 5
                        && Integer.parseInt(cursor.getString(1)) == 6) {
                    mark6 = true;
                }
                if (Integer.parseInt(cursor.getString(0)) == 5
                        && Integer.parseInt(cursor.getString(1)) == 7) {
                    mark7 = true;
                }
            }
            while(cursor.moveToNext());
        }
        cursor.close();

        c = dbh.getAllUploads(dbh);
        c.moveToFirst();
        if (c.moveToFirst()) {
            do {
                if (Integer.parseInt(c.getString(0)) == 1 && Integer.parseInt(c.getString(1)) == 1 && mark1){
                    marker1.setIcon(BitmapDescriptorFactory.fromResource(iconDone));
                    marker1.setSnippet(getString(R.string.done_Snippet));
                }
                if (Integer.parseInt(c.getString(0)) == 1
                        && Integer.parseInt(c.getString(1)) == 2  && mark2){
                    marker2.setIcon(BitmapDescriptorFactory.fromResource(iconDone));
                    marker2.setSnippet(getString(R.string.done_Snippet));
                }
                if (Integer.parseInt(c.getString(0)) == 1
                        && Integer.parseInt(c.getString(1)) == 3 && mark3){
                    marker3.setIcon(BitmapDescriptorFactory.fromResource(iconDone));
                    marker3.setSnippet(getString(R.string.done_Snippet));
                }
                if (Integer.parseInt(c.getString(0)) == 1
                        && Integer.parseInt(c.getString(1)) == 4 && mark4){
                    marker4.setIcon(BitmapDescriptorFactory.fromResource(iconDone));
                    marker4.setSnippet(getString(R.string.done_Snippet));
                }
                if (Integer.parseInt(c.getString(0)) == 1
                        && Integer.parseInt(c.getString(1)) == 5 && mark5){
                    marker5.setIcon(BitmapDescriptorFactory.fromResource(iconDone));
                    marker5.setSnippet(getString(R.string.done_Snippet));
                }
                if (Integer.parseInt(c.getString(0)) == 1
                        && Integer.parseInt(c.getString(1)) == 6 && mark6){
                    marker6.setIcon(BitmapDescriptorFactory.fromResource(iconDone));
                    marker6.setSnippet(getString(R.string.done_Snippet));
                }
                if (Integer.parseInt(c.getString(0)) == 1
                        && Integer.parseInt(c.getString(1)) == 7 && mark7){
                    marker7.setIcon(BitmapDescriptorFactory.fromResource(iconDone));
                    marker7.setSnippet(getString(R.string.done_Snippet));
                }
            }
            while(c.moveToNext());
        }
        c.close();


        googleMap.setInfoWindowAdapter(new UserInfoWindowAdapter(getLayoutInflater()));
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                LatLng markerPosition = marker.getPosition();
                myLocation = locationManager.getLastKnownLocation(provider);
                Location markerLoc = new Location("marker");
                markerLoc.setLatitude(markerPosition.latitude);
                markerLoc.setLongitude(markerPosition.longitude);
                float meters = myLocation.distanceTo(markerLoc);
                String markerTitle = marker.getTitle();
                if (markerTitle.equals(getString(R.string.Title1))) {
                   markerID = 1;
                }
                else if (markerTitle.equals(getString(R.string.Title2))) {
                    markerID = 2;
                }
                else if (markerTitle.equals(getString(R.string.Title3))) {
                    markerID = 3;
                }
                else if (markerTitle.equals(getString(R.string.Title4))) {
                    markerID = 4;
                }
                else if (markerTitle.equals(getString(R.string.Title5))) {
                    markerID = 5;
                }
                else if (markerTitle.equals(getString(R.string.Title6))) {
                    markerID = 6;
                }
                else if (markerTitle.equals(getString(R.string.Title7))) {
                    markerID = 7;
                }

                if (meters < 200 || marker.getSnippet().equals(getString(R.string.done_Snippet))) {
                    Intent intent = new Intent(MainActivity.this, Discover.class);
                    intent.putExtra("MarkerID", markerID);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getBaseContext(),getString(R.string.go_to_toast)+ marker.getTitle()+getString(R.string.go_to_toast_2), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

            switch (item.getItemId()) {
            case R.id.action_help:
                Toast.makeText(getBaseContext(),getString(R.string.mainactivity_help), Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_badges:
                openBadges();
                return true;
            case R.id.action_questionnaire:
                openQuestionnaire();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openBadges() {
        Intent intent = new Intent(MainActivity.this, Badges.class);
        startActivity(intent);
    }

    void openQuestionnaire() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_evaluation_start)
                .setMessage(getString(R.string.dialog_evaluation))
                .setPositiveButton(getString(R.string.dialog_evaluation_start), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent openURL = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.soscisurvey.de/CityExplorer"));
                        startActivity(openURL);
                    }
                })
                .setNegativeButton(getString(R.string.dialog_evaluation_later), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.ic_action_questionnaire)
                .show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        lockScreenRotation(Configuration.ORIENTATION_PORTRAIT);
    }

    private void lockScreenRotation(int orientation)
    {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onBackPressed () {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
