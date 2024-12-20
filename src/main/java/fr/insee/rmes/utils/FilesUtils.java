package fr.insee.rmes.utils;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;

public class FilesUtils {


    public static String reduceFileNameSize(String fileName, int maxLength) {
		return fileName.substring(0, Math.min(fileName.length(), maxLength));
	}

	//todo : besoin
	public static File streamToFile(InputStream in, String fileName, String fileExtension) throws IOException {
		final File tempFile = File.createTempFile(fileName, fileExtension);
		tempFile.deleteOnExit();
		try (FileOutputStream out = new FileOutputStream(tempFile)) {
			IOUtils.copy(in, out);
		}
		return tempFile;
	}

	public static void addFileToZipFolder(Path fileToAdd, Path zipArchive) throws IOException {
		try(FileSystem fs = FileSystems.newFileSystem(zipArchive)){
			Files.write(fs.getPath(fileToAdd.getFileName().toString()), Files.readAllBytes(fileToAdd));
		}
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
