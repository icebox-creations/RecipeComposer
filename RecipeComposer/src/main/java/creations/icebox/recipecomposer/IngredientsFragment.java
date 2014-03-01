package creations.icebox.recipecomposer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import creations.icebox.recipecomposer.adapter.IngredientAdapter;

public class IngredientsFragment extends ListFragment {

    private static final String DEBUG_TAG = "***INGREDIENTS FRAGMENT: ";
    private static SharedPreferences settings;
    HashMap<String, Ingredient> ingredientMap;

    public static IngredientsFragment newInstance() {
        Log.d(DEBUG_TAG, "newInstance of IngredientsFragment");
        return new IngredientsFragment();
    }

    public IngredientsFragment() {
        ingredientMap = new HashMap<String, Ingredient>();
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
                    String ingredientTitle = ingredientEditText.getText().toString();
                    Log.d("Clicked!: ", ingredientTitle);
                    if(ingredientMap.containsKey(ingredientTitle) == false){
                        ingredientMap.put(ingredientTitle, new Ingredient(ingredientTitle));
                        ListView lv = (ListView) rootView.findViewById(android.R.id.list);
                        lv.setAdapter(new IngredientAdapter(getActivity(), android.R.layout.simple_selectable_list_item, ingredientMap));

                        // Playing with storing in database OR private preferences
                        SharedPreferences.Editor edit = settings.edit();
                        edit.putString(ingredientTitle, ingredientTitle);
                        edit.apply();
                        Log.d("Stored!: ", ingredientTitle);

                        FileOutputStream fos = getActivity().openFileOutput("ingredientslist_file", getActivity().MODE_PRIVATE);
                        fos.write(ingredientTitle.getBytes());
                        fos.close();
                    }
                } catch (Exception e) {
                    Log.d("Caught File Exception: ", e.getMessage());
                }
            }
        });

        final Button readIngredientButton = (Button) rootView.findViewById(R.id.readIngredient);
        readIngredientButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                String ingredientTitle = ingredientEditText.getText().toString();
                SharedPreferences.Editor edit = settings.edit();
                String ingredientStr = settings.getString(ingredientTitle, "<none>");
                Log.d("Read from memory!: ", ingredientStr);

                try{
                    FileInputStream fis = getActivity().openFileInput("ingredientslist_file");

                    byte[] buffer = new byte[1024];
                    int err = fis.read(buffer, 0, 1024);
                    if (err != 0){
                        Log.d("File error: ", "Error reading from file...ingredientslist_file.. " + Integer.toString(err));
                    }
                    Toast.makeText(getActivity(), "(Read from file) Last Added: " + new String(buffer), Toast.LENGTH_SHORT).show();
                    fis.close();
                    Log.d("Read from file all things read before: ", ingredientStr);
                } catch (Exception e){
                     Log.d("ERROR in File Manip: ", e.getMessage());
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

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}