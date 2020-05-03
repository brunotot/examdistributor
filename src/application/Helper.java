package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Helper class. Contains helper methods for minor functionalities.
 * 
 * @author Bruno
 *
 */
public class Helper {
	
	/**
	 * Returns duration in format hh : mm : ss.
	 * @param seconds Value of seconds to convert
	 * @return Duration in hh : mm : ss format
	 */
	public static String getDurationString(int seconds) {
	    int hours = seconds / 3600;
	    int minutes = (seconds % 3600) / 60;
	    seconds = seconds % 60;

	    return String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds);
	}
	
	/**
	 * Method for acquiring Properties configuration object.
	 * @param configFile Configuration file.
	 * 
	 * @return Configuration properties.
	 */
	public static Properties loadProperties(String configFile) {
		Properties properties = new Properties();
		try {
			File file = new File(configFile);
			if (!file.exists()) {
				Helper.createProperties(properties, configFile);
			}
	    	FileInputStream in = new FileInputStream(new File(configFile));
			properties.loadFromXML(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	/**
	 * Saves given properties to config.xml.
	 * 
	 * @param properties Properties object to save
	 * @param file File name
	 */
	public static void saveProperties(Properties properties, String file) {
		try {
			File configFile = new File(file);
			FileOutputStream out = new FileOutputStream(configFile);
			properties.storeToXML(out, "Configuration");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates new properties file on file system.
	 * 
	 * @param properties Properties to create on
	 * @param file File path
	 */
	public static void createProperties(Properties properties, String file) {
		properties.setProperty("seconds", String.valueOf(Constants.TOTAL_SECONDS_TIME_DEFAULT_VALUE));
		properties.setProperty("tasks", String.valueOf(Constants.NUMBER_OF_TASKS_DEFAULT_VALUE));
		String instructions = "START        -> starts the application\n" + 
				"F10 (NEXT) -> continues to next task\n" + 
				"\n" + 
				"Purpose:\n" + 
				"Fair time distribution while attempting an online exam.\n" + 
				"\n" + 
				"How it works?\r\n" + 
				"- Start the program when starting an exam\n" + 
				"- Application fairly distributes time for each task\n" + 
				"- You get informed about which task number you are on\n" + 
				"- You get informed 10 seconds before your time on current task is about to run out\n" + 
				"- When task is finished sooner you press NEXT (F10 on keyboard)\n" + 
				"- Application then recalculates your total time for each task and you get your time evenly distributed\n";
		properties.setProperty("instructions", instructions);
		properties.setProperty("window_height", "130");
		properties.setProperty("window_width", "300");
		Helper.saveProperties(properties, file);
	}
	
}
