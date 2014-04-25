package creations.icebox.recipecomposer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

import creations.icebox.recipecomposer.R;
import creations.icebox.recipecomposer.lib.Recipe;

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
//        TextView recipeURL;
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

        final ViewHolderItem viewHolder;
        //Log.d(TAG, "ConvertView " + String.valueOf(position));

        /* convertView is the list item */
        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_recipe, null);

            // set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.recipeTitle = (TextView) convertView.findViewById(R.id.recipeTitleTextView);
            viewHolder.recipeIngredients = (TextView) convertView.findViewById(R.id.recipeIngredientsTextView);
//            viewHolder.recipeURL = (TextView) convertView.findViewById(R.id.recipeURLTextView);

            convertView.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on the resource file every time
            // just use the viewHolder
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        // Recipe item based on the position
        Recipe recipe = recipeArrayList.get(position);
        String recipeImageURL = recipe.getRecipePicUrl();


        // assign values if the recipe is not null
        if (recipe != null) {
            viewHolder.recipeTitle.setText(recipe.getRecipeTitle());
            viewHolder.recipeIngredients.setText(recipe.getRecipeIngredients());
//            viewHolder.recipeURL.setText(recipe.getRecipeURL());
        }

        ImageView iv =  (ImageView) convertView.findViewById(R.id.recipeImageView);
        if (iv != null) {
                (new DownloadImageTask(iv))
                        .execute(recipeImageURL);
        }

        convertView.findViewById(R.id.favoriteRecipeImageView)
                .setOnClickListener(new View.OnClickListener()
        {
            int favoritedState = 0;
            @Override
            public void onClick(View v) {
                if (favoritedState == 0) {
                    ((ImageView)v).setImageResource(android.R.drawable.btn_star_big_on);
                    favoritedState = 1;
                } else if (favoritedState == 1) {
                    ((ImageView)v).setImageResource(android.R.drawable.btn_star_big_off);
                    favoritedState = 0;
                }
            }

        });

        return convertView;
    }

//    public void setAllImages(){
//        for (int i = 0; i < recipeArrayList.size(); i++) {
//            recipeArrayList.get(i).getRecipePicUrl();
//        }
//    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                Log.d(TAG, "IMAGE URL: " + urldisplay);
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                Log.d(TAG, "LOADED THE IMAGE FROM INTERNET..");
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                bmImage.setImageBitmap(result);
            }
        }
    }
}
