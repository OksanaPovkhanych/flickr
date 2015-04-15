package image.collection.test.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final String PHOTO_TABLE = "photo_table";
    public static final String ID = "_id";
    public static final String URL = "url";
    public static final String FARM = "farm";
    public static final String TITLE = "title";
    public static final String OWNER = "owner";
    public static final String OWNER_NAME = "owner_name";
    public static final String SERVER = "server";
    public static final String SECRET = "secret";
    public static final String TIMESTAMP = "timestamp";
    private static final String CREATE_PHOTO_TABLE =
            " CREATE TABLE " + PHOTO_TABLE +
                    " (" + ID + " TEXT PRIMARY KEY, " +
                    " " + URL + " TEXT NOT NULL, " +
                    " " + FARM + " TEXT NOT NULL, " +
                    " " + TITLE + " TEXT NOT NULL, " +
                    " " + OWNER + " TEXT NOT NULL, " +
                    " " + OWNER_NAME + " TEXT NOT NULL, " +
                    " " + SERVER + " TEXT NOT NULL, " +
                    " " + SECRET + " TEXT NOT NULL, " +
                    " " + TIMESTAMP + " TEXT NOT NULL);";

    public static final String COMMENT_TABLE = "comment";

    public static final String PHOTO_ID = "photo_id";
    public static final String AUTHOR = "author";
    public static final String AUTHOR_NAME = "authorname";
    public static final String CONTENT = "content";
    private static final String CREATE_COMMENT_TABLE =
            " CREATE TABLE " + COMMENT_TABLE +
                    " (" + ID + " TEXT PRIMARY KEY, " +
                    " " + PHOTO_ID + " TEXT NOT NULL, " +
                    " " + AUTHOR + " TEXT NOT NULL, " +
                    " " + AUTHOR_NAME + " TEXT NOT NULL, " +
                    " " + CONTENT + " TEXT NOT NULL);";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FlickrDatabase";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PHOTO_TABLE);
        db.execSQL(CREATE_COMMENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + CREATE_PHOTO_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_COMMENT_TABLE);
        onCreate(db);
    }

}