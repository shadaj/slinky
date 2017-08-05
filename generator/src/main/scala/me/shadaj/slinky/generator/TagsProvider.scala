package me.shadaj.slinky.generator

trait TagsProvider {
  def extract: (Seq[Tag], Seq[Attribute])
}
