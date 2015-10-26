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
    private final List<TableViewMatcher> tableMatchers;

    private TableMatcher(List<TableViewMatcher> tableMatchers) {
      this.tableMatchers = tableMatchers;
    }

    public static Matcher<TableView<Object>> isATable(List<TableViewMatcher> tableMatchers) {
      return new TableMatcher(tableMatchers);
    }

    public static List<TableViewMatcher> with(TableViewMatcher... tableMatchers) {
      return asList(tableMatchers);
    }

    @Override
    public void describeTo(Description description) {
      description.appendText("is a table with ")
        .appendList("", ", ", "", tableMatchers);
    }

    @Override
    protected boolean matchesSafely(TableView<Object> table, Description mismatchDescription) {
      NodeFinder nodeFinder = FxAssert.assertContext().getNodeFinder();

      mismatchDescription.appendText("was the table:\n");
      List<String> columnNames = table.getColumns()
        .stream()
        .map(TableColumnBase::getText)
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

      return tableMatchers.stream().allMatch(tableMatcher ->
          tableMatcher.matches(table)
      );
    }
  }

  public static class TableRowMatcher extends TableViewMatcher {
    private final List<Matcher<TableCell<Object, Object>>> matchers;

    private TableRowMatcher(List<Matcher<TableCell<Object, Object>>> matchers) {
      this.matchers = matchers;
    }

    @SafeVarargs
    public static TableRowMatcher aRowThatHas(Matcher<TableCell<Object, Object>>... matchers) {
      return new TableRowMatcher(asList(matchers));
    }

    @Override
    public void describeTo(Description description) {
      description
        .appendText("a row that has ")
        .appendList("", ", ", "", matchers);
    }

    @Override
    protected boolean matchesSafely(TableView<Object> table, Description mismatchDescription) {
      NodeFinder nodeFinder = FxAssert.assertContext().getNodeFinder();
      Set<TableRow<Object>> tableRows = nodeFinder.from(table).lookup(".table-row-cell").queryAll();
      return tableRows.stream().anyMatch((row) -> {
        Set<TableCell<Object, Object>> cells = nodeFinder.from(row).lookup(".table-cell").queryAll();
        return matchers.stream().allMatch(matcher ->
            cells.stream().anyMatch(matcher::matches)
        );
      });
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

  public static class TableColumnTitlesMatcher extends TableViewMatcher {

    private final List<String> expectedTitles;

    public static TableColumnTitlesMatcher columnTitles(String... titles) {
      return new TableColumnTitlesMatcher(asList(titles));
    }

    public TableColumnTitlesMatcher(List<String> expectedTitles) {
      this.expectedTitles = expectedTitles;
    }

    @Override
    protected boolean matchesSafely(
      TableView<Object> table,
      Description mismatchDescription
    ) {
      List<String> columnTitles = table.getColumns().stream()
        .map(TableColumnBase::getText)
        .collect(toList());
      mismatchDescription.appendValueList(
        "",
        ", ",
        "",
        columnTitles
      );
      return columnTitles.containsAll(expectedTitles);
    }

    @Override
    public void describeTo(Description description) {
      description
        .appendText("columns with titles ")
        .appendValueList("", ", ", "", expectedTitles);
    }
  }

  public static abstract class TableViewMatcher extends TypeSafeDiagnosingMatcher<TableView<Object>> {
  }
}
