package creations.icebox.recipecomposer;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class SetPreferencesActivity extends Activity {
    final private static String TAG = "***SET PREF ACTIVITY***";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PreferencesFragment()).commit();
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