package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import es.uva.tfg.hector.SkyWalkerApp.R;
import io.fabric.sdk.android.Fabric;

/**
 * Enter point for the App, after the splash one.
 * @author Hector Del Campo Pando
 */
public class EntryActivity extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        setContentView(R.layout.entry_on_boarding);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager, true);

    }

    /**
     * Called whenever login button is clicked, starts the login activity.
     * @param view who called.
     */
    public void loginClick (View view) {
        Intent intent = new Intent(this, EntryLoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private static final int NUM_PAGES = 2;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new AROnBoardingFragment();
                case 1:
                    return new AROnBoardingFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }

}
