package me.liuli.mmd.file

import me.liuli.mmd.file.parser.VmdParser

/**
 * VMD(Vocaloid Motion Data) is a motion data file format used in the program [MikuMikuDance](https://mikumikudance.jp/).
 */
class VmdFile : InteractiveFile() {
    var name = "Empty File"
    val boneFrames = mutableListOf<BoneFrame>()
    val faceFrames = mutableListOf<FaceFrame>()
    val cameraFrames = mutableListOf<CameraFrame>()
    val lightFrames = mutableListOf<LightFrame>()
    val ikFrames = mutableListOf<IkFrame>()

    class BoneFrame {
        // 骨骼名 (ボーン名)
        var name = "Empty Bone"
        // 帧数 (フレーム番号)
        var frame = 0
        // 位置
        val position = arrayOf(0f, 0f, 0f)
        // 旋转 (回転)
        val orientation = arrayOf(0f, 0f, 0f, 0f)
        // 插值曲线 (補間曲線)
        var interpolation = ByteArray(4 * 4 * 4)
    }

    class FaceFrame {
        // 表情名
        var name = "Empty Face"
        // 表情重量 (表情の重み)
        var weight = 0f
        // 帧数 (フレーム番号)
        var frame = 0
    }

    class CameraFrame {
        // 帧数 (フレーム番号)
        var frame = 0
        // 距离 (距離)
        var distance = 0f
        // 位置
        val position = arrayOf(0f, 0f, 0f)
        // 旋转 (回転)
        val orientation = arrayOf(0f, 0f, 0f, 0f)
        // 插值曲线 (補間曲線)
        var interpolation = ByteArray(6 * 4)
        // 可视角度 (視野角)
        var angle = 0f
        // unknown
        var unknown = ByteArray(3)
    }

    class LightFrame {
        // 帧数 (フレーム番号)
        var frame = 0
        // 颜色 (色)
        val color = arrayOf(0f, 0f, 0f)
        // 位置
        val position = arrayOf(0f, 0f, 0f)
    }

    class IkFrame {
        // 帧数 (フレーム番号)
        var frame = 0
        var display = false
        val iks = mutableListOf<Ik>()

        class Ik {
            // name
            var name = "Empty Ik"
            // enable
            var enable = false
        }
    }

    override fun read(byteArray: ByteArray) {
        VmdParser.readToInstance(this, byteArray)
    }

    override fun write(): ByteArray {
        return VmdParser.write(this)
    }
}