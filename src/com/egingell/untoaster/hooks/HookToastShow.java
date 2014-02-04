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
 *     
 *     Xposed log: tail -f -n 100 /data/data/de.robv.android.xposed.installer/log/debug.log  >/sdcard/UnToaster.log
 *     Logcat: logcat | grep "UnToaster"
 */

package com.egingell.untoaster.hooks;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import com.egingell.untoaster.Util;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookToastShow extends XC_MethodReplacement {

    private final XC_LoadPackage.LoadPackageParam mParam;
    private final HashMap<String,Pattern> patternCache = new HashMap<String,Pattern>();
    
    public HookToastShow(XC_LoadPackage.LoadPackageParam packageParam) {
        this.mParam = packageParam;
        //context = c;
    }

    @SuppressWarnings("unchecked")
	@TargetApi(Build.VERSION_CODES.FROYO)
	@Override
    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
    	boolean show = true;
    	Toast t = (Toast) param.thisObject;
    	try {
			String blocked = "allowed";
			View view = t.getView();
			// Friendly App Name
			String appName = view.getContext().getPackageManager().getApplicationLabel(mParam.appInfo).toString();
			
			// Package Name.
			String packageName = mParam.appInfo.packageName;
			
			ArrayList<String> list = new ArrayList<String>();
			do {
				int i = 0;
				LinearLayout lv = ((LinearLayout) view);
				do {
					try {
						list.add(((TextView) lv.getChildAt(i++)).getText().toString().replace("\n", " ").trim());
					} catch (ClassCastException e) {
						// It's not a valid String. Skip it.
					}
				} while (lv.getChildCount() > i);
			} while (false);
			
			File ignoresFileDir = new File(Util.extSdCard, Util.ignoresDir);
			ignoresFileDir.mkdir();
			ArrayList<String> ignores = new ArrayList<String>(), tmp = new ArrayList<String>();
			boolean emptyFile = false, fileExists= false;
			if (! emptyFile && Util.readFromFile(tmp, ignoresFileDir + "/all", emptyFile, fileExists)) {
				ignores = (ArrayList<String>) tmp.clone();
			}
 			Util.readFromFile(ignores, ignoresFileDir + "/" + packageName, emptyFile, fileExists);

			for (String content : list) {
		 		for (String s : ignores) {
					if (filter(s, content)) {
						//t.setText(content + "\nUnToaster: blocking.");
						blocked = "blocked";
						show = false;
					}
				}
		 		if (fileExists && emptyFile) {
					//t.setText(content + "\nUnToaster: blocking.");
					blocked = "blocked";
					show = false;
		 		}
		 		Util.log("UnToaster: " + packageName + " (" + appName + ")\n\t: " + content + "\n\t: " + blocked);
    		}
	    } catch (Throwable e) {
	    	Util.log(e);
	    }
        if (show) {
        	return XposedBridge.invokeOriginalMethod(param.method, t, param.args);
        } else {
        	return null;
        }
   }
   private boolean filter(String needle, String haystack) {
	   if (! patternCache.containsKey(needle)) {
		   patternCache.put(needle, Pattern.compile(needle));
	   }
	   boolean b = patternCache.get(needle).matcher(haystack).find();
	   Util.log("UnToaster: checking\n\tpattern " + needle + "\n\tin " + haystack + "\n\tresult " + (b ? "true" : "false"));
	   return b;
   }
}