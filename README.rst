===============
Image Processor
===============

This tool takes a file containing a list of links to images as input (with each link separated by a newline) and for
each url writes the 3 most common colors in the image to a file.

JDK
===

This software is written in Kotlin and runs on the JVM. This implementation has been tested on JDK 1.8.

Usage
=====

Clone the repo::

    git clone git@github.com:MMoskowitz9/image-processing.git

Change to the project's root directory and build::

    ./gradlew shadowjar

Run::

    java -jar build/libs/imageprocessing-1.0-all.jar <path to input file> <path to output file>

The input and output file paths can be absolute or relative to the project's root directory.

High level description of what's going on here
==============================================

The program starts an ingest thread that reads lines from the input file containing urls and adds them to a
concurrent queue. If the queue reaches max size (1000) the thread will block until there is room to add more. This may
save us from exhausting heap space in the event of a huge input file on a system with limited resources.

We spawn a thread pool and pull the urls from the queue mentioned above, passing runnables to the thread pool to perform
the actual image processing. The thread pool's queue also has a limited size to prevent heap space issues on huge
inputs.

The jobs performing the image processing pass the result from each image to a results queue and those values are
eventually written to the output file.

Possible Improvements
=====================

An incomplete list of ways I think this could be improved if developed further, in no particular order.

Thorough and configurable logging.

More robust exception handling in processWorker.

Tracking progress of program so it can resume in the event of an interruption.

More unit testing.

Since a decent amount of this task seems IO bounded (fetching the images) we may get a performance bump from using
Kotlin's coroutines library instead of a raw thread pool.

More performance profiling, possibly make better use of system resources.

Better validation of arguments.

Also, I've taken the word `prevalent` in the instructions to be synonymous with `commonly occurring`.