/*
 * eTicket.java
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

import java.util.Calendar;
import java.util.Date;

import com.hsl.util.Convert;

/**
 * The Class eTicket represents a single ticket data that is used both in HSL single tickets and in HSL travel card's value tickets.
 */
public class eTicket 
{

	/** The product code. **/
	private int		productCode;
	/** The child. **/
	private int		child;
	/** The language code. **/
	private int	 	languageCode;
	/** The validity length type. **/
	private int	 	validityLengthType;
	/** The validity length. **/
	private int	 	validityLength;
	/** The validity area type. **/
	private int	 	validityAreaType;
	/** The validity area. **/
	private int	 	validityArea;
	/** The sale date. **/
	private Date	saleDate;
	/** The sale time. **/
	private int		saleTime;
	/** The group size. **/
	private int	 	groupSize;
	/** The sale status. **/
	private int		saleStatus;
	// Joni 9.11.2018
	// Sale price i.e. ticketFare
	private int		ticketFare;
	/** The validity start date. **/
	private Date 	validityStartDate;
	/** The validity end date. **/
	private Date 	validityEndDate;
	/** The validity status. **/
	private int	 	validityStatus;
	//Last boarding info
	/** The boarding date. **/
	private Date	boardingDate;
	/** The boarding vehicle. **/
	private int		boardingVehicle;
	/** The boarding location num type. **/
	private int		boardingLocationNumType;
	/** The boarding location num. **/
	private int	 	boardingLocationNum;
	/** The boarding direction. **/
	private int		boardingDirection;
	/** The boarding area. **/
	private int		boardingArea;

	/** New values for TLJ2014 spec **/
	/** Is this trip extra zone trip **/
	private int     extraZone;
	/** Extra zone ticket period pass area **/
	private int     extPeriodPassValidityArea;
	/** Extra zone ticket product code **/
	private int     extProductCode;
	/** Extra zone 1st part validity area **/
	private int     ext1ValidityArea;
	/** Extra zone 1st part ticket fare **/
	private int     ext1Fare;
	/** Extra zone 2nd part validity area **/
	private int     ext2ValidityArea;
	/** Extra zone 2nd part ticket fare **/
	private int     ext2Fare;
	/** The product code for group ticket. **/
	private int		productCodeGroup;
	/** The validity length type for group ticket. **/
	private int		validityLengthTypeGroup;
	/** The validity length for group ticket. **/
	private int		validityLengthGroup;
	/* Sale price i.e. ticketFare for group ticket. **/
	private int		ticketFareGroup;
	/** The validity end date for group ticket. **/
	private Date	validityEndDateGroup;

	/**
	 * Instantiates a new eTicket from given data.
	 *
	 * @param eTicket the raw byte data read from single ticket or HSL travel card's eTicket file.
	 * @param containsSeals tells if the data is from single ticket (or from HSL travel card's value ticket).
	 * @param version which version value ticket data is from, 1 = old, TLJ2010 spec, 2 = new, TLJ2014 spec
	 * @param isSingleTicket is card type singleticket or travelcard
	 */
	public eTicket(byte[] eTicket, boolean containsSeals, int version, boolean isSingleTicket)
	{
		if (version == 1)
			initV1(eTicket, containsSeals);
		else {
			if (isSingleTicket)
				initV2SingleTicket(eTicket, containsSeals);
			else
				initV2(eTicket, containsSeals);
		}
	}

