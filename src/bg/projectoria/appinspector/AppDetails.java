/*
 * Copyright (C) 2011  Projectoria Ltd.
 * This file is part of App Inspector.
 *
 * App Inspector is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import android.view.Menu;
import android.view.MenuItem;
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
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu
			.add(Menu.CATEGORY_CONTAINER,
					About.MENU_ABOUT,
					Menu.FIRST,
					"About")
					.setIcon(android.R.drawable.ic_menu_info_details);
		return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case About.MENU_ABOUT:
			startActivity(new Intent(this, About.class));
			return true;
		}
		return false;
	}
}
