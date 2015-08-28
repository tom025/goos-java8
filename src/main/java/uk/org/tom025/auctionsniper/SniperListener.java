package uk.org.tom025.auctionsniper;

public interface SniperListener {
  void sniperLost();
  void sniperBidding();
  void sniperWinning();
  void sniperWon();
  void sniperJoined(AuctionSniper auctionSniper);
}
