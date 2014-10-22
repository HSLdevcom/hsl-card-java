/*
 * Convert.java
 *
 * Copyright (C) 2012 HSL/HRT (Helsingin seudun liikenne/ Helsinki Region Transport) 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.hsl.util;

import java.util.Calendar;
import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Convert class contains conversion utilities for hex string and date conversions from the ticket data.
 */
public class Convert 
{
	
	/** The en1545 format zero date (1.1.1997) in java Date format (number of milliseconds since 1.1.1970). */
	public static long en1545zeroDate = 852076800000L;
	
	/** The length of one day in milliseconds. */
	public static long dayInMs = 86400000L;
	
	/** The length of one minute in milliseconds. */
	public static long minuteInMs = 60000L;
	
	/**
	 * Gets the hex string.
	 *
	 * @param b the Byte buffer to convert
	 * @return String representing the hex values of the given bytes 
	 */
	public static String getHexString(byte[] b)  
	{
		String result = "";

		for (int i=0; i < b.length; i++) 
		{
			result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		
		return result;
	}

/**
 * En5145 date to java Date conversion.
 *
 * @param date the date in the en5145 format (number of days since 1.1.1997)
 * @return the date in java Date format
 */
public static java.util.Date en5145Date2JavaDate(short date)
	{
		Calendar cal = Calendar.getInstance();
		int utcOffset = cal.getTimeZone().getOffset(((long)date * dayInMs) + en1545zeroDate );
		Date utcDate = new Date( ((long)date * dayInMs) + en1545zeroDate - (long)utcOffset); 
		
		return utcDate; 
	}

	/**
	 * Java Date to en5145 date conversion.
	 *
	 * @param date the java Date to convert
	 * @return the date in en5145 format (number of days since 1.1.1997)
	 */
	public static short JavaDate2en5145Date(Date date)
	{
		return (short)((date.getTime() - en1545zeroDate) / dayInMs);
	}

	/**
	 * En5145 date and time to java Date conversion
	 * 
	 *
	 * @param date the date in the en5145 format (number of days since 1.1.1997)
	 * @param time the time in en1545 format (number of minutes since 00:00)
	 * @return the date  (with time) in java Date format
	 */
	public static Date en5145DateAndTime2JavaDate(short date, short time)
	{
		Calendar cal = Calendar.getInstance();
		int utcOffset = cal.getTimeZone().getOffset(((long)date * dayInMs) + en1545zeroDate + ((long)time * minuteInMs));
		Date utcDate = new Date( ((long)date * dayInMs) + en1545zeroDate + ((long)time * minuteInMs) - (long)utcOffset);

		return utcDate; 
	}
}
