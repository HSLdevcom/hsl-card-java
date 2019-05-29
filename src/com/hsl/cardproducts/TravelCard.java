/*
 * TravelCard.java
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

import java.util.Date;
import com.hsl.util.Convert;

/**
 * The Class TravelCard stores all the data read from the HSL travel card.
 * When the class is instantiated it reads the raw file data from given parameters and extracts it to the member variables that can be read through provided getter methods.
 * The class can be instantiated using appropriate error status from reading of the travel card. This may be used as a simple way of forwarding the status of reading of the card from the card reading thread to UI thread.
 *
 * @author Bonwal Oy
 *
 */
public class TravelCard 
{
	/** The OK status when extracting card content */
	public static final int OK_STATUS = 0;
	/** The Constant NO_HSL_CARD. */
	public static final int NO_HSL_CARD = 1;
	/** The Constant HSL_CARD_DATA_FAILURE. */
	public static final int HSL_CARD_DATA_FAILURE = 2;
	/** The Constant CARD_READ_FAILURE. */
	public static final int CARD_READ_FAILURE = 3;
	/** The Constant HSL_CARDNUMBER_FAILURE. */
	public static final int HSL_CARDNUMBER_FAILURE = 4;

	//HSL data file arrays
	/** The application information data byte array */
	private byte[]	applicationInformationData = new byte[11];
	/** The control information data byte array */
    private byte[]	controlInformationData = new byte[6];
	/** The period pass data byte array */
	private byte[]	periodPassData = new byte[32];
	/** The stored value data byte array */
	private byte[]	storedValueData = new byte[12];
	/** The value ticket data byte array */
	private byte[]	eTicketData = new byte[26];
	/** The history data byte array */
	private byte[]	historyData = new byte[96];

	//Member variables for data extracted from ApplicationInformation file
	/** The application version. */
	private int 	applicationVersion;
	/** The application key version. */
	private int 	applicationKeyVersion;
	/** The application instance id. */
	private String  applicationInstanceId;
	/** The platform type. */
	private int		platformType;
	/** The security level. */
	private int		securityLevel;

	//Member variables for data extracted from PeriodPass file

	//First period product
	/** The product code1 type */
	private int		productCodeType1;
	/** The product code1. */
	private int		productCode1;
	/** The validity area type1. */
	private int		validityAreaType1;
	/** The validity area1. */
	private int		validityArea1;
	/** The period start date1. */
	private Date	periodStartDate1;
	/** The period end date1. */
	private Date	periodEndDate1;
	/** The period length1. */
	private int		periodLength1;

	//Second period product
	/** The product code2 type */
	private int		productCodeType2;
	/** The product code2. */
	private int		productCode2;
	/** The validity area type2. */
	private int		validityAreaType2;
	/** The validity area2. */
	private int		validityArea2;
	/** The period start date2. */
	private Date	periodStartDate2;
	/** The period end date2. */
	private Date	periodEndDate2;
	/** The period length2. */
	private int		periodLength2;

	//Period pass last loading info
	/** The loaded period product type. */
	private int		loadedPeriodProductType;
	/** The loaded period product. */
	private int		loadedPeriodProduct;
	/** The period loading date. */
	private Date	periodLoadingDate;
	/** The loaded period length. */
	private int		loadedPeriodLength;
	/** The loaded period price. */
	private int		loadedPeriodPrice;
	/** The period loading organization. */
	private int		periodLoadingOrganization;
	/** The period loading device number. */
	private int		periodLoadingDeviceNumber;

	//Period pass last boarding info
	/** The boarding date. */
	private Date	boardingDate;
	/** The boarding vehicle. */
	private int		boardingVehicle;
	/** The boarding location num type. */
	private int		boardingLocationNumType;
	/** The boarding location num. */
	private int 	boardingLocationNum;
	/** The boarding direction. */
	private int		boardingDirection;
	/** The boarding area. */
	private int		boardingArea;
	/** The boarding area type. */
	private int		boardingAreaType;

