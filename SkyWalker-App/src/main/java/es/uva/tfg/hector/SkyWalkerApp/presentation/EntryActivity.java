package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

    /**
     * View's pager.
     */
    private ViewPager viewPager;

    /**
     * Carousel changing times.
     */
    private static final long
            INITIAL_CAROUSEL_CHANGE = 1500,
            STANDARD_CAROUSEL_CHANGE = INITIAL_CAROUSEL_CHANGE*2;

    /**
     * Carousel handler.
     */
    private Handler handler = new Handler();

    /**
     * Carousel runnable.
     */
    private Runnable carousel = new Runnable() {
        @Override
        public void run() {
            final int currentItem = viewPager.getCurrentItem();
            viewPager.setCurrentItem(currentItem != SectionsPagerAdapter.NUM_PAGES - 1 ? currentItem + 1 : 0, true);
            handler.postDelayed(carousel, STANDARD_CAROUSEL_CHANGE);
        }
    };


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

        handler.postDelayed(carousel, INITIAL_CAROUSEL_CHANGE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        stopCarousel();
        return super.dispatchTouchEvent(ev);
    }

    /**
     * Called whenever login button is clicked, starts the login activity.
     * @param view who called.
     */
    public void loginClick (View view) {
        final boolean hasPermissions = PermissionsRequestActivity.hasEnoughPermissions(this);
        Intent intent =  new Intent(this,
                hasPermissions ? NewConnectionActivity.class : PermissionsRequestActivity.class);
        startActivity(intent);
    }

    public void stopCarousel () {

        if (handler == null) {
            return;
        }

        handler.removeCallbacks(carousel);
        handler = null;
        carousel = null;

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        /**
         * The number of pages to show.
         */
        static final int NUM_PAGES = 3;

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
