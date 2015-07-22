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


    //Lade alle Eintr�ge mit der jeweiligen ID des Markers �ber ein phpSkript auf dem Server herunter und gebe sie in einem Array zur�ck
    public JSONArray getAllEntrys(String markerID)
    {
        String url = "http://www.creepyhollow.bplaced.net/CityExplorer/getAllEntrys.php?markerID="+markerID;
        // Get HttpResponse Object from url.
        // Get HttpEntity from Http Response Object
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();  // Default HttpClient
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (ClientProtocolException e) {
            // Signals error in http protocol
            e.printStackTrace();
            //Log Errors Here
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Convert HttpEntity into JSON Array
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

    //Lade ein Bild �ber ein php-Skript auf den Server auf den Server
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

    //Lade einen Text �ber ein php-Skript auf dem Server auf den Server
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

    //Erh�he die Anzahl positiver Bewertungen bei der �bergebenen ID des Eintrags �ber ein php-Skript auf dem Server
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

    //Vermindere die Anzahl positiver Bewertungen bei der �bergebenen ID des Eintrags �ber ein php-Skript auf dem Server
    public Boolean dislike (List<NameValuePair> params) {
        String url = "http://www.creepyhollow.bplaced.net/CityExplorer/dislike.php";

        HttpEntity httpEntity = null;

        try
        {
            DefaultHttpClient httpClient = new DefaultHttpClient();  // Default HttpClient
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
