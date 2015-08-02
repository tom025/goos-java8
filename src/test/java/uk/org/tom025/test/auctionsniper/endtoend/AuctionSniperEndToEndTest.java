package uk.org.tom025.test.auctionsniper.endtoend;

import org.junit.After;
import org.junit.Test;
import uk.org.tom025.test.auctionsniper.endtoend.testsupport.ApplicationRunner;
import uk.org.tom025.test.auctionsniper.endtoend.testsupport.FakeAuctionServer;

public class AuctionSniperEndToEndTest {
  private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
  private final ApplicationRunner application = new ApplicationRunner();

  @Test
  public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
    auction.startSellingItem();

    application.startBiddingIn(auction);
    auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_ID);
    auction.announceClosed();
    application.showsSniperHasLostAuction();
  }

  @Test
  public void sniperMakesAHigherBidButLooses() throws Exception {
    auction.startSellingItem();

    application.startBiddingIn(auction);
    auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_ID);

    auction.reportPrice(1000, 98, "other bidder");
    application.hasShownSniperIsBidding();

    auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_ID);

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
