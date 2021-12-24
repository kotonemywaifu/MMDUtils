package me.liuli.mmd.file.parser

import me.liuli.mmd.file.VpdFile

object VpdParser : Parser<VpdFile> {
    override fun read(input: ByteArray): VpdFile {
        val vpd = VpdFile()

        readToInstance(vpd, input)

        return vpd
    }

    override fun readToInstance(instance: VpdFile, input: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun write(data: VpdFile): ByteArray {
        TODO("Not yet implemented")
    }

}