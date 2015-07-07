package uk.org.tom025.test.endtoend.auctionsniper;

import org.junit.After;
import org.junit.Test;
import uk.org.tom025.test.endtoend.auctionsniper.testsupport.ApplicationRunner;
import uk.org.tom025.test.endtoend.auctionsniper.testsupport.FakeAuctionServer;

import java.util.concurrent.TimeoutException;

public class AuctionSniperEndToEndTest {
  private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
  private final ApplicationRunner application = new ApplicationRunner();

  @Test
  public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
    auction.startSellingItem();
    application.startBiddingIn(auction);
    auction.hasReceivedJoinRequestFromSniper();
    auction.announceClosed();
    application.showsSniperHasLostAuction();
  }

  @After
  public void stopAuction() {
    auction.stop();
  }

  @After
  public void stopApplication() throws Exception {
    application.stop();
  }

}