	/**
	 * Instantiates a new eTicket from given data for old card spec.
	 *
	 * @param eTicket the raw byte data read from single ticket or HSL travel card's eTicket file.
	 * @param containsSeals tells if the data is from single ticket (or from HSL travel card's value ticket).
	 */
	private void initV1(byte[] eTicket, boolean containsSeals) {
		int c = 0;

		productCode =  (short) ( ((eTicket[0] & 0xFF) << 6) | ((eTicket[1] & 0xFC) >>> 2) );
		child = (byte)((eTicket[1] & 0x02) >>> 1);
		languageCode = (byte)( ((eTicket[1] & 0x01) << 1) | ((eTicket[2] & 0x80) >>> 7) );
		validityLengthType = (byte)((eTicket[2] & 0x60) >>> 5 );
		validityLength = (byte)( ((eTicket[2] & 0x1F) << 3) | ((eTicket[3] & 0xE0) >>> 5) );
		validityAreaType = (byte)((eTicket[3] & 0x10) >>> 4);
		validityArea = (byte)(eTicket[3] & 0x0F);

		short date1 = (short)( ((eTicket[4] & 0xFF) << 6) | ((eTicket[5] & 0xFC) >>> 2) );
		saleDate = Convert.en5145Date2JavaDate(date1);
		saleTime = (byte)( ((eTicket[5] & 0x03) << 3) | ((eTicket[6] & 0xE0) >>> 5) );

		// Joni 9.11.2018
		// Sale price
		ticketFare 				= Convert.getShortValue	(eTicket, 68, 14);

		groupSize = (byte)( (eTicket[10] & 0x3E) >>> 1);
		//sale status is relevant only in value tickets on desfire cards
		saleStatus = (byte) (eTicket[10] & 0x01);

		//if were reading separate eTicket (single ticket) note the seals
		if (containsSeals)
			c = 6;

		//read validity start datestamp
		date1 = (short)( ((eTicket[11+c] & 0xFF) << 6) | ((eTicket[12+c] & 0xFC) >>> 2) );
		//read validity start timestamp
		short time1 = (short)( ((eTicket[12+c] & 0x03) << 9) | ((eTicket[13+c] & 0xFF) << 1) | ((eTicket[14+c] & 0x80) >>> 7) );
		validityStartDate = Convert.en5145DateAndTime2JavaDate(date1, time1);

		//read validity end datestamp
		date1 = (short)( ((eTicket[14+c] & 0x7F) << 7) | ((eTicket[15+c] & 0xFF) >>> 1) );
		//read validity end timestamp
		time1 = (short)( ((eTicket[15+c] & 0x01) << 10) | ((eTicket[16+c] & 0xFF) << 2) | ((eTicket[17+c] & 0xC0) >>> 6) );
		validityEndDate = Convert.en5145DateAndTime2JavaDate(date1, time1);

		//validity status is relevant only in value tickets on desfire cards
		validityStatus = (byte) (eTicket[17+c] & 0x01);

		//LAST USE (BOARDING)

		date1 = (short) (((eTicket[18+c] & 0xFF) << 6) | ((eTicket[19+c] & 0xFC) >>> 2) );
		time1 = (short)( ((eTicket[19+c] & 0x03) << 9) | ((eTicket[20+c] & 0xFF) << 1) | ((eTicket[21+c] & 0x80) >>> 7) );
		boardingDate = Convert.en5145DateAndTime2JavaDate(date1, time1);
		boardingVehicle = (short)( ((eTicket[21+c] & 0x7F) << 7) | (eTicket[22+c] & 0xFE) >>> 1);
		boardingLocationNumType = (byte)( ((eTicket[22+c] & 0x01) << 1) | ((eTicket[23+c] & 0x80) >>> 7) );
		boardingLocationNum = (short)( ((eTicket[23+c] & 0x7F) << 7) | ((eTicket[24+c] & 0xFE) >>> 1) );
		boardingDirection = (byte) (eTicket[24+c] & 0x01);
		boardingArea = (byte)((eTicket[25+c] & 0xF0) >>> 4);
	}

