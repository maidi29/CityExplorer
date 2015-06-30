package de.mareike.cityexplorer;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    public Cursor cursor;
    private SQLiteDatabase dbase;
    DbHelper dbh;
    Context context;
    String pos;
    PinboardActivity pinboard = new PinboardActivity();


    private static final String baseUrlForImage = "http://www.creepyhollow.bplaced.net/CityExplorer/CEimages/";

    public GetAllEntrysListViewAdapter(JSONArray jsonArray, Activity a) {
        this.dataArray = jsonArray;
        this.activity = a;
        context = pinboard;
        dbh = new DbHelper(context);
        inflater = (LayoutInflater)this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /*public GetAllEntrysListViewAdapter(Context context, Cursor cursor) {
        super();
        cursor = cursor;
        DbHelper dbh  = new DbHelper(context);
        inflater=LayoutInflater.from(context);
    }*/

    @Override
    public int getCount() {
        if (this.dataArray != null) {
            return this.dataArray.length();
        }
        else {
            return 0;
        }
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
            cell.likeImage = (ImageView) convertView.findViewById(R.id.heartImage);

            convertView.setTag(cell);

        }
        else {
            cell = (ListCell)convertView.getTag();
        }

        try {
            JSONObject jsonObject = this.dataArray.getJSONObject(position);
            cell.likes.setText(jsonObject.getString("likes"));
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

            /*pos = ""+position;
            cursor = getLikes(dbh);
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                do {
                    if (Integer.parseInt(cursor.getString(2)) == 1) {
                        cell.likeImage.setImageResource(R.drawable.heart_filled);
                    }
                }
                while(cursor.moveToNext());
            }
            else {
                cursor.close();
            }
            cursor.close();*/

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;

    }

    public static class ListCell {
        private TextView likes;
        private TextView note;
        private ImageView img;
        public ImageView likeImage;
        public boolean touched;

    }

    public Cursor getLikes(DbHelper dbh) {
        dbase = dbh.getReadableDatabase();
        String columns[] = {dbh.LIKES_MARKERID, dbh.LIKES_POSITION, dbh.LIKES_LIKE};
        String args[] = {pinboard.markerID.toString(), pos};
        Cursor cursor = dbase.query(dbh.TABLE_LIKES, columns, dbh.LIKES_MARKERID + " LIKE ? AND " + dbh.LIKES_POSITION + " LIKE ? ", args , null, null, null, null);
        return cursor;
    }


}
