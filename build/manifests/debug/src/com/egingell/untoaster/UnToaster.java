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

package com.egingell.untoaster;

import java.io.File;
import java.util.Map;

import de.robv.android.xposed.XSharedPreferences;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
//import de.robv.android.xposed.XposedBridge;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;

public class UnToaster extends Activity {
	public XSharedPreferences prefs;
	public Map<String,?> prefsMap;
	public String[] listViewItems, listViewItemsBool;
	ArrayAdapter<String> adapter;
	@TargetApi(Build.VERSION_CODES.FROYO)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        String extSdCard = Environment.getExternalStorageDirectory().getPath();
		File ignoresFileDir = new File(extSdCard, "UnToaster");
		ignoresFileDir.mkdir();
    }
	/*
    public void XposedLog(View v) throws Throwable {
    	try {
    		Button button = (Button) v;
	    	setContentView(R.layout.layout);
	    	TextView tv = (TextView) findViewById(R.id.text);
	    	if (button.getText().equals(getText(R.string.xposedlog))) {
	    		button.setText(getText(R.string.back));
	    		try {
	    			tv.setText(Util.readFromFile(new File(getFilesDir().getPath() + "/data/de.robv.android.xposed.installer/log/debug.log")));
	    		} catch (Throwable e) {
	    			tv.setText(getText(R.string.nofile));
	    			XposedBridge.log(e);
	    		}
	    	} else {
	    		button.setText(getText(R.string.xposedlog));
		    	tv.setText(getText(R.string.sorry));
	    	}
    	} catch (Throwable e) {
	        XposedBridge.log(e);
	    }
    }
    */
}
