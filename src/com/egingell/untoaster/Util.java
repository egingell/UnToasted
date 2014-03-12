/*
 * This file is part of UnToasted.
 *
 * Copyright 2014 Eric Gingell (c)
 *
 *     UnToaster is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     UnToaster is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with UnToasted.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.egingell.untoaster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import de.robv.android.xposed.XposedBridge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

public class Util {
	public static String extSdCard;
	public static String prefsFileName = null;
	final public static String ignoresDir = "UnToaster";
	final static public HashMap<String,SharedPreferences> prefsMap = new HashMap<String,SharedPreferences>();
	
    public static void init() {
		try {
			extSdCard = Environment.getExternalStorageDirectory().getPath();
		} catch (Throwable e) {
			log(e);
		}
    }
    
    public static void log(Throwable e) {
    	try {
    		XposedBridge.log(e);
    	} catch (Throwable ignored) {
    		Log.e("UnToaster", null, e);
    	}
    }
    public static void log(String e) {
    	try {
    		XposedBridge.log(e);
    	} catch (Throwable ignored) {
    		Log.e("UnToaster", e);
    	}
    }
    
    static public boolean readFromFile(final ArrayList<String> ignores, String fName, boolean emptyFile, boolean fileExists) throws Throwable {
	    try {
	    	File tFile = new File(fName);
			emptyFile = tFile.isFile() && tFile.length() <= 1;
			fileExists = tFile.isFile() && tFile.exists();
			if (!fileExists) {
				return fileExists;
			}
			if (! emptyFile) {
				FileInputStream is = new FileInputStream(tFile);
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String line = reader.readLine();
				while (line != null) {
					ignores.add(line.trim());
					line = reader.readLine();
				}
				reader.close();
			}
		} catch (Throwable e) {
			log(e);
	    }
	    return fileExists;
    }
    static public ArrayList<String> readDirectory(String dir) throws Throwable {
    	ArrayList<String> ret = new ArrayList<String>();
    	try {
    		File mDir = new File(dir);
    		int i = 0;
    		for (String file : mDir.list()) {
    			log("Adding " + file + " to ListView at position " + (i++) + ".");
    			ret.add(file);
    		}
		} catch (Throwable e) {
			log(e);
	    }
    	return ret;
    }
    static public void moveDir(File src, File dst) {
		for (File f : src.listFiles()) {
			try {
				writeToFile(dst, readFromFile(f));
				f.delete();
			} catch (Throwable e) {
				log(e);
			}
		}
    }
    static public int NO_LIMIT = 0;
    public static void logToApp(String addText) {
		try {
	    	final File out = new File(Environment.getDataDirectory(), "data/com.egingell.untoaster/cache/log.log");
	    	out.createNewFile();
			addText += "\n" + getLogedToApp(97);
			FileOutputStream fs = new FileOutputStream(out);
			fs.write(addText.getBytes());
			fs.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
    }
    public static String getLogedToApp() {
    	return getLogedToApp(NO_LIMIT);
    }
    public static String getLogedToApp(int limit) {
    	String ret = "";
    	if (limit == NO_LIMIT) {
    		limit = 97;
    	}
		try {
	    	final File out = new File(Environment.getDataDirectory(), "data/com.egingell.untoaster/cache/log.log");
	    	out.createNewFile();
			FileInputStream is = new FileInputStream(out);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line;
			int i = 97;
			while ((line = reader.readLine()) != null && --i > NO_LIMIT) {
				ret += line;
			}
			reader.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return ret;
    }
    static public void writeToFile(String file, String contents) throws Throwable {
    	writeToFile(new File(file), contents);
    }
    static public void writeToFile(File file, String contents) throws Throwable {
    	if (file.isDirectory()) {
    		return;
    	}
    	FileOutputStream fs = new FileOutputStream(file);
    	file.createNewFile();
    	fs.write(contents.getBytes());
    	fs.close();
    }
    static public String readFromFile(String file) throws Throwable {
    	return readFromFile(new File(file));
    }
    static public String readFromFile(File file) throws Throwable {
    	String ret = "";
    	if (file.isDirectory()) {
    		Log.d("UnToaster", file.getPath());
    		return ret;
    	}
	    try {
	    	FileInputStream is = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String s = reader.readLine();
			while (s != null) {
				ret = ret + s + "\n";
				s = reader.readLine();
			}
			reader.close();
			is.close();
		} catch (Throwable e) {
			log(e);
	    }
    	return ret;
    }
    static public void getSharedPreferences(String name) {
    	if (prefsMap.get(name) != null) {
    		return;
    	}
    	MySettings s = new MySettings(name);
    	prefsMap.put(name, s.get());
    }
    static public void getSharedPreferences(Context context, String name) {
    	if (prefsMap.get(name) != null) {
    		return;
    	}
    	MySettings s = new MySettings(context, name);
		prefsMap.put(name, s.get(context));
    }
}
