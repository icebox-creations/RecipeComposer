package creations.icebox.recipecomposer.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import creations.icebox.recipecomposer.lib.Ingredient;
import creations.icebox.recipecomposer.lib.Recipe;

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

    private String[] recipeFavoritesColumns = {
            SQLiteDBHelper.RECIPE_FAV_COLUMN_ID,
            SQLiteDBHelper.RECIPE_FAV_COLUMN_TITLE,
            SQLiteDBHelper.RECIPE_FAV_COLUMN_INGREDIENT_LIST,
            SQLiteDBHelper.RECIPE_FAV_COLUMN_URL
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


    /** DAO methods to access ingredient objects from the Database:
     *
     *  We need to be able to create new ingredeints and update them..
     *  We also need to be able to get the list of all ingredients*/

    public Ingredient createIngredient(String ingredientTitle) {

        // Check for duplications
        String duplicateCheckQuery =
                "select * from "
                + SQLiteDBHelper.TABLE_INGREDIENTS
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
        contentValues.put(SQLiteDBHelper.COLUMN_TITLE, ingredientTitle.toLowerCase().trim());

        long insertId = sqLiteDatabase.insert(
                SQLiteDBHelper.TABLE_INGREDIENTS,
                null,
                contentValues
        );

        cursor = sqLiteDatabase.query(
                SQLiteDBHelper.TABLE_INGREDIENTS,
                ingredientsColumns,
                SQLiteDBHelper.COLUMN_ID + " = " + insertId,
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
                SQLiteDBHelper.TABLE_INGREDIENTS,
                SQLiteDBHelper.COLUMN_ID + " = " + id,
                null
        );
    }

    public int updateIngredientTitle(String oldIngredientTitle, String newIngredientTitle) {

        if (newIngredientTitle.isEmpty()) {
            Toast.makeText(context, "You must specify a new ingredient title", Toast.LENGTH_LONG).show();
            return 0;
        }

        ContentValues newContentValues = new ContentValues();
        newContentValues.put("title", newIngredientTitle.toLowerCase().trim());

        return sqLiteDatabase.update(
                SQLiteDBHelper.TABLE_INGREDIENTS,
                newContentValues,
                "title = \"" + oldIngredientTitle + "\"",
                null
        );
    }

    public ArrayList<Ingredient> getAllIngredients() {
        ArrayList<Ingredient> ingredientList = new ArrayList<Ingredient>();

        Cursor cursor;
        cursor = sqLiteDatabase.query(
                SQLiteDBHelper.TABLE_INGREDIENTS,
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


    /** DAO methods to access database for recipe favorite objects:
     *
     *  We need to be able to create new recipe favorites, store them
     *      in a database and later retrieve them
     *
     *  We also need the ability to retrieve the list of all recipe favorites
     *      so we can update the recipe favorite list view in the recipe fragment
     *      using the recipe favorite adapter... */

    public Recipe createRecipeFavorite(String recipeTitle) {

        // Check for duplications
        String duplicateCheckQuery =
                "select * from "
                        + SQLiteDBHelper.TABLE_RECIPE_FAVS
                        + " where title like '" + recipeTitle + "';";

        Cursor cursor = sqLiteDatabase.rawQuery(duplicateCheckQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            Toast.makeText(context, "Ingredient is already in the list", Toast.LENGTH_LONG).show();
            return null;
        } else if (recipeTitle.isEmpty()) {
            Toast.makeText(context, "You must specify an ingredient", Toast.LENGTH_LONG).show();
            return null;
        }

        // if the title isn't blank or a duplicate, insert it
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLiteDBHelper.RECIPE_FAV_COLUMN_TITLE, recipeTitle.toLowerCase().trim());

        long insertId = sqLiteDatabase.insert(
                SQLiteDBHelper.TABLE_INGREDIENTS,
                null,
                contentValues
        );

        cursor = sqLiteDatabase.query(
                SQLiteDBHelper.TABLE_RECIPE_FAVS,
                recipeFavoritesColumns,
                SQLiteDBHelper.RECIPE_FAV_COLUMN_ID + " = " + insertId,
                null, null, null, null
        );

        cursor.moveToFirst();
        Recipe newRecipe = cursorToRecipeFavorite(cursor);
        cursor.close();
        return newRecipe;
    }

    public void deleteRecipeFavorite(Recipe recipeFavorite) {
        long id = recipeFavorite.getRecipeId();
        Log.d(TAG, "recipe favorite deleted with id: " + id);

        sqLiteDatabase.delete(
                SQLiteDBHelper.TABLE_RECIPE_FAVS,
                SQLiteDBHelper.COLUMN_ID + " = " + id,
                null
        );
    }

    /** Takes a new recipe and updates the recipe with the same ID, which exists in the database
     *  TODO: check to see if the id of the recipe we wish to update already exists in the database
     *        before updating */
    public int updateRecipeFavorite(Recipe newRecipe) {
        if (newRecipe == null) {
            Toast.makeText(context, "Update Recipe Favorite newRecipe is null..", Toast.LENGTH_LONG).show();
            return 0;
        }

        long id                         = newRecipe.getRecipeId();
        String newRecipeTitle           = newRecipe.getRecipeTitle();
        String newRecipeIngredientList  = newRecipe.getRecipeTitle();
        String newRecipeUrl             = newRecipe.getRecipeTitle();

        ContentValues newContentValues = new ContentValues();
        newContentValues.put(SQLiteDBHelper.RECIPE_FAV_COLUMN_TITLE, newRecipeTitle.toLowerCase().trim());
        newContentValues.put(SQLiteDBHelper.RECIPE_FAV_COLUMN_INGREDIENT_LIST, newRecipeIngredientList.toLowerCase().trim());
        newContentValues.put(SQLiteDBHelper.RECIPE_FAV_COLUMN_URL, newRecipeUrl.toLowerCase().trim());

        return sqLiteDatabase.update(
                SQLiteDBHelper.TABLE_RECIPE_FAVS,
                newContentValues,
                SQLiteDBHelper.RECIPE_FAV_COLUMN_ID + " = \"" + id + "\"",
                null
        );
    }

    public ArrayList<Recipe> getAllRecipeFavorites() {
        ArrayList<Recipe> recipeList = new ArrayList<Recipe>();

        Cursor cursor;
        cursor = sqLiteDatabase.query(
                SQLiteDBHelper.TABLE_RECIPE_FAVS,
                recipeFavoritesColumns,
                null, null, null, null, null
        );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Recipe recipe = cursorToRecipeFavorite(cursor);
            recipeList.add(recipe);
            cursor.moveToNext();
        }

        cursor.close();
        return recipeList;
    }

    private Recipe cursorToRecipeFavorite(Cursor cursor) {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(cursor.getLong(0));
        recipe.setRecipeTitle(cursor.getString(1));
        recipe.setRecipeIngredients(cursor.getString(2));
        recipe.setRecipeURL(cursor.getString(3));
        return recipe;
    }
}