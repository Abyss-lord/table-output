/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.tyy.output.table;

import static com.tyy.output.table.Constant.DATA_LINE_COLUMN_SEPARATOR_IDX;
import static com.tyy.output.table.Constant.DATA_LINE_LEFT_IDX;
import static com.tyy.output.table.Constant.DATA_LINE_RIGHT_IDX;
import static com.tyy.output.table.Constant.DATA_ROW_BORDER_COLUMN_SEPARATOR_IDX;
import static com.tyy.output.table.Constant.DATA_ROW_BORDER_LEFT_IDX;
import static com.tyy.output.table.Constant.DATA_ROW_BORDER_MIDDLE_IDX;
import static com.tyy.output.table.Constant.DATA_ROW_BORDER_RIGHT_IDX;
import static com.tyy.output.table.Constant.ELLIPSIS;
import static com.tyy.output.table.Constant.HEADER_BOTTOM_BORDER_COLUMN_SEPARATOR_IDX;
import static com.tyy.output.table.Constant.HEADER_BOTTOM_BORDER_LEFT_IDX;
import static com.tyy.output.table.Constant.HEADER_BOTTOM_BORDER_MIDDLE_IDX;
import static com.tyy.output.table.Constant.HEADER_BOTTOM_BORDER_RIGHT_IDX;
import static com.tyy.output.table.Constant.TABLE_BOTTOM_BORDER_COLUMN_SEPARATOR_IDX;
import static com.tyy.output.table.Constant.TABLE_BOTTOM_BORDER_LEFT_IDX;
import static com.tyy.output.table.Constant.TABLE_BOTTOM_BORDER_MIDDLE_IDX;
import static com.tyy.output.table.Constant.TABLE_BOTTOM_BORDER_RIGHT_IDX;
import static com.tyy.output.table.Constant.TABLE_UPPER_BORDER_COLUMN_SEPARATOR_IDX;
import static com.tyy.output.table.Constant.TABLE_UPPER_BORDER_LEFT_IDX;
import static com.tyy.output.table.Constant.TABLE_UPPER_BORDER_MIDDLE_IDX;
import static com.tyy.output.table.Constant.TABLE_UPPER_BORDER_RIGHT_IDX;
import static com.tyy.output.table.Constant.TABLE_UPPER_BORDER_WITH_TITLE_COLUMN_SEPARATOR_IDX;
import static com.tyy.output.table.Constant.TITLE_LINE_BORDER_COLUMN_SEPARATOR_IDX;
import static com.tyy.output.table.Constant.TITLE_LINE_BORDER_LEFT_IDX;
import static com.tyy.output.table.Constant.TITLE_LINE_BORDER_MIDDLE_IDX;
import static com.tyy.output.table.Constant.TITLE_LINE_BORDER_RIGHT_IDX;
import static com.tyy.output.table.Constant.TITLE_LINE_ROW_BORDER_COLUMN_SEPARATOR_IDX;
import static com.tyy.output.table.Constant.TITLE_LINE_ROW_BORDER_LEFT_IDX;
import static com.tyy.output.table.Constant.TITLE_LINE_ROW_BORDER_MIDDLE_IDX;
import static com.tyy.output.table.Constant.TITLE_LINE_ROW_BORDER_RIGHT_IDX;
import static com.tyy.output.table.Constant.UNLIMITED;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.tyy.output.table.utils.LineUtil;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.Getter;

/** 表格渲染类。 支持设置标题、列、边框样式、行号等，并提供灵活的格式化输出功能。 */
public class Table {
  public static final int PADDING = 1;
  private List<String> titleLines;
  private List<Column> columns;
  private String[] footer;
  private String[] header;
  private int titleWidth;
  @Getter private final int limit;
  @Getter private boolean rowNumbersEnabled;
  @Getter private OverflowBehaviour overflowBehaviour;
  private BorderStyle borderStyle;
  private int rowCount;

