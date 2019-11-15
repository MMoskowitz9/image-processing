package imageprocessing

import java.awt.image.BufferedImage
import java.net.URL
import java.util.concurrent.LinkedBlockingQueue
import javax.imageio.ImageIO

object ImageProcessor {
    fun hexString(color: Int): String {
        return Integer.toHexString(color).toUpperCase().padStart(8, "0".toCharArray().first()).substring(2)
    }

    fun processColors(url: URL, image: BufferedImage): List<String> {
        val colors = mutableMapOf<String, Int>()

        val width = image.width
        val height = image.height

        (0 until width).forEach { x->
            (0 until height).forEach { y ->
                val rgb = image.getRGB(x, y)
                val hex = hexString(rgb)
                if(!colors.containsKey(hex)) {
                    colors.put(hex, 1)
                }
                else {
                    val count = colors.get(hex)
                            ?: throw IllegalStateException("Expected value for color $hex in map")
                    colors.put(hex, count + 1)
                }
            }
        }
        val sorted = colors.toList().sortedBy { (_, value) -> value }.reversed()
        val result = mutableListOf(url.toString())
        for (i in 0 until minOf(3, sorted.size)) {
            result.add(sorted[i].first)
        }
        return result
    }

    fun processWorker(url: URL, results: LinkedBlockingQueue<List<String>>): Runnable {
        return  Runnable {
            try {
                val image = ImageIO.read(url)
                val colors = ImageProcessor.processColors(url, image)
                results.add(colors)
            }
            catch (ex: Exception) {
                println("Thread ${Thread.currentThread()} encountered an error processing image: ${ex.message}")
            }
        }
    }
}
