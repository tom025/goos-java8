package uk.org.tom025.test.auctionsniper.endtoend.testsupport;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.testfx.api.FxAssert;
import org.testfx.service.finder.NodeFinder;
import uk.org.tom025.auctionsniper.ui.controller.MainController;

import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.hasTableCell;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class AuctionSniperDriver {

  AuctionSniperDriver() {
  }

  public static AuctionSniperDriver newInstance() throws TimeoutException {
    return new AuctionSniperDriver();
  }

  public void showsSniperStatus(String itemId, int lastPrice, int lastBid, String status) {
    waitForFxEvents();
    final NodeFinder finder = FxAssert.assertContext().getNodeFinder();
    final TableView<MainController.Sniper> sniperTable = finder.lookup("#snipers").queryFirst();
    verifyThat(sniperTable, hasTableCell(itemId));

    final ObservableList<MainController.Sniper> snipers = sniperTable.getItems();
    final MainController.Sniper sniper = snipers.stream()
      .filter(sn -> sn.itemId.get().equals(itemId))
      .findFirst()
      .get();


    assertThat(sniper, notNullValue());
    assertThat(sniper.status.get(), equalTo(status));
  }
}
