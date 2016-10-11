package es.uva.tfg.hector.SkyWalkerApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hector Del Campo Pando on 11/10/2016.
 */

/**
 * Dialog to filter {@Code PointsOfInterest}.
 */
public class FilterActivity extends Activity {

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
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putStringArrayListExtra("selected", (ArrayList<String>) getSelectedPoints());
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    /**
     * Adds points to show, and selects their CheckBoxes if they were already being shown.
     */
    private void addPoints() {
        LinearLayout checkList = (LinearLayout) findViewById(R.id.pointsList);

        for (String point: allPoints) {
            CheckBox listElement = new CheckBox(this);
            listElement.setChecked(false);
            listElement.setText(point);
            View row = getLayoutInflater().inflate(R.layout.filter_row, null);
            TextView id = (TextView) row.findViewById(R.id.rowText);
            id.setText(point);

            if (usedPoints.contains(point)) {
                CheckBox checkBox = (CheckBox) row.findViewById(R.id.checkBox);
                checkBox.setChecked(true);
            }

            checkList.addView(row);
        }
    }

    /**
     * Retrieves a list of user's selected {@Code PointsOfInterest} to show.
     * @return the list of IDs.
     */
    private List<String> getSelectedPoints() {
        List<String> selecteds = new ArrayList<>();
        LinearLayout checkList = (LinearLayout) findViewById(R.id.pointsList);

        for (int i = 0; i < checkList.getChildCount(); i++) {
            View row = checkList.getChildAt(i);
            CheckBox checkBox = (CheckBox) row.findViewById(R.id.checkBox);
            if (checkBox.isChecked()) {
                selecteds.add(allPoints.get(i));
            }
        }

        return selecteds;
    }

}
