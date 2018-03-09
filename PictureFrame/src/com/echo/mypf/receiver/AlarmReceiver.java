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
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.echo.mypf.custom.Echo;

public class AlarmReceiver extends BroadcastReceiver {

	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {

		Log.e("alarm_on_receiver:", "true");
		SharedPreferences sp = context.getSharedPreferences(
				Echo.SHARED_PREFERENCES_NAME, 0);
		String mode_on = sp.getString(Echo.MODE_SCREEN_ON_SP, "Everyday");
		int currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
		if (currentDayOfWeek == 0) {
			currentDayOfWeek = 7;
		}
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intentOn = new Intent(context, AlarmReceiver.class);
		alarm.setExact(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + AlarmManager.INTERVAL_DAY,
				PendingIntent.getBroadcast(context, 0, intentOn, 0));
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
		if (mode_on.equals("Everyday")) {
			wl.acquire();
			wl.release();
		} else if (mode_on.equals("Weekday")) {
			if (currentDayOfWeek <= 5) {
				Log.e("AlarmOnReceiver", "go");
				wl.acquire();
				wl.release();
			}
		} else if (mode_on.equals("Weekend")) {
			if (currentDayOfWeek >= 6) {
				wl.acquire();
				wl.release();
			}
		}

	}

}
