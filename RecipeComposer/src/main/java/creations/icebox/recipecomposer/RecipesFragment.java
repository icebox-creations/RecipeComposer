package creations.icebox.recipecomposer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class RecipesFragment extends Fragment {

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

    public static RecipesFragment newInstance() {
        Log.d(TAG, "newInstance");
        return new RecipesFragment();
    }

    public RecipesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_recipes, container, false);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        /* Configure the fragment instance to be retained on configuration
        * change. Then start the async task */
        setRetainInstance(true);

        // Call for doInBackground() in RecipeDownloader to be executed
        RecipeDownloaderAsyncTask recipeDownloaderAsyncTask = new RecipeDownloaderAsyncTask(this);
        this.recipeDownloaderAsyncTaskWeakReference
                = new WeakReference<RecipeDownloaderAsyncTask>(recipeDownloaderAsyncTask);
        recipeDownloaderAsyncTask.execute();
     }

    /* Test if the async task is running or not */
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

        // JSON RESTful API test
        private String recipePuppyURL = "http://www.recipepuppy.com/api/?i=onions,garlic&q=omelet&p=3";
        private String TAG = "***RECIPE DOWNLOADER***: ";

        private String recipeTitle = "";
        private String recipeURL = "";
        private String recipeIngredients = "";

        ArrayList<Recipe> recipeList = new ArrayList<Recipe>();

        private WeakReference<RecipesFragment> recipesFragmentWeakReference;

        private RecipeDownloaderAsyncTask (RecipesFragment recipesFragment) {
            this.recipesFragmentWeakReference
                    = new WeakReference<RecipesFragment>(recipesFragment);
        }

        @Override
        protected String doInBackground(String... strings) {

            // HTTP Client that supports streaming uploads and downloads
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient(new BasicHttpParams());

            // Define that I want to use the POST method to grab data from the provided URL
            HttpPost httpPost = new HttpPost(recipePuppyURL);

            // Web service used is defined
            httpPost.setHeader("Content-type", "application/json");

            // Used to read data from the URL
            InputStream inputStream = null;

            // Will hold all the data from the URL
            String queryResult = null;

            try {
                // Get a response if any from the web service
                HttpResponse httpResponse = defaultHttpClient.execute(httpPost);

                // The content from the requested URL along with headers, etc.
                HttpEntity httpEntity = httpResponse.getEntity();

                // Get the main content from the URL
                inputStream = httpEntity.getContent();

                // JSON is UTF-8 by default
                // BufferedReader reads data from the InputStream until the Buffer is full
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);

                // Will store the data
                StringBuilder stringBuilder = new StringBuilder();

                String line = "";

                // Read in the data from the Buffer until nothing is left
                while ((line = bufferedReader.readLine()) != null) {

                    // Add data from the Buffer until nothing is left
                    stringBuilder.append(line + '\n');
                }

                // Store the complete data in result
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
                // Print out all the data read in
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
                        recipeTitle = recipe.getString("title");
                        recipeURL = recipe.getString("href");
                        recipeIngredients = recipe.getString("ingredients");

                        recipeList.add(new Recipe(recipeTitle, recipeURL, recipeIngredients));

                        Log.d("Title: ", recipeTitle);
                        Log.d("URL: ", recipeURL);
                        Log.d("Ingredients List: ", recipeIngredients);

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
//
//        TextView title = (TextView) findViewById(R.id.title);
//        TextView url = (TextView) findViewById(R.id.href);
//        TextView ingredients = (TextView) findViewById(R.id.ingredients);
//
//            title.setText("Title: " + recipeTitle);
//            url.setText("URL: " + recipeURL);
//            ingredients.setText("Ingredients: " + recipeIngredients);
//
//        title.setText("Title: " + recipeList.get(0).getRecipeTitle());
//        url.setText("URL: " + recipeList.get(0).getRecipeURL());
//        ingredients.setText("Ingredients: " + recipeList.get(0).getRecipeIngredients());

            Log.d(TAG, "Title: " + recipeList.get(0).getRecipeTitle());
            Log.d(TAG, "URL: " + recipeList.get(0).getRecipeURL());
            Log.d(TAG, "Ingredients: " + recipeList.get(0).getRecipeIngredients());

            if (this.recipesFragmentWeakReference.get() != null) {
                Log.d(TAG, "Now treat the result");
            }
        }
    }
}
