package uk.org.tom025.test.endtoend.auctionsniper.testsupport;

import auctionsniper.Main;
import javafx.application.Application;
import org.testfx.api.FxToolkit;

import java.util.concurrent.TimeoutException;

public class ApplicationRunner {

  private static final String XMPP_HOSTNAME = "localhost";
  private static final String SNIPER_PASSWORD = "sniper";
  private static final String SNIPER_ID = "sniper";
  private static final String STATUS_JOINING = "joining";
  private static final String STATUS_LOST = "lost";
  private AuctionSniperDriver driver;
  private Application application;

  public void startBiddingIn(final FakeAuctionServer auction) throws Exception {
    FxToolkit.registerPrimaryStage();
    application = FxToolkit.setupApplication(
      Main.class,
      XMPP_HOSTNAME,
      SNIPER_ID,
      SNIPER_PASSWORD,
      auction.getItemId()
    );
    driver = AuctionSniperDriver.newInstance();
    driver.showsSniperStatus(STATUS_JOINING);
  }

  public void stop() throws TimeoutException {
    if (application != null) FxToolkit.cleanupApplication(application);
  }

  public void showsSniperHasLostAuction() {
    driver.showsSniperStatus(STATUS_LOST);
  }
}
