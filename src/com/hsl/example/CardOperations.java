/*
 * CardOperations.java
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

package com.hsl.example;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import android.content.Context;

import com.hsl.cardlibrary.R;
import com.hsl.cardproducts.SingleTicket;
import com.hsl.cardproducts.TravelCard;
import com.hsl.cardproducts.TravelCard.History;
import com.hsl.cardproducts.eTicket;
import com.hsl.util.ValidityAreaMappings;

/**
 * The class CardOperations provides an code example about how to read files from HSL card, 
 * how to create instance of TravelCard or SingleTicket class with the read data
 * and how to read information from created instances. 
 * <p>
 * The descriptions of TravelCard and SingleTicket classes include brief description of how to read the data from the cards 
 * and how to instantiate classes. The full example is in the source code of this class.
 * <p>
 * @author Bonwal Oy 
 *   
 */
public class CardOperations 
{
	
	/** Select command for the HSL application. */
	private static byte[] selectHslCommand = {(byte)0x90, (byte)0x5A, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x11, (byte)0x20, (byte)0xEF, (byte)0x00};
	
	/** the command for reading the ApplicationInformation file. */
	private static byte[] readAppinfoCommand = {(byte)0x90, (byte)0xBD, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x08, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0B, (byte)0x00, (byte)0x00, (byte)0x00};
	
	/** The command for reading the PeriodPass file. */
	private static byte[] readPeriodpassCommand = {(byte)0x90, (byte)0xBD, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x20, (byte)0x00, (byte)0x00, (byte)0x00};
	
	/** The command for reading the StoredValue file. */
	private static byte[] readStoredvalueCommand = {(byte)0x90, (byte)0xBD, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x00, (byte)0x00, (byte)0x00};
	
	/** The command for reading the eTicket file. */
	private static byte[] readETicketCommand = {(byte)0x90, (byte)0xBD, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x1A, (byte)0x00, (byte)0x00, (byte)0x00};
	
