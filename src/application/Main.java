package application;
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Main class.
 * 
 * @author Bruno
 *
 */
public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource(Constants.FXML_INDEX));
			Scene scene = new Scene(root, 300, 130);
			scene.getStylesheets().add(getClass().getResource(Constants.CSS_INDEX).toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setTitle(Constants.WINDOW_TITLE);
			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream(Constants.ICON_IMAGE)));
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			    @Override
			    public void handle(WindowEvent t) {
			        Platform.exit();
			        System.exit(0);
			    }
			});
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Main method. Used for launching JavaFX app.
	 * @param args Command line arguments, default is null
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
}
