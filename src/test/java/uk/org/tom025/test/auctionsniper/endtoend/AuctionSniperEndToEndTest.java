package uk.org.tom025.test.auctionsniper.endtoend;

import org.junit.After;
import org.junit.Test;
import uk.org.tom025.test.auctionsniper.endtoend.testsupport.ApplicationRunner;
import uk.org.tom025.test.auctionsniper.endtoend.testsupport.FakeAuctionServer;

import static uk.org.tom025.test.auctionsniper.endtoend.testsupport.ApplicationRunner.SNIPER_ID;

public class AuctionSniperEndToEndTest {
  private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
  private final ApplicationRunner application = new ApplicationRunner();

  @Test
  public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
    auction.startSellingItem();

    application.startBiddingIn(auction);
    auction.hasReceivedJoinRequestFromSniper(SNIPER_ID);
    auction.announceClosed();
    application.showsSniperHasLostAuction();
  }

  @Test
  public void sniperMakesAHigherBidButLooses() throws Exception {
    auction.startSellingItem();

    application.startBiddingIn(auction);
    auction.hasReceivedJoinRequestFromSniper(SNIPER_ID);

    auction.reportPrice(1000, 98, "other bidder");
    application.hasShownSniperIsBidding();

    auction.hasReceivedBid(1098, SNIPER_ID);

    auction.announceClosed();
    application.showsSniperHasLostAuction();
  }

  @Test
  public void sniperWinsAnAuctionByBiddingHigher() throws Exception {
    auction.startSellingItem();

    application.startBiddingIn(auction);
    auction.hasReceivedJoinRequestFromSniper(SNIPER_ID);

    auction.reportPrice(1000, 98, "other bidder");
    application.hasShownSniperIsBidding();

    auction.hasReceivedBid(1098, SNIPER_ID);

    auction.reportPrice(1000, 98, SNIPER_ID);
    application.hasShownSniperIsWining();

    auction.announceClosed();
    application.showsSniperHasWonAuction();
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
