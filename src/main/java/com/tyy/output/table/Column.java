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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.tyy.output.table.utils.LineUtil;
import java.util.List;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;

/** 表格列的实现类。 支持设置列头、列尾、对齐方式、可见性等属性， 并提供了Builder模式进行构建。 */
public class Column {
  @Getter private final String header;

  @Getter private final String footer;

  @Getter private final HorizontalAlign headerAlign;

  @Getter private final HorizontalAlign dataAlign;

  @Getter private final HorizontalAlign footerAlign;

  @Setter @Getter private int maxWidth;
  @Getter private final boolean visible;
  @Getter private final boolean isCustomerWidth;
  private List<String> cellContents;

  private Column(Builder builder) {
    this.header = builder.header.toUpperCase(Locale.ENGLISH);
    this.footer = builder.footer;

    this.headerAlign = builder.headerAlign;
    this.dataAlign = builder.dataAlign;
    this.footerAlign = builder.footerAlign;
    this.visible = builder.visible;
    this.maxWidth = builder.maxWidth;
    this.cellContents = builder.cellContents;
    this.isCustomerWidth = builder.isCustomerWidth;
  }

  /** 创建Builder实例并初始化默认值。 */
  public static class Builder {
    private String header;
    private String footer;
    private HorizontalAlign headerAlign;
    private HorizontalAlign dataAlign;
    private HorizontalAlign footerAlign;
    private boolean visible;
    private int maxWidth;
    private boolean isCustomerWidth;
    private List<String> cellContents;

    public Builder() {
      this.header = "";
      this.footer = "";
      this.headerAlign = com.tyy.output.table.HorizontalAlign.CENTER;
      this.dataAlign = com.tyy.output.table.HorizontalAlign.LEFT;
      this.footerAlign = com.tyy.output.table.HorizontalAlign.CENTER;
      this.visible = true;
      this.maxWidth = 0;
      this.isCustomerWidth = false;
      this.cellContents = Lists.newArrayList();
    }

    /**
     * 设置列头文本。 如果未设置自定义宽度，会根据文本长度自动调整列宽。
     *
     * @param header 列头文本
     * @return Builder实例
     * @throws NullPointerException 当header为null时抛出
     */
    public Builder withHeader(String header) {
      Preconditions.checkNotNull(header, "Header cannot be null");
      this.header = header;
      this.maxWidth =
          isCustomerWidth ? maxWidth : Math.max(maxWidth, LineUtil.getDisplayWidth(header));
      return this;
    }

    public Builder withFooter(String footer) {
      Preconditions.checkNotNull(footer, "Footer cannot be null");
      this.footer = footer;
      this.maxWidth =
          isCustomerWidth ? maxWidth : Math.max(maxWidth, LineUtil.getDisplayWidth(footer));
      return this;
    }

    public Builder withHeaderAlign(String headerAlign) {
      this.headerAlign = HorizontalAlign.fromString(headerAlign);
      return this;
    }

    public Builder withHeaderAlign(HorizontalAlign headerAlign) {
      this.headerAlign = headerAlign;
      return this;
    }

    public Builder withDataAlign(String dataAlign) {
      this.dataAlign = HorizontalAlign.fromString(dataAlign);
      return this;
    }

    public Builder withDataAlign(HorizontalAlign dataAlign) {
      this.dataAlign = dataAlign;
      return this;
    }

    public Builder withFooterAlign(String footerAlign) {
      this.footerAlign = HorizontalAlign.fromString(footerAlign);
      return this;
    }

    public Builder withFooterAlign(HorizontalAlign footerAlign) {
      this.footerAlign = footerAlign;
      return this;
    }

    public Builder withVisible(boolean visible) {
      this.visible = visible;
      return this;
    }

    public Builder withMaxWidth(int maxWidth) {
      Preconditions.checkArgument(maxWidth >= 0, "Max width must be non-negative");
      this.maxWidth = maxWidth;
      this.isCustomerWidth = true;
      return this;
    }

    public Column build() {
      Column column = new Column(this);
      return column;
    }
  }

  /**
   * 创建当前列的副本。
   *
   * @return 列的新实例，包含相同的配置和内容
   */
  public Column copy() {
    Builder builder = new Builder();
    builder.header = header;
    builder.footer = footer;
    builder.headerAlign = headerAlign;
    builder.dataAlign = dataAlign;
    builder.footerAlign = footerAlign;
    builder.visible = visible;
    builder.maxWidth = maxWidth;
    builder.cellContents = Lists.newArrayList(cellContents);
    return builder.build();
  }

  /**
   * 添加字符串类型的单元格内容。
   *
   * @param cell 字符值
   * @return 当前列实例
   */
  public Column addCell(String cell) {
    if (cell == null) {
      cell = "null";
    }

    maxWidth = isCustomerWidth ? maxWidth : Math.max(maxWidth, LineUtil.getDisplayWidth(cell));
    cellContents.add(cell);
    return this;
  }

  public Column addCell(char c) {
    return addCell(String.valueOf(c));
  }

  public Column addCell(int i) {
    return addCell(String.valueOf(i));
  }

  public Column addCell(double d) {
    return addCell(String.valueOf(d));
  }

  public Column addCell(boolean b) {
    return addCell(String.valueOf(b));
  }

  /**
   * 创建一个内容数量受限的新列， 并添加省略号
   *
   * @param limit 内容数量限制
   * @return 新的列实例
   */
  public Column getLimitedColumn(int limit) {
    if (cellContents.size() <= limit) {
      return this;
    }

    Column newColumn = copy();
    newColumn.cellContents = cellContents.subList(0, Math.min(limit, cellContents.size()));
    newColumn.reCalculateMaxWidth();
    newColumn.addCell(Constant.ELLIPSIS);

    return newColumn;
  }

  public String getCell(int index) {
    return cellContents.get(index);
  }

  public int getCellCount() {
    return cellContents.size();
  }

  /** 重新计算列的最大宽度。 仅当未设置自定义宽度时进行计算。 */
  private void reCalculateMaxWidth() {
    for (String cell : cellContents) {
      maxWidth = isCustomerWidth ? maxWidth : Math.max(maxWidth, LineUtil.getDisplayWidth(cell));
    }
  }
}
