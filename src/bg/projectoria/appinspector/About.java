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

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class About extends SherlockActivity {
	
	private static final String TAG = "About";
	
	static final int MENU_ABOUT = Menu.FIRST + 1000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		PackageManager pman = getPackageManager();
		CharSequence appName = pman.getApplicationLabel(getApplicationInfo());
		CharSequence appVersion = "";
		try {
			appVersion = pman
				.getPackageInfo(getPackageName(), 0)
				.versionName;
		}
		catch (NameNotFoundException e) {
			// This really shouldn't happen since we're looking for the package
			// of the currently running app.
			Log.e(TAG, "Can't find my own package", e);
		}
		String appFull = appName + " " + appVersion;
		((TextView) findViewById(R.id.name_and_version)).setText(appFull);
	}
}
