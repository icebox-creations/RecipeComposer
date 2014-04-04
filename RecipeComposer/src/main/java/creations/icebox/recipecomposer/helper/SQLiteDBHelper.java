package creations.icebox.recipecomposer.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Responsible for creating the database, tables
 */
public class SQLiteDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "***SQLITEDBHELPER***: ";

    final static String DB_NAME = "recipe_composer.db";
    final static int DB_VERSION = 1;

    public static final String TABLE_INGREDIENTS = "ingredients";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";

    /** Database sql statements */
    private static final String CREATE_TABLE_INGREDIENTS = "create table "
            + TABLE_INGREDIENTS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TITLE
            + " text not null);";
    private static final String DB_DROP = "drop table if exists "
            + TABLE_INGREDIENTS;

    public SQLiteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(TAG, "in constructor");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        db.execSQL(CREATE_TABLE_INGREDIENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DB_DROP);
        onCreate(db);
    }
}