	// 8.11.2018
	// Application status
	private int		appStatus;

	//Member variables for data extracted from StoredValue file
	/** The stored value counter. */
	private int		storedValueCounter;

	//Member variables for data extracted from History file
	/** The history fields. */
	private History[] historyFields = new History[8];
	/** The history len. */
	private int		historyLen;

	// New, TLJ2014 spec card byte array tables
	/** The application information data byte array */
	private byte[]	applicationInformationData_v2 = new byte[11];
	/** The control information data byte array */
	private byte[]	controlInformationData_v2 = new byte[10];
	/** The period pass data byte array */
	private byte[]	periodPassData_v2 = new byte[35];
	/** The stored value data byte array */
	private byte[]	storedValueData_v2 = new byte[13];
	/** The value ticket data byte array */
	private byte[]	eTicketData_v2 = new byte[45];
	/** The history data byte array */
	private byte[]	historyData_v2 = new byte[96];

	/** Version of card
	 *	1 = Old, TLJ2010 spec
	 *	2 = New, TLJ2014 spec
	 */
	private int version = 0;

	//Member variable for error status of the travel card
	/** The error status. */
	public int errorStatus = 0;

	/** The value ticket. */
	private eTicket valueTicket;

	/**
	 * Instantiates a new travel card using given data from HSL card's files.
	 * <p>
	 * The lengths of the byte arrays to be passed as parameters have to be at least the lengths of the actual files.
	 *
	 * @param appInfoBytes data from the ApplicationInformation file
	 * @param controlInfoBytes data from the ControlInformation file
	 * @param periodPassBytes data from the PeriodPass file
	 * @param storedValueBytes data from the StoredValue file
	 * @param eTicketBytes data from the eTicket file
	 * @param historyBytes data from the History file
	 * @param version version code for card (1 or 2)
	 *
	 */
	public TravelCard(byte[] appInfoBytes, byte[] controlInfoBytes, byte[] periodPassBytes, byte[] storedValueBytes, byte[] eTicketBytes, byte[] historyBytes, int version)
	{
		this.version = version;
		// Check card version
		if (version == 2) {
			//Copy raw data
			System.arraycopy(appInfoBytes, 0, applicationInformationData_v2, 0, applicationInformationData_v2.length);
			System.arraycopy(controlInfoBytes, 0, controlInformationData_v2, 0, controlInformationData_v2.length);
			System.arraycopy(periodPassBytes, 0, periodPassData_v2, 0, periodPassData_v2.length);
			System.arraycopy(storedValueBytes, 0, storedValueData_v2, 0, storedValueData_v2.length);
			System.arraycopy(eTicketBytes, 0, eTicketData_v2, 0, eTicketData_v2.length);
			System.arraycopy(historyBytes, 0, historyData_v2, 0, historyBytes.length);

			//Read data from application info
			readApplicationInfo(applicationInformationData_v2);

			// Read control info from bytes
			readControlInfo(controlInformationData_v2);

			//Read period pass data
			readPeriodPass_v2(periodPassData_v2); // readPeriodPass_v2(periodPassData_v2);

			//Read stored value
			readStoredValue(storedValueData_v2);

			//read value ticket
			valueTicket = new eTicket(eTicketData_v2, false, 2, false);

			//Read history data
			readHistory_v2(historyData_v2, historyBytes.length);
		}
		else {
			//Copy raw data
			System.arraycopy(appInfoBytes, 0, applicationInformationData, 0, applicationInformationData.length);
			System.arraycopy(controlInfoBytes, 0, controlInformationData, 0, controlInformationData.length);
			System.arraycopy(periodPassBytes, 0, periodPassData, 0, periodPassData.length);
			System.arraycopy(storedValueBytes, 0, storedValueData, 0, storedValueData.length);
			System.arraycopy(eTicketBytes, 0, eTicketData, 0, eTicketData.length);
			System.arraycopy(historyBytes, 0, historyData, 0, historyBytes.length);

			//Read data from application info
			readApplicationInfo(applicationInformationData);

			// Read control info from bytes
			readControlInfo(controlInformationData);

			//Read period pass data
			readPeriodPass(periodPassData);

			//Read stored value
			readStoredValue(storedValueData);

			//read value ticket
			valueTicket = new eTicket(eTicketData, false, 1, false);

			//Read history data
			readHistory(historyData, historyBytes.length);
		}
	}

