/*
 * Copyright (C) 2011-2012  Projectoria Ltd.
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
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class AppDetails extends SherlockFragmentActivity {
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(savedInstanceState == null) {
        	AppDetailsFragment fragment = new AppDetailsFragment();
        	getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();
        }
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	menu
    	.add(Menu.CATEGORY_CONTAINER,
    			About.MENU_ABOUT,
    			Menu.FIRST,
    			"About")
    			.setIcon(android.R.drawable.ic_menu_info_details)
    			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
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
