package creations.icebox.recipecomposer.helper;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.appcompat.R;
import android.util.Log;

import creations.icebox.recipecomposer.Ingredient;
import creations.icebox.recipecomposer.Recipe;
//import creations.icebox.recipecomposer.;

public class DBHelper extends SQLiteOpenHelper{
    final static String DB_NAME = "recipe_composer.db";
    final static int DB_VERSION = 1;
    private final String INGREDIENTS_TABLE = "ingredients_table";
    private final String RECIPES_TABLE = "recipes_table";
    private final String INGREDIENTS_RECIPES_TABLE = "ingredients_recipes_table";
    Context context;

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        Log.d("Creating DB: ", "...");
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS " + INGREDIENTS_TABLE
                + " (id primary key autoincrement, title VARCHAR NOT NULL, description VARCHAR,"
                + ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + INGREDIENTS_RECIPES_TABLE
                + " (id primary key autoincrement, ingredient_id INTEGER, recipe_id INTEGER"
                + " FOREIGN KEY (ingredient_id) REFERENCES " + INGREDIENTS_TABLE + " (ingredient_id)"
                + " FOREIGN KEY (recipe_id) REFERENCES " + RECIPES_TABLE + " (recipe_id)"
                + ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + RECIPES_TABLE
                + " (id primary key autoincrement, title VARCHAR NOT NULL, description VARCHAR,"
                + ");");

        Log.d("Created DB: ", "...");
        // db.execSQL("CREATE TABLE IF NOT EXISTS " + RECIPES_TABLE + " (title VARCHAR, );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }


    public boolean insertIngredient(Ingredient ingredient){
        // ingredient
        String title        = ingredient.getName();
        String description  = ingredient.getDescription();

        try{
            //DBHelper appDB = new DBHelper(context);
            SQLiteDatabase qdb = this.getWritableDatabase();

            Log.d("DB Insert: ", "INSERT OR REPLACE INTO " +
                    INGREDIENTS_TABLE + " (title, description) Values (" + title + "," + description + ");");
            qdb.execSQL("INSERT OR REPLACE INTO " +
                    INGREDIENTS_TABLE + " (title, description) Values (\""+ title + "," + description + "\");");

            qdb.close();
        }
        catch(SQLiteException se){
            Log.d("DB Insert Error: ",se.toString());
            return false;
        }

        return true;
    }

    public boolean insertRecipe(Recipe recipe){
        // ingredient
        String title        = recipe.getRecipeTitle();
        String description  = recipe.getRecipeIngredients();

        try{
            //DBHelper appDB = new DBHelper(context);
            SQLiteDatabase qdb = this.getWritableDatabase();

            Log.d("DB Insert: ", "INSERT OR REPLACE INTO " +
                    RECIPES_TABLE + " (title, description) Values (" + title + "," + description + ");");
            qdb.execSQL("INSERT OR REPLACE INTO " +
                    RECIPES_TABLE + " (title, description) Values (\""+ title + "," + description + "\");");

            qdb.close();
        }
        catch(SQLiteException se){
            Log.d("DB Insert Error: ",se.toString());
            return false;
        }

        return true;
    }


    public boolean insertText(String text){
        try{
            //DBHelper appDB = new DBHelper(context);
            SQLiteDatabase qdb = this.getWritableDatabase();

            Log.d("DB Insert: ", "INSERT OR REPLACE INTO " +
                    INGREDIENTS_TABLE + " (text) Values ("+ text + ");");
            qdb.execSQL("INSERT OR REPLACE INTO " +
                    INGREDIENTS_TABLE + " (text) Values (\""+ text + "\");");

            qdb.close();
        }
        catch(SQLiteException se){
            Log.d("DB Insert Error: ",se.toString());
            return false;
        }
        return true;
    }

    public String getText(){
        String toReturn = "";
        try{
            //DBHelper appDB = new DBHelper(context);
            SQLiteDatabase qdb = this.getReadableDatabase();
            qdb.execSQL("CREATE TABLE IF NOT EXISTS " + INGREDIENTS_TABLE + " (text VARCHAR);");
            Cursor c = qdb.rawQuery("SELECT * FROM " +
                    INGREDIENTS_TABLE, null);
            if (c != null ) {
                if  (c.moveToFirst()) {
                    do {
                        String text = c.getString(c.getColumnIndex("text"));
                        toReturn += text + "\n";
                    }
                    while (c.moveToNext());
                }
            }
            qdb.close();
        }
        catch(SQLiteException se){
            Log.d("DB Select Error: ",se.toString());
            return "";
        }
        return toReturn;
    }



}