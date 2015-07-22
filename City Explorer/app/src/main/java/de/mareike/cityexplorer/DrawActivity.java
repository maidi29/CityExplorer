package de.mareike.cityexplorer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class DrawActivity extends ActionBarActivity implements OnClickListener {

    Button pinnenButton;
    Integer markerID;
    Context context = this;
    String calling = "activity";
    TextView taskText;

    private DrawingView drawView;
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn;
    private float smallBrush, mediumBrush, largeBrush;

    private SQLiteDatabase dbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_layout);

        //übergebene Marker ID entgegennehmen
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                markerID= null;
            } else {
                markerID= extras.getInt("MarkerID");
            }
        } else {
            markerID = (Integer) savedInstanceState.getSerializable("MarkerID");
        }


        //Layout Elemente verbinden und definieren
        pinnenButton = (Button) findViewById(R.id.pinnenButton);
        taskText = (TextView) findViewById(R.id.taskTextView);

        drawView = (DrawingView)findViewById(R.id.drawing);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);
        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);
        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        drawView.setBrushSize(mediumBrush);

        //Je nach marker ID die entsprechende Aufgabe anzeigen
        if (markerID == 2) {
            taskText.setText(getString(R.string.TaskText2));
        }
        else if (markerID == 5) {
            taskText.setText(getString(R.string.TaskText5));
        }
        else {
            taskText.setText("");
        }
    }

    //Klicks auf die Buttons abfangen
    @Override
    public void onClick(View view){
        /*Beim Klick auf den Stift Button wird ein Dialogfesnter mit 3 Stiftgröen angezeigt, je nachdem welche Größe
        in diesem Fenster ausgewählt wird, wird die Methode setBrushSize der Klasse DrawingView mit der ensprechenden Größe aufgerufen und
        das Dialogfesnter geschlossen*/
        if(view.getId()==R.id.draw_btn){
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle(getString(R.string.dialogTitleBrush));
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });
            brushDialog.show();
        }

        /*Beim Klick auf den Radiergummi wird analog zum Klick auf den Stift verfahren, nur dass die Methode setErase der Klasse DrawningView aufgerufen wird*/
        else if(view.getId()==R.id.erase_btn){
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle(getString(R.string.dialogTitleEraser));
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });
            brushDialog.show();
        }
        //Beim Klick auf das "Neue Dokument" wird ein Dialog angezeigt in dem abgefragt wird ob der Nutzer das aktuelle Bild verwerfen will. Wird bestätigt wird die Methode startNew() aufgerufen
        else if(view.getId()==R.id.new_btn){
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle(getString(R.string.dialogNewTitle));
            newDialog.setMessage(getString(R.string.dialogNewQuestion));
            newDialog.setPositiveButton(getString(R.string.dialogNewYes), new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton(getString(R.string.dialogNewCancel), new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
        }
        /*Beim Klick auf den Spreichern Button wird in einem Dialog Fesnter gefragt ob die Zeichnungn gespreichert werden soll, bei ja wird das Bild mit einem zufälligen Namen im Format png auf dem Gerät gespeichert
        und zur Bestätigung ein Hinweis angezeigt*/
        else if(view.getId()==R.id.save_btn){
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle(getString(R.string.dialogSaveTitle));
            saveDialog.setMessage(getString(R.string.dialogSaveQuestion));
            saveDialog.setPositiveButton(getString(R.string.dialogSaveYes), new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.setDrawingCacheEnabled(true);
                    String imgSaved = MediaStore.Images.Media.insertImage(
                            getContentResolver(), drawView.getDrawingCache(),
                            UUID.randomUUID().toString()+".png", "drawing");

                    if(imgSaved!=null){
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                getString(R.string.dialogSavedToast), Toast.LENGTH_SHORT);
                        savedToast.show();
                    }
                    else{
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                getString(R.string.dialogNotSavedToast), Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }

                    drawView.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton(getString(R.string.dialogSaveCancel), new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }

    }

    //Methode um ein Bild zu enkodieren
    public static String encodeTobase64(Bitmap image) {
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

    //Beim Klick auf den Button "Anpinnen" wird das Bild mit der obigen Methide enkodiert und gemeinsam mit der Marker ID an den ApiConnector übergeben, der anzeigt ob die Pinnwand über diesen oder den "Anpinneen" Button geöffnet wurde
    public void StartPinnen (View view) {
        calling = "upload";
        drawView.setDrawingCacheEnabled(true);
        final Bitmap image = Bitmap
                .createBitmap(drawView.getDrawingCache());
        drawView.setDrawingCacheEnabled(true);

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

        //Besteht bei diesem Marker noch kein Eintrag in der Upload Tabelle der Datenbank wird dieser hinzugefügt, besteht bereits einer passiert nichts
        DbHelper dbh = new DbHelper(context);
        Cursor cursor = getUpload(dbh);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
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

        //Dann wird die neue Activity gestartet in der die Pinnwand mit allen Einträgen angezeigt wiird
        Intent intent = new Intent(DrawActivity.this,PinboardActivity.class);
        intent.putExtra("MarkerID", markerID);
        intent.putExtra("calling", calling);
        startActivity(intent);
    }

    //Methode wird aufgerufen, wenn Nutzer auf Pinnwnad-Button klickt ohne etwas hochzuladen, es wird die neue PinboardActivity gestartet und neben der Marker ID ein String übergeben
    public void Pinnwand (View view) {
        Intent intent = new Intent(DrawActivity.this,PinboardActivity.class);
        intent.putExtra("MarkerID", markerID);
        intent.putExtra("calling", calling);
        startActivity(intent);
    }

    //wird eine Farbe angeklickt wird diese dem DrawingView über die Methode setColor mitgegeben
    public void paintClicked(View view){
        drawView.setErase(false);
        drawView.setBrushSize(drawView.getLastBrushSize());
        if(view!=currPaint){
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
        }

    }

    //Zeiger definieren um nur die Upload-Einträge zu dieser Marker ID zu erhalten
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

    //Beim Klick des Zurück-Buttons soll die DiscoverActivity mit der entsprechenden Marker ID aufgerufen werden
    @Override
    public void onBackPressed () {
        {
            Intent intent = new Intent(DrawActivity.this, DiscoverActivity.class);
            intent.putExtra("MarkerID", markerID);
            startActivity(intent);
        }
    }

}