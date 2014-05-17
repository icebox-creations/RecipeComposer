package creations.icebox.recipecomposer;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import creations.icebox.recipecomposer.adapter.TabsPagerAdapter;

public class MainActivity extends ActionBarActivity
        implements ActionBar.TabListener, IngredientsFragment.OnPageChangeListener {

    private static final String TAG = "***MAIN ACTIVITY***: ";
    private TabsPagerAdapter mTabsPagerAdapter;
    private Menu menu;
    private ViewPager mViewPager;
    private StringBuffer ingredientTitles;
    private String query;

    public StringBuffer getIngredientTitles() {
        return ingredientTitles;
    }

    public String getQuery() {
        return query;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three primary sections of
        // the activity.
        mTabsPagerAdapter = new TabsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mTabsPagerAdapter);

        // When swiping between different sections, select the corresponding tab. We can also use
        // ActionBar.Tab#select() to do this if we have a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected");
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mTabsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter. Also
            // specify this Activity object, which implements the TabListener interface, as the
            // callback (listener) for when this tab is selected.
            actionBar.addTab(
                actionBar.newTab()
                    .setText(mTabsPagerAdapter.getPageTitle(i, getApplicationContext()))
                    .setTabListener(this));
        }
        mViewPager.setCurrentItem(1);
        mViewPager.setOffscreenPageLimit(2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
//        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SetPreferencesActivity.class);
            startActivityForResult(intent, 0);
            return true;
        } else if (id == R.id.action_about) {
            FragmentManager fragmentManager = this.getFragmentManager();
            DialogAboutFragment dialogAboutFragment
                    = new DialogAboutFragment();
            dialogAboutFragment.show(fragmentManager, "dialog about fragment");
            return true;
//        } else if (id == R.id.action_create_new_recipe) {
//            getFragmentManager().beginTransaction()
//                    .replace(android.R.id.content, new RecipeCreatorFragment())
//                    .addToBackStack(null).commit();
//            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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

/***************************************************************************************************
* Listen to OnPageChangeListener event from Ingredients Fragment and forward to Recipes Fragment
* @param ingredientTitles
*/
    @Override
    public void onPageChange(StringBuffer ingredientTitles, String query) {
        try {
            this.ingredientTitles = ingredientTitles;
            this.query = query;
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
        }
    }
}
