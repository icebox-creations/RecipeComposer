package creations.icebox.recipecomposer;

import android.app.Activity;
import android.app.AlertDialog;
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

import java.util.ArrayList;

import creations.icebox.recipecomposer.adapter.IngredientAdapter;
import creations.icebox.recipecomposer.helper.SQLiteDAO;
import creations.icebox.recipecomposer.lib.Ingredient;

public class IngredientsFragment extends ListFragment {
    private static final String TAG = "***INGREDIENTS FRAGMENT***: ";
    private SQLiteDAO sqLiteDAO;

    Button      clearQueryButton;
    EditText    keywordEditText;
    SearchView  mSearchView; // ingerdient search

    String query = new String();

    OnPageChangeListener mCallback;

    private ArrayList<Ingredient> ingredientArrayList;
    //private ArrayList<Ingredient> ingredientArrayList;

    IngredientAdapter ingredientAdapter;

    public interface OnPageChangeListener {
        public void onPageChange(StringBuffer ingredientTitles, String query);
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

        // when added, invalid optiosn menu, fragments on options menu..
        // manage own items.. no menu switching!
        setHasOptionsMenu(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        try {
            if (!isVisibleToUser) {
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

        MenuItem searchItem = menu.findItem(R.id.action_search);

        try{
            mSearchView = (SearchView) searchItem.getActionView();
            setupSearchView(mSearchView);

            if (mSearchView != null) {
                mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        Log.d(TAG, newText);
                        ListView lv = getListView();
                        if (newText.length() == 0){
                            lv.clearTextFilter();
                        } else {
                            lv.setFilterText(newText);
                        }
                        return false;
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("New Ingredient");

            // Set up the input
            final EditText ingredientTitleInputEditText = new EditText(getActivity());
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            builder.setView(ingredientTitleInputEditText);

            // Set up the buttons
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String ingredientTitle = "";
                    if (ingredientTitleInputEditText.getText() != null
                          && ingredientTitleInputEditText.getText().length() > 0)
                    {
                        ingredientTitle = ingredientTitleInputEditText.getText().toString();
                        Ingredient ingredient = sqLiteDAO.createIngredient(ingredientTitle);

                        try {
                            if (ingredient != null) {
                                ingredientAdapter.add(ingredient, mSearchView.getQuery().toString());
                                ingredientAdapter.notifyDataSetChanged();
                            }
                        } catch (NullPointerException e) {
                            Log.d(TAG, "add ing. button error: " + e.toString());
                        }
                    }
                    Toast.makeText(getActivity().getBaseContext(), "Add ingredient: "
                            + ingredientTitle, Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();

            Log.d(TAG, "About to add a new ingredient");
            return true;
        } else if (id == R.id.action_remove_ingredient) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

//            builder.setTitle("Remove Ingredient");
            builder.setMessage("Remove selected ingredient(s)?");
            builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CheckBox checkbox;
                    ListView listView = getListView();
                    int listViewItemCount = listView.getChildCount();
                    for (int i = 0; i < listViewItemCount; i++){
                        try{
                            View v = listView.getAdapter().getView(i, null, null);
                            checkbox = (CheckBox) v.findViewById(R.id.ingredientCheckbox);
                            if (checkbox.isChecked()){
                                Log.d(TAG, "delete... " + ingredientArrayList.get(i));

                                // Remove this ingredient from the db
                                sqLiteDAO.deleteIngredient(ingredientArrayList.get(i));

                                // Remove from listview -- doesn't handle the case of searching for
                                // an ingredient and deleting it. It still appears in the original
                                // list. But upon closing the app and re-opening, it's removed. So
                                // we need to update the listview when that happens.
                                ingredientArrayList.remove(i);
                                ingredientAdapter.notifyDataSetChanged();
                            }

                        } catch (Exception e) {
                            Log.d(TAG, "error!!->" + e.getMessage());
                        }
                    }
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
            keywordEditText = (EditText) rootView.findViewById(R.id.keywordEditText);

            keywordEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    query = keywordEditText.getText().toString();
                    Log.d(TAG, "addTextChangedListener-> query: " + query + " char seq: " + s.toString());
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
//        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
//
//        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
//            @Override
//            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
//                Log.d(TAG, "STAte CHANGED ");
//            }
//
//            @Override
//            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//                MenuInflater menuInflater = mode.getMenuInflater();
//                menuInflater.inflate(R.menu.ingredient_cab, menu);
//
//                CheckBox checkbox;
//                int listViewItemCount = listView.getChildCount();
//                for (int i = 0; i < listViewItemCount; i++){
//                    try{
//                        View v = listView.getAdapter().getView(i, null, null);
//                        checkbox = (CheckBox) v.findViewById(R.id.ingredientCheckbox);
//                        Log.d(TAG, " " + i + checkbox.getTag().toString());
//                        if (checkbox.isChecked()){
////                            checkbox.setButtonDrawable(R.drawable.btn_check_on_pressed_red);
////                            checkbox.refreshDrawableState();
//                        }
//                    } catch (Exception e) {
//                        Log.d(TAG, "DUn BROKE CHECKBOX DrawaBLE SWItcHArOO: " + e.getMessage());
//                    }
//
//                }
//                Log.d(TAG, "CREATING CONTEXT MENU");
//                return true;
//            }
//
//            @Override
//            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                return false;
//            }
//
//            @Override
//            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//
//                switch (item.getItemId()) {
//                    case R.id.action_remove_ingredient:
//                        Toast.makeText(getActivity(), "Remove clicked", Toast.LENGTH_SHORT).show();
//                        mode.finish();
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//
//            @Override
//            public void onDestroyActionMode(ActionMode mode) {
//                CheckBox checkbox;
//                int listViewItemCount = listView.getChildCount();
//                Log.d(TAG, " " + listViewItemCount);
//                for (int i = 0; i < listViewItemCount; i++){
//                    try{
//                        View v = listView.getChildAt(i);
//                        checkbox = (CheckBox) v.findViewById(R.id.ingredientCheckbox);
//                        if (checkbox.isChecked()){
////                            checkbox.setButtonDrawable(R.drawable.btn_check_on_pressed_holo_dark);
//                        }
//                    } catch (Exception e) {
//                        Log.d(TAG, e.getMessage());
//                    }
//
//                }
//                Log.d(TAG, "EXITED CONTEXT MENU");
//            }
//        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "LIST CLICK");
                ingredientAdapter.itemClickListener(view, position);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "LONG CLICK");

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                final Ingredient ingredient = (Ingredient) listView.getItemAtPosition(position);

                final String oldIngredientTitle;
                oldIngredientTitle = ingredient.getIngredientTitle();

                final EditText ingredientTitleInputEditText = new EditText(getActivity());
                ingredientTitleInputEditText.setText(ingredient.getIngredientTitle());
                ingredientTitleInputEditText.setSelection(ingredientTitleInputEditText.getText().length());

                final int pos = position;

                builder.setView(ingredientTitleInputEditText);

                builder.setTitle("Edit Ingredient");
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        sqLiteDAO.updateIngredientTitle(oldIngredientTitle, ingredientTitleInputEditText.getText().toString());

                        ingredientAdapter.getItem(pos).setIngredientTitle(ingredientTitleInputEditText.getText().toString());
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
        });
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        MenuInflater menuInflater = getActivity().getMenuInflater();
//        menuInflater.inflate(R.menu.ingredient_cab, menu);
//    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info =
//                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//
//        switch (item.getItemId()) {
//            case R.id.action_remove_ingredient:
//                // Remove this ingredient from the db
//                sqLiteDAO.deleteIngredient(ingredientArrayList.get(info.position));
//
//                // Remove from listview
//                ingredientArrayList.remove(info.position);
//                ((IngredientAdapter)getListAdapter()).notifyDataSetChanged();
//                return true;
//        }
//        return false;
//    }

    private void setupSearchView(SearchView searchView){
        searchView.setQueryHint("Find ingredient..");
    }
}
