package creations.icebox.recipecomposer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import creations.icebox.recipecomposer.adapter.RecipeAdapter;

public class RecipesFragment extends ListFragment {

    private static final String TAG = "***RECIPES FRAGMENT: ";

    /*
    A weak reference is used so that the fragment and the async task
    are loosely coupled. If a weak reference isn't used, the async
    task will not be garbage collected because the fragment maintains
    a reference to it.

    You should think about using one whenever you need a reference to
    an object, but you don't want that reference to protect the object
    from the garbage collector.
    */
    private WeakReference<RecipeDownloaderAsyncTask> recipeDownloaderAsyncTaskWeakReference;

    ArrayList<Recipe> recipeList = new ArrayList<Recipe>();

    public static RecipesFragment newInstance() {
        Log.d(TAG, "newInstance of RecipesFragment");
        return new RecipesFragment();
    }

    public RecipesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_recipes, container, false);

        RecipeDownloaderAsyncTask recipeDownloaderAsyncTask = new RecipeDownloaderAsyncTask(this);
        this.recipeDownloaderAsyncTaskWeakReference
                = new WeakReference<RecipeDownloaderAsyncTask>(recipeDownloaderAsyncTask);

        recipeDownloaderAsyncTask.execute();
        Log.d(TAG, "execute 1st");

        return rootView;
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.d(TAG, recipeList.get(position).getRecipeTitle() + " clicked");
        Toast.makeText(getActivity(),
                "Recipe #" + position + " clicked",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        /* Configure the fragment instance to be retained on configuration
        * change. Then start the async task */
        setRetainInstance(true);
     }

    /* Use this to test if the async task is running or not */
    private boolean isAsyncTaskPendingOrRunning() {
        try {
            return this.recipeDownloaderAsyncTaskWeakReference != null
                    && this.recipeDownloaderAsyncTaskWeakReference.get() != null
                    && !this.recipeDownloaderAsyncTaskWeakReference.get()
                        .getStatus().equals(AsyncTask.Status.FINISHED);
        } catch (NullPointerException e) {
            Log.d(TAG, "Error in isAsyncTaskPendingOrRunning: " + e.toString());
            return false;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
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
    private class RecipeDownloaderAsyncTask extends AsyncTask<String, String, String> {

        private String recipePuppyURL = "http://www.recipepuppy.com/api/?i=onions,garlic&q=omelet&p=3";
        private String TAG = "***RECIPE DOWNLOADER***: ";

        private String recipeTitle = "";
        private String recipeURL = "";
        private String recipeIngredients = "";

        private WeakReference<RecipesFragment> recipesFragmentWeakReference;

        private RecipeDownloaderAsyncTask (RecipesFragment recipesFragment) {
            this.recipesFragmentWeakReference
                    = new WeakReference<RecipesFragment>(recipesFragment);
        }

        @Override
        protected String doInBackground(String... strings) {

            DefaultHttpClient defaultHttpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost httpPost = new HttpPost(recipePuppyURL);
            httpPost.setHeader("Content-type", "application/json");
            InputStream inputStream = null;
            String queryResult = null;

            try {
                HttpResponse httpResponse = defaultHttpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();

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

            try {
                Log.v("JSONParser RESULT: ", queryResult);

                // Get the root JSONObject
                jsonObject = new JSONObject(queryResult);
                Log.d(TAG, jsonObject.toString());

                JSONArray jsonArray = jsonObject.getJSONArray("results");
                Log.d(TAG, jsonArray.toString());

                for (int i = 0; i < jsonArray.length(); ++i)
                {
                    try {
                        JSONObject recipe = jsonArray.getJSONObject(i);

                        // Pull items from the array
                        recipeTitle = recipe.getString("title").replace("\n","");
                        recipeURL = recipe.getString("href");
                        recipeIngredients = recipe.getString("ingredients").replace("\n", "");

                        recipeList.add(new Recipe(recipeTitle, recipeURL, recipeIngredients));

//                        Log.d("Title: ", recipeTitle);
//                        Log.d("URL: ", recipeURL);
//                        Log.d("Ingredients List: ", recipeIngredients);

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
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.d(TAG, "onPostExecute");

//            Log.d(TAG, "Title: " + recipeList.get(0).getRecipeTitle());
//            Log.d(TAG, "URL: " + recipeList.get(0).getRecipeURL());
//            Log.d(TAG, "Ingredients: " + recipeList.get(0).getRecipeIngredients());

            if (this.recipesFragmentWeakReference.get() != null) {
                Log.d(TAG, "Now treat the result");

                // if the list view doesnt show up, confirm th context of the list view here:
                ListView myList= getListView();
                myList.setAdapter(new RecipeAdapter(getActivity(), android.R.layout.simple_selectable_list_item,recipeList));

//                setListAdapter(new RecipeAdapter(getActivity(), android.R.layout.simple_selectable_list_item, recipeList));
            }

        }
    }
}
