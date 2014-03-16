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

//package creations.icebox.recipecomposer.helper;
//
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteException;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
//import java.util.HashMap;
//
//import creations.icebox.recipecomposer.Ingredient;
//import creations.icebox.recipecomposer.Recipe;
//
//public class DBHelper extends SQLiteOpenHelper{
//    final static String DB_NAME = "recipe_composer.db";
//    final static int DB_VERSION = 1;
//    private final String INGREDIENTS_TABLE = "ingredients_table";
//    private final String RECIPES_TABLE = "recipes_table";
//    private final String INGREDIENTS_RECIPES_TABLE = "ingredients_recipes_table";
//    Context context;
//
//    public DBHelper(Context context){
//        super(context, DB_NAME, null, DB_VERSION);
//        Log.d("Creating DB: ", "...");
//        this.context=context;
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + INGREDIENTS_TABLE
//                + " (_id INTEGER primary key autoincrement, title VARCHAR, description VARCHAR"
//                + ");");
//
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + INGREDIENTS_RECIPES_TABLE
//                + " (_id INTEGER primary key autoincrement, ingredient_id INTEGER, recipe_id INTEGER,"
//                + " FOREIGN KEY (ingredient_id) REFERENCES " + INGREDIENTS_TABLE + " (ingredient_id),"
//                + " FOREIGN KEY (recipe_id) REFERENCES " + RECIPES_TABLE + " (recipe_id)"
//                + ");");
//
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + RECIPES_TABLE
//                + " (_id INTEGER primary key autoincrement, title VARCHAR NOT NULL, description VARCHAR"
//                + ");");
//
//        Log.d("Created DB: ", "...");
//        // db.execSQL("CREATE TABLE IF NOT EXISTS " + RECIPES_TABLE + " (title VARCHAR, );");
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        // TODO Auto-generated method stub
//
//    }
//
//
//    public boolean insertIngredient(Ingredient ingredient){
//        // ingredient
//        String title        = ingredient.getName();
//        String description  = ingredient.getDescription();
//
//        try{
//            SQLiteDatabase qdb = this.getWritableDatabase();
//
//            qdb.execSQL("CREATE TABLE IF NOT EXISTS " + INGREDIENTS_TABLE
//                    + " (_id INTEGER primary key autoincrement, title VARCHAR, description VARCHAR"
//                    + ");");
//
//            qdb.execSQL("CREATE TABLE IF NOT EXISTS " + INGREDIENTS_RECIPES_TABLE
//                    + " (_id INTEGER primary key autoincrement, ingredient_id INTEGER, recipe_id INTEGER,"
//                    + " FOREIGN KEY (ingredient_id) REFERENCES " + INGREDIENTS_TABLE + " (ingredient_id),"
//                    + " FOREIGN KEY (recipe_id) REFERENCES " + RECIPES_TABLE + " (recipe_id)"
//                    + ");");
//
//            qdb.execSQL("CREATE TABLE IF NOT EXISTS " + RECIPES_TABLE
//                    + " (_id INTEGER primary key autoincrement, title VARCHAR NOT NULL, description VARCHAR"
//                    + ");");
//
//            Log.d("DB Insert Singleton: ", "INSERT OR REPLACE INTO " +
//                    INGREDIENTS_TABLE + " (title, description) Values (" + title + ", " + description + ");");
//
//            String query = "INSERT OR REPLACE INTO " + INGREDIENTS_TABLE + " (title) Values (\"" + title + "\");";
//            qdb.execSQL(query);
//
//            qdb.close();
//        }
//        catch(SQLiteException se){
//            Log.d("DB Insert Error: ",se.toString());
//            return false;
//        }
//
//        return true;
//    }
//
//    public Ingredient getIngredient(String ingredientTitle){
//
//        Ingredient ingredient = null;          // temporary ingredient
//        String title        = "";
//        String description  = "";
//
//        try{
//            SQLiteDatabase qdb = this.getReadableDatabase();
//
//            qdb.execSQL("CREATE TABLE IF NOT EXISTS " + INGREDIENTS_TABLE
//                    + " (_id INTEGER primary key autoincrement, title VARCHAR , description VARCHAR"
//                    + ");");
//
//            qdb.execSQL("CREATE TABLE IF NOT EXISTS " + INGREDIENTS_RECIPES_TABLE
//                    + " (_id INTEGER primary key autoincrement, ingredient_id INTEGER, recipe_id INTEGER,"
//                    + " FOREIGN KEY (ingredient_id) REFERENCES " + INGREDIENTS_TABLE + " (ingredient_id),"
//                    + " FOREIGN KEY (recipe_id) REFERENCES " + RECIPES_TABLE + " (recipe_id)"
//                    + ");");
//
//            qdb.execSQL("CREATE TABLE IF NOT EXISTS " + RECIPES_TABLE
//                    + " (_id INTEGER primary key autoincrement, title VARCHAR NOT NULL, description VARCHAR"
//                    + ");");
//
//            String query = "SELECT title, description FROM " + INGREDIENTS_TABLE + " WHERE title=\"" + ingredientTitle + "\"";
//            Log.d("Retrieve Ingredients: ", query);
//            Cursor cursor = qdb.rawQuery(query, null);
//
//            if (cursor != null){
//                Log.d("Get Ingredient 'title' index == ", Integer.toString(cursor.getColumnIndex("title")));
//                title        = cursor.getString(cursor.getColumnIndex("title"));
//                Log.d("Get Ingredient title: ", title);
//
//                Log.d("Get Ingredient 'description' index == ", Integer.toString(cursor.getColumnIndex("description")));
//                description  = cursor.getString(cursor.getColumnIndex("description"));
//                Log.d("Get Ingredient description: ", description);
//
//                ingredient = new Ingredient(title);
//                ingredient.setDescription(description);
//            } else {
//                Log.d("Retrieve Ingredients: ", "Cursor is NULL");
//            }
//
//            cursor.close();
//            qdb.close();
//        }
//        catch(SQLiteException se){
//            Log.d("DB Insert Error: ",se.toString());
//            return null;
//        }
//
//        return ingredient;
//    }
//
//    public HashMap<String, Ingredient> getAllIngredients(){
//
//        HashMap<String, Ingredient> storedIngredients = null;
//        // ingredient
//        Ingredient ingredient;
//        String title        = "";
//        String description  = "";
//
//        try{
//            SQLiteDatabase qdb      = this.getReadableDatabase();
//
//            String query = "SELECT * FROM " + INGREDIENTS_TABLE;
//            Log.d("Retrieve Ingredients: ", query);
//            Cursor cursor           = qdb.rawQuery(query, null);
//
//            if (cursor != null){
//                if (cursor.moveToFirst()){
//                    storedIngredients = new HashMap<String, Ingredient>();
//                    do {
//                        title        = cursor.getString(cursor.getColumnIndex("title"));
//                        description  = cursor.getString(cursor.getColumnIndex("description"));
//
//                        Log.d("Get All ingredients: ", "title: " + title + " descr: " + description);
//
//                        if (storedIngredients.containsKey(title) == false){
//                            storedIngredients.put(title, new Ingredient(title));
//                            storedIngredients.get(title).setDescription(description);
//                        }
//
//                    } while (cursor.moveToNext());
//
//                } else {
//                    Log.d("Retrieve Ingredients: ", "Failed to move the cursor to first element");
//                }
//            } else {
//                Log.d("Retrieve Ingredients: ", "Cursor is NULL");
//            }
//
//            cursor.close();
//            qdb.close();
//        }
//        catch(SQLiteException se){
//            Log.d("DB Insert Error: ",se.toString());
//            return null;
//        }
//
//        return storedIngredients;
//    }
//
//    public boolean dropTables(){
//        try{
//            //DBHelper appDB = new DBHelper(context);
//            SQLiteDatabase qdb = this.getWritableDatabase();
//            Log.d("DB DROP INGREDIENTS TABLE: ", "DROP TABLE IF EXISTS " + INGREDIENTS_TABLE + ";");
//
//            qdb.execSQL("DROP TABLE IF EXISTS " + INGREDIENTS_TABLE + ";");
//
//            qdb.close();
//        }
//        catch(SQLiteException se){
//            Log.d("DB Insert Error: ",se.toString());
//            return false;
//        }
//        return true;
//    }
//
//}