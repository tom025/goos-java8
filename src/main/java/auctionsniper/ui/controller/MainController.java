package auctionsniper.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
  public static final String STATUS_JOINING = "joining";

  @FXML private Label sniperStatus;

  @FXML private void initialize() {
    sniperStatus.setText(STATUS_JOINING);
  }
}
