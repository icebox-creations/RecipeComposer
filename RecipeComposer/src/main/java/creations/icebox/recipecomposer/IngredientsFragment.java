package creations.icebox.recipecomposer;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import creations.icebox.recipecomposer.adapter.IngredientAdapter;
import creations.icebox.recipecomposer.adapter.TabsPagerAdapter;
import creations.icebox.recipecomposer.helper.SQLiteDAO;

public class IngredientsFragment extends ListFragment {
    private static final String TAG = "***INGREDIENTS FRAGMENT***: ";
    private SQLiteDAO sqLiteDAO;
    private View rootView;

    Button addIngredientButton;
    EditText ingredientEditText;

    StringBuffer ingredientTitles;

    OnPageChangeListener mCallback;

    private ArrayList<Ingredient> ingredientArrayListChecked;
    private ArrayList<Ingredient> ingredientArrayList;

    public interface OnPageChangeListener {
        public void onNavigationToRecipes(StringBuffer ingredientTitles);
    }

    public static IngredientsFragment newInstance() {
        Log.d(TAG, "newInstance of IngredientsFragment");
        return new IngredientsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        sqLiteDAO = new SQLiteDAO(getActivity());
        sqLiteDAO.open();
        ingredientTitles = new StringBuffer();
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
        if (activity instanceof OnPageChangeListener) {
            mCallback = (OnPageChangeListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement IngredientsFragment.OnNavigationListener");
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.ingredient_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.action_remove_ingredient:
                // Remove this ingredient from the db
                sqLiteDAO.deleteIngredient(ingredientArrayList.get(info.position));

                // Remove from listview
                ingredientArrayList.remove(info.position);
                ((IngredientAdapter)getListAdapter()).notifyDataSetChanged();
                return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        sqLiteDAO.open();
        super.onResume();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        sqLiteDAO.close();
        super.onPause();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);


        try {
            addIngredientButton = (Button) rootView.findViewById(R.id.addIngredient);
            ingredientEditText = (EditText) rootView.findViewById(R.id.ingredientEditText);

            addIngredientButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "add ingredient button clicked!");

                    if (ingredientEditText.getText().length() > 0) {
                        String ingredientTitle = ingredientEditText.getText().toString();
                        Ingredient ingredient = sqLiteDAO.createIngredient(ingredientTitle);

                        if (ingredient != null) {
                            ArrayAdapter<Ingredient> arrayAdapter = (ArrayAdapter<Ingredient>) getListAdapter();
                            arrayAdapter.add(ingredient);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getActivity(),
                                "Sorry, you must supply an ingredient",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreateView: " + e.toString());
        }
        return rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ingredientArrayList = sqLiteDAO.getAllIngredients();


        // https://developer.android.com/guide/topics/ui/menus.html#context-menu
        ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.ingredient_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_remove_ingredient:
                        Toast.makeText(getActivity(), "Remove clicked", Toast.LENGTH_SHORT).show();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick");

                ((CheckBox) view.getTag(R.id.ingredientCheckbox)).toggle();

                ingredientArrayListChecked = ((IngredientAdapter)getListAdapter()).getIngredientArrayList();

                ingredientTitles.delete(0, ingredientTitles.length());

                String title = ((TextView)view.findViewById(R.id.ingredientTitleTextView)).getText().toString();
                Toast.makeText(getActivity(),
                        "Ingredient #" + position + " clicked " + title,
                        Toast.LENGTH_SHORT).show();

                for (Ingredient ingredient : ingredientArrayListChecked) {
                    if (ingredient.isSelected()) {
                        // push the ingredient onto the list
                        if (ingredientTitles.length() == 0) {
                            ingredientTitles.append(ingredient.getIngredientTitle());
                        } else if (ingredientTitles.length() > 0) {
                            ingredientTitles.append("," + ingredient.getIngredientTitle());
                        }
                    }
                }
                Toast.makeText(getActivity(), ingredientTitles, Toast.LENGTH_SHORT).show();

                // Send the event to the host activity
                mCallback.onNavigationToRecipes(ingredientTitles);
            }
        });
        setListAdapter(new IngredientAdapter(getActivity(),
                android.R.layout.simple_list_item_multiple_choice, ingredientArrayList));
    }
}