	/**
	 * Instantiates a new travel card without any data but sets the error status.
	 * This can be used to instantiate the class to be passed on after card reading errors.
	 * This way it is possible to use single point of error handling for card reading and data extracting.
	 *
	 * @param errorStatus the error status
	 */
	public TravelCard(int errorStatus)
	{
		this.errorStatus = errorStatus;
	}

	/**
	 * Extract application information data from the card to the member variables.
	 *
	 * @param appInfo byte array containing ApplicationInformation file data
	 */
	private void readApplicationInfo(byte[] appInfo)
	{
		//Read data from application info
		applicationVersion = (byte)(appInfo[0] & 0xF0);
		applicationKeyVersion = (byte)(appInfo[0] & 0x0F);
		
		byte[] temp = new byte[9];
		System.arraycopy(appInfo, 1, temp, 0, 9);
		applicationInstanceId = Convert.getHexString(temp);
		
		platformType = (byte)(appInfo[10] & 0xE0);
		securityLevel = (byte)(appInfo[10] & 0x10);
	}

	/**
	 * Extract control information data from the card to the member variables.
	 *
	 * @param ctrlInfo byte array containing ControlInformation file data
	 */
	private void readControlInfo(byte[] ctrlInfo) {
		appStatus = Convert.getByteValue(ctrlInfo, 14, 1);
	}

