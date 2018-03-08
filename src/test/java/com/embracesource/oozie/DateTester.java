package com.embracesource.oozie;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTester {
	
	public static void main(String[] args) {
		
		Date date = new Date("Sat, 12 Aug 1995 13:30:00 GMT+0430");
		
		System.out.println(date);
		
		date = new Date();
		
		System.out.println(date);
		
		System.out.println(new Date(1514737200000L));
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:00+0800");
		
		System.out.println(sdf.format(date));
	}
}
