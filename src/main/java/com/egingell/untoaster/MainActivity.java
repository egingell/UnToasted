package com.egingell.untoaster;

import android.app.*;
import android.os.*;
import de.robv.android.xposed.*;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import android.view.*;
import android.widget.*;
import java.io.*;
import android.content.pm.*;
import android.content.res.*;
import org.w3c.dom.*;

class MainActivity extends Activity implements IXposedHookZygoteInit {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {

        }
    }

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (lpparam.packageName.equals("android.widget.Toast")) {
			XposedBridge.log("Loaded app: " + lpparam.packageName);
			findAndHookMethod("android.widget.Toast", lpparam.classLoader, "setText", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					XposedBridge.log((String) param.args[0]);
				}
			});
		}
	}

	private void filteredShow(String packageName, String toastText) {

	}
}
