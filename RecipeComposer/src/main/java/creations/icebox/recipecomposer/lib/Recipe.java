package creations.icebox.recipecomposer.lib;

/** Plain Old Java Object (POJO) */
public class Recipe {
    private static final String TAG = "***NEW RECIPE***: ";

    private long    id = 0;
    private String  recipeTitle = "";
    private String  recipeURL = "";
    private String  recipeIngredients = "";
    private String  recipePicUrl = "";
    private boolean isFavorited = false;


    public long getRecipeId() {
        return id;
    }

    public void setRecipeId(long id) {
        this.id = id;
    }


    public String getRecipeTitle() {
        return recipeTitle;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }


    public String getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(String recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }


    public String getRecipeURL() {
        return recipeURL;
    }

    public void setRecipeURL(String recipeURL) {
        this.recipeURL = recipeURL;
    }


    public String getRecipePicUrl() {
        return recipePicUrl;
    }

    public void setRecipePicUrl(String recipePicUrl) {
        this.recipePicUrl = recipePicUrl;
    }


    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean isFavorited) {
        this.isFavorited = isFavorited;
    }

}
