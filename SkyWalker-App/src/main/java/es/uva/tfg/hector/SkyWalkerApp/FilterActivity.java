package es.uva.tfg.hector.SkyWalkerApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hector Del Campo Pando on 11/10/2016.
 */

/**
 * Dialog to filter {@Code PointsOfInterest}.
 */
public class FilterActivity extends Activity implements View.OnClickListener {

    /**
     * Activity's identifier tag
     */
    private static String TAG = "Filter dialog";

    /**
     * List of items that were being already used, and list of all items to represent.
     */
    private List<String> usedPoints,
                        allPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.filter_points);
        super.onCreate(savedInstanceState);
        usedPoints = getIntent().getStringArrayListExtra("usedPoints");
        allPoints = getIntent().getStringArrayListExtra("allPoints");
        addPoints();
        Button acceptBtn = (Button) findViewById(R.id.acceptButton);
        acceptBtn.setOnClickListener(this);

        Button selectBtn = (Button) findViewById(R.id.selectButton);
        selectBtn.setOnClickListener(this);

        Button deselectBtn = (Button) findViewById(R.id.deselectButton);
        deselectBtn.setOnClickListener(this);

        Button cancelBtn = (Button) findViewById(R.id.cancelButton);
        cancelBtn.setOnClickListener(this);
    }



    /**
     * Adds points to show, and selects their CheckBoxes if they were already being shown.
     */
    private void addPoints() {
        ListView itemsList = (ListView) findViewById(R.id.itemsList);
        itemsList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        String[] from = new String[allPoints.size()];
        from = allPoints.toArray(from);
        final int[] to = {android.R.id.text1};

        ArrayAdapter<String> adapter = new ArrayAdapter(this,
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
     * Retrieves a list of user's selected {@Code PointsOfInterest} to show.
     * @return the list of IDs.
     */
    private List<String> getSelectedPoints() {
        List<String> selecteds = new ArrayList<>();
        final ListView itemsList = (ListView) findViewById(R.id.itemsList);

        final SparseBooleanArray checked = itemsList.getCheckedItemPositions();

        for (int i = 0; i < itemsList.getAdapter().getCount(); i++) {
            if (checked.get(i)){
                selecteds.add(allPoints.get(i));
            }
        }

        return selecteds;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.acceptButton:
                accept();
                break;
            case R.id.cancelButton:
                cancel();
                break;
            case R.id.selectButton:
                selectAll();
                break;
            case R.id.deselectButton:
                deselectAll();
                break;
            default:
                Log.e(TAG, "Unregistered event");
                break;
        }
    }

    /**
     * Finishes the activity and sets result as a list of selected {@code PointsOfInterest}'s IDs.
     */
    private void accept() {
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra("selected", (ArrayList<String>) getSelectedPoints());
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    /**
     * Finishes the activity without changing anything.
     */
    private void cancel() {
        finish();
    }

    /**
     * Checks all items inside the list view
     */
    private void selectAll() {
        final ListView itemsList = (ListView) findViewById(R.id.itemsList);

        for(int i = 0; i < itemsList.getAdapter().getCount(); i++) {
            Log.e(TAG, "Item: " + i);
            itemsList.setItemChecked(i, true);
        }

    }

    /**
     * Eliminates all checks inside the list view
     */
    private void deselectAll() {
        final ListView itemsList = (ListView) findViewById(R.id.itemsList);

        for(int i = 0; i < itemsList.getAdapter().getCount(); i++) {
            itemsList.setItemChecked(i, false);
        }
    }
}
