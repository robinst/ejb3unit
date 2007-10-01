package com.bm.testsuite.dataloader;

import java.util.Date;

import junit.framework.TestCase;

public class DateFormatsTest extends TestCase {

	public void testTimeStamp() throws Exception {
		Date result = DateFormats.USER_DATE_TIME.setUserDefinedFomatter("yyyy.MM.dd HH:mm z").parse(
				"1971.08.24 16:15 EDT");
		assertEquals(51912900000L, result.getTime());
	}
	
	public void testDate() throws Exception {
		Date result = DateFormats.USER_DATE.setUserDefinedFomatter("yyyy.MM.dd z").parse(
				"1971.08.24 EDT");
		assertEquals(51854400000L, result.getTime());
	}
	
	public void testTime() throws Exception {
		Date result = DateFormats.USER_TIME.setUserDefinedFomatter("HH:mm z").parse(
				"16:15 EDT");
		assertEquals(72900000, result.getTime());
	}

}
