/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arny.passlock.helpers;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Functions {

    private static Functions instance;

    private Functions() {
    }

    public static Functions getInstance() {
	if (instance == null) {
	    instance = new Functions();
	}
	return instance;
    }

    public String getStringDateTime(int year, int monthOfYear, int dayOfMonth) {
	String strDateFormat = "MMM";
	String strMonth = new DateFormatSymbols().getMonths()[monthOfYear];
	Date date = null;
	try {
	    date = new SimpleDateFormat(strDateFormat, Locale.getDefault()).parse(strMonth);
	} catch (ParseException e) {
	    e.printStackTrace();
	}
	String formDate = new SimpleDateFormat("MMM", Locale.getDefault()).format(date);
	return dayOfMonth + " " + formDate + " " + year;
    }

    public String strLogTime(int logtime) {
	int h = logtime / 60;
	int m = logtime % 60;
	return pad(h) + ":" + pad(m);
    }

    public String pad(int c) {
	if (c >= 10) {
	    return String.valueOf(c);
	} else {
	    return "0" + String.valueOf(c);
	}
    }
    
    public long randLong(long min, long max) {
	Random rnd = new Random();
	if (min > max) {
	    throw new IllegalArgumentException("min>max");
	}
	if (min == max) {
	    return min;
	}
	long n = rnd.nextLong();
	//abs (use instead of Math.abs, which might return min value) :
	n = n == Long.MIN_VALUE ? 0 : n < 0 ? -n : n;
	//limit to range:
	n = n % (max - min);
	return min + n;
    }
    
    public int randInt(int min, int max) {
	Random rnd = new Random();
	int range = max - min + 1;
	return rnd.nextInt(range) + min;
    }
    
    
    public int getMonth(long time) {
	Calendar cal = Calendar.getInstance();
	cal.setTimeInMillis(time);
	return cal.get(Calendar.MONTH) + 1;
    }
    
    public String getStrMonth(long time) {
	Calendar cal = Calendar.getInstance();
	cal.setTimeInMillis(time);
	int y = cal.get(Calendar.YEAR);
	int m = cal.get(Calendar.MONTH);
	int d = cal.get(Calendar.DAY_OF_MONTH);
	String strDateFormat = "MMM";
	String strM = new DateFormatSymbols().getMonths()[m];
	Date dat = null;
	try {
	    dat = new SimpleDateFormat(strDateFormat, Locale.getDefault()).parse(strM);
	} catch (ParseException e) {
	    e.printStackTrace();
	}
	String mMonth = new SimpleDateFormat("MMM", Locale.getDefault()).format(dat);
	return mMonth + " " + y;
    }

    public String getStrDate(long time) {
	Calendar cal = Calendar.getInstance();
	cal.setTimeInMillis(time);
	int y = cal.get(Calendar.YEAR);
	int m = cal.get(Calendar.MONTH);
	int d = cal.get(Calendar.DAY_OF_MONTH);
	String strDateFormat = "MMM";
	String strM = new DateFormatSymbols().getMonths()[m];
	Date dat = null;
	try {
	    dat = new SimpleDateFormat(strDateFormat, Locale.getDefault()).parse(strM);
	} catch (ParseException e) {
	    e.printStackTrace();
	}
	String fDate = new SimpleDateFormat("MMM", Locale.getDefault()).format(dat);
	return d + " " + fDate + " " + y;
    }

    public String getStrTime(long timestamp) {
	Date d = new Date(timestamp);
	SimpleDateFormat format = new SimpleDateFormat("hh:mm", Locale.getDefault());
	return format.format(d);
    }

	public String getDateTime(long timestamp) {
		Date date = new Date(timestamp); // *1000 is to convert seconds to milliseconds
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss",Locale.getDefault()); // the format of your date
		return sdf.format(date);
	}

	public String getUnixDateTime(long timestamp) {
		Date date = new Date(timestamp*1000L); // *1000 is to convert seconds to milliseconds
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()); // the format of your date
		return sdf.format(date);
	}

    public String getCurrentDate() {
	Calendar cal = Calendar.getInstance();
	int y = cal.get(Calendar.YEAR);
	int m = cal.get(Calendar.MONTH);
	int d = cal.get(Calendar.DAY_OF_MONTH);
	String strDateFormat = "MMM";
	String strM = new DateFormatSymbols().getMonths()[m];
	Date dat = null;
	try {
	    dat = new SimpleDateFormat(strDateFormat, Locale.getDefault()).parse(strM);
	} catch (ParseException e) {
	    e.printStackTrace();
	}
	String fDate = new SimpleDateFormat("MMM", Locale.getDefault()).format(dat);
	return d + " " + fDate + " " + y;
    }

    public long convertTimeStringToLong(String myTimestamp) {
	Calendar mCalendar = Calendar.getInstance();
	SimpleDateFormat curFormater = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
	Date dateObj = null;
	try {
	    dateObj = curFormater.parse(myTimestamp);
	} catch (ParseException e) {
	    e.printStackTrace();
	}
	mCalendar.setTime(dateObj);
	return mCalendar.getTimeInMillis();
    }

}
