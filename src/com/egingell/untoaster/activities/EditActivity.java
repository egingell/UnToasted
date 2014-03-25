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
import java.util.ArrayList;
import java.util.Collections;

import com.egingell.untoaster.R;
import com.egingell.untoaster.common.MySettings;
import com.egingell.untoaster.common.Util;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ListView;

public class EditActivity extends ListActivity {
	private ArrayList<String> mStrings = new ArrayList<String>();
	private String currentFile = "";
	private EditText editPattern, fileName;
	private Button saveButton, resetButton, cancelButton, deleteButton;
	public static Context context;
	private static Context mContext;
	ArrayAdapter<String> adapter;
	ListView lv;
    final Runnable saveAction = new Runnable() {
		@Override
		public void run() {
			saveButton.performClick();
		}
	};

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		try {
			Util.init();
			for (String s : Util.commonPrefs) {
				Util.prefs.put(s, new MySettings(this, s));
			}
	        setContentView(R.layout.list_layout);
			//File ignoresFileDir = new File(Util.ignoresDir);
			lv = (ListView) findViewById(android.R.id.list);
			saveButton = (Button) findViewById(R.id.saveButton);
			resetButton = (Button) findViewById(R.id.resetButton);
			cancelButton = (Button) findViewById(R.id.cancelButton);
			deleteButton = (Button) findViewById(R.id.deleteButton);
			editPattern = (EditText) findViewById(R.id.editPattern);
			fileName = (EditText) findViewById(R.id.fileName);
			//TextView logView = (TextView) findViewById(R.id.log);
			
			//logView.setText(Util.getLoggedToApp());
			try {
				currentFile = savedInstanceState.getString("currentFile", "");
				setText();
			} catch (Throwable e) {}
			context = ((ViewGroup) lv.getParent()).getContext();
			
			File ignoresFileDir = new File(Util.extSdCard, Util.ignoresDir);
			if (ignoresFileDir.exists()) {
				for (File f : ignoresFileDir.listFiles()) {
					try {
						Util.prefs.put(f.getName(), new MySettings(this, f.getName()));

						MySettings myAppSettings = Util.prefs.get(getPackageName()), thisPackage = Util.prefs.get(f.getName());
						
						myAppSettings.get(this).edit().putBoolean(f.getName(), true).commit();
						thisPackage.get(this).edit().putString("content", Util.readFromFile(f)).commit();
					} catch (Throwable e) {
						Util.log(e);
					}
					f.delete();
				}
				ignoresFileDir.delete();
			}
			
	        // Use the built-in layout for showing a list item with a single
	        // line of text whose background is changes when activated.
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings);
			populateList();
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
			    		savedInstanceState.putString("currentFile", currentFile);
			    	} catch (NullPointerException e) {
			    		Util.log(e);
			    	}
			    	fileName.setText(currentFile);
			    	try {
			    		setText();
					} catch (Throwable e) {
						Util.log(e);
					}
					//cancelButton.setEnabled(true);
					deleteButton.setEnabled(true);
					resetButton.setEnabled(false);
					saveButton.setEnabled(false);
			        lv.setItemChecked(position, true);
				}
	        });
	        resetButton.setEnabled(false);
			saveButton.setEnabled(false);
	        //cancelButton.setEnabled(false);
			deleteButton.setEnabled(false);
	        resetButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if (currentFile.trim().equals("")) {
						return;
					}
					try {
						setText();
					} catch (Throwable e) {
						Util.log(e);
					}
					resetButton.setEnabled(false);
					saveButton.setEnabled(false);
					//fileName.removeCallbacks(saveAction);
					editPattern.removeCallbacks(saveAction);
				}
			});
	        cancelButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					finish();
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
						Util.prefs.put(fName, new MySettings(mContext, fName));
						MySettings thisPackage = Util.prefs.get(fName), mine = Util.prefs.get("com.egingell.untoaster");
						
						thisPackage.get(mContext).edit().putString("content", editPattern.getText().toString()).commit();
						mine.get(mContext).edit().putBoolean(fName, true).commit();
						
						if (!currentFile.equals(fName)) {
							addAndSort(fName);
							adapter.notifyDataSetChanged();
						}
						currentFile = fName;
						savedInstanceState.putString("currentFile", currentFile);
					} catch (Throwable e) {
						Util.log(e);
					}
					resetButton.setEnabled(false);
					saveButton.setEnabled(false);
					deleteButton.setEnabled(true);
					//fileName.removeCallbacks(saveAction);
					editPattern.removeCallbacks(saveAction);
				}
			});
	        deleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Util.prefs.put(currentFile, new MySettings(mContext, currentFile));
						MySettings thisPackage = Util.prefs.get(currentFile), mine = Util.prefs.get("com.egingell.untoaster");
						
						thisPackage.get(mContext).edit().putString("content", editPattern.getText().toString()).commit();
						mine.get(mContext).edit().putBoolean(currentFile, true).commit();

						thisPackage.get(mContext).edit().clear().commit();
						mine.get(mContext).edit().remove(currentFile).commit();
						
						deleteAndSort(currentFile);
						//currentFile = "";
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
					//fileName.removeCallbacks(saveAction);
					editPattern.removeCallbacks(saveAction);
					//fileName.postDelayed(saveAction, 5000);
				}
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
	        });
	        editPattern.addTextChangedListener(new TextWatcher() {
				@Override
				public void afterTextChanged(Editable arg0) {
			        //cancelButton.setEnabled(true);
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
					//fileName.removeCallbacks(saveAction);
					editPattern.removeCallbacks(saveAction);
					editPattern.postDelayed(saveAction, 5000);
				}
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
	        });
		} catch (Throwable e) {
			Util.log(e);
		}
    }
	private void setText() {
		Util.prefs.put(currentFile, new MySettings(mContext, currentFile));
		MySettings thisPackage = Util.prefs.get(currentFile);
		try {
			editPattern.setText(thisPackage.get("content", ""));
		} catch (Throwable e) {
			Util.log(e);
		}
		fileName.setText(currentFile);
	}
	private void cancel() {
		try {
			editPattern.setText("");
			fileName.setText("");
		} catch (Throwable e) {
			Util.log(e);
		}
		//fileName.removeCallbacks(saveAction);
		editPattern.removeCallbacks(saveAction);
        //cancelButton.setEnabled(false);
		deleteButton.setEnabled(false);
		resetButton.setEnabled(false);
		saveButton.setEnabled(false);
	}
	private void addAndSort(String item) throws Throwable {
		mStrings.add(item);
		sort();
	}
	private void deleteAndSort(String item) throws Throwable {
		mStrings.remove(item);
		sort();
	}
	private void sort() throws Throwable {
		sort(false);
	}
	private void sort(boolean returnEarly) throws Throwable {
		Collections.sort(mStrings, String.CASE_INSENSITIVE_ORDER);
		if (returnEarly) {
			return;
		}
		populateList();
	}
    private void populateList() throws Throwable {
    	try {
		   	//mStrings = Util.readDirectory(Util.extSdCard + "/" + Util.ignoresDir);
    		if (mStrings != null) {
    			mStrings.clear();
    		} else {
    			mStrings = new ArrayList<String>();
    		}
			Util.prefs.put(currentFile, new MySettings(this, currentFile));
			MySettings mine = Util.prefs.get("com.egingell.untoaster");

    		for (String key : mine.get(this).getAll().keySet()) {
    			mStrings.add(key);
    		}
	    	sort(true);
	    	try {
	    		adapter.notifyDataSetChanged();
			} catch (Throwable e) {
				Util.log(e);
		    }
		} catch (Throwable e) {
			Util.log(e);
	    }
    }
}
