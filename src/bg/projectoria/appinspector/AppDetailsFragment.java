package bg.projectoria.appinspector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.SherlockListFragment;

public class AppDetailsFragment extends SherlockListFragment {

	private static final String TAG = "AppDetails";
	
	private List<Map<String, ?>> entries = new ArrayList<Map<String, ?>>();
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		this.setListShownNoAnimation(true);
		ListView detailsList = getListView(); 
		detailsList.setChoiceMode(ListView.CHOICE_MODE_NONE);
		detailsList.setSelector(R.drawable.list_item_selector_none);
		
        Intent i = getSherlockActivity().getIntent();
        Uri uri = i.getData();
        
        if(uri != null)
        	showDetails(uri);
    }
	
	public void showDetails(Uri uri) {
		if(getSherlockActivity() == null)
			return;
		
		if(!entries.isEmpty())
			entries.clear();
		
		Log.d(TAG, "uri = " + uri);
		PackageManager pman = getSherlockActivity().getPackageManager();
		ApplicationInfo app = null;
		PackageInfo pkg = null;
		String pkgName = uri.getAuthority();
		Log.d(TAG, "uri = " + pkgName);
		try {
			app = pman.getApplicationInfo(pkgName, PackageManager.GET_META_DATA);
			pkg = pman.getPackageInfo(pkgName, PackageManager.GET_META_DATA);
			getActivity().setTitle(pman.getApplicationLabel(app));
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
		
		setListAdapter(new SimpleAdapter(getSherlockActivity(),
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
