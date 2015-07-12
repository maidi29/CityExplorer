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
import android.widget.ImageView;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PinboardActivity extends ActionBarActivity{

    ListView getALlEntrysListView;
    PinboardListViewAdapter.ListCell listCell;

    public JSONArray jsonArray;
    public Integer markerID;
    String entryID;
    String calling;
    public String pos;
    int i = 0;

    ImageView likeButton;

    Context context = this;
    private SQLiteDatabase dbase;
    DbHelper dbh = new DbHelper(context);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pinboard_layout);

        listCell = new PinboardListViewAdapter.ListCell();
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
                    handler.postDelayed(r, 400);
                } else if (i == 2) {
                    i = 0;
                    try {
                        JSONObject entryClicked = jsonArray.getJSONObject(position);
                        entryID = entryClicked.getString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    likeButton = (ImageView) view.findViewById(R.id.heartImage);
                    pos = "" + position;
                    int objectID = Integer.parseInt(entryID);
                    Cursor cursor = getLikes(dbh);
                    cursor.moveToFirst();
                    if (cursor.moveToFirst()) {
                        do {
                            if (Integer.parseInt(cursor.getString(2)) == 1) {
                                dbh.updateLike(dbh, markerID, objectID, Integer.parseInt(cursor.getString(2)), 0);
                                dislike(entryID);
                                cursor.close();
                            } else if (Integer.parseInt(cursor.getString(2)) == 0) {
                                dbh.updateLike(dbh, markerID, objectID, Integer.parseInt(cursor.getString(2)), 1);
                                likeUpload(entryID);
                                cursor.close();
                            } else {
                                dbh.addLike(dbh, markerID, objectID, 1);
                                likeUpload(entryID);
                                cursor.close();
                            }
                        } while (cursor.moveToNext());
                    } else {
                        dbh.addLike(dbh, markerID, objectID, 1);
                        likeUpload(entryID);
                        cursor.close();
                    }
                }

            }
        });
    }

    public  void setListAdapter(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
        this.getALlEntrysListView.setAdapter(new PinboardListViewAdapter(jsonArray, this, markerID));
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
            Intent intent = new Intent(PinboardActivity.this, DiscoverActivity.class);
            intent.putExtra("MarkerID", markerID);
            startActivity(intent);
        }
    }

    public void likeUpload (String entryID){

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

    }

    private void dislike(String entryID) {

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

    }

    public Cursor getLikes(DbHelper dbh) {
        dbase = dbh.getReadableDatabase();
        String columns[] = {dbh.LIKES_MARKERID, dbh.LIKES_ENTRYID, dbh.LIKES_LIKE};
        String args[] = {markerID.toString(), entryID};
        Cursor cursor = dbase.query(dbh.TABLE_LIKES, columns, dbh.LIKES_MARKERID + " LIKE ? AND " + dbh.LIKES_ENTRYID + " LIKE ? ", args , null, null, null, null);
        return cursor;
    }

}
