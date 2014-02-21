package com.egingell.untoaster;

import java.io.File;
import java.net.URI;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class debug implements IXposedHookZygoteInit {
	private String tPathName = "com.egingell.untoaster";
	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		try {
			XposedBridge.hookAllConstructors(File.class, new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) {
					String packageName = param.thisObject.getClass().getPackage().getName();
					String file = null, name = null;
					if (param.args[0] instanceof File) {
						file = ((File) param.args[0]).getPath();
						name = (String) param.args[1];
					} else if (param.args[0] instanceof String && param.args[1] instanceof String) {
						file = (String) param.args[0];
						name = (String) param.args[1];
					} else if (param.args[0] instanceof String) {
						file = (String) param.args[0];
					} else if (param.args[0] instanceof URI) {
						file = ((URI) param.args[0]).getPath();
					}
					
					if (file.contains(tPathName) || (name.contains(tPathName) && name != null)) {
						XposedBridge.log(packageName + ": " + file + File.separator + name);
					} else if (file.contains(tPathName) && name == null) {
						XposedBridge.log(packageName + ": " + file);
					}
				}
			});
		} catch (Throwable e) {}
	}
}
