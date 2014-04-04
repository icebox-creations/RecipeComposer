package creations.icebox.recipecomposer;

import android.util.Log;

public class Recipe {
    private String recipeTitle = "";
    private String recipeURL = "";
    private String recipeIngredients = "";

    public String getRecipePicUrl() {
        return recipePicUrl;
    }

    public void setRecipePicUrl(String recipePicUrl) {
        this.recipePicUrl = recipePicUrl;
    }

    private String recipePicUrl = "";

    private static final String TAG = "***NEW RECIPE***: ";

    public Recipe(String recipeTitle, String recipeURL, String recipeIngredients, String recipePicUrl) {
//        Log.d(TAG, "constructor");

        this.recipeTitle = recipeTitle;
        this.recipeURL = recipeURL;
        this.recipeIngredients = recipeIngredients;
        setRecipePicUrl(recipePicUrl);
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }

    public String getRecipeURL() {
        return recipeURL;
    }

    public void setRecipeURL(String recipeURL) {
        this.recipeURL = recipeURL;
    }

    public String getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(String recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }
}
