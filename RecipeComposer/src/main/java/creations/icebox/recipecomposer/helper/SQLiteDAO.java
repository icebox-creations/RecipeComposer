package creations.icebox.recipecomposer.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import creations.icebox.recipecomposer.Ingredient;

/**
 * Maintains the database connection and supports adding new ingredients and fetching all
 * ingredients.
 */
public class SQLiteDAO {

    private static final String TAG = "***SQLITEDAO***: ";
    private Context context;

    private SQLiteDatabase sqLiteDatabase;
    private SQLiteDBHelper sqLiteDBHelper;
    private String[] ingredientsColumns = {
            SQLiteDBHelper.COLUMN_ID,
            SQLiteDBHelper.COLUMN_TITLE
    };

    public SQLiteDAO(Context context) {
        Log.d(TAG, "in constructor");
        sqLiteDBHelper = new SQLiteDBHelper(context);
        this.context = context;
    }

    public void open() {
        Log.d(TAG, "open");
        sqLiteDatabase = sqLiteDBHelper.getWritableDatabase();
    }

    public void close() {
        Log.d(TAG, "close");
        sqLiteDBHelper.close();
    }

    public Ingredient createIngredient(String ingredientTitle) {

        // Check for duplications
        String duplicateCheckQuery =
                "select * from "
                + sqLiteDBHelper.TABLE_INGREDIENTS
                + " where title like '" + ingredientTitle + "';";

        Cursor cursor = sqLiteDatabase.rawQuery(duplicateCheckQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            Toast.makeText(context, "Ingredient is already in the list", Toast.LENGTH_LONG).show();
            return null;
        } else if (ingredientTitle.isEmpty()) {
            Toast.makeText(context, "You must specify an ingredient", Toast.LENGTH_LONG).show();
            return null;
        }

        // if the title isn't blank or a duplicate, insert it
        ContentValues contentValues = new ContentValues();
        contentValues.put(sqLiteDBHelper.COLUMN_TITLE, ingredientTitle.toLowerCase());

        long insertId = sqLiteDatabase.insert(
                SQLiteDBHelper.TABLE_INGREDIENTS,
                null,
                contentValues
        );

        cursor = sqLiteDatabase.query(
                sqLiteDBHelper.TABLE_INGREDIENTS,
                ingredientsColumns,
                sqLiteDBHelper.COLUMN_ID + " = " + insertId,
                null, null, null, null
        );

        cursor.moveToFirst();
        Ingredient newIngredient = cursorToIngredient(cursor);
        cursor.close();
        return newIngredient;
    }

    public void deleteIngredient(Ingredient ingredient) {
        long id = ingredient.getIngredientId();
        Log.d(TAG, "ingredient deleted with id: " + id);

        sqLiteDatabase.delete(
                sqLiteDBHelper.TABLE_INGREDIENTS,
                sqLiteDBHelper.COLUMN_ID + " = " + id,
                null
        );
    }

    public ArrayList<Ingredient> getAllIngredients() {
        ArrayList<Ingredient> ingredientList = new ArrayList<Ingredient>();

        Cursor cursor = sqLiteDatabase.query(
                sqLiteDBHelper.TABLE_INGREDIENTS,
                ingredientsColumns,
                null, null, null, null, null
        );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Ingredient ingredient = cursorToIngredient(cursor);
            ingredientList.add(ingredient);
            cursor.moveToNext();
        }

        cursor.close();
        return ingredientList;
    }

    private Ingredient cursorToIngredient(Cursor cursor) {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientId(cursor.getLong(0));
        ingredient.setIngredientTitle(cursor.getString(1));
        return ingredient;
    }
}