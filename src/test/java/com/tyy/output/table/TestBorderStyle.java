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

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestBorderStyle {
  @Test
  void testCreateBorderStyleWithArray() {
    Character[] borderStyles =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ123".chars().mapToObj(c -> (char) c).toArray(Character[]::new);
    BorderStyle borderStyle = new BorderStyle(borderStyles, false);
    Table table =
        new Table.Builder()
            .withTitle("TITLE LINE 1")
            .withRowNumbersEnabled(true)
            .withOverflowBehaviour(OverflowBehaviour.CLIP_LEFT)
            .withBorderStyle(borderStyles, false)
            .build();

    // 创建列
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
    Assertions.assertEquals(
        "ABBBBBBBBBBBBBBBBBBBBBBBBD\n"
            + "E      TITLE LINE 1      G\n"
            + "STTTCTTTTTTTCTTTTTTTTTTTTV\n"
            + "E   F NAME  F  COMMENT   G\n"
            + "STTTUTTTTTTTUTTTTTTTTTTTTV\n"
            + "E 1 F John  F …a comment G\n"
            + "E 2 F Jane  F …r comment G\n"
            + "E 3 F Bob   F …d comment G\n"
            + "E 4 F Alice F …h comment G\n"
            + "OPPPQPPPPPPPQPPPPPPPPPPPPR\n"
            + "E   F foot1 F   foot2    G\n"
            + "Z1112111111121111111111113\n",
        table.getStringFormat());
  }

  @Test
  void testCreateBorderStyleWithList() {
    List<Character> borderStyles =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ123"
            .chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.toList());
    Table table =
        new Table.Builder()
            .withTitle("TITLE LINE 1")
            .withRowNumbersEnabled(true)
            .withOverflowBehaviour(OverflowBehaviour.CLIP_LEFT)
            .withBorderStyle(borderStyles, false)
            .build();

    // 创建列
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
    Assertions.assertEquals(
        "ABBBBBBBBBBBBBBBBBBBBBBBBD\n"
            + "E      TITLE LINE 1      G\n"
            + "STTTCTTTTTTTCTTTTTTTTTTTTV\n"
            + "E   F NAME  F  COMMENT   G\n"
            + "STTTUTTTTTTTUTTTTTTTTTTTTV\n"
            + "E 1 F John  F …a comment G\n"
            + "E 2 F Jane  F …r comment G\n"
            + "E 3 F Bob   F …d comment G\n"
            + "E 4 F Alice F …h comment G\n"
            + "OPPPQPPPPPPPQPPPPPPPPPPPPR\n"
            + "E   F foot1 F   foot2    G\n"
            + "Z1112111111121111111111113\n",
        table.getStringFormat());
  }

  @Test
  void testCreateBorderStyleWithImmutableList() {
    List<Character> borderStyles =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ123"
            .chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.toList());
    ImmutableList<Character> characterImmutableList = ImmutableList.copyOf(borderStyles);
    BorderStyle borderStyle = new BorderStyle(characterImmutableList, false);
    Table table =
        new Table.Builder()
            .withTitle("TITLE LINE 1")
            .withRowNumbersEnabled(true)
            .withOverflowBehaviour(OverflowBehaviour.CLIP_LEFT)
            .withBorderStyle(borderStyle)
            .build();

    // 创建列
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
    Assertions.assertEquals(
        "ABBBBBBBBBBBBBBBBBBBBBBBBD\n"
            + "E      TITLE LINE 1      G\n"
            + "STTTCTTTTTTTCTTTTTTTTTTTTV\n"
            + "E   F NAME  F  COMMENT   G\n"
            + "STTTUTTTTTTTUTTTTTTTTTTTTV\n"
            + "E 1 F John  F …a comment G\n"
            + "E 2 F Jane  F …r comment G\n"
            + "E 3 F Bob   F …d comment G\n"
            + "E 4 F Alice F …h comment G\n"
            + "OPPPQPPPPPPPQPPPPPPPPPPPPR\n"
            + "E   F foot1 F   foot2    G\n"
            + "Z1112111111121111111111113\n",
        table.getStringFormat());
  }
}
