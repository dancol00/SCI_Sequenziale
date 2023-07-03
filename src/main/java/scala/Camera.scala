import it.unisa.di.dif.SCIManager
import it.unisa.di.dif.pattern.Image
import it.unisa.di.dif.pattern.ReferencePattern
import it.unisa.di.dif.pattern.ResidualNoise

import java.io.File
import java.nio.file.Path
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters.SeqHasAsJava

class Camera {
  private var cameraName: String = ""
  private var referencePattern: ReferencePattern = null

  private var referencePatternFiles = ArrayBuffer[Path]()
  private var residualNoiseFiles = ArrayBuffer[File]()

  def this(folder: File) = {
    this()
    cameraName = folder.getName
    val imgFolder = new File(folder.getPath + "/img/")

    assert(imgFolder.exists())

    imgFolder.listFiles().foreach(f => residualNoiseFiles +:= f)
  }

  def computeReferencePattern(sampleAmount: Int = 60): ReferencePattern = {
    if (referencePattern != null) { return referencePattern}
    println("Computing reference pattern for camera " + cameraName)
    for(i <- 0 to sampleAmount) {
      val randomNumber: Int = scala.util.Random.nextInt(residualNoiseFiles.size)
      referencePatternFiles +:= residualNoiseFiles(randomNumber).toPath
      residualNoiseFiles.remove(randomNumber)
    }

    referencePattern = SCIManager.extractReferencePattern(referencePatternFiles.toList.asJava)

    referencePattern
  }

  def computeResidualNoise(f: File): ResidualNoise = {
    var noise: ResidualNoise = null

    println("Computing Residual Noise of image " + f.getName)
    val image = new Image(f)
    noise = SCIManager.extractResidualNoise(image)
    noise
  }

  def computeResidualAndCompare(referencePatterns: scala.List[(String, ReferencePattern)]): scala.List[(String, String, Double)] = { //ReferencePatternTuple
    val compareList = new ArrayBuffer[(String, String, Double)]()
    var i = 0
    for(f <- residualNoiseFiles) {
      val filename: String = f.getName
      val noise: ResidualNoise = computeResidualNoise(f)
      for(rpTuple <- referencePatterns) {
        val (rpCamera, rpVal): (String, ReferencePattern) = rpTuple
        println("Starting comparison between image " + filename + " and Reference Pattern " + rpCamera)
        val compare: Double = SCIManager.compare(rpVal, noise)
        val noiseTuple: (String, String, Double) = (filename, rpCamera, compare)
        println("Correlation value between image " + filename + " and Reference Pattern " + rpCamera + " is " + compare)
        compareList += noiseTuple
      }
      i += 1
    }
    compareList.toList
  }
}
