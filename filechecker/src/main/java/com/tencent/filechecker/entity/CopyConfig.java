package com.tencent.filechecker.entity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by haoozhou on 2016/5/19.
 */
public class CopyConfig {

    public boolean EnableQuickCopy;
    public boolean DoCopy;
    public boolean DoCheck;

    //车机本地存储路径
    public String PREFIX_NATIVE_EXTERNAL = "";

    //U盘路径
    public String PREFIX_USB_EXTERNAL = "";

    public CopyConfig() {
        super();
        EnableQuickCopy = false;
        DoCopy = true;
        DoCheck = true;
        PREFIX_USB_EXTERNAL = "/mnt/usb/sda1/8_4/data";//FileUtils.PREFIX_NATIVE_EXTERNAL;
        PREFIX_NATIVE_EXTERNAL = "/storage/extsd";//FileUtils.PREFIX_SDCARD_EXTERNAL;
    }

    public static CopyConfig defaultConfig() {
        return new CopyConfig();
    }

    public void loadConfig(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        PREFIX_USB_EXTERNAL = sp.getString("CopyConfig-PREFIX_USB_EXTERNAL", "/mnt/usb/sda1/8_4/data");
        PREFIX_NATIVE_EXTERNAL = sp.getString("CopyConfig-PREFIX_NATIVE_EXTERNAL", "/storage/extsd");
        EnableQuickCopy = sp.getBoolean("CopyConfig-EnableQuickCopy", false);
        DoCopy = sp.getBoolean("CopyConfig-DoCopy", true);
        DoCheck = sp.getBoolean("CopyConfig-DoCheck", true);
    }

    public void saveConfig(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("CopyConfig-PREFIX_USB_EXTERNAL", PREFIX_USB_EXTERNAL);
        editor.putString("CopyConfig-PREFIX_NATIVE_EXTERNAL", PREFIX_NATIVE_EXTERNAL);
        editor.putBoolean("CopyConfig-EnableQuickCopy", EnableQuickCopy);
        editor.putBoolean("CopyConfig-DoCopy", DoCopy);
        editor.putBoolean("CopyConfig-DoCheck", DoCheck);
        editor.commit();
    }
}
