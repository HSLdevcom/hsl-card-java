/*
 * HSLDomicileMappings.java
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

import android.content.Context;

import com.hsl.cardlibrary.R;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * The Class ValidityAreaMappings is used to get names for ticket validity areas.
 * <p>
 * In the ticket data the validity area type can be "zone" or "vehicle" and the validity area is represented as a number.
 * With this class you can get localized string for the zone or vehicle code.
 * Supported locale languages are Finnish, Swedish and English.
 * <p>
 * The localization is made with android's internal locale system using strings.xml to store the localized strings.
 */
public class HSLDomicileMappings {

    /** The codes0. */
    protected static Hashtable<Integer, String> codes0 = null;

    /** The codes1. */
    protected static Hashtable<Integer, String> codes1 = null;

    /** The codes2. */
    protected static Hashtable<Integer, String> codes2 = null;

    /**
     * Instantiates a new validity area mappings.
     *
     * @param app_context the app_context
     */
    public HSLDomicileMappings (Context app_context)
    {
        initCodes0 (app_context);
        initCodes1 (app_context);
        initCodes2 ();
    }

    /**
     * Initializes the zone names table.
     *
     * @param app_context the Android application context needed to get the zone names string resources
     */
    protected void initCodes0 (Context app_context)
    {
        codes1 = new Hashtable<Integer, String> ();
        codes1.put (new Integer (0), app_context.getResources().getString(R.string.d0));
        codes1.put (new Integer (1), app_context.getResources().getString(R.string.d1));
        codes1.put (new Integer (2), app_context.getResources().getString(R.string.d2));
        codes1.put (new Integer (4), app_context.getResources().getString(R.string.d4));
        codes1.put (new Integer (5), app_context.getResources().getString(R.string.d5));
        codes1.put (new Integer (6), app_context.getResources().getString(R.string.d6));
        codes1.put (new Integer (7), app_context.getResources().getString(R.string.d7));
        codes1.put (new Integer (8), app_context.getResources().getString(R.string.d8));
        codes1.put (new Integer (9), app_context.getResources().getString(R.string.d9));
        codes1.put (new Integer (10), app_context.getResources().getString(R.string.d10));
        codes1.put (new Integer (14), app_context.getResources().getString(R.string.d14));
        codes1.put (new Integer (15), app_context.getResources().getString(R.string.d15));
    }

    /**
     * Initializes the vehicle names table.
     *
     * @param app_context the Android application context needed to get the vehicle names string resources
     */
    protected void initCodes1 (Context app_context)
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
     * Initializes the new HSL zones.
     *
     */
    protected void initCodes2 ()
    {
        codes2 = new Hashtable<Integer, String> ();
        codes2.put (new Integer (0), "A");
        codes2.put (new Integer (1), "B");
        codes2.put (new Integer (2), "C");
        codes2.put (new Integer (3), "D");
        codes2.put (new Integer (4), "E");
        codes2.put (new Integer (5), "F");
        codes2.put (new Integer (6), "G");
        codes2.put (new Integer (7), "H");
    }

    public Hashtable<Integer, String> getDomicileHash ()
    {
        return codes0;
    }

    /**
     * Gets the domicile name.
     * <p>
     * The returned name is vehicle name or zone name depending of the validity area type
     *
     * @param type the validity area type
     * @param code the validity area code
     * @return the name of the validity area
     */
    public String getDomicile (int type, int code)
    {
        if (codes0 == null)
        {
            return null;
        }
        if (codes1 == null)
        {
            return null;
        }
        String domicile = null;
        if (type == 0)
        {
            domicile = codes0.get(new Integer (code));
            if (code > 0) {
                String arcDom = getArcDomicile(code);
                if (arcDom != null)
                    domicile += " (" + getArcDomicile(code) + ")";
            }
        }
        else if (type == 1)
        {
            domicile = codes1.get(new Integer (code));
        }
        else if (type == 2) {
            // 12.4.2019 - New zones addition
            int zoneFrom = bitExtracted(code, 3, 4);
            int zoneTo = bitExtracted(code, 3, 1);
            domicile = "";
            for (int i = zoneFrom; i < (zoneTo+1); i++) {
                domicile += codes2.get(i);
            }
        }
        else {
            domicile = "ERROR";
        }
        return domicile;
    }

    /**
     * Get list of certain areas.
     * @param type the validity area type
     * @param ignore_zero dont count zero values.
     * @return String array of searched area.
     */
    public String[] getDomicileList (int type, boolean ignore_zero)
    {
        Hashtable<Integer, String> codes;
        String[] strs = null;

        if (codes0 == null)
        {
            return null;
        }
        if (codes1 == null)
        {
            return null;
        }

        if (type == 0)
            codes = codes0;
        else if (type == 1)
            codes = codes1;
        else if (type == 2)
            codes = codes2;
        else {
            return null;
        }

        strs = new String[codes.size()];
        Iterator<String> c = codes.values().iterator();
        //If we are skipping over the first values
        if (ignore_zero && c.hasNext())
            c.next();

        int i = 0;
        while (c.hasNext())
        {
            strs[i] = c.next();
            i++;
        }

        return strs;
    }

    /**
     * Function to extract k bits from p position
     * and returns the extracted value as integer
     **/
    int bitExtracted(int number, int k, int p)
    {
        return (((1 << k) - 1) & (number >> (p - 1)));
    }

    /**
     * Method for converting old ticket zones to new HSL zones (i.e. Helsinki = AB).
     * @param code old zone domicile code.
     * @return string for new zone domicile.
     **/
    public static String getArcDomicile(int code) {
        switch (code) {
            case 1: return "AB";    // Helsinki
            case 2: return "BC";    // Espoo
            case 4: return "BC";    // Vantaa
            case 5: return "ABC";   // Seutu
            case 6: return "D";     // Knummi-Siuntio
            case 9: return "D";     // Kerava-Sipoo-Tuusula
            case 14: return "BCD";  // Lähiseutu
            case 15: return "ABCD"; // Lähiseutu3
            default: return null;
        }
    }
}
