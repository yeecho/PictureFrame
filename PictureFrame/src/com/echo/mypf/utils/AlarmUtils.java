package com.echo.mypf.utils;

import java.util.Calendar;

import com.echo.mypf.custom.Echo;
import com.echo.mypf.receiver.AlarmOffReceiver;
import com.echo.mypf.receiver.AlarmReceiver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmUtils {

	@SuppressLint("NewApi")
	public static void setAlarm(Context context, Calendar c, String mode) {
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		int currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
		if (currentDayOfWeek == 0) {
			currentDayOfWeek = 7;
		}
		Log.e("currentDayOfWeek:", "" + currentDayOfWeek);
		if (mode.equals("Weekday")) {
			for (int i = 1; i <= 5; i++) {
				int days = 0;
				if (currentDayOfWeek <= i) {
					days = i - currentDayOfWeek;
				} else {
					days = 7 - currentDayOfWeek + i;
				}
				long tempAlarmTime = getRealAlarmTime(c) + days * 1000 * 3600
						* 24;
				// alarm.setRepeating(AlarmManager.RTC_WAKEUP,
				// tempAlarmTime ,
				// // System.currentTimeMillis()+5000,
				// // AlarmManager.INTERVAL_DAY,
				// 1000*60*6,
				// PendingIntent.getBroadcast(context, Echo.AlarmID[i], intent,
				// PendingIntent.FLAG_CANCEL_CURRENT));

				alarm.set(AlarmManager.RTC_WAKEUP,
						System.currentTimeMillis() + 10000, PendingIntent
								.getBroadcast(context, Echo.AlarmID[i], intent,
										PendingIntent.FLAG_CANCEL_CURRENT));
				Log.e("weekday:", "" + Echo.AlarmID[i] + " "
						+ (tempAlarmTime - System.currentTimeMillis()));
			}

		} else if (mode.equals("Weekend")) {
			for (int i = 6; i <= 7; i++) {
				int days = 0;
				if (currentDayOfWeek <= i) {
					days = i - currentDayOfWeek;
				} else {
					days = 7 - currentDayOfWeek + i;
				}
				long tempAlarmTime = getRealAlarmTime(c) + days * 1000 * 3600
						* 24;
				alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,
						tempAlarmTime, 1000 * 3600 * 24 * 7, PendingIntent
								.getBroadcast(context, Echo.AlarmID[i], intent,
										PendingIntent.FLAG_CANCEL_CURRENT));
				Log.e("weekend:", "" + Echo.AlarmID[i] + " "
						+ (tempAlarmTime - System.currentTimeMillis()));
			}
		} else if (mode.equals("Everyday")) {
			PendingIntent pi = PendingIntent.getBroadcast(context,
					Echo.AlarmID[0], intent, PendingIntent.FLAG_CANCEL_CURRENT);
			// alarm.setRepeating(AlarmManager.RTC_WAKEUP, getRealAlarmTime(c) ,
			// AlarmManager.INTERVAL_DAY, pi);
			alarm.set(AlarmManager.RTC_WAKEUP, getRealAlarmTime(c), pi);
			Log.e("everyday:", "" + Echo.AlarmID[0] + " "
					+ (getRealAlarmTime(c) - System.currentTimeMillis()));
		}
		Toast.makeText(context, "alarm is set!", Toast.LENGTH_SHORT).show();
	}

	@SuppressLint("NewApi")
	public static void setAlarm(Context context, Calendar c, int id) {
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		if (id == 0) {
			Intent intent = new Intent(context, AlarmReceiver.class);
			intent.putExtra("alarm_type", 0);
			alarm.setExact(AlarmManager.RTC_WAKEUP, getRealAlarmTime(c),
					PendingIntent.getBroadcast(context, 0, intent,
							0));  
			Log.e("setAlarm", " id="+id+"间隔："
					+ (getRealAlarmTime(c) - System.currentTimeMillis() + 1000)/60000);
		}else if (id == 1){
			Intent intent2 = new Intent(context, AlarmOffReceiver.class);
			intent2.putExtra("alarm_type", 1);
			alarm.setExact(AlarmManager.RTC_WAKEUP, getRealAlarmTime(c),
					PendingIntent.getBroadcast(context, 1, intent2,
							0));  
			Log.e("setAlarm", " id="+id+"间隔："
					+ (getRealAlarmTime(c) - System.currentTimeMillis() + 1000)/60000);
		}
		Toast.makeText(context, "alarm is set!", Toast.LENGTH_SHORT).show();
	}

	public static long getRealAlarmTime(Calendar c) {
		long realAlarmTime = c.getTimeInMillis() > System.currentTimeMillis() ? c
				.getTimeInMillis() : c.getTimeInMillis() + 1000 * 3600 * 24;
		return realAlarmTime;
	}

	public static void cancelAlarm(Context context, String mode) {
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		if (mode.equals("Weekday")) {
			alarm.cancel(PendingIntent.getBroadcast(context, Echo.AlarmID[1],
					intent, PendingIntent.FLAG_UPDATE_CURRENT));
			alarm.cancel(PendingIntent.getBroadcast(context, Echo.AlarmID[2],
					intent, PendingIntent.FLAG_UPDATE_CURRENT));
			alarm.cancel(PendingIntent.getBroadcast(context, Echo.AlarmID[3],
					intent, PendingIntent.FLAG_UPDATE_CURRENT));
			alarm.cancel(PendingIntent.getBroadcast(context, Echo.AlarmID[4],
					intent, PendingIntent.FLAG_UPDATE_CURRENT));
			alarm.cancel(PendingIntent.getBroadcast(context, Echo.AlarmID[5],
					intent, PendingIntent.FLAG_UPDATE_CURRENT));
		} else if (mode.equals("Weekend")) {
			alarm.cancel(PendingIntent.getBroadcast(context, Echo.AlarmID[6],
					intent, PendingIntent.FLAG_UPDATE_CURRENT));
			alarm.cancel(PendingIntent.getBroadcast(context, Echo.AlarmID[7],
					intent, PendingIntent.FLAG_UPDATE_CURRENT));
		} else if (mode.equals("Everyday")) {
			alarm.cancel(PendingIntent.getBroadcast(context, Echo.AlarmID[0],
					intent, PendingIntent.FLAG_UPDATE_CURRENT));
		}
	}
	
	public static void cancelAlarm(Context context) {
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		alarm.cancel(PendingIntent.getBroadcast(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT));
	}
}
