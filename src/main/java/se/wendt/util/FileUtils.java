package se.wendt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {

	public static void verifyExistingReadableDirectory(String directoryPath) {
		if (directoryPath == null) {
			throw new IllegalArgumentException("Can't write to directory null");
		}

		File file = new File(directoryPath);
		if (!file.exists()) {
			throw new IllegalArgumentException(directoryPath + " is not an existing directory");
		}
		if (!file.isDirectory()) {
			throw new IllegalArgumentException(directoryPath + " exists, but is not a directory");
		}
		if (!file.canRead()) {
			throw new IllegalArgumentException(directoryPath + " is not readable");
		}
	}

	/**
	 * Returns true if the directory pointed out is writeable.
	 * <p>
	 * This method will actually try to write to a file in the directory to make
	 * sure it works.
	 * 
	 * @param directoryPath path to the directory to test for writeability
	 */
	public static void verifyExistingWriteableDirectory(String directoryPath) {
		if (directoryPath == null) {
			throw new IllegalArgumentException("Can't write to directory null");
		}

		File file = new File(directoryPath);
		// create
		if (!file.exists()) {
			try {
				file.mkdirs();
			} catch (Exception e) {
				throw new IllegalArgumentException(directoryPath + " didn't exist, and we failed to create it: "
						+ e.getMessage(), e);
			}
		}
		if (!file.isDirectory()) {
			throw new IllegalArgumentException(directoryPath + " is not a directory");
		}
		if (!file.canWrite()) {
			throw new IllegalArgumentException(directoryPath + " is read only (can't write anyway)");
		}
		createTestFileInDirectory(directoryPath);
	}

	private static void createTestFileInDirectory(String permanentStoragePath) {
		File dir = new File(permanentStoragePath);
		File file;
		try {
			file = File.createTempFile("test", "test", dir);
		} catch (IOException e1) {
			throw new IllegalArgumentException("Failed to create test file in " + permanentStoragePath + ": "
					+ e1.getMessage(), e1);
		}
		file.deleteOnExit();
		try {
			FileWriter fw = new FileWriter(file);
			fw.write("test");
			fw.close();
			file.delete();
		} catch (IOException e) {
			throw new IllegalArgumentException("Failed to write to test file in " + permanentStoragePath + ": "
					+ e.getMessage(), e);
		}
	}

	/**
	 * Deletes the file, if it exists and is a file (not directory)
	 * 
	 * @param file the file to delete
	 */
	public static void deleteFileIfExists(File file) {
		if (file != null && file.exists() && file.isFile()) {
			file.delete();
		}
	}

	/**
	 * Copies file content of file source to file target, returning the number
	 * of bytes copied.
	 * 
	 * @param source source file
	 * @param target target file
	 * @return the number of bytes copied.
	 * @throws IOException
	 */
	public static long copyFile(File source, File target) throws IOException {
		FileOutputStream targetOut = new FileOutputStream(target);
		FileInputStream sourceIn = new FileInputStream(source);
		byte[] buffer = new byte[4096];
		long fileSize = 0;
		while (true) {
			int bytesRead = sourceIn.read(buffer);
			if (bytesRead == -1) {
				break;
			} else {
				targetOut.write(buffer, 0, bytesRead);
				fileSize = fileSize + (long) bytesRead;
			}
		}
		targetOut.flush();
		targetOut.close();
		sourceIn.close();
		return fileSize;
	}

}