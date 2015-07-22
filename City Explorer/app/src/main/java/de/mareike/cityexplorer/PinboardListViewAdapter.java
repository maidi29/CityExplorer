package de.mareike.cityexplorer;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PinboardListViewAdapter extends BaseAdapter {

    private JSONArray dataArray;
    private Activity activity;
    private static LayoutInflater inflater = null;
    public Cursor cursor;
    private SQLiteDatabase dbase;
    DbHelper dbh;
    private Context context;
    String pos;
    Integer markerID;
    public String objectID;


    //URL in der Bilder abgespeichert werden sollen
    private static final String baseUrlForImage = "http://www.creepyhollow.bplaced.net/CityExplorer/CEimages/";

    public PinboardListViewAdapter(JSONArray jsonArray, Context context, Integer markerID) {
        this.dataArray = jsonArray;
        this.context= context;
        this.markerID = markerID;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

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

        //Elemente der einzelnen Listen-Items verbinden
        final ListCell cell;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.pinboard_list_view_cell, null);
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
        cell.position = position;

        //Listen Items mit entsprechenden Elementen aus dem heruntergeladenen Array befüllen
        try {
            JSONObject jsonObject = this.dataArray.getJSONObject(position);
            cell.likes.setText(jsonObject.getString("likes"));
            cell.note.setText(jsonObject.getString("note"));
            cell.entryID = jsonObject.getString("id");
            String img = jsonObject.getString("image");
            String urlForImageInServer = baseUrlForImage + img;

            //Bild herunterladen mit der Picasso Bibliothek
            Picasso.with(context)
                .load(urlForImageInServer)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.no_picture)
                .into(cell.img);


            //Das Herz ausfüllen, fass der Nutzer den entsprechenden Eintrag positiv bewertet hat, also ein entsprechender Eintrag in der Like Tabelle der SQLite Datenbank besteht
            objectID  = ""+cell.entryID;
            dbh = new DbHelper(context);
            cursor = getLikes(dbh);
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                do {
                    if (Integer.parseInt(cursor.getString(2)) == 1) {
                        cell.likeImage.setImageResource(R.drawable.heart_filled);
                    }
                    else if (Integer.parseInt(cursor.getString(2)) == 0) {
                        cell.likeImage.setImageResource(R.drawable.heart);
                    }
                }
                while(cursor.moveToNext());
            }
            else {
                cursor.close();
            }
            cursor.close();

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;

    }

    //Elemente jedes Listen Items
    public static class ListCell {
        private TextView likes;
        private TextView note;
        private ImageView img;
        public ImageView likeImage;
        public int position;
        public String entryID;
    }

    //Zeiger für den Abruf der Likes bei der aktuellen Marker ID und der entsprechenen ID des Eeintrags
    public Cursor getLikes(DbHelper dbh) {
        dbase = dbh.getReadableDatabase();
        String columns[] = {dbh.LIKES_MARKERID, dbh.LIKES_ENTRYID, dbh.LIKES_LIKE};
        String selection = dbh.LIKES_MARKERID + " LIKE ? AND " + dbh.LIKES_ENTRYID + " LIKE ? ";
        String args[] = {markerID.toString(), objectID};
        Cursor cursor = dbase.query(dbh.TABLE_LIKES, columns, selection, args , null, null, null, null);
        return cursor;
    }


}
