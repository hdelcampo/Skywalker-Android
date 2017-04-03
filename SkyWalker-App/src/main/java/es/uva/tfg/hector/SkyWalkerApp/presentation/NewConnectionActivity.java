package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import es.uva.tfg.hector.SkyWalkerApp.R;

/**
 * Activity to handle new connections requests.
 * @author Hector Del Campo Pando
 */
public class NewConnectionActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tabs_connection_layout);
        TabLayout tabHost = (TabLayout)findViewById(R.id.tabHost);

        tabHost.addTab(tabHost.newTab());
        tabHost.addTab(tabHost.newTab());
        tabHost.setTabGravity(TabLayout.GRAVITY_FILL);

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

            Fragment fragment;

            switch (position) {
                case 0:
                    fragment = new QRConnectionFragment();
                    return fragment;
                case 1:
                    fragment = new ManualConnectionFragment();
                    return fragment;
                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return NewConnectionActivity.this.getString(R.string.QR);
                case 1:
                    return NewConnectionActivity.this.getString(R.string.Manual);
                default:
                    return null;
            }
        }
    }

}
