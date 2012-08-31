/*
 * Copyright (C) 2012  Projectoria Ltd.
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

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

public class AppsListFragment extends SherlockListFragment {

	PackageManager pman = null;
	private int currentPosition = -1;
	private OnAppSelectedListener appSelectedListener = null;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			appSelectedListener = (OnAppSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(getSherlockActivity().toString() + 
					": must implement OnAppSelectedListener"
					);
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ListView appsList = getListView(); 
		appsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		appsList.setSelector(R.drawable.list_item_selector);
        
        pman = getSherlockActivity().getPackageManager();
        
        List<ApplicationInfo> apps = pman.getInstalledApplications(PackageManager.GET_META_DATA);
        Collections.sort(apps, new LabelComparator());
        
        ListAdapter adapter = new AppAdapter(apps);
        setListAdapter(adapter);
        
        if(savedInstanceState != null) {
        	currentPosition = savedInstanceState.getInt("currentChoice");
        	if(currentPosition > 0)
        		showDetails(currentPosition, true);
        }
        
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("currentChoice", currentPosition);
	}
	
    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
    	currentPosition = position;
    	l.setItemChecked(position, true);
   		showDetails(position, false);
    }
    
	private void showDetails(int position, boolean savedState) {
		ApplicationInfo app = (ApplicationInfo) getListView().getItemAtPosition(position);
		Uri uri = (new Uri.Builder())
				.scheme("app-inspector")
				.authority(app.packageName)
				.build();
		
		appSelectedListener.onAppSelected(uri, savedState);
	}
	
	private class AppAdapter extends ArrayAdapter<ApplicationInfo> {
	
		public AppAdapter(List<ApplicationInfo> apps) {
			super(getSherlockActivity(), R.layout.app_item, R.id.label, apps);
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
	
	public interface OnAppSelectedListener {
		public void onAppSelected(Uri uri, boolean savedState);
	}

}
