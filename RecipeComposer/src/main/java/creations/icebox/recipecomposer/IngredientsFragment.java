package creations.icebox.recipecomposer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import creations.icebox.recipecomposer.adapter.IngredientAdapter;
import creations.icebox.recipecomposer.helper.SQLiteDAO;
import creations.icebox.recipecomposer.lib.Ingredient;

public class IngredientsFragment extends ListFragment {
    private static final String TAG = "***INGREDIENTS FRAGMENT***: ";
    private SQLiteDAO sqLiteDAO;

    private ArrayList<String>   ingredientsSuggestionsArrayList;

    Button      clearQueryButton;
    EditText    keywordEditText;
    SearchView  keywordSearchView;  // for the keyword search query at the top of ingredients list
    SearchView  mSearchView;        // ingredients list search

    String query = new String();

    OnPageChangeListener mCallback;

    private ArrayList<Ingredient> ingredientArrayList;

    IngredientAdapter ingredientAdapter;

    public interface OnPageChangeListener {
        public void onPageChange(StringBuffer ingredientTitles, String query);
    }

    public static IngredientsFragment newInstance() {
        Log.d(TAG, "newInstance of IngredientsFragment");
        return new IngredientsFragment();
    }

    private ArrayList<String> readIngredientsSuggestionsFile(){
        ArrayList<String> ingredientsSuggestionsLocal = new ArrayList<String>();
        try {
            InputStream inputStream = getActivity().getResources().openRawResource(R.raw.ingredient_suggestions);
            if (inputStream != null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  /// read the input..

                String receiveString;

                /* Read the input using the bufferedReader (which is connected to the inputStream..) */
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    ingredientsSuggestionsLocal.add(receiveString);
                }

                inputStream.close();
                bufferedReader.close();

//                String file_contents = ingredientsSuggestionsLocal.toString();
//                Toast.makeText(getActivity(), "Ingredient Suggestions Parsed!  " + file_contents, Toast.LENGTH_SHORT).show();
            }
        } catch(Exception e){
            Log.d(TAG, "FILE EXCEPTION: " + e.getMessage());
        }

