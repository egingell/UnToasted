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

import com.egingell.untoaster.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

@SuppressLint("NewApi")
public class Welcome extends Activity {
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);
		final Intent existingIntent = new Intent(),
					 logs = new Intent(),
					 allApps = new Intent();
		Button log = (Button) findViewById(R.id.log), apps = (Button) findViewById(R.id.apps), existing = (Button) findViewById(R.id.existing);

		existingIntent.setClass(this, EditActivity.class);
		logs.setClass(this, LogsActivity.class);
		allApps.setClass(this, AppsActivity.class);
		
		existing.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View button) {
				startActivity(existingIntent, savedInstanceState);
			}
		});
		log.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View button) {
				startActivity(logs, savedInstanceState);
			}
		});
		apps.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View button) {
				startActivity(allApps, savedInstanceState);
			}
		});
	}
}
