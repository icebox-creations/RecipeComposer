package creations.icebox.recipecomposer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import creations.icebox.recipecomposer.Ingredient;
import creations.icebox.recipecomposer.R;


/**
 * Created on 2/28/14.
 */
public class IngredientAdapter extends ArrayAdapter<Ingredient> {
    private static final String DEBUG_TAG = "***RECIPE ADAPTER: ";

    private ArrayList<Ingredient> ingredientList;

    /*
    * here we must override the constructor for ArrayAdapter.
    * the only variable we care about now is ArrayList<Item> objects
    * because it is the list of objects we want to display.
    */
    public IngredientAdapter(Context context, int resource, ArrayList<Ingredient> ingredientList) {
        super(context, resource, ingredientList);
        this.ingredientList = ingredientList;
    }

    /*
   * we are overriding the getView method here - this is what defines how each
   * list item will look.
   */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // assign the view we are converting to a local variable
        View view = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_ingredient, null);
        }

        /*
         * Recall that the variable position is sent in as an argument to this method.
         * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
         * iterates through the list we sent it)
         *
         * Therefore, i refers to the current Item object.
         */
        Ingredient i = ingredientList.get(position);

        if (i != null) {

            try {
                TextView title = (TextView) view.findViewById(R.id.title);

                if (title != null) {
                    title.setText(i.getIngredientTitle());
                }
//                if (url != null) {
//                    url.setText(i.topIngredientTag());
//                }
//                if (ingredients != null) {
//                    ingredients.setText(i.getRecipeIngredients());
//                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        return view;
    }
}





