package de.mareike.cityexplorer;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.MalformedInputException;
import java.util.List;

import de.mareike.cityexplorer.R;

public class GetAllEntrysListViewAdapter extends BaseAdapter {

    private JSONArray dataArray;
    private Activity activity;
    private static LayoutInflater inflater = null;

    private static final String baseUrlForImage = "http://www.creepyhollow.bplaced.net/CityExplorer/CEimages/";

    public GetAllEntrysListViewAdapter(JSONArray jsonArray, Activity a) {
        this.dataArray = jsonArray;
        this.activity = a;

        inflater = (LayoutInflater)this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.dataArray.length();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ListCell cell;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.get_all_entry_list_view_cell, null);

            cell = new ListCell();
            cell.likes = (TextView) convertView.findViewById(R.id.listViewLikes);
            cell.note = (TextView) convertView.findViewById(R.id.listViewNote);
            cell.img = (ImageView) convertView.findViewById(R.id.listViewImg);

            convertView.setTag(cell);

        }
        else {
            cell = (ListCell)convertView.getTag();
        }

        try {
            JSONObject jsonObject = this.dataArray.getJSONObject(position);
            cell.likes.setText("Likes: "+ jsonObject.getString("likes"));
            cell.note.setText(jsonObject.getString("note"));

            String img = jsonObject.getString("image");
            String urlForImageInServer = baseUrlForImage + img;

            new AsyncTask<String, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... params) {
                    //download image
                    String url = params[0];
                    Bitmap icon = null;

                    try {
                        InputStream in = new java.net.URL(url).openStream();
                        icon = BitmapFactory.decodeStream(in);
                    } catch(MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return icon;
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    cell.img.setImageBitmap(result);
                }

            }.execute(urlForImageInServer);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;

    }

    public class ListCell {
        private TextView likes;
        private TextView note;
        private ImageView img;
        public ImageButton likeButton;
        public boolean touched;

    }


}
