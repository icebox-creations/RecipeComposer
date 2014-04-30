package creations.icebox.recipecomposer;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
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

                recipeFavoritesAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "is not visible");
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "setUserVisibleHint-> " + e.toString());
        }
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

        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.recipe_fragment_context_menu, menu);
    }
}
