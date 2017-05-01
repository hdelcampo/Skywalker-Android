package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.uva.tfg.hector.SkyWalkerApp.R;
import es.uva.tfg.hector.SkyWalkerApp.business.PointOfInterest;

/**
 * Dialog to filter {@Code PointsOfInterest}.
 * @author Hector Del Campo Pando
 */
public class FilterActivity extends Activity implements FilterRowLayout.Listener {

    /**
     * Intent's extra data IDs
     */
    public static final String USED_POINTS_EXTRA = "usedPoints";
    public static final String ALL_POINTS_EXTRA = "allPoints";
    public static final String POINTS_TO_SHOW = "pointsToShow";

    /**
     * List of items that were being already used, and list of all items to represent.
     */
    private List<PointOfInterest> usedPoints,
                        allPoints;

    /**
     * Current selected items.
     */
    private Set<PointOfInterest> selectedPoints = new HashSet<>(OverlayView.MAX_ELEMENTS_TO_DRAW);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_points_layout);

        usedPoints = getIntent().getParcelableArrayListExtra(USED_POINTS_EXTRA);
        allPoints = getIntent().getParcelableArrayListExtra(ALL_POINTS_EXTRA);
        addPoints();

        ((TextView) findViewById(R.id.selected_counter_label)).setText(
                String.format(
                        getString(R.string.selected_status_msg)
                        , selectedPoints.size(), OverlayView.MAX_ELEMENTS_TO_DRAW));
    }



    /**
     * Adds points to show, and selects their CheckBoxes if they were already being shown.
     */
    private void addPoints() {
        ListView itemsList = (ListView) findViewById(R.id.itemsList);

        itemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((FilterRowLayout)view).toggle();
            }
        });

        for (PointOfInterest point: allPoints) {
            if (usedPoints.contains((point))) {
                selectedPoints.add(point);
            }
        }

        ArrayAdapter<PointOfInterest> adapter =
                new ArrayAdapter<PointOfInterest>(
                        this,
                        R.layout.filter_row_layout,
                        R.id.elementTextView,
                        allPoints
                ) {

                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        FilterRowLayout view = (FilterRowLayout) super.getView(position, convertView, parent);
                        PointOfInterest point = allPoints.get(position);
                        view.setPoint(point);
                        view.setListener(FilterActivity.this);
                        view.setChecked(selectedPoints.contains(point));
                        return view;
                    }
        };

        itemsList.setAdapter(adapter);

    }

    /**
     * Retrieves a list of user's selected {@link PointOfInterest} to show.
     * @return the list of IDs.
     */
    private List<PointOfInterest> getSelectedPoints() {
        List<PointOfInterest> selecteds = new ArrayList<>(selectedPoints);
        return selecteds;
    }

    /**
     * Finishes the activity and sets result as a list of selected {@link PointOfInterest}'s IDs.
     * @param view who called.
     */
    public void accept(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putParcelableArrayListExtra(POINTS_TO_SHOW, (ArrayList<PointOfInterest>) getSelectedPoints());
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    /**
     * Finishes the activity without changing anything.
     * @param view who called.
     */
    public void cancel(View view) {
        finish();
    }

    /**
     * Eliminates all checks inside the list view
     * @param view who called.
     */
    public void deselectAll(View view) {
        final ListView itemsList = (ListView) findViewById(R.id.itemsList);

        selectedPoints.clear();

        for(int i = 0; i < itemsList.getCount(); i++) {
            FilterRowLayout child = ((FilterRowLayout)itemsList.getChildAt(i));
            if (null != child) {
                child.setChecked(false);
            }
        }

        ((TextView) findViewById(R.id.selected_counter_label)).setText(
                String.format(
                        getString(R.string.selected_status_msg)
                        , selectedPoints.size(), OverlayView.MAX_ELEMENTS_TO_DRAW));

    }

    @Override
    public boolean canToggle (PointOfInterest point) {
        int size = selectedPoints.size();
        boolean contains = selectedPoints.contains(point);
        return OverlayView.MAX_ELEMENTS_TO_DRAW > size
                || contains;
    }

    @Override
    public void onToggle(PointOfInterest point, boolean checked) {
        if (checked) {
            selectedPoints.add(point);
        } else {
            selectedPoints.remove(point);
        }

        ((TextView) findViewById(R.id.selected_counter_label)).setText(
                String.format(
                        getString(R.string.selected_status_msg)
                , selectedPoints.size(), OverlayView.MAX_ELEMENTS_TO_DRAW));
    }

}