//import android.app.Activity;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.os.Environment;
//import android.preference.PreferenceManager;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.ListFragment;
//import android.text.Layout;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListAdapter;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Toast;
//
//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.lang.reflect.Array;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
//import creations.icebox.recipecomposer.adapter.IngredientAdapter;
//import creations.icebox.recipecomposer.helper.DBHelper;
//
//public class IngredientsFragment extends ListFragment {
//
//    private static final String TAG = "***INGREDIENTS FRAGMENT***: ";
//
//    private DBHelper dbh;   // instances of fragments can be destroyed..
//    private View rootView;
//    private static SharedPreferences settings;
//    HashMap<String, Ingredient> ingredientMap;
//
//    public static IngredientsFragment newInstance() {
//        Log.d(TAG, "newInstance of IngredientsFragment");
//        return new IngredientsFragment();
//    }
//
//    public IngredientsFragment() {
//        ingredientMap = new HashMap<String, Ingredient>();
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        Log.d(TAG, "onCreateView");
//
//        dbh = new DBHelper(getActivity());
//        rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);
//        HashMap<String, Ingredient> init_ingredients = dbh.getAllIngredients();
//
//        appendHashToIngredientMap(init_ingredients);
//
//        populateListView();
//
//        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
//
//        // button click -> add the ingredient to the user's list of ingredients..
//        final Button addIngredientButton = (Button) rootView.findViewById(R.id.addIngredient);
//        final EditText  ingredientEditText  = (EditText) rootView.findViewById(R.id.ingredientEditText);
//        addIngredientButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                try{
//                    String ingredientTitle = ingredientEditText.getText().toString();
//                    Log.d("Clicked!: ", ingredientTitle);
//                    if(ingredientMap.containsKey(ingredientTitle) == false){
//                        ingredientMap.put(ingredientTitle, new Ingredient(ingredientTitle));
//                        dbh.insertIngredient(ingredientMap.get(ingredientTitle));
//
//                        populateListView();
//                    }
//                } catch (Exception e) {
//                    Log.d("Caught File Exception: ", e.getMessage());
//                }
//            }
//        });
//
//        final Button findAllIngredients = (Button) rootView.findViewById(R.id.findAllIngredients);
//        findAllIngredients.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v) {
//                String ingredientTitle = ingredientEditText.getText().toString();
//                SharedPreferences.Editor edit = settings.edit();
//                String ingredientStr = settings.getString(ingredientTitle, "<none>");
//
//                try{
//                    Ingredient testIngredient = null; // = dbh.getIngredient(ingredientTitle);
//                    String response = "";
//                    if (testIngredient != null){
//                        response = testIngredient.getTitle() + " Exists!";
//                    } else {
//                        response = "<Not Implemented Yet>";
//                    }
//                    Toast.makeText(getActivity(), "Ingredient (find button): " + new String(response), Toast.LENGTH_SHORT).show();
//                } catch (Exception e){
//                     Log.d("ERROR in File Manip: ", e.getMessage());
//                }
//            }
//        });
//
//        final Button readIngredientButton = (Button) rootView.findViewById(R.id.readAllIngredients);
//        readIngredientButton.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v) {
//                try{
//                    Toast.makeText(getActivity(), "Reading all ingredients from database! ", Toast.LENGTH_SHORT).show();
//                    HashMap<String, Ingredient> ingredients_hash = dbh.getAllIngredients();
//
//                    String ingredientsSerialized = "<NONE>";
//
//                    if (ingredients_hash == null){
//                        Toast.makeText(getActivity(), "Ingredients table is empty.. add ingredients!", Toast.LENGTH_SHORT).show();
//                    } else{
//                        int ingredients_count = ingredients_hash.size();
//                        if (ingredients_count > 0){
//                            ingredientsSerialized = ".";
//                            int iter = ingredients_count; // iterator until 1, if 1 then dont add comma..
//                            String title = "";
//                            String description = "";
//                            for (Map.Entry<String, Ingredient> entry : ingredients_hash.entrySet()) {
//                                title = entry.getKey();
//                                Ingredient tmpIng = entry.getValue();
//                                ingredientsSerialized =  ((iter == 1) ? "": ", ") + title + ingredientsSerialized;
//                                iter--;
//                            }
//                        }
//                    }
//
//                    Toast.makeText(getActivity(), "Ingredients (read button): " + ingredientsSerialized, Toast.LENGTH_SHORT).show();
//                } catch (Exception e){
//                    Log.d("ERROR: ", e.getMessage());
//                }
//            }
//        });
//
//
//        final Button clearDBButton = (Button) rootView.findViewById(R.id.clearFile);
//        clearDBButton.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v) {
//                try{
//                    Toast.makeText(getActivity(), "DB Tables Dropped!", Toast.LENGTH_SHORT).show();
//                    dbh.dropTables();
//                    appendHashToIngredientMap(null);
//                    populateListView();
//                } catch (Exception e){
//                    Log.d("ERROR: ", e.getMessage());
//                }
//            }
//        });
//
//
//        return rootView;
//    }
//
//
//    private void appendHashToIngredientMap(HashMap<String, Ingredient> hash1){
//        if (hash1 != null){
//            ingredientMap = new HashMap<String, Ingredient>(hash1);
//        } else {
//            ingredientMap = new HashMap<String, Ingredient>();
//        }
//    }
//
//    private void populateListView(){
//        ListView lv1 = (ListView) rootView.findViewById(android.R.id.list);
//        lv1.setAdapter(new IngredientAdapter(getActivity(), android.R.layout.simple_selectable_list_item, ingredientMap));
//    }
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        Log.d(TAG, "onCreate");
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        Log.d(TAG, "onAttach");
//        super.onAttach(activity);
//    }
//
//    @Override
//    public void onDetach() {
//        Log.d(TAG, "onDetach");
//        super.onDetach();
//    }
//
//    public boolean isExternalStorageWritable() {
//        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state)) {
//            return true;
//        }
//        return false;
//    }
//
//}


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