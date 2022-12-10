
package com.abcbank.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;


public class Login extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("ABC Bank");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/icon.png"))));
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
