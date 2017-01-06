package uk.org.tom025.test.auctionsniper.unit;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Test;
import uk.org.tom025.auctionsniper.*;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;
import static uk.org.tom025.auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static uk.org.tom025.auctionsniper.AuctionEventListener.PriceSource.FromSniper;
import static uk.org.tom025.test.auctionsniper.unit.AuctionSniperTest.SniperSnapshotWithStateMatcher.aSniperWithState;

public class AuctionSniperTest {
  private static final String ITEM_ID = "ITEM_ID";

  private final SniperListener listener = mock(SniperListener.class);
  private final Auction auction = mock(Auction.class);
  private final AuctionSniper sniper = AuctionSniper.newInstance(ITEM_ID, auction, listener);

  @Test
  public void reportsLostIfAuctionClosesImmediately() throws Exception {
    sniper.auctionClosed();
    verify(listener).sniperStateChanged(
      argThat(CoreMatchers.is(aSniperWithState(SniperState.LOST)))
    );
  }

  @Test
  public void reportsLostIfAuctionClosesWhenBidding() throws Exception {
    sniper.currentPrice(1001, 7, FromOtherBidder);
    verify(listener).sniperStateChanged(
      argThat(CoreMatchers.is(aSniperWithState(SniperState.BIDDING)))
    );
    sniper.auctionClosed();
    verify(listener, atLeastOnce()).sniperStateChanged(
      argThat(CoreMatchers.is(aSniperWithState(SniperState.LOST)))
    );
  }

  @Test
  public void reportsWonIfAuctionClosesWhenWinning() throws Exception {
    sniper.currentPrice(1001, 7, FromSniper);
    verify(listener).sniperStateChanged(
      argThat(CoreMatchers.is(aSniperWithState(SniperState.WINNING)))
    );
    sniper.auctionClosed();
    verify(listener).sniperStateChanged(
      argThat(CoreMatchers.is(aSniperWithState(SniperState.WON)))
    );
  }

  @Test
  public void reportsWinningWhenCurrentPriceComesFromSniper() throws Exception {
    int price = 1001;
    int increment = 7;
    sniper.currentPrice(price, increment, FromSniper);
    verify(listener).sniperStateChanged(
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
    verify(listener).sniperStateChanged(
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

  public static class SniperSnapshotWithStateMatcher extends TypeSafeDiagnosingMatcher<SniperSnapshot> {
    private final SniperState expectedSniperState;

    public static SniperSnapshotWithStateMatcher aSniperWithState(SniperState expectedSniperState) {
      return new SniperSnapshotWithStateMatcher(expectedSniperState);
    }

    public SniperSnapshotWithStateMatcher(SniperState expectedSniperState) {
      this.expectedSniperState = expectedSniperState;
    }

    @Override
    public void describeTo(Description description) {
      description
        .appendText("a sniper with state ")
        .appendValue(expectedSniperState);
    }

    @Override
    protected boolean matchesSafely(SniperSnapshot sniperSnapshot, Description mismatchDescription) {
      return expectedSniperState.equals(sniperSnapshot.state);
    }
  }
}
