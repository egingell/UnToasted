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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ListView;

public class UnToaster extends ListActivity {
	public Map<String,?> prefsMap;
	private ArrayList<String> mStrings;
	private String currentFile = "";
	private EditText editPattern, fileName;
	private Button saveButton, resetButton, cancelButton, deleteButton;
	ArrayAdapter<String> adapter;
	ListView lv;
    final Runnable saveAction = new Runnable() {
		@Override
		public void run() {
			saveButton.performClick();
		}
	};
	@TargetApi(Build.VERSION_CODES.FROYO)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
		File ignoresFileDir = new File(Util.ignoresDir);
		lv = (ListView) findViewById(android.R.id.list);
		saveButton = (Button) findViewById(R.id.saveButton);
		resetButton = (Button) findViewById(R.id.resetButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		deleteButton = (Button) findViewById(R.id.deleteButton);
		editPattern = (EditText) findViewById(R.id.editPattern);
		fileName = (EditText) findViewById(R.id.fileName);
		try {
			populateList();
		} catch (Throwable e) {
			Util.log(e);
		}
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings);
		ignoresFileDir.mkdir();
        // Use the built-in layout for showing a list item with a single
        // line of text whose background is changes when activated.
        setListAdapter(adapter);
        lv.setTextFilterEnabled(true);
        
        // Tell the list view to show one checked/activated item at a time.
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        // Start with first item activated.
        // Make the newly clicked item the currently selected one.
        lv.setItemChecked(0, true);
        
        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				// Make the newly clicked item the currently selected one.
		    	TextView tv = (TextView) v;
	    		Log.e("UnToaster", "Clicked position " + position + " with ID " + id + ".");
		    	try {
		    		currentFile = tv.getText().toString();
		    	} catch (NullPointerException e) {
		    		Util.log(e);
		    	}
		    	fileName.setText(currentFile);
		    	try {
					String info = Util.readFromFile(new File(Util.extSdCard + "/" + Util.ignoresDir + "/" + currentFile));
					editPattern.setText(info);
				} catch (Throwable e) {
					Util.log(e);
				}
				cancelButton.setEnabled(true);
				deleteButton.setEnabled(true);
				resetButton.setEnabled(false);
				saveButton.setEnabled(false);
		        lv.setItemChecked(position, true);
			}
        });
        resetButton.setEnabled(false);
		saveButton.setEnabled(false);
        cancelButton.setEnabled(false);
		deleteButton.setEnabled(false);
        resetButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (currentFile.trim().equals("")) {
					return;
				}
				try {
					editPattern.setText(Util.readFromFile(Util.extSdCard + "/" + Util.ignoresDir + "/" + currentFile));
				} catch (Throwable e) {
					Util.log(e);
				}
				fileName.setText(currentFile);
				resetButton.setEnabled(false);
				saveButton.setEnabled(false);
				fileName.removeCallbacks(saveAction);
				editPattern.removeCallbacks(saveAction);
			}
		});
        cancelButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
        saveButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String fName = fileName.getText().toString().trim();
				if (fName.equals("")) {
					return;
				}
				try {
					Util.writeToFile(Util.extSdCard + "/" + Util.ignoresDir + "/" + fName, editPattern.getText().toString());
					if (!currentFile.equals(fName)) {
						addAndSort(fName);
						adapter.notifyDataSetChanged();
					}
					currentFile = fName;
				} catch (Throwable e) {
					Util.log(e);
				}
				resetButton.setEnabled(false);
				saveButton.setEnabled(false);
				deleteButton.setEnabled(true);
				fileName.removeCallbacks(saveAction);
				editPattern.removeCallbacks(saveAction);
			}
		});
        deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					File f = new File(Util.extSdCard + "/" + Util.ignoresDir + "/" + currentFile);
					f.delete();
					deleteAndSort(currentFile);
					currentFile = "";
					adapter.notifyDataSetChanged();
				} catch (Throwable e) {
					Util.log(e);
				}
				cancel();
			}
		});
        fileName.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				if (currentFile.equals("")) {
		        	resetButton.setEnabled(false);
		        } else {
		        	resetButton.setEnabled(true);
		        }
				saveButton.setEnabled(true);
				if (fileName.getText().toString().trim().equals("")) {
					deleteButton.setEnabled(false);
					saveButton.setEnabled(false);
				} else {
					deleteButton.setEnabled(true);
					saveButton.setEnabled(true);
				}
				fileName.removeCallbacks(saveAction);
				editPattern.removeCallbacks(saveAction);
				fileName.postDelayed(saveAction, 5000);
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });
        editPattern.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
		        cancelButton.setEnabled(true);
		        if (currentFile.equals("")) {
		        	resetButton.setEnabled(false);
		        } else {
		        	resetButton.setEnabled(true);
		        }
				saveButton.setEnabled(true);
				if (fileName.getText().toString().trim().equals("")) {
					deleteButton.setEnabled(false);
					saveButton.setEnabled(false);
				} else {
					deleteButton.setEnabled(true);
					saveButton.setEnabled(true);
				}
				fileName.removeCallbacks(saveAction);
				editPattern.removeCallbacks(saveAction);
				editPattern.postDelayed(saveAction, 5000);
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });
    }
	private void cancel() {
		try {
			editPattern.setText("");
			fileName.setText("");
		} catch (Throwable e) {
			Util.log(e);
		}
		fileName.removeCallbacks(saveAction);
		editPattern.removeCallbacks(saveAction);
        cancelButton.setEnabled(false);
		deleteButton.setEnabled(false);
		resetButton.setEnabled(false);
		saveButton.setEnabled(false);
	}
	private void addAndSort(String item) {
		mStrings.add(item);
		sort();
	}
	private void deleteAndSort(String item) {
		mStrings.remove(item);
		sort();
	}
	private void sort() {
		Collections.sort(mStrings, String.CASE_INSENSITIVE_ORDER);
		// debug
		int i = 0;
		for (String s : mStrings) {
			Util.log(s + " is at position " + (i++) + ".");
		}
	}
    private void populateList() throws Throwable {
    	try {
		   	mStrings = Util.readDirectory(Util.extSdCard + "/" + Util.ignoresDir);
	    	sort();
		} catch (Throwable e) {
			Util.log(e);
	    }
    }
}