package creations.icebox.recipecomposer;

import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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

    RecipesFragment recipesFragment;

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

    /**
     * Save all appropriate fragment state.
     *
     * @param outState
     */
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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


        mViewPager.setCurrentItem(tab.getPosition());
        if (tab.getPosition() == 1) {
            //make recipe requests using the ingredients titles..
            Log.v(TAG, "onTabSelected clicked: recipe tab");
        } else if (tab.getPosition() == 0) {
            Log.v(TAG, "onTabSelected clicked: ingredients tab");
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
    public void onNavigationToRecipes(StringBuffer ingredientTitles) {
        this.ingredientTitles = ingredientTitles;
        Toast.makeText(this, "ingredientTitles in Main Activity = " + this.ingredientTitles, Toast.LENGTH_SHORT).show();
        Log.d(TAG, this.ingredientTitles.toString());

        recipesFragment = new RecipesFragment();
        recipesFragment.onNavigationToRecipes(ingredientTitles);
    }
}
