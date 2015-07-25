package auctionsniper;

import auctionsniper.ui.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
  public static void main(String... args) throws Exception {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
    final StackPane root = loader.load();
    final MainController mainController = loader.getController();

    primaryStage.setTitle("Auction Sniper");
    primaryStage.setScene(new Scene(root, 800, 600));
    primaryStage.show();
  }
}