	/**
	 * Extract period pass data from the card to the member variables.
	 *
	 * @param periodPass byte array containing PeriodPass file data
	 */
	private void readPeriodPass(byte[] periodPass)
	{
		productCode1 =  (short) (((periodPass[0] & 0xFF) << 6) | ((periodPass[1] & 0xFC) >>> 2) );
		validityAreaType1 = (byte)( (periodPass[1] & 0x02) >>> 1);
		validityArea1 = (byte)( ((periodPass[1] & 0x01) << 3) | ((periodPass[2] & 0xE0) >>> 5));
		
		short date1 = (short)( ((periodPass[2] & 0x1F) << 9) | ((periodPass[3] & 0xFF) << 1) | ((periodPass[4] & 0x80) >>> 7));  
		periodStartDate1 = Convert.en5145Date2JavaDate(date1);
		
		short date2 = (short)( ((periodPass[4] & 0x7F) << 7) | ((periodPass[5] & 0xFE) >>> 1));  
		periodEndDate1 = Convert.en5145Date2JavaDate(date2);
		//Add time 23:59:59 to date in milliseconds
		periodEndDate1.setTime(periodEndDate1.getTime() + (24L*60L*60L-1L)*1000L);
		//store period length
		periodLength1 = (short)(date2 - date1 + 1);
		
		//Read period pass 2 data
		productCode2 =  (short) (((periodPass[6] & 0xFF) << 8) | (periodPass[7] & 0xFC));
		productCode2 >>>= 2;
		validityAreaType2 = (byte)(periodPass[7] & 0x02);
		validityArea2 = (byte)( ((periodPass[7] & 0x01) << 3) | ((periodPass[8] & 0xE0) >>> 5));
		
		date1 = (short)( ((periodPass[8] & 0x1F) << 9) | ((periodPass[9] & 0xFF) << 1) | ((periodPass[10] & 0x80) >>> 7));  
		periodStartDate2 = Convert.en5145Date2JavaDate(date1);
		
		date2 = (short)( ((periodPass[10] & 0x7F) << 7) | ((periodPass[11] & 0xFE) >>> 1));  
		periodEndDate2 = Convert.en5145Date2JavaDate(date2);
		//Add time 23:59:59 to date in milliseconds
		periodEndDate2.setTime(periodEndDate2.getTime() + (24L*60L*60L-1L)*1000L);
		
		//store period length
		periodLength2 = (short)(date2 - date1 + 1);
		
		//LAST LOADING
		loadedPeriodProduct =  (short) (((periodPass[12] & 0xFF) << 6) | ((periodPass[13] & 0xFC) >>> 2) );
		date1 = (short)( ((periodPass[13] & 0x03) << 12) | ((periodPass[14] & 0xFF) << 4) | ((periodPass[15] & 0xF0) >>> 4) );
		short time1 = (short)( ((periodPass[15] & 0x0F) << 7) | ((periodPass[16] & 0xFE) >>> 1) );
		periodLoadingDate = Convert.en5145DateAndTime2JavaDate(date1, time1);
		loadedPeriodLength = (short)( ((periodPass[16] & 0x01) << 8) | (periodPass[17] & 0xFF) );
		loadedPeriodPrice = (int)((periodPass[18] & 0xFF) << 12) | ((periodPass[19] & 0xFF) << 4) | ((periodPass[20] & 0xF0) >>> 4);
		periodLoadingOrganization = (short) (((periodPass[20] & 0x0F) << 10) | ((periodPass[21] & 0xFF) << 2) | ((periodPass[22] & 0xC0) >>> 6) );
		periodLoadingDeviceNumber = (short) (((periodPass[22] & 0x3F) << 8) | (periodPass[23] & 0xFF) );

		//LAST USE (BOARDING)
		date1 = (short) (((periodPass[24] & 0xFF) << 6) | ((periodPass[25] & 0xFC) >>> 2) );
		time1 = (short)( ((periodPass[25] & 0x03) << 9) | ((periodPass[26] & 0xFF) << 1) | ((periodPass[27] & 0x80) >>> 7) );
		boardingDate = Convert.en5145DateAndTime2JavaDate(date1, time1);
		boardingVehicle = (short)( ((periodPass[27] & 0x7F) << 7) | (periodPass[28] & 0xFE) >>> 1);
		boardingLocationNumType = (byte)( ((periodPass[28] & 0x01) << 1) | ((periodPass[29] & 0x80) >>> 7) );
		boardingLocationNum = (short)( ((periodPass[29] & 0x7F) << 7) | ((periodPass[30] & 0xFE) >>> 1) );
		boardingDirection = (byte) (periodPass[30] & 0x01);
		boardingArea = (byte)((periodPass[31] & 0xF0) >>> 4);

	}

