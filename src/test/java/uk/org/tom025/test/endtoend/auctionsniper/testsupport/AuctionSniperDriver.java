package uk.org.tom025.test.endtoend.auctionsniper.testsupport;

import javafx.stage.Stage;

import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;
import static org.testfx.matcher.base.NodeMatchers.isNotNull;

public class AuctionSniperDriver {

  AuctionSniperDriver() {
  }

  public static AuctionSniperDriver newInstance() throws TimeoutException {
    return new AuctionSniperDriver();
  }

  public void showsSniperStatus(String status) {
    verifyThat("#status", isNotNull());
    verifyThat("#status", hasText(status));
  }

  public void dispose() {
//    throw new UnsupportedOperationException("not implemented yet");
  }
}
