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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Main extends ListActivity {
	
	PackageManager pman = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
        
        pman = getPackageManager();
        
        List<ApplicationInfo> apps = pman.getInstalledApplications(PackageManager.GET_META_DATA);
        Collections.sort(apps, new LabelComparator());
        
        ListAdapter adapter = new AppAdapter(apps);
        setListAdapter(adapter);
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
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	ApplicationInfo app = (ApplicationInfo) l.getItemAtPosition(position);
    	Uri uri = (new Uri.Builder())
    		.scheme("app-inspector")
    		.authority(app.packageName)
    		.build();
    	
    	Intent i = new Intent(this, AppDetails.class);
    	i.setData(uri);
    	startActivity(i);
    }
    
    private class AppAdapter extends ArrayAdapter<ApplicationInfo> {

		public AppAdapter(List<ApplicationInfo> apps) {
			super(Main.this, R.layout.app_item, R.id.label, apps);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			ApplicationInfo app = getItem(position);
			
			ImageView icon = (ImageView) view.findViewById(R.id.icon);
			TextView label = (TextView) view.findViewById(R.id.label);
			icon.setImageDrawable(pman.getApplicationIcon(app));
			label.setText(pman.getApplicationLabel(app));
			return view;
		}
    	
    }
    
    private class LabelComparator implements Comparator<ApplicationInfo> {

		@Override
		public int compare(ApplicationInfo app1, ApplicationInfo app2) {
			String label1 = pman.getApplicationLabel(app1).toString();
			String label2 = pman.getApplicationLabel(app2).toString();
			return label1.compareTo(label2);
		}
    	
    }
}