package com.qtplaf.overlap;

public class TestFlags {

	public static void main(String[] args) {
		int flags = 0;
		int count = 30;
//		boolean b = true;
//		for (int flag = 0; flag <= count; flag++) {
//			flags = set(flags, flag, b);
//			System.out.println(flag+": "+flags+" "+get(flags,flag));
//		}
//		b = false;
//		for (int flag = 0; flag <= count; flag++) {
//			flags = set(flags, flag, b);
//			System.out.println(flag+": "+flags+" "+get(flags,flag));
//		}
		int flag = 0;
		flags = set(flags, flag, true);
		System.out.println(flag+": "+flags+" "+get(flags,flag));
		flags = set(flags, flag, false);
		System.out.println(flag+": "+flags+" "+get(flags,flag));
	}

	private static int set(int flags, int flag, boolean b) {
        if(b) {
            flags |= (1 << flag);
        } else {
            flags &= ~(1 << flag);
        }
		return flags;
	}
    
	private static boolean get(int flags, int flag) {
        int mask = (1 << flag);
        return ((flags & mask) == mask);
    }
}
