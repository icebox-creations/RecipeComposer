package creations.icebox.recipecomposer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

public class RecipeComposerActivity extends Activity {

    // JSON RESTful API test
    static String recipePuppyURL = "http://www.recipepuppy.com/api/?i=onions,garlic&q=omelet&p=3";
    static String TAG = "***RECIPE COMPOSER***: ";

    static String recipeTitle = "";
    static String recipeURL = "";
    static String recipeIngredients = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Get the saved data
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        // Point to the name for the layout XML file used
        setContentView(R.layout.activity_recipe_composer);

        // Call for doInBackground() in RecipeDownloader to be executed
        new RecipeDownloader().execute();
    }

    /*
    * Use AsyncTask if you need to perform background tasks, but also need
    * to change components on the GUI. Put the background operations in
    * doInBackground. Put the GUI manipulation code in onPostExecute
    * */
    private class RecipeDownloader extends AsyncTask<String, String, String> {

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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute");

            TextView title = (TextView) findViewById(R.id.title);
            TextView url = (TextView) findViewById(R.id.href);
            TextView ingredients = (TextView) findViewById(R.id.ingredients);

            title.setText("Title: " + recipeTitle);
            url.setText("URL: " + recipeURL);
            ingredients.setText("Ingredients: " + recipeIngredients);
        }
    }
}
