package de.mareike.cityexplorer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PictureActivity extends ActionBarActivity {
    Integer markerID;
    Button pinnenButton;
    ImageButton cameraButton;
    private File imageFile;
    Context context = this;
    Uri uriImage;
    TextView taskText;
    String calling = "activity";
    private SQLiteDatabase dbase;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_layout);

        //Layout-Elemente verbinden
        pinnenButton = (Button) findViewById(R.id.pinnenButtonPicture);
        cameraButton = (ImageButton) findViewById(R.id.cameraButton);
        taskText = (TextView) findViewById(R.id.taskText);

        //übergebene Marker ID entgegennehmen
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                markerID = null;
            } else {
                markerID = extras.getInt("MarkerID");
            }
        } else {
            markerID = (Integer) savedInstanceState.getSerializable("MarkerID");
        }

        //Je nach Marker ID den entsprechenden Aufgabentext setzen
        if (markerID == 4) {
            taskText.setText(getString(R.string.TaskText4));
        }
        else if (markerID == 7) {
            taskText.setText(getString(R.string.TaskText7));
        }
    }

    /*wird beim Klick auf den Kamera Button aufgerufen (XML)
    Smartphone-interne Kamera Starten und gemachtes Bild im png Format auf dem Gerät abspeichern*/
    public void startCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFolder = new File (Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM);
        imageFile = new File (imageFolder, UUID.randomUUID().toString()+".png");
        uriImage = Uri.fromFile(imageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Falls Foto gemacht wurde, die Adresse der Datei aufrufen
        if (requestCode == 0 && resultCode == getActivity().RESULT_OK) {
            String filePath = uriImage.getPath();

            //Bild im ImageView anzeigen
            try
            {
                File f = new File(filePath);
                ExifInterface exif = new ExifInterface(f.getPath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                int angle = 0;

                if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                {
                    angle = 90;
                }
                else if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                {
                    angle = 180;
                }
                else if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                {
                    angle = 270;
                }

                Matrix mat = new Matrix();
                mat.postRotate(angle);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;

                Bitmap bmp1 = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
                Bitmap bmp = Bitmap.createBitmap(bmp1, 0, 0, bmp1.getWidth(), bmp1.getHeight(), mat, true);
                OutputStream stream = new FileOutputStream(filePath);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

                final Bitmap image  = BitmapFactory.decodeFile(filePath);
                ImageView picture = (ImageView) findViewById(R.id.picture);
                picture.setImageBitmap(image);

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (OutOfMemoryError oom)
            {
                oom.printStackTrace();
            }
        }
    }

    //Methode wird beim Klick auf den Anpinnen-Button aufgerufen (XML)
    public void StartPinnenPicture (View view) {
        calling = "upload";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        //Absturz verhindern falls kein Bild gemacht wurde
        if (imageFile == null) {
            Toast.makeText(getBaseContext(), getString(R.string.toast_no_picture), Toast.LENGTH_LONG).show();
        }
        else {
            //Bild encodieren und an ApiConnector übergeben
            final Bitmap image = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
            String imageData = encodeTobase64(image);
            final List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("image", imageData));
            params.add(new BasicNameValuePair("markerID", markerID.toString()));

            new AsyncTask<ApiConnector, Long, Boolean>() {
                @Override
                protected Boolean doInBackground(ApiConnector... apiConnectors) {
                    return apiConnectors[0].uploadImageToServer(params);

                }
            }.execute(new ApiConnector());

            //Eintrag in Upload Tabelle erstellen; falls bereits einer besteht nichts tun
            DbHelper dbh = new DbHelper(context);
            Cursor cursor = getUpload(dbh);
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                do {
                    if (Integer.parseInt(cursor.getString(0)) == 0) {
                        dbh.addUpload(dbh, 1, markerID);
                        finish();
                    } else if (Integer.parseInt(cursor.getString(0)) == 1) {
                        finish();
                    } else {
                        dbh.addUpload(dbh, 1, markerID);
                        finish();
                    }
                }
                while (cursor.moveToNext());
            } else {
                dbh.addUpload(dbh, 1, markerID);
                finish();
            }
            cursor.close();

            //Pinborad Activity starten und neben Marker ID auch übergeben von welchem Button aus die Activity gestartet wurde
            Intent intent = new Intent(PictureActivity.this, PinboardActivity.class);
            intent.putExtra("MarkerID", markerID);
            intent.putExtra("calling", calling);
            startActivity(intent);
        }
    }

    //Methode wird beim Klick auf Pinnwand-Button aufgerufen (XML) und startet die PinboardActivity
    public void Pinnwand (View view) {
        Intent intent = new Intent(PictureActivity.this,PinboardActivity.class);
        intent.putExtra("MarkerID", markerID);
        intent.putExtra("calling", calling);
        startActivity(intent);
    }

    //Methode um Bild Datei zu enkodieren
    public static String encodeTobase64(Bitmap image) {
        System.gc();
        if (image == null) {
            return null;
        }
        Bitmap imagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    public Activity getActivity() {
        return activity;
    }

    //Zeiger definieren, der nur Einträge aus der Upload Tabelle mit dieser Marker ID anzeigt
    public Cursor getUpload(DbHelper dbh) {
        String markerId = ""+markerID;
        dbase = dbh.getReadableDatabase();
        String columns[] = {dbh.UPLOAD, dbh.MARKERID};
        String args[] = {markerId};
        Cursor cursor = dbase.query(dbh.UPLOAD_TABLE, columns, dbh.MARKERID + " LIKE ?", args , null, null, null, null);
        return cursor;
    }

    //Rotation verhindern
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

    //Bei Rotaton Zustand speichern und wiederhersetellen

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", uriImage);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        uriImage = savedInstanceState.getParcelable("file_uri");
    }

    //Beim Klick auf den Zurück-Button die DiscoverActivity mit ensprechender Marker ID öffnen
    @Override
    public void onBackPressed () {
        {
            Intent intent = new Intent(PictureActivity.this, DiscoverActivity.class);
            intent.putExtra("MarkerID", markerID);
            startActivity(intent);
        }
    }

}
