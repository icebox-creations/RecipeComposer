package creations.icebox.recipecomposer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import creations.icebox.recipecomposer.R;
import creations.icebox.recipecomposer.helper.SQLiteDAO;
import creations.icebox.recipecomposer.lib.Recipe;

/** Follows the ViewHolder Design Pattern */
public class RecipeAdapter extends ArrayAdapter<Recipe> {

    private static final String TAG = "***RECIPE ADAPTER***: ";
    private ArrayList<Recipe> recipeArrayList;
    SQLiteDAO sqLiteDAO;

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

        sqLiteDAO = new SQLiteDAO(getContext());
        sqLiteDAO.open();
    }

    /**
     * ViewHolder: caches our TextViews
     */
    static class ViewHolderItem {
        TextView recipeTitle;
        TextView recipeIngredients;
        TextView recipeURL;
        ImageView recipeStar;
        ImageView recipePic;
    }

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

        Log.d(TAG, "IN GET VIEW!!");
        final ViewHolderItem viewHolder;

        // convertView is the list item
        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_recipe, null);

            // set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.recipeTitle = (TextView) convertView.findViewById(R.id.recipeTitleTextView);
            viewHolder.recipeIngredients = (TextView) convertView.findViewById(R.id.recipeIngredientsTextView);
            viewHolder.recipeURL = (TextView) convertView.findViewById(R.id.recipeUrlTextView);
            viewHolder.recipeStar = (ImageView) convertView.findViewById(R.id.recipeFavoriteStarImageView);
            viewHolder.recipePic = (ImageView) convertView.findViewById(R.id.recipeImageView);

            convertView.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on the resource file every time
            // just use the viewHolder
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        // Recipe item based on the position
        if (recipeArrayList.isEmpty()) {
            Log.d(TAG, "recipeArrayList is empty");
            return convertView;
        }

        Recipe recipe = recipeArrayList.get(position);

        if (recipe != null) {
            String recipeImageURL = recipe.getRecipePicUrl();

            // assign values if the recipe is not null
            viewHolder.recipeTitle.setText(recipe.getRecipeTitle());
            viewHolder.recipeIngredients.setText(recipe.getRecipeIngredients());

            try {
                URL recipeUrl = new URL(recipe.getRecipeURL());
                viewHolder.recipeURL.setText(recipeUrl.getHost());
            } catch (MalformedURLException e) {
                Log.d(TAG, "URL parsing error.");
                recipeImageURL = "";
            }

            UrlImageViewHelper.setUrlDrawable(viewHolder.recipePic, recipeImageURL, R.drawable.placeholder);

            if (sqLiteDAO.isExistsRecipe(recipe)) {
                viewHolder.recipeStar.setImageResource(R.drawable.star_small);
            } else {
                viewHolder.recipeStar.setImageResource(0);
            }
            viewHolder.recipeStar.invalidate();
        }

//        viewHolder.recipeStar.invalidate();
//        notifyDataSetChanged();

        return convertView;
    }
}
