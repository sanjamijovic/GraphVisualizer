package graphvisualizer;

import graphvisualizer.ui.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Graph Visualizer");

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        Controller controller = loader.getController();
        primaryStage.setOnCloseRequest(e -> controller.stopThread());
    }


    public static void main(String[] args) {
        launch(args);
    }
}
