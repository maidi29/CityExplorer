package de.mareike.cityexplorer;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.mareike.cityexplorer.R;

public class PinboardActivity extends ActionBarActivity{

    ListView getALlEntrysListView;
    private JSONArray jsonArray;
    String markerID;
    String entryID;
    GetAllEntrysListViewAdapter.ListCell listCell;
    ImageView likeButton;
    Context context = this;
    String calling;
    private SQLiteDatabase dbase;
    DbHelper dbh = new DbHelper(context);
    public String pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pinboard_layout);

        listCell = new GetAllEntrysListViewAdapter.ListCell();
        getALlEntrysListView = (ListView) findViewById(R.id.getAllEntrysListView);
        //listCell.likeButton = (ImageButton) findViewById(R.id.likeButton);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                markerID= null;
                calling = "activity";
            } else {
                markerID= extras.getString("MarkerID");
                calling = extras.getString("calling");
            }
        } else {
            markerID = (String) savedInstanceState.getSerializable("MarkerID");
            calling = (String) savedInstanceState.getSerializable("calling");
        }

        new getAllEntrysTask().execute(new ApiConnector());

        getALlEntrysListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                likeButton = (ImageView) view.findViewById(R.id.heartImage);

                pos = ""+ position;
                Cursor cursor = getLikes(dbh);
                cursor.moveToFirst();
                if (cursor.moveToFirst()) {
                    do {
                        Log.d("Debug", "LongItemClick on " + position);
                        Log.d("Database", "Inhalt: " + cursor.getString(0) + cursor.getString(1) + cursor.getString(2));
                        if (Integer.parseInt(cursor.getString(2)) == 1) {
                            dbh.updateLike(dbh, markerID, position, Integer.parseInt(cursor.getString(2)), 0);
                            dislike(position);
                            Log.d("Debug", "Dislike");
                            cursor.close();
                            return true;
                        }
                        else if (Integer.parseInt(cursor.getString(2)) == 0) {
                            Log.d("Debug", "Change Dislike to Like");
                            dbh.updateLike(dbh, markerID, position, Integer.parseInt(cursor.getString(2)), 1);
                            likeUpload(position);
                            cursor.close();
                            return true;
                        }
                        else{
                            Log.d("Debug", "Neuer Like");
                            dbh.addLike(dbh, markerID, position, 1);
                            likeUpload(position);
                            cursor.close();
                            return true;
                        }
                    } while (cursor.moveToNext());
                } else {
                    Log.d("Debug", "Erster Eintrag");
                    dbh.addLike(dbh, markerID, position, 1);
                    likeUpload(position);
                    cursor.close();
                    return true;
                }
            }
        });
    }


    public  void setListAdapter(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
        this.getALlEntrysListView.setAdapter(new GetAllEntrysListViewAdapter(jsonArray, this));
    }

    private class getAllEntrysTask extends AsyncTask<ApiConnector,Long,JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            return params[0].getAllEntrys(markerID);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            setListAdapter(jsonArray);
        }
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
        if (calling.equals("activity")) {
            finish();
        }
        else if(calling.equals("upload")) {
            Intent intent = new Intent(PinboardActivity.this, Discover.class);
            intent.putExtra("MarkerID", markerID);
            startActivity(intent);
        }
    }

    public void likeUpload (int position){
        try {

            JSONObject entryClicked = jsonArray.getJSONObject(position);
            entryID = entryClicked.getString("id");

            final List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", entryID));
            params.add(new BasicNameValuePair("markerID", markerID));

            new AsyncTask<ApiConnector, Long, Boolean>() {
                @Override
                protected Boolean doInBackground(ApiConnector... apiConnectors) {
                    return apiConnectors[0].like(params);

                }
            }.execute(new ApiConnector());

            finish();
            startActivity(getIntent());
            //heart.setImageResource(R.drawable.heart_filled);

        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void dislike(int position) {
        try {

        JSONObject entryClicked = jsonArray.getJSONObject(position);
        entryID = entryClicked.getString("id");

        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", entryID));
        params.add(new BasicNameValuePair("markerID", markerID));

        new AsyncTask<ApiConnector, Long, Boolean>() {
            @Override
            protected Boolean doInBackground(ApiConnector... apiConnectors) {
                return apiConnectors[0].dislike(params);

            }
        }.execute(new ApiConnector());

        finish();
        startActivity(getIntent());
        //heart.setImageResource(R.drawable.heart_filled);

    }
    catch (JSONException e){
        e.printStackTrace();
    }
    }

    public Cursor getLikes(DbHelper dbh) {
        dbase = dbh.getReadableDatabase();
        String columns[] = {dbh.LIKES_MARKERID, dbh.LIKES_POSITION, dbh.LIKES_LIKE};
        String args[] = {markerID, pos};
        Cursor cursor = dbase.query(dbh.TABLE_LIKES, columns, dbh.LIKES_MARKERID + " LIKE ? AND " + dbh.LIKES_POSITION + " LIKE ? ", args , null, null, null, null);
        return cursor;
    }

}
