/*
 * This file is part of UnToasted.
 *
 * Copyright 2014 Eric Gingell (c)
 *
 *     ButteredToast is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ButteredToast is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with UnToasted.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.egingell.untoaster.activities;

import java.util.List;

import com.egingell.untoaster.R;

import android.app.ListActivity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class AppsActivity extends ListActivity {
	private List<ApplicationInfo> mStrings;
	ArrayAdapter<ApplicationInfo> adapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apps_layout);
		adapter = new ArrayAdapter<ApplicationInfo>(this, android.R.layout.simple_list_item_1, mStrings);
		final PackageManager pm = getPackageManager();
		mStrings = pm.getInstalledApplications(0);
        setListAdapter(adapter);
    }
}
