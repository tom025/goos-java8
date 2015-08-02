package uk.org.tom025.auctionsniper.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
  public static final String STATUS_JOINING = "joining";
  public static final String STATUS_LOST = "lost";
  public static final String STATUS_BIDDING = "bidding";

  @FXML private Label sniperStatus;

  @FXML private void initialize() {
    sniperStatus.setText(STATUS_JOINING);
  }

  public void showStatus(String status) {
    sniperStatus.setText(status);
  }
}
