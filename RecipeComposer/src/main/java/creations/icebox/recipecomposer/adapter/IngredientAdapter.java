package creations.icebox.recipecomposer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import creations.icebox.recipecomposer.Ingredient;
import creations.icebox.recipecomposer.R;

public class IngredientAdapter extends ArrayAdapter<Ingredient> {

    private static final String TAG = "***INGREDIENT ADAPTER***: ";

    private List<Ingredient> ingredientList;

    public IngredientAdapter(Context context, int resource, List<Ingredient> ingredientList) {
        super(context, resource, ingredientList);
        this.ingredientList = ingredientList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_ingredient, null);
        }

        Ingredient i = ingredientList.get(position);

        if (i != null) {

            try {
                TextView title = (TextView) view.findViewById(R.id.ingredientTitleTextView);

                if (title != null) {
                    title.setText(i.getTitle());
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        return view;
    }
}

//package creations.icebox.recipecomposer.adapter;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import creations.icebox.recipecomposer.Ingredient;
//import creations.icebox.recipecomposer.R;
//
//public class IngredientAdapter extends BaseAdapter {
//    private static final String DEBUG_TAG = "***INGREDIENT ADAPTER***: ";
//
//    private HashMap<String, Ingredient> ingredientMap;
//    private String[] mKeys;
//    private Context context;
//
//    /*
//    * here we must override the constructor for ArrayAdapter.
//    * the only variable we care about now is ArrayList<Item> objects
//    * because it is the list of objects we want to display.
//    */
//    public IngredientAdapter(Context context, int resource, HashMap<String, Ingredient> ingredientMap) {
//        Log.d(DEBUG_TAG, "in constructor");
//        this.context = context;
//        this.ingredientMap = ingredientMap;
//        this.mKeys = ingredientMap.keySet().toArray(new String[ingredientMap.size()]);
//    }
//
//    static class ViewHolder {
//        protected TextView textview;
//        protected CheckBox checkbox;
//    }
//
//    @Override
//    public int getCount() {
//        return ingredientMap.size();
//    }
//
//    // This is tricky since the setListAdapter looks
//    // for the the String value.. to display..
//    // in this case it is an ingredient name
//    @Override
//    public Ingredient getItem(int position) {
//        return ingredientMap.get(mKeys[position]);
//    }
//
//    @Override
//    public long getItemId(int arg0) {
//        return arg0;
//    }
//    /*
//   * we are overriding the getView method here - this is what defines how each
//   * list item will look.
//   */
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        Log.d(DEBUG_TAG, "in getView");
//
//        // assign the view we are converting to a local variable
//        View view = null;
//
//
//        // first check to see if the view is null. if so, we have to inflate it.
//        // to inflate it basically means to render, or show, the view.
//
//        // http://www.vogella.com/tutorials/AndroidListView/article.html
//        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            view = inflater.inflate(R.layout.list_item_ingredient, null);
//
//            final ViewHolder viewHolder = new ViewHolder();
//            viewHolder.textview = (TextView) view.findViewById(R.id.title);
//            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
//            viewHolder.checkbox
//                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//                        @Override
//                        public void onCheckedChanged(CompoundButton buttonView,
//                                                     boolean isChecked) {
//                            Ingredient element = (Ingredient) viewHolder.checkbox.getTag();
//                            element.setSelected(buttonView.isChecked());
//
//                        }
//                    });
//            view.setTag(viewHolder);
//            viewHolder.checkbox.setTag(ingredientMap.get(mKeys[position]));
//        } else {
//            view = convertView;
//            ((ViewHolder) view.getTag()).checkbox.setTag(ingredientMap.get(mKeys[position]));
//        }
//
//        ViewHolder holder = (ViewHolder) view.getTag();
//        holder.textview.setText(mKeys[position]);
//        holder.checkbox.setChecked(ingredientMap.get(mKeys[position]).isSelected());
//
//        /*
//         * Recall that the variable position is sent in as an argument to this method.
//         * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
//         * iterates through the list we sent it)
//         *
//         * Therefore, i refers to the current Item object.
//         */
//        Ingredient i = getItem(position);
//
//        if (i != null) {
//
//            try {
//                TextView title = (TextView) view.findViewById(R.id.title);
//
//                if (title != null) {
//                    Log.d(DEBUG_TAG, "right here: " + i.getIngredientTitle());
//                    title.setText(i.getIngredientTitle());
//                }
////                if (url != null) {
////                    url.setText(i.topIngredientTag());
////                }
////                if (ingredients != null) {
////                    ingredients.setText(i.getRecipeIngredients());
////                }
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return view;
//    }
//}