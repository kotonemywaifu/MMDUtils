package me.liuli.mmd.utils

import java.nio.ByteBuffer
import java.nio.charset.Charset

/**
 * read a length of bytes from [ByteIterator]
 */
fun ByteIterator.read(length: Int): ByteArray {
    val bytes = ByteArray(length)
    for (i in 0 until length) {
        bytes[i] = this.next()
        if(!this.hasNext()) {
            break
        }
    }
    return bytes
}

/**
 * read a length of bytes from [ByteIterator] to a given [ByteArray]
 */
fun ByteIterator.read(bytes: ByteArray, offset: Int, length: Int): ByteArray {
    for (i in 0 until length) {
        bytes[offset + i] = this.next()
        if (!this.hasNext()) {
            break
        }
    }
    return bytes
}

/**
 * read int from [ByteIterator]
 */
fun ByteIterator.readInt(): Int {
    return ByteBuffer.wrap(this.read(sizeof("int")).reversedArray()).int
}

/**
 * read long from [ByteIterator]
 */
fun ByteIterator.readLong(): Long {
    return ByteBuffer.wrap(this.read(sizeof("long")).reversedArray()).long
}

/**
 * read short from [ByteIterator]
 */
fun ByteIterator.readShort(): Short {
    return ByteBuffer.wrap(this.read(sizeof("short")).reversedArray()).short
}

/**
 * read float from [ByteIterator]
 */
fun ByteIterator.readFloat(): Float {
    return ByteBuffer.wrap(this.read(sizeof("float")).reversedArray()).float
}

/**
 * read double from [ByteIterator]
 */
fun ByteIterator.readDouble(): Double {
    return ByteBuffer.wrap(this.read(sizeof("double")).reversedArray()).double
}

/**
 * read string from [ByteIterator]
 */
fun ByteIterator.readString(length: Int, charset: Charset = Charsets.UTF_8): String {
    return this.read(length).toString(charset)
}

/**
 * read bool from [ByteIterator]
 */
fun ByteIterator.readBool(): Boolean {
    return this.next() == 1.toByte()
}

/**
 * sizeof in c++
 */
fun sizeof(type: String): Int {
    return when(type.lowercase()) {
        "int" -> 4
        "short" -> 2
        "char" -> 1
        "long" -> 8
        "float" -> 4
        "double" -> 8
        else -> throw IllegalArgumentException("unknown type: $type")
    }
}