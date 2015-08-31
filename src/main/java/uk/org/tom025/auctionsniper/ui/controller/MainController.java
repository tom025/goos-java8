package uk.org.tom025.auctionsniper.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import uk.org.tom025.auctionsniper.SniperSnapshot;
import uk.org.tom025.auctionsniper.ui.model.Sniper;

public class MainController {
  @FXML public TableView<Sniper> snipers;
  @FXML public TableColumn<Sniper, String> statusColumn;
  @FXML public TableColumn<Sniper, String> itemIdColumn;
  @FXML public TableColumn<Sniper, String> lastPriceColumn;
  @FXML public TableColumn<Sniper, String> lastBidColumn;
  private ObservableList<Sniper> sniperList = FXCollections.observableArrayList();

  @FXML private void initialize() {
    itemIdColumn.setCellValueFactory(p -> p.getValue().itemId);
    statusColumn.setCellValueFactory(p -> p.getValue().status);
    lastPriceColumn.setCellValueFactory(p -> p.getValue().lastPrice.asString());
    lastBidColumn.setCellValueFactory(p -> p.getValue().lastBid.asString());
    snipers.setItems(sniperList);
  }

  public void sniperAdded(SniperSnapshot sniperSnapshot) {
    sniperList.add(Sniper.newInstance(sniperSnapshot));
  }

  public void sniperStatusChanged(SniperSnapshot sniperSnapshot) {
    final Sniper sniper = sniperList.get(0);
    sniper.update(sniperSnapshot);
  }

}
