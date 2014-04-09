package creations.icebox.recipecomposer;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class PreferencesActivity extends Activity {

    final private static String TAG = "***SetPrefActivity***";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

//        Globals.progressDialog.dismiss();

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PreferencesFragment()).commit();

//        try {
//            getActionBar().setDisplayHomeAsUpEnabled(true);
//        } catch (NullPointerException e) {
//            Log.d(TAG, e.toString());
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch(item.getItemId()) {
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                return true;
//            default:
                return super.onOptionsItemSelected(item);
//        }
    }
}