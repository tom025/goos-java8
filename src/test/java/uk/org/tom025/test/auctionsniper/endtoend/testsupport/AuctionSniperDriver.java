package uk.org.tom025.test.auctionsniper.endtoend.testsupport;

import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;
import static org.testfx.matcher.base.NodeMatchers.isNotNull;
import static org.testfx.matcher.control.TableViewMatchers.hasTableCell;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class AuctionSniperDriver {

  AuctionSniperDriver() {
  }

  public static AuctionSniperDriver newInstance() throws TimeoutException {
    return new AuctionSniperDriver();
  }

  public void showsSniperStatus(String status) {
    waitForFxEvents();
    verifyThat("#snipers", hasTableCell(status));
  }
}
