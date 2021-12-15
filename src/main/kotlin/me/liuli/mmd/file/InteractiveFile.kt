package me.liuli.mmd.file

import java.io.File

abstract class InteractiveFile {
    /**
     * read data from [byteArray]
     */
    abstract fun read(byteArray: ByteArray)

    /**
     * write data to [ByteArray]
     * @return the result
     */
    abstract fun write(): ByteArray

    /**
     * read data from [file]
     */
    open fun readFromFile(file: File) {
        val bytes = file.readBytes()
        read(bytes)
    }

    /**
     * write data to [file]
     */
    open fun writeToFile(file: File) {
        val bytes = write()
        file.writeBytes(bytes)
    }
}