/*
 * SingleTicket.java
 *
 * Copyright (C) 2018 HSL/HRT (Helsingin seudun liikenne/ Helsinki Region Transport)
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
	/** Byte array to store single ticket data. */
	private byte[]	applicationInformationData = new byte[23];

	/** Byte array to store eTicket (i.e. value ticket) data. */
	private byte[]	eTicketData = new byte[41];

	/** The application version. */
	private int 	applicationVersion;

	/** The application key version. */
	private int 	applicationKeyVersion;

	/** The application instance id (18 numbers). */
	private String  applicationInstanceId; //18 numbers

	/** The platform type. */
	private int	platformType;

	/** Security level byte. */
	private int	securityLevel;

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

		//Copy raw data
		System.arraycopy(appInfoBytes, 0, applicationInformationData, 0, applicationInformationData.length);
		//Read data from application info
		readApplicationInfo(applicationInformationData);
		//Copy raw data
		System.arraycopy(eTicketBytes, 0, eTicketData, 0, eTicketData.length);
		//read value ticket
		// New read method for v2 single ticket
		if (applicationVersion == 2)
			valueTicket = new eTicket(eTicketData, true, 2, true);
		else
			valueTicket = new eTicket(eTicketData, true, 1, true);
	}

	/**
	 * Read and extract the application information data from the single ticket.
	 *
	 * @param appInfo the byte buffer containing ApplicationInformation data
	 */
	private void readApplicationInfo(byte[] appInfo)
	{
		//Read data from application info
		applicationVersion = Convert.getByteValue(appInfo, 128, 4);
		applicationKeyVersion = Convert.getByteValue(appInfo, 132, 4);

		byte[] temp = new byte[5];
		System.arraycopy(appInfo, 17, temp, 0, 5);
		applicationInstanceId = Convert.getHexString(temp);
		int num = ( (appInfo[1] ^ appInfo[5]) & 0x7F );
		num = (num << 8) + ((appInfo[2] ^ appInfo[6]) & 0xFF);
		num = (num << 8) + ((appInfo[4] ^ appInfo[7]) & 0xFF);

		applicationInstanceId = applicationInstanceId.concat(String.format("%07d", num)).concat(""+((appInfo[22] & 0xF0)>>>4));

		platformType = Convert.getByteValue(appInfo, 180, 3);
		securityLevel = Convert.getByteValue(appInfo, 183, 1);
	}

	/**
	 * Gets the application version.
	 *
	 * @return the version of the ticket application
	 */
	public int getApplicationVersion() {
		return applicationVersion;
	}

	/**
	 * Gets the application key version.
	 *
	 * @return the application key version
	 */
	public int getApplicationKeyVersion() {
		return applicationKeyVersion;
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
	public int getPlatformType() {
		return platformType;
	}

	/**
	 * Gets the security level.
	 *
	 * @return the security level
	 */
	public int getSecurityLevel() {
		return securityLevel;
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
