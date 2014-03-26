package creations.icebox.recipecomposer;

import android.util.Log;

public class Ingredient {
    private static final String TAG = "***NEW INGREDIENT***: ";
    private long id = 0;
    private String ingredientTitle = "";
    private boolean selected = false;

    public long getIngredientId() {
        return id;
    }

    public void setIngredientId(long id) {
        this.id = id;
    }

    public String getIngredientTitle() {
        return ingredientTitle;
    }

    public void setIngredientTitle(String recipeTitle) {
        this.ingredientTitle = recipeTitle;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /** Will be used by the ArrayAdapter in the Listview */
    @Override
    public String toString() {
        return ingredientTitle;
    }
}

// public class Ingredient {
//    private String ingredientTitle = "";
//    private String ingredientDescription = "";
//    private ArrayList<String> tags; // tags that associates this ingredients with ideas.
//    private boolean isSelected;
//
//    private static final String TAG = "***NEW INGREDIENT***: ";
//
//    public Ingredient(String ingredientTitle) {
//        Log.d(TAG, "constructor");
//        if (ingredientTitle == null){
//            ingredientTitle = "";
//        }
//        this.ingredientTitle = ingredientTitle;
//        this.tags = new ArrayList<String>();
//        this.isSelected = false;
//    }
//
//    public String getTitle() { return ingredientTitle; }
//
//    public String getDescription() { return ingredientDescription; }
//
//
//    public String getName() { return ingredientTitle; }
//    public String getIngredientTitle() {
//        return ingredientTitle;
//    }
//
//
//    public void setIngredientTitle(String recipeTitle) {
//        this.ingredientTitle = recipeTitle;
//    }
//    public void setDescription(String ingredientDescription) {
//        if (ingredientDescription == null){
//            ingredientDescription = "";
//        }
//        this.ingredientTitle = ingredientDescription;
//    }
//
//    public ArrayList<String> getIngredientTags(){
//        return tags;
//    }
//
//    public String topIngredientTag() {
//        if (tags.size() <= 0){
//            return "";
//        }
//        return this.tags.get(0);
//    }
//
//    public void pushIngredientTag(String ingredientTag) {
//        this.tags.add(ingredientTag);
//    }
//
//    public boolean isSelected(){
//        return isSelected;
//    }
//
//    public void setSelected(boolean isSelected) {
//        this.isSelected = isSelected;
//    }
//
//}