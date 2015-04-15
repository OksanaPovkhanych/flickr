package image.collection.test.db;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class FlickrContentProvider extends ContentProvider {
    private static final String PROVIDER_NAME = "image.collection.test.provider";

    private static final String PHOTO_URL = "content://" + PROVIDER_NAME + "/photo";
    public static final Uri PHOTO_URI = Uri.parse(PHOTO_URL);
    private static final String COMMENT_URL = "content://" + PROVIDER_NAME + "/comment";
    public static final Uri COMMENT_URI = Uri.parse(COMMENT_URL);

    private static final int PHOTO = 1;
    private static final int PHOTO_ID = 2;
    private static final int COMMENT = 3;
    private static final int COMMENT_ID = 4;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "photo", PHOTO);
        uriMatcher.addURI(PROVIDER_NAME, "photo/#", PHOTO_ID);
        uriMatcher.addURI(PROVIDER_NAME, "comment", COMMENT);
        uriMatcher.addURI(PROVIDER_NAME, "comment/#", COMMENT_ID);
    }

    private DBHelper dbHelper;

    public FlickrContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case PHOTO:
                count = dbHelper.getWritableDatabase().delete(DBHelper.PHOTO_TABLE, selection, selectionArgs);
                break;
            case PHOTO_ID:
                String id = uri.getLastPathSegment();
                count = dbHelper.getWritableDatabase().delete(DBHelper.PHOTO_TABLE, DBHelper.ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" +
                        selection + ')' : ""), selectionArgs);
                break;
            case COMMENT:
                count = dbHelper.getWritableDatabase().delete(DBHelper.COMMENT_TABLE, selection, selectionArgs);
                break;
            case COMMENT_ID:
                id = uri.getLastPathSegment();
                count = dbHelper.getWritableDatabase().delete(DBHelper.COMMENT_TABLE, DBHelper.ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" +
                        selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported Delete URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case PHOTO:
                return "vnd.android.cursor.dir/vnd.example.photo";
            case PHOTO_ID:
                return "vnd.android.cursor.item/vnd.example.photo";
            case COMMENT:
                return "vnd.android.cursor.dir/vnd.example.comment";
            case COMMENT_ID:
                return "vnd.android.cursor.item/vnd.example.comment";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table;
        switch (uriMatcher.match(uri)) {
            case PHOTO:
            case PHOTO_ID:
                table = DBHelper.PHOTO_TABLE;

                break;
            case COMMENT:
            case COMMENT_ID:
                table = DBHelper.COMMENT_TABLE;
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        long row = dbHelper.getWritableDatabase().insertWithOnConflict(table, "", values, SQLiteDatabase.CONFLICT_REPLACE);
        if (row > 0) {
            Uri newUri = ContentUris.withAppendedId(uri, row);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
        throw new SQLException("Insert failed: " + uri);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DBHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case PHOTO:
                queryBuilder.setTables(DBHelper.PHOTO_TABLE);
                break;
            case PHOTO_ID:
                queryBuilder.setTables(DBHelper.PHOTO_TABLE);
                queryBuilder.appendWhere(DBHelper.ID + " =" + uri.getLastPathSegment());
                break;
            case COMMENT:
                queryBuilder.setTables(DBHelper.COMMENT_TABLE);
                break;
            case COMMENT_ID:
                queryBuilder.setTables(DBHelper.COMMENT_TABLE);
                queryBuilder.appendWhere(DBHelper.ID + " =" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown Query URI " + uri);
        }

        if (sortOrder == null || sortOrder.equals("")) {
            sortOrder = DBHelper.ID;
        }

        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(), projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case PHOTO:
                count = dbHelper.getWritableDatabase().update(DBHelper.PHOTO_TABLE, values, selection, selectionArgs);
                break;
            case PHOTO_ID:
                String id = uri.getLastPathSegment();
                count = dbHelper.getWritableDatabase().update(DBHelper.PHOTO_TABLE, values, DBHelper.ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" +
                        selection + ')' : ""), selectionArgs);
                break;
            case COMMENT:
                count = dbHelper.getWritableDatabase().update(DBHelper.COMMENT_TABLE, values, selection, selectionArgs);
                break;
            case COMMENT_ID:
                id = uri.getLastPathSegment();
                count = dbHelper.getWritableDatabase().update(DBHelper.COMMENT_TABLE, values, DBHelper.ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" +
                        selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported Update URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
