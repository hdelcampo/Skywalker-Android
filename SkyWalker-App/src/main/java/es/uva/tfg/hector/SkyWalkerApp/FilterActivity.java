package es.uva.tfg.hector.SkyWalkerApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog to filter {@Code PointsOfInterest}.
 * @author Hector Del Campo Pando
 */
public class FilterActivity extends Activity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_points_dialog_layout);

        usedPoints = getIntent().getParcelableArrayListExtra(USED_POINTS_EXTRA);
        allPoints = getIntent().getParcelableArrayListExtra(ALL_POINTS_EXTRA);
        addPoints();
    }



    /**
     * Adds points to show, and selects their CheckBoxes if they were already being shown.
     */
    private void addPoints() {
        ListView itemsList = (ListView) findViewById(R.id.itemsList);
        itemsList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        PointOfInterest[] from = new PointOfInterest[allPoints.size()];
        from = allPoints.toArray(from);

        ArrayAdapter<PointOfInterest> adapter =
                new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice,
                from);

        itemsList.setAdapter(adapter);

        for(int i = 0; i < adapter.getCount(); i++) {
            if (usedPoints.contains(adapter.getItem(i))) {
                itemsList.setItemChecked(i, true);
            }
        }

    }

    /**
     * Retrieves a list of user's selected {@link PointOfInterest} to show.
     * @return the list of IDs.
     */
    private List<PointOfInterest> getSelectedPoints() {
        List<PointOfInterest> selecteds = new ArrayList<>();
        final ListView itemsList = (ListView) findViewById(R.id.itemsList);

        final SparseBooleanArray checked = itemsList.getCheckedItemPositions();

        for (int i = 0; i < itemsList.getAdapter().getCount(); i++) {
            if (checked.get(i)){
                selecteds.add(allPoints.get(i));
            }
        }

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
     * Checks all items inside the list view
     * @param view who called.
     */
    public void selectAll(View view) {
        final ListView itemsList = (ListView) findViewById(R.id.itemsList);

        for(int i = 0; i < itemsList.getAdapter().getCount(); i++) {
            itemsList.setItemChecked(i, true);
        }

    }

    /**
     * Eliminates all checks inside the list view
     * @param view who called.
     */
    public void deselectAll(View view) {
        final ListView itemsList = (ListView) findViewById(R.id.itemsList);

        for(int i = 0; i < itemsList.getAdapter().getCount(); i++) {
            itemsList.setItemChecked(i, false);
        }
    }
}
