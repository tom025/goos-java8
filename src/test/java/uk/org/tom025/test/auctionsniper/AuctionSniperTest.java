package uk.org.tom025.test.auctionsniper;

import org.junit.Test;
import uk.org.tom025.auctionsniper.Auction;
import uk.org.tom025.auctionsniper.AuctionSniper;
import uk.org.tom025.auctionsniper.SniperListener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static uk.org.tom025.auctionsniper.AuctionEventListener.PriceSource.*;

public class AuctionSniperTest {

  private final SniperListener listener = mock(SniperListener.class);
  private final Auction auction = mock(Auction.class);
  private final String itemId = "itemId";
  private final AuctionSniper sniper = new AuctionSniper(itemId, auction, listener);

  @Test
  public void reportsLostIfAuctionClosesImmediately() throws Exception {
    sniper.auctionClosed();
    verify(listener).sniperLost();
  }

  @Test
  public void reportsLostIfAuctionClosesWhenBidding() throws Exception {
    sniper.currentPrice(1001, 7, FromOtherBidder);
    sniper.auctionClosed();
    verify(listener).sniperLost();
  }

  @Test
  public void reportsWonIfAuctionClosesWhenWinning() throws Exception {
    sniper.currentPrice(1001, 7, FromSniper);
    sniper.auctionClosed();
    verify(listener).sniperWon();
  }

  @Test
  public void reportsWinningWhenCurrentPriceComesFromSniper() throws Exception {
    sniper.currentPrice(1001, 7, FromSniper);
    verify(listener).sniperWinning();
  }

  @Test
  public void reportsBiddingWhenNewPriceArrivesFromOtherBidder() throws Exception {
    sniper.currentPrice(1001, 7, FromOtherBidder);
    verify(listener).sniperBidding();
  }

  @Test
  public void bidsHigherAndReportsBiddingWhenNewPriceArrives() throws Exception {
    int price = 1001;
    int increment = 7;
    sniper.currentPrice(price, increment, FromOtherBidder);
    verify(auction).bid(price + increment);
  }
}