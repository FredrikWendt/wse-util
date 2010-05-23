package se.wendt.util;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationUtils {

	private static Logger logger = LoggerFactory.getLogger(ConfigurationUtils.class);

	public static String loadDirectoryProperty(Properties configuration, String propertyname) {
		String path = configuration.getProperty(propertyname);
		if (path == null) {
			throw new IllegalStateException("Property " + propertyname + " is not set, it must be");
		}
		path = path.trim();
		if (path.length() == 0) {
			throw new IllegalStateException("Property " + propertyname + " is empty - it should point out a directory");
		}
		File dir = new File(path);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new IllegalStateException("Directory " + path + " (setting " + propertyname
						+ ") didn't exist, and I can't create it.");
			}
		}
		if (!dir.isDirectory()) {
			throw new IllegalStateException("Directory " + path + " (setting " + propertyname
					+ ") exists, but it's not a directory - it must be");
		}
		if (!dir.canWrite()) {
			throw new IllegalStateException("Directory " + path + " (setting " + propertyname
					+ ") exists, but is read only.");
		}
		return path;
	}
	
	/**
	 * Locates the specified resource and loads it into the properties set given.
	 * <p>
	 * @see Properties#load(InputStream)
	 * @see #locateConfiguration(String)
	 * 
	 * @param configuration the properties set to load properties into
	 * @param resourceName the name of the resource to locate
	 */
	public static void loadConfiguration(Properties configuration, String resourceName) {
		URL location = locateConfiguration(resourceName);
		try {
			InputStream inputStream = location.openStream();
			configuration.load(inputStream);
			inputStream.close();
		} catch (Exception e) {
			throw new WseException("Failure while reading configuration: " + e.getMessage(), e);
		}
	}

	/**
	 * Tries to find the named resource.
	 * <p>
	 * Strategies:
	 * <ul>
	 * <li>see if there's a <code>-D<em>resourceName</em>=/path/here</code></li>
	 * <li>try using class loaders' {@link ClassLoader#getResource(String)} and
	 * {@link ClassLoader#getSystemResource(String)}</li>
	 * <li>see if there's a file in user.dir, user.home, java.home</li>
	 * 
	 * @param resourceName
	 * @return
	 */
	public static URL locateConfiguration(String resourceName) {
		// check -D first
		String configSetting = System.getProperty(resourceName);
		if (configSetting != null) {
			File file = new File(configSetting);
			if (file.exists()) {
				logger.info("Found configuration using -D" + resourceName);
				try {
					return file.toURI().toURL();
				} catch (MalformedURLException e) {
					throw new WseException("Failed to convert local file path " + file.getPath() + " to URL: " + e.getMessage(), e);
				}
			} else {
				throw new IllegalStateException("-D" + resourceName + "=" + configSetting + " is not a valid file");
			}
		}

		URL resource = null;
		if (resource == null) {
			resource = tryClass("class.getResource", ConfigurationUtils.class.getResource(resourceName));
		}
		if (resource == null) {
			ClassLoader classLoader = ConfigurationUtils.class.getClassLoader();
			resource = tryClass("class.getClassLoader.getResource", classLoader.getResource(resourceName));
		}
		if (resource == null) {
			resource = tryClass("ClassLoader.getSystemResource", ClassLoader.getSystemResource(resourceName));
		}
		if (resource == null) {
			resource = tryFile("user.dir", resourceName); // check in user.dir (current dir)
		}
		if (resource == null) {
			resource = tryFile("user.home", resourceName); // check in user.home
		}
		if (resource == null) {
			resource = tryFile("java.home", resourceName); // check in java.home
		}
		if (resource == null) {
			throw new WseException("Can't locate " + resourceName
					+ " - must be on the classpath, in java.home, user.home or pointed out using -D" + resourceName
					+ "=/path/");
		}
		return resource;
	}

	private static URL tryClass(String msg, URL resource) {
		if (resource != null) {
			logger.info("Found configuration using " + msg);
		}
		return resource;
	}

	private static URL tryFile(String property, String resourceName) {
		File file = new File(System.getProperty(property), resourceName);
		if (file.exists() && file.isFile() && file.canRead()) {
			logger.info("Found configuration using system property " + property + ": " + System.getProperty(property));
			try {
				return file.toURI().toURL();
			} catch (MalformedURLException e) {
				throw new WseException("Failed to convert local file path " + file.getPath() + " to URL: " + e.getMessage(), e);
			}
		}
		return null;
	}

}