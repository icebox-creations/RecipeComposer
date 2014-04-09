package creations.icebox.recipecomposer.lib;

/** Plain Old Java Object (POJO) */
public class Ingredient {
    private static final String TAG = "***NEW INGREDIENT***: ";

    private long id = 0;
    private String ingredientTitle = "";
    private boolean selected = false;

    private SelectedStateType selectedState = SelectedStateType.NORMAL_STATE;

    public enum SelectedStateType {
        NULL_STATE,
        NORMAL_STATE,
        REQUIRED_STATE,
        EXCLUDE_STATE
    };

    public SelectedStateType getSelectedState() {
        return selectedState;
    }

    public void setSelectedState(SelectedStateType selectedState) {
        this.selectedState = selectedState;
    }


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

//    /** Will be used by the ArrayAdapter in the Listview */
//    @Override
//    public String toString() {
//        return ingredientTitle;
//    }
}
