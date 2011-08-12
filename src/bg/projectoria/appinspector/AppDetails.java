package bg.projectoria.appinspector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleAdapter;

public class AppDetails extends ListActivity {
	
	private static final String TAG = "AppDetails";
	
	private List<Map<String, ?>> entries = new ArrayList<Map<String, ?>>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(android.R.layout.simple_list_item_2);
        
        Intent i = getIntent();
        Uri uri = i.getData();
        Log.d(TAG, "uri = " + uri);
        PackageManager pman = getPackageManager();
        ApplicationInfo app = null;
        PackageInfo pkg = null;
        String pkgName = uri.getAuthority();
        Log.d(TAG, "uri = " + pkgName);
        try {
        	app = pman.getApplicationInfo(pkgName, PackageManager.GET_META_DATA);
        	pkg = pman.getPackageInfo(pkgName, PackageManager.GET_META_DATA);
        	setTitle(pman.getApplicationLabel(app));
        }
        catch (NameNotFoundException e) {
        	// FIXME
        	Log.e(TAG, "error", e);
        	return;
        }
        
        // Fill entries
        entry("Label", pman.getApplicationLabel(app));
        entry("Package name", app.packageName);
        entry("Version name", pkg.versionName);
        entry("Version code", pkg.versionCode);
        entry("Target SDK version", app.targetSdkVersion);
        entry("Class name", app.className);
        entry("Source dir", app.sourceDir);
        entry("Data dir", app.dataDir);
        
        setListAdapter(new SimpleAdapter(this,
        		entries,
        		android.R.layout.simple_list_item_2,
        		new String[] {"key", "value"},
        		new int[] {android.R.id.text1, android.R.id.text2}));
    }
    
    private void entry(String key, Object value) {
    	Map<String,Object> e = new HashMap<String,Object>();
    	e.put("key", key);
    	e.put("value", value);
    	entries.add(e);
    }
}
