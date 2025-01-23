package com.tyy.output.table;

/** 水平对齐方式的枚举类。 */
public enum HorizontalAlign {
  /** 左对齐 */
  LEFT,

  /** 居中对齐 */
  CENTER,

  /** 右对齐 */
  RIGHT;

  /**
   * 根据字符串获取对应的水平对齐枚举值。
   *
   * @param align 对齐方式的字符串表示，不区分大小写
   * @return 对应的 HorizontalAlign 枚举值
   * @throws IllegalArgumentException 当传入的对齐方式字符串无效时抛出
   */
  public static HorizontalAlign fromString(String align) {
    if (align.equalsIgnoreCase("left")) {
      return LEFT;
    } else if (align.equalsIgnoreCase("center")) {
      return CENTER;
    } else if (align.equalsIgnoreCase("right")) {
      return RIGHT;
    } else {
      throw new IllegalArgumentException("无效的水平对齐方式: " + align);
    }
  }
}
