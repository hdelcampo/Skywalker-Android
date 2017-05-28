package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

import es.uva.tfg.hector.SkyWalkerApp.R;
import es.uva.tfg.hector.SkyWalkerApp.business.PointOfInterest;

/**
 * Custom filter row layout class.
 * @author Héctor Del Campo Pando
 */
public class FilterRowLayout extends LinearLayout implements Checkable {

    /**
     * The view's represented point.
     */
    private PointOfInterest point;

    /**
     * The checkbox.
     */
    private CheckBox checkBox;

    /**
     * Checkable listener.
     */
    private FilterCellDelegate listener;

    /**
     * Creates a new row layout.
     * @param context to use.
     * @param attrs of the layot.
     */
    public FilterRowLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        checkBox = (CheckBox) findViewById(R.id.checkBox);
    }

    @Override
    public void setChecked(boolean checked) {
        checkBox.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return checkBox.isChecked();
    }

    @Override
    public void toggle() {
        if (null == listener || listener.canToggle(point)) {
            setChecked(!checkBox.isChecked());
            if (null != listener) {
                listener.onToggle(point, checkBox.isChecked());
            }
        }
    }

    /**
     * Sets a listener for callbacks
     * @param listener to add.
     */
    public void setListener(FilterCellDelegate listener) {
        this.listener = listener;
    }

    /**
     * Sets the point to hold.
     * @param point to hold.
     */
    public void setPoint (PointOfInterest point) {
        this.point = point;
    }

    /**
     * Delegate interface.
     */
    interface FilterCellDelegate {

        /**
         * Decides whether this view can toggle its checkbox or not.
         * @param point of the view.
         * @return true if can toggle checkbox, false otherwise.
         */
        boolean canToggle (PointOfInterest point);

        /**
         * Called when checkbox was toggled.
         * @param point of the view.
         * @param checked new state of the checkbox.
         */
        void onToggle(PointOfInterest point, boolean checked);

    }

}
