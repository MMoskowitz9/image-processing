package imageprocessing

import org.apache.commons.io.FileUtils
import org.apache.commons.io.LineIterator
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

fun urlIngestThread(file: File, output: LinkedBlockingQueue<URL>): Thread {
    val ingest = Runnable {
        val it = FileUtils.lineIterator(file, "UTF-8")
        try {
            while (it.hasNext()) {
                val line = it.nextLine()
                try {
                    while (!output.offer(URL(line), Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
                        continue
                    }
                }
                catch (ex: MalformedURLException) {
                    println("Exception occured for url $line: ${ex.message}")
                }
            }
        }
        finally {
            LineIterator.closeQuietly(it)
        }
    }
    return Thread(ingest)
}

fun writeResultsThread(results: LinkedBlockingQueue<List<String>>, output: String, complete: AtomicBoolean): Thread {
    val writeResults = Runnable {
        val file = File(output)
        file.createNewFile()
        BufferedWriter(OutputStreamWriter(FileOutputStream(file))).use { writer ->
            while(true) {
                if (complete.get() && results.isEmpty()) {
                    break
                }
                val res = results.poll(1L, TimeUnit.SECONDS)
                res ?: continue
                val line = "${res.get(0)}, ${res.get(1)}, ${res.get(2)}, ${res.get(3)}"
                writer.write(line)
                writer.newLine()
                writer.flush()
            }
        }
    }
    return Thread(writeResults)
}
