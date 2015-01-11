package models.admin.adminutils;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.TimeZone;
public class AdminUtils{

	public static String getCurrentClientTime(TimeZone timezone){
		DateFormat formatter= new SimpleDateFormat("HH:mm a Z");
		formatter.setTimeZone(timezone);
		return formatter.format(new Date());
	}
	
	public static String getTimeRemaining(Date deadline){
		Date now = new Date();
		long diff = deadline.getTime() - now.getTime();
		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);
		return diffDays + " days, "+diffHours + " Hrs " + diffMinutes+" Mins.";
	}
	
}
