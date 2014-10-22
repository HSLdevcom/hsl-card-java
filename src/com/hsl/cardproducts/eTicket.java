/*
 * eTicket.java
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
 * The Class eTicket represents a single ticket data that is used both in HSL single tickets and in HSL travel card's value tickets.
 */
public class eTicket 
{
	
	/** The product code. */
	private short	productCode;
	
	/** The child. */
	private byte	child;
	
	/** The language code. */
	private byte 	languageCode;
	
	/** The validity length type. */
	private byte 	validityLengthType;
	
	/** The validity length. */
	private byte 	validityLength;
	
	/** The validity area type. */
	private byte 	validityAreaType;
	
	/** The validity area. */
	private byte 	validityArea;
	
	/** The sale date. */
	private Date	saleDate;
	
	/** The sale time. */
	private byte	saleTime;
	
	/** The group size. */
	private byte 	groupSize;
	
	/** The sale status. */
	private byte	saleStatus;

	/** The validity start date. */
	private Date validityStartDate;
	
	/** The validity end date. */
	private Date validityEndDate;
	
	/** The validity status. */
	private byte validityStatus;

	//Last boarding info
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

	/**
	 * Instantiates a new eTicket from given data.
	 *
	 * @param eTicket the raw byte data read from single ticket or HSL travel card's eTicket file.
	 * @param isSingleTicket tells if the data is from single ticket (or from HSL travel card's value ticket). 
	 */
	public eTicket(byte[] eTicketData, boolean isSingleTicket)
	{
		int c = 0;
		
		productCode =  (short) ( ((eTicketData[0] & 0xFF) << 6) | ((eTicketData[1] & 0xFC) >>> 2) );
		child = (byte)((eTicketData[1] & 0x02) >>> 1);
		languageCode = (byte)( ((eTicketData[1] & 0x01) << 1) | ((eTicketData[2] & 0x80) >>> 7) );
		validityLengthType = (byte)((eTicketData[2] & 0x60) >>> 5 );
		validityLength = (byte)( ((eTicketData[2] & 0x1F) << 3) | ((eTicketData[3] & 0xE0) >>> 5) );
		validityAreaType = (byte)((eTicketData[3] & 0x10) >>> 4);
		validityArea = (byte)(eTicketData[3] & 0x0F);
		
		short date1 = (short)( ((eTicketData[4] & 0xFF) << 6) | ((eTicketData[5] & 0xFC) >>> 2) );  
		saleDate = Convert.en5145Date2JavaDate(date1);
		saleTime = (byte)( ((eTicketData[5] & 0x03) << 3) | ((eTicketData[6] & 0xE0) >>> 5) );
		groupSize = (byte)( (eTicketData[10] & 0x3E) >>> 1);
		//sale status is relevant only in value tickets on desfire cards
		saleStatus = (byte) (eTicketData[10] & 0x01);

		//if we're reading separate eTicket (single ticket) skip over some data
		if (isSingleTicket)
			c = 6;
		
		//read validity start datestamp
		date1 = (short)( ((eTicketData[11+c] & 0xFF) << 6) | ((eTicketData[12+c] & 0xFC) >>> 2) );
		//read validity start timestamp
		short time1 = (short)( ((eTicketData[12+c] & 0x03) << 9) | ((eTicketData[13+c] & 0xFF) << 1) | ((eTicketData[14+c] & 0x80) >>> 7) );
		validityStartDate = Convert.en5145DateAndTime2JavaDate(date1, time1);
		
		//read validity end datestamp
		date1 = (short)( ((eTicketData[14+c] & 0x7F) << 7) | ((eTicketData[15+c] & 0xFF) >>> 1) );
		//read validity end timestamp
		time1 = (short)( ((eTicketData[15+c] & 0x01) << 10) | ((eTicketData[16+c] & 0xFF) << 2) | ((eTicketData[17+c] & 0xC0) >>> 6) );
		validityEndDate = Convert.en5145DateAndTime2JavaDate(date1, time1);

		//validity status is relevant only in value tickets on desfire cards
		validityStatus = (byte) (eTicketData[17+c] & 0x01);

		//LAST USE (BOARDING)

		date1 = (short) (((eTicketData[18+c] & 0xFF) << 6) | ((eTicketData[19+c] & 0xFC) >>> 2) );
		time1 = (short)( ((eTicketData[19+c] & 0x03) << 9) | ((eTicketData[20+c] & 0xFF) << 1) | ((eTicketData[21+c] & 0x80) >>> 7) );
		boardingDate = Convert.en5145DateAndTime2JavaDate(date1, time1);
		boardingVehicle = (short)( ((eTicketData[21+c] & 0x7F) << 7) | (eTicketData[22+c] & 0xFE) >>> 1);
		boardingLocationNumType = (byte)( ((eTicketData[22+c] & 0x01) << 1) | ((eTicketData[23+c] & 0x80) >>> 7) );
		boardingLocationNum = (short)( ((eTicketData[23+c] & 0x7F) << 7) | ((eTicketData[24+c] & 0xFE) >>> 1) );
		boardingDirection = (byte) (eTicketData[24+c] & 0x01);
		boardingArea = (byte)((eTicketData[25+c] & 0xF0) >>> 4);

	}

