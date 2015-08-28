package uk.org.tom025.auctionsniper.ui.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import uk.org.tom025.auctionsniper.AuctionSniper;

public class MainController {
  public static final String STATUS_JOINING = "joining";
  public static final String STATUS_LOST = "lost";
  public static final String STATUS_BIDDING = "bidding";
  public static final String STATUS_WINNING = "winning";
  public static final String STATUS_WON = "won";
  @FXML public TableView<Sniper> snipers;
  @FXML public TableColumn<Sniper, String> statusColumn;
  private ObservableList<Sniper> sniperList = FXCollections.observableArrayList();

  @FXML private void initialize() {
    statusColumn.setCellValueFactory(p -> p.getValue().status);

    snipers.setItems(sniperList);
  }


  public void showStatus(String status) {
    sniperList.get(0).status.set(status);
  }

  public void sniperAdded(String itemId, String status) {
    sniperList.add(Sniper.newInstance(itemId, status));
  }

  public static class Sniper {
    public final SimpleStringProperty status;
    public final SimpleStringProperty itemId;

    private static Sniper newInstance(String itemId, String status) {
      return new Sniper(
        new SimpleStringProperty(status),
        new SimpleStringProperty(itemId)
      );
    }

    private Sniper(SimpleStringProperty status, SimpleStringProperty itemId) {
      this.status = status;
      this.itemId = itemId;
    }
  }
}
