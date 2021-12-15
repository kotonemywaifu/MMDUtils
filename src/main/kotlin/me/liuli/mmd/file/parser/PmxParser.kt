package me.liuli.mmd.file.parser

import me.liuli.mmd.file.PmxFile
import me.liuli.mmd.utils.*
import java.io.ByteArrayOutputStream

object PmxParser : Parser<PmxFile> {
    override fun readToInstance(file: PmxFile, input: ByteArray) {
        val iterator = input.iterator()

        // header
        val header = iterator.readString(4)
        if(header != "PMX ") {
            throw IllegalArgumentException("Invalid PMX file header: $header")
        }

        // check if the version is supported
        val version = iterator.readFloat()
        if(version != 2.0f && version != 2.1f) {
            throw IllegalArgumentException("Unsupported PMX version: $version")
        }

        // read settings
        readSettings(iterator, file.setting)

        // read basic info
        file.name = readString(iterator, file.setting.encoding)
        file.englishName = readString(iterator, file.setting.encoding)
        file.comment = readString(iterator, file.setting.encoding)
        file.englishComment = readString(iterator, file.setting.encoding)

        // read vertices
        val vertexCount = iterator.readInt()
    }

    private fun readSettings(iterator: ByteIterator, setting: PmxFile.Setting) {
        val sections = iterator.next().toInt() - 8

        if(sections < 0) {
            throw IllegalArgumentException("Failed to parse pmx setting part: sections < 8")
        }

        setting.encoding = iterator.next() == 0x00.toByte()
        setting.uv = iterator.next().toInt()
        setting.vertexIndexSize = iterator.next().toInt()
        setting.textureIndexSize = iterator.next().toInt()
        setting.materialIndexSize = iterator.next().toInt()
        setting.boneIndexSize = iterator.next().toInt()
        setting.morphIndexSize = iterator.next().toInt()
        setting.rigidBodyIndexSize = iterator.next().toInt()

        if(sections > 0) {
            for(i in 0 until sections) {
                iterator.next()
            }
        }
    }

    override fun write(data: PmxFile): ByteArray {
        TODO("Not yet implemented")
    }

    /**
     * read string in PMX file from [iterator]
     */
    private fun readString(iterator: ByteIterator, encoding: Boolean): String {
        val length = iterator.readInt()
        if(length <= 0) {
            return ""
        }
        return iterator.read(length).toString(if(encoding) {Charsets.UTF_16LE} else {Charsets.UTF_8})
    }

    /**
     * write string in PMX file to [bos]
     */
    private fun writeString(bos: ByteArrayOutputStream, string: String, encoding: Boolean) {
        if(string.isEmpty()) {
            bos.writeInt(0)
            return
        }
        val bytes = string.toByteArray(if (encoding) { Charsets.UTF_16LE } else { Charsets.UTF_8 })
        bos.writeInt(bytes.size)
        bos.write(bytes)
    }

    override fun read(input: ByteArray): PmxFile {
        return PmxFile().also { readToInstance(it, input) }
    }
}