	/**
	 * Instantiates a new eTicket from given data for new card spec.
	 *
	 * @param eTicket the raw byte data read from single ticket or HSL travel card's eTicket file.
	 * @param containsSeals tells if the data is from single ticket (or from HSL travel card's value ticket).
	 */
	private void initV2(byte[] eTicket, boolean containsSeals) {
		productCode 			= Convert.getShortValue	(eTicket, 1, 14);
		productCodeGroup		= Convert.getShortValue (eTicket, 15, 14);
		languageCode 			= Convert.getByteValue	(eTicket, 39, 2);
		validityLengthType		= Convert.getByteValue	(eTicket, 41, 2);
		validityLength			= Convert.getShortValue	(eTicket, 43, 8);
		validityLengthTypeGroup = Convert.getByteValue  (eTicket, 51, 2);
		validityLengthGroup		= Convert.getShortValue (eTicket, 53, 8);
		validityAreaType 		= Convert.getByteValue	(eTicket, 61, 2);
		validityArea			= Convert.getByteValue	(eTicket, 63, 6);
		int date1				= Convert.getShortValue	(eTicket, 69, 14);
		saleDate				= Convert.en5145Date2JavaDate(date1);
		saleTime				= Convert.getByteValue	(eTicket, 83, 5);
		ticketFare 				= Convert.getShortValue	(eTicket, 105, 14);
		ticketFareGroup			= Convert.getShortValue (eTicket, 119, 14);
		groupSize				= Convert.getByteValue	(eTicket, 133, 6);

		// Extension ticket information
		extraZone               = Convert.getByteValue (eTicket, 139, 1);
		extPeriodPassValidityArea = Convert.getByteValue(eTicket, 140, 6);
		extProductCode          = Convert.getIntValue  (eTicket, 146, 14);
		ext1ValidityArea        = Convert.getByteValue  (eTicket, 160, 6);
		ext1Fare                = Convert.getIntValue   (eTicket, 166, 14);
		ext2ValidityArea        = Convert.getByteValue  (eTicket, 180, 6);
		ext2Fare                = Convert.getIntValue   (eTicket, 186, 14);

		saleStatus				= Convert.getByteValue	(eTicket, 200, 1);

		date1					= Convert.getShortValue	(eTicket, 205, 14);
		int time1				= Convert.getShortValue	(eTicket, 219, 11);
		validityStartDate		= Convert.en5145DateAndTime2JavaDate(date1, time1);
		date1					= Convert.getShortValue	(eTicket, 230, 14);
		time1					= Convert.getShortValue	(eTicket, 244, 11);
		validityEndDate			= Convert.en5145DateAndTime2JavaDate(date1, time1);
		date1					= Convert.getShortValue (eTicket, 255, 14);
		time1					= Convert.getShortValue (eTicket, 269, 11);
		// validityEndDateGroup should be null if date and time are zeros
		validityEndDateGroup	= date1 > 0 && time1 > 0 ? Convert.en5145DateAndTime2JavaDate(date1, time1) : null;
		// RFU 5bits
		validityStatus			= Convert.getByteValue	(eTicket, 285, 1);

		//Last boarding info
		date1					= Convert.getShortValue	(eTicket, 286, 14);
		time1					= Convert.getShortValue	(eTicket, 300, 11);
		boardingDate			= Convert.en5145DateAndTime2JavaDate(date1, time1);
		boardingVehicle			= Convert.getShortValue	(eTicket, 311, 14);
		boardingLocationNumType	= Convert.getByteValue	(eTicket, 325, 2);
		boardingLocationNum		= Convert.getShortValue	(eTicket, 327, 14);
		boardingDirection		= Convert.getByteValue	(eTicket, 341, 1);
		boardingArea			= Convert.getByteValue	(eTicket, 344, 6);
	}

