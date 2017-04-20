package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        setTheme(R.style.AppTheme);
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
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        /**
         * The number of pages to show.
         */
        private static final int NUM_PAGES = 3;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return OnBoardingPageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }

    /**
     * A On-Boarding page, position determines the content.
     */
    public static class OnBoardingPageFragment extends Fragment {

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         * @param sectionNumber The page index
         */
        public static OnBoardingPageFragment newInstance(int sectionNumber) {
            OnBoardingPageFragment fragment = new OnBoardingPageFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 0:
                    rootView = inflater.inflate(R.layout.ar_on_boarding_page, container, false);
                    break;
                case 1:
                    rootView = inflater.inflate(R.layout.beacon_on_boarding_page, container, false);
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.indoor_outdoor_on_boarding_page, container, false);
                    break;
            }

            return rootView;
        }

    }


}
