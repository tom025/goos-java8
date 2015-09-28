package uk.org.tom025.auctionsniper.ui.controller;

import javafx.fxml.FXML;
import uk.org.tom025.auctionsniper.SniperSnapshot;

public class MainController {
  @FXML public SniperController snipersController;

  public void sniperAdded(SniperSnapshot sniperSnapshot) {
    snipersController.sniperAdded(sniperSnapshot);
  }

  public void sniperStatusChanged(SniperSnapshot sniperSnapshot) {
    snipersController.sniperStatusChanged(sniperSnapshot);
  }

}
