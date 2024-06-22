package com.bok.iso.util.test;

import net.moznion.random.string.RandomStringGenerator;

public class PatternWorkMain {

	public static void main(String[] args) {

//		String pattern = "[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[89ab][a-f0-9]{3}-[a-f0-9]{12}";
//		String pattern = "[A-Za-z0-9]{8}";
//		String pattern = ".*(\\+|-)((0[0-9])|(1[0-3])):[0-5][0-9]";
		String pattern = "[0-9a-zA-Z/\\-\\?:\\(\\)\\.,'\\+ ]{1,35}";
//		String pattern = "[0-9a-zA-Z]{1,35}";
		System.out.println(pattern);
		if ( pattern.contains("{1,") ) {
			pattern = "[0-9a-zA-Z]{" + pattern.substring(pattern.indexOf("{1,") + 3);
		}
		System.out.println(pattern);
		// [0-9a-zA-Z/\-\?:\(\)\.,'\+ ]{1,35}
		RandomStringGenerator generator = new RandomStringGenerator();
		// generates random string (e.g. "a5B123 18X")
		String randomString = generator.generateByRegex(pattern);
		System.out.println(randomString);

	}

}
