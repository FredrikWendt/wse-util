package se.wendt.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads configuration, logging where it came from and when settings are called for that are missing
 * values.
 */
public class ConfigurationAssistant {

	static Logger logger = LoggerFactory.getLogger(ConfigurationAssistant.class);

	protected Properties properties = new Properties();

	private Set<String> missingKeys = new HashSet<String>();

	public String getProperty(String key) {
		Object object = properties.get(key);
		if (object == null) {
			if (!missingKeys.contains(key)) {
				missingKeys.add(key);
				logger.warn("Missing configuration for key: {}", key);
			}
			return null;
		}
		return object.toString();
	}

	public void loadConfiguration(String resourceName) {
		loadConfiguration(ConfigurationUtils.locateConfiguration(resourceName));
	}

	private void loadConfiguration(URL configurationLocation) {
		try {
			InputStream inputStream = configurationLocation.openStream();
			this.properties.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			throw new WseException("Failed to load configuration from " + configurationLocation, e);
		}
		logger.info("Configuration loaded from {}", configurationLocation.toString());
	}

	/**
	 * Used for testing.
	 */
	Set<String> getMissingKeys() {
		return missingKeys;
	}
}