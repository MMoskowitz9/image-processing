package imageprocessing

import org.junit.Assert.*
import org.junit.Test
import java.awt.image.BufferedImage
import java.net.URL

class ImageProcessorTests {

    @Test
    fun processColorsTest() {
        val image = BufferedImage(3, 3, 2)
        image.setRGB(0, 0, 10)
        image.setRGB(0, 1, 10)
        image.setRGB(0, 2, 10)
        image.setRGB(1, 0, 9)
        image.setRGB(1, 1, 9)
        image.setRGB(1, 2, 8)
        image.setRGB(2, 0, 8)
        image.setRGB(2, 1, 7)
        image.setRGB(2, 2, 5)

        val expected = setOf("http://www.fakeurl.com", "00000A", "000009", "000008")
        val actual = ImageProcessor.processColors(URL("http://www.fakeurl.com"), image)
        assertEquals(expected, actual.toSet())
    }

    @Test
    fun fewerThanThreeColorsTest() {
        val image = BufferedImage(3, 3, 2)
        image.setRGB(0, 0, 10)
        image.setRGB(0, 1, 10)
        image.setRGB(0, 2, 10)
        image.setRGB(1, 0, 10)
        image.setRGB(1, 1, 10)
        image.setRGB(1, 2, 9)
        image.setRGB(2, 0, 9)
        image.setRGB(2, 1, 9)
        image.setRGB(2, 2, 9)

        val expected = setOf("http://www.fakeurl.com", "00000A", "000009")
        val actual = ImageProcessor.processColors(URL("http://www.fakeurl.com"), image)
        assertEquals(expected, actual.toSet())
    }

}