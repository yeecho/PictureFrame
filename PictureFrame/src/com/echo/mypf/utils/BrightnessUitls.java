package com.echo.mypf.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.Window;
import android.view.WindowManager;

public class BrightnessUitls{
    public static final int ACTIVITY_BRIGHTNESS_AUTOMATIC = -1;
    public static final int SCREEN_BRIGHTNESS_MODE_AUTOMATIC = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
    public static final int SCREEN_BRIGHTNESS_MODE_MANUAL = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
    public static final int SCREEN_BRIGHTNESS_DEFAULT = 75;
    public static final int MAX_BRIGHTNESS = 100;
    public static final int MIN_BRIGHTNESS = 0;
    private static final int mMaxBrighrness = 255;
    private static final int mMinBrighrness = 10;

    // 当前系统调节模式
    private boolean sysAutomaticMode;
    // 当前系统亮度值
    private int sysBrightness;

    private Context context;

    private BrightnessUitls(Context context, int sysBrightness,
            boolean sysAutomaticMode)
    {
        this.context = context;
        this.sysBrightness = sysBrightness;
        this.sysAutomaticMode = sysAutomaticMode;
    }

   
    public static BrightnessUitls Builder(Context context)
    {
        int brightness;
        boolean automaticMode;
        try
        {
            // 获取当前系统亮度值
            brightness = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
            // 获取当前系统调节模式
            automaticMode = Settings.System.getInt(
                    context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        }
        catch (SettingNotFoundException e)
        {
            return null;
        }

        return new BrightnessUitls(context, brightness, automaticMode);
    }

   
    public boolean getSystemAutomaticMode()
    {
        return sysAutomaticMode;
    }

   
    public int getSystemBrightness()
    {
        return sysBrightness;
    }

   
    public void setMode(int mode)
    {
        if (mode != SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                && mode != SCREEN_BRIGHTNESS_MODE_MANUAL)
            return;

        sysAutomaticMode = mode == SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
    }

   
    public static void setBrightness(ContentResolver resolver,int brightness)
    {
        int mid = mMaxBrighrness - mMinBrighrness;
        int bri = (int) (mMinBrighrness + mid * ((float) brightness)
                / MAX_BRIGHTNESS);

//        ContentResolver resolver = context.getContentResolver();
        Settings.System
                .putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, bri);
    }

   
    public static void brightnessPreview(Activity activity, float brightness)
    {
        Window window = activity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness;
        window.setAttributes(lp);
    }

   
    public static void brightnessPreviewFromPercent(Activity activity,
            float percent)
    {
        float brightness = percent + (1.0f - percent)
                * (((float) mMinBrighrness) / mMaxBrighrness);
        brightnessPreview(activity, brightness);
    }

}
