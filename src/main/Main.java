package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Graph Visualizer");


        BorderPane layout = new BorderPane();
        Scene scene = new Scene(layout);

        MenuBar menuBar = new MenuBar();

        // --- Menu File
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().add(new MenuItem("Open"));

        Menu viewMenu = new Menu("View");
        viewMenu.getItems().add(new MenuItem("Zoom in"));
        viewMenu.getItems().add(new MenuItem("Zoom out"));
        viewMenu.getItems().add(new MenuItem("Move"));
        viewMenu.getItems().add(new MenuItem("Expansion/Contraction"));
        viewMenu.getItems().add(new MenuItem("Color"));
        viewMenu.getItems().add(new MenuItem("Vertex degree based formatting"));

        Menu editMenu = new Menu("Edit");
        editMenu.getItems().add(new MenuItem("Select"));
        editMenu.getItems().add(new MenuItem("Delete or change"));
        editMenu.getItems().add(new MenuItem("Shortest path"));

        Menu extrasMenu = new Menu("Extras");
        extrasMenu.getItems().add(new MenuItem("Export"));
        extrasMenu.getItems().add(new MenuItem("Import"));
        extrasMenu.getItems().add(new MenuItem("Undo"));
        extrasMenu.getItems().add(new MenuItem("Redo"));


        menuBar.getMenus().addAll(fileMenu, viewMenu, editMenu, extrasMenu);
        layout.setTop(menuBar);

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
