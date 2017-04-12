package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

import es.uva.tfg.hector.SkyWalkerApp.R;

/**
 * Custom filter row layout class.
 * @author Héctor Del Campo Pando
 */
public class FilterRowLayout extends LinearLayout implements Checkable {

    /**
     * The checkbox.
     */
    private CheckBox checkBox;

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
        checkBox.setChecked(!checkBox.isChecked());
    }

}
