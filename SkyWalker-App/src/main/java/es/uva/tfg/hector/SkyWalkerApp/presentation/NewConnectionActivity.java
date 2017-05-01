package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import es.uva.tfg.hector.SkyWalkerApp.R;

/**
 * Activity to handle new connections requests.
 * @author Hector Del Campo Pando
 */
public class NewConnectionActivity extends AppCompatActivity {

    /**
     * The QR Fragment.
     */
    private QRConnectionFragment qrFragment;

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
                Log.e("Tab", "selected " + tab.getPosition());
                if (1 == tab.getPosition()) {
                    qrFragment.enableDetection(true);
                    qrFragment.startDetection();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.e("Tab", "unselected " + tab.getPosition());
                if (1 == tab.getPosition()) {
                    qrFragment.stopDetection();
                    qrFragment.enableDetection(false);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    /**
     * Tabs fragments adapter.
     */
    private class Adapter extends FragmentPagerAdapter {

        /**
         * The number of tabs to display.
         */
        private static final int NUM_TABS = 2;

        Adapter(FragmentManager fm) {
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
                    NewConnectionActivity.this.qrFragment = (QRConnectionFragment) fragment;
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
