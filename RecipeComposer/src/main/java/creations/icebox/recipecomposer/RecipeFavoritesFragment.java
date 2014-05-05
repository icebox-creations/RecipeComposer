package creations.icebox.recipecomposer;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import creations.icebox.recipecomposer.adapter.RecipeFavoritesAdapter;
import creations.icebox.recipecomposer.helper.SQLiteDAO;
import creations.icebox.recipecomposer.lib.Recipe;

public class RecipeFavoritesFragment extends ListFragment {

    private static final String TAG = "***RECIPE FAVORITES FRAGMENT: ";

    private ListView listView;
    private RecipeFavoritesAdapter recipeFavoritesAdapter;
    private SQLiteDAO sqLiteDAO;
    private ArrayList<Recipe> recipeFavoritesList = new ArrayList<Recipe>();

    public static RecipeFavoritesFragment newInstance() {
        Log.d(TAG, "newInstance of RecipesFragment");
        return new RecipeFavoritesFragment();
    }

    public RecipeFavoritesFragment() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        try{
            if (isVisibleToUser) {
                Log.d(TAG, "isVisible");
                recipeFavoritesAdapter.clear();
                for (Recipe recipe : recipeFavoritesList = sqLiteDAO.getAllRecipeFavorites()) {
                    recipeFavoritesAdapter.add(recipe);
                }
                recipeFavoritesAdapter.notifyDataSetChanged();
//                listView = getListView();
//                registerForContextMenu(listView);
            } else {
                Log.d(TAG, "is not visible");
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "setUserVisibleHint-> " + e.toString());
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recipe_favorites_fragment_actions, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_recipe_favorites, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated...");

        recipeFavoritesList = sqLiteDAO.getAllRecipeFavorites();

        for (int i = 0; i < recipeFavoritesList.size(); ++i) {
            Log.d(TAG, recipeFavoritesList.get(i).toString());
        }

        final ListView listView = getListView();
        recipeFavoritesAdapter = new RecipeFavoritesAdapter(getActivity(),
                android.R.layout.simple_list_item_multiple_choice, recipeFavoritesList);
        registerForContextMenu(listView);
        listView.setAdapter(recipeFavoritesAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String recipeTitle = recipeFavoritesList.get(position).getRecipeTitle();
        Log.d(TAG, recipeTitle + " clicked");

        /* Open recipe's URL in browser */
        Uri uriUrl = Uri.parse(recipeFavoritesList.get(position).getRecipeURL());
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        sqLiteDAO = new SQLiteDAO(getActivity());
        sqLiteDAO.open();

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }
//
    /* created when we long hold a specific item in the recipe list */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.recipe_favorites_fragment_context_menu, menu);
    }

    /* When an item is selected in the context menu */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo itemInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

//        Log.d(TAG, " Recipe favorite fragment ..");
        if (getUserVisibleHint() == false)
            return false;

        Recipe recipeFavorite = recipeFavoritesAdapter.getItem(itemInfo.position);
        switch (item.getItemId()) {
            case R.id.actionShareRecipeFavorite:
                try {
                    Log.d(TAG, "share recipe: " + recipeFavorite.getRecipeTitle());
                    shareTextRecipe(itemInfo);
                } catch (NullPointerException e) {
                    Log.d(TAG, e.toString());
                }
                return true;
            case R.id.actionRemoveFavoriteRecipeFavorite:
                try {
                    if (recipeFavorite == null) {
                        Toast.makeText(getActivity(), "Tried to delete a non existant recipe, silly! ",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        recipeFavoritesAdapter.remove(recipeFavorite);
                        sqLiteDAO.deleteRecipeFavorite(recipeFavorite);
                        recipeFavoritesAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(),
                                "Removed '" + recipeFavorite.getRecipeTitle() + "' from your favorites!  ",
                                Toast.LENGTH_SHORT).show();

                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, e.toString());
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void shareTextRecipe(AdapterView.AdapterContextMenuInfo itemInfo) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        Recipe recipe = recipeFavoritesAdapter.getItem(itemInfo.position);

        String textMessage = recipe.getRecipeTitle() + "\nMain ingredients: "
                + recipe.getRecipeIngredients() + "\n" + recipe.getRecipeURL();
        String subjectMessage = "Hey, let's make " + recipe.getRecipeTitle() + "!";

        try {
            List<ResolveInfo> resolveInfoList = getActivity().getPackageManager()
                    .queryIntentActivities(shareIntent, 0);

            if (!resolveInfoList.isEmpty()) {
                List<Intent> targetedShareIntents = new ArrayList<Intent>();
                Intent targetedShareIntent;

                for (ResolveInfo resolveInfo : resolveInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;

                    targetedShareIntent = new Intent(Intent.ACTION_SEND);
                    targetedShareIntent.setType("text/plain");
                    targetedShareIntent.putExtra(Intent.EXTRA_SUBJECT, subjectMessage);
                    targetedShareIntent.putExtra(Intent.EXTRA_TEXT, textMessage);
                    targetedShareIntent.setPackage(packageName);

                    targetedShareIntents.add(targetedShareIntent);
                }

                Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0),
                        getResources().getString(R.string.share_intent));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                        targetedShareIntents.toArray(new Parcelable[] {}));
                startActivityForResult(chooserIntent, 0);
            }

        } catch (NullPointerException e) {
            Log.d(TAG, e.toString());
        }


    }


    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }
}
