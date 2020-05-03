package application;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for Index.fxml.
 * 
 * @author Bruno
 *
 */
public class IndexController implements NativeKeyListener {
	
	/**
	 * Total number of tasks.
	 */
	int numberOfTasks;
	
	/**
	 * Total time in seconds.
	 */
	int totalSecondsTime;
	
	/**
	 * Current time in seconds.
	 */
	int currentTimeSecond = 1;
	
	/**
	 * Current task time in sceonds.
	 */
	int currentTaskTimeSecond = 1;
	
	/**
	 * Current task number.
	 */
	int taskNumber = 0;
	
	/**
	 * Time in seconds.
	 */
	Integer secondsTime;
	
	/**
	 * Current task's timeline.
	 */
	Timeline timeline;
	
	/**
	 * Main timeline.
	 */
	Timeline mainTimeline;
	
	/**
	 * Next button.
	 */
	@FXML
	Button nextButton;
	
	/**
	 * Current task's progress bar.
	 */
	@FXML
	ProgressBar currentProgressBar;

	/**
	 * Config button from menu.
	 */
	@FXML
	MenuItem configButton;
	
	/**
	 * Main progress bar.
	 */
	@FXML
	ProgressBar progressBar;
	
	/**
	 * Start button.
	 */
	@FXML
	Button mainButton;
	
	/**
	 * Main time.
	 */
	@FXML
	Text mainTime;
	
	/**
	 * Text left of task's progress bar.
	 */
	@FXML
	Text taskText;
	
	/**
	 * Text which contains timer for current task.
	 */
	@FXML
	Text currentTaskText;
	
	/**
	 * Configuration properties.
	 */
	Properties properties;
	
	/**
	 * Initializer method.
	 */
    public void initialize() {
		nextButton.setDisable(true);
    	LogManager.getLogManager().reset();
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
    	
    	properties = Helper.loadProperties(Constants.CONFIG_FILE);
		totalSecondsTime = Integer.parseInt(properties.getProperty("seconds"));
		numberOfTasks = Integer.parseInt(properties.getProperty("tasks"));
		secondsTime = totalSecondsTime / numberOfTasks;
		
		try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.exit(1);
        }
        GlobalScreen.addNativeKeyListener(this);
        
