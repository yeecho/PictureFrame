package com.echo.mypf.receiver;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import com.echo.mypf.custom.Echo;

public class AlarmOffReceiver extends BroadcastReceiver {

	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("alarm_off_receiver:", "true");
		SharedPreferences sp = context.getSharedPreferences(
				Echo.SHARED_PREFERENCES_NAME, 0);
		String mode_off = sp.getString(Echo.MODE_SCREEN_OFF_SP, "Everyday");
		int currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
		if (currentDayOfWeek == 0) {
			currentDayOfWeek = 7;
		}
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intentOff = new Intent(context, AlarmOffReceiver.class);
		alarm.setExact(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + AlarmManager.INTERVAL_DAY,
				PendingIntent.getBroadcast(context, 1, intentOff, 0));

		if (mode_off.equals("Everyday")) {
			pm.goToSleep(SystemClock.uptimeMillis());
		} else if (mode_off.equals("Weekday")) {
			if (currentDayOfWeek <= 5) {
				Log.e("AlarmOffReceiver", "go");
				pm.goToSleep(SystemClock.uptimeMillis());
			}
		} else if (mode_off.equals("Weekend")) {
			if (currentDayOfWeek >= 6) {
				pm.goToSleep(SystemClock.uptimeMillis());
			}
		}
	}

}
