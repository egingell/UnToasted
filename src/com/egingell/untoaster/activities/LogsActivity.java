/*
 * This file is part of UnToasted.
 *
 * Copyright 2014 Eric Gingell (c)
 *
 *     UnToasted is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     UnToasted is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with UnToasted.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.egingell.untoaster.activities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import com.egingell.untoaster.R;
import com.egingell.untoaster.common.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

@SuppressLint("NewApi")
public class LogsActivity extends Activity {
	Context mContext;
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
        setContentView(R.layout.logs_layout);
        Button refresh = (Button) findViewById(R.id.refresh);
        Button edit = (Button) findViewById(R.id.edit);
		Button cancelButton = (Button) findViewById(R.id.cancelButton);
        refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View button) {
				onResume();
			}
		});
        cancelButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        edit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				final String app = "#Intent;action=android.intent.action.MAIN;category=android.intent.category.LAUNCHER;package=" + getPackageName() + ";component=" + getPackageName() + "/" + getPackageName() + ".activities.EditActivity;end";
				final Intent intent;
				try {
					intent = Intent.parseUri(app, 0);
					startActivity(intent);
				} catch (Throwable e) {
					Util.log(e);
				}
			}
		});
	}
	final Pattern pattern = Pattern.compile("[:]");
	@Override
	protected void onResume() {
		super.onResume();
        String text = "--No Logs--";
        EditText logs = (EditText) findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setTextIsSelectable(true);
		try {
			text = "";
			InputStream stdout = Runtime.getRuntime().exec("logcat -d UnToaster:I *:S").getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
        	try {
    			String line;
    			while ((line = reader.readLine()) != null) {
    				try {
	    				String[] strs = pattern.split(line, 2);
    					line = strs[1];
    				} catch (Throwable e) {
    					//Util.log(e);
    				}
    				text += line + "\n";
    			}
    			reader.close();
			} catch (Throwable e) {
				Util.log(e);
			}
		} catch (Throwable e1) {
			Util.log(e1);
		}
        logs.setText(text);
	}
}