	/**
	 * Instantiates a new SingleTicket eTicket from given data for new card spec.
	 *
	 * @param eTicket the raw byte data read from single ticket or HSL travel card's eTicket file.
	 * @param containsSeals tells if the data is from single ticket (or from HSL travel card's value ticket).
	 */
	private void initV2SingleTicket(byte[] eTicket, boolean containsSeals) {
		productCode 			= Convert.getShortValue	(eTicket, 0, 10);
		child                   = Convert.getByteValue  (eTicket, 10, 1);
		languageCode 			= Convert.getByteValue	(eTicket, 11, 2);
		validityLengthType		= Convert.getByteValue	(eTicket, 13, 2);
		validityLength			= Convert.getShortValue	(eTicket, 15, 8);
		validityAreaType 		= Convert.getByteValue	(eTicket, 23, 2);
		validityArea			= Convert.getByteValue	(eTicket, 25, 6);
		int date1				= Convert.getShortValue	(eTicket, 31, 14);
		saleDate				= Convert.en5145Date2JavaDate(date1);
		saleTime				= Convert.getByteValue	(eTicket, 45, 5);
		ticketFare 				= Convert.getShortValue	(eTicket, 67, 15);
		groupSize				= Convert.getByteValue	(eTicket, 82, 6);

		// SEAL1 6 bytes, 48 bits

		date1					= Convert.getShortValue	(eTicket, 136, 14);
		int time1				= Convert.getShortValue	(eTicket, 150, 11);
		validityStartDate		= Convert.en5145DateAndTime2JavaDate(date1, time1);
		date1					= Convert.getShortValue	(eTicket, 161, 14);
		time1					= Convert.getShortValue	(eTicket, 175, 11);
		validityEndDate			= Convert.en5145DateAndTime2JavaDate(date1, time1);

		// RFU 1.75 bytes, 14 bits
		// SEAL2 8 bytes, 64 bits

		//Last boarding info
		date1					= Convert.getShortValue	(eTicket, 264, 14);
		time1					= Convert.getShortValue	(eTicket, 278, 11);
		boardingDate			= Convert.en5145DateAndTime2JavaDate(date1, time1);
		boardingVehicle			= Convert.getShortValue	(eTicket, 289, 14);
		boardingLocationNumType	= Convert.getByteValue	(eTicket, 303, 2);
		boardingDirection		= Convert.getByteValue	(eTicket, 319, 1);
	}

	/**
	 * If ticket is for children
	 *
	 * @return true if the ticket is for children
	 */
	public int	 getChild() {
		return child;
	}

	/**
	 * Gets the language code of the ticket.
	 *
	 * @return the language code
	 */
	public int	 getLanguageCode() {
		return languageCode;
	}

	/**
	 * Gets the type of the ticket's validity area.
	 *
	 * @return the validity area type
	 */
	public int	 getValidityAreaType() {
		return validityAreaType;
	}

	/**
	 * Gets the validity area of the ticket.
	 *
	 * @return the validity area
	 */
	public int	 getValidityArea() {
		return validityArea;
	}

	/**
	 * Gets the ticket sale date.
	 *
	 * @return the sale date
	 */
	public Date  getSaleDate() {
		return saleDate;
	}

	/**
	 * Gets the ticket sale time.
	 *
	 * @return the sale time
	 */
	public int	 getSaleTime() {
		return saleTime;
	}

	/**
	 * Gets the group size.
	 *
	 * @return the group size
	 */
	public int	 getGroupSize() {
		return groupSize;
	}

	/**
	 * Gets the sale status.
	 *
	 * @return the sale status
	 */
	public int	 getSaleStatus() {
		return saleStatus;
	}

	/**
	 * Gets the validity start date.
	 *
	 * @return the validity start date
	 */
	public Date  getValidityStartDate() {
		return validityStartDate;
	}

	/**
	 * Gets the validity status.
	 *
	 * @return the validity status
	 */
	public int	 getValidityStatus() {
		return validityStatus;
	}

	/**
	 * Gets the boarding area.
	 *
	 * @return the boarding area
	 */
	public int	 getBoardingArea() {
		return boardingArea;
	}

