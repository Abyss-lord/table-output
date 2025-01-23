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

package com.tyy.output.table.utils;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import org.jline.utils.AttributedString;

public class LineUtil {

  /**
   * 计算单个字符的显示宽度。
   *
   * @param c 要测量的字符
   * @return 字符的显示宽度，全角字符返回2，其他返回1
   */
  public static int getDisplayWidth(char c) {
    return getDisplayWidth(String.valueOf(c));
  }
  /**
   * 计算字符串的显示宽度，会考虑全角字符。 全角字符的宽度计为2，其他字符计为1。
   *
   * @param str 要测量的输入字符串
   * @return 字符串的显示宽度，如果输入为null则返回0
   */
  public static int getDisplayWidth(String str) {
    if (str == null || str.isEmpty()) {
      return 0;
    }

    AttributedString attributedString = new AttributedString(str);

    return attributedString.columnLength();
  }

  /**
   * 创建一个包含指定数量空格的字符串。
   *
   * @param n 需要生成的空格数量
   * @return 包含n个空格的字符串
   * @throws IllegalArgumentException 当n为负数时抛出异常
   */
  public static String getSpaces(int n) {
    Preconditions.checkArgument(n >= 0, "n must be non-negative");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < n; i++) {
      sb.append(' ');
    }
    return sb.toString();
  }

  /**
   * 创建一个新数组，用指定的字符串值填充。
   *
   * @param str 用于填充数组的字符串
   * @param length 要创建的数组长度
   * @return 用指定字符串填充的新数组
   * @throws IllegalArgumentException 当str为null或length不是正数时抛出异常
   */
  public static String[] buildArrayWithFill(String str, int length) {
    Preconditions.checkArgument(str != null, "str must not be null");
    Preconditions.checkArgument(length > 0, "length must be greater than 0");
    return Collections.nCopies(length, str).toArray(new String[0]);
  }

  /**
   * 判断数组是否全为空
   *
   * @param arr 待班定数组
   * @return {@code true} 全为空，{@code false} 存在非空元素
   */
  public static boolean isAllEmpty(String[] arr) {
    if (arr == null || arr.length == 0) {
      return true;
    }
    for (String s : arr) {
      if (s != null && !s.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  /** 如果字符不为null则写入输出流。 */
  public static void writeIfNotNull(OutputStreamWriter osw, Character ch) throws IOException {
    if (ch != null) {
      osw.write(ch);
    }
  }

  /** 如果字符串不为null则写入输出流。 */
  public static void writeIfNotNull(OutputStreamWriter osw, String str) throws IOException {
    if (str != null) {
      osw.write(str);
    }
  }

  /**
   * 重复输出字符num次。
   *
   * @param osw 输出流
   * @param c 待输出的字符
   * @param num 重复次数
   * @throws IOException 如果输出失败，则抛出 {@code IOException}
   */
  public static void writeRepeated(OutputStreamWriter osw, char c, int num) throws IOException {
    for (int i = 0; i < num; i++) {
      osw.append(c);
    }
  }
}
