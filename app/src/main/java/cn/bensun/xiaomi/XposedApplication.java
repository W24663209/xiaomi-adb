package cn.bensun.xiaomi;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedApplication implements IXposedHookLoadPackage {
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.android.settings")) {
            XposedHelpers.findAndHookMethod(Application.class, "attach", new Object[]{Context.class, new XC_MethodHook() { // from class: con.bensun.readassistant.Xposed.1
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    XposedApplication.this.settings(lpparam.classLoader);
                }
            }});
            Log.e("加载xposed2", "开始");
            Log.e("packageName", lpparam.packageName);
            Log.e("appInfo", String.valueOf(lpparam.appInfo));
            Log.e("壳的classLoader", String.valueOf(lpparam.classLoader));
            Log.e("isFirstApplication", String.valueOf(lpparam.isFirstApplication));
        }else if (lpparam.packageName.equals("com.miui.securitycenter")) {
            XposedHelpers.findAndHookMethod(Application.class, "attach", new Object[]{Context.class, new XC_MethodHook() { // from class: con.bensun.readassistant.Xposed.1
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    XposedApplication.this.securitycenter(lpparam.classLoader);
                }
            }});
            Log.e("加载xposed2", "开始");
            Log.e("packageName", lpparam.packageName);
            Log.e("appInfo", String.valueOf(lpparam.appInfo));
            Log.e("壳的classLoader", String.valueOf(lpparam.classLoader));
            Log.e("isFirstApplication", String.valueOf(lpparam.isFirstApplication));
        }
    }

    private void settings(ClassLoader loader) throws Exception {
        {
            //解决垃圾小米无法adb触摸
            Class<?> clazz = loader.loadClass("com.android.security.AdbUtils");
            XposedHelpers.findAndHookMethod(clazz, "isInputEnabled", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object result = param.getResult();
                    if (result.equals(false)) {
                        Method setInputEnabled = clazz.getDeclaredMethod("setInputEnabled",boolean.class);
                        setInputEnabled.setAccessible(true);
                        setInputEnabled.invoke(null,true);
                    }
                }
            });
        }
    }

    private void securitycenter(ClassLoader loader) throws Exception {

        {
            //解决垃圾小米无法adb安装app
            Class<?> clazz = loader.loadClass("com.miui.permcenter.install.d");
            XposedHelpers.findAndHookMethod(clazz, "fq", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(true);
                }
            });
            XposedHelpers.findAndHookMethod(clazz, "isEnabled", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(false);
                }
            });
        }
    }
}