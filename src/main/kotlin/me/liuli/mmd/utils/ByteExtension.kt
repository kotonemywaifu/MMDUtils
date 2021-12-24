package me.liuli.mmd.utils

import java.awt.Color
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f

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
fun ByteIterator.readString(length: Int, noZero: Boolean = true): String {
    val sb = StringBuilder()
    this.read(length).forEach {
        it.toInt().also { charcode ->
            if(charcode != 0 || !noZero) {
                sb.append(charcode.toChar())
            }
        }
    }
    return sb.toString()
}

/**
 * read bool from [ByteIterator]
 */
fun ByteIterator.readBool(): Boolean {
    return this.next() == 1.toByte()
}

/**
 * read [Vector3f] from [ByteIterator]
 */
fun ByteIterator.readVector3f(vector: Vector3f) {
    vector.x = this.readFloat()
    vector.y = this.readFloat()
    vector.z = this.readFloat()
}

/**
 * read [Vector4f] from [ByteIterator]
 */
fun ByteIterator.readVector4f(vector: Vector4f) {
    vector.x = this.readFloat()
    vector.y = this.readFloat()
    vector.z = this.readFloat()
    vector.w = this.readFloat()
}

/**
 * read color3f from [ByteIterator]
 */
fun ByteIterator.readColor3f(): Color {
    return Color(this.readFloat().coerceAtLeast(1f).coerceAtLeast(0f)
        , this.readFloat().coerceAtLeast(1f).coerceAtLeast(0f)
        , this.readFloat().coerceAtLeast(1f).coerceAtLeast(0f))
}

/**
 * read color4f from [ByteIterator]
 */
fun ByteIterator.readColor4f(): Color {
    return Color(this.readFloat().coerceAtLeast(1f).coerceAtLeast(0f)
        , this.readFloat().coerceAtLeast(1f).coerceAtLeast(0f)
        , this.readFloat().coerceAtLeast(1f).coerceAtLeast(0f)
        , this.readFloat().coerceAtLeast(1f).coerceAtLeast(0f))
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

/**
 * write single byte to [ByteArrayOutputStream]
 */
fun ByteArrayOutputStream.write(byte: Byte) {
    this.write(byteArrayOf(byte))
}

/**
 * write int to [ByteArrayOutputStream]
 */
fun ByteArrayOutputStream.writeInt(value: Int) {
    this.write(ByteBuffer.allocate(sizeof("int")).putInt(value).array().reversedArray())
}

/**
 * write long to [ByteArrayOutputStream]
 */
fun ByteArrayOutputStream.writeLong(value: Long) {
    this.write(ByteBuffer.allocate(sizeof("long")).putLong(value).array().reversedArray())
}

/**
 * write short to [ByteArrayOutputStream]
 */
fun ByteArrayOutputStream.writeShort(value: Short) {
    this.write(ByteBuffer.allocate(sizeof("short")).putShort(value).array().reversedArray())
}

/**
 * write float to [ByteArrayOutputStream]
 */
fun ByteArrayOutputStream.writeFloat(value: Float) {
    this.write(ByteBuffer.allocate(sizeof("float")).putFloat(value).array().reversedArray())
}

/**
 * write double to [ByteArrayOutputStream]
 */
fun ByteArrayOutputStream.writeDouble(value: Double) {
    this.write(ByteBuffer.allocate(sizeof("double")).putDouble(value).array().reversedArray())
}

/**
 * write bool to [ByteArrayOutputStream]
 */
fun ByteArrayOutputStream.writeBool(value: Boolean) {
    this.write(byteArrayOf(if (value) 1.toByte() else 0.toByte()))
}

/**
 * write bytes to [ByteArrayOutputStream]
 */
fun ByteArrayOutputStream.writeLimited(value: ByteArray, size: Int) {
    if(value.size < size) {
        this.write(value)
        for (i in 0 until size - value.size) {
            this.write(0x00)
        }
    } else if (value.size > size) {
        this.write(value, 0, size)
    } else {
        this.write(value)
    }
}

/**
 * write string to [ByteArray] without any encoding
 */
fun String.toStandardByteArray(): ByteArray {
    val bos = ByteArrayOutputStream()
    for (c in this) {
        bos.write(byteArrayOf(c.code.toByte()))
    }
    return bos.toByteArray()
}

/**
 * write [Vector3f] to [ByteArrayOutputStream]
 */
fun ByteArrayOutputStream.writeVector3f(vector: Vector3f) {
    this.writeFloat(vector.x)
    this.writeFloat(vector.y)
    this.writeFloat(vector.z)
}

/**
 * write [Vector4f] to [ByteArrayOutputStream]
 */
fun ByteArrayOutputStream.writeVector4f(vector: Vector4f) {
    this.writeFloat(vector.x)
    this.writeFloat(vector.y)
    this.writeFloat(vector.z)
    this.writeFloat(vector.w)
}

/**
 * write color3f to [ByteArrayOutputStream]
 */
fun ByteArrayOutputStream.writeColor3f(color: Color) {
    this.writeFloat(color.red / 255f)
    this.writeFloat(color.green / 255f)
    this.writeFloat(color.blue / 255f)
}

/**
 * write color4f to [ByteArrayOutputStream]
 */
fun ByteArrayOutputStream.writeColor4f(color: Color) {
    this.writeFloat(color.red / 255f)
    this.writeFloat(color.green / 255f)
    this.writeFloat(color.blue / 255f)
    this.writeFloat(color.alpha / 255f)
}