	/**
	 * Extract period pass data from the card to the member variables for new, TLJ2014 card.
	 *
	 * @param periodPass byte array containing PeriodPass file data
	 */
	private void readPeriodPass_v2(byte[] periodPass)
	{
//		NEW
//		//Read period pass 1 data
		productCodeType1			= Convert.getByteValue  (periodPass, 0, 1);
		productCode1 				= Convert.getShortValue	(periodPass, 1, 14);
		validityAreaType1			= Convert.getByteValue	(periodPass, 15, 2);
		validityArea1				= Convert.getShortValue	(periodPass, 17, 6);
		int date1					= Convert.getShortValue	(periodPass, 23, 14);
		int date2					= Convert.getShortValue	(periodPass, 37, 14);
		periodStartDate1 			= Convert.en5145Date2JavaDate(date1);
		periodEndDate1 				= Convert.en5145Date2JavaDate(date2);
		//store period length
		periodLength1 				=	(short)(date2 - date1 + 1);

		//Read PERIOD PASS 2 data
		productCodeType2			= Convert.getByteValue  (periodPass, 56, 1);
		productCode2 				= Convert.getShortValue	(periodPass, 57, 14);
		validityAreaType2			= Convert.getByteValue	(periodPass, 71, 2);
		validityArea2				= Convert.getShortValue	(periodPass, 73, 6);
		date1			    		= Convert.getShortValue	(periodPass, 79, 14);
		date2			   	 		= Convert.getShortValue	(periodPass, 93, 14);
		periodStartDate2			= Convert.en5145Date2JavaDate(date1);
		periodEndDate2 				= Convert.en5145Date2JavaDate(date2);
		//store period length
		periodLength2 				=	(short)(date2 - date1 + 1);

        //LAST LOADING
		loadedPeriodProductType		= Convert.getByteValue	(periodPass, 112, 1);
        loadedPeriodProduct 		= Convert.getShortValue (periodPass, 113, 14);
        date1               		= Convert.getShortValue (periodPass, 127, 14);
        int time1           		= Convert.getShortValue (periodPass, 141, 11);
        periodLoadingDate   		= Convert.en5145DateAndTime2JavaDate(date1, time1);
        loadedPeriodLength  		= Convert.getShortValue (periodPass, 152, 9);
        loadedPeriodPrice   		= Convert.getShortValue (periodPass, 161, 20);
        periodLoadingOrganization 	= Convert.getShortValue(periodPass, 181, 14);
        periodLoadingDeviceNumber 	= Convert.getShortValue(periodPass, 195, 13);

        //LAST USE (BOARDING)
		date1						= Convert.getShortValue (periodPass, 208, 14);
		time1           			= Convert.getShortValue (periodPass, 222, 11);
		boardingDate 				= Convert.en5145DateAndTime2JavaDate(date1, time1);
		boardingVehicle				= Convert.getShortValue (periodPass, 233, 14);
		boardingLocationNumType 	= Convert.getShortValue (periodPass, 247, 2)	;
		boardingLocationNum			= Convert.getShortValue (periodPass, 249, 14);
		boardingDirection			= Convert.getByteValue  (periodPass, 263, 1);
		boardingAreaType			= Convert.getByteValue  (periodPass, 264, 2);
		boardingArea				= Convert.getByteValue  (periodPass, 266, 6);
	}

	/**
	 * Extracts the stored value file fields to member variables.
	 * Inserts data into variables:
	 * <p>
	 * {@code valueCounter, loadingDate, loadingTime, loadingValue, loadingOrganisationID, loadingDeviceNumber}
	 *
	 * @param storedValue the StoredValue file contents
	 */
	private void readStoredValue(byte[] storedValue)
	{
		storedValueCounter = ((storedValue[0]&0xFF) << 12) | ((storedValue[1]&0xFF) << 4) | ((storedValue[2] & 0xF0) >>> 4);
	}

	/**
	 * Read history.
	 *
	 * @param historyData the history data
	 * @param length the length
	 */
	private void readHistory(byte[] historyData, int length)
	{
		//count history data fields
		int dataCount = length / 12;
		//set history count initially to zero 
		historyLen = 0;
		
		for (int i=0; i < dataCount; i++)
		{
			//check if current field seems to contain data (some date and time bytes are not zeroes)
			if ( (historyData[i*12+1] != (byte)0) || (historyData[i*12+2] != (byte)0) || (historyData[i*12+3] != (byte)0) || (historyData[i*12+4] != (byte)0) )
			{
				//allocate memory for new history field
				historyFields[historyLen] = new History();
				//Store transaction type 
				historyFields[historyLen].setTransactionType((byte)((historyData[i*12+0] & 0x80) >>> 7)); 
				//Get transaction date and time (date from transfer end date, and time from boarding time)
				short date = (short)( ((historyData[i*12+3] & 0x3F) << 8) | (historyData[i*12+4] & 0xFF) );
				short time = (short)( ((historyData[i*12+1] & 0x01) << 10) | ((historyData[i*12+2] & 0xFF) << 2) | ((historyData[i*12+3] & 0xC0) >>> 6) );
				short endTime = (short)( ((historyData[i*12+5] & 0xFF) << 3) | ((historyData[i*12+6] & 0xE0) >>> 5) );
				//if transfer end time is before boarding time, the day has changed after boarding 
				//and we have to subtract one day from the transfer end date to get real boarding date
				if (endTime < time)
					date -= 1;
				
				//set visible boarding date and time
				historyFields[historyLen].setTransactionDTime(Convert.en5145DateAndTime2JavaDate(date, time));

				//Get group size
				historyFields[historyLen].setGroupSize( (byte)((historyData[i*12+8] & 0x7C) >>> 2) );
				//Get value ticket price
				historyFields[historyLen].setPrice( (short)(((historyData[i*12+6] & 0x1F) << 9) | ((historyData[i*12+7] & 0xFF) << 1) | ((historyData[i*12+8] & 0x80) >>> 7)) );
				
				//increment counter
				historyLen++;
			}
		}
		
	}

