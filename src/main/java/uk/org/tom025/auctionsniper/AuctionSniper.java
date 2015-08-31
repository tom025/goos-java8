package uk.org.tom025.auctionsniper;

public class AuctionSniper implements AuctionEventListener {
  private final Auction auction;
  private final SniperListener listener;
  public final String itemId;
  private SniperSnapshot sniperSnapshot;

  public static AuctionSniper newInstance(String itemId, Auction auction, SniperListener listener) {
    SniperSnapshot sniperSnapshot = SniperSnapshot.joining(itemId);
    final AuctionSniper auctionSniper = new AuctionSniper(sniperSnapshot, auction, listener);
    listener.sniperAdded(auctionSniper.sniperSnapshot);
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
    sniperSnapshot = sniperSnapshot.closed();
    notifyChange();
  }

  private void notifyChange() {
    listener.sniperStateChanged(sniperSnapshot);
  }

  @Override
  public void currentPrice(int price, int increment, PriceSource priceSource) {
    switch (priceSource) {
      case FromSniper:
        sniperSnapshot = sniperSnapshot.winning(price);
        break;
      case FromOtherBidder:
        int bid = price + increment;
        auction.bid(bid);
        sniperSnapshot = sniperSnapshot.bidding(price, bid);
        break;
    }
    notifyChange();
  }

}
