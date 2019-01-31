import java.io.{BufferedWriter, FileWriter}

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by DJ Yuhn on 1/31/19.
  */

object SparkSortWords {

  def main(args: Array[String]) {

    System.setProperty("hadoop.home.dir", "C:\\winutils")

    val sparkConf = new SparkConf().setAppName("SparkSortWords").setMaster("local[*]")
    val sc=new SparkContext(sparkConf)

    val input=sc.textFile("input")

    val groupedWords = input
      .flatMap( line => {
        line.split(" ")
      })
      .groupBy(word => {
        word.charAt(0)
      })
      .sortByKey()
      .cache()

    val output = groupedWords.collect()

    val groupedWordsWriter = new BufferedWriter(new FileWriter("groupedWords.txt"))

    output.foreach(group => {
      val char = group._1

      groupedWordsWriter.append(group._1).append(": ")

      group._2.foreach(word => {
        groupedWordsWriter.append(word + " ")
      })

      groupedWordsWriter.append("\n")
    })

    groupedWordsWriter.close()

  }

}
