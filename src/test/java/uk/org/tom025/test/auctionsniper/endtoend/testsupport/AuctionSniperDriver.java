package uk.org.tom025.test.auctionsniper.endtoend.testsupport;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import org.testfx.api.FxAssert;
import org.testfx.service.finder.NodeFinder;
import uk.org.tom025.auctionsniper.ui.model.Sniper;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
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

    final TableRow<Sniper> sniperRow = finder.lookup("#snipers .table-row-cell")
      .select(row -> {
        final Set<TableCell> cells = finder.from(row).lookup(".table-cell").queryAll();
        return cells.stream().anyMatch(cell ->
          Objects.equals(cell.getTableColumn().getId(), "itemIdColumn")
            && Objects.equals(cell.getText(), itemId));
      })
      .queryFirst();

    final Set<TableCell> rowCells = finder.from(sniperRow).lookup(".table-cell").queryAll();
    final String actualStatus = rowCells.stream().filter(cell ->
        Objects.equals(cell.getTableColumn().getId(), "statusColumn")
    ).findFirst().get().getText();
    assertThat(actualStatus, equalTo(status));

    final String actualLastPrice = rowCells.stream().filter(cell ->
        Objects.equals(cell.getTableColumn().getId(), "lastPriceColumn")
    ).findFirst().get().getText();
    assertThat(actualLastPrice, equalTo(String.valueOf(lastPrice)));
  }
}
