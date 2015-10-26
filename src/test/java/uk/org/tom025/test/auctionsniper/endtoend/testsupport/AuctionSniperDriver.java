package uk.org.tom025.test.auctionsniper.endtoend.testsupport;

import javafx.scene.control.TableView;
import org.testfx.api.FxAssert;
import org.testfx.service.finder.NodeFinder;

import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertThat;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
import static uk.org.tom025.test.auctionsniper.endtoend.testsupport.TableViewMatchers.TableCellMatcher.aCellInTheColumn;
import static uk.org.tom025.test.auctionsniper.endtoend.testsupport.TableViewMatchers.TableColumnTitlesMatcher.columnTitles;
import static uk.org.tom025.test.auctionsniper.endtoend.testsupport.TableViewMatchers.TableMatcher.isATable;
import static uk.org.tom025.test.auctionsniper.endtoend.testsupport.TableViewMatchers.TableMatcher.with;
import static uk.org.tom025.test.auctionsniper.endtoend.testsupport.TableViewMatchers.TableRowMatcher.aRowThatHas;

public class AuctionSniperDriver {
  private AuctionSniperDriver() { }

  public static AuctionSniperDriver newInstance() throws TimeoutException {
    return new AuctionSniperDriver();
  }

  public void showsSniperStatus(String itemId, int lastPrice, int lastBid, String status) {
    waitForFxEvents();
    final NodeFinder finder = FxAssert.assertContext().getNodeFinder();
    TableView<Object> table = finder.lookup("#snipers").queryFirst();

    assertThat(table, isATable(with(
        aRowThatHas(
          aCellInTheColumn("itemIdColumn").withTheValue(itemId),
          aCellInTheColumn("statusColumn").withTheValue(status),
          aCellInTheColumn("lastPriceColumn").withTheValue(lastPrice),
          aCellInTheColumn("lastBidColumn").withTheValue(lastBid)
        )
      )
    ));
  }

  public void hasColumnTitles() {
    waitForFxEvents();
    final NodeFinder finder = FxAssert.assertContext().getNodeFinder();
    TableView<Object> table = finder.lookup("#snipers").queryFirst();

    assertThat(table, isATable(with(
      columnTitles("Item", "Last Price", "Last Bid", "State")
    )));
  }

}
