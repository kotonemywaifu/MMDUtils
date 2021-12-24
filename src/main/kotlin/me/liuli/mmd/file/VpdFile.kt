package me.liuli.mmd.file

import me.liuli.mmd.file.parser.VpdParser
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f

/**
 * VPD(Vocaloid Pose Data) is a pose data file format used in the program [MikuMikuDance](https://mikumikudance.jp/).
 */
class VpdFile : InteractiveFile() {
    val bones = mutableListOf<Bone>()
    val morphs = mutableListOf<Morph>()

    class Bone {
        var name = ""
        val translate = Vector3f()
        val quat = Vector4f()
    }

    class Morph {
        var name = ""
        var weight = 0f
    }

    override fun read(byteArray: ByteArray) {
        VpdParser.readToInstance(this, byteArray)
    }

    override fun write(): ByteArray {
        return VpdParser.write(this)
    }
}