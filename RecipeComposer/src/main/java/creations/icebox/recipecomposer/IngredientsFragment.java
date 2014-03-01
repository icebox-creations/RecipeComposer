package creations.icebox.recipecomposer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import android.view.View;
import android.view.View.OnClickListener;

import java.io.FileOutputStream;
import java.util.ArrayList;

import creations.icebox.recipecomposer.adapter.IngredientAdapter;

public class IngredientsFragment extends ListFragment {

    private static final String DEBUG_TAG = "***INGREDIENTS FRAGMENT: ";
    private static SharedPreferences settings;
    ArrayList<Ingredient> ingredientList = new ArrayList<Ingredient>();

    public static IngredientsFragment newInstance() {
        Log.d(DEBUG_TAG, "newInstance of IngredientsFragment");
        return new IngredientsFragment();
    }

    public IngredientsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "onCreateView");
        final View rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // button click -> add the ingredient to the user's list of ingredients..
        final Button addIngredientButton = (Button) rootView.findViewById(R.id.addIngredient);
        final EditText  ingredientEditText  = (EditText) rootView.findViewById(R.id.ingredientEditText);
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try{
//                    FileOutputStream fos = getActivity().openFileOutput("ingredientslist_file", getActivity().MODE_PRIVATE);
//                    fos.write();
//                    fos.close();
                    String ingredientTitle = ingredientEditText.getText().toString();
                    ingredientList.add(new Ingredient(ingredientTitle));
                    ListView lv = (ListView) rootView.findViewById(android.R.id.list);
                    lv.setAdapter(new IngredientAdapter(getActivity(), android.R.layout.simple_selectable_list_item, ingredientList));

                    // store in database or private preferences
                    SharedPreferences.Editor edit = settings.edit();
                    edit.putString("ingredient", ingredientTitle);
                    edit.apply();
                    String ingredient = settings.getString("ingredient", "");
                    Log.d("Clicked!: ", ingredient);
                } catch (Exception e) {
                    Log.d("Caught File Exception: ", e.getMessage());
                }
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(DEBUG_TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        Log.d(DEBUG_TAG, "onDetach");
        super.onDetach();
    }

}