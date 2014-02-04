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

// This is not yet implemented. It will eventually replace flat files.
package com.egingell.untoaster;

import java.util.HashMap;

import de.robv.android.xposed.XSharedPreferences;

public class Prefs {
	private HashMap<String,XSharedPreferences> prefs = null;
	public XSharedPreferences getPrefs() {
    	prefs.put("shared_prefs", new XSharedPreferences("com.egingell.untoaster", ".prefs"));
    	return prefs.get("shared_prefs");
	}
    public XSharedPreferences getPrefs(String fName) { // General
    	prefs.put(fName, new XSharedPreferences("com.egingell.untoaster", fName));
    	return prefs.get(fName);
    }
    
    public Object getPref(String fName, String key) {
    	try {
    		return prefs.get(fName).getBoolean(key, false);
    	} catch (Throwable ignored) {}
    	try {
    		return prefs.get(fName).getLong(key, 0);
    	} catch (Throwable ignored) {}
    	try {
    		return prefs.get(fName).getFloat(key, 0);
    	} catch (Throwable ignored) {}
    	try {
    		return prefs.get(fName).getString(key, "");
    	} catch (Throwable ignored) {}
    	return null;
    }
    
    public boolean putPref(String fName, String key, boolean value) {
    	return prefs.get(fName).edit().putBoolean(key, value).commit();
    }
    public boolean putPref(String fName, String key, float value) {
    	return prefs.get(fName).edit().putFloat(key, value).commit();
    }
    public boolean putPref(String fName, String key, long value) {
    	return prefs.get(fName).edit().putLong(key, value).commit();
    }
    public boolean putPref(String fName, String key, String value) {
    	return prefs.get(fName).edit().putString(key, value).commit();
    }
}