  private Table(Builder builder) {
    this.borderStyle = builder.borderStyle;
    this.limit = builder.limit;
    this.titleLines = builder.titleLines;
    this.columns = builder.columns;
    this.rowNumbersEnabled = builder.rowNumbersEnabled;
    this.overflowBehaviour = builder.overflowBehaviour;
  }

  static final class Builder {
    private boolean rowNumbersEnabled;
    private BorderStyle borderStyle;

    private int limit;
    private OverflowBehaviour overflowBehaviour;
    private List<String> titleLines;
    private List<Column> columns;

    /** Builder 模式构造器类。 用于配置和创建 Table 实例。 */
    public Builder() {
      this.rowNumbersEnabled = false;
      this.borderStyle = BorderStyle.BASIC2;
      this.limit = UNLIMITED;
      this.overflowBehaviour = OverflowBehaviour.CLIP_RIGHT;

      this.titleLines = Lists.newArrayList();
      this.columns = Lists.newArrayList();
    }

    public Builder withRowNumbersEnabled(boolean rowNumbersEnabled) {
      this.rowNumbersEnabled = rowNumbersEnabled;
      return this;
    }

    public Builder withBorderStyle(BorderStyle borderStyle) {
      this.borderStyle = borderStyle;
      return this;
    }

    public Builder withBorderStyle(ImmutableList<Character> characters, boolean showRowBoundaries) {
      this.borderStyle = new BorderStyle(characters, showRowBoundaries);
      return this;
    }

    public Builder withBorderStyle(Character[] characters, boolean showRowBoundaries) {
      this.borderStyle = new BorderStyle(characters, showRowBoundaries);
      return this;
    }

    public Builder withBorderStyle(List<Character> characters, boolean showRowBoundaries) {
      this.borderStyle = new BorderStyle(ImmutableList.copyOf(characters), showRowBoundaries);
      return this;
    }

    public Builder withColumn(Column column) {
      this.columns.add(column);
      return this;
    }

    public Builder withLimit(int limit) {
      this.limit = limit;
      return this;
    }

    public Builder withOverflowBehaviour(OverflowBehaviour overflowBehaviour) {
      this.overflowBehaviour = overflowBehaviour;
      return this;
    }

    public Builder withColumns(Column... columns) {
      this.columns.addAll(Arrays.asList(columns));
      return this;
    }

    public Builder withTitle(String titleLine) {
      this.titleLines.add(titleLine);
      return this;
    }

    public Table build() {
      return new Table(this);
    }
  }

