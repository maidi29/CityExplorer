package de.mareike.cityexplorer;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;


public class ApiConnector {


    //Alle Einträge mit der jeweiligen ID des Markers über ein phpSkript auf dem Server herunterladen und in einem Array zurückgeben
    public JSONArray getAllEntrys(String markerID)
    {
        String url = "http://www.creepyhollow.bplaced.net/CityExplorer/getAllEntrys.php?markerID="+markerID;
        // HttpResponse Object der URL.
        // HttpEntity des Http Response Objects
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();  // Default HttpClient
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (ClientProtocolException e) {
            // Fehler im http protocol
            e.printStackTrace();
            //Log Errors Here
        } catch (IOException e) {
            e.printStackTrace();
        }
        //HttpEntity in JSON Array kovertieren
        JSONArray jsonArray = null;
        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);
                Log.e("Entity Response  : ", entityResponse);
                jsonArray = new JSONArray(entityResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    //Ein Bild über ein php-Skript auf den Server auf den Server laden
    public Boolean uploadImageToServer(List<NameValuePair> params) {
        String url = "http://www.creepyhollow.bplaced.net/CityExplorer/uploadImage.php";

        HttpEntity httpEntity = null;

        try
        {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            httpEntity = httpResponse.getEntity();

            String entityResponse = EntityUtils.toString(httpEntity);
            Log.e("Entity Response: ", entityResponse);

            return true;

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Einen Text über ein php-Skript auf dem Server auf den Server laden
    public Boolean uploadNoteToServer(List<NameValuePair> params) {
        String url = "http://www.creepyhollow.bplaced.net/CityExplorer/uploadNote.php";

        HttpEntity httpEntity = null;

        try
        {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            httpEntity = httpResponse.getEntity();

            String entityResponse = EntityUtils.toString(httpEntity);
            Log.e("Entity Response: ", entityResponse);

            return true;

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Anzahl positiver Bewertungen bei der übergebenen ID des Eintrags über ein php-Skript auf dem Server erhöhen
    public Boolean like (List<NameValuePair> params) {
        String url = "http://www.creepyhollow.bplaced.net/CityExplorer/like.php";

        HttpEntity httpEntity = null;

        try
        {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            httpEntity = httpResponse.getEntity();

            String entityResponse = EntityUtils.toString(httpEntity);
            Log.e("Entity Response: ", entityResponse);

            return true;

        } catch (ClientProtocolException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Anzahl positiver Bewertungen bei der übergebenen ID des Eintrags über ein php-Skript auf dem Server vermindern
    public Boolean dislike (List<NameValuePair> params) {
        String url = "http://www.creepyhollow.bplaced.net/CityExplorer/dislike.php";

        HttpEntity httpEntity = null;

        try
        {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            httpEntity = httpResponse.getEntity();

            String entityResponse = EntityUtils.toString(httpEntity);
            Log.e("Entity Response: ", entityResponse);

            return true;

        } catch (ClientProtocolException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}
