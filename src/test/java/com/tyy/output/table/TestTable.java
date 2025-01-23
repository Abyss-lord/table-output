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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestTable {
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @BeforeEach
  void setUp() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  @AfterEach
  public void restoreStreams() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  @Test
  void testCreateTableWithTitle() {
    Table table = new Table.Builder().withTitle("TITLE1").withTitle("TITLE2").build();
    Assertions.assertEquals(2, table.getTitleLines().size());
    Assertions.assertEquals("TITLE1", table.getTitleLines().get(0));
    Assertions.assertEquals("TITLE2", table.getTitleLines().get(1));
  }

  @Test
  void testCreateTableWithColumn() {
    Column name = new Column.Builder().withHeader("name").build();
    Column age = new Column.Builder().withHeader("age").build();
    Column gender = new Column.Builder().withHeader("gender").build();
    Table table =
        new Table.Builder()
            .withTitle("TITLE1")
            .withColumn(name)
            .withBorderStyle(Constant.BASIC_ASCII, true)
            .withColumns(gender, age)
            .build();
    table.print(System.err);
    String errOutput = new String(errContent.toByteArray(), StandardCharsets.UTF_8).trim();
    assertEquals(
        "+---------------------+\n"
            + "|       TITLE1        |\n"
            + "+------+--------+-----+\n"
            + "| NAME | GENDER | AGE |\n"
            + "+------+--------+-----+\n"
            + "|      |        |     |\n"
            + "+------+--------+-----+",
        errOutput);
  }

  @Test
  void testCreateTable() {
    Table table = new Table.Builder().build();
    table.addHeader("name", "age");
    table.addData("name", "John");
    table.addData("age", "25");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> table.addData("unknown", "unknown"));
    String[][] data = {{"Tom", "14"}, {"Mary", "16"}};
    table.addData(data);
    assertEquals(
        "+------+-----+\n"
            + "| NAME | AGE |\n"
            + "+------+-----+\n"
            + "| John | 25  |\n"
            + "| Tom  | 14  |\n"
            + "| Mary | 16  |\n"
            + "+------+-----+\n",
        table.getStringFormat());
  }

  @Test
  void testCreateTableWithoutColumns() {
    Table table = new Table.Builder().build();
    assertThrows(IllegalArgumentException.class, () -> table.printInfo());
  }

  @Test
  void testDefaultTable() {
    Table table = new Table.Builder().build();
    table.addTitleLine("TITLE line 1");
    table.addTitleLine("TITLE line 2");

    Column name = new Column.Builder().withHeader("name").build();
    name.addCell("John").addCell("Jane").addCell("Bob");
    Column age = new Column.Builder().withHeader("age").build();
    age.addCell(25).addCell(30).addCell(40);
    table.addColumns(name, age);
    table.printInfo();
    String infoOutput = new String(outContent.toByteArray(), StandardCharsets.UTF_8).trim();
    Assertions.assertEquals(
        "+--------------+\n"
            + "| TITLE LINE 1 |\n"
            + "+--------------+\n"
            + "| TITLE LINE 2 |\n"
            + "+-------+------+\n"
            + "| NAME  | AGE  |\n"
            + "+-------+------+\n"
            + "| John  | 25   |\n"
            + "| Jane  | 30   |\n"
            + "| Bob   | 40   |\n"
            + "+-------+------+",
        infoOutput);
  }

  @Test
  void testDefaultTableWithChinese() {
    Table table = new Table.Builder().build();
    table.addTitleLine("TITLE line 1");
    table.addTitleLine("TITLE line 2");

    Column name = new Column.Builder().withHeader("name").build();
    name.addCell("张远航").addCell("欧阳吹雪").addCell("王武").addCell("超级长的名字用于测试");
    Column age = new Column.Builder().withHeader("age").build();
    age.addCell(25).addCell(30).addCell(40).addCell(59);
    table.addColumns(name, age);
    table.printInfo();
    String infoOutput = new String(outContent.toByteArray(), StandardCharsets.UTF_8).trim();
    Assertions.assertEquals(
        "+----------------------------+\n"
            + "|        TITLE LINE 1        |\n"
            + "+----------------------------+\n"
            + "|        TITLE LINE 2        |\n"
            + "+----------------------+-----+\n"
            + "|         NAME         | AGE |\n"
            + "+----------------------+-----+\n"
            + "| 张远航               | 25  |\n"
            + "| 欧阳吹雪             | 30  |\n"
            + "| 王武                 | 40  |\n"
            + "| 超级长的名字用于测试 | 59  |\n"
            + "+----------------------+-----+",
        infoOutput);
  }

  @Test
  void testDefaultTableWithTitle() {
    Table table = new Table.Builder().withTitle("TITLE").build();
    Column name = new Column.Builder().withHeader("name").build();
    name.addCell("John").addCell("Jane").addCell("Bob").addCell("Alice");
    Column comment = new Column.Builder().withHeader("Comment").build();
    comment
        .addCell("This is a comment")
        .addCell("This is another comment")
        .addCell("This is a third comment")
        .addCell("This is a fourth comment");
    table.addColumns(name, comment);
    table.printInfo();
    String infoOutput = new String(outContent.toByteArray(), StandardCharsets.UTF_8).trim();
    Assertions.assertEquals(
        "+----------------------------------+\n"
            + "|              TITLE               |\n"
            + "+-------+--------------------------+\n"
            + "| NAME  |         COMMENT          |\n"
            + "+-------+--------------------------+\n"
            + "| John  | This is a comment        |\n"
            + "| Jane  | This is another comment  |\n"
            + "| Bob   | This is a third comment  |\n"
            + "| Alice | This is a fourth comment |\n"
            + "+-------+--------------------------+",
        infoOutput);
  }

  @Test
  void testDefaultTableWithoutTitle() {
    Table table = new Table.Builder().build();
    Column name = new Column.Builder().withHeader("name").build();
    name.addCell("John").addCell("Jane").addCell("Bob").addCell("Alice");
    Column comment = new Column.Builder().withHeader("Comment").build();
    comment
        .addCell("This is a comment")
        .addCell("This is another comment")
        .addCell("This is a third comment")
        .addCell("This is a fourth comment");
    table.addColumns(name, comment);
    table.printInfo();
    String infoOutput = new String(outContent.toByteArray(), StandardCharsets.UTF_8).trim();
    Assertions.assertEquals(
        "+-------+--------------------------+\n"
            + "| NAME  |         COMMENT          |\n"
            + "+-------+--------------------------+\n"
            + "| John  | This is a comment        |\n"
            + "| Jane  | This is another comment  |\n"
            + "| Bob   | This is a third comment  |\n"
            + "| Alice | This is a fourth comment |\n"
            + "+-------+--------------------------+",
        infoOutput);
  }

  @Test
  void testDefaultTableWithoutData() {
    Table table = new Table.Builder().build();
    Column name = new Column.Builder().withHeader("name").withFooter("foot1").build();
    Column comment = new Column.Builder().withHeader("Comment").withFooter("foot2").build();

    table.addColumns(name, comment);
    table.printInfo();
    String infoOutput = new String(outContent.toByteArray(), StandardCharsets.UTF_8).trim();
    Assertions.assertEquals(
        "+-------+---------+\n"
            + "| NAME  | COMMENT |\n"
            + "+-------+---------+\n"
            + "|       |         |\n"
            + "+-------+---------+\n"
            + "| foot1 |  foot2  |\n"
            + "+-------+---------+",
        infoOutput);
  }

  @Test
  void testDefaultTableWithFooterAndTitle() {
    Table table = new Table.Builder().withTitle("TITLE LINE 1").build();
    Column name = new Column.Builder().withHeader("name").withFooter("foot1").build();
    name.addCell("John").addCell("Jane").addCell("Bob").addCell("Alice");
    Column comment = new Column.Builder().withHeader("Comment").withFooter("foot2").build();
    comment
        .addCell("This is a comment")
        .addCell("This is another comment")
        .addCell("This is a third comment")
        .addCell("This is a fourth comment");
    table.addColumns(name, comment);

    table.printInfo();
    String infoOutput = new String(outContent.toByteArray(), StandardCharsets.UTF_8).trim();
    Assertions.assertEquals(
        "+----------------------------------+\n"
            + "|           TITLE LINE 1           |\n"
            + "+-------+--------------------------+\n"
            + "| NAME  |         COMMENT          |\n"
            + "+-------+--------------------------+\n"
            + "| John  | This is a comment        |\n"
            + "| Jane  | This is another comment  |\n"
            + "| Bob   | This is a third comment  |\n"
            + "| Alice | This is a fourth comment |\n"
            + "+-------+--------------------------+\n"
            + "| foot1 |          foot2           |\n"
            + "+-------+--------------------------+",
        infoOutput);
  }

  @Test
  void testDefaultTableWithRowNumbersEnabled() {
    Table table = new Table.Builder().withTitle("TITLE LINE 1").withRowNumbersEnabled(true).build();
    Column name = new Column.Builder().withHeader("name").withFooter("foot1").build();
    name.addCell("John").addCell("Jane").addCell("Bob").addCell("Alice");
    Column comment = new Column.Builder().withHeader("Comment").withFooter("foot2").build();
    comment
        .addCell("This is a comment")
        .addCell("This is another comment")
        .addCell("This is a third comment")
        .addCell("This is a fourth comment");
    table.addColumns(name, comment);
    table.printInfo();
    String infoOutput = new String(outContent.toByteArray(), StandardCharsets.UTF_8).trim();
    Assertions.assertEquals(
        "+--------------------------------------+\n"
            + "|             TITLE LINE 1             |\n"
            + "+---+-------+--------------------------+\n"
            + "|   | NAME  |         COMMENT          |\n"
            + "+---+-------+--------------------------+\n"
            + "| 1 | John  | This is a comment        |\n"
            + "| 2 | Jane  | This is another comment  |\n"
            + "| 3 | Bob   | This is a third comment  |\n"
            + "| 4 | Alice | This is a fourth comment |\n"
            + "+---+-------+--------------------------+\n"
            + "|   | foot1 |          foot2           |\n"
            + "+---+-------+--------------------------+",
        infoOutput);
  }

  @Test
  void testDefaultTableWithMaxWidth() {
    Table table = new Table.Builder().withTitle("TITLE LINE 1").withRowNumbersEnabled(true).build();
    Column name = new Column.Builder().withHeader("name").withFooter("foot1").build();
    name.addCell("John").addCell("Jane").addCell("Bob").addCell("Alice");
    Column comment =
        new Column.Builder().withHeader("Comment").withFooter("foot2").withMaxWidth(10).build();
    comment
        .addCell("This is a comment")
        .addCell("This is another comment")
        .addCell("This is a third comment")
        .addCell("This is a fourth comment");
    table.addColumns(name, comment);
    table.printInfo();
    String infoOutput = new String(outContent.toByteArray(), StandardCharsets.UTF_8).trim();
    Assertions.assertEquals(
        "+------------------------+\n"
            + "|      TITLE LINE 1      |\n"
            + "+---+-------+------------+\n"
            + "|   | NAME  |  COMMENT   |\n"
            + "+---+-------+------------+\n"
            + "| 1 | John  | This is a… |\n"
            + "| 2 | Jane  | This is a… |\n"
            + "| 3 | Bob   | This is a… |\n"
            + "| 4 | Alice | This is a… |\n"
            + "+---+-------+------------+\n"
            + "|   | foot1 |   foot2    |\n"
            + "+---+-------+------------+",
        infoOutput);
  }

  @Test
  void testTableWithClipLeft() {
    Table table =
        new Table.Builder()
            .withTitle("TITLE LINE 1")
            .withRowNumbersEnabled(true)
            .withOverflowBehaviour(OverflowBehaviour.CLIP_LEFT)
            .build();
    Column name = new Column.Builder().withHeader("name").withFooter("foot1").build();
    name.addCell("John").addCell("Jane").addCell("Bob").addCell("Alice");
    Column comment =
        new Column.Builder().withHeader("Comment").withFooter("foot2").withMaxWidth(10).build();
    comment
        .addCell("This is a comment")
        .addCell("This is another comment")
        .addCell("This is a third comment")
        .addCell("This is a fourth comment");
    table.addColumns(name, comment);
    table.printInfo();
    String infoOutput = new String(outContent.toByteArray(), StandardCharsets.UTF_8).trim();
    Assertions.assertEquals(
        "+------------------------+\n"
            + "|      TITLE LINE 1      |\n"
            + "+---+-------+------------+\n"
            + "|   | NAME  |  COMMENT   |\n"
            + "+---+-------+------------+\n"
            + "| 1 | John  | …a comment |\n"
            + "| 2 | Jane  | …r comment |\n"
            + "| 3 | Bob   | …d comment |\n"
            + "| 4 | Alice | …h comment |\n"
            + "+---+-------+------------+\n"
            + "|   | foot1 |   foot2    |\n"
            + "+---+-------+------------+",
        infoOutput);
  }

  @Test
  void testTableWithHeaderLeftAlignment() {
    Table table = new Table.Builder().withTitle("TITLE LINE 1").withRowNumbersEnabled(true).build();
    Column name =
        new Column.Builder()
            .withHeader("name")
            .withFooter("foot1")
            .withHeaderAlign(HorizontalAlign.LEFT)
            .build();
    name.addCell("John").addCell("Jane").addCell("Bob").addCell("Alice");
    Column comment =
        new Column.Builder()
            .withHeader("Comment")
            .withHeaderAlign(HorizontalAlign.LEFT)
            .withFooter("foot2")
            .withMaxWidth(10)
            .build();
    comment
        .addCell("This is a comment")
        .addCell("This is another comment")
        .addCell("This is a third comment")
        .addCell("This is a fourth comment");
    table.addColumns(name, comment);
    table.printInfo();
    String infoOutput = new String(outContent.toByteArray(), StandardCharsets.UTF_8).trim();
    Assertions.assertEquals(
        "+------------------------+\n"
            + "|      TITLE LINE 1      |\n"
            + "+---+-------+------------+\n"
            + "|   | NAME  | COMMENT    |\n"
            + "+---+-------+------------+\n"
            + "| 1 | John  | This is a… |\n"
            + "| 2 | Jane  | This is a… |\n"
            + "| 3 | Bob   | This is a… |\n"
            + "| 4 | Alice | This is a… |\n"
            + "+---+-------+------------+\n"
            + "|   | foot1 |   foot2    |\n"
            + "+---+-------+------------+",
        infoOutput);
  }

  @Test
  void testTableWithHeaderRightAlignment() {
    Table table =
        new Table.Builder().withTitle("TITLE LINE 1").withRowNumbersEnabled(false).build();
    Column name =
        new Column.Builder()
            .withHeader("name")
            .withFooter("foot1")
            .withHeaderAlign(HorizontalAlign.RIGHT)
            .build();
    name.addCell("John").addCell("Jane").addCell("Bob").addCell("Alice");
    Column comment =
        new Column.Builder()
            .withHeader("Comment")
            .withHeaderAlign(HorizontalAlign.RIGHT)
            .withFooter("foot2")
            .withMaxWidth(10)
            .build();
    comment
        .addCell("This is a comment")
        .addCell("This is another comment")
        .addCell("This is a third comment")
        .addCell("This is a fourth comment");
    table.addColumns(name, comment);
    table.printInfo();
    String infoOutput = new String(outContent.toByteArray(), StandardCharsets.UTF_8).trim();
    Assertions.assertEquals(
        "+--------------------+\n"
            + "|    TITLE LINE 1    |\n"
            + "+-------+------------+\n"
            + "|  NAME |    COMMENT |\n"
            + "+-------+------------+\n"
            + "| John  | This is a… |\n"
            + "| Jane  | This is a… |\n"
            + "| Bob   | This is a… |\n"
            + "| Alice | This is a… |\n"
            + "+-------+------------+\n"
            + "| foot1 |   foot2    |\n"
            + "+-------+------------+",
        infoOutput);
  }

  @Test
  void testTableWithoutRowNumbersAndOverflow() {
    Table table =
        new Table.Builder().withTitle("TITLE LINE 1").withRowNumbersEnabled(false).build();
    Column name =
        new Column.Builder()
            .withHeader("name")
            .withFooter("foot1")
            .withHeaderAlign(HorizontalAlign.RIGHT)
            .build();
    name.addCell("John").addCell("Jane").addCell("Bob").addCell("Alice");
    Column comment =
        new Column.Builder()
            .withHeader("Comment")
            .withHeaderAlign(HorizontalAlign.RIGHT)
            .withFooter("foot2")
            .build();
    comment
        .addCell("This is a comment")
        .addCell("This is another comment")
        .addCell("This is a third comment")
        .addCell("This is a fourth comment");
    table.addColumns(name, comment);
    table.printInfo();
    String infoOutput = new String(outContent.toByteArray(), StandardCharsets.UTF_8).trim();
    Assertions.assertEquals(
        "+----------------------------------+\n"
            + "|           TITLE LINE 1           |\n"
            + "+-------+--------------------------+\n"
            + "|  NAME |                  COMMENT |\n"
            + "+-------+--------------------------+\n"
            + "| John  | This is a comment        |\n"
            + "| Jane  | This is another comment  |\n"
            + "| Bob   | This is a third comment  |\n"
            + "| Alice | This is a fourth comment |\n"
            + "+-------+--------------------------+\n"
            + "| foot1 |          foot2           |\n"
            + "+-------+--------------------------+",
        infoOutput);
  }

  @Test
  void testTableWithVisible() {
    Table table =
        new Table.Builder().withTitle("TITLE LINE 1").withRowNumbersEnabled(false).build();
    Column name =
        new Column.Builder()
            .withHeader("name")
            .withFooter("foot1")
            .withVisible(false)
            .withHeaderAlign(HorizontalAlign.RIGHT)
            .build();
    name.addCell("John").addCell("Jane").addCell("Bob").addCell("Alice");
    Column comment =
        new Column.Builder()
            .withHeader("Comment")
            .withHeaderAlign(HorizontalAlign.RIGHT)
            .withFooter("foot2")
            .build();
    comment
        .addCell("This is a comment")
        .addCell("This is another comment")
        .addCell("This is a third comment")
        .addCell("This is a fourth comment");
    table.addColumns(name, comment);
    table.printInfo();
    String infoOutput = new String(outContent.toByteArray(), StandardCharsets.UTF_8).trim();
    Assertions.assertEquals(
        "+--------------------------+\n"
            + "|       TITLE LINE 1       |\n"
            + "+--------------------------+\n"
            + "|                  COMMENT |\n"
            + "+--------------------------+\n"
            + "| This is a comment        |\n"
            + "| This is another comment  |\n"
            + "| This is a third comment  |\n"
            + "| This is a fourth comment |\n"
            + "+--------------------------+\n"
            + "|          foot2           |\n"
            + "+--------------------------+",
        infoOutput);
  }

  @Test
  void testTableWithVisibleAndRowNumbers() {
    Table table = new Table.Builder().withTitle("TITLE LINE 1").withRowNumbersEnabled(true).build();
    Column name =
        new Column.Builder()
            .withHeader("name")
            .withFooter("foot1")
            .withVisible(false)
            .withHeaderAlign(HorizontalAlign.RIGHT)
            .build();
    name.addCell("John").addCell("Jane").addCell("Bob").addCell("Alice");
    Column comment =
        new Column.Builder()
            .withHeader("Comment")
            .withHeaderAlign(HorizontalAlign.RIGHT)
            .withFooter("foot2")
            .build();
    comment
        .addCell("This is a comment")
        .addCell("This is another comment")
        .addCell("This is a third comment")
        .addCell("This is a fourth comment");
    table.addColumns(name, comment);
    table.printInfo();
    String infoOutput = new String(outContent.toByteArray(), StandardCharsets.UTF_8).trim();
    Assertions.assertEquals(
        "+------------------------------+\n"
            + "|         TITLE LINE 1         |\n"
            + "+---+--------------------------+\n"
            + "|   |                  COMMENT |\n"
            + "+---+--------------------------+\n"
            + "| 1 | This is a comment        |\n"
            + "| 2 | This is another comment  |\n"
            + "| 3 | This is a third comment  |\n"
            + "| 4 | This is a fourth comment |\n"
            + "+---+--------------------------+\n"
            + "|   |          foot2           |\n"
            + "+---+--------------------------+",
        infoOutput);
  }

  @Test
  void testTableWithDataCenteredAlign() {
    Table table =
        new Table.Builder().withTitle("TITLE LINE 1").withRowNumbersEnabled(false).build();
    Column name =
        new Column.Builder()
            .withHeader("name")
            .withFooter("foot1")
            .withHeaderAlign(HorizontalAlign.RIGHT)
            .build();
    name.addCell("John").addCell("Jane").addCell("Bob").addCell("Alice");
    Column comment =
        new Column.Builder()
            .withHeader("Comment")
            .withHeaderAlign(HorizontalAlign.CENTER)
            .withDataAlign(HorizontalAlign.CENTER)
            .withFooter("foot2")
            .build();
    comment
        .addCell("This is a comment")
        .addCell("This is another comment")
        .addCell("This is a third comment")
        .addCell("This is a fourth comment");
    table.addColumns(name, comment);
    table.printInfo();
    String infoOutput = new String(outContent.toByteArray(), StandardCharsets.UTF_8).trim();
    Assertions.assertEquals(
        "+----------------------------------+\n"
            + "|           TITLE LINE 1           |\n"
            + "+-------+--------------------------+\n"
            + "|  NAME |         COMMENT          |\n"
            + "+-------+--------------------------+\n"
            + "| John  |    This is a comment     |\n"
            + "| Jane  | This is another comment  |\n"
            + "| Bob   | This is a third comment  |\n"
            + "| Alice | This is a fourth comment |\n"
            + "+-------+--------------------------+\n"
            + "| foot1 |          foot2           |\n"
            + "+-------+--------------------------+",
        infoOutput);
  }

  @Test
  void testTableWithLimit() {
    Table table =
        new Table.Builder()
            .withTitle("TITLE LINE 1")
            .withRowNumbersEnabled(false)
            .withLimit(2)
            .build();
    Column name =
        new Column.Builder()
            .withHeader("name")
            .withFooter("foot1")
            .withHeaderAlign(HorizontalAlign.RIGHT)
            .build();
    name.addCell("John").addCell("Jane").addCell("Bob").addCell("Alice");
    Column comment =
        new Column.Builder()
            .withHeader("Comment")
            .withHeaderAlign(HorizontalAlign.CENTER)
            .withDataAlign(HorizontalAlign.CENTER)
            .withFooter("foot2")
            .build();
    comment
        .addCell("This is a comment")
        .addCell("This is another comment")
        .addCell("This is a third comment")
        .addCell("This is a fourth comment");
    table.addColumns(name, comment);
    table.printInfo();
    String infoOutput = new String(outContent.toByteArray(), StandardCharsets.UTF_8).trim();
    Assertions.assertEquals(
        "+----------------------------------+\n"
            + "|           TITLE LINE 1           |\n"
            + "+-------+--------------------------+\n"
            + "|  NAME |         COMMENT          |\n"
            + "+-------+--------------------------+\n"
            + "| John  |    This is a comment     |\n"
            + "| Jane  | This is another comment  |\n"
            + "| …     |            …             |\n"
            + "+-------+--------------------------+\n"
            + "| foot1 |          foot2           |\n"
            + "+-------+--------------------------+",
        infoOutput);
  }

  @Test
  void testTableWithLimitAndRowNumbers() {
    Table table =
        new Table.Builder()
            .withTitle("TITLE LINE 1")
            .withRowNumbersEnabled(true)
            .withLimit(2)
            .build();
    Column name =
        new Column.Builder()
            .withHeader("name")
            .withFooter("foot1")
            .withHeaderAlign(HorizontalAlign.RIGHT)
            .build();
    name.addCell("John").addCell("Jane").addCell("Bob").addCell("Alice");
    Column comment =
        new Column.Builder()
            .withHeader("Comment")
            .withHeaderAlign(HorizontalAlign.CENTER)
            .withDataAlign(HorizontalAlign.CENTER)
            .withFooter("foot2")
            .build();
    comment
        .addCell("This is a comment")
        .addCell("This is another comment")
        .addCell("This is a third comment")
        .addCell("This is a fourth comment");
    table.addColumns(name, comment);
    table.printInfo();
    String infoOutput = new String(outContent.toByteArray(), StandardCharsets.UTF_8).trim();
    Assertions.assertEquals(
        "+--------------------------------------+\n"
            + "|             TITLE LINE 1             |\n"
            + "+---+-------+--------------------------+\n"
            + "|   |  NAME |         COMMENT          |\n"
            + "+---+-------+--------------------------+\n"
            + "| 1 | John  |    This is a comment     |\n"
            + "| 2 | Jane  | This is another comment  |\n"
            + "| 3 | …     |            …             |\n"
            + "+---+-------+--------------------------+\n"
            + "|   | foot1 |          foot2           |\n"
            + "+---+-------+--------------------------+",
        infoOutput);
  }

  @Test
  void testTableWithFancyStyle() {
    Table table =
        new Table.Builder()
            .withTitle("TITLE LINE 1")
            .withRowNumbersEnabled(false)
            .withLimit(2)
            .withBorderStyle(BorderStyle.FANCY)
            .build();
    Column name =
        new Column.Builder()
            .withHeader("name")
            .withFooter("foot1")
            .withHeaderAlign(HorizontalAlign.RIGHT)
            .build();
    name.addCell("John").addCell("Jane").addCell("Bob").addCell("Alice");
    Column comment =
        new Column.Builder()
            .withHeader("Comment")
            .withHeaderAlign(HorizontalAlign.CENTER)
            .withDataAlign(HorizontalAlign.CENTER)
            .withFooter("foot2")
            .build();
    comment
        .addCell("This is a comment")
        .addCell("This is another comment")
        .addCell("This is a third comment")
        .addCell("This is a fourth comment");
    table.addColumns(name, comment);
    table.printInfo();
    String infoOutput = new String(outContent.toByteArray(), StandardCharsets.UTF_8).trim();
    Assertions.assertEquals(
        "╔══════════════════════════════════╗\n"
            + "║           TITLE LINE 1           ║\n"
            + "╠═══════╤══════════════════════════╣\n"
            + "║  NAME │         COMMENT          ║\n"
            + "╠═══════╪══════════════════════════╣\n"
            + "║ John  │    This is a comment     ║\n"
            + "║ Jane  │ This is another comment  ║\n"
            + "║ …     │            …             ║\n"
            + "╟───────┼──────────────────────────╢\n"
            + "║ foot1 │          foot2           ║\n"
            + "╚═══════╧══════════════════════════╝",
        infoOutput);
  }

  @Test
  void testTableWithDataRowSeparator() {
    Table table =
        new Table.Builder()
            .withTitle("TITLE LINE 1")
            .withRowNumbersEnabled(false)
            .withLimit(2)
            .withBorderStyle(BorderStyle.BASIC)
            .build();
    Column name =
        new Column.Builder()
            .withHeader("name")
            .withFooter("foot1")
            .withHeaderAlign(HorizontalAlign.RIGHT)
            .build();
    name.addCell("John").addCell("Jane").addCell("Bob").addCell("Alice");
    Column comment =
        new Column.Builder()
            .withHeader("Comment")
            .withHeaderAlign(HorizontalAlign.CENTER)
            .withDataAlign(HorizontalAlign.CENTER)
            .withFooter("foot2")
            .build();
    comment
        .addCell("This is a comment")
        .addCell("This is another comment")
        .addCell("This is a third comment")
        .addCell("This is a fourth comment");
    table.addColumns(name, comment);
    table.printInfo();
    String infoOutput = new String(outContent.toByteArray(), StandardCharsets.UTF_8).trim();
    Assertions.assertEquals(
        "+----------------------------------+\n"
            + "|           TITLE LINE 1           |\n"
            + "+-------+--------------------------+\n"
            + "|  NAME |         COMMENT          |\n"
            + "+-------+--------------------------+\n"
            + "| John  |    This is a comment     |\n"
            + "+-------+--------------------------+\n"
            + "| Jane  | This is another comment  |\n"
            + "+-------+--------------------------+\n"
            + "| …     |            …             |\n"
            + "+-------+--------------------------+\n"
            + "| foot1 |          foot2           |\n"
            + "+-------+--------------------------+",
        infoOutput);
  }
}
