package de.marmor.discover;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private GoogleMap googleMap;
    Context context = this;
    Cursor cursor;
    Cursor c;
    boolean mark1;
    boolean mark2;
    boolean mark3;
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

            /*LocationManager mng = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = mng.getLastKnownLocation(mng.getBestProvider(new Criteria(), false));

            double lat = location.getLatitude();
            double lon = location.getLongitude();

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 300);
            googleMap.animateCamera(cameraUpdate);*/
        }
        catch(Exception e) {
            e.printStackTrace();
        }



        Marker marker1 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(49.793012, 9.926201))
                .title(getString(R.string.Title1))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        Marker marker2 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(49.792742, 9.939118))
                .title(getString(R.string.Title2))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        Marker marker3 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(49.793349, 9.932558))
                .title(getString(R.string.Title3))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        /*Marker marker4 = googleMap.addMarker(new MarkerOptions()
            .position(new LatLng(49.794438, 9.94746))
            .title("Mareike")
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));*/

        DbHelper dbh  = new DbHelper(context);
        cursor = dbh.getScore(dbh);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                if (Integer.parseInt(cursor.getString(0)) == 5
                        && marker1.getTitle().equals(cursor.getString(1))){
                    mark1 = true;
                }
                if (Integer.parseInt(cursor.getString(0)) == 5
                        && marker2.getTitle().equals(cursor.getString(1))) {
                    mark2 = true;
                }
                if (Integer.parseInt(cursor.getString(0)) == 5
                        && marker3.getTitle().equals(cursor.getString(1))) {
                    mark3 = true;
                }
            }
            while(cursor.moveToNext());
        }
        cursor.close();

        c = dbh.getUpload(dbh);
        c.moveToFirst();
        if (c.moveToFirst()) {
            do {
                if (Integer.parseInt(c.getString(0)) == 1
                        && marker1.getTitle().equals(c.getString(1)) && mark1){
                    marker1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.markerdone));
                }
                if (Integer.parseInt(c.getString(0)) == 1
                        && marker2.getTitle().equals(c.getString(1)) && mark2){
                    marker2.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.markerdone));
                }
                if (Integer.parseInt(c.getString(0)) == 1
                        && marker3.getTitle().equals(c.getString(1)) && mark3){
                    marker3.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.markerdone));
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

                //if (meters < 200 || marker.getIcon() == BitmapDescriptorFactory.fromResource(R.drawable.markerdone)) {
                    Intent intent = new Intent(MainActivity.this, Discover.class);
                    intent.putExtra("MarkerID", marker.getTitle());
                    startActivity(intent);
                /*} else {
                    Toast.makeText(getBaseContext(),"Gehe zur/zum "+ marker.getTitle()+" um diesen Marker zu oeffnen.", Toast.LENGTH_LONG).show();
                }*/
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openBadges() {
        Intent intent = new Intent(MainActivity.this, Badges.class);
        startActivity(intent);
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
