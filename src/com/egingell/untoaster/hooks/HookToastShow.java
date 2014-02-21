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

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import com.egingell.untoaster.MySettings;
import com.egingell.untoaster.Util;
import static com.egingell.untoaster.Util.splitPattern;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookToastShow extends XC_MethodReplacement {
	private MySettings pAll, appSettings, settings;
    private final XC_LoadPackage.LoadPackageParam mParam;
    private final HashMap<String,Pattern> patternCache = new HashMap<String,Pattern>();
    String packageName;
    public HookToastShow(XC_LoadPackage.LoadPackageParam packageParam) {
        this.mParam = packageParam;
		packageName = mParam.packageName;//.appInfo.packageName;
		settings = new MySettings(packageName);
        pAll = new MySettings("all");
    	appSettings = new MySettings("com.egingell.untoaster");
    }

	@Override
    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
    	boolean show = true;
    	Toast t = (Toast) param.thisObject;
    	try {
    		appSettings.reload();
    		settings.reload();
    		pAll.reload();
			String blocked = "allowed";
			View view = t.getView();
			// Friendly App Name
			String appName = view.getContext().getPackageManager().getApplicationLabel(mParam.appInfo).toString();
			
			// Package Name.
			ArrayList<String> list = new ArrayList<String>();
			int i = 0;
			try {
				LinearLayout lv = ((LinearLayout) view);
				do {
					try {
						list.add(splitPattern.matcher(((TextView) lv.getChildAt(i++)).getText().toString()).replaceAll(" ").trim());
					} catch (ClassCastException e) {
						Util.log("Can't find TextView for " + lv.toString());
					}
				} while (lv.getChildCount() > i);
			} catch (Throwable e) {
				Util.log("Unable to find a layout for " + appName);
			}
			String all = pAll.get("content", "NA").trim();
			String fPackage = settings.get("content", "NA").trim();
	 		String patterns = "";
	 		
	 		if (! fPackage.equals("NA")) {
	 			if (fPackage.equals("")) {
	 				fPackage = ".*";
	 			}
		 		if (! all.equals("NA")) {
		 			patterns += all + "\n" + fPackage;
		 		} else {
		 			patterns += fPackage;
		 		}
	 		} else if (! all.equals("NA")) {
	 			patterns += all;
	 		}
	 		for (String content : list) {
	 			if (! patterns.trim().equals("NA") && ! patterns.trim().equals("")) {
		 			for (String s : splitPattern.split(patterns)) {
						if (filter(s, content)) {
							blocked = "blocked";
							show = false;
						}
		 			}
		 		}
		 		Util.log(packageName + "#show (" + appName + ")\n\thaystack: " + content + "\n\tneedles: " + all + "<=>" + fPackage + "\n\t: " + blocked);
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
	   Util.log("UnToaster#filter: checking\n\tpattern " + needle + "\n\tin " + haystack + "\n\tresult " + (b ? "true" : "false"));
	   return b;
   }
}
