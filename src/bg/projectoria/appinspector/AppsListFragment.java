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
		
        pman = getSherlockActivity().getPackageManager();
        
        List<ApplicationInfo> apps = pman.getInstalledApplications(PackageManager.GET_META_DATA);
        Collections.sort(apps, new LabelComparator());
        
        ListAdapter adapter = new AppAdapter(apps);
        setListAdapter(adapter);
        
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

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
