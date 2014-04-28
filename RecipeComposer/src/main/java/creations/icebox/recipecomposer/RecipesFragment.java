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

import creations.icebox.recipecomposer.adapter.RecipeAdapter;
import creations.icebox.recipecomposer.lib.Recipe;

public class RecipesFragment extends ListFragment {

    private static final String TAG = "***RECIPES FRAGMENT: ";

    ListView listView;
    RecipeAdapter recipeAdapter;
    StringBuffer ingredientTitles;
    StringBuffer ingredientTitlesOld = new StringBuffer();
    String queryOld = new String();
    String query;
    private int preLast;
    int currentPageGlobal = 1;
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

    public RecipesFragment() {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            ingredientTitles = ((MainActivity) getActivity()).getIngredientTitles();
            query = ((MainActivity) getActivity()).getQuery();
            Log.d(TAG, "setUserVisibleHint-> ingredientTitles: " + ingredientTitles + " | query: " + query);

            if (!ingredientTitles.toString().isEmpty() || query != null) {
                RecipeDownloaderAsyncTask recipeDownloaderAsyncTask = new RecipeDownloaderAsyncTask(this, ingredientTitles, query, currentPageGlobal);
//                this.recipeDownloaderAsyncTaskWeakReference
//                    = new WeakReference<RecipeDownloaderAsyncTask>(recipeDownloaderAsyncTask);
                recipeDownloaderAsyncTask.execute();
            } else {
                Log.d(TAG, "ELSE");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_recipes, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");

        listView = getListView();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int page = 1;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            // https://stackoverflow.com/questions/5123675/find-out-if-listview-is-scrolled-to-the-bottom
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                switch(view.getId()) {
                case android.R.id.list:
                    final int lastItem = firstVisibleItem + visibleItemCount;
                    if(lastItem == totalItemCount) {
                        if(preLast!=lastItem){ //to avoid multiple calls for last item
                            Log.d(TAG, "BOTTOM");
                            preLast = lastItem;

                            // Temporary fix to call the next subsequent page the first time.
                            // currentPageGlobal is 0 here even when initialized to some other value
                            // outside of this class.
                            page += 1;
                            currentPageGlobal += 1;

                            new RecipeDownloaderAsyncTask((RecipesFragment) getTargetFragment(),
                                    ingredientTitles, query, page).execute();
                        }
                    }
                }
            }
        });
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
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo itemInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.actionShareRecipe:
                try {
                    Log.d(TAG, "share recipe: " + recipeAdapter.getItem(itemInfo.position).getRecipeTitle());
                    shareTextRecipe(itemInfo);
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

        Recipe recipe = recipeAdapter.getItem(itemInfo.position);

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

//        Recipe recipe = recipeAdapter.getItem(itemInfo.position);
//
//        String recipeInfo = recipe.getRecipeTitle() + "\nMain ingredients: "
//                + recipe.getRecipeIngredients() + "\n" + recipe.getRecipeURL();
//
//        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "[Recipe Composer] Check out this recipe!");
//        shareIntent.putExtra(Intent.EXTRA_TEXT, recipeInfo);
//
//        startActivity(Intent.createChooser(shareIntent, "Share recipe info to..."));
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

    /*
    * Use AsyncTask if you need to perform background tasks, but also need
    * to change components on the GUI. Put the background operations in
    * doInBackground. Put the GUI manipulation code in onPostExecute
    * */
    private class RecipeDownloaderAsyncTask extends AsyncTask<String, Integer, String> {

        private String TAG = "***RECIPE DOWNLOADER***: ";

        private String recipeTitle = "";
        private String recipePuppyURL = "http://www.recipepuppy.com/api/?i=";
        private String recipeURL = "";
        private String recipeIngredients = "";
        private String recipePicUrl = "";

        private ProgressDialog progressDialog;

        private int lastStatusCode = 200;

        public int getLastStatusCode() {
            return lastStatusCode;
        }

        WeakReference<RecipesFragment> recipesFragmentWeakReference;

        private RecipeDownloaderAsyncTask (RecipesFragment recipesFragment, StringBuffer ingredientTitles, String query, int currentPage) {

            query = query.replace(" ", "+");

            String currPage;
            if (currentPage != 0) {
                currPage = "&p=" + currentPage;
            } else {
                currPage = "";
            }

            if (ingredientTitles == null) {
                recipePuppyURL = recipePuppyURL + "&q=" + query + currPage;
            } else if (query == null) {
                recipePuppyURL = recipePuppyURL + ingredientTitles + currPage;
            } else {
                recipePuppyURL = recipePuppyURL + ingredientTitles + "&q=" + query + currPage;
            }
            Log.d(TAG, "URL now = " + recipePuppyURL);

//            this.recipesFragmentWeakReference
//                    = new WeakReference<RecipesFragment>(recipesFragment);
        }

        @Override
        protected String doInBackground(String... params) {

            /*
            * if the url is different, we know we should clear the recipeList and call the api
            * else don't call api (cache previous result)
            *   watch for scroll to bottom
            * */

            //http://www.recipepuppy.com/api/?i=onions,garlic&q=omelet&p=3
            /*
                if i and q are the same
                    if p is the same
                        nothing
                    else p is different
                        request
                        append
                else i or q are different than previous vals
                    clear
                    request
                    append
            */


            if (ingredientTitlesOld.toString().equals(ingredientTitles.toString()) && queryOld.equals(query)) {
                Log.d(TAG, "page old = " + currentPageOld);
                Log.d(TAG, "page new = " + currentPageGlobal);
                if (currentPageOld == currentPageGlobal) {
                    Log.d(TAG, "not going to call API");
                    return "";
                } else {
                    Log.d(TAG, "append to recipe list");
                }
            } else {
                Log.d(TAG, "clear recipe list");
                currentPageGlobal = 0;
                recipeList.clear();
            }

            ingredientTitlesOld = new StringBuffer(ingredientTitles);
            if (query == null)
                queryOld = "";
            else queryOld = new String(query);
            currentPageOld = currentPageGlobal;

            DefaultHttpClient defaultHttpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost httpPost = new HttpPost(recipePuppyURL);
            httpPost.setHeader("Content-type", "application/json");
            InputStream inputStream = null;
            String queryResult = null;

            try {
                HttpResponse httpResponse = defaultHttpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();

                Log.d(TAG, "RESULT STATUS: " + httpResponse.getStatusLine().getStatusCode());
                lastStatusCode = httpResponse.getStatusLine().getStatusCode();
                if (lastStatusCode != 200) {
                    return "";
                }
                // BufferedReader reads data from the InputStream until the Buffer is full
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);

                // Will store the data
                StringBuilder stringBuilder = new StringBuilder();

                String line;

                // Read in the data from the Buffer until nothing is left
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + '\n');
                }

                queryResult = stringBuilder.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            finally {
                // Close the InputStream
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Holds Key / Value pairs from a JSON source
            JSONObject jsonObject;

            if(queryResult == null || queryResult.isEmpty()){
                return "";
            }

            try {
                Log.v("JSONParser RESULT: ", queryResult);

                // Get the root JSONObject
                jsonObject = new JSONObject(queryResult);
//                Log.d(TAG, jsonObject.toString());

                JSONArray jsonArray = jsonObject.getJSONArray("results");
//                Log.d(TAG, jsonArray.toString());

                for (int i = 0; i < jsonArray.length(); ++i)
                {
                    try {
                        JSONObject recipe = jsonArray.getJSONObject(i);

                        Recipe new_recipe = new Recipe();
                        recipeTitle         = recipe.getString("title").trim().replace("&amp;", "&").replaceAll("&.*;", "");
                        recipeURL           = recipe.getString("href").trim();
                        recipeIngredients   = recipe.getString("ingredients").trim();
                        recipePicUrl        = recipe.getString("thumbnail").trim();

                        new_recipe.setRecipeTitle(recipeTitle);
                        new_recipe.setRecipeURL(recipeURL);
                        new_recipe.setRecipeIngredients(recipeIngredients);
                        new_recipe.setRecipePicUrl(recipePicUrl);

                        recipeList.add(new_recipe);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (JSONException je) {
                je.printStackTrace();
            }

            return queryResult;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String response) {
            progressDialog.dismiss();

            super.onPostExecute(response);

//                if (this.recipesFragmentWeakReference.get() != null) {
//                }
                Log.d(TAG, "Now treat the result");

                if(response.equals("")){
                    return;
                }

                listView = getListView();
                registerForContextMenu(listView);
                if (listView.getAdapter() == null) {
                    Log.d(TAG, "onPostExecute-> Adapter is null");
                    recipeAdapter = new RecipeAdapter(getActivity(), android.R.layout.simple_selectable_list_item, recipeList);
                    listView.setAdapter(recipeAdapter);
                } else {
                    Log.d(TAG, "onPostExecute-> Adapter is already created");
                    if (lastStatusCode == 200) {
                        recipeAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "status = " + lastStatusCode + " so don't do anything");
                    }
                }

        }
    }
}
