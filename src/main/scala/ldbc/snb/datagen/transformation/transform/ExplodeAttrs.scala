package ldbc.snb.datagen.transformation.transform

import ldbc.snb.datagen.syntax._
import ldbc.snb.datagen.transformation.model.EntityType.{Attr, Node}
import ldbc.snb.datagen.transformation.model.{Graph, Mode}
import ldbc.snb.datagen.transformation.transform.Raw.withRawColumns
import org.apache.spark.sql.{Column, DataFrame}
import org.apache.spark.sql.functions.{explode, split}

object ExplodeAttrs extends Transform[Mode.Raw.type, Mode.Raw.type] {
  override def transform(input: In): Out = {

      def explodedAttr(attr: Attr, node: DataFrame, column: Column) =
        attr -> node.select(withRawColumns(attr, $"id".as(s"${attr.parent}.id"), explode(split(column, ",")).as(s"${attr.attribute}.id")))

      val updatedEntities = input.entities.collect {
        case (k@Node("Person", false), v) => Map(
          explodedAttr(Attr("Email", "Person", "EmailAddress"), v, $"email"),
          explodedAttr(Attr("Speaks", "Person", "Language"), v, $"language"),
          k -> v.drop("email", "language")
        )
      }.foldLeft(input.entities)(_ ++ _)

      Graph("NonMerged", Mode.Raw, updatedEntities)
  }
}
