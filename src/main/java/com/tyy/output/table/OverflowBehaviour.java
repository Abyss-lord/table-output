package com.tyy.output.table;

/** 文本溢出处理行为的枚举类。 */
public enum OverflowBehaviour {
  /** 从右侧裁剪溢出内容。 例如："Hello World" -> "Hello..." */
  CLIP_RIGHT,

  /** 从左侧裁剪溢出内容。 例如："Hello World" -> "...World" */
  CLIP_LEFT
}
