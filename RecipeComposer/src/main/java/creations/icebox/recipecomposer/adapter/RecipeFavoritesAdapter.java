package creations.icebox.recipecomposer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import creations.icebox.recipecomposer.R;
import creations.icebox.recipecomposer.lib.Recipe;

public class RecipeFavoritesAdapter extends ArrayAdapter<Recipe> {
    private static final String TAG = "***RECIPE FAVORITES ADAPTER***: ";
    private ArrayList<Recipe> recipeFavoritesArrayList;


    public RecipeFavoritesAdapter(Context context, int resource, ArrayList<Recipe> recipeFavoritesArrayList) {
        super(context, resource, recipeFavoritesArrayList);
        this.recipeFavoritesArrayList = recipeFavoritesArrayList;
    }

    static class ViewHolderItem {
        TextView recipeFavoriteTitle;
        TextView recipeFavoriteIngredients;
        TextView recipeFavoriteURL;
    }

    @Override
    public void remove(Recipe object) {
        super.remove(object);
        recipeFavoritesArrayList.remove(object);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolderItem viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_recipe_favorite, null);

            viewHolder = new ViewHolderItem();
            viewHolder.recipeFavoriteTitle = (TextView) convertView.findViewById(R.id.recipeFavoriteTitleTextView);
            viewHolder.recipeFavoriteIngredients = (TextView) convertView.findViewById(R.id.recipeFavoriteIngredientsTextView);
            viewHolder.recipeFavoriteURL = (TextView) convertView.findViewById(R.id.recipeFavoriteUrlTextView);

            // store the holder with the view
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        // Recipe favorite item based on the position
        Recipe recipe = recipeFavoritesArrayList.get(position);

        // assign values if the ingredient is not null
        if (recipe != null) {
            viewHolder.recipeFavoriteTitle.setText(recipe.getRecipeTitle());
            viewHolder.recipeFavoriteIngredients.setText(recipe.getRecipeIngredients());
            viewHolder.recipeFavoriteURL.setText(recipe.getRecipeIngredients());
        }
        return convertView;
    }
}
