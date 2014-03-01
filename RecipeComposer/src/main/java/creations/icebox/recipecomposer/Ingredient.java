package creations.icebox.recipecomposer;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created on 2/28/14.
 */
public class Ingredient {
    private String ingredientTitle = "";
    private ArrayList<String> tags;         // tags that associates this ingredients with ideas.
    private boolean is_selected;

    private static final String DEBUG_TAG = "***NEW RECIPE: ";

    public Ingredient(String ingredientTitle) {
        Log.d(DEBUG_TAG, "constructor");

        this.ingredientTitle = ingredientTitle;
        this.tags = new ArrayList<String>();
        this.is_selected = false;
    }


    public String getName() { return ingredientTitle; }

    public String getIngredientTitle() {
        return ingredientTitle;
    }

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
        return is_selected;
    }

    public void setSelected(boolean selected) {
        this.is_selected = selected;
    }

}
