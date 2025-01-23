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

import static com.tyy.output.table.Constant.BASIC_ASCII;
import static com.tyy.output.table.Constant.FANCY_ASCII;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;

/** 边框样式 */
public class BorderStyle {
  public static final BorderStyle FANCY = new BorderStyle(FANCY_ASCII, false);
  public static final BorderStyle FANCY2 = new BorderStyle(FANCY_ASCII, true);
  public static final BorderStyle BASIC = new BorderStyle(BASIC_ASCII, true);
  public static final BorderStyle BASIC2 = new BorderStyle(BASIC_ASCII, false);

  /** 边框字符列表 */
  private final ImmutableList<Character> characters;

  /** 是否显示行边界 */
  private final boolean showRowBoundaries;

  /**
   * 创建边框样式。
   *
   * @param characters 边框字符列表
   * @param showRowBoundaries 是否显示行边界
   * @throws IllegalArgumentException 当字符数量小于或等于28时抛出
   */
  public BorderStyle(ImmutableList<Character> characters, boolean showRowBoundaries) {
    Preconditions.checkArgument(characters.size() > 28, "Invalid number of characters");
    this.characters = characters;
    this.showRowBoundaries = showRowBoundaries;
  }

  /**
   * 创建边框样式。
   *
   * @param characters 边框字符数组
   * @param showRowBoundaries 是否显示行边界
   * @throws IllegalArgumentException 当字符数量小于或等于28时抛出
   */
  public BorderStyle(Character[] characters, boolean showRowBoundaries) {
    Preconditions.checkArgument(characters.length > 28, "Invalid number of characters");
    this.characters = ImmutableList.copyOf(characters);
    this.showRowBoundaries = showRowBoundaries;
  }

  /**
   * 获取用于渲染的边框字符列表。
   *
   * @return 边框字符列表
   */
  public List<Character> getCharacters() {
    return characters;
  }

  /**
   * 判断是否启用了数据行之间的边界显示。
   *
   * @return 如果启用了行边界显示返回 {@code true}，否则返回 {@code false}
   */
  public boolean isRowBoundariesEnabled() {
    return showRowBoundaries;
  }
}
