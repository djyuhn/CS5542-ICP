import java.io.PrintStream

/**
  * @author djyuhn
  *         3/14/2019
  */
object EM_Clustering {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "C:\\winutils\\")
    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")
    val sc = new SparkContext(sparkConf)

    val features =sc.textFile("data\\Flickr8k.token.txt")
      .map(f=>{
        val str=f.replaceAll(",","")
        val ff=f.split(" ")
        ff.drop(1).toSeq
      })
    val hashingTF=new HashingTF(100)

    val tf=hashingTF.transform(features)

    val idf = new IDF().fit(tf)
    val tfidf = idf.transform(tf)


    // Cluster the data into two classes using GaussianMixture
    val gmm = new GaussianMixture().setK(10).run(tf)

    val clusters=gmm.predict(tf)

    val out=new PrintStream("data\\resultsEM.csv")

    features.zip(clusters).collect().foreach(f=>{
      out.println(f._1.mkString(" ")+","+f._2)
    })

  }

}
