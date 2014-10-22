/*
 * TravelCard.java
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
	//Error status list for travel cards
	
	/** The OK status when extracting card content */
	public static final int STATUS_OK = 0;
	/** The Constant NO_HSL_CARD. */
	public static final int STATUS_NO_HSL_CARD = 1;
	/** The Constant HSL_CARD_DATA_FAILURE. */
	public static final int STATUS_HSL_CARD_DATA_FAILURE = 2;
	/** The Constant CARD_READ_FAILURE. */
	public static final int STATUS_CARD_READ_FAILURE = 3;
	/** The Constant HSL_CARDNUMBER_FAILURE. */
	public static final int STATUS_HSL_CARDNUMBER_FAILURE = 4;
	
	//HSL data file lengths
	/** The length of application information file. */
	public static final int APP_INFO_LEN = 11;
	/** The length of period pass file. */
	public static final int PERIOD_PASS_LEN = 32;
	/** The length of stored value file. */
	public static final int STORED_VALUE_LEN = 12;
	/** The length of eTicket file. */
	public static final int ETICKET_LEN = 26;
	/** The maximum length of history file. */
	public static final int HISTORY_MAX_LEN = 96;
	
	//Member variables for data extracted from ApplicationInformation file
	/** The application version. */
	private byte 	applicationVersion;
	/** The application instance id. */
	private String  applicationInstanceId;
	/** The platform type. */
	private byte	platformType;
	
	//Member variables for data extracted from PeriodPass file

	//First period product
	/** The product code1. */
	private short	productCode1;
	/** The validity area type1. */
	private byte	validityAreaType1;
	/** The validity area1. */
	private byte	validityArea1;
	/** The period start date1. */
	private Date	periodStartDate1;
	/** The period end date1. */
	private Date	periodEndDate1;
	/** The period length1. */
	private short	periodLength1;
	//Second period product
	/** The product code2. */
	private short	productCode2;
	/** The validity area type2. */
	private byte	validityAreaType2;
	/** The validity area2. */
	private byte	validityArea2;
	/** The period start date2. */
	private Date	periodStartDate2;
	/** The period end date2. */
	private Date	periodEndDate2;
	/** The period length2. */
	private short	periodLength2;
	//Period pass last loading info
	/** The loaded period product. */
	private short	loadedPeriodProduct;
	/** The period loading date. */
	private Date	periodLoadingDate;
	/** The loaded period length. */
	private short	loadedPeriodLength;
	/** The loaded period price. */
	private int		loadedPeriodPrice;
	/** The period loading organization. */
	private short	periodLoadingOrganisation;
	/** The period loading device number. */
	private short	periodLoadingDeviceNumber;
	//Period pass last boarding info
	/** The boarding date. */
	private Date	boardingDate;
	/** The boarding vehicle. */
	private short	boardingVehicle;
	/** The boarding location num type. */
	private byte	boardingLocationNumType;
	/** The boarding location num. */
	private short 	boardingLocationNum;
	/** The boarding direction. */
	private byte	boardingDirection;
	/** The boarding area. */
	private byte	boardingArea;

	//Member variables for data extracted from StoredValue file
	/** The stored value counter. */
	private int		valueCounter;
	/** The value loading date. */
	private Date	loadingDate;
	/** The value loading time. */
	private byte	loadingTime;
	/** The loaded value. */
	private int		loadedValue;
	/** The period loading organization. */
	private short	loadingOrganisationID;
	/** The period loading device number. */
	private short	loadingDeviceNumber;
	
	//Member variables for data extracted from History file
	/** The history fields. */
	private History[] historyFields = new History[8];	
	/** The history len. */
	private byte historyLen;

	//Member variable for error status of the travel card
	/** The error status. */
	private int errorStatus = 0;
	
	/** The value ticket. */
	private eTicket valueTicket;
	
	/**
	 * Instantiates a new travel card using given data from HSL card's files.
	 * <p>
	 * The lengths of the byte arrays to be passed as parameters have to be at least the lengths of the actual files.
	 * 
	 * @param appInfoBytes data from the ApplicationInformation file
	 * @param periodPassBytes data from the PeriodPass file
	 * @param storedValueBytes data from the StoredValue file
	 * @param eTicketBytes data from the eTicket file
	 * @param historyBytes data from the History file
	 * 
	 */
	public TravelCard(byte[] appInfoBytes, byte[] periodPassBytes, byte[] storedValueBytes, byte[] eTicketBytes, byte[] historyBytes)
	{
		//Check length of other files than history file
		if ( (appInfoBytes.length >= APP_INFO_LEN) && (periodPassBytes.length >= PERIOD_PASS_LEN) && (storedValueBytes.length >= STORED_VALUE_LEN) && (eTicketBytes.length >= ETICKET_LEN))
		{
			//Read data from application info
			readApplicationInfo(appInfoBytes);
			
			//Read period pass data
			readPeriodPass(periodPassBytes);
			
			//Read stored value
			readStoredValue(storedValueBytes);
			
			//read value ticket 
			valueTicket = new eTicket(eTicketBytes, false);
			
			//Read history data
			readHistory(historyBytes, historyBytes.length);
		}
		else
		{
			this.errorStatus = STATUS_HSL_CARD_DATA_FAILURE;
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
	 * @param applicationInformation byte array containing ApplicationInformation file data
	 */
	private void readApplicationInfo(byte[] applicationInformation)
	{
		//Read data from application info
		applicationVersion = (byte)(applicationInformation[0] & 0xF0);
		
		//Card number
		byte[] temp = new byte[9];
		System.arraycopy(applicationInformation, 1, temp, 0, 9);
		applicationInstanceId = Convert.getHexString(temp);
		
		//card platform type
		platformType = (byte)(applicationInformation[10] & 0xE0);
	}

	/**
	 * Extract period pass data from the card to the member variables.
	 *
	 * @param periodPass byte array containing PeriodPass file data
	 */
	private void readPeriodPass(byte[] periodPass)
	{
		//Extract file data to member variables
		
		//Read PERIOD PASS 1 data
		productCode1 =  (short) (((periodPass[0] & 0xFF) << 6) | ((periodPass[1] & 0xFC) >>> 2) );
		validityAreaType1 = (byte)( (periodPass[1] & 0x02) >>> 1);
		validityArea1 = (byte)( ((periodPass[1] & 0x01) << 3) | ((periodPass[2] & 0xE0) >>> 5));
		
		short date1 = (short)( ((periodPass[2] & 0x1F) << 9) | ((periodPass[3] & 0xFF) << 1) | ((periodPass[4] & 0x80) >>> 7));  
		periodStartDate1 = Convert.en5145Date2JavaDate(date1);
		
		short date2 = (short)( ((periodPass[4] & 0x7F) << 7) | ((periodPass[5] & 0xFE) >>> 1));  
		periodEndDate1 = Convert.en5145Date2JavaDate(date2);
		//Add time 23:59:59 to date in milliseconds to cover the whole day
		periodEndDate1.setTime(periodEndDate1.getTime() + (24L*60L*60L-1L)*1000L);
		//store period length
		periodLength1 = (short)(date2 - date1 + 1);
		
		//Read PERIOD PASS 2 data
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
		periodLoadingOrganisation = (short) (((periodPass[20] & 0x0F) << 10) | ((periodPass[21] & 0xFF) << 2) | ((periodPass[22] & 0xC0) >>> 6) );
		periodLoadingDeviceNumber = (short) (((periodPass[22] & 0x3F) << 8) | (periodPass[23] & 0xFF) );

		//LAST USE (BOARDING DATA)
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
	 * Extracts the stored value file fields to member variables.
	 * Inserts data into variables: 
	 * <p>
	 * {@code valueCounter, loadingDate, loadingTime, loadingValue, loadingOrganisationID, loadingDeviceNumber}
	 *
	 * @param storedValue the StoredValue file contents
	 */
	private void readStoredValue(byte[] storedValue)
	{
		//Value
		valueCounter = ((storedValue[0]&0xFF) << 12) | ((storedValue[1]&0xFF) << 4) | ((storedValue[2] & 0xF0) >>> 4);
		//Last value loading
		short date1 = (short)( ((storedValue[2] & 0x0F) << 10) | ((storedValue[3] & 0xFF) << 2) | ((storedValue[4] & 0xC0) >>> 6) );
		short time1 = (short)( ((storedValue[4] & 0x3F) << 5) | ((storedValue[5] & 0xF8) >>> 3) );
		loadingDate = Convert.en5145DateAndTime2JavaDate(date1, time1);
		loadedValue = ((storedValue[5] & 0x07) << 17) | ((storedValue[6]&0xFF) << 9) | ((storedValue[7]&0xFF) << 1) | ((storedValue[8] & 0x80) >>> 7);
		loadingOrganisationID = (short)( ((storedValue[8] & 0x7F) << 4) | ((storedValue[9] & 0xFE) >>> 1) );
		loadingDeviceNumber = (short)( ((storedValue[9] & 0x01) << 13) | ((storedValue[10] & 0xFF) << 5) | ((storedValue[11] & 0xF8) >>> 3) );
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
	 * Gets the application version.
	 *
	 * @return the application version
	 */
	public byte getApplicationVersion() {
		return applicationVersion;
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
	public byte getPlatformType() {
		return platformType;
	}
	
	/**
	 * Gets the first period pass product's product code.
	 *
	 * @return product code
	 */
	public short getProductCode1() {
		return productCode1;
	}
	
	/**
	 * Gets the first period pass product's validity area type.
	 *
	 * @return period's validity area type
	 */
	public byte getValidityAreaType1() {
		return validityAreaType1;
	}
	
	/**
	 * Gets the first period pass product's validity area.
	 *
	 * @return period's validity area
	 */
	public byte getValidityArea1() {
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
	public short getPeriodLength1() {
		return periodLength1;
	}
	
	/**
	 * Gets the second period pass product's product code.
	 *
	 * @return period's product code
	 */
	public short getProductCode2() {
		return productCode2;
	}
	
	/**
	 * Gets the second period pass product's validity area type.
	 *
	 * @return period's validity area type
	 */
	public byte getValidityAreaType2() {
		return validityAreaType2;
	}
	
	/**
	 * Gets the second period pass product's validity area.
	 *
	 * @return period's validity area
	 */
	public byte getValidityArea2() {
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
	public short getPeriodLength2() {
		return periodLength2;
	}
	
	/**
	 * Gets the amount of stored value on the card-
	 *
	 * @return the amount of stored value
	 */
	public int getValueCounter() {
		return valueCounter;
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
	public byte getHistoryLen(){
		return historyLen;
	}
	
	/**
	 * Gets the boarding area.
	 *
	 * @return the boarding area
	 */
	public byte getBoardingArea() {
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
	public short getBoardingVehicle() {
		return boardingVehicle;
	}
	
	/**
	 * Gets the boarding location num type.
	 *
	 * @return the boarding location num type
	 */
	public byte getBoardingLocationNumType() {
		return boardingLocationNumType;
	}
	
	/**
	 * Gets the boarding location num.
	 *
	 * @return the boarding location num
	 */
	public short getBoardingLocationNum() {
		return boardingLocationNum;
	}
	
	/**
	 * Gets the boarding direction.
	 *
	 * @return the boarding direction
	 */
	public byte getBoardingDirection() {
		return boardingDirection;
	}
	
	/**
	 * Gets the loaded period product.
	 *
	 * @return the loaded period product
	 */
	public short getLoadedPeriodProduct() {
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
	public short getLoadedPeriodLength() {
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
	public short getPeriodLoadingOrganisation() {
		return periodLoadingOrganisation;
	}
	
	/**
	 * Gets the period loading device number.
	 *
	 * @return the period loading device number
	 */
	public short getPeriodLoadingDeviceNumber() {
		return periodLoadingDeviceNumber;
	}

	/**
	 * The History class represents one history record from the History file. History file holds up to 7 records of transaction history.
	 * <p>
	 * History class stores following data from transaction:
	 * <ul>
	 * <li>{@code transactionDTime} - Date and time of boarding</li>
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

	    /**
    	 * Gets the transaction d time.
    	 *
    	 * @return the transaction d time
    	 */
    	public Date getTransactionDateTime() {return transactionDTime;}
	    
    	/**
    	 * Sets the transaction d time.
    	 *
    	 * @param in the new transaction d time
    	 */
    	void setTransactionDTime(Date in){ transactionDTime = in; }
	    
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
    	void setTransactionType(int in){ transactionType = in; }
	    
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
    	void setGroupSize(int in){ groupSize = in; }
	    
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
    	void setPrice(int in){ price = in; }
	}

	/**
	 * Gets the loading date.
	 *
	 * @return the loading date
	 */
	public Date getLoadingDate() {
		return loadingDate;
	}

	/**
	 * Gets the loading time.
	 *
	 * @return the loading time
	 */
	public byte getLoadingTime() {
		return loadingTime;
	}

	/**
	 * Gets the loaded value.
	 *
	 * @return the loaded value
	 */
	public int getLoadedValue() {
		return loadedValue;
	}

	/**
	 * Gets the loading organization.
	 *
	 * @return the loading organization
	 */
	public short getLoadingOrganizationID() {
		return loadingOrganisationID;
	}

	/**
	 * Gets the loading device number.
	 *
	 * @return the loading device number
	 */
	public short getLoadingDeviceNumber() {
		return loadingDeviceNumber;
	}

	/**
	 * Gets the set error status of the travel card.
	 *
	 * @return the error status
	 */
	public int getErrorStatus() {
		return errorStatus;
	}

}
