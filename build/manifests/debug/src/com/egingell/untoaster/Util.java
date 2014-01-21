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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;

public class Util {

    private Util() {}

    static public void readFromFile(ArrayList<String> ignores, String fName, boolean emptyFile, boolean fileExists) throws Throwable {
	    try {
	    	File tFile = new File(fName);
			emptyFile = tFile.isFile() && tFile.length() <= 1;
			fileExists = tFile.isFile() && tFile.exists();
			if (fileExists && ! emptyFile) {
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
	        XposedBridge.log(e);
	    }
    }
    static public String readFromFile(File file) throws Throwable {
    	String ret = "";
	    try {
	    	FileInputStream is = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			XposedBridge.log("UnToaster: reading from file '" + file.getPath() + "'");
			String s = reader.readLine();
			while (s != null) {
				XposedBridge.log("\t" + s);
				ret = ret + s + "\n";
				s = reader.readLine();
			}
			reader.close();
			is.close();
		} catch (Throwable e) {
	        XposedBridge.log(e);
	    }
    	return ret;
    }
}
