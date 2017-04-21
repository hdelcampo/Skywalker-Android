package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import es.uva.tfg.hector.SkyWalkerApp.R;

/**
 * Activity to handle new connections requests.
 * @author Hector Del Campo Pando
 */
public class NewConnectionActivity extends AppCompatActivity {

    /**
     * Permissions IDs.
     */
    private static final int APP_PERMISSIONS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tabs_connection_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabHost = (TabLayout)findViewById(R.id.tabHost);

        tabHost.addTab(tabHost.newTab());
        tabHost.addTab(tabHost.newTab());

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final Adapter adapter = new Adapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabHost));
        tabHost.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //checkPermissions();

    }

    /**
     * Checks App granted permissions.
     */
    private void checkPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    APP_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case APP_PERMISSIONS: {
                if (grantResults.length == 0
                        || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Snackbar.make(findViewById(R.id.container), getString(R.string.needs_camera_permissions), Snackbar.LENGTH_LONG).show();
                    checkPermissions();
                }
            }
        }
    }

    /**
     * Tabs fragments adapter.
     */
    private class Adapter extends FragmentPagerAdapter {

        /**
         * The number of tabs to display.
         */
        private static final int NUM_TABS = 2;

        protected Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;

            switch (position) {
                case 0:
                    fragment = new ManualConnectionFragment();
                    break;
                case 1:
                    fragment = new QRConnectionFragment();
                    break;
            }

            return fragment;

        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return NewConnectionActivity.this.getString(R.string.Manual);
                case 1:
                    return NewConnectionActivity.this.getString(R.string.QR);
                default:
                    return null;
            }
        }
    }

}
