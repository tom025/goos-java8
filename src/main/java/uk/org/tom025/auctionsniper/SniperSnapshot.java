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
}
