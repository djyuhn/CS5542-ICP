import java.io.PrintStream

import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.{SparkConf, SparkContext}

/**
  * @author djyuhn
  *         3/14/2019
  */
object kM_Clustering {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "C:\\winutils\\")
    val sparkConf = new SparkConf().setAppName("SparkKMClustering").setMaster("local[*]")
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

    val hashingTF=new HashingTF()

    val tf = hashingTF.transform(features)
    val kMeansModel = KMeans.train(tf,10,1000)

    val WSSSE = kMeansModel.computeCost(tf)
    println("Within Set Sum of Squared Errors = " + WSSSE)

    val clusters = kMeansModel.predict(tf)

    val out = new PrintStream("data_project\\resultsKM.csv")

    features.zip(clusters).collect().foreach(line => {
       out.println(line._2 + "\t" + line._1.mkString(" "))
      })
  }

}
