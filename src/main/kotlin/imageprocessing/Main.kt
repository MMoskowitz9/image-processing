package imageprocessing

import java.io.File
import java.net.URL
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean

fun main(commandLineArgs: Array<String>) {
    println("Starting image processing")
    if (commandLineArgs.size != 2) {
        println("For the first command line argument please pass a path to the input file. For the second please specify " +
                "a path to the desired output file. Paths can be absolute or relative to the home directory of this project.")
        return
    }
    val input = commandLineArgs[0]
    val output = commandLineArgs[1]

    val urls = LinkedBlockingQueue<URL>(1000)
    val results = LinkedBlockingQueue<List<String>>()
    val complete = AtomicBoolean(false)

    val ingestThread = urlIngestThread(File(input), urls)
    ingestThread.start()

    val writerThread = writeResultsThread(results, output, complete)
    writerThread.start()


    val executor = ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors(), 10L, TimeUnit.SECONDS, LinkedBlockingQueue<Runnable>(1000))
    while (true) {
        if (!ingestThread.isAlive && urls.isEmpty()) {
            executor.shutdown()
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)
            complete.compareAndSet(false, true)
            writerThread.join()
            println("Complete")
            break
        }
        val url = urls.poll(1L, TimeUnit.SECONDS)
        url ?: continue
        try {
            // Works alright for our single producer to executor queue, possible race conditions otherwise
            while (executor.queue.remainingCapacity() < 1) {
                continue
            }
            executor.execute(ImageProcessor.processWorker(url, results))
        }
        catch (ex: RejectedExecutionException) {
            // Just in case it's still rejected, try to offer it back to urls
            urls.offer(url, 1L, TimeUnit.MILLISECONDS)
        }
    }
}
