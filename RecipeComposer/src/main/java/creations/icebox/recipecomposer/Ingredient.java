package creations.icebox.recipecomposer;

import android.util.Log;

import java.util.ArrayList;

public class Ingredient {
    private String ingredientTitle = "";
    private String ingredientDescription = "";
    private ArrayList<String> tags; // tags that associates this ingredients with ideas.
    private boolean isSelected;

    private static final String TAG = "***NEW INGREDIENT***: ";

    public Ingredient(String ingredientTitle) {
        Log.d(TAG, "constructor");

        this.ingredientTitle = ingredientTitle;
        this.tags = new ArrayList<String>();
        this.isSelected = false;
    }

    /* Getters */
    public String getTitle() { return ingredientTitle; }

    public String getDescription() { return ingredientDescription; }

    /* aliases for getTitle */
    public String getName() { return ingredientTitle; }

    public String getIngredientTitle() {
        return ingredientTitle;
    }

    public String getDescriptionTitle() {
        return ingredientDescription;
    }

    /* Setters */
    public void setIngredientTitle(String recipeTitle) {
        this.ingredientTitle = recipeTitle;
    }

    public ArrayList<String> getIngredientTags(){
        return tags;
    }

    public String topIngredientTag() {
        if (tags.size() <= 0){
            return "";
        }
        return this.tags.get(0);
    }

    public void pushIngredientTag(String ingredientTag) {
        this.tags.add(ingredientTag);
    }

    public boolean isSelected(){
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

}
