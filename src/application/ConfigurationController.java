package application;

import java.util.Properties;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Controller for Configuration.fxml.
 * 
 * @author Bruno
 *
 */
public class ConfigurationController {
	
	/**
	 * FXML element which represents seconds TextField.
	 */
	@FXML
	TextField seconds;
	
	/**
	 * FXML element which represents tasks TextField.
	 */
	@FXML
	TextField tasks;

	/**
	 * Number of seconds from configuration initialized from the constructor.
	 */
	Integer secondsTime;
	
	/**
	 * Number of tasks from configuration initialized from the constructor.
	 */
	Integer numberOfTasks;

	/**
	 * Saves new configuration on window shutdown.
	 */
    public void shutdown() {
    	Properties properties = Helper.loadProperties(Constants.CONFIG_FILE);
		properties.setProperty("seconds", seconds.getText());
		properties.setProperty("tasks", tasks.getText());
		Helper.saveProperties(properties, Constants.CONFIG_FILE);
    }
    
    /**
     * Constructor for ConfigurationController. Used to initialize members secondsTime and numberOfTasks.
     * 
     * @param secondsTime Integer member for configured total number of seconds
     * @param numberOfTasks Integer member for configured total number of tasks
     */
	public ConfigurationController(Integer secondsTime, Integer numberOfTasks) {
		this.secondsTime = secondsTime;
		this.numberOfTasks = numberOfTasks;
	}

	/**
	 * Initializer method. Used for initializing TextFields seconds and tasks and
	 * setting their text property to numbers only. 
	 */
	public void initialize() {
		seconds.setText(secondsTime.toString());
		tasks.setText(numberOfTasks.toString());
		
		seconds.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	seconds.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		    }
		});
		
		tasks.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	tasks.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		    }
		});
	}
}
