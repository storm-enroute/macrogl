package org.macrogl.algebra






sealed abstract class MajorOrder


case object RowMajor extends MajorOrder {
  override def toString = "row-major"
}


case object ColumnMajor extends MajorOrder {
  override def toString = "column-major"
}
