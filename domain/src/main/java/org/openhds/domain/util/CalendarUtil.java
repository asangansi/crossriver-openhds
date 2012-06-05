package org.openhds.domain.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.openhds.domain.service.SitePropertiesService;

public class CalendarUtil {
	
	SitePropertiesService siteProperties;
	private static final String MYSQL_DATE_FORMAT = "yyyy-MM-dd";
	
	public static Calendar getMidPointDate(Calendar startDate, Calendar endDate) {
		int daysBtw = (int)daysBetween(startDate, endDate);
		Calendar midPoint = (Calendar)startDate.clone();
		midPoint.add(Calendar.DATE, (int) (daysBtw * 0.5));
		return midPoint;
	}
	
	public Calendar parseDate(String dateStr) throws ParseException {
    	DateFormat formatter = new SimpleDateFormat(siteProperties.getDateFormat());
        Date date = formatter.parse(dateStr);
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        return dateCal;
    }
    
	public Calendar convertDateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
    	DateFormat formatter = new SimpleDateFormat(siteProperties.getDateFormat());
		cal.setTime(date);
		formatter.setCalendar(cal);
		return cal;
	}
	
	public static long daysBetween(Calendar startDate, Calendar endDate) {  
		Calendar date = (Calendar) startDate.clone();  
		long daysBetween = 0;  
		while (date.before(endDate)) {  
			date.add(Calendar.DAY_OF_MONTH, 1);  
			daysBetween++;  
		}  
		return daysBetween;  
	}  	
	
	public Calendar getCalendar(int month, int day, int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, day);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		return cal;
	}
	
	public String formatDate(Calendar calendar) {
		SimpleDateFormat format = new SimpleDateFormat(siteProperties.getDateFormat());
		return format.format(calendar.getTime());
	}
	
	private static Calendar formatStringToCalendar(String date, String format)
			throws ParseException {
		Calendar cal = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat(format);
		formatter.setLenient(false);
		Date newDate = (Date)formatter.parse(date); 
		cal.setTime(newDate);
	
		return cal;
	}
	
	public Calendar stringToCalendar(String date) throws ParseException {
		try {
			return formatStringToCalendar(date, siteProperties.getDateFormat());
		} catch(ParseException e) {
			return formatStringToCalendar(date, MYSQL_DATE_FORMAT);
		}
	}
	
    public SitePropertiesService getSiteProperties() {
		return siteProperties;
	}

	public void setSiteProperties(SitePropertiesService siteProperties) {
		this.siteProperties = siteProperties;
	}
}