	/**
	 * Gets the product code.
	 *
	 * @return the product code value of the single ticket
	 */
	public short getProductCode() {
		return productCode;
	}
	
	/**
	 * If ticket is for children
	 *
	 * @return true if the ticket is for children
	 */
	public boolean getChild() {
		return  (child > 0);
	}
	
	/**
	 * Gets the language code of the ticket.
	 *
	 * @return the language code
	 */
	public byte getLanguageCode() {
		return languageCode;
	}
	
	/**
	 * Gets the type of ticket's validity length.
	 *
	 * @return the validity length type
	 */
	public byte getValidityLengthType() {
		return validityLengthType;
	}
	
	/**
	 * Gets the length of ticket's validity.
	 *
	 * @return the validity length
	 */
	public byte getValidityLength() {
		return validityLength;
	}
	
	/**
	 * Gets the type of the ticket's validity area.
	 *
	 * @return the validity area type
	 */
	public byte getValidityAreaType() {
		return validityAreaType;
	}
	
	/**
	 * Gets the validity area of the ticket.
	 *
	 * @return the validity area
	 */
	public byte getValidityArea() {
		return validityArea;
	}
	
	/**
	 * Gets the ticket sale date.
	 *
	 * @return the sale date
	 */
	public Date getSaleDate() {
		return saleDate;
	}
	
	/**
	 * Gets the ticket sale time.
	 *
	 * @return the sale time
	 */
	public byte getSaleTime() {
		return saleTime;
	}
	
	/**
	 * Gets the group size.
	 *
	 * @return the group size
	 */
	public byte getGroupSize() {
		return groupSize;
	}
	
	/**
	 * Gets the sale status.
	 *
	 * @return the sale status
	 */
	public byte getSaleStatus() {
		return saleStatus;
	}
	
	/**
	 * Gets the validity start date.
	 *
	 * @return the validity start date
	 */
	public Date getValidityStartDate() {
		return validityStartDate;
	}
	
	/**
	 * Gets the validity end date.
	 *
	 * @return the validity end date
	 */
	public Date getValidityEndDate() {
		return validityEndDate;
	}
	
	/**
	 * Gets the validity status.
	 *
	 * @return the validity status
	 */
	public byte getValidityStatus() {
		return validityStatus;
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
	 * Gets the boarding location number type.
	 *
	 * @return the boarding location number type
	 */
	public byte getBoardingLocationNumType() {
		return boardingLocationNumType;
	}
	
	/**
	 * Gets the boarding location number.
	 *
	 * @return the boarding location number
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

}
