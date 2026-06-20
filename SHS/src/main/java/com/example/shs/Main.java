package com.example.shs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);

        primaryStage.setTitle("Student Helping System");
        primaryStage.setScene(scene);

        // Add shutdown hook to save data when application closes
        primaryStage.setOnCloseRequest(this::handleCloseRequest);

        // Add shutdown hook for unexpected termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DataManager.getInstance().shutdown();
        }));

        primaryStage.show();

        // Print startup information
        System.out.println("=== Student Helping System Started ===");
        System.out.println("Data will be automatically saved to the 'data' directory");
        System.out.println("=====================================");
    }

    private void handleCloseRequest(WindowEvent event) {
        // Save data before closing
        System.out.println("Application closing - saving data...");
        DataManager.getInstance().shutdown();
        Platform.exit();
    }

    @Override
    public void stop() throws Exception {
        // This method is called when the application is shutting down
        DataManager.getInstance().shutdown();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}