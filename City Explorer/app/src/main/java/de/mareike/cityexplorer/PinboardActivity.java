package de.mareike.cityexplorer;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

import de.mareike.cityexplorer.R;

public class PinboardActivity extends ActionBarActivity{

    ListView getALlEntrysListView;
    private JSONArray jsonArray;
    String markerID;
    String entryID;
    Context context = this;
    ImageView heart;
    //ImageButton likeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pinboard_layout);

        getALlEntrysListView = (ListView) findViewById(R.id.getAllEntrysListView);
        heart = (ImageView) findViewById(R.id.heartImage);
        //likeButton = (ImageButton) findViewById(R.id.likeButton);

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

        new getAllEntrysTask().execute(new ApiConnector());

        getALlEntrysListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

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
                return true;
            }
        });
    }

    public  void setListAdapter(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
        this.getALlEntrysListView.setAdapter(new GetAllEntrysListViewAdapter(jsonArray,this));
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
        Intent intent = new Intent(PinboardActivity.this,Discover.class);
        intent.putExtra("MarkerID", markerID);
        startActivity(intent);
    }

}
