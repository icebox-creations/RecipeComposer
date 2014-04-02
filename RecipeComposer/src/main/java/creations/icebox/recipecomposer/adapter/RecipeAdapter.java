package creations.icebox.recipecomposer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import creations.icebox.recipecomposer.R;
import creations.icebox.recipecomposer.Recipe;
import creations.icebox.recipecomposer.RecipesFragment;

/** Follows the ViewHolder Design Pattern */
public class RecipeAdapter extends ArrayAdapter<Recipe> {

    private static final String TAG = "***RECIPE ADAPTER***: ";

    private ArrayList<Recipe> recipeArrayList;

    /*
    * here we must override the constructor for ArrayAdapter.
	* the only variable we care about now is ArrayList<Item> objects
	* because it is the list of objects we want to display.
	*/
    public RecipeAdapter(Context context, int resource, ArrayList<Recipe> recipeArrayList) {
        super(context, resource, recipeArrayList);
        this.recipeArrayList = new ArrayList<Recipe>();
        Log.d(TAG, "constructor: recipeArrayList size = " + recipeArrayList.size());
        this.recipeArrayList = recipeArrayList;
    }

    /** ViewHolder: caches our TextViews */
    static class ViewHolderItem {
        TextView recipeTitle;
        TextView recipeIngredients;
        TextView recipeURL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyDataSetChanged() {
        Log.d(TAG, "notifyDataSetChanged: recipeArrayList size = " + recipeArrayList.size());
        super.notifyDataSetChanged();
    }

    /*
    * we are overriding the getView method here - this is what defines how each
    * list item will look.
    */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolderItem viewHolder;
        //Log.d(TAG, "ConvertView " + String.valueOf(position));

        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_recipe, null);

            // set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.recipeTitle = (TextView) convertView.findViewById(R.id.recipeTitleTextView);
            viewHolder.recipeIngredients = (TextView) convertView.findViewById(R.id.recipeIngredientsTextView);
            viewHolder.recipeURL = (TextView) convertView.findViewById(R.id.recipeURLTextView);

            convertView.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on the resource file every time
            // just use the viewHolder
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        // Recipe item based on the position
        Recipe recipe = recipeArrayList.get(position);

        // assign values if the recipe is not null
        if (recipe != null) {
            viewHolder.recipeTitle.setText(recipe.getRecipeTitle());
            viewHolder.recipeIngredients.setText(recipe.getRecipeIngredients());
            viewHolder.recipeURL.setText(recipe.getRecipeURL());
        }

        return convertView;
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        // assign the view we are converting to a local variable
//        View view = convertView;
//
//        // first check to see if the view is null. if so, we have to inflate it.
//        // to inflate it basically means to render, or show, the view.
//        if (view == null) {
//            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            view = inflater.inflate(R.layout.list_item_recipe, null);
//        }
//
//        /*
//         * Recall that the variable position is sent in as an argument to this method.
//         * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
//         * iterates through the list we sent it)
//         *
//         * Therefore, i refers to the current Item object.
//         */
//        Recipe i = recipeArrayList.get(position);
//
//        if (i != null) {
//
//            try {
//                TextView title = (TextView) view.findViewById(R.id.recipeTitleTextView);
//                TextView url = (TextView) view.findViewById(R.id.recipeURLTextView);
//                TextView ingredients = (TextView) view.findViewById(R.id.recipeIngredientsTextView);
//
//                if (title != null) {
//                    title.setText(i.getRecipeTitle());
//                }
//                if (url != null) {
//                    url.setText(i.getRecipeURL());
//                }
//                if (ingredients != null) {
//                    ingredients.setText(i.getRecipeIngredients());
//                }
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return view;
//    }
}
