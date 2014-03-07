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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import creations.icebox.recipecomposer.adapter.IngredientAdapter;
import creations.icebox.recipecomposer.helper.DBHelper;

public class IngredientsFragment extends ListFragment {

    private static final String TAG = "***INGREDIENTS FRAGMENT***: ";

    private DBHelper dbh;   // instances of fragments can be destroyed..
    private View rootView;
    private static SharedPreferences settings;
    HashMap<String, Ingredient> ingredientMap;

    public static IngredientsFragment newInstance() {
        Log.d(TAG, "newInstance of IngredientsFragment");
        return new IngredientsFragment();
    }

    public IngredientsFragment() {
        ingredientMap = new HashMap<String, Ingredient>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        dbh = new DBHelper(getActivity());
        rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);
        HashMap<String, Ingredient> init_ingredients = dbh.getAllIngredients();

        appendHashToIngredientMap(init_ingredients);

        populateListView();

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
                        dbh.insertIngredient(ingredientMap.get(ingredientTitle));

                        populateListView();
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

                try{
                    Ingredient testIngredient = null; // = dbh.getIngredient(ingredientTitle);
                    String response = "";
                    if (testIngredient != null){
                        response = testIngredient.getTitle() + " Exists!";
                    } else {
                        response = "<Not Implemented Yet>";
                    }
                    Toast.makeText(getActivity(), "Ingredient: " + new String(response), Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                     Log.d("ERROR in File Manip: ", e.getMessage());
                }
            }
        });

        final Button findAllIngredients = (Button) rootView.findViewById(R.id.findAllIngredients);
        findAllIngredients.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                try{
                    Toast.makeText(getActivity(), "Reading all ingredients from database! ", Toast.LENGTH_SHORT).show();
                    HashMap<String, Ingredient> ingredients_hash = dbh.getAllIngredients();

                    String ingredientsSerialized = "<NONE>";

                    if (ingredients_hash == null){
                        Toast.makeText(getActivity(), "Ingredients table is empty.. add ingredients!", Toast.LENGTH_SHORT).show();
                    } else{
                        int ingredients_count = ingredients_hash.size();
                        if (ingredients_count > 0){
                            ingredientsSerialized = ".";
                            int iter = ingredients_count; // iterator until 1, if 1 then dont add comma..
                            String title = "";
                            String description = "";
                            for (Map.Entry<String, Ingredient> entry : ingredients_hash.entrySet()) {
                                title = entry.getKey();
                                Ingredient tmpIng = entry.getValue();
                                ingredientsSerialized =  ((iter == 1) ? "": ", ") + title + ingredientsSerialized;
                                iter--;
                            }
                        }
                    }

                    Toast.makeText(getActivity(), "Ingredients: " + ingredientsSerialized, Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Log.d("ERROR: ", e.getMessage());
                }
            }
        });


        final Button clearDBButton = (Button) rootView.findViewById(R.id.clearFile);
        clearDBButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                try{
                    Toast.makeText(getActivity(), "DB Tables Dropped!", Toast.LENGTH_SHORT).show();
                    dbh.dropTables();
                    appendHashToIngredientMap(null);
                    populateListView();
                } catch (Exception e){
                    Log.d("ERROR: ", e.getMessage());
                }
            }
        });


        return rootView;
    }


    private void appendHashToIngredientMap(HashMap<String, Ingredient> hash1){
        if (hash1 != null){
            ingredientMap = new HashMap<String, Ingredient>(hash1);
        } else {
            ingredientMap = new HashMap<String, Ingredient>();
        }
    }

    private void populateListView(){
        ListView lv1 = (ListView) rootView.findViewById(android.R.id.list);
        lv1.setAdapter(new IngredientAdapter(getActivity(), android.R.layout.simple_selectable_list_item, ingredientMap));
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
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


//                        // Playing with storing in database OR private preferences
//                        SharedPreferences.Editor edit = settings.edit();
//                        edit.putString(ingredientTitle, ingredientTitle);
//                        edit.apply();
//                        Log.d("Stored!: ", ingredientTitle);
//
//                        // Direct file manip..l
//
//                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getActivity().openFileOutput("ingredientslist_file.txt", Context.MODE_PRIVATE | Context.MODE_APPEND));
//                        outputStreamWriter.write(ingredientTitle);
//                        outputStreamWriter.close();



//                    // read directly from file..
//                    InputStream inputStream = getActivity().openFileInput("ingredientslist_file.txt");
//                    if (inputStream != null){
//                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                        String receiveString = "";
//                        StringBuilder stringBuilder = new StringBuilder();
//
//                        while ( (receiveString = bufferedReader.readLine()) != null ) {
//                            stringBuilder.append(receiveString);
//                        }
//                        inputStream.close();
//
//                        String file_contents = stringBuilder.toString();
//                        Toast.makeText(getActivity(), "Ingredients Added: " + new String(file_contents), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getActivity(), "Couldnt read file: ingredientslist_file", Toast.LENGTH_SHORT).show();
//                    }
//                    Log.d("Read from file all things read before: ", ingredientStr);


//                    getActivity().deleteFile("ingredientslist_file.txt");