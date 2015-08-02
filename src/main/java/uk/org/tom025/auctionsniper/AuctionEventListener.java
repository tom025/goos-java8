package uk.org.tom025.auctionsniper;

public interface AuctionEventListener {
  void auctionClosed();
  void currentPrice(int price, int increment);
}
