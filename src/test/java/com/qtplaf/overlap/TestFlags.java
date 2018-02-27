package com.qtplaf.overlap;

public class TestFlags {

	public static void main(String[] args) {
		long flags = 0;
		int count = 1000;
		for (int flag = 0; flag <= count; flag++) {
			boolean b = true;
			flags = Flags.set(flags, flag, b);
	        System.out.println(flag+": "+flags);
		}
		for (int flag = 0; flag <= count; flag++) {
			System.out.println(flag+": "+Flags.get(flags, flag));
		}
	}

}
