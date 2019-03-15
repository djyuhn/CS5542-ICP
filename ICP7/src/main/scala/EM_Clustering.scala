import java.io.PrintStream

import org.apache.spark.mllib.clustering.GaussianMixture
import org.apache.spark.mllib.feature.{HashingTF, IDF}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * @author djyuhn
  *         3/14/2019
  */
object EM_Clustering {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "C:\\winutils\\")
    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")
    val sc = new SparkContext(sparkConf)

    val features = sc.textFile("data_project\\coco_images_original.txt")
      .map(line =>{
        val splitLine = line.split("\t")
        var tokenize = Array[String]()
        if (splitLine.length == 3) {
          val caption = line.split("\t")(1)
          val commasRemoved = caption.replaceAll(",", "")
          tokenize = commasRemoved.split(" ")

        }
        tokenize.toSeq
      })

    val hashingTF = new HashingTF(100)

    val tf = hashingTF.transform(features)

    val idf = new IDF().fit(tf)
    val tfidf = idf.transform(tf)


    // Cluster the data into 10 classes using GaussianMixture
    val gmm = new GaussianMixture().setK(10).run(tf)

    val clusters = gmm.predict(tf)

    val out = new PrintStream("data_project\\resultsEM.csv")

    features.zip(clusters).collect().foreach(line => {
      out.println(line._2 + "\t" + line._1.mkString(" "))
    })

  }

}
