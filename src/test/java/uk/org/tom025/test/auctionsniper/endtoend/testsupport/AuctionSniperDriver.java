package uk.org.tom025.test.auctionsniper.endtoend.testsupport;

import javafx.scene.control.Labeled;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.testfx.api.FxAssert;
import org.testfx.service.finder.NodeFinder;
import uk.org.tom025.auctionsniper.ui.model.Sniper;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertThat;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
import static uk.org.tom025.test.auctionsniper.endtoend.testsupport.AuctionSniperDriver.TableCellMatcher.aCellInTheColumn;
import static uk.org.tom025.test.auctionsniper.endtoend.testsupport.AuctionSniperDriver.TableMatcher.isATableWith;
import static uk.org.tom025.test.auctionsniper.endtoend.testsupport.AuctionSniperDriver.TableRowMatcher.aRowThatHas;

public class AuctionSniperDriver {

  AuctionSniperDriver() {
  }

  public static AuctionSniperDriver newInstance() throws TimeoutException {
    return new AuctionSniperDriver();
  }

  public void showsSniperStatus(String itemId, int lastPrice, int lastBid, String status) {
    waitForFxEvents();
    final NodeFinder finder = FxAssert.assertContext().getNodeFinder();

    TableView<Sniper> table = finder.lookup("#snipers").queryFirst();
    assertThat(table, isATableWith(
      aRowThatHas(
        aCellInTheColumn("itemIdColumn").withTheValue(itemId),
        aCellInTheColumn("statusColumn").withTheValue(status),
        aCellInTheColumn("lastPriceColumn").withTheValue(lastPrice))));
  }

  public static class TableMatcher extends TypeSafeDiagnosingMatcher<TableView<Sniper>> {
    private final Matcher<TableRow<Sniper>> tableRowMatcher;

    private TableMatcher(Matcher<TableRow<Sniper>> tableRowMatcher) {
      this.tableRowMatcher = tableRowMatcher;
    }

    public static Matcher<TableView<Sniper>> isATableWith(Matcher<TableRow<Sniper>> tableRowMatcher) {
      return new TableMatcher(tableRowMatcher);
    }

    @Override
    public void describeTo(Description description) {
      description
        .appendText("is a table with ")
        .appendDescriptionOf(tableRowMatcher);
    }

    @Override
    protected boolean matchesSafely(TableView<Sniper> table, Description mismatchDescription) {
      NodeFinder nodeFinder = FxAssert.assertContext().getNodeFinder();

      mismatchDescription.appendText("was the table:\n");
      Set<TableRow<Sniper>> tableRows = nodeFinder.from(table).lookup(".table-row-cell").queryAll();
      tableRows
        .forEach(row -> {
          Set<TableCell<Sniper, Object>> cells = nodeFinder.from(row).lookup(".table-cell").queryAll();
          List<String> cellValues = cells
            .stream()
            .map(Labeled::getText)
            .collect(toList());
          if (cellValues.stream().anyMatch(value -> value != null)) {
            mismatchDescription.appendValueList("", ", ", "\n", cellValues);
          }
        });

      return tableRows.stream().anyMatch(tableRowMatcher::matches);
    }
  }

  public static class TableRowMatcher extends TypeSafeDiagnosingMatcher<TableRow<Sniper>> {
    private final List<Matcher<TableCell<Sniper, Object>>> matchers;

    private TableRowMatcher(List<Matcher<TableCell<Sniper, Object>>> matchers) {
      this.matchers = matchers;
    }

    @SafeVarargs
    public static Matcher<TableRow<Sniper>> aRowThatHas(Matcher<TableCell<Sniper, Object>>... matchers) {
      return new TableRowMatcher(asList(matchers));
    }

    @Override
    public void describeTo(Description description) {
      description
        .appendText("a row that has ")
        .appendList("", ", ", "", matchers);
    }

    @Override
    protected boolean matchesSafely(TableRow<Sniper> row, Description mismatchDescription) {
      NodeFinder finder = FxAssert.assertContext().getNodeFinder();
      Set<TableCell<Sniper, Object>> cells = finder.from(row).lookup(".table-cell").queryAll();
      return matchers.stream().allMatch(matcher ->
        cells.stream().anyMatch(matcher::matches)
      );
    }
  }

  public static class TableCellMatcher<T> extends TypeSafeDiagnosingMatcher<TableCell<Sniper, Object>> {
    private final String expectedColumnId;
    private final T expectedValue;

    private TableCellMatcher(String expectedColumnId, T expectedValue) {
      this.expectedColumnId = expectedColumnId;
      this.expectedValue = expectedValue;
    }

    public static TableCellMatcher.Builder aCellInTheColumn(String columnName) {
      return new TableCellMatcher.Builder(columnName);
    }

    @Override
    public void describeTo(Description description) {
      description
        .appendText("a cell in the column ")
        .appendValue(expectedColumnId)
        .appendText(" with the value ")
        .appendValue(expectedValue);
    }

    @Override
    protected boolean matchesSafely(TableCell<Sniper, Object> cell, Description mismatchDescription) {
      String actualColumnId = cell.getTableColumn().getId();
      String actualValue = cell.getText();
      return expectedColumnId.equals(actualColumnId)
        && expectedValue.toString().equals(actualValue);
    }

    public static class Builder {
      private final String columnName;

      public Builder(String columnName) {
        this.columnName = columnName;
      }

      public <T> Matcher<TableCell<Sniper, Object>> withTheValue(T value) {
        return new TableCellMatcher<>(columnName, value);
      }
    }
  }
}
