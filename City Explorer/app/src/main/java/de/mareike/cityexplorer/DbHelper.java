package de.mareike.cityexplorer;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 28;
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

    public static final String TABLE_LIKES = "likes";
    public static final String LIKES_ID = "likes_id";
    public static final String LIKES_MARKERID = "likes_markerid";
    public static final String LIKES_ENTRYID = "likes_entryid";
    public static final String LIKES_LIKE = "likes_like";

    private SQLiteDatabase dbase;

    private Context context;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        dbase = db;

        String create_query = "CREATE TABLE IF NOT EXISTS " + SCORE_TABLE + " ( "
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SCORE + " INTEGER, "
                + COLUMN_MARKERID + " INTEGER) ";
        db.execSQL(create_query);

        String query = "CREATE TABLE IF NOT EXISTS " + UPLOAD_TABLE + " ( "
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + UPLOAD + " INTEGER, "
                + MARKERID + " INTEGER) ";
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
                + LIKES_MARKERID + " INTEGER, "
                + LIKES_ENTRYID + " INTEGER, "
                + LIKES_LIKE + " INTEGER) ";
        db.execSQL(likes);
        //db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUEST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIKES);
        db.execSQL("DROP TABLE IF EXISTS " + SCORE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + UPLOAD_TABLE);
        onCreate(db);
    }

    public void addScore (DbHelper dbh, Integer score, Integer markerID) {
        dbase = dbh.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SCORE, score);
        cv.put(COLUMN_MARKERID, markerID);
        dbase.insert(SCORE_TABLE, null, cv);
    }

    public void updateScore (DbHelper dbh, Integer score, Integer markerID, Integer newScore) {
        dbase = dbh.getWritableDatabase();
        String selection = COLUMN_SCORE+ " LIKE ? AND "+ COLUMN_MARKERID + " LIKE ? ";
        String args[] = {score.toString(), markerID.toString()};
        ContentValues values = new ContentValues();
        values.put (COLUMN_SCORE, newScore);
        dbase.update(SCORE_TABLE, values, selection, args);
    }

    public Cursor getAllScores(DbHelper dbh) {
        dbase = dbh.getReadableDatabase();
        String columns[] = {COLUMN_SCORE, COLUMN_MARKERID};
        Cursor cursor = dbase.query(SCORE_TABLE, columns, null, null, null, null, null);
        return cursor;
    }


    public void addUpload (DbHelper dbh, Integer upload, Integer markerID) {
        dbase = dbh.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(UPLOAD, upload);
        cv.put(MARKERID, markerID);
        dbase.insert(UPLOAD_TABLE, null, cv);
    }

    public Cursor getAllUploads(DbHelper dbh) {
        dbase = dbh.getReadableDatabase();
        String columns[] = {UPLOAD, MARKERID};
        Cursor cursor = dbase.query(UPLOAD_TABLE, columns, null, null, null, null, null);
        return cursor;
    }

    public void addLike (DbHelper dbh, Integer markerID, Integer entryID, Integer like) {
        dbase = dbh.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LIKES_MARKERID, markerID);
        cv.put(LIKES_ENTRYID, entryID);
        cv.put(LIKES_LIKE, like);
        dbase.insert(TABLE_LIKES, null, cv);
    }

    public void updateLike (DbHelper dbh, Integer markerID, Integer entryID, Integer like, Integer newLike) {
        dbase = dbh.getWritableDatabase();
        String selection = LIKES_ENTRYID + " LIKE ? AND " + LIKES_MARKERID + " LIKE ? ";
        String args[] = {entryID.toString(), markerID.toString()};
        ContentValues values = new ContentValues();
        values.put (LIKES_LIKE, newLike);
        dbase.update(TABLE_LIKES, values, selection, args);
    }




    public void addQuestion(Question quest) {
        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_QUES, quest.getQUESTION());
        values.put(KEY_ANSWER, quest.getANSWER());
        values.put(KEY_OPTA, quest.getOPTA());
        values.put(KEY_OPTB, quest.getOPTB());
        values.put(KEY_OPTC, quest.getOPTC());

        dbase.insert(TABLE_QUEST, null, values);
    }

    public List<Question> getAllQuestions() {
        List<Question> quesList = new ArrayList<Question>();
        String selectQuery = "SELECT  * FROM " + TABLE_QUEST;
        dbase=this.getReadableDatabase();
        Cursor cursor = dbase.rawQuery(selectQuery, null);

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

    /*public int rowcount()
    {   int row=0;
        String selectQuery = "SELECT  * FROM " + TABLE_QUEST;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        row=cursor.getCount();
        return row;
    }*/

    private void addQuestions()
    {
        Question q1=new Question(context.getString(R.string.Quest1), context.getString(R.string.Answer1A), context.getString(R.string.Answer1B), context.getString(R.string.Answer1C), context.getString(R.string.Answer1A));
        this.addQuestion(q1);
        Question q2=new Question(context.getString(R.string.Quest2), context.getString(R.string.Answer2A), context.getString(R.string.Answer2B), context.getString(R.string.Answer2C), context.getString(R.string.Answer2C));
        this.addQuestion(q2);
        Question q3=new Question(context.getString(R.string.Quest3), context.getString(R.string.Answer3A), context.getString(R.string.Answer3B), context.getString(R.string.Answer3C), context.getString(R.string.Answer3A));
        this.addQuestion(q3);
        Question q4=new Question(context.getString(R.string.Quest4), context.getString(R.string.Answer4A), context.getString(R.string.Answer4B), context.getString(R.string.Answer4C), context.getString(R.string.Answer4B));
        this.addQuestion(q4);
        Question q5=new Question(context.getString(R.string.Quest5), context.getString(R.string.Answer5A), context.getString(R.string.Answer5B), context.getString(R.string.Answer5C), context.getString(R.string.Answer5C));
        this.addQuestion(q5);
        Question q6=new Question(context.getString(R.string.Quest6), context.getString(R.string.Answer6A), context.getString(R.string.Answer6B), context.getString(R.string.Answer6C), context.getString(R.string.Answer6B));
        this.addQuestion(q6);
        Question q7=new Question(context.getString(R.string.Quest7), context.getString(R.string.Answer7A), context.getString(R.string.Answer7B), context.getString(R.string.Answer7C), context.getString(R.string.Answer7A));
        this.addQuestion(q7);
        Question q8=new Question(context.getString(R.string.Quest8), context.getString(R.string.Answer8A), context.getString(R.string.Answer8B), context.getString(R.string.Answer8C), context.getString(R.string.Answer8C));
        this.addQuestion(q8);
        Question q9=new Question(context.getString(R.string.Quest9), context.getString(R.string.Answer9A), context.getString(R.string.Answer9B), context.getString(R.string.Answer9C), context.getString(R.string.Answer9B));
        this.addQuestion(q9);
        Question q10=new Question(context.getString(R.string.Quest10), context.getString(R.string.Answer10A), context.getString(R.string.Answer10B), context.getString(R.string.Answer10C), context.getString(R.string.Answer10A));
        this.addQuestion(q10);
        Question q11=new Question(context.getString(R.string.Quest11), context.getString(R.string.Answer11A), context.getString(R.string.Answer11B), context.getString(R.string.Answer11C), context.getString(R.string.Answer11B));
        this.addQuestion(q11);
        Question q12=new Question(context.getString(R.string.Quest12), context.getString(R.string.Answer12A), context.getString(R.string.Answer12B), context.getString(R.string.Answer12C), context.getString(R.string.Answer12C));
        this.addQuestion(q12);
        Question q13=new Question(context.getString(R.string.Quest13), context.getString(R.string.Answer13A), context.getString(R.string.Answer13B), context.getString(R.string.Answer13C), context.getString(R.string.Answer13A));
        this.addQuestion(q13);
        Question q14=new Question(context.getString(R.string.Quest14), context.getString(R.string.Answer14A), context.getString(R.string.Answer14B), context.getString(R.string.Answer14C), context.getString(R.string.Answer14C));
        this.addQuestion(q14);
        Question q15=new Question(context.getString(R.string.Quest15), context.getString(R.string.Answer15A), context.getString(R.string.Answer15B), context.getString(R.string.Answer15C), context.getString(R.string.Answer15B));
        this.addQuestion(q15);
        Question q16=new Question(context.getString(R.string.Quest16), context.getString(R.string.Answer16A), context.getString(R.string.Answer16B), context.getString(R.string.Answer16C), context.getString(R.string.Answer16B));
        this.addQuestion(q16);
        Question q17=new Question(context.getString(R.string.Quest17), context.getString(R.string.Answer17A), context.getString(R.string.Answer17B), context.getString(R.string.Answer17C), context.getString(R.string.Answer17A));
        this.addQuestion(q17);
        Question q18=new Question(context.getString(R.string.Quest18), context.getString(R.string.Answer18A), context.getString(R.string.Answer18B), context.getString(R.string.Answer18C), context.getString(R.string.Answer18C));
        this.addQuestion(q18);
        Question q19=new Question(context.getString(R.string.Quest19), context.getString(R.string.Answer19A), context.getString(R.string.Answer19B), context.getString(R.string.Answer19C), context.getString(R.string.Answer19A));
        this.addQuestion(q19);
        Question q20=new Question(context.getString(R.string.Quest20), context.getString(R.string.Answer20A), context.getString(R.string.Answer20B), context.getString(R.string.Answer20C), context.getString(R.string.Answer20A));
        this.addQuestion(q20);
        Question q21=new Question(context.getString(R.string.Quest21), context.getString(R.string.Answer21A), context.getString(R.string.Answer21B), context.getString(R.string.Answer21C), context.getString(R.string.Answer21A));
        this.addQuestion(q21);
        Question q22=new Question(context.getString(R.string.Quest22), context.getString(R.string.Answer22A), context.getString(R.string.Answer22B), context.getString(R.string.Answer22C), context.getString(R.string.Answer22C));
        this.addQuestion(q22);
        Question q23=new Question(context.getString(R.string.Quest23), context.getString(R.string.Answer23A), context.getString(R.string.Answer23B), context.getString(R.string.Answer23C), context.getString(R.string.Answer23C));
        this.addQuestion(q23);
        Question q24=new Question(context.getString(R.string.Quest24), context.getString(R.string.Answer24A), context.getString(R.string.Answer24B), context.getString(R.string.Answer24C), context.getString(R.string.Answer24A));
        this.addQuestion(q24);
        Question q25=new Question(context.getString(R.string.Quest25), context.getString(R.string.Answer25A), context.getString(R.string.Answer25B), context.getString(R.string.Answer25C), context.getString(R.string.Answer25C));
        this.addQuestion(q25);
        Question q26=new Question(context.getString(R.string.Quest26), context.getString(R.string.Answer26A), context.getString(R.string.Answer26B), context.getString(R.string.Answer26C), context.getString(R.string.Answer26C));
        this.addQuestion(q26);
        Question q27=new Question(context.getString(R.string.Quest27), context.getString(R.string.Answer27A), context.getString(R.string.Answer27B), context.getString(R.string.Answer27C), context.getString(R.string.Answer27A));
        this.addQuestion(q27);
        Question q28=new Question(context.getString(R.string.Quest28), context.getString(R.string.Answer28A), context.getString(R.string.Answer28B), context.getString(R.string.Answer28C), context.getString(R.string.Answer28C));
        this.addQuestion(q28);
        Question q29=new Question(context.getString(R.string.Quest29), context.getString(R.string.Answer29A), context.getString(R.string.Answer29B), context.getString(R.string.Answer29C), context.getString(R.string.Answer29B));
        this.addQuestion(q29);
        Question q30=new Question(context.getString(R.string.Quest30), context.getString(R.string.Answer30A), context.getString(R.string.Answer30B), context.getString(R.string.Answer30C), context.getString(R.string.Answer30B));
        this.addQuestion(q30);
        Question q31=new Question(context.getString(R.string.Quest31), context.getString(R.string.Answer31A), context.getString(R.string.Answer31B), context.getString(R.string.Answer31C), context.getString(R.string.Answer31A));
        this.addQuestion(q31);
        Question q32=new Question(context.getString(R.string.Quest32), context.getString(R.string.Answer32A), context.getString(R.string.Answer32B), context.getString(R.string.Answer32C), context.getString(R.string.Answer32B));
        this.addQuestion(q32);
        Question q33=new Question(context.getString(R.string.Quest33), context.getString(R.string.Answer33A), context.getString(R.string.Answer33B), context.getString(R.string.Answer33C), context.getString(R.string.Answer33C));
        this.addQuestion(q33);
        Question q34=new Question(context.getString(R.string.Quest34), context.getString(R.string.Answer34A), context.getString(R.string.Answer34B), context.getString(R.string.Answer34C), context.getString(R.string.Answer34A));
        this.addQuestion(q34);
        Question q35=new Question(context.getString(R.string.Quest35), context.getString(R.string.Answer35A), context.getString(R.string.Answer35B), context.getString(R.string.Answer35C), context.getString(R.string.Answer35A));
        this.addQuestion(q35);
    }
}

