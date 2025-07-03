//> using scala 2.13
//> using dep "org.scalameta::scalameta:4.7.7"
//> using toolkit 0.5.0

import scala.io.Source
import java.io.FileWriter
import java.io.File
import scala.util.Using

object CsvToHtml {

  private def linesFromFile(filename: String): List[String] =
    Source.fromFile(filename).getLines().toList

  private def datasetFromLines(lines: List[String]): String = {
    s"""
       |  const data = [
       |    ${lines.flatMap { csvLineToJson }.mkString(", \n") }
       |  ];
       |""".stripMargin
  }

  private def generateHtml(dataset: String): String = {
    linesFromFile("template.txt").mkString("\n").replace("$$data$$", dataset)
  }

  private def csvLineToJson(line: String): Option[String] = {
    val tuple = line.split(",").toList
    if (tuple.length >= 2) {
      Some(s"{ year: '${tuple(0)}', count: ${tuple(1)} }")
    } else None
  }

  def writeContentsToFile(contents: String, file: File) = {
    val fileWriter = new FileWriter(file)
    fileWriter.write(contents)
    fileWriter.close()
  }

  def main(args: Array[String]): Unit = {
    val datasetToInject = datasetFromLines(linesFromFile("components.csv"))
    val generatedHtml = generateHtml(datasetToInject)
    writeContentsToFile(generatedHtml, new File("charts.html"))
  }
}
