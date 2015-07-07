package auctionsniper;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
  public static void main(String... args) throws Exception {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    final StackPane root = new StackPane();
    final Label label = new Label("foo");
    label.setId("status");

    root.getChildren().add(label);
    primaryStage.setScene(new Scene(root, 800, 600));
    primaryStage.show();
  }
}
