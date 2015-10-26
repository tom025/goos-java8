package uk.org.tom025.test.auctionsniper.endtoend.testsupport;

import javafx.application.Application;
import org.testfx.api.FxToolkit;
import uk.org.tom025.auctionsniper.Main;
import uk.org.tom025.auctionsniper.ui.model.Sniper;

import java.util.concurrent.TimeoutException;

public class ApplicationRunner {

  private static final String XMPP_HOSTNAME = "localhost";
  private static final String SNIPER_PASSWORD = "sniper";
  public static final String SNIPER_ID = "sniper";
  private AuctionSniperDriver driver;
  private Application application;
  private String itemId;

  public void startBiddingIn(final FakeAuctionServer auction) throws Exception {
    itemId = auction.getItemId();
    FxToolkit.registerPrimaryStage();
    application = FxToolkit.setupApplication(
      Main.class,
      XMPP_HOSTNAME,
      SNIPER_ID,
      SNIPER_PASSWORD,
      auction.getItemId()
    );
    driver = AuctionSniperDriver.newInstance();
    driver.hasColumnTitles();
    driver.showsSniperStatus(itemId, 0, 0, Sniper.STATUS_JOINING);
  }

  public void stop() throws TimeoutException {
    if (application != null) FxToolkit.cleanupApplication(application);
  }

  public void showsSniperHasLostAuction(int lastPrice, int lastBid) {
    driver.showsSniperStatus(itemId, lastPrice, lastBid, Sniper.STATUS_LOST);
  }

  public void hasShownSniperIsBidding(int lastPrice, int lastBid) {
    driver.showsSniperStatus(itemId, lastPrice, lastBid, Sniper.STATUS_BIDDING);
  }

  public void hasShownSniperIsWining(int winningBid) {
    driver.showsSniperStatus(itemId, winningBid, winningBid, Sniper.STATUS_WINNING);
  }

  public void showsSniperHasWonAuction(int lastPrice) {
    driver.showsSniperStatus(itemId, lastPrice, lastPrice, Sniper.STATUS_WON);
  }
}
