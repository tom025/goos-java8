package uk.org.tom025.auctionsniper;

public interface SniperListener {
  void sniperStateChanged(SniperSnapshot sniperSnapshot);
  void sniperAdded(SniperSnapshot sniperSnapshot);
}
