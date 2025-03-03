package fr.insee.rmes.utils;

import java.text.Normalizer;

public class FilesUtils {


    public static String reduceFileNameSize(String fileName, int maxLength) {
		return fileName.substring(0, Math.min(fileName.length(), maxLength));
	}

	private FilesUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static String removeAsciiCharacters(String fileName) {
		return Normalizer.normalize(fileName, Normalizer.Form.NFD)
				.replaceAll("œ", "oe")
				.replaceAll("Œ", "OE")
				.replaceAll("\\p{M}+", "")
				.replaceAll("\\p{Punct}", "");
	}

}
