package bg.projectoria.appinspector;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AppsListFragment extends ListFragment {

	PackageManager pman = null;
	private int currentPosition = 0;
	private OnAppSelectedListener appSelectedListener = null;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
        pman = getActivity().getPackageManager();
        
        List<ApplicationInfo> apps = pman.getInstalledApplications(PackageManager.GET_META_DATA);
        Collections.sort(apps, new LabelComparator());
        
        ListAdapter adapter = new AppAdapter(apps);
        setListAdapter(adapter);
        
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        if(savedInstanceState != null) {
        	currentPosition = savedInstanceState.getInt("currentChoice");
        	showDetails(currentPosition);
        }
        
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			appSelectedListener = (OnAppSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(getActivity().toString() + 
										": must implement OnAppSelectedListener"
										);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("currentChoice", currentPosition);
	}
	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//	
//		return super.onCreateView(inflater, container, savedInstanceState);
//	}
	
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		super.onCreateOptionsMenu(menu);
//		menu
//			.add(Menu.CATEGORY_CONTAINER,
//					About.MENU_ABOUT,
//					Menu.FIRST,
//					"About")
//					.setIcon(android.R.drawable.ic_menu_info_details);
//		return true;
//	}
//    
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case About.MENU_ABOUT:
//			startActivity(new Intent(this, About.class));
//			return true;
//		}
//		return false;
//	}
    
    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
   		showDetails(position);
    }
    
	private void showDetails(int position) {
		ApplicationInfo app = (ApplicationInfo) getListView().getItemAtPosition(position);
		Uri uri = (new Uri.Builder())
				.scheme("app-inspector")
				.authority(app.packageName)
				.build();
		
		appSelectedListener.onAppSelected(uri);
	}
	
	private class AppAdapter extends ArrayAdapter<ApplicationInfo> {
	
		public AppAdapter(List<ApplicationInfo> apps) {
			super(getActivity(), R.layout.app_item, R.id.label, apps);
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
		public void onAppSelected(Uri uri);
	}

}
