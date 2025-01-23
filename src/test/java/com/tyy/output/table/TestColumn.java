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

import static com.tyy.output.table.Constant.ELLIPSIS;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestColumn {

  @Test
  void testCreateDefaultColumn() {
    Column column = new Column.Builder().build();

    Assertions.assertEquals(column.getHeader(), "");
    Assertions.assertEquals(column.getFooter(), "");
    Assertions.assertEquals(column.getHeaderAlign(), HorizontalAlign.CENTER);
    Assertions.assertEquals(column.getDataAlign(), HorizontalAlign.LEFT);
    Assertions.assertEquals(column.getFooterAlign(), HorizontalAlign.CENTER);
    Assertions.assertEquals(column.getMaxWidth(), 0);
    Assertions.assertTrue(column.isVisible());
    Assertions.assertFalse(column.isCustomerWidth());
    Assertions.assertEquals(column.getCellCount(), 0);
  }

  @Test
  void testCreateColumn() {
    Column column =
        new Column.Builder()
            .withHeader("Header")
            .withFooter("Footer")
            .withHeaderAlign(HorizontalAlign.RIGHT)
            .withDataAlign(HorizontalAlign.RIGHT)
            .withFooterAlign(HorizontalAlign.RIGHT)
            .withMaxWidth(10)
            .withVisible(false)
            .build();

    Assertions.assertEquals(column.getHeader(), "HEADER");
    Assertions.assertEquals(column.getFooter(), "Footer");
    Assertions.assertEquals(column.getHeaderAlign(), HorizontalAlign.RIGHT);
    Assertions.assertEquals(column.getDataAlign(), HorizontalAlign.RIGHT);
    Assertions.assertEquals(column.getFooterAlign(), HorizontalAlign.RIGHT);
    Assertions.assertEquals(column.getMaxWidth(), 10);
    Assertions.assertFalse(column.isVisible());
    Assertions.assertTrue(column.isCustomerWidth());
    Assertions.assertEquals(column.getCellCount(), 0);
  }

  @Test
  void testAddCell() {
    Column column =
        new Column.Builder()
            .withHeaderAlign("center")
            .withDataAlign("left")
            .withFooterAlign("center")
            .build();
    column.addCell("Cell1").addCell(1).addCell('c').addCell(3.4).addCell(true).addCell(null);
    Assertions.assertEquals(column.getCellCount(), 6);
    Assertions.assertEquals(column.getCell(5), "null");
  }

  @Test
  void testGetLimitedColumn() {
    Column column =
        new Column.Builder()
            .withHeader("Header")
            .withFooter("Footer")
            .withHeaderAlign(HorizontalAlign.RIGHT)
            .withDataAlign(HorizontalAlign.RIGHT)
            .withFooterAlign(HorizontalAlign.RIGHT)
            .withMaxWidth(10)
            .withVisible(false)
            .build();
    column.addCell("cell1").addCell("cell2").addCell("cell3").addCell("cell4").addCell("cell5");
    Column limitedColumn = column.getLimitedColumn(10);
    Assertions.assertEquals(limitedColumn.getCellCount(), 5);

    limitedColumn = column.getLimitedColumn(3);
    Assertions.assertEquals(limitedColumn.getCellCount(), 4);
    Assertions.assertEquals(limitedColumn.getCell(2), "cell3");
    Assertions.assertEquals(limitedColumn.getCell(3), String.valueOf(ELLIPSIS));
  }
}
