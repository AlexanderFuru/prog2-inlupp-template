package se.su.inlupp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Gui extends Application {

  @Override
  public void start(Stage stage) {
    MainView mainView = new MainView();
    Scene scene = new Scene(mainView, 1920, 1080);

    stage.setTitle("Subway system simulator");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
