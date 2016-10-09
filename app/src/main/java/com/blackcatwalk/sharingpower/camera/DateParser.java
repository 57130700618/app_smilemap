package com.blackcatwalk.sharingpower.camera;


import android.annotation.SuppressLint;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


@SuppressLint("SimpleDateFormat")
public class DateParser {

	private final String dateFormat = "yyyy-MM-dd HH:mm:ss.SSSZ";

	private final TimeZone utc = TimeZone.getTimeZone("UTC");

	public String dateToString(Date date) {
		if (date == null) {
			return null;
		} else {
			DateFormat df = new SimpleDateFormat(dateFormat);
			df.setTimeZone(utc);
			return df.format(date);	
		}
	}

	public Date stringToDate(String dateAsString) {
		try {
			DateFormat df = new SimpleDateFormat(dateFormat);
			df.setTimeZone(utc);
			return df.parse(dateAsString);
		} catch (ParseException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}
}