package uk.org.tom025.auctionsniper;

import java.util.Objects;

public class SniperSnapshot {
  public final String itemId;
  public final int lastPrice;
  public final int lastBid;
  public final SniperState state;

  public SniperSnapshot(
    String itemId,
    int lastPrice,
    int lastBid,
    SniperState state
  ) {
    this.itemId = itemId;
    this.lastPrice = lastPrice;
    this.lastBid = lastBid;
    this.state = state;
  }

  public static SniperSnapshot joining(String itemId) {
    return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SniperSnapshot that = (SniperSnapshot) o;
    return Objects.equals(lastPrice, that.lastPrice) &&
      Objects.equals(lastBid, that.lastBid) &&
      Objects.equals(itemId, that.itemId) &&
      Objects.equals(state, that.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemId, lastPrice, lastBid, state);
  }

  @Override
  public String toString() {
    return "SniperSnapshot{" +
      "itemId='" + itemId + '\'' +
      ", lastPrice=" + lastPrice +
      ", lastBid=" + lastBid +
      ", state=" + state +
      '}';
  }

  SniperSnapshot winning(int price) {
    return new SniperSnapshot(itemId, price, price, SniperState.WINNING);
  }

  SniperSnapshot bidding(int price, int bid) {
    return new SniperSnapshot(itemId, price, bid, SniperState.BIDDING);
  }

  public SniperSnapshot closed() {
    return new SniperSnapshot(
      itemId,
      lastPrice,
      lastBid,
      state.whenAuctionClosed()
    );
  }
}