		taskText.setText("Task " + String.format("%02d", taskNumber) + ":  ");
		currentTaskText.setText(Helper.getDurationString(secondsTime));
		mainTime.setText(Helper.getDurationString(totalSecondsTime));
	}
	
    /**
     * Sets next task event.
     * 
     * @param event Used as a flag (if exists then the button NEXT is pressed, and recalculation is done)
     */
	@FXML
	void setNextTask(ActionEvent event) {
		if (!mainButton.isDisable()) {
			return;
		}
		
		if (timeline != null) {
			timeline.stop();
		}

		/* RESET WHEN DONE */
		if (taskNumber == numberOfTasks) {
			timeline.stop();
			mainTimeline.stop();
			mainButton.setDisable(false);
			configButton.setDisable(false);
			progressBar.setProgress(0);
			currentProgressBar.setProgress(0);
			taskNumber = 0;
			taskText.setText("Task " + String.format("%02d", taskNumber) + ":  ");
			currentTimeSecond = 1;
			currentTaskTimeSecond = 1;
			secondsTime = Integer.parseInt(properties.getProperty("seconds")) / numberOfTasks;
			mainTime.setText(Helper.getDurationString(Integer.parseInt(properties.getProperty("seconds"))));
			currentTaskText.setText(Helper.getDurationString(secondsTime));
			nextButton.setDisable(true);
			return;
		}
		
		if (event != null) {
			int extraSeconds = secondsTime - currentTaskTimeSecond;
			int remainingTaskNumber = numberOfTasks - taskNumber;
			int add = (int) (extraSeconds / remainingTaskNumber);
			secondsTime += add;
			currentTaskTimeSecond = 1;
		}
		
		taskNumber++;
		if (taskNumber <= 40) {
			playSound("resources/sounds/effects/" + taskNumber + ".wav");
		}
    	
		taskText.setText("Task " + String.format("%02d", taskNumber) + ":  ");
		
		timeline = new Timeline(new KeyFrame(Duration.seconds(1),
	    		new EventHandler<ActionEvent>() {
	    			@Override
	    			public void handle(ActionEvent event) {
	    				Platform.runLater(new Runnable() {
	    					@Override 
	    					public void run() {
	    						double percentage = ((double) currentTaskTimeSecond / secondsTime);
	    						currentProgressBar.setProgress(percentage);
	    						
	    						int remainingSeconds = (int) (secondsTime - currentTaskTimeSecond);
	    						currentTaskText.setText(Helper.getDurationString(remainingSeconds));
	    						currentTaskTimeSecond++;
	    						
	    						if (remainingSeconds == 11) {
	    				        	playSound(Constants.EFFECTS_TEN_SECONDS);
	    						}
	    						
	    						if (remainingSeconds == 2) {
	    							playSound(Constants.EFFECTS_NEXT_TASK);
	    						}
	    						
	    						if (percentage == 1) {
	    							currentTaskTimeSecond = 1;
	    							if (taskNumber == numberOfTasks) {
	    								mainTimeline.stop();
	    								return;
	    							}
	    							setNextTask(null);
	    						}
	    					}
	    				});
	    			}
	    		}));
	    
	    timeline.setCycleCount(secondsTime);
	    timeline.play();
	}
	
	/**
	 * Opens Instructions window.
	 */
	@FXML
	public void openInstructions() {
		try {
			Stage stage = new Stage();
			VBox vbox = new VBox();
			String readmeText = properties.getProperty("instructions");
			Label readmeLabel = new Label(readmeText);
			readmeLabel.setWrapText(true);
			readmeLabel.setPadding(new Insets(15, 15, 15, 15));
			vbox.getChildren().add(readmeLabel);
			Scene scene = new Scene(vbox, Constants.WIDTH_INSTRUCTIONS, Constants.HEIGHT_INSTRUCTIONS);
			stage.setScene(scene);
			stage.getIcons().add(new Image(Main.class.getResourceAsStream(Constants.ICON_IMAGE)));
			stage.setResizable(false);
			stage.setTitle("Instructions"); 
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Opens Settings window.
	 */
	@FXML
	public void openSettings() {
		try {
			Stage stage = new Stage();
			ConfigurationController controller = new ConfigurationController(totalSecondsTime, numberOfTasks);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Configuration.fxml"));
			loader.setController(controller);
			BorderPane root = loader.load();
			Scene scene = new Scene(root, 160, 60);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.setTitle("Configuration"); 
			stage.getIcons().add(new Image(Main.class.getResourceAsStream(Constants.ICON_IMAGE)));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setOnHidden(e -> {
			    controller.shutdown();
			    try {
		    		FileInputStream in;
		    		in = new FileInputStream(new File("config.xml"));
		    		Properties properties = new Properties();
		    		properties.loadFromXML(in);
		    		int secs = Integer.parseInt(properties.getProperty("seconds"));
		    		totalSecondsTime = secs;
		    		numberOfTasks = Integer.parseInt(properties.getProperty("tasks"));
		    		secondsTime = secs / numberOfTasks;
		    		currentTaskText.setText(Helper.getDurationString(secondsTime));
		    		mainTime.setText(Helper.getDurationString(secs));
		    	} catch (Exception ex) {
		    		ex.printStackTrace();
		    	}
			});
			stage.showAndWait();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts main timer - onclick of Start.
	 */
	@FXML
	void startMainTimer() {
		nextButton.setDisable(false);
		configButton.setDisable(true);
		mainButton.setDisable(true);
		mainTimeline = new Timeline(new KeyFrame(Duration.seconds(1),
	    		new EventHandler<ActionEvent>() {
	    			@Override
	    			public void handle(ActionEvent event) {
	    				Platform.runLater(new Runnable() {
	    					@Override 
	    					public void run() {
	    						double percentage = ((double) currentTimeSecond / totalSecondsTime);
	    						progressBar.setProgress(percentage);
	    						
	    						int remainingSeconds = (int) (totalSecondsTime - currentTimeSecond);
	    						mainTime.setText(Helper.getDurationString(remainingSeconds));
	    						currentTimeSecond++;
	    					}
	    				});
	    			}
	    		}));
	    
		mainTimeline.setCycleCount(totalSecondsTime);
		mainTimeline.play();
	    setNextTask(null);
	}
	
	/**
	 * Plays sound from given path.
	 * 
	 * @param soundFile Sound file.
	 */
	void playSound(String soundFile) {
		try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource(soundFile));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}

	public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_F10) {
        	setNextTask(new ActionEvent());
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {

    }

    public void nativeKeyTyped(NativeKeyEvent e) {
    
    }
}