        return ingredientsSuggestionsLocal;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        sqLiteDAO = new SQLiteDAO(getActivity());
        sqLiteDAO.open();
        ingredientsSuggestionsArrayList = readIngredientsSuggestionsFile();
        setHasOptionsMenu(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        try {
            if (!isVisibleToUser) { // query is the keyword search query term passed in, not ingedients
                mCallback.onPageChange(ingredientAdapter.getIngredientTitles(), query);
                Log.d(TAG, "isVisibleToUser = false");
            } else {
                Log.d(TAG, "isVisibleToUser = true");
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "setUserVisibleHint-> " + e.toString());
        }
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
    public void onResume() {
        Log.d(TAG, "onResume");
        sqLiteDAO.open();
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        sqLiteDAO.close();
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.ingredient_fragment_actions, menu);

            // ((MainActivity)getActivity())._menu.
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final MenuItem settingsMenuItem = menu.findItem(R.id.action_settings);
        final MenuItem aboutAppMenuItem = menu.findItem(R.id.action_about);

        try{
            mSearchView = (SearchView) searchItem.getActionView();
            setupSearchView(mSearchView);

            if (mSearchView != null) {
                mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            try {
                                settingsMenuItem.setVisible(false);
                                aboutAppMenuItem.setVisible(false);
                            } catch (Exception e) {
                                Log.d(TAG, "Exception 1");
                            }
                        } else {
                            try {
                                settingsMenuItem.setVisible(true);
                                aboutAppMenuItem.setVisible(true);
                            } catch (Exception e) {
                                Log.d(TAG, "Exception 2");
                            }
                        }
                        //  getActivity().invalidateOptionsMenu();
                    }
                });
                mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    android.widget.Filter filter = ingredientAdapter.getFilter();

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        Log.d(TAG, newText);
                        filter.filter(newText);
                        return true;
                    }
                });
            }
        } catch (NullPointerException e){
            Log.d(TAG, e.getLocalizedMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_new_ingredient) {

            FragmentManager fragmentManager = getActivity().getFragmentManager();
            DialogAddIngredientFragment dialogAddIngredientFragment
                    = new DialogAddIngredientFragment(ingredientAdapter, sqLiteDAO, ingredientsSuggestionsArrayList);
            dialogAddIngredientFragment.show(fragmentManager, "add ingredient dialog");
            return true;

        } else if (id == R.id.action_remove_ingredient) {
            ListView listView = getListView();
            int listViewItemCount = listView.getChildCount();

            if (listViewItemCount <= 0) {
                Toast.makeText(getActivity(), "No ingredients selected..", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                final ArrayList<Ingredient> ingredientsToDelete = new ArrayList<Ingredient>();
                CheckBox checkbox;
                for (int i = 0; i < listViewItemCount; i++){
                    try{
                        View v = listView.getAdapter().getView(i, null, null);
                        checkbox = (CheckBox) v.findViewById(R.id.ingredientCheckbox);
                        if (checkbox.isChecked()){
                            ingredientsToDelete.add(ingredientAdapter.getItem(i));
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "error!!->" + e.getMessage());
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setMessage("Remove selected ingredient(s)?");
                builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < ingredientsToDelete.size(); i++){
                            sqLiteDAO.deleteIngredient(ingredientsToDelete.get(i));

                            // Remove from listview -- doesn't handle the case of searching for
                            // an ingredient and deleting it. It still appears in the original
                            // list. But upon closing the app and re-opening, it's removed. So
                            // we need to update the listview when that happens.
                            ingredientAdapter.remove(ingredientsToDelete.get(i));
                        }
                        ingredientAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                return true;
            }
        } else if (id == R.id.action_search) {
            Log.d(TAG, "About to search");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);

        try {
            clearQueryButton = (Button) rootView.findViewById(R.id.clearQueryButton);
//            keywordSearchView = (SearchView) rootView.findViewById(R.id.keywordSearchView);
//            keywordSearchView.setIconifiedByDefault(false);
            keywordEditText = (EditText) rootView.findViewById(R.id.keywordEditText);

//            keywordSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                @Override
//                public boolean onQueryTextSubmit(String query) {
//                    return false;
//                }
//
//                @Override
//                public boolean onQueryTextChange(String newText) {
//                    query = newText;
//                    Log.d(TAG, "addTextChangedListener-> query: " + query);
//                    return true;
//                }
//            });

//            keywordSearchView.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    query = query.trim().replace(" ", "+");
//                }
//            });

            keywordEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        query = keywordEditText.getText().toString();
                        Log.d(TAG, "addTextChangedListener-> query: " + query + " char seq: " + s.toString());
                    } catch (NullPointerException e) {
                        Log.d(TAG, e.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    query = query.trim().replace(" ", "+");
                }
            });

            clearQueryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "clear query button clicked!");
                    try {
                        if (keywordEditText.getText().length() > 0) {
                            keywordEditText.setText("");
                        }
                    } catch (NullPointerException e) {
                        Log.d(TAG, "clearQueryButton: " + e.toString());
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

        Log.d(TAG, "onActivityCreated");
        ingredientArrayList = sqLiteDAO.getAllIngredients();

        // https://developer.android.com/guide/topics/ui/menus.html#context-menu
        final ListView listView = getListView();
        ingredientAdapter = new IngredientAdapter(getActivity(),
                android.R.layout.simple_list_item_multiple_choice, ingredientArrayList);
        listView.setAdapter(ingredientAdapter);
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "LIST CLICK");
                ingredientAdapter.itemClickListener(view, position);
            }
        });

        /* EDIT INGREDIENT FEATURE */
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "LONG CLICK");

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                final Ingredient ingredient = (Ingredient) listView.getItemAtPosition(position);

                final String oldIngredientTitle;
                oldIngredientTitle = ingredient.getIngredientTitle();

                final int pos = position;

                final View v = view;
                View  editIngredientContentView = View.inflate(getActivity(), R.layout.edit_ingredient_contentview, null);
                final EditText ingredientTitleInputEditText = (EditText) editIngredientContentView.findViewById(R.id.editIngredientEditText);

                ingredientTitleInputEditText.setText(ingredient.getIngredientTitle());
                ingredientTitleInputEditText.setSelection(ingredientTitleInputEditText.getText().length());


                final CheckBox checkBoxExcludeIngredient = (CheckBox) editIngredientContentView.findViewById(R.id.checkboxExcludeIngredient);
                final CheckBox checkboxRequiredIngredient = (CheckBox) editIngredientContentView.findViewById(R.id.checkboxRequiredIngredient);

                switch (ingredient.getSelectedState()){
                    case EXCLUDE_STATE:
                        checkBoxExcludeIngredient.setChecked(true);
                        break;
                    case REQUIRED_STATE:
                        checkboxRequiredIngredient.setChecked(true);
                        break;
                }

                    /* onCheckChanged vs onClickListener... */
                checkBoxExcludeIngredient.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( checkBoxExcludeIngredient.isChecked() ) {
                            checkboxRequiredIngredient.setChecked(false);
                            ingredient.setSelectedState(Ingredient.SelectedStateType.EXCLUDE_STATE);
                        } else {
                            ingredient.setSelectedState(Ingredient.SelectedStateType.NORMAL_STATE);
                        }
                    }
                });
                checkBoxExcludeIngredient.setText(" Exclude  ");

                checkboxRequiredIngredient.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( checkboxRequiredIngredient.isChecked() ) {
                            checkBoxExcludeIngredient.setChecked(false);
                            ingredient.setSelectedState(Ingredient.SelectedStateType.REQUIRED_STATE);
                        } else {
                            ingredient.setSelectedState(Ingredient.SelectedStateType.NORMAL_STATE);
                        }
                    }
                });
                checkboxRequiredIngredient.setText(" Require ");

                builder.setTitle("Edit Ingredient");
                builder.setView(editIngredientContentView);

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        boolean prevSelectedState; // false
                        CheckBox checkbox = (CheckBox) v.findViewById(R.id.ingredientCheckbox);
                        prevSelectedState = checkbox.isChecked();
                        checkbox.setChecked(false);
                        ingredientAdapter.changeIngredientSelectedState(v, pos); // not type of ingredient, just ...

                        sqLiteDAO.updateIngredientTitle(oldIngredientTitle, ingredientTitleInputEditText.getText().toString());
                        ingredientAdapter.getItem(pos).setIngredientTitle(ingredientTitleInputEditText.getText().toString());
                        ingredientAdapter.notifyDataSetChanged();

                        if (!prevSelectedState){
                            checkbox.toggle();
                        }

                        ingredientAdapter.itemClickListener(v, pos);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;
            }
        });
    }

    private void setupSearchView(SearchView searchView){
        searchView.setQueryHint("Find ingredient..");
    }
}
