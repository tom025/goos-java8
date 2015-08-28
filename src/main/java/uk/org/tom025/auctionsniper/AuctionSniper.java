package uk.org.tom025.auctionsniper;

import static uk.org.tom025.auctionsniper.AuctionEventListener.PriceSource.FromSniper;

public class AuctionSniper implements AuctionEventListener {
  public final String itemId;
  private final Auction auction;
  private final SniperListener listener;
  private boolean isWinning = false;

  public AuctionSniper(String itemId, Auction auction, SniperListener listener) {
    this.itemId = itemId;
    this.auction = auction;
    this.listener = listener;
  }

  @Override
  public void auctionClosed() {
    if (isWinning) {
      listener.sniperWon();
    } else {
      listener.sniperLost();
    }
  }

  @Override
  public void currentPrice(int price, int increment, PriceSource priceSource) {
    isWinning = priceSource == FromSniper;
    switch (priceSource) {
      case FromSniper:
        listener.sniperWinning();
        break;
      case FromOtherBidder:
        auction.bid(price + increment);
        listener.sniperBidding();
        break;
      default:
        throw new UnsupportedOperationException(
          "Do not know how to handle price source: " + priceSource
        );
    }
  }

  public static AuctionSniper newInstance(String itemId, Auction auction, SniperListener listener) {
    final AuctionSniper auctionSniper = new AuctionSniper(itemId, auction, listener);
    listener.sniperJoined(auctionSniper);
    return auctionSniper;
  }
}
