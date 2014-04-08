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

import creations.icebox.recipecomposer.adapter.IngredientAdapter;
import creations.icebox.recipecomposer.helper.SQLiteDAO;
import creations.icebox.recipecomposer.lib.Ingredient;

public class DialogAddIngredientFragment extends DialogFragment {
    private static final String TAG = "***DIALOG FRAGMENT***: ";

    IngredientAdapter ingredientAdapter;
    SQLiteDAO sqLiteDAO;
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> stringArrayAdapter;

    static final String[] INGREDIENTS = new String[] {
            "basil", "cream", "berries"
    };

    public DialogAddIngredientFragment(IngredientAdapter ingredientAdapter, SQLiteDAO sqLiteDAO) {
        this.ingredientAdapter = ingredientAdapter;
        this.sqLiteDAO = sqLiteDAO;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.fragment_dialog_add_ingredient, null);
        builder.setView(rootView);

        autoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.addIngredientAutoCompleteTextView);
        autoCompleteTextView.setThreshold(1);
        stringArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, INGREDIENTS);
        autoCompleteTextView.setAdapter(stringArrayAdapter);

        builder.setTitle("New Ingredient");

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String ingredientTitle = "";
                    if (autoCompleteTextView.getText() != null
                            && autoCompleteTextView.getText().length() > 0) {
                        ingredientTitle = autoCompleteTextView.getText().toString();
                        Ingredient ingredient = sqLiteDAO.createIngredient(ingredientTitle);

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
