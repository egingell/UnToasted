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

package com.egingell.untoaster.activities;

import java.io.File;

import com.egingell.untoaster.R;
import com.egingell.untoaster.common.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

@SuppressLint("NewApi")
public class LogsActivity extends Activity {
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.logs_layout);
        EditText logs = (EditText) findViewById(R.id.logs);
        String text = "";
        final File logsDir = this.getDir("logs", Activity.MODE_PRIVATE);
        for (File f : logsDir.listFiles()) {
        	try {
        		text += f.getName() + "\n";
				text += Util.readFromFile(f) + "\n";
        		text += "\n";
			} catch (Throwable e) {
				Util.log(e);
			}
        }
        logs.setText(text);
	}
}
