package com.coffeetechgaff.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author VivekSubedi
 *
 */
public final class DateUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);

	private DateUtils(){
		// can not initialize it
	}

	private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<>();
	static {
		// 20170125
		DATE_FORMAT_REGEXPS.put("^\\d{8}$", "yyyyMMdd");
		// 25-01-2017
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
		// 2017-01-14
		DATE_FORMAT_REGEXPS.put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
		// 01/14/2017
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
		// 2017/01/14
		DATE_FORMAT_REGEXPS.put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
		// 14 JAN 2014
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
		// 14 JANUARY 2014
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
		// 14 JANUARY, 2014
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}\\s[a-z]{4,},\\s\\d{4}$", "dd MMMM, yyyy");
		// 201701141703
		DATE_FORMAT_REGEXPS.put("^\\d{12}$", "yyyyMMddHHmm");
		// 20170114 1703
		DATE_FORMAT_REGEXPS.put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
		// 14-01-2017 17:23
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
		// 2017/01/14 17:23
		DATE_FORMAT_REGEXPS.put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
		// 01/14/2017 17:23
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
		// 2017/01/14 17:23
		DATE_FORMAT_REGEXPS.put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
		// 14 JAN 2017 17:23
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
		// 14 JANUARY 2017 17:23
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
		// 14 JANUARY, 2017 17:23
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}\\s[a-z]{4,},\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM, yyyy HH:mm");
		// 20170114172334
		DATE_FORMAT_REGEXPS.put("^\\d{14}$", "yyyyMMddHHmmss");
		// 20170114 172334
		DATE_FORMAT_REGEXPS.put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
		// 14-01-2017 17:23:33
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
		// 2017/01/14 17:23:33
		DATE_FORMAT_REGEXPS.put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
		// 01/14/2017 17:23:33
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
		// 2017/01/14 17:23:33
		DATE_FORMAT_REGEXPS.put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
		// 14 JAN 2017 17:23:33
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
		// 14 JANUARY 2017 17:23:33
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
		// 14 JANUARY, 2017 17:23:33
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}\\s[a-z]{4,},\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM, yyyy HH:mm:ss");
		// 14-JANUARY-2017 17:23:33
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}-[a-z]{4,}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MMMM-yyyy HH:mm:ss");
		// 2017-01-14 03:25:45 PM
		DATE_FORMAT_REGEXPS.put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}\\s[a-z]{2}$", "yyyy-MM-dd hh:mm:ss a");
		// 01/14/2017 03:25:45 PM
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}\\s[a-z]{2}$", "MM/dd/yyyy hh:mm:ss a");
		// 01/14/2017 03:25 PM
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}\\s[a-z]{2}$", "MM/dd/yyyy hh:mm a");
		// 01-14-2017 03:25:45 PM
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}\\s[a-z]{2}$", "MM-dd-yyyy hh:mm:ss a");
		// 2017/01/14 03:25:45 PM
		DATE_FORMAT_REGEXPS.put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}\\s[a-z]{2}$", "yyyy/MM/dd hh:mm:ss a");
		// 2017/01/14 03:25 PM
		DATE_FORMAT_REGEXPS.put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}\\s[a-z]{2}$", "yyyy/MM/dd hh:mm a");
		// 14 JAN 2017 17:23:33
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM, yyyy HH:mm:ss");
		// 14 JANUARY, 2017 17:23:33
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}\\s[a-z]{4,},\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM, yyyy HH:mm:ss");
		// 01-JAN-2015 10:15:00
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}-[a-z]{3}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MMM-yyyy HH:mm:ss");
		// 2016-10-11T23:35:26.204
		DATE_FORMAT_REGEXPS.put("^\\d{4}-\\d{1,2}-\\d{1,2}[t]\\d{1,2}:\\d{2}:\\d{2}.\\d{3,}$", "yyyy-MM-ddTHH:mm:ss.SSS");
		// 10/11/2017 23:35:26.204
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3,}$", "dd/MM/yyyy HH:mm:ss.SSS");
		DATE_FORMAT_REGEXPS.put("^\\d{4}-\\d{1,2}-\\d{1,2}[t]\\d{1,2}:\\d{2}$", "yyyy-MM-ddTHH:mm");
		DATE_FORMAT_REGEXPS.put("^\\d{4}-\\d{1,2}-\\d{1,2}[t]\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-ddTHH:mm:ss");
		// 01-25-2017 12:00 AM
		DATE_FORMAT_REGEXPS.put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}\\s[a-z]{2}$", "MM-dd-yyyy hh:mm a");
		// Feb 22, 2017 3:01:47 PM(zeppelin create date format)
		DATE_FORMAT_REGEXPS.put("^[a-z]{3}\\s\\d{1,2},\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}\\s[a-z]{2}$", "MMM dd, yyyy hh:mm:ss a");
	}
			

	/**
	 * Determine SimpleDateFormat pattern matching with the given date string.
	 * Returns null if format is unknown. We can simply extend DateUtil with
	 * more formats if needed.
	 * 
	 * @param dateString
	 *            The date string to determine the SimpleDateFormat pattern for.
	 * @return The matching SimpleDateFormat pattern, or null if format is
	 *         unknown.
	 * @see SimpleDateFormat
	 */
	public static String determineDateFormat(String dateString){
		for(Map.Entry<String, String> regexp : DATE_FORMAT_REGEXPS.entrySet()){
			if(dateString.toLowerCase().matches(regexp.getKey())){
				return regexp.getValue();
			}
		}

		return null;
	}

	/**
	 * Parsed the user provided date with the provided date format and returns
	 * the formatted date to the user
	 * 
	 * @param dateTime
	 *            -user provided datetime or date
	 * @param dateFormat
	 *            -datetime format
	 * @return formatted date
	 * @throws ParseException
	 */
	public static String getFormattedDate(String dateTime, String dateFormat) throws ParseException{
		LOGGER.debug("The datetime [{}] we are getting is in [{}] format", dateTime, dateFormat);
		try{
			final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
			LocalDate parsedDate = LocalDate.parse(dateTime, dateTimeFormatter);
			return parsedDate.toString();
		}catch(DateTimeParseException e){
			SimpleDateFormat formatDate = new SimpleDateFormat(dateFormat);
			Date varDate = formatDate.parse(dateTime);
			formatDate = new SimpleDateFormat("yyyy-MM-dd");
			return formatDate.format(varDate);
		}
	}

	/**
	 * Parsed the user provided datetime with the provided datetime format and
	 * returns the formatted datetime to the user
	 * 
	 * @param dateTime
	 *            -user provided datetime
	 * @param dateFormat
	 *            -datetime format
	 * @return formatted datetime
	 * @throws ParseException
	 */
	public static String getFormattedDateTime(String dateTime, String dateFormat) throws ParseException{
		LOGGER.debug("The datetime [{}] we are getting is in [{}] format", dateTime, dateFormat);
		try{
			DateTimeFormatter dateTimeFormatter;
			try{
				dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
				LocalDateTime parsedDateTime = LocalDateTime.parse(dateTime, dateTimeFormatter);
				return parsedDateTime.toString();
			}catch(IllegalArgumentException e1){
				LocalDateTime parsedDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
				return parsedDateTime.toString();
			}
		}catch(DateTimeParseException e){
			SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat);
			Date varDate = dateTimeFormat.parse(dateTime);
			dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			return dateTimeFormat.format(varDate);
		}
	}
}
