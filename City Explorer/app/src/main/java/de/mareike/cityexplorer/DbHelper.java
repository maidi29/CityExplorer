package de.mareike.cityexplorer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DbHelper extends SQLiteOpenHelper {
    //Versionsnummer um die Database bei jeder Aenderung zu upgraden
    private static final int DATABASE_VERSION = 18;
    // Database Name
    private static final String DATABASE_NAME = "CE";

    public static final String SCORE_TABLE = "score";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_SCORE = "SCORE";
    public static final String COLUMN_MARKERID = "MARKERID";

    public static final String UPLOAD_TABLE = "upload";
    public static final String ID = "ID";
    public static final String UPLOAD = "UPLOAD";
    public static final String MARKERID = "MARKERID";

    private static final String TABLE_QUEST = "quest";
    private static final String KEY_ID = "id";
    private static final String KEY_QUES = "question";
    private static final String KEY_ANSWER = "answer"; //correct option
    private static final String KEY_OPTA= "opta"; //option a
    private static final String KEY_OPTB= "optb"; //option b
    private static final String KEY_OPTC= "optc"; //option c

    private static final String TABLE_LIKES = "likes";
    private static final String LIKES_ID = "likes_id";
    private static final String LIKES_MARKERID = "likes_markerid";
    private static final String LIKES_POSITION = "likes_position";
    private static final String LIKES_LIKE = "likes_like";

    private SQLiteDatabase dbase;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Hier alle Tables erstellen
        dbase = db;

        String create_query = "CREATE TABLE IF NOT EXISTS " + SCORE_TABLE + " ( "
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SCORE + " INTEGER, "
                + COLUMN_MARKERID + " TEXT) ";
        db.execSQL(create_query);

        String query = "CREATE TABLE IF NOT EXISTS " + UPLOAD_TABLE + " ( "
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + UPLOAD + " INTEGER, "
                + MARKERID + " TEXT) ";
        db.execSQL(query);

        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_QUEST + " ( "
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_QUES + " TEXT, "
                + KEY_ANSWER+ " TEXT, "
                + KEY_OPTA +" TEXT, "
                + KEY_OPTB +" TEXT, "
                + KEY_OPTC+" TEXT)";
        db.execSQL(sql);
        addQuestions();

        String likes = "CREATE TABLE IF NOT EXISTS " + TABLE_LIKES + " ( "
                + LIKES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LIKES_MARKERID + " TEXT, "
                + LIKES_POSITION + " INTEGER, "
                + LIKES_LIKE + " INTEGER) ";
        db.execSQL(likes);
        //db.close();
    }

    public void addScore (DbHelper dbh, Integer score, String markerID) {
        dbase = dbh.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SCORE, score);
        cv.put(COLUMN_MARKERID, markerID);
        dbase.insert(SCORE_TABLE, null, cv);
    }

    public void updateScore (DbHelper dbh, Integer score, String markerID, Integer newScore) {
        dbase = dbh.getWritableDatabase();
        String selection = COLUMN_SCORE+ " LIKE ? AND "+ COLUMN_MARKERID + " LIKE ? ";
        String args[] = {score.toString(), markerID};
        ContentValues values = new ContentValues();
        values.put (COLUMN_SCORE, newScore);
        dbase.update(SCORE_TABLE, values, selection, args);
    }

    public Cursor getScore(DbHelper dbh) {
        dbase = dbh.getReadableDatabase();
        String columns[] = {COLUMN_SCORE, COLUMN_MARKERID};
        Cursor cursor = dbase.query(SCORE_TABLE, columns, null, null, null, null, null);
        return cursor;
    }


    public void addUpload (DbHelper dbh, Integer upload, String markerID) {
        dbase = dbh.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(UPLOAD, upload);
        cv.put(MARKERID, markerID);
        dbase.insert(UPLOAD_TABLE, null, cv);
    }

    public Cursor getUpload(DbHelper dbh) {
        dbase = dbh.getReadableDatabase();
        String columns[] = {UPLOAD, MARKERID};
        Cursor cursor = dbase.query(UPLOAD_TABLE, columns, null, null, null, null, null);
        return cursor;
    }


    private void addQuestions()
    {
        Question q1=new Question("Frage 1","Falsch", "Falsch", "Richtig", "Richtig");
        this.addQuestion(q1);
        Question q2=new Question("Frage 2","Falsch", "Falsch", "Richtig", "Richtig");
        this.addQuestion(q2);
        Question q3=new Question("Frage 3","Falsch", "Falsch", "Richtig", "Richtig");
        this.addQuestion(q3);
        Question q4=new Question("Frage 4","Falsch", "Falsch", "Richtig", "Richtig");
        this.addQuestion(q4);
        Question q5=new Question("Frage 5","Falsch", "Falsch", "Richtig", "Richtig");
        this.addQuestion(q5);
        Question q6=new Question("Frage 6","Falsch", "Falsch", "Richtig", "Richtig");
        this.addQuestion(q6);
        Question q7=new Question("Frage 7","Falsch", "Falsch", "Richtig", "Richtig");
        this.addQuestion(q7);
        Question q8=new Question("Frage 8","Falsch", "Falsch", "Richtig", "Richtig");
        this.addQuestion(q8);
        Question q9=new Question("Frage 9","Falsch", "Falsch", "Richtig", "Richtig");
        this.addQuestion(q9);
        Question q10=new Question("Frage 10","Falsch", "Falsch", "Richtig", "Richtig");
        this.addQuestion(q10);
        Question q11=new Question("Frage 11","Falsch", "Falsch", "Richtig", "Richtig");
        this.addQuestion(q11);
        Question q12=new Question("Frage 12","Falsch", "Falsch", "Richtig", "Richtig");
        this.addQuestion(q12);
        Question q13=new Question("Frage 13","Falsch", "Falsch", "Richtig", "Richtig");
        this.addQuestion(q13);
        Question q14=new Question("Frage 14","Falsch", "Falsch", "Richtig", "Richtig");
        this.addQuestion(q14);
        Question q15=new Question("Frage 15","Falsch", "Falsch", "Richtig", "Richtig");
        this.addQuestion(q15);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUEST);
        // Create tables again
        onCreate(db);
    }

    // Adding new question
    public void addQuestion(Question quest) {
        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_QUES, quest.getQUESTION());
        values.put(KEY_ANSWER, quest.getANSWER());
        values.put(KEY_OPTA, quest.getOPTA());
        values.put(KEY_OPTB, quest.getOPTB());
        values.put(KEY_OPTC, quest.getOPTC());
        // Inserting Row
        dbase.insert(TABLE_QUEST, null, values);
    }
    public List<Question> getAllQuestions() {
        List<Question> quesList = new ArrayList<Question>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_QUEST;
        dbase=this.getReadableDatabase();
        Cursor cursor = dbase.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Question quest = new Question();
                quest.setID(cursor.getInt(0));
                quest.setQUESTION(cursor.getString(1));
                quest.setANSWER(cursor.getString(2));
                quest.setOPTA(cursor.getString(3));
                quest.setOPTB(cursor.getString(4));
                quest.setOPTC(cursor.getString(5));
                quesList.add(quest);
            } while (cursor.moveToNext());
        }
        return quesList;
    }
    public int rowcount()
    {
        int row=0;
        String selectQuery = "SELECT  * FROM " + TABLE_QUEST;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        row=cursor.getCount();
        return row;
    }


    public void addLike (DbHelper dbh, String markerID, Integer position, Integer like) {
        dbase = dbh.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LIKES_MARKERID, markerID);
        cv.put(LIKES_POSITION, position);
        cv.put(LIKES_LIKE, like);
        dbase.insert(TABLE_LIKES, null, cv);
    }

    public void updateLike (DbHelper dbh, String markerID, Integer position, Integer like, Integer newLike) {
        dbase = dbh.getWritableDatabase();
        String selection = LIKES_POSITION+ " LIKE ? AND "+ LIKES_MARKERID + " LIKE ? ";
        String args[] = {like.toString(), markerID};
        ContentValues values = new ContentValues();
        values.put (LIKES_LIKE, newLike);
        dbase.update(TABLE_LIKES, values, selection, args);
    }

    public Cursor getLike(DbHelper dbh) {
        dbase = dbh.getReadableDatabase();
        String columns[] = {LIKES_MARKERID, LIKES_POSITION, LIKES_LIKE};
        Cursor cursor = dbase.query(TABLE_LIKES, columns, null, null, null, null, null);
        return cursor;
    }

}

