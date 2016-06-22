package io.hydrosphere.mist

//import io.hydrosphere.mist.MistConfig
import org.apache.spark.{SparkConf, SparkContext}

/** Builds spark contexts with necessary settings */
private[mist] object ContextBuilder {

  /** Build contexts with namespace
    *
    * @param name namespace
    * @return [[ContextWrapper]] with prepared context
    */
  def namedSparkContext(name: String): ContextWrapper = {

    val sparkConf = new SparkConf()
      .setMaster("local[*]"/*MistConfig.Spark.master*/) // TODO add mistconfig
      .setAppName(name)
      .set("spark.driver.allowMultipleContexts", "true")

    /*val sparkConfSettings = MistConfig.Contexts.sparkConf(name)

    for (keyValue: List[String] <- sparkConfSettings) {
      sparkConf.set(keyValue.head, keyValue(1))
    }
    */ //TODO add conf
    NamedContextWrapper(new SparkContext(sparkConf), name)
  }
}