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
import creations.icebox.recipecomposer.lib.Recipe;

public class RecipeFavoritesFragment extends ListFragment {

    private static final String TAG = "***RECIPES FRAGMENT: ";

    ListView listView;
    RecipeFavoritesAdapter recipeAdapter;
    StringBuffer ingredientTitles;
    StringBuffer ingredientTitlesOld = new StringBuffer();
    String queryOld = new String();
    String query;
    private int preLast;
    int currentPageGlobal = 0;
    int currentPageOld = 0;

    /*
    A weak reference is used so that the fragment and the async task
    are loosely coupled. If a weak reference isn't used, the async
    task will not be garbage collected because the fragment maintains
    a reference to it.

    You should think about using one whenever you need a reference to
    an object, but you don't want that reference to protect the object
    from the garbage collector.
    */
//    WeakReference<RecipeDownloaderAsyncTask> recipeDownloaderAsyncTaskWeakReference;

    ArrayList<Recipe> recipeList = new ArrayList<Recipe>();

    public static RecipesFragment newInstance() {
        Log.d(TAG, "newInstance of RecipesFragment");
        return new RecipesFragment();
    }

    public RecipeFavoritesFragment() {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            ingredientTitles = ((MainActivity) getActivity()).getIngredientTitles();
//            query = ((MainActivity) getActivity()).getQuery();
//            Log.d(TAG, "setUserVisibleHint-> ingredientTitles: " + ingredientTitles + " | query: " + query);
//
//            if (!ingredientTitles.toString().isEmpty() || query != null) {
//                RecipeDownloaderAsyncTask recipeDownloaderAsyncTask = new RecipeDownloaderAsyncTask(this, ingredientTitles, query, currentPageGlobal);
//                this.recipeDownloaderAsyncTaskWeakReference
//                        = new WeakReference<RecipeDownloaderAsyncTask>(recipeDownloaderAsyncTask);
//                recipeDownloaderAsyncTask.execute();
//            } else {
//                Log.d(TAG, "ELSE");
//            }
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "Recipe Favs Fragment !! onCreateView");
        return inflater.inflate(R.layout.fragment_recipe_favorites, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "Recipe Favorites !! onViewCreated !!");
//
//        listView = getListView();
//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//            }
//
//            // https://stackoverflow.com/questions/5123675/find-out-if-listview-is-scrolled-to-the-bottom
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                switch(view.getId()) {
//                    case android.R.id.list:
//                        final int lastItem = firstVisibleItem + visibleItemCount;
//                        if(lastItem == totalItemCount) {
//                            if(preLast!=lastItem){ //to avoid multiple calls for last item
//                                Log.d(TAG, "BOTTOM");
//                                preLast = lastItem;
//
//                                currentPageGlobal += 1;
//
////                                new RecipeDownloaderAsyncTask((RecipesFragment) getTargetFragment(),
////                                        ingredientTitles, query, currentPageGlobal).execute();
//                            }
//                        }
//                }
//            }
//        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated...");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String recipeTitle = recipeList.get(position).getRecipeTitle();
        Log.d(TAG, recipeTitle + " clicked");
        Toast.makeText(getActivity(),
                "'" + recipeTitle + "..'",
                Toast.LENGTH_SHORT).show();

        Uri uriUrl = Uri.parse(recipeList.get(position).getRecipeURL());
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        ingredientTitles = new StringBuffer();

        /* Configure the fragment instance to be retained on configuration
        * change. Then start the async task */
        setRetainInstance(true);
        // when added, invalid optiosn menu, fragments on options menu..
        // manage own items.. no menu switching!
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.recipe_fragment_context_menu, menu);
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