	/** The command for reading the History file. */
	private static byte[] readHistoryCommand = {(byte)0x90, (byte)0xBB, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
	
	/** The command for reading last part of the History file. */
	private static byte[] readNextCommand = {(byte)0x90, (byte)0xAF, (byte)0x00, (byte)0x00, (byte)0x00};

	/** OK response form the card. */
	private static byte[] OK = { (byte) 0x91, (byte) 0x00 };
	
	/** There's more data to be read -response from the card. */
	private static byte[] MORE_DATA = { (byte) 0x91, (byte) 0xAF };

	/** Static definitions for the type of ticket validity length*/
	private static final int MINUTES = 0, HOURS = 1, ALLDAY = 2, DAYS =3;

	/**
	 * This method reads the travel card data from HSL Mifare DESFire card.
	 * <p>
	 * In the description below, only few main points are brought up and the full example can be found from the source code.<br>
	 * To read the data from HSL travel cards, the first step after detection of the card is to select the HSL card application:
	 * <p>
	 * <pre style="font-size:1.0em;">
	 * {@code 
	 * //Select HSL application
	 * selection = ISOCard.transceive(selectHslCommand);
	 * }
	 * </pre>
	 * <p>
	 * After successful selection the next step is to read the content of the HSL-files in the HSL card application:
	 * <p>
	 * <pre style="font-size:1.0em;">
	 * {@code 
	 * //Read ApplicationInformation
	 * appInfo = ISOCard.transceive(readAppinfoCommand);
	 * //Read PeriodPass
	 * periodPass = ISOCard.transceive(readPeriodpassCommand);
	 * //Read StoredValue
	 * storedValue = ISOCard.transceive(readStoredvalueCommand);
	 * //Read eTicket
	 * eTicket = ISOCard.transceive(readETicketCommand);
	 * //Read History
	 * hist1 = ISOCard.transceive(readHistoryCommand);
	 * }
	 * </pre>
	 * For reading the whole history data file you need to make another read command (see the source code), 
	 * but after successful reading of files the last step is to create an instance of the TravelCard class with the data read from the files
	 * <p>
	 * <pre style="font-size:1.0em;">
	 * {@code 
	 * //Create and return instance of new TravelCard
	 * return new TravelCard(appInfo, periodPass, storedValue, eTicket, history);
	 * }
	 * </pre>
	 * Now all the data from the files is extracted and is ready to use from the TravelCard class instance.
	 * <p>
	 * <b>Note that the android.nfc.tech.IsoDep.transceive method is an I/O operation and will block until complete. It must not be called from the main application thread.</b>
	 * 
	 * @param ISOCard as instance of read android.nfc.tech.IsoDep
	 * @return Instance of created TravelCard class
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static TravelCard readTravelCardData(android.nfc.tech.IsoDep ISOCard) throws IOException
	{
		//Temporary byte arrays 
		byte[]	appInfo, periodPass, storedValue, eTicket, history, selection;
		byte[] 	hist1, hist2 = new byte[2];

		//Start reading

		//Select HSL application
		selection = ISOCard.transceive(selectHslCommand);
		if ( Arrays.equals(selection, OK))
		{
			//Selection ok, read files
			
			//Read ApplicationInformation
			appInfo = ISOCard.transceive(readAppinfoCommand);

			//Read PeriodPass
			periodPass = ISOCard.transceive(readPeriodpassCommand);
	
			//Read StoredValue
			storedValue = ISOCard.transceive(readStoredvalueCommand);

			//Read eTicket
			eTicket = ISOCard.transceive(readETicketCommand);

			//Read History
			hist1 = ISOCard.transceive(readHistoryCommand);
 
			//Check if more history data is waiting on the card
			if (Arrays.equals( Arrays.copyOfRange(hist1, hist1.length-2, hist1.length), MORE_DATA))
			{
				//Read rest of the history data
				hist2 = ISOCard.transceive(readNextCommand);
			}

			//Combine the two read history data blocks
			history = new byte[hist1.length-2 + hist2.length-2];
			System.arraycopy(hist1, 0, history, 0, hist1.length-2);
			System.arraycopy(hist2, 0, history, hist1.length-2, hist2.length-2);
			
			//Create and return instance of new TravelCard
			return new TravelCard(appInfo, periodPass, storedValue, eTicket, history);
		}
		else
		{
			//Set HSL application select error status to travel card instance (or throw exception etc.)
			return new TravelCard(TravelCard.STATUS_NO_HSL_CARD);
		}		
	}

	/**
	 * Read the single ticket card data from HSL single ticket card.
	 * <p>
	 * In the description below, only few main points are brought up and the full example can be found from the source code.<br>
	 * For reading the HSL single ticket's data from the ticket the first step after the card detection is to read all the data from the single ticket.
	 * <p>
	 * <pre style="font-size:1.0em;">
	 * {@code 
	 * //Read 4 data blocks (16 bytes at a time)
	 * for (int i=0; i < 4; i++)
	 * {
	 * 	pages = ulCard.readPages(i*4);
	 * 	System.arraycopy(pages, 0, bytes, i*16, pages.length);
	 * }
	 * }
	 * </pre>
	 * After successful reading the data needs to be divided into logical parts (Application Information and eTicket),
	 * which are given as parameters to the SingleTicket class constructor.
	 * <p>
	 * <pre style="font-size:1.0em;">
	 * {@code 
	 * //Get ApplicationInformation data from ultralight card's data
	 * System.arraycopy(bytes, 0, appinfodata, 0, appinfodata.length);
	 * //Get eTicket data from ultralight card's data
	 * System.arraycopy(bytes, 23, eticketdata, 0, eticketdata.length);
	 *
	 * //Create and return instance of new SingleTicket
	 * return new SingleTicket(appinfodata, eticketdata);
	 * }
	 * </pre>
	 *
	 * <b>Note that the android.nfc.tech.MifareUltralight.readPages method is an I/O operation and will block until complete. It must not be called from the main application thread.</b>
	 * 
	 * @param ulCard the read single ticket as instance of android.nfc.tech.MifareUltralight
	 * @return Instance of created SingleTicket class.
	 * @throws IOException Signals that an I/O exception has occurred.
	 * 
	 */
	public static SingleTicket readSingleTicketData(android.nfc.tech.MifareUltralight ulCard) throws IOException
	{
		//Byte array for all card data 
		byte[] bytes = new byte[64];
		byte[] pages;

		//Byte arrays for application information and eTicket data 
		byte[] appinfodata = new byte[23];
		byte[] eticketdata = new byte[41];

		//Start the reading of ultralight card
		
		//Read 4 data blocks (16 bytes at a time)
		for (int i=0; i < 4; i++)
		{
			pages = ulCard.readPages(i*4);
			System.arraycopy(pages, 0, bytes, i*16, pages.length);
		}

		//Get ApplicationInformation data from ultralight card's data
		System.arraycopy(bytes, 0, appinfodata, 0, appinfodata.length);
		//Get eTicket data from ultralight card's data
		System.arraycopy(bytes, 23, eticketdata, 0, eticketdata.length);

		//Create and return instance of new SingleTicket
		return new SingleTicket(appinfodata, eticketdata);
	}

	/**
	 * Example of how to get travel card's period, value, value ticket and history strings from previously read HSL travel card.
	 * <p>
	 * Look the commented source code to see the example about how to use the data from the TravelCard class to output the various information from HSL travel cards.
	 * <p>
	 * 
	 * @param card the travel card data as instance of com.hsl.cardproducts.TravelCard
	 * @param app_context the Android application context that is needed to get the string resources used by the library
	 * @return String representing travel card's value.
	 * 
	 */
	public static String getTravelCardStrings(TravelCard card, Context app_context)
	{
		//Get travel card's number
		String cardNumber = card.getApplicationInstanceId();

		//Get period 1 validity
		String period1 = getTravelCardPeriod1Validity(card, app_context);
		
		//get period 2 validity
		// - not implemented in this example -
		
		//Get travel card value
		String cardValue = getTravelCardValue(card);
		
		//Get travel card's value ticket info
		String valueTicket = getETicketValidity(card.getValueTicket(), app_context);
		
		//Get travel card's history string
		String history = getTravelCardHistory(card);
		
		//return combination of strings 
		String retStr =  "Travel card: " + cardNumber + "\n\n"
				+ period1 + "\n\n"
				+ cardValue + "\n\n"
				+ valueTicket + "\n\n"
				+ history;
		
		//return
		return   retStr;
	}

	/**
	 * Example of how to get single ticket'svalidity string from previously read HSL single ticket.
	 * <p>
	 * Look the commented source code to see the example about how to use the data from the TravelCard class to output the various information from HSL travel cards.
	 * <p>
	 * 
	 * @param card the travel card data as instance of com.hsl.cardproducts.TravelCard
	 * @param app_context the Android application context that is needed to get the string resources used by the library
	 * @return String representing travel card's value.
	 * 
	 */
	public static String getSingleTicketStrings(SingleTicket singleTicket, Context app_context)
	{
		//Get ticket's serial number
		String cardNumber = singleTicket.getApplicationInstanceId();
		
		//Get ticket's validity info
		String ticketInfo = getETicketValidity(singleTicket.getValueTicket(), app_context);
		
		//return combination of strings 
		return "Travel card: " + cardNumber + "\n\n" + ticketInfo;
	}

	/**
	 * Get a string representing validity of eTicket using the data from HSL travel card or HSL single ticket.
	 * <p>
	 * eTicket class is used in both the single ticket's data and travel card's value ticket's data. eTicket is available from {@code SingleTicket} and {@code TravelCard} classes using the method {@code getValueTicket()}.
	 * <br>
	 * Look the commented source code to see the example about how to use the data from the SingleTicket class to determine the various validity information of HSL single tickets.
	 * <p>
	 * 
	 * @param eTicket the eTicket data as instance of {@code com.hsl.cardproducts.eTicket}.  
	 * eTicket is available from {@code SingleTicket} and {@code TravelCard} classes using the method {@code getValuTicket()}. 
	 * @param app_context the Android application context that is needed to get the string resources used by the library
	 * @return String representing tickets validity information.
	 * 
	 */
	public static String getETicketValidity(eTicket eTicket, Context app_context) 
	{
		//Set date format to be used on output string
		SimpleDateFormat datetimeFormat = new SimpleDateFormat("d.M.yyyy HH:mm");

		//Instantiate helper class to get names for the ticket's zone or vehicle type where the ticket is valid
		ValidityAreaMappings mappings = new ValidityAreaMappings(app_context);
		//Get the tickets validity area name
		String validityArea = mappings.getValidityArea(eTicket.getValidityAreaType(), eTicket.getValidityArea());
		
		//Special handling of the case when ticket has no validity area set
		//We just assume it to mean the whole area (Region three-zone/Koko alue))
		if (validityArea == app_context.getResources().getString(R.string.z0))
		{
			validityArea = app_context.getResources().getString(R.string.z15);
		}
		
		//Ticket's validity start date
		Calendar periodStartCal = Calendar.getInstance(); 
		periodStartCal.setTime (eTicket.getValidityStartDate());
		//Ticket's validity end date
		Calendar periodEndCal = Calendar.getInstance(); 
		periodEndCal.setTime (eTicket.getValidityEndDate());

		//Current date from the device
		Calendar currentCal = Calendar.getInstance ();
 		
		//Ticket status string
		String status = "Ticket status: ";
		//String to tell more about validity of the ticket
		String validityStr = "\n";
			
		//NOTE! If a date is not set on ticket, it's extracted date value is 1.1.1997!
		//This is due to date format on tickets which stores the number of days since 1.1.1997
		
		//If no end date is set for the ticket (the date is 1.1.1997)
		if (periodEndCal.get(Calendar.YEAR) == 1997)
		{
			//Ticket is unused
			status += "Unused \n\n";
			//Tell user when the validity starts
			validityStr += "The ticket is valid from the first use.";
		}
		//If start date is set, but start date is in the future
		else if (periodStartCal.after(currentCal))
		{
			//Ticket is not yet valid
			status += "Not started yet \n\n";
			
			//tell start and end dates for the validity
			validityStr += "Valid:\n"
					+ datetimeFormat.format(periodStartCal.getTime()) + " - "
					+ datetimeFormat.format(periodEndCal.getTime());
		}
		//If ticket's validity end date was before current date
		else if (periodEndCal.before(currentCal))
		{
			//Ticket is used and no longer valid
			status += "No longer valid \n\n";

			//Tell the time when the validity of the ticket will end or has ended 
			validityStr += "Valid until: " + datetimeFormat.format(periodEndCal.getTime()); 
		}
		//no other options left, ticket is valid
		else 
		{
			//Valid ticket
			status += "Valid \n\n";

			//Tell the time when the validity of the ticket will end or has ended 
			validityStr += "Valid until: " + datetimeFormat.format(periodEndCal.getTime()); 
		}
		
		//Get validity length number from single ticket field ValidityLength 
		//(the meaning of this number is later checked from ValidityLengthType -field)
		int valueLen = eTicket.getValidityLength();

		String infoStr = "Zone: " + validityArea + "\n"
				+ "Group size: " + eTicket.getGroupSize() + "\n"
				+ "Validity time: " + String.valueOf (valueLen) + " ";

		//Add appropriate validity time unit based on tickets validityLengthType
		switch (eTicket.getValidityLengthType())
		{
		case (MINUTES): //minutes
			infoStr += "min";
		case (HOURS): //hours
			infoStr += "h";
		case (ALLDAY): //periods of 24h
			infoStr += "days";
		case (DAYS): //days
			infoStr += "days";
		}
		
		//Return the ticket status, information and validity strings
		String retString = status + infoStr + validityStr;
		return retString;
	}
	
	/**
	 * Get a string representing validity of HSL travel card's first period product.
	 * <p>
	 * Look the commented source code to see the example about how to use the data from the TravelCard class to output the various information from HSL travel cards.
	 * <p>
	 * 
	 * @param card the travel card data as instance of com.hsl.cardproducts.TravelCard
	 * @param app_context the Android application context that is needed to get the string resources used by the library
	 * @return String representing travel card's first period product's validity information.
	 * 
	 */
	public static String getTravelCardPeriod1Validity(TravelCard card, Context app_context)
	{
		//Period info strings
		String period1Info = null;
		String period1Status = null;

		//Set date format to be used on output string
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

		//Current calendar
		Calendar currentCal = Calendar.getInstance ();

		//Instantiate helper class to get names for the ticket's zone or vehicle type where the ticket is valid
		ValidityAreaMappings mappings = new ValidityAreaMappings(app_context);
		String validityArea = mappings.getValidityArea((int) card.getValidityAreaType1(), (int) card.getValidityArea1());
		
		//get Calendar instance of Period start date 
		Calendar periodStartCal1 = Calendar.getInstance(); 
		periodStartCal1.setTime (card.getPeriodStartDate1());
		
		//get Calendar instance of Period end date 
		Calendar periodEndCal1 = Calendar.getInstance(); 
		periodEndCal1.setTime (card.getPeriodEndDate1());
		
		//Check that we've got validity area and that period 1 exists (it's starting date is set)
		//If period 1 does not exist, it's data on the card is filled with zeroes and en1545 date with 0 value is converted to java Date 1.1.1997...
		if ( (validityArea != null) && (periodStartCal1.get(Calendar.YEAR) > 1997) )
		{
			//if period is valid for now (no end date set)
			if ( periodStartCal1.before(currentCal) && (periodEndCal1.get(Calendar.YEAR) == 1997) )
			{
				//Info 
				period1Info = "Zone: " + validityArea; 
				//Period status
				period1Status = "Valid for the present";
			}
			else
			{			
				//Set Info text
				period1Info = "Zone: " + validityArea + "\n" 
						+ dateFormat.format(periodStartCal1.getTime()); 
				
				//If period's end date is not set
				if (periodEndCal1.get(Calendar.YEAR) == 1997)
				{
					//Indication of no ending date
					period1Info += " -->";
				}
				//If eperido end date is set
				else
				{
					//Write ending date into info string
					period1Info += " - " + dateFormat.format(periodEndCal1.getTime()) ;
				}
				
				//Determine the status of the period 1
				//If period starting date is in the future 
				if (periodStartCal1.after(currentCal))
				{
					//Set status text
					period1Status = "Not started yet";
				}
				//if period is currently valid
				else if (currentCal.before(periodEndCal1) && currentCal.after(periodStartCal1))
				{
					//Set status text
					period1Status = "Currently valid";
				}
				//Othervise the period is not valid anymore
				else
				{
					//set status text
					period1Status = "No longer valid";
				}
			}
		}
	
		//Return the ticket number, ticket status, information and validity strings
		String retString = "Ticket number: " + card.getApplicationInstanceId() + "\n\n" ;
		retString += period1Status + "\n\n" + period1Info;
		
		//Return the ticket status, information and validity strings
		return retString;
	}

	/**
	 * Get a string representing the value stored in HSL travel card.
	 * <p>
	 * Look the commented source code to see the example about how to use the data from the TravelCard class to output the various information from HSL travel cards.
	 * <p>
	 * 
	 * @param card the travel card data as instance of com.hsl.cardproducts.TravelCard
	 * @return String representing travel card's value.
	 * 
	 */
	public static String getTravelCardValue(TravelCard card)
	{
		//Calculate euros and cents out of value counter
		int euros = card.getValueCounter() / 100;
		int cents = card.getValueCounter() % 100;
		
		//Build string, cents with 2 digits and leading zeros. Add euro-character at the end. 
		String value = String.format("%d.%02d \u20ac",euros,cents);

		return value;
	}

	/**
	 * Get a string representing the travel history fields stored in HSL travel card.
	 * <p>
	 * Look the commented source code to see the example about how to use the data from the TravelCard class to output the various information from HSL travel cards.
	 * <p>
	 * 
	 * @param card the travel card data as instance of com.hsl.cardproducts.TravelCard
	 * @return String representing travel card's history.
	 * 
	 */
	public static String getTravelCardHistory(TravelCard card)
	{
		//String to return
		String historyStr = "";
		//Calendar instance to get transaction times
		Calendar transactionTime = Calendar.getInstance(); 
		//Set date format to be used on output string
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		
		//get the history data from the card
		History[] hist = card.getHistory();

		//Show last 7 trips from history
		for (int i=card.getHistoryLen()-1; i >= 0; i--)
		{
			//get transaction date and time
			transactionTime.setTime (hist[i].getTransactionDateTime());

			//print date and time
			historyStr += dateFormat.format(transactionTime.getTime());
			
			//If this is season journey (0 = Season journey , 1 = Value ticket)
			if (hist[i].getTransactionType() == 0)
			{
				
				//Add transaction type "season" to string
				historyStr += " Season journey";
			}
			//This is value ticket journey
			else
			{
				//Add transaction type "value ticket" to string
				historyStr += " Value ticket\n"
						+ " - ";
				//If valu ticket is bouht for more than 1 person
				if (hist[i].getGroupSize() > 1)
				{
					//Add number of ticket to the string
					historyStr += "" + hist[i].getGroupSize() + " pcs, ";
				}
				
				//Add the price of the value ticket to the string, ending with euro-character
				historyStr += "Price "
						+ String.format("%d,%02d", (hist[i].getPrice()/100), (hist[i].getPrice()%100)) 
						+ "\u20ac"; //euro
			}

			//Add line break at the end if we've not reached the last history field 
			if (i > 0)
				historyStr += "\n";
		}
		
		//Return the history
		return historyStr;
	}


}