	/**
	 * Read history for new, TLJ2014 card.
	 *
	 * @param historyData the history data
	 * @param length the length
	 */
	private void readHistory_v2(byte[] historyData, int length)
	{
		//count history data fields
		int dataCount = length / 12;
		//set history count initially to zero
		historyLen = 0;

		for (int i=0; i < dataCount; i++)
		{
			//check if current field seems to contain data (some date and time bytes are not zeroes)
			if ( (historyData[i*12+1] != (byte)0) || (historyData[i*12+2] != (byte)0) || (historyData[i*12+3] != (byte)0) || (historyData[i*12+4] != (byte)0) )
			{
				//allocate memory for new history field
				historyFields[historyLen] = new History();
				//Store transaction type
				historyFields[historyLen].setTransactionType(Convert.getByteValue(historyData, 0 + (i * 96), 1));
				//Get transaction date and time (date from transfer end date, and time from boarding time)
				int date 	= Convert.getShortValue(historyData, 1 	+ (i * 96), 14);
				int time 	= Convert.getShortValue(historyData, 15 + (i * 96), 11);

                //set visible boarding date and time
                historyFields[historyLen].setTransactionDTime(Convert.en5145DateAndTime2JavaDate(date, time));

                // 15.11.2018 Joni
                // Add end dates
                date 	= Convert.getShortValue(historyData, 26	+ (i * 96), 14);
                time 	= Convert.getShortValue(historyData, 40 + (i * 96), 11);
                historyFields[historyLen].setTransferEndDate(Convert.en5145DateAndTime2JavaDate(date, time));

				//Get value ticket price
				historyFields[historyLen].setPrice( Convert.getShortValue(historyData, 51 + (i * 96), 14) );
				//Get group size
				historyFields[historyLen].setGroupSize( Convert.getByteValue(historyData, 65 + (i * 96), 6) );
				//increment counter
				historyLen++;
			}
		}

	}

	/**
	 * Gets the application version.
	 *
	 * @return the application version
	 */
	public int getApplicationVersion() {
		return applicationVersion;
	}

	/**
	 * Gets the card application key version.
	 *
	 * @return the application key version int
	 */
	public int getApplicationKeyVersion() {
		return applicationKeyVersion;
	}

	/**
	 * Gets the card number as a string form the card.
	 *
	 * @return the application instance id string
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
	 * Gets the first period pass product's product code type.
	 *
	 * @return product code type
	 */
	public int getProductCodeType1() { return productCodeType1; }

	/**
	 * Gets the first period pass product's product code.
	 *
	 * @return product code
	 */
	public int getProductCode1() {
		return productCode1;
	}

	/**
	 * Gets the first period pass product's validity area type.
	 *
	 * @return period's validity area type
	 */
	public int getValidityAreaType1() {
		return validityAreaType1;
	}

	/**
	 * Gets the first period pass product's validity area.
	 *
	 * @return period's validity area
	 */
	public int getValidityArea1() {
		return validityArea1;
	}

