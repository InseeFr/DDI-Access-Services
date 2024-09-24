package fr.insee.rmes.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FilesUtils {

	private static final Logger log = LoggerFactory.getLogger(FilesUtils.class);


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

	public static String cleanFileNameAndAddExtension(String fileName, String extension) {
		fileName = fileName.toLowerCase().trim();
		fileName = StringUtils.normalizeSpace(fileName);
		fileName = fileName.replace(" ", "-");
		fileName = Normalizer.normalize(fileName, Normalizer.Form.NFD).replace("[^\\p{ASCII}]", "");
		if (extension.startsWith(".")) {
			fileName += extension;
		} else {
			fileName += "." + extension;
		}
		return fileName;
	}

	public static void addFileToZipFolder(Path fileToAdd, Path zipArchive) throws IOException {
		try(FileSystem fs = FileSystems.newFileSystem(zipArchive)){
			Files.write(fs.getPath(fileToAdd.getFileName().toString()), Files.readAllBytes(fileToAdd));
		}
	}

	private FilesUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static void zipDirectory(File directoryToZip) throws IOException {
		FileOutputStream fos = new FileOutputStream(directoryToZip + "/" + directoryToZip.getName() + ".zip");
		ZipOutputStream zipOut = new ZipOutputStream(fos);

		zipFile(directoryToZip, directoryToZip.getName(), zipOut);
		
		try {
			zipOut.close();
			fos.close();
		} catch(IOException e ) {
			log.warn("outputStream already closed");
		}
		
	}
	public static String removeAsciiCharacters(String fileName) {
		return Normalizer.normalize(fileName, Normalizer.Form.NFD)
				.replaceAll("œ", "oe")
				.replaceAll("Œ", "OE")
				.replaceAll("\\p{M}+", "")
				.replaceAll("\\p{Punct}", "");
	}

	private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
		if (fileToZip.isHidden() || fileName.endsWith(".zip")) {
			return;
		}
		if (fileToZip.isDirectory()) {
			if (fileName.endsWith("/")) {
				zipOut.putNextEntry(new ZipEntry(fileName));
				zipOut.closeEntry();
			} else {
				zipOut.putNextEntry(new ZipEntry(fileName + "/"));
				zipOut.closeEntry();
			}
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
			}

			return;
		}
		try (FileInputStream fis = new FileInputStream(fileToZip)) {
			ZipEntry zipEntry = new ZipEntry(fileName);
			zipOut.putNextEntry(zipEntry);
			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zipOut.write(bytes, 0, length);
			}
		}
	}

}
