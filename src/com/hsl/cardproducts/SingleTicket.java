/*
 * SingleTicket.java
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
package com.hsl.cardproducts;

import com.hsl.util.Convert;

/**
 * The Class SingleTicket stores all the data read from the HSL single ticket.
 * When the class is instantiated it reads the raw data from given parameters and extracts it to the member variables that can be read through provided getter methods.
 */
public class SingleTicket 
{
	/** The application version. */
	private byte 	applicationVersion;
	
	/** The application instance id (18 numbers). */
	private String  applicationInstanceId; 
	
	/** The platform type. */
	private byte	platformType;
	
	/** The value ticket. */
	private eTicket valueTicket;
	
	/**
	 * Instantiates a new single ticket using the data given as parameters.
	 * <p>
	 *
	 * @param appInfoBytes the byte buffer containing ApplicationInformation data
	 * @param eTicketBytes the byte buffer containing the eTicket data
	 */
	public SingleTicket(byte[] appInfoBytes, byte[] eTicketBytes)
	{
		//Read data from application info
		readApplicationInfo(appInfoBytes);
		
		//read value ticket 
		valueTicket = new eTicket(eTicketBytes, true);	
	}

	/**
	 * Read and extract the application information data from the single ticket.
	 *
	 * @param appInfo the byte buffer containing ApplicationInformation data
	 */
	private void readApplicationInfo(byte[] appInfo)
	{
		//Read data from application info
		applicationVersion = (byte)(appInfo[16] & 0xF0);
		
		byte[] temp = new byte[5];
		System.arraycopy(appInfo, 17, temp, 0, 5);
		applicationInstanceId = Convert.getHexString(temp);
		int num = ( (appInfo[1] ^ appInfo[5]) & 0x7F );
		num = (num << 8) + ((appInfo[2] ^ appInfo[6]) & 0xFF);
		num = (num << 8) + ((appInfo[4] ^ appInfo[7]) & 0xFF);

		applicationInstanceId = applicationInstanceId.concat(String.format("%07d", num)).concat(""+((appInfo[22] & 0xF0)>>>4));
		
		platformType = (byte)(appInfo[22] & 0x0E);
	}

	/**
	 * Gets the application version.
	 *
	 * @return the version of the ticket application
	 */
	public byte getApplicationVersion() {
		return applicationVersion;
	}

	/**
	 * Gets the application instance id.
	 *
	 * @return the application instance id
	 */
	public String getApplicationInstanceId() {
		return applicationInstanceId;
	}

	/**
	 * Gets the platform type.
	 *
	 * @return the platform type
	 */
	public byte getPlatformType() {
		return platformType;
	}

	/**
	 * Gets the value ticket.
	 *
	 * @return the value ticket
	 */
	public eTicket getValueTicket() {
		return valueTicket;
	}

}
