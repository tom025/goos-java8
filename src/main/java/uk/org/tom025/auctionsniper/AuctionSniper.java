package uk.org.tom025.auctionsniper;

import static uk.org.tom025.auctionsniper.AuctionEventListener.PriceSource.FromSniper;

public class AuctionSniper implements AuctionEventListener {
  public final SniperSnapshot sniperSnapshot;
  private final Auction auction;
  private final SniperListener listener;
  private boolean isWinning = false;
  public final String itemId;

  public static AuctionSniper newInstance(String itemId, Auction auction, SniperListener listener) {
    SniperSnapshot sniperSnapshot = new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
    final AuctionSniper auctionSniper = new AuctionSniper(sniperSnapshot, auction, listener);
    listener.sniperJoined(auctionSniper);
    return auctionSniper;
  }

  public AuctionSniper(SniperSnapshot sniperSnapshot, Auction auction, SniperListener listener) {
    this.sniperSnapshot = sniperSnapshot;
    this.itemId = sniperSnapshot.itemId;
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
        listener.sniperWinning(new SniperSnapshot(itemId, price, price, SniperState.WINNING));
        break;
      case FromOtherBidder:
        int bid = price + increment;
        auction.bid(bid);
        listener.sniperBidding(new SniperSnapshot(itemId, price, bid, SniperState.BIDDING));
        break;
      default:
        throw new UnsupportedOperationException(
          "Do not know how to handle price source: " + priceSource
        );
    }
  }
}
