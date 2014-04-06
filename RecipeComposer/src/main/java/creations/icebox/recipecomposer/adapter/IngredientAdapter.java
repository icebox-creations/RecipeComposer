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

import creations.icebox.recipecomposer.lib.Ingredient;
import creations.icebox.recipecomposer.R;

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
    }

    //    @Override
    public void add(Ingredient ingredient, String searchQuery) {
        this.add(ingredient);

        ingredientArrayListView.clear();
        for (Ingredient i : ingredientArrayList) {
            if (i.getIngredientTitle().toLowerCase().contains(searchQuery)) {
                ingredientArrayListView.add(i);
                Log.d(TAG, " - - - - - " + i.getIngredientTitle() + ", ");
            }
        }
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

            /*  */

//            convertView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    Log.d(TAG, "LONG CLICKED from adapter");
//                    return false;
//                }
//            });


            /*  */

//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, "CLICKED from adapter");
//
//                    Ingredient ingredient = (Ingredient) viewHolder.ingredientCheckBox.getTag();
//
//                    if (viewHolder.ingredientCheckBox.isChecked()) {
//                        Log.d(TAG, "checked so uncheck");
//                        ingredient.setSelected(false);
//                    } else {
//                        Log.d(TAG, "not checked so check");
//                        ingredient.setSelected(true);
//                    }
//                    viewHolder.ingredientCheckBox.toggle();
//
//                    ingredientTitles.delete(0, ingredientTitles.length());
//
//                    for (Ingredient i : ingredientArrayList) {
//                        if (i.isSelected()) {
//                            if (ingredientTitles.length() == 0) {
//                                ingredientTitles.append(i.getIngredientTitle());
//                            } else if (ingredientTitles.length() > 0) {
//                                ingredientTitles.append("," + i.getIngredientTitle());
//                            }
//                        }
//                    }
//                    Log.d(TAG, "ingredient titles in TextView = " + ingredientTitles);
//                }
//            });

/*  */

//            viewHolder.ingredientTitle.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, "TextView was clicked");
//
//                    Ingredient ingredient = (Ingredient) viewHolder.ingredientCheckBox.getTag();
//
//                    if (viewHolder.ingredientCheckBox.isChecked()) {
//                        Log.d(TAG, "checked so uncheck");
//                        ingredient.setSelected(false);
//                    } else {
//                        Log.d(TAG, "not checked so check");
//                        ingredient.setSelected(true);
//                    }
//                    viewHolder.ingredientCheckBox.toggle();
//
//                    ingredientTitles.delete(0, ingredientTitles.length());
//
//                    for (Ingredient i : ingredientArrayList) {
//                        if (i.isSelected()) {
//                            if (ingredientTitles.length() == 0) {
//                                ingredientTitles.append(i.getIngredientTitle());
//                            } else if (ingredientTitles.length() > 0) {
//                                ingredientTitles.append("," + i.getIngredientTitle());
//                            }
//                        }
//                    }
//                    Log.d(TAG, "ingredient titles in TextView = " + ingredientTitles);
//                }
//            });
//
            viewHolder.ingredientCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "CheckBox was clicked");
                    CheckBox checkBox = (CheckBox) v;
                    /* This is to fix the checkbox double check and off issue */
                    checkBox.toggle(); // under the assumtion toggle happens.. we need to do this
                    itemClickListener(v, position);
                }
            });

//            viewHolder.ingredientCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    Log.d(TAG, "checkbox changed to " + isChecked);
//                    Ingredient ingredient = (Ingredient) buttonView.getTag();
//                    ingredient.setSelected(isChecked);
//                }
//            });

        } else {
            // we've just avoided calling findViewById() on the resource file every time
            // just use the viewHolder
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        // Ingredient item based on the position
        Ingredient ingredient = ingredientArrayListView.get(position);

        // assign values if the ingredient is not null
        if (ingredient != null) {
            viewHolder.ingredientTitle.setText(ingredient.getIngredientTitle());
            viewHolder.ingredientCheckBox.setChecked(ingredient.isSelected());
            viewHolder.ingredientCheckBox.setTag(ingredient);
        }

        return convertView;
    }

    public void itemClickListener(View view, int position) {

        Log.d(TAG, "position: " + position + "<- itemClickListener");

        Log.d(TAG, "itemClickListener in the adapter!");

//        Ingredient ingredient = (Ingredient) checkBox.getTag();

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.ingredientCheckbox);
        checkBox.toggle();

        Ingredient ingredient = ingredientArrayListView.get(position);
        ingredient.setSelected(checkBox.isChecked());

//        if (ingredient.isSelected()) {
//            ingredient.setSelected(false);
//        } else {
//            ingredient.setSelected(true);
//        }

        ingredientTitles.delete(0, ingredientTitles.length());

        for (Ingredient i : ingredientArrayList) {
            i.setIngredientTitle(i.getIngredientTitle().trim().replace(" ", "+"));
            if (i.isSelected()) {
                if (ingredientTitles.length() == 0) {
                    ingredientTitles.append(i.getIngredientTitle());
                } else if (ingredientTitles.length() > 0) {
                    ingredientTitles.append("," + i.getIngredientTitle());
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

                if (constraint != null && constraint.toString().length() > 0) {
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