  /**
   * 将消息输出到指定的输出流。
   *
   * @param message 要输出的消息
   * @param os 输出流
   * @throws IllegalArgumentException 当message或os为null时抛出
   * @throws UncheckedIOException 当写入输出流失败时抛出
   */
  public static void output(String message, OutputStream os) {
    if (message == null || os == null) {
      throw new IllegalArgumentException("Message and OutputStream cannot be null");
    }
    boolean isSystemStream = (os == System.out || os == System.err);

    try {
      PrintStream printStream =
          new PrintStream(
              isSystemStream ? os : new BufferedOutputStream(os),
              true,
              StandardCharsets.UTF_8.name());

      try {
        printStream.println(message);
        printStream.flush();
      } finally {
        if (!isSystemStream) {
          printStream.close();
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to write message to output stream", e);
    }
  }

  /** 输出表格到标准输出。 */
  public void printInfo() {
    String stringFormat = getStringFormat();
    output(stringFormat, System.out);
  }

  /**
   * 输出表格到指定输出流。
   *
   * @param os 输出流
   */
  public void print(OutputStream os) {
    String stringFormat = getStringFormat();
    output(stringFormat, os);
  }

  /**
   * 添加标题行。 标题文本会被自动转换为大写。
   *
   * @param header 标题文本
   * @throws IllegalArgumentException 当header为null时抛出
   */
  public void addTitleLine(String header) {
    Preconditions.checkArgument(header != null, "Header cannot be null");
    titleLines.add(header.toUpperCase(Locale.ENGLISH));
  }

  public void addHeader(String... headers) {
    Preconditions.checkArgument(headers != null, "Headers cannot be null");
    for (String header : headers) {
      Column column = new Column.Builder().withHeader(header).build();
      columns.add(column);
    }
  }

  public void addColumns(Column... columns) {
    Preconditions.checkArgument(columns != null, "Columns cannot be null");
    this.columns.addAll(Arrays.asList(columns));
  }

  public void addData(String[][] data) {
    Preconditions.checkArgument(
        data.length == columns.size(), "Data size does not match number of columns");
    for (String[] row : data) {
      for (int i = 0; i < row.length; i++) {
        getColumn(i).addCell(row[i]);
      }
    }
  }

  public void addData(String column, String data) {
    Column col = getColumn(column);
    if (col == null) {
      throw new IllegalArgumentException("Column " + column + " does not exist");
    }

    col.addCell(data);
  }

  public List<String> getTitleLines() {
    return ImmutableList.copyOf(titleLines);
  }

  private Column getColumn(int index) {
    return columns.get(index);
  }

  private Column getColumn(String name) {
    for (Column column : columns) {
      if (column.getHeader().equalsIgnoreCase(name)) {
        return column;
      }
    }
    return null;
  }

  /**
   * 生成表格的字符串格式。 这个方法处理所有的格式化逻辑，包括： 1. 行数限制处理 2. 行号添加（如果启用） 3. 标题宽度处理 4. 如果有行标题存在，则重新计算列宽度
   *
   * @return 格式化后的表格字符串
   * @throws IllegalArgumentException 当表格配置无效时抛出
   */
  public String getStringFormat() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    List<Character> borders = borderStyle.getCharacters();

    checkColumns();

    // 1. 处理 limit
    if (getLimit() != UNLIMITED) {
      columns =
          columns.stream().map(c -> c.getLimitedColumn(getLimit())).collect(Collectors.toList());
    }

    // 2. 计算 rowCount
    rowCount = columns.get(0).getCellCount();
    if (rowCount == 0) {
      columns.stream().forEach(c -> c.addCell(""));
      rowCount = 1;
    }

    // 3. 处理 row numbers
    if (isRowNumbersEnabled()) {
      Column rowNumberColumn =
          new Column.Builder().withHeader("").withDataAlign(HorizontalAlign.CENTER).build();
      for (int i = 0; i < rowCount; i++) {
        rowNumberColumn.addCell(String.valueOf(i + 1));
      }
      this.columns.add(0, rowNumberColumn);
    }

    // 4. 计算 titleWidth
    if (!titleLines.isEmpty()) {
      processColumnsWidth();
    }

    Column[] columns = this.columns.stream().filter(Column::isVisible).toArray(Column[]::new);
    header = Arrays.stream(columns).map(Column::getHeader).toArray(String[]::new);
    footer = Arrays.stream(columns).map(Column::getFooter).toArray(String[]::new);

    try (OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {
      writeUpperBorder(osw, borders, System.lineSeparator(), columns, titleLines.isEmpty());
      writeTitleLines(osw, borders, System.lineSeparator(), columns, overflowBehaviour);

      writeHeader(osw, borders, System.lineSeparator(), columns, overflowBehaviour);
      writeHeaderBorder(osw, borders, System.lineSeparator(), columns);
      if (rowCount > 0) {
        writeData(osw, borderStyle, columns, System.lineSeparator(), overflowBehaviour);
      }

      if (!LineUtil.isAllEmpty(footer)) {
        writeRowSeparator(osw, borderStyle, System.lineSeparator(), columns);
        writeFooter(osw, borders, columns, overflowBehaviour);
      }

      writeBottomBorder(osw, borders, System.lineSeparator(), columns);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new String(baos.toByteArray(), StandardCharsets.UTF_8);
  }

  private void checkColumns() {
    Preconditions.checkArgument(!columns.isEmpty(), "No columns added");
    int rowCount = columns.get(0).getCellCount();
    for (Column column : columns) {
      if (column.getCellCount() != rowCount) {
        throw new IllegalArgumentException("All columns must have the same number of rows");
      }
    }
  }

  private void writeTitleLines(
      OutputStreamWriter writer,
      List<Character> borders,
      String lineSeparator,
      Column[] columns,
      OverflowBehaviour overflowBehaviour)
      throws IOException {

    for (int i = 0; i < titleLines.size(); i++) {
      writeTitleLine(
          writer,
          titleLines.get(i),
          borders.get(DATA_LINE_LEFT_IDX),
          borders.get(DATA_LINE_RIGHT_IDX),
          lineSeparator,
          overflowBehaviour);

      if (i == titleLines.size() - 1) {
        writeTitleBottomBorder(writer, borderStyle, System.lineSeparator(), columns);
      } else {
        writeTitleRowSeparator(writer, borderStyle, System.lineSeparator(), columns);
      }
    }
  }

  private void processColumnsWidth() {
    List<Column> visibleColumns =
        columns.stream().filter(Column::isVisible).collect(Collectors.toList());

    int totalPaddingWidth = (visibleColumns.size() - 1) * (1 + 2 * PADDING);
    int totalColumnsWidth = visibleColumns.stream().mapToInt(Column::getMaxWidth).sum();

    int currentTotalWidth = totalPaddingWidth + totalColumnsWidth;
    int maxTitleWidth = titleLines.stream().mapToInt(LineUtil::getDisplayWidth).max().orElse(0);

    titleWidth = Math.max(maxTitleWidth, currentTotalWidth);

    if (maxTitleWidth > currentTotalWidth) {
      distributeExtraWidth(visibleColumns, maxTitleWidth - currentTotalWidth);
    }
  }

  private void distributeExtraWidth(List<Column> columns, int extraWidth) {
    int columnCount = columns.size();
    if (columnCount == 0) return;

    int widthPerColumn = extraWidth / columnCount;
    int allocatedWidth = 0;

    for (int i = 0; i < columnCount - 1; i++) {
      Column column = columns.get(i);
      column.setMaxWidth(column.getMaxWidth() + widthPerColumn);
      allocatedWidth += widthPerColumn;
    }

    Column lastColumn = columns.get(columnCount - 1);
    int remainingWidth = extraWidth - allocatedWidth;
    lastColumn.setMaxWidth(lastColumn.getMaxWidth() + remainingWidth);
  }

  private void writeTitleLine(
      OutputStreamWriter osw,
      String header,
      Character left,
      Character right,
      String lineSeparator,
      OverflowBehaviour overflowBehaviour)
      throws IOException {
    if (left != null) {
      osw.write(left);
    }

    writeJustified(osw, header, HorizontalAlign.CENTER, titleWidth, PADDING, overflowBehaviour);

    if (right != null) {
      osw.write(right);
    }

    if (lineSeparator != null) {
      osw.write(lineSeparator);
    }
  }

  private void writeHeaderBorder(
      OutputStreamWriter writer, List<Character> borders, String lineSeparator, Column[] columns)
      throws IOException {
    writeHorizontalLine(
        writer,
        borders.get(HEADER_BOTTOM_BORDER_LEFT_IDX),
        borders.get(HEADER_BOTTOM_BORDER_MIDDLE_IDX),
        borders.get(HEADER_BOTTOM_BORDER_COLUMN_SEPARATOR_IDX),
        borders.get(HEADER_BOTTOM_BORDER_RIGHT_IDX),
        lineSeparator,
        columns);
  }

  private void writeData(
      OutputStreamWriter writer,
      BorderStyle borderStyle,
      Column[] columns,
      String lineSeparator,
      OverflowBehaviour overflowBehaviour)
      throws IOException {
    List<Character> borders = borderStyle.getCharacters();
    HorizontalAlign[] dataAligns =
        Arrays.stream(columns).map(Column::getDataAlign).toArray(HorizontalAlign[]::new);

    for (int i = 0; i < rowCount; i++) {
      String[] data = getData(columns, i);
      writeRow(
          writer,
          borders.get(DATA_LINE_LEFT_IDX),
          borders.get(DATA_LINE_COLUMN_SEPARATOR_IDX),
          borders.get(DATA_LINE_RIGHT_IDX),
          data,
          columns,
          dataAligns,
          lineSeparator,
          overflowBehaviour);

      if (i < rowCount - 1 && borderStyle.isRowBoundariesEnabled()) {
        writeRowSeparator(writer, borderStyle, lineSeparator, columns);
      }
    }
  }

  private static void writeUpperBorder(
      OutputStreamWriter writer,
      List<Character> borders,
      String lineSeparator,
      Column[] columns,
      boolean isTitleLinesEmpty)
      throws IOException {
    if (isTitleLinesEmpty) {
      writeHorizontalLine(
          writer,
          borders.get(TABLE_UPPER_BORDER_LEFT_IDX),
          borders.get(TABLE_UPPER_BORDER_MIDDLE_IDX),
          borders.get(TABLE_UPPER_BORDER_COLUMN_SEPARATOR_IDX),
          borders.get(TABLE_UPPER_BORDER_RIGHT_IDX),
          lineSeparator,
          columns);
    } else {
      writeHorizontalLine(
          writer,
          borders.get(TABLE_UPPER_BORDER_LEFT_IDX),
          borders.get(TABLE_UPPER_BORDER_MIDDLE_IDX),
          borders.get(TABLE_UPPER_BORDER_WITH_TITLE_COLUMN_SEPARATOR_IDX),
          borders.get(TABLE_UPPER_BORDER_RIGHT_IDX),
          lineSeparator,
          columns);
    }
  }

  private void writeHeader(
      OutputStreamWriter osw,
      List<Character> borders,
      String lineSeparator,
      Column[] columns,
      OverflowBehaviour overflowBehaviour)
      throws IOException {
    HorizontalAlign[] dataAligns =
        Arrays.stream(columns).map(Column::getHeaderAlign).toArray(HorizontalAlign[]::new);

    writeRow(
        osw,
        borders.get(4),
        borders.get(5),
        borders.get(6),
        header,
        columns,
        dataAligns,
        lineSeparator,
        overflowBehaviour);
  }

  private void writeTitleBottomBorder(
      OutputStreamWriter writer, BorderStyle borderStyle, String lineSeparator, Column[] columns)
      throws IOException {
    List<Character> borders = borderStyle.getCharacters();
    writeHorizontalLine(
        writer,
        borders.get(TITLE_LINE_BORDER_LEFT_IDX),
        borders.get(TITLE_LINE_BORDER_MIDDLE_IDX),
        borders.get(TITLE_LINE_BORDER_COLUMN_SEPARATOR_IDX),
        borders.get(TITLE_LINE_BORDER_RIGHT_IDX),
        lineSeparator,
        columns);
  }

  private void writeTitleRowSeparator(
      OutputStreamWriter writer, BorderStyle borderStyle, String lineSeparator, Column[] columns)
      throws IOException {
    List<Character> borders = borderStyle.getCharacters();
    writeHorizontalLine(
        writer,
        borders.get(TITLE_LINE_ROW_BORDER_LEFT_IDX),
        borders.get(TITLE_LINE_ROW_BORDER_MIDDLE_IDX),
        borders.get(TITLE_LINE_ROW_BORDER_COLUMN_SEPARATOR_IDX),
        borders.get(TITLE_LINE_ROW_BORDER_RIGHT_IDX),
        lineSeparator,
        columns);
  }

  private void writeRowSeparator(
      OutputStreamWriter writer, BorderStyle borderStyle, String lineSeparator, Column[] columns)
      throws IOException {
    List<Character> borders = borderStyle.getCharacters();
    writeHorizontalLine(
        writer,
        borders.get(DATA_ROW_BORDER_LEFT_IDX),
        borders.get(DATA_ROW_BORDER_MIDDLE_IDX),
        borders.get(DATA_ROW_BORDER_COLUMN_SEPARATOR_IDX),
        borders.get(DATA_ROW_BORDER_RIGHT_IDX),
        lineSeparator,
        columns);
  }

  /**
   * 写入水平分隔线。
   *
   * @param osw 输出流写入器
   * @param left 左边界字符
   * @param middle 填充字符
   * @param columnSeparator 列分隔符
   * @param right 右边界字符
   * @param lineSeparator 行分隔符
   * @param columns 列数组
   * @throws IOException 写入失败时抛出
   */
  private static void writeHorizontalLine(
      OutputStreamWriter osw,
      Character left,
      Character middle,
      Character columnSeparator,
      Character right,
      String lineSeparator,
      Column[] columns)
      throws IOException {

    Integer[] columnWidths =
        Arrays.stream(columns)
            .map(column -> column.getMaxWidth() + 2 * PADDING)
            .toArray(Integer[]::new);

    LineUtil.writeIfNotNull(osw, left);

    for (int i = 0; i < columnWidths.length; i++) {
      LineUtil.writeRepeated(osw, middle, columnWidths[i]);

      boolean isLastColumn = i == columnWidths.length - 1;
      if (!isLastColumn && columnSeparator != null) {
        osw.write(columnSeparator);
      }
    }

    LineUtil.writeIfNotNull(osw, right);
    LineUtil.writeIfNotNull(osw, lineSeparator);
  }

  /**
   * 写入对齐的文本内容。 处理文本的对齐方式和溢出行为，确保输出格式正确。
   *
   * @param osw 输出流写入器
   * @param str 要写入的字符串
   * @param align 对齐方式
   * @param maxLength 最大长度
   * @param minPadding 最小内边距
   * @param overflowBehaviour 溢出处理行为
   * @throws IOException 写入失败时抛出
   */
  private static void writeJustified(
      OutputStreamWriter osw,
      String str,
      HorizontalAlign align,
      int maxLength,
      int minPadding,
      OverflowBehaviour overflowBehaviour)
      throws IOException {

    osw.write(LineUtil.getSpaces(minPadding));

    int contentWidth = LineUtil.getDisplayWidth(str);

    if (contentWidth <= maxLength) {
      writeAlignedContent(osw, str, align, maxLength, contentWidth);
    } else {
      writeOverflowedString(osw, str, maxLength, overflowBehaviour);
    }

    osw.write(LineUtil.getSpaces(minPadding));
  }

  /** 写入对齐的内容，处理不同的对齐方式。 */
  private static void writeAlignedContent(
      OutputStreamWriter osw, String str, HorizontalAlign align, int maxLength, int contentWidth)
      throws IOException {

    if (contentWidth == maxLength) {
      osw.write(str);
      return;
    }
    // 1. 计算左侧填充宽度并写入
    // 2. 写入内容
    // 3. 计算右侧填充宽度并写入
    int leftPadding = calculateLeftPadding(align, maxLength, contentWidth);

    LineUtil.writeRepeated(osw, ' ', leftPadding);

    osw.write(str);

    int rightPadding = maxLength - contentWidth - leftPadding;
    LineUtil.writeRepeated(osw, ' ', rightPadding);
  }

  /** 根据对齐方式计算左侧填充宽度。 */
  private static int calculateLeftPadding(HorizontalAlign align, int maxLength, int contentWidth) {
    switch (align) {
      case LEFT:
        return 0;
      case CENTER:
        return (maxLength - contentWidth) / 2;
      case RIGHT:
        return maxLength - contentWidth;
      default:
        throw new IllegalArgumentException("不支持的对齐方式: " + align);
    }
  }

  /**
   * 处理超出最大长度的字符串。 根据溢出行为，从左侧或右侧截断文本并添加省略号。
   *
   * @param osw 输出流写入器
   * @param str 原始字符串
   * @param maxLength 最大允许长度
   * @param overflowBehaviour 溢出处理行为
   * @throws IOException 写入失败时抛出
   */
  private static void writeOverflowedString(
      OutputStreamWriter osw, String str, int maxLength, OverflowBehaviour overflowBehaviour)
      throws IOException {

    int strLength = LineUtil.getDisplayWidth(str);
    int ellipsisLength = LineUtil.getDisplayWidth(ELLIPSIS);
    int remainingLength = maxLength - ellipsisLength;

    switch (overflowBehaviour) {
      case CLIP_RIGHT:
        String leftPart = str.substring(0, remainingLength);
        writeWithEllipsis(osw, leftPart, String.valueOf(ELLIPSIS));
        break;

      case CLIP_LEFT:
        String rightPart = str.substring(strLength - remainingLength);
        writeWithEllipsis(osw, String.valueOf(ELLIPSIS), rightPart);
        break;

      default:
        throw new IllegalArgumentException("不支持的溢出处理方式: " + overflowBehaviour);
    }
  }

  /**
   * 写入带省略号的文本。
   *
   * @param osw 输出流写入器
   * @param prefix 前缀文本
   * @param suffix 后缀文本
   * @throws IOException 写入失败时抛出
   */
  private static void writeWithEllipsis(OutputStreamWriter osw, String prefix, String suffix)
      throws IOException {
    osw.write(prefix);
    osw.write(suffix);
  }

  /**
   * 写入表格的一行数据。 处理每个单元格的内容对齐和边界字符。
   *
   * @param osw 输出流写入器
   * @param left 左边界字符
   * @param columnSeparator 列分隔符
   * @param right 右边界字符
   * @param data 行数据数组
   * @param columns 列配置数组
   * @param dataAligns 数据对齐方式数组
   * @param lineSeparator 行分隔符
   * @param overflowBehaviour 溢出处理行为
   * @throws IOException 写入失败时抛出
   */
  private static void writeRow(
      OutputStreamWriter osw,
      Character left,
      Character columnSeparator,
      Character right,
      String[] data,
      Column[] columns,
      HorizontalAlign[] dataAligns,
      String lineSeparator,
      OverflowBehaviour overflowBehaviour)
      throws IOException {

    LineUtil.writeIfNotNull(osw, left);

    for (int i = 0; i < data.length; i++) {
      ColumnConfig config = new ColumnConfig(columns[i].getMaxWidth(), dataAligns[i]);

      writeJustified(osw, data[i], config.align, config.width, PADDING, overflowBehaviour);

      boolean isLastColumn = i == data.length - 1;
      if (!isLastColumn && columnSeparator != null) {
        osw.write(columnSeparator);
      }
    }

    LineUtil.writeIfNotNull(osw, right);
    LineUtil.writeIfNotNull(osw, lineSeparator);
  }

  private static String[] getData(Column[] columns, int rowIndex) {
    return Arrays.stream(columns).map(c -> c.getCell(rowIndex)).toArray(String[]::new);
  }

  private void writeFooter(
      OutputStreamWriter osw,
      List<Character> borders,
      Column[] columns,
      OverflowBehaviour overflowBehaviour)
      throws IOException {

    if (footer.length != columns.length) {
      throw new IllegalArgumentException("Footer size does not match number of columns");
    }

    HorizontalAlign[] dataAligns =
        Arrays.stream(columns).map(Column::getFooterAlign).toArray(HorizontalAlign[]::new);
    writeRow(
        osw,
        borders.get(4),
        borders.get(5),
        borders.get(6),
        footer,
        columns,
        dataAligns,
        System.lineSeparator(),
        overflowBehaviour);
  }

  private void writeBottomBorder(
      OutputStreamWriter writer, List<Character> borders, String lineSeparator, Column[] columns)
      throws IOException {
    writeHorizontalLine(
        writer,
        borders.get(TABLE_BOTTOM_BORDER_LEFT_IDX),
        borders.get(TABLE_BOTTOM_BORDER_MIDDLE_IDX),
        borders.get(TABLE_BOTTOM_BORDER_COLUMN_SEPARATOR_IDX),
        borders.get(TABLE_BOTTOM_BORDER_RIGHT_IDX),
        lineSeparator,
        columns);
  }

  /** 列配置数据类，用于简化参数传递 */
  private static class ColumnConfig {
    final int width;
    final HorizontalAlign align;

    ColumnConfig(int width, HorizontalAlign align) {
      this.width = width;
      this.align = align;
    }
  }
}
