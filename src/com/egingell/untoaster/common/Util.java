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

package com.egingell.untoaster.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import de.robv.android.xposed.XposedBridge;

import android.os.Environment;
import android.util.Log;

public class Util {
	final public static HashMap<String,MySettings> prefs = new HashMap<String,MySettings>();
	final public static String[] commonPrefs = new String[] {
		"all", "com.egingell.untoaster",
	};

	public static String extSdCard;
	final public static String ignoresDir = "UnToaster";
	
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
    		Log.e(ignoresDir, null, e);
    	}
    }
    public static void log(String e) {
    	try {
    		XposedBridge.log(e);
    	} catch (Throwable ignored) {
    		Log.e(ignoresDir, e);
    	}
    }
    
    static public int NO_LIMIT = 0;
    static private int MAX_LINES = 95;
    public static void logToApp(String addText) {
    	logToApp("all", addText);
    }
    public static void logToApp(String fileName, String addText) {
		try {
			File out = new File(Environment.getDataDirectory(), "data/com.egingell.untoaster/logs/" + fileName + ".log");
			final File logScript = new File(Environment.getDataDirectory(), "data/com.egingell.untoaster/cache/makeLog.sh");
	    	try {
				out.getParentFile().getParentFile().setWritable(true, false); // data/com.egingell.untoaster
				out.getParentFile().setWritable(true, false); // data/com.egingell.untoaster/cache
				out.setWritable(true, false); // data/com.egingell.untoaster/cache/${fileName}.log
		    	out.getParentFile().mkdirs();
		    	try {
		    		out.createNewFile();
		    	} catch (Throwable e) {}
		    	try {
		    		logScript.createNewFile();
			    	Runtime.getRuntime().equals("chmod 755 " + logScript.getPath());
			    	String filePath = out.getPath(), text = "D=$(tail -n " + MAX_LINES + " \"" + filePath + "\");\necho \"$D" + addText + "\" > \"" + filePath + "\";";
					final FileOutputStream fs = new FileOutputStream(logScript);
					fs.write(text.getBytes());
					fs.close();
			    	Runtime.getRuntime().exec(logScript.getPath());
			    	logScript.delete();
		    	} catch (Throwable e) {}
		    	//logScript.setExecutable(true, false);
			} catch (Throwable e) {
				log(e);
			}
	    	/*
			addText += "\n" + getLogedToApp(MAX_LINES);
			FileOutputStream fs = new FileOutputStream(out);
			fs.write(addText.getBytes());
			fs.close();
			*/
		} catch (Throwable e) {
			log(e);
		}
    }
    public static String getLoggedToApp() {
    	return getLoggedToApp("all", NO_LIMIT);
    }
    public static String getLoggedToApp(String fileName) {
    	return getLoggedToApp(fileName, NO_LIMIT);
    }
    public static String getLoggedToApp(String fileName, int limit) {
    	String ret = "";
    	if (limit == NO_LIMIT) {
    		limit = 97;
    	}
		try {
	    	final File out = new File(Environment.getDataDirectory(), "data/com.egingell.untoaster/logs/"+ fileName + ".log");
	    	out.getParentFile().mkdirs();
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
			log(e);
		}
		return ret;
    }
    static public String readFromFile(String file) throws Throwable {
    	return readFromFile(new File(file));
    }
    static public String readFromFile(File file) throws Throwable {
    	String ret = "";
    	if (file.isDirectory()) {
    		log(file.getPath());
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
}
