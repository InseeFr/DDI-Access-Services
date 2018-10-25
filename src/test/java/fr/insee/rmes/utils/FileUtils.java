package fr.insee.rmes.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.rmes.metadata.fragmentInstance.FragmentInstanceServiceTest;

public class FileUtils {

	private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

	public static InputStream fileToIS(File file) {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return is;
	}

	public static String getResourceAsString(String resourcePath) throws IOException {
		InputStream is = FragmentInstanceServiceTest.class.getResourceAsStream(resourcePath);
		String result = IOUtils.toString(is, StandardCharsets.UTF_8);
		is.close();
		return result;
	}

}
