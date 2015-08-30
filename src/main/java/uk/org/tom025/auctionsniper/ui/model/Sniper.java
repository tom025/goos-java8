package uk.org.tom025.auctionsniper.ui.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Sniper {
  public final SimpleStringProperty status;
  public final SimpleStringProperty itemId;
  public final SimpleIntegerProperty lastPrice;

  public static Sniper newInstance(String itemId, String status, int lastPrice) {
    return new Sniper(
      new SimpleStringProperty(status),
      new SimpleStringProperty(itemId),
      new SimpleIntegerProperty(lastPrice)
    );
  }

  private Sniper(
    SimpleStringProperty status,
    SimpleStringProperty itemId,
    SimpleIntegerProperty lastPrice
  ) {
    this.status = status;
    this.itemId = itemId;
    this.lastPrice = lastPrice;
  }
}
