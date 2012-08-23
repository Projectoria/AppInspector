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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import bg.projectoria.appinspector.AppsListFragment.OnAppSelectedListener;

public class Main extends FragmentActivity implements OnAppSelectedListener{
	
	private boolean isDual = false;
	private AppDetailsFragment detailsFragment = null;
	private AppsListFragment appsFragment = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        appsFragment = (AppsListFragment) getSupportFragmentManager().findFragmentById(R.id.apps_list);
        detailsFragment = (AppDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.app_details);
        
        View detailsView = findViewById(R.id.app_details);
        
        if(detailsView != null && detailsView.getVisibility() == View.VISIBLE){
        	isDual = true;
        }
        
    }
    
	@Override
	public void onAppSelected(Uri uri) {
		if(!isDual){
			Intent i = new Intent(this, AppDetails.class);
			i.setData(uri);
			startActivity(i);
		}
		
		detailsFragment.showDetails(uri);
	}
}    
