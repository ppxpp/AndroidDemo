package com.tencent.filechecker.entity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by haoozhou on 2016/5/19.
 */
public class DetectConfig {

    //车机本地存储路径
    //public String PREFIX_NATIVE_EXTERNAL = "";

    //U盘路径
    public String PREFIX_USB_EXTERNAL = "";

    public DetectConfig() {
        PREFIX_USB_EXTERNAL = "/mnt/usb/sda1/8_4/data";//FileUtils.PREFIX_NATIVE_EXTERNAL;
        //PREFIX_NATIVE_EXTERNAL = "/storage/extsd";//FileUtils.PREFIX_SDCARD_EXTERNAL;
    }

    public static DetectConfig defaultConfig() {
        return new DetectConfig();
    }

    public void loadConfig(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        PREFIX_USB_EXTERNAL = sp.getString("DetectConfig-PREFIX_USB_EXTERNAL", PREFIX_USB_EXTERNAL);
        //PREFIX_NATIVE_EXTERNAL = sp.getString("DetectConfig-PREFIX_NATIVE_EXTERNAL", "/storage/extsd");
    }

    public void saveConfig(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("DetectConfig-PREFIX_USB_EXTERNAL", PREFIX_USB_EXTERNAL);
        //editor.putString("DetectConfig-PREFIX_NATIVE_EXTERNAL", PREFIX_NATIVE_EXTERNAL);
        editor.commit();
    }
}
