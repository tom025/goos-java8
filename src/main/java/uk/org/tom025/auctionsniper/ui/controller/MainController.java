package uk.org.tom025.auctionsniper.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import uk.org.tom025.auctionsniper.SniperSnapshot;
import uk.org.tom025.auctionsniper.ui.model.Sniper;

public class MainController {
  public static final String STATUS_JOINING = "joining";
  public static final String STATUS_LOST = "lost";
  public static final String STATUS_BIDDING = "bidding";
  public static final String STATUS_WINNING = "winning";
  public static final String STATUS_WON = "won";
  public static final String[] STATUS_TEXT = {
    STATUS_JOINING,
    STATUS_BIDDING,
    STATUS_WINNING,
    STATUS_LOST,
    STATUS_WON
  };
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

  public void showStatus(String status) {
    final Sniper sniper = sniperList.get(0);
    sniper.status.set(status);
  }

  public void sniperAdded(String itemId, String status, int lastPrice, int lastBid) {
    sniperList.add(Sniper.newInstance(itemId, status, lastPrice, lastBid));
  }

  public void sniperStatusChanged(SniperSnapshot sniperSnapshot) {
    final Sniper sniper = sniperList.get(0);
    final String statusText = STATUS_TEXT[sniperSnapshot.state.ordinal()];
    sniper.status.set(statusText);
    sniper.lastPrice.set(sniperSnapshot.lastPrice);
    sniper.lastBid.set(sniperSnapshot.lastBid);
  }
}
