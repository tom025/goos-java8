package uk.org.tom025.test.auctionsniper.endtoend.testsupport;

import uk.org.tom025.auctionsniper.Main;
import javafx.application.Application;
import org.testfx.api.FxToolkit;
import uk.org.tom025.auctionsniper.ui.controller.MainController;

import java.util.concurrent.TimeoutException;

import static uk.org.tom025.auctionsniper.ui.controller.MainController.*;

public class ApplicationRunner {

  private static final String XMPP_HOSTNAME = "localhost";
  private static final String SNIPER_PASSWORD = "sniper";
  public static final String SNIPER_ID = "sniper";
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

  public void hasShownSniperIsBidding() {
    driver.showsSniperStatus(STATUS_BIDDING);
  }
}
