package creations.icebox.recipecomposer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import creations.icebox.recipecomposer.adapter.IngredientAdapter;
import creations.icebox.recipecomposer.helper.SQLiteDAO;

public class IngredientsFragment extends ListFragment {
    private static final String TAG = "***INGREDIENTS FRAGMENT***: ";
    private SQLiteDAO sqLiteDAO;
    private View rootView;

    Button addIngredientButton;
    EditText ingredientEditText;

    Button clearQueryButton;
    EditText queryEditText;

    String query = new String();

    OnPageChangeListener mCallback;

    private ArrayList<Ingredient> ingredientArrayListChecked;
    private ArrayList<Ingredient> ingredientArrayList;

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

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        MenuInflater menuInflater = getActivity().getMenuInflater();
//        menuInflater.inflate(R.menu.ingredient_cab, menu);
//    }
//
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.ingredient_fragment_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
                                ingredientAdapter.add(ingredient);
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
        } else if (id == R.id.action_search) {
            Log.d(TAG, "About to search");
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            addIngredientButton = (Button) rootView.findViewById(R.id.addIngredientButton);
            ingredientEditText = (EditText) rootView.findViewById(R.id.ingredientEditText);

            clearQueryButton = (Button) rootView.findViewById(R.id.clearQueryButton);
            queryEditText = (EditText) rootView.findViewById(R.id.queryEditText);

            addIngredientButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "add ingredient button clicked!");

                    if (ingredientEditText.getText().length() > 0) {
                        String ingredientTitle = ingredientEditText.getText().toString();
                        Ingredient ingredient = sqLiteDAO.createIngredient(ingredientTitle);
                        try {
                            if (ingredient != null) {
                                ingredientAdapter.add(ingredient);
                                ingredientAdapter.notifyDataSetChanged();
                            }
                        } catch (NullPointerException e) {
                            Log.d(TAG, "add ing. button error: " + e.toString());
                        }
                    } else {
                        Toast.makeText(getActivity(),
                                "Sorry, you must supply an ingredient",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            queryEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    query = queryEditText.getText().toString();
                    Log.d(TAG, "addTextChangedListener-> query: " + query);
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
                        if (queryEditText.getText().length() > 0) {
                            queryEditText.setText("");
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
        ListView listView = getListView();
        ingredientAdapter = new IngredientAdapter(getActivity(),
                android.R.layout.simple_list_item_multiple_choice, ingredientArrayList);
        listView.setAdapter(ingredientAdapter);

//        setListAdapter(new IngredientAdapter(getActivity(),
//                android.R.layout.simple_list_item_multiple_choice, ingredientArrayList));

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.ingredient_cab, menu);
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

    }
}

