package uk.org.tom025.test.auctionsniper;

import org.junit.Test;
import uk.org.tom025.auctionsniper.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static uk.org.tom025.auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static uk.org.tom025.auctionsniper.AuctionEventListener.PriceSource.FromSniper;

public class AuctionSniperTest {
  private static final String ITEM_ID = "ITEM_ID";

  private final SniperListener listener = mock(SniperListener.class);
  private final Auction auction = mock(Auction.class);
  private final AuctionSniper sniper = AuctionSniper.newInstance(ITEM_ID, auction, listener);

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
    int price = 1001;
    int increment = 7;
    sniper.currentPrice(price, increment, FromSniper);
    verify(listener).sniperWinning(
      new SniperSnapshot(
        ITEM_ID,
        price,
        price,
        SniperState.WINNING
      )
    );
  }

  @Test
  public void reportsBiddingWhenNewPriceArrivesFromOtherBidder() throws Exception {
    int price = 1001;
    int increment = 7;
    int bid = price + increment;
    sniper.currentPrice(price, increment, FromOtherBidder);
    verify(listener).sniperBidding(
      new SniperSnapshot(
        ITEM_ID,
        price,
        bid,
        SniperState.BIDDING
      )
    );
  }

  @Test
  public void bidsHigherAndReportsBiddingWhenNewPriceArrives() throws Exception {
    int price = 1001;
    int increment = 7;
    sniper.currentPrice(price, increment, FromOtherBidder);
    verify(auction).bid(price + increment);
  }
}