	/**
	 * Gets the boarding date.
	 *
	 * @return the boarding date
	 */
	public Date  getBoardingDate() {
		return boardingDate;
	}

	/**
	 * Gets the boarding vehicle.
	 *
	 * @return the boarding vehicle
	 */
	public int	 getBoardingVehicle() {
		return boardingVehicle;
	}

	/**
	 * Gets the boarding location number type.
	 *
	 * @return the boarding location number type
	 */
	public int	 getBoardingLocationNumType() {
		return boardingLocationNumType;
	}

	/**
	 * Gets the boarding location number.
	 *
	 * @return the boarding location number
	 */
	public int	 getBoardingLocationNum() {
		return boardingLocationNum;
	}

	/**
	 * Gets the boarding direction.
	 *
	 * @return the boarding direction
	 */
	public int	 getBoardingDirection() {
		return boardingDirection;
	}

	// 15.4.2019
	// Extension ticket information

	/**
	 * Gets the Extra Zone.
	 * @return extra zone ticket, 1 = true, 0 = false.
	 */
	public int   getExtraZone() { return extraZone; }

	/**
	 * Gets extra zone ticket's pass validity area.
	 * @return validity area code.
	 */
	public int   getExtPeriodPassValidityArea() { return extPeriodPassValidityArea; }

	/**
	 * Gets extra zone ticket's product code.
	 * @return Product code.
	 */
	public int   getExtProductCode() { return extProductCode; }

	/**
	 * Gets extra zone ticket's 1st part validity area.
	 * @return Validity area code.
	 */
	public int   getExt1ValidityArea() { return ext1ValidityArea; }

	/**
	 * Gets extra zone ticket's 1st fare.
	 * @return Ticket fare.
	 */
	public int   getExt1Fare() { return ext1Fare; }

	/**
	 * Gets extra zone ticket's 2nd part validity area.
	 * @return Validity area code.
	 */
	public int   getExt2ValidityArea() { return ext2ValidityArea; }

	/**
	 * Gets extra zone ticket's 2nd fare.
	 * @return Ticket fare.
	 */
	public int   getExt2Fare() { return ext2Fare; }

	// 24.4.2019
	// Modified getters to adjust to extension ticket changes

	/**
	 * Gets the product code.
	 *
	 * @return the product code value of the single ticket
	 */
	public int	 getProductCode() {
		if (productCodeGroup > 0) return productCodeGroup;
		else return productCode;
	}

	/**
	 * Gets the type of ticket's validity length.
	 *
	 * @return the validity length type
	 */
	public int	 getValidityLengthType() {
		if (validityLengthTypeGroup > 0) return validityLengthTypeGroup;
		else return validityLengthType;
	}

	/**
	 * Gets the length of ticket's validity.
	 *
	 * @return the validity length
	 */
	public int	 getValidityLength() {
		if (validityLengthGroup > 0) return validityLengthGroup;
		else return validityLength;
	}

	/**
	 * Gets the ticket fare.
	 *
	 * @return the ticket fare
	 */
	public int	 getTicketFare() {
		if (ticketFare > 0) return ticketFare;
		else if (ticketFareGroup > 0) return ticketFareGroup;
		else return 0;
	}

	/**
	 * Gets the validity end date.
	 *
	 * @return the validity end date
	 */
	public Date  getValidityEndDate() {
		if (validityEndDateGroup != null) return validityEndDateGroup;
		else return validityEndDate;
	}

	// 24.4.2019
	/** Right price, i.e. fare added and total price **/
	public int	 getRightFare() {
		int fare = ticketFare;
		int size = groupSize;
		if ((extraZone == 1 || ticketFare > 0) && size > 1) size--;
		fare += ticketFareGroup * size;
		return fare;
	}

	/** Total price with extra zone ticket **/
	public int	getTotalFare() {
		int fare = getRightFare();
		if (extraZone == 1)
			fare += ext1Fare + ext2Fare;
		return fare;
	}
}