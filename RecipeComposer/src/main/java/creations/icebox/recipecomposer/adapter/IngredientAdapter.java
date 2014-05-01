package creations.icebox.recipecomposer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

import creations.icebox.recipecomposer.R;
import creations.icebox.recipecomposer.lib.Ingredient;

/** Follows the ViewHolder Design Pattern */
public class IngredientAdapter extends ArrayAdapter<Ingredient> {

    private static final String TAG = "***INGREDIENT ADAPTER***: ";

    private ArrayList<Ingredient> ingredientArrayList; // for the view

    public ArrayList<Ingredient> getIngredientArrayListView() {
        return ingredientArrayListView;
    }

    public ArrayList<Ingredient> getIngredientArrayList() {
        return ingredientArrayList;
    }

    private ArrayList<Ingredient> ingredientArrayListView;  // used fro the data

    private StringBuffer ingredientTitles;

    public StringBuffer getIngredientTitles() {
        return ingredientTitles;
    }

    public IngredientAdapter(Context context, int resource, ArrayList<Ingredient> ingredientArrayListView) {
        super(context, resource, ingredientArrayListView);
        this.ingredientArrayListView = ingredientArrayListView;
        ingredientArrayList = new ArrayList<Ingredient>(ingredientArrayListView); // original datas
        ingredientTitles = new StringBuffer();
    }

    /**
     * ViewHolder: caches our TextView and CheckBox
     */
    static class ViewHolderItem {
        TextView ingredientTitle;
        CheckBox ingredientCheckBox;
    }

    public void toggleCheckBoxDrawable(){


    }

    @Override
    public void add(Ingredient ingredient) {
        /* all ingredients */
        ingredientArrayList.add(ingredient);

        ingredientArrayListView.clear();
        for (Ingredient i : ingredientArrayList) {
                ingredientArrayListView.add(i);
                Log.d(TAG, " - - - - - " + i.getIngredientTitle() + ", ");
        }
    }

    @Override
    public void remove(Ingredient object) {
        super.remove(object);
        ingredientArrayList.remove(object);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //Log.d(TAG, "position: " + position + "<- getView");
        final ViewHolderItem viewHolder;
        //Log.d(TAG, "ConvertView " + String.valueOf(position));

        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_ingredient, null);

            // set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.ingredientTitle = (TextView) convertView.findViewById(R.id.ingredientTitleTextView);
            viewHolder.ingredientCheckBox = (CheckBox) convertView.findViewById(R.id.ingredientCheckbox);

            // store the holder with the view
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.ingredientCheckbox, viewHolder.ingredientCheckBox);


            viewHolder.ingredientCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "CheckBox was clicked");
                    CheckBox checkBox = (CheckBox) v;

                     /* get the tag of the ingredient that belongs to this checbkox..
                      * from the ingredient arraylist for listview  */
                    Ingredient ingredient = (Ingredient) checkBox.getTag();
                    int pos = 0;
                    for ( ; pos < ingredientArrayListView.size(); pos++){
                        if (ingredientArrayListView.get(pos).getIngredientTitle()
                                  .equals(ingredient.getIngredientTitle()) )
                        {
                            break;
                        }
                    }

                    /* This is to fix the checkbox double check and off issue */
                    checkBox.toggle(); // under the assumtion toggle happens.. we need to do this
                    Log.d(TAG, " ::: " + pos);
                    itemClickListener(v, pos);
                }
            });

        } else {
            // we've just avoided calling findViewById() on the resource file every time
            // just use the viewHolder
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        // Ingredient item based on the position
        Ingredient ingredient = ingredientArrayListView.get(position);

        TextView ingredientIndicator = (TextView) convertView.findViewById(R.id.ingredientIndicator);

        switch (ingredient.getSelectedState()){
            case NORMAL_STATE:
                ingredientIndicator.setBackgroundColor(getContext().getResources().getColor(R.color.grey));
                break;
            case EXCLUDE_STATE:
                ingredientIndicator.setBackgroundColor(getContext().getResources().getColor(R.color.red));
                break;
            case REQUIRED_STATE:
                ingredientIndicator.setBackgroundColor(getContext().getResources().getColor(R.color.green));
                break;
            case NULL_STATE:
                // undefined behaviour..
            default:
                ingredientIndicator.setBackgroundColor(getContext().getResources().getColor(R.color.blue));
                break;
        }

        // assign values if the ingredient is not null
        if (ingredient != null) {
            viewHolder.ingredientTitle.setText(ingredient.getIngredientTitle());
            viewHolder.ingredientCheckBox.setChecked(ingredient.isSelected());
            viewHolder.ingredientCheckBox.setTag(ingredient);
        }

        return convertView;
    }

    public void changeIngredientSelectedState(View view, int position){
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.ingredientCheckbox);
        Ingredient ingredient = ingredientArrayListView.get(position);
        ingredient.setSelected(checkBox.isChecked());
    }

    public void itemClickListener(View view, int position) {

        Log.d(TAG, "position: " + position + "<- itemClickListener");

        Log.d(TAG, "itemClickListener in the adapter!");

        /* Manually check the checkbox and select the ingredient  */
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.ingredientCheckbox);
        checkBox.toggle();

        Ingredient ingredient = ingredientArrayListView.get(position);
        ingredient.setSelected(checkBox.isChecked());

        Log.d(TAG, "INGERDIENT SELECTED: " + ingredient.getIngredientTitle());


        /* seth the list of ingedient titles that we want in the url */
        ingredientTitles.delete(0, ingredientTitles.length());

        for (Ingredient i : ingredientArrayList) {
            if (i.isSelected()) {
                String ingredientTitlePrioritized =  i.getIngredientTitle();
                switch (i.getSelectedState()){
                    case EXCLUDE_STATE:
                        ingredientTitlePrioritized = "-" + ingredientTitlePrioritized;
                        break;
                    case REQUIRED_STATE:
                        ingredientTitlePrioritized = "+" + ingredientTitlePrioritized;
                        break;
                }

                ingredientTitlePrioritized = ingredientTitlePrioritized.trim().replace(" ", "+");

                if (ingredientTitles.length() == 0) {
                    ingredientTitles.append(ingredientTitlePrioritized);
                } else if (ingredientTitles.length() > 0) {
                    ingredientTitles.append("," + ingredientTitlePrioritized);
                }
            }
        }
        Log.d(TAG, "ingredient titles in Checkbox: = " + ingredientTitles);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.d(TAG, "performFiltering");
                constraint = constraint.toString().toLowerCase();
                FilterResults results = new FilterResults();

                if (constraint.toString().length() > 0) {
                    for (Ingredient anIngredient : ingredientArrayList) {
                        Log.d(TAG, " Original Data.. " + anIngredient.getIngredientTitle() + "... ");
                    }

                    ArrayList<Ingredient> found = new ArrayList<Ingredient>();
                    for (Ingredient ingredient : ingredientArrayList) {
                        if (ingredient.getIngredientTitle().toLowerCase().contains(constraint)) {
                            found.add(ingredient);
                            Log.d(TAG, " View List we want " + ingredient.getIngredientTitle() + ", ");
                        }
                    }
                    results.values = new ArrayList<Ingredient>(found);
                    results.count = found.size();
                } else {
                    results.values = new ArrayList<Ingredient>(ingredientArrayList);
                    results.count = ingredientArrayList.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                Log.d(TAG, "publishResults");
                ingredientArrayListView.clear();
                try {
                    for (Ingredient ingredient : (ArrayList<Ingredient>) results.values) {
                        Log.d(TAG, " + + + + + " + ingredient.getIngredientTitle() + ", ");
                        ingredientArrayListView.add(ingredient);
                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, "HEY " + e.getMessage());
                }
                notifyDataSetChanged();
            }
        };
    }
}