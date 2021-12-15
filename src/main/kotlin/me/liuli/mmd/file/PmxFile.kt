package me.liuli.mmd.file

import me.liuli.mmd.file.parser.PmxParser

/**
 * PMX(Polygon Model eXtended) is a 3d model file format used in the program [MikuMikuDance](https://mikumikudance.jp/).
 */
class PmxFile: InteractiveFile() {
    var name = ""
    var englishName = ""
    var comment = ""
    var englishComment = ""
    val setting = Setting()

    class Setting {
        /**
         * true(0x00): UTF-16LE, false(!=0x00): UTF-8
         */
        var encoding = false
        var uv = 0
        var vertexIndexSize = 0
        var textureIndexSize = 0
        var materialIndexSize = 0
        var boneIndexSize = 0
        var morphIndexSize = 0
        var rigidBodyIndexSize = 0
    }

    class Vertex {
        val position = floatArrayOf(0f, 0f, 0f)
        val normal = floatArrayOf(0f, 0f, 0f)
        val uv = floatArrayOf(0f, 0f)
        val uva = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        var skinning: SkinningType = SkinningBDEF1()
        var edge = 0f

        interface SkinningType {
            val code: Int
        }

        class SkinningBDEF1 : SkinningType {
            override val code: Int = 0
            var boneIndex = 0
        }

        class SkinningBDEF2 : SkinningType {
            override val code: Int = 1
            var boneIndex1 = 0
            var boneIndex2 = 0
            var weight = 0f
        }

        class SkinningBDEF4 : SkinningType {
            override val code: Int = 2
            var boneIndex1 = 0
            var boneIndex2 = 0
            var boneIndex3 = 0
            var boneIndex4 = 0
            var weight1 = 0f
            var weight2 = 0f
            var weight3 = 0f
            var weight4 = 0f
        }

        class SkinningSDEF : SkinningType {
            override val code: Int = 3
            var boneIndex1 = 0
            var boneIndex2 = 0
            var weight = 0f
            var c = floatArrayOf(0f, 0f, 0f)
            var r0 = floatArrayOf(0f, 0f, 0f)
            var r1 = floatArrayOf(0f, 0f, 0f)
        }

        class SkinningQDEF : SkinningType {
            override val code: Int = 4
            var boneIndex1 = 0
            var boneIndex2 = 0
            var boneIndex3 = 0
            var boneIndex4 = 0
            var weight1 = 0f
            var weight2 = 0f
            var weight3 = 0f
            var weight4 = 0f
        }
    }

    override fun read(byteArray: ByteArray) {
        PmxParser.readToInstance(this, byteArray)
    }

    override fun write(): ByteArray {
        return PmxParser.write(this)
    }
}