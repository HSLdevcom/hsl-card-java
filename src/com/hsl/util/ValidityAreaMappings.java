/*
 * ValidityAreaMappings.java
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


import java.util.Hashtable;
import android.annotation.SuppressLint;
import android.content.Context;

import com.hsl.cardlibrary.R;

/**
 * The Class ValidityAreaMappings is used to get names for ticket validity areas.
 * <p>
 * In the ticket data the validity area type can be "zone" or "vehicle" and the validity area is represented as a number.
 * With this class you can get localized string for the zone or vehicle code. 
 * Supported locale languages are Finnish, Swedish and English.
 * <p>
 * The localization is made with android's internal locale system using strings.xml to store the localized strings.
 */
@SuppressLint("UseValueOf")
public class ValidityAreaMappings 
{
	
	/** The codes1. */
	protected static Hashtable<Integer, String> codes1 = null;
	
	/** The codes2. */
	protected static Hashtable<Integer, String> codes2 = null;	

	/**
	 * Instantiates a new validity area mappings.
	 *
	 * @param app_context the app_context
	 */
	public ValidityAreaMappings (android.content.Context app_context)
	{
		initCodes1 (app_context);
		initCodes2 (app_context);
	}
	
	/**
	 * Initializes the zone names table.
	 *
	 * @param app_context the Android application context needed to get the zone names string resources
	 */
	protected void initCodes1 (Context app_context)
	{
		codes1 = new Hashtable<Integer, String> ();
		codes1.put (new Integer (0), app_context.getResources().getString(R.string.z0));
		codes1.put (new Integer (1), app_context.getResources().getString(R.string.z1));
		codes1.put (new Integer (2), app_context.getResources().getString(R.string.z2));
		codes1.put (new Integer (4), app_context.getResources().getString(R.string.z4));
		codes1.put (new Integer (5), app_context.getResources().getString(R.string.z5));
		codes1.put (new Integer (6), app_context.getResources().getString(R.string.z6));
		codes1.put (new Integer (7), app_context.getResources().getString(R.string.z7));
		codes1.put (new Integer (8), app_context.getResources().getString(R.string.z8));
		codes1.put (new Integer (9), app_context.getResources().getString(R.string.z9));
		codes1.put (new Integer (10), app_context.getResources().getString(R.string.z10));
		codes1.put (new Integer (14), app_context.getResources().getString(R.string.z14));
		codes1.put (new Integer (15), app_context.getResources().getString(R.string.z15));
	}
	
	/**
	 * Initializes the vehicle names table.
	 *
	 * @param app_context the Android application context needed to get the vehicle names string resources
	 */
	protected void initCodes2 (Context app_context)
	{
		codes2 = new Hashtable<Integer, String> ();
		codes2.put (new Integer (0), app_context.getResources().getString(R.string.v0));
		codes2.put (new Integer (1), app_context.getResources().getString(R.string.v1));
		codes2.put (new Integer (5), app_context.getResources().getString(R.string.v5));
		codes2.put (new Integer (6), app_context.getResources().getString(R.string.v6));
		codes2.put (new Integer (7), app_context.getResources().getString(R.string.v7));		
		codes2.put (new Integer (8), app_context.getResources().getString(R.string.v8));
		codes2.put (new Integer (9), app_context.getResources().getString(R.string.v9));
	}
	
	/**
	 * Gets the validity area name.
	 * <p>
	 * The returned name is vehicle name or zone name depending of the validity area type
	 *
	 * @param areaType the validity area type
	 * @param areaCode the validity area code
	 * @return the name of the validity area
	 */
	public String getValidityArea (int areaType, int areaCode)
	{
		if ((codes1 == null) || (codes2 == null))
		{
			return null;
		}

		String validArea = null;
		if (areaType == 0)
		{
			validArea = codes1.get(new Integer (areaCode));
		}
		else
		{
			validArea = codes2.get(new Integer (areaCode));			
		}
		return validArea;
	}
	
}
