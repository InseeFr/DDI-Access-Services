package fr.insee.rmes.utils;

import java.util.Arrays;

public class StringUtils {
	
	  private StringUtils() {
		    throw new IllegalStateException("Utility class");
	}

	public static boolean stringContainsItemFromList(String string, String[] list) {
		return Arrays.stream(list).anyMatch(string::contains);
	}


}