	/**
	 * Gets the first period pass product's period start date.
	 *
	 * @return period's start date
	 */
	public Date getPeriodStartDate1() {
		return periodStartDate1;
	}

	/**
	 * Gets the first period pass product's period end date.
	 *
	 * @return period's end date
	 */
	public Date getPeriodEndDate1() {
		return periodEndDate1;
	}

	/**
	 * Gets the first period pass product's period length.
	 *
	 * @return length of the period
	 */
	public int getPeriodLength1() {
		return periodLength1;
	}

	/**
	 * Gets the second period pass product's product code type.
	 *
	 * @return period's product code type
	 */
	public int getProductCodeType2() { return productCodeType2; }

	/**
	 * Gets the second period pass product's product code.
	 *
	 * @return period's product code
	 */
	public int getProductCode2() {
		return productCode2;
	}

	/**
	 * Gets the second period pass product's validity area type.
	 *
	 * @return period's validity area type
	 */
	public int getValidityAreaType2() {
		return validityAreaType2;
	}

	/**
	 * Gets the second period pass product's validity area.
	 *
	 * @return period's validity area
	 */
	public int getValidityArea2() {
		return validityArea2;
	}

	/**
	 * Gets the second period pass product's period start date.
	 *
	 * @return period's start date
	 */
	public Date getPeriodStartDate2() {
		return periodStartDate2;
	}

	/**
	 * Gets the second period pass product's period end date.
	 *
	 * @return period's end date
	 */
	public Date getPeriodEndDate2() {
		return periodEndDate2;
	}

	/**
	 * Gets the second period pass product's period length.
	 *
	 * @return length of the period
	 */
	public int getPeriodLength2() {
		return periodLength2;
	}

	/**
	 * Gets the amount of stored value on the card-
	 *
	 * @return the amount of stored value
	 */
	public int getStoredValueCounter() {
		return storedValueCounter;
	}

	/**
	 * Gets the eTicket instance that represents the latests value ticket on the card.
	 *
	 * @return the value ticket instance
	 */
	public eTicket getValueTicket() {
		return valueTicket;
	}

	/**
	 * Gets the card usage history data. The array contains at maximum seven latest boarding events
	 *
	 * @return the history
	 */
	public History[] getHistory() {
		return historyFields;
	}

	/**
	 * Gets the number of elements in the history array.
	 *
	 * @return number of elements in the card's @see History[] array
	 */
	public int getHistoryLen(){
		return historyLen;
	}

	/**
	 * Gets the boarding area type.
	 *
	 * @return the boarding area type
	 */
	public int getBoardingAreaType() { return boardingAreaType; }

	/**
	 * Gets the boarding area.
	 *
	 * @return the boarding area
	 */
	public int getBoardingArea() {
		return boardingArea;
	}

	/**
	 * Gets the boarding date.
	 *
	 * @return the boarding date
	 */
	public Date getBoardingDate() {
		return boardingDate;
	}

	/**
	 * Gets the boarding vehicle.
	 *
	 * @return the boarding vehicle
	 */
	public int getBoardingVehicle() {
		return boardingVehicle;
	}

	/**
	 * Gets the boarding location num type.
	 *
	 * @return the boarding location num type
	 */
	public int getBoardingLocationNumType() {
		return boardingLocationNumType;
	}

	/**
	 * Gets the boarding location num.
	 *
	 * @return the boarding location num
	 */
	public int getBoardingLocationNum() {
		return boardingLocationNum;
	}

	/**
	 * Gets the boarding direction.
	 *
	 * @return the boarding direction
	 */
	public int getBoardingDirection() {
		return boardingDirection;
	}

	/**
	 * Gets the loaded period product type.
	 *
	 * @return the loaded period product type
	 */
	public int getLoadedPeriodProductType() { return loadedPeriodProductType; }

