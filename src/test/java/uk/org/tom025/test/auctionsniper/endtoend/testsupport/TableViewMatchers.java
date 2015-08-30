package uk.org.tom025.test.auctionsniper.endtoend.testsupport;

import javafx.scene.control.*;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.testfx.api.FxAssert;
import org.testfx.service.finder.NodeFinder;

import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class TableViewMatchers {
  public static class TableMatcher extends TypeSafeDiagnosingMatcher<TableView<Object>> {
    private final Matcher<TableRow<Object>> tableRowMatcher;

    private TableMatcher(Matcher<TableRow<Object>> tableRowMatcher) {
      this.tableRowMatcher = tableRowMatcher;
    }

    public static Matcher<TableView<Object>> isATableWith(Matcher<TableRow<Object>> tableRowMatcher) {
      return new TableMatcher(tableRowMatcher);
    }

    @Override
    public void describeTo(Description description) {
      description
        .appendText("is a table with ")
        .appendDescriptionOf(tableRowMatcher);
    }

    @Override
    protected boolean matchesSafely(TableView<Object> table, Description mismatchDescription) {
      NodeFinder nodeFinder = FxAssert.assertContext().getNodeFinder();

      mismatchDescription.appendText("was the table:\n");
      List<String> columnNames = table.getColumns()
        .stream()
        .map(TableColumnBase::getId)
        .collect(toList());
      mismatchDescription.appendValueList("", ", ", "\n", columnNames);

      Set<TableRow<Object>> tableRows = nodeFinder.from(table).lookup(".table-row-cell").queryAll();
      tableRows
        .forEach(row -> {
          Set<TableCell<Object, Object>> cells = nodeFinder.from(row).lookup(".table-cell").queryAll();
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

  public static class TableRowMatcher extends TypeSafeDiagnosingMatcher<TableRow<Object>> {
    private final List<Matcher<TableCell<Object, Object>>> matchers;

    private TableRowMatcher(List<Matcher<TableCell<Object, Object>>> matchers) {
      this.matchers = matchers;
    }

    @SafeVarargs
    public static Matcher<TableRow<Object>> aRowThatHas(Matcher<TableCell<Object, Object>>... matchers) {
      return new TableRowMatcher(asList(matchers));
    }

    @Override
    public void describeTo(Description description) {
      description
        .appendText("a row that has ")
        .appendList("", ", ", "", matchers);
    }

    @Override
    protected boolean matchesSafely(TableRow<Object> row, Description mismatchDescription) {
      NodeFinder finder = FxAssert.assertContext().getNodeFinder();
      Set<TableCell<Object, Object>> cells = finder.from(row).lookup(".table-cell").queryAll();
      return matchers.stream().allMatch(matcher ->
        cells.stream().anyMatch(matcher::matches)
      );
    }
  }

  public static class TableCellMatcher<T> extends TypeSafeDiagnosingMatcher<TableCell<Object, Object>> {
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
    protected boolean matchesSafely(TableCell<Object, Object> cell, Description mismatchDescription) {
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

      public <T> Matcher<TableCell<Object, Object>> withTheValue(T value) {
        return new TableCellMatcher<>(columnName, value);
      }
    }
  }
}
