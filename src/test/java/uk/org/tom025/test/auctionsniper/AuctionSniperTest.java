package uk.org.tom025.test.auctionsniper;

import org.junit.Test;
import uk.org.tom025.auctionsniper.Auction;
import uk.org.tom025.auctionsniper.AuctionSniper;
import uk.org.tom025.auctionsniper.SniperListener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AuctionSniperTest {

  private final SniperListener listener = mock(SniperListener.class);
  private final Auction auction = mock(Auction.class);
  private final AuctionSniper sniper = new AuctionSniper(auction, listener);

  @Test
  public void reportsLostWhenAuctionCloses() throws Exception {
    sniper.auctionClosed();
    verify(listener).sniperLost();
  }

  @Test
  public void bidsHigherAndReportsBiddingWhenNewPriceArrives() throws Exception {
    int price = 1001;
    int increment = 7;
    sniper.currentPrice(price, increment);
    verify(listener).sniperBidding();
    verify(auction).bid(price + increment);
  }
}