	/**
	 * Gets the loaded period product.
	 *
	 * @return the loaded period product
	 */
	public int getLoadedPeriodProduct() {
		return loadedPeriodProduct;
	}

	/**
	 * Gets the period loading date.
	 *
	 * @return the period loading date
	 */
	public Date getPeriodLoadingDate() {
		return periodLoadingDate;
	}

	/**
	 * Gets the loaded period length.
	 *
	 * @return the loaded period length
	 */
	public int getLoadedPeriodLength() {
		return loadedPeriodLength;
	}

	/**
	 * Gets the loaded period price.
	 *
	 * @return the loaded period price
	 */
	public int getLoadedPeriodPrice() {
		return loadedPeriodPrice;
	}

	/**
	 * Gets the period loading organization.
	 *
	 * @return the period loading organization
	 */
	public int getPeriodLoadingOrganization() {
		return periodLoadingOrganization;
	}

	/**
	 * Gets the period loading device number.
	 *
	 * @return the period loading device number
	 */
	public int getPeriodLoadingDeviceNumber() {
		return periodLoadingDeviceNumber;
	}

	/**
	 * Gets the app status.
	 *
	 * @return the app status
	 */
	public int getAppStatus() {
		return appStatus;
	}

	/**
	 * Gets the card version.<br>
	 *	1 = Old, TLJ2010 spec<br>
	 *	2 = New, TLJ2014 spec<br>
	 * @return the card version.
	 */
	public int getVersion() { return version; }

	/**
	 * The History class represents one history record from the History file. History file holds up to 7 records of transaction history.
	 * <p>
	 * History class stores following data from transaction:
	 * <ul>
	 * <li>{@code transactionDTime} - Date and time of boarding</li>
	 * <li>{@code transferEndDate} - Date and time of transfer end</li>
	 * <li>{@code transactionType} - Transaction type (season journey or value ticket)</li>
	 * <li>{@code groupSize} - Amount of tickets included in the value ticket (used only with value tickets)</li>
	 * <li>{@code price} - The price of the value ticket (used only with value tickets)</li>
	 * </ul>
	 */
	public class History {

		/** The transaction d time. */
	    private Date transactionDTime;
		/** The transaction type. */
	    private int transactionType;
		/** The group size. */
	    private int groupSize;
		/** The price. */
	    private int price;

	    // 15.11.2018
        // Transfer end date field
        private Date transferEndDate;

		/**
		 * Gets the transaction d time.
		 *
		 * @return the transaction d time
		 */
	    public Date getTransactionDTime() {return transactionDTime;}

		/**
		 * Sets the transaction d time.
		 *
		 * @param in the new transaction d time
		 */
		public void setTransactionDTime(Date in){ transactionDTime = in; }

		/**
		 * Gets the transaction type.
		 *
		 * @return the transaction type
		 */
		public int getTransactionType(){return transactionType;}

		/**
		 * Sets the transaction type.
		 *
		 * @param in the new transaction type
		 */
		public void setTransactionType(int in){ transactionType = in; }

		/**
		 * Gets the group size.
		 *
		 * @return the group size
		 */
		public int getGroupSize(){return groupSize;}

		/**
		 * Sets the group size.
		 *
		 * @param in the new group size
		 */
		public void setGroupSize(int in){ groupSize = in; }

		/**
		 * Gets the price.
		 *
		 * @return the price
		 */
		public int getPrice(){return price;}

		/**
		 * Sets the price.
		 *
		 * @param in the new price
		 */
		public void setPrice(int in){ price = in; }

		/**
		 * Gets the transfer end date.
		 *
		 * @return the transfer end date
		 */
		public Date getTransferEndDate() { return transferEndDate; }

		/**
		 * Sets the transfer end date.
		 *
		 * @param transferEndDate new end date
		 */
        public void setTransferEndDate(Date transferEndDate) {
            this.transferEndDate = transferEndDate;
        }
    }
}
