/*
 * LuhnMod10.java
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

public class LuhnMod10 
{

    public static boolean IsValid(String id)
    {
        
        int idLength = id.length() ;
        int currentDigit ;
        int idSum = 0 ;
        int currentProcNum = 0 ; //the current process number (to calc odd/even proc)
        
        for(int i=idLength-1; i>=0; i--)
        {
            //get the current rightmost digit from the string
            String idCurrentRightmostDigit = id.substring(i,i+1);
            
            try {
                //parse to int the current rightmost digit, if fail return false (not-valid id)
                currentDigit = Integer.parseInt(idCurrentRightmostDigit);
            } catch (NumberFormatException e)
            {
            	return false;
            }
            
            //double value of every 2nd rightmost digit (odd)
            //if value 2 digits (can be 18 at the current case),
            //then sumarize the digits (made it easy the by remove 9)
            if(currentProcNum%2 != 0)
            {
            	if((currentDigit*= 2) > 9)
            		currentDigit-= 9 ;
            }
            currentProcNum++ ; //increase the proc number
            
            //summarize the processed digits
            idSum += currentDigit ;
        }
        
        //if digits sum is exactly divisible by 10, return true (valid), else false (not-valid)
        return (idSum%10 == 0) ;
    }               
}
