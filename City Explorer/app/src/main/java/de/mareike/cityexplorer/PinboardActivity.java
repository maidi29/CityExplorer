package de.mareike.cityexplorer;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
    public Integer markerID;
    String entryID;
    GetAllEntrysListViewAdapter.ListCell listCell;
    ImageView likeButton;
    Context context = this;
    String calling;
    private SQLiteDatabase dbase;
    DbHelper dbh = new DbHelper(context);
    public String pos;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pinboard_layout);

        listCell = new GetAllEntrysListViewAdapter.ListCell();
        getALlEntrysListView = (ListView) findViewById(R.id.getAllEntrysListView);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                markerID= null;
                calling = "activity";
            } else {
                markerID= extras.getInt("MarkerID");
                calling = extras.getString("calling");
            }
        } else {
            markerID = (Integer) savedInstanceState.getSerializable("MarkerID");
            calling = (String) savedInstanceState.getSerializable("calling");
        }

        new getAllEntrysTask().execute(new ApiConnector());

        getALlEntrysListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                i++;
                Handler handler = new Handler();
                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        i = 0;
                    }
                };

                if (i == 1) {
                    handler.postDelayed(r, 350);
                } else if (i == 2) {
                    i = 0;
                    likeButton = (ImageView) view.findViewById(R.id.heartImage);
                    pos = "" + position;
                    Cursor cursor = getLikes(dbh);
                    cursor.moveToFirst();
                    if (cursor.moveToFirst()) {
                        do {
                            if (Integer.parseInt(cursor.getString(2)) == 1) {
                                dbh.updateLike(dbh, markerID, position, Integer.parseInt(cursor.getString(2)), 0);
                                dislike(position);
                                cursor.close();
                            } else if (Integer.parseInt(cursor.getString(2)) == 0) {
                                dbh.updateLike(dbh, markerID, position, Integer.parseInt(cursor.getString(2)), 1);
                                likeUpload(position);
                                cursor.close();
                            } else {
                                dbh.addLike(dbh, markerID, position, 1);
                                likeUpload(position);
                                cursor.close();
                            }
                        } while (cursor.moveToNext());
                    } else {
                        dbh.addLike(dbh, markerID, position, 1);
                        likeUpload(position);
                        cursor.close();
                    }
                }

            }
        });
    }

    public  void setListAdapter(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
        this.getALlEntrysListView.setAdapter(new GetAllEntrysListViewAdapter(jsonArray, this, markerID));
    }

    private class getAllEntrysTask extends AsyncTask<ApiConnector,Long,JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            return params[0].getAllEntrys(markerID.toString());
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
            params.add(new BasicNameValuePair("markerID", markerID.toString()));

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
        params.add(new BasicNameValuePair("markerID", markerID.toString()));

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
        String args[] = {markerID.toString(), pos};
        Cursor cursor = dbase.query(dbh.TABLE_LIKES, columns, dbh.LIKES_MARKERID + " LIKE ? AND " + dbh.LIKES_POSITION + " LIKE ? ", args , null, null, null, null);
        return cursor;
    }

}
