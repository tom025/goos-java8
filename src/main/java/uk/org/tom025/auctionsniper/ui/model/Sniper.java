package uk.org.tom025.auctionsniper.ui.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import uk.org.tom025.auctionsniper.SniperSnapshot;

public class Sniper {
  public static final String STATUS_WON = "won";
  public static final String STATUS_WINNING = "winning";
  public static final String STATUS_BIDDING = "bidding";
  public static final String STATUS_LOST = "lost";
  public static final String STATUS_JOINING = "joining";
  public static final String[] STATUS_TEXT = {
    STATUS_JOINING,
    STATUS_BIDDING,
    STATUS_WINNING,
    STATUS_LOST,
    STATUS_WON
  };
  public final SimpleStringProperty status;
  public final SimpleStringProperty itemId;
  public final SimpleIntegerProperty lastPrice;
  public final SimpleIntegerProperty lastBid;

  private Sniper(
    SimpleStringProperty status,
    SimpleStringProperty itemId,
    SimpleIntegerProperty lastPrice,
    SimpleIntegerProperty lastBid
  ) {
    this.status = status;
    this.itemId = itemId;
    this.lastPrice = lastPrice;
    this.lastBid = lastBid;
  }

  public static Sniper newInstance(SniperSnapshot sniperSnapshot) {
    return new Sniper(
      new SimpleStringProperty(STATUS_TEXT[sniperSnapshot.state.ordinal()]),
      new SimpleStringProperty(sniperSnapshot.itemId),
      new SimpleIntegerProperty(sniperSnapshot.lastPrice),
      new SimpleIntegerProperty(sniperSnapshot.lastBid)
    );
  }

  public void update(SniperSnapshot sniperSnapshot) {
    final String statusText = STATUS_TEXT[sniperSnapshot.state.ordinal()];
    status.set(statusText);
    lastPrice.set(sniperSnapshot.lastPrice);
    lastBid.set(sniperSnapshot.lastBid);
  }
}
