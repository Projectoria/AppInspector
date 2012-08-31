package bg.projectoria.appinspector;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private boolean isChecked;
    private final ColorDrawable selected = new ColorDrawable(getResources().getColor(R.color.selected));

    public CheckableLinearLayout(Context context) {
        super(context);
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override    
    public void setChecked(boolean checked) {
        isChecked = checked;
        setBackgroundDrawable(checked ? selected : null);
    }

    @Override
    public boolean isChecked() {
    	return isChecked;
    }
    
	@Override
	public void toggle() {
		setChecked(!isChecked);
	}

}
