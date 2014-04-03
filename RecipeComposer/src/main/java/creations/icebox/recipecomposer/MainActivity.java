package creations.icebox.recipecomposer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Vector;

import creations.icebox.recipecomposer.adapter.TabsPagerAdapter;

public class MainActivity extends ActionBarActivity
        implements ActionBar.TabListener, IngredientsFragment.OnPageChangeListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    TabsPagerAdapter mTabsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the tab contents.
     */
    ViewPager mViewPager;

    private static final String TAG = "***MAIN ACTIVITY: ";

    StringBuffer ingredientTitles;
    String query;

    public StringBuffer getIngredientTitles() {
        return ingredientTitles;
    }
    public String getQuery() {
        return query;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate");
        //dbh = new DBHelper(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        Log.d(TAG, "set up action bar");
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        Log.d(TAG, "set up tab adapter");
        mTabsPagerAdapter = new TabsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        Log.d(TAG, "set up the ViewPager");
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mTabsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected");
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mTabsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mTabsPagerAdapter.getPageTitle(i, getApplicationContext()))
                            .setTabListener(this));
        }


//        if (savedInstanceState != null) {
//            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
//        }
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Log.d(TAG, fragmentTransaction.toString());

        if (mViewPager.getCurrentItem() != tab.getPosition()) {
            mViewPager.setCurrentItem(tab.getPosition());
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    /**
     * Listen to OnPageChangeListener event from Ingredients Fragment and forward to Recipes Fragment
     * @param ingredientTitles
     */
    @Override
    public void onPageChange(StringBuffer ingredientTitles, String query) {
        try {
            this.ingredientTitles = ingredientTitles;
            this.query = query;
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }
}
