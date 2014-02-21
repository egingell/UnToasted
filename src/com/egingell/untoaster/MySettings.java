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
 */

package com.egingell.untoaster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;


public class MySettings {
	
	/** Private properties. */
	private XSharedPreferences xSharedPreferences = null;
    private SharedPreferences sharedPreferences = null;
	private Context mContext = null;
	
	/** Constructors. */
	
	/**
	 * Make a new instance using XSharedPreferences.
	 * @param name - The file name to be read from.
	 */
    public MySettings(String name) {
    	try {
	    	xSharedPreferences = new XSharedPreferences("com.egingell.untoaster", name);
	    	xSharedPreferences.makeWorldReadable();
    	} catch (Throwable e) {
    		XposedBridge.log(e);
    	}
    }
    /**
     * Make a new instance using SharedPreferences.
     * @param context - The app context from which to retrieve the SharedPreferences.
     * @param name - The file name to read/write from.
     */
	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	public MySettings(Context context, String name) {
    	mContext = context;
    	sharedPreferences = mContext.getSharedPreferences(name, Context.MODE_WORLD_READABLE);
    }
    
    /** Public methods */
    
    /**
     * Used to add or change 'name' in the preference file.
     * @param name - The property name.
     * @param value - The value of said property.
     * @throws Throwable - Thrown if attempting to write to the file when it's either open by XSharedPreferences or not open.
     */
    public void put(String name, String value) throws Throwable {
    	if (sharedPreferences == null) {
    		throw new Throwable("Readonly or unavailable");
    	}
    	sharedPreferences.edit().putString(name, value);
    }
    /**
     * Used to add or change 'name' in the preference file.
     * @param name - The property name.
     * @param value - The value of said property.
     * @throws Throwable - Thrown if attempting to write to the file when it's either open by XSharedPreferences or not open.
     */
    public void put(String name, boolean value) throws Throwable {
    	if (sharedPreferences == null) {
    		throw new Throwable("Readonly or unavailable");
    	}
    	sharedPreferences.edit().putBoolean(name, value);
    }
    /**
     * Used to get 'name' from the preference file.
     * @param name - The property name.
     * @param value - The value of said property.
     * @throws Throwable - Thrown if neither XSharedPreferences nor SharedPreferences has the file open.
     */
    public String get(String name, String defValue) throws Throwable {
    	if (xSharedPreferences != null) {
    		reload();
    		return xSharedPreferences.getString(name, defValue);
    	} else if (sharedPreferences != null) {
    		return sharedPreferences.getString(name, defValue);
    	} else throw new Throwable("Can't get pref, " + name);
    }
    /**
     * Used to get 'name' from the preference file.
     * @param name - The property name.
     * @param value - The value of said property.
     * @throws Throwable - Thrown if neither XSharedPreferences nor SharedPreferences has the file open.
     */
    public boolean get(String name, boolean defValue) throws Throwable {
    	if (xSharedPreferences != null) {
    		reload();
    		return xSharedPreferences.getBoolean(name, defValue);
    	} else if (sharedPreferences != null) {
    		return sharedPreferences.getBoolean(name, defValue);
    	} else throw new Throwable("Can't get pref, " + name);
    }
    /**
     * Use to get the XSharedPreferences stored in this instance.
     * @return XSharedPreferences xSharedPreferences
     */
    public XSharedPreferences get() {
    	return xSharedPreferences;
    }
    /**
     * Use to get the SharedPreferences stored in this instance.
     * @param context - only used to disambiguate SharedPreferences from XSharedPreferences
     * @return SharedPreferences sharedPreferences
     */
    public SharedPreferences get(Context context) {
    	return sharedPreferences;
    }
    
    /** Private methods. */
    
    /**
     * Used internally to reload the shared prefs file.
     */
    public void reload() {
		if (xSharedPreferences != null) {
			xSharedPreferences.reload();
		}
    }
}
