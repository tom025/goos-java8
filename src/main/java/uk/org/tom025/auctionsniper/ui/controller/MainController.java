package uk.org.tom025.auctionsniper.ui.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.logging.Logger;

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
    sniperList.add(Sniper.newInstance(STATUS_JOINING));
  }

  public void showStatus(String status) {
    sniperList.get(0).status.set(status);
  }

  private static class Sniper {
    public final SimpleStringProperty status;

    private static Sniper newInstance(String status) {
      return new Sniper(new SimpleStringProperty(status));
    }

    private Sniper(SimpleStringProperty status) {
      this.status = status;
    }
  }
}
