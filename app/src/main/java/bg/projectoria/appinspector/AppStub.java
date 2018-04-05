package bg.projectoria.appinspector;

import android.graphics.drawable.Drawable;

import java.util.Comparator;

public class AppStub {

    public final String packageName;
    public final String label;
    public final Drawable icon;

    public AppStub(String packageName, CharSequence label, Drawable icon) {
        this.packageName = packageName;
        this.label = (label != null)? String.valueOf(label) : "";
        this.icon = icon;
    }

    public static Comparator<AppStub> LABEL_COMPARATOR =
            (app1, app2) -> app1.label.compareTo(app2.label);
}
