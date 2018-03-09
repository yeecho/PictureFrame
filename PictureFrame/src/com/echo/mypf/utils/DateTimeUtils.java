package com.echo.mypf.utils;

import java.io.IOException;
import java.util.Calendar;

import android.os.SystemClock;
import android.util.Log;

public class DateTimeUtils {

	public static void setDateTime(int year, int month, int day, int hour,
			int minute) throws IOException, InterruptedException {

		Calendar c = Calendar.getInstance();

		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);

		long when = c.getTimeInMillis();

		if (when / 1000 < Integer.MAX_VALUE) {
			SystemClock.setCurrentTimeMillis(when);
			Log.e("dateset", ""+when);
		}

		long now = Calendar.getInstance().getTimeInMillis();

		if (now - when > 1000)
			throw new IOException("failed to set Date.");

	}

	public static void setDate(int year, int month, int day)
			throws IOException, InterruptedException {

		Calendar c = Calendar.getInstance();

		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DAY_OF_MONTH, day);
		long when = c.getTimeInMillis();

		if (when / 1000 < Integer.MAX_VALUE) {
			SystemClock.setCurrentTimeMillis(when);
		}

		long now = Calendar.getInstance().getTimeInMillis();

		if (now - when > 1000)
			throw new IOException("failed to set Date.");
	}

	public static void setTime(int hour, int minute) throws IOException,
			InterruptedException {

		Calendar c = Calendar.getInstance();

		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		long when = c.getTimeInMillis();

		if (when / 1000 < Integer.MAX_VALUE) {
			SystemClock.setCurrentTimeMillis(when);
		}

		long now = Calendar.getInstance().getTimeInMillis();

		if (now - when > 1000)
			throw new IOException("failed to set Time.");
	}

}
