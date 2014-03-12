/**
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

package com.egingell.untoaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {

	//private static String PATH = null;
	private HashMap<String,MySettings> prefs = new HashMap<String,MySettings>();
	private XposedHook() {}

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
		prefs.put(loadPackageParam.packageName, new MySettings(loadPackageParam.packageName));
		prefs.get(loadPackageParam.packageName).reload();
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
    	XC_MethodReplacement hook = new XC_MethodReplacement() {
			final private Pattern splitPattern = Pattern.compile("[\r\n]+");
		    private final HashMap<String,Pattern> patternCache = new HashMap<String,Pattern>();
		    private boolean filter(String needle, String haystack) {
			    if (! patternCache.containsKey(needle)) {
			 	   patternCache.put(needle, Pattern.compile(needle));
			    }
			    boolean b = patternCache.get(needle).matcher(haystack).find();
			    //Util.log("UnToaster#filter: checking\n\tpattern " + needle + "\n\tin " + haystack + "\n\tresult " + (b ? "true" : "false"));
			    return b;
		    }
			@Override
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
				LinearLayout layout = (LinearLayout) ((Toast) param.thisObject).getView();
				Context context = layout.getContext();
				TextView view = (TextView) layout.findViewById(android.R.id.message);
		    	boolean show = true;
				try {
					final PackageManager pm = context.getPackageManager();
					String packageName = context.getPackageName();
					String appName = pm.getApplicationLabel(context.getApplicationInfo()).toString();
					ArrayList<String> list = new ArrayList<String>();
					list.add(splitPattern.matcher(view.getText().toString()).replaceAll(" ").trim());
					int i = 0;
					try {
						do {
							try {
								list.add(splitPattern.matcher(((TextView) layout.getChildAt(i++)).getText().toString()).replaceAll(" ").trim());
							} catch (ClassCastException e) {}
						} while (layout.getChildCount() > i);
					} catch (Throwable e) {
						Util.log("Unable to find a layout for " + packageName + " (" + appName + ")");
					}
					
					MySettings settings = prefs.get(packageName), pAll = new MySettings("all"), appSettings = new MySettings("com.egingell.untoaster");
					
		    		appSettings.reload();
		    		settings.reload();
		    		pAll.reload();
		    		
					String blocked = "allowed";
					
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
				 		Util.log(packageName + "#show (" + appName + ")\n\thaystack: " + content + "\n\tneedles (all): " + all + "\n\tneedles (" + packageName + "): " + fPackage + "\n\ttoast: " + blocked);
			 		}
				} catch (Throwable e) {
					Util.log(e);
				}
		        if (show) {
		        	return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
		        } else {
		        	return null;
		        }
			}
		};
    	XposedHelpers.findAndHookMethod(Toast.class, "show", hook);
        //PATH = startupParam.modulePath;
    }
}
