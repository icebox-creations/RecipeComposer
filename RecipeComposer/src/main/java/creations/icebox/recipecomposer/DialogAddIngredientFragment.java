package creations.icebox.recipecomposer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;

import creations.icebox.recipecomposer.adapter.IngredientAdapter;
import creations.icebox.recipecomposer.helper.SQLiteDAO;
import creations.icebox.recipecomposer.lib.Ingredient;

public class DialogAddIngredientFragment extends DialogFragment {

    private static final String TAG = "***DIALOG FRAGMENT***: ";
    private IngredientAdapter ingredientAdapter;
    private SQLiteDAO sqLiteDAO;
    private ArrayList<String> ingredientSuggestions;
    private AutoCompleteTextView autoCompleteTextView;

    public DialogAddIngredientFragment(IngredientAdapter ingredientAdapter, SQLiteDAO sqLiteDAO,
                                       ArrayList<String> ingredientSuggestions) {
        this.ingredientAdapter = ingredientAdapter;
        this.sqLiteDAO = sqLiteDAO;
        this.ingredientSuggestions = ingredientSuggestions;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.fragment_dialog_add_ingredient, null);

        autoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.addIngredientAutoCompleteTextView);
        autoCompleteTextView.setThreshold(1);
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ingredientSuggestions);
        autoCompleteTextView.setAdapter(stringArrayAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("New Ingredient");
        builder.setView(rootView);

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String ingredientTitle = "";
                    if (autoCompleteTextView.getText() != null
                            && autoCompleteTextView.getText().length() > 0) {
                        ingredientTitle = autoCompleteTextView.getText().toString();
                        Ingredient ingredient = sqLiteDAO.createIngredient(ingredientTitle.trim().toLowerCase());

                        try {
                            if (ingredient != null) {
                                ingredientAdapter.add(ingredient);
                                ingredientAdapter.notifyDataSetChanged();
                            }
                        } catch (NullPointerException e) {
                            Log.d(TAG, "error: " + e.toString());
                        }
                    }
                    Toast.makeText(getActivity().getBaseContext(), ingredientTitle + " added",
                            Toast.LENGTH_SHORT).show();
                }
            });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        return builder.create();
    }
}
