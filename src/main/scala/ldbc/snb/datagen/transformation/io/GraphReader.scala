package ldbc.snb.datagen.transformation.io

import ldbc.snb.datagen.transformation.model.{Graph, GraphDef, Id, Mode}
import org.apache.spark.sql.{DataFrame, SparkSession}
import better.files._
import org.apache.hadoop.fs.{FileSystem, Path}

import java.net.URI

trait GraphReader[M <: Mode] {
  type Data
  def read(graphDef: GraphDef[M], path: String): Graph[M, Data]
  def exists(graphDef: GraphDef[M], path: String): Boolean
}

object GraphReader {
  type Aux[M <: Mode, D] = GraphReader[M] { type Data = D }

  def apply[M <: Mode, D](implicit ev: GraphReader.Aux[M, D]): GraphReader.Aux[M, D] = ev
}

private final class DataFrameGraphReader[M <: Mode](implicit spark: SparkSession, ev: Id[DataFrame] =:= M#Layout[DataFrame]) extends GraphReader[M] {
  type Data = DataFrame

  val csvOptions = Map(
    "header" -> "true",
    "sep" -> "|"
  )

  override def read(definition: GraphDef[M], path: String): Graph[M, DataFrame] = {
    val entities = (for { entity <- definition.entities } yield {
      val df = spark.read.options(csvOptions).csv((path / entity.entityPath).toString())
      entity -> ev(df)
    }).toMap
    Graph(definition.layout, definition.mode, entities)
  }

  override def exists(graphDef: GraphDef[M], path: String): Boolean = {
    val hadoopPath = new Path(path)
    FileSystem.get(new URI(path), spark.sparkContext.hadoopConfiguration).exists(hadoopPath)
  }
}

trait GraphReaderInstances {
  implicit def dataFrameGraphReader[M <: Mode]
  (implicit spark: SparkSession, ev: Id[DataFrame] =:= M#Layout[DataFrame]): GraphReader.Aux[M, DataFrame] =
    new DataFrameGraphReader[M]
}
