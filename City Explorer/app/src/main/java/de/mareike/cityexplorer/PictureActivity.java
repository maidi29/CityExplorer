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

import de.mareike.cityexplorer.R;

public class PictureActivity extends ActionBarActivity {
    Integer markerID;
    Button pinnenButton;
    ImageButton cameraButton;
    EditText noteEditText;
    private File imageFile;
    private Activity activity;
    Context context = this;
    Uri uriImage;
    TextView taskText;
    String calling = "activity";
    private SQLiteDatabase dbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_layout);

        pinnenButton = (Button) findViewById(R.id.pinnenButtonPicture);
        cameraButton = (ImageButton) findViewById(R.id.cameraButton);
        taskText = (TextView) findViewById(R.id.taskText);

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

        if (markerID == 4) {
            taskText.setText(getString(R.string.TaskText4));
        }
        else if (markerID == 7) {
            taskText.setText(getString(R.string.TaskText7));
        }
    }

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

        if (requestCode == 0 && resultCode == getActivity().RESULT_OK) {
            String filePath = uriImage.getPath();

            Log.d("FILE PATH CAMEREA:->", filePath);

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

    public void StartPinnenPicture (View view) {
        calling = "upload";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        final Bitmap image = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        Log.d("Pin", imageFile.toString());
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

        DbHelper dbh = new DbHelper(context);
        Cursor cursor = dbh.getAllUploads(dbh);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                Log.d("Datenbank", "Inhalt: "+cursor.getString(0)+cursor.getString(1));
                if (Integer.parseInt(cursor.getString(0)) == 0) {
                    dbh.addUpload(dbh, 1,  markerID);
                    finish();
                }
                else if (Integer.parseInt(cursor.getString(0))== 1) {
                    finish();
                }
                else {
                    dbh.addUpload(dbh, 1, markerID);
                    finish();
                }
            }
            while (cursor.moveToNext());
        }
        else {
            dbh.addUpload(dbh, 1, markerID);
            finish();
        }
        cursor.close();

        Intent intent = new Intent(PictureActivity.this,PinboardActivity.class);
        intent.putExtra("MarkerID", markerID);
        intent.putExtra("calling", calling);
        startActivity(intent);
    }

    public void Pinnwand (View view) {
        Intent intent = new Intent(PictureActivity.this,PinboardActivity.class);
        intent.putExtra("MarkerID", markerID);
        intent.putExtra("calling", calling);
        startActivity(intent);
    }

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

    public Cursor getUpload(DbHelper dbh) {
        String markerId = ""+markerID;
        dbase = dbh.getReadableDatabase();
        String columns[] = {dbh.UPLOAD, dbh.MARKERID};
        String args[] = {markerId};
        Cursor cursor = dbase.query(dbh.UPLOAD_TABLE, columns, dbh.MARKERID + " LIKE ?", args , null, null, null, null);
        return cursor;
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

    @Override
    public void onBackPressed () {
        {
            Intent intent = new Intent(PictureActivity.this, Discover.class);
            intent.putExtra("MarkerID", markerID);
            startActivity(intent);
        }
    }

}
