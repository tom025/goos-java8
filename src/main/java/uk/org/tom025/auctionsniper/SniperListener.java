package uk.org.tom025.auctionsniper;

public interface SniperListener {
  void sniperLost();
  void sniperBidding(SniperSnapshot sniperSnapshot);
  void sniperWinning(SniperSnapshot sniperSnapshot);
  void sniperWon();
  void sniperJoined(AuctionSniper auctionSniper);
}
