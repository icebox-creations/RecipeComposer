package creations.icebox.recipecomposer.pojo;

import android.util.Log;

/** Plain Old Java Object (POJO) */
public class Recipe {
    private static final String TAG = "***NEW RECIPE***: ";

    private String recipeTitle = "";
    private String recipeURL = "";
    private String recipeIngredients = "";
    private String recipePicUrl = "";

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

    public String getRecipePicUrl() {
        return recipePicUrl;
    }

    public void setRecipePicUrl(String recipePicUrl) {
        this.recipePicUrl = recipePicUrl;
    }
}
