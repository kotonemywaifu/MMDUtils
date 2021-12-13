package me.liuli.mmd.file.parser

import me.liuli.mmd.file.VmdFile
import me.liuli.mmd.utils.*
import java.io.IOException

object VmdParser : Parser<VmdFile> {
    override fun read(input: ByteArray): VmdFile {
        val file = VmdFile()
        val iterator = input.iterator()

        // magic (文件头)
        val magic = iterator.read(30)
        if(!magic.toString(Charsets.UTF_8).startsWith("Vocaloid Motion Data")) {
            throw IOException("Invalid VMD file header")
        }

        // name of the motion data
        file.name = iterator.readString(20)

        // bone frame count
        val boneFrameNum = iterator.readInt()
        for (i in 0 until boneFrameNum) {
            file.boneFrames.add(readBoneFrame(iterator))
        }

        // face frame count
        val faceFrameNum = iterator.readInt()
        for (i in 0 until faceFrameNum) {
            file.faceFrames.add(readFaceFrame(iterator))
        }

        // camera frame count
        val cameraFrameNum = iterator.readInt()
        for (i in 0 until cameraFrameNum) {
            file.cameraFrames.add(readCameraFrame(iterator))
        }

        // light frame count
        val lightFrameNum = iterator.readInt()
        for (i in 0 until lightFrameNum) {
            file.lightFrames.add(readLightFrame(iterator))
        }

        // unknown2
        iterator.readInt()

        if (iterator.hasNext()) {
            // ik frame count
            val ikFrameNum = iterator.readInt()
            for (i in 0 until ikFrameNum) {
                file.ikFrames.add(readIkFrame(iterator))
            }
        }

        if (iterator.hasNext()) {
            throw IOException("VMD file has unknown data")
        }

        return file
    }

    override fun write(data: VmdFile): ByteArray {
        TODO("Not yet implemented")
    }

    private fun readBoneFrame(iterator: ByteIterator): VmdFile.BoneFrame {
        val boneFrame = VmdFile.BoneFrame()

        boneFrame.name = iterator.readString(15)
        boneFrame.frame = iterator.readInt()
        boneFrame.position[0] = iterator.readFloat()
        boneFrame.position[1] = iterator.readFloat()
        boneFrame.position[2] = iterator.readFloat()
        boneFrame.orientation[0] = iterator.readFloat()
        boneFrame.orientation[1] = iterator.readFloat()
        boneFrame.orientation[2] = iterator.readFloat()
        boneFrame.orientation[3] = iterator.readFloat()
        iterator.read(boneFrame.interpolation, 0, 4 * 4 * 4)

        return boneFrame
    }

    private fun readFaceFrame(iterator: ByteIterator): VmdFile.FaceFrame {
        val faceFrame = VmdFile.FaceFrame()

        faceFrame.name = iterator.readString(15)
        faceFrame.frame = iterator.readInt()
        faceFrame.weight = iterator.readFloat()

        return faceFrame
    }

    private fun readCameraFrame(iterator: ByteIterator): VmdFile.CameraFrame {
        val cameraFrame = VmdFile.CameraFrame()

        cameraFrame.frame = iterator.readInt()
        cameraFrame.distance = iterator.readFloat()
        cameraFrame.position[0] = iterator.readFloat()
        cameraFrame.position[1] = iterator.readFloat()
        cameraFrame.position[2] = iterator.readFloat()
        cameraFrame.orientation[0] = iterator.readFloat()
        cameraFrame.orientation[1] = iterator.readFloat()
        cameraFrame.orientation[2] = iterator.readFloat()
        iterator.read(cameraFrame.interpolation, 0, 24)
        cameraFrame.angle = iterator.readFloat()
        iterator.read(cameraFrame.unknown, 0, 3)

        return cameraFrame
    }

    private fun readLightFrame(iterator: ByteIterator): VmdFile.LightFrame {
        val lightFrame = VmdFile.LightFrame()

        lightFrame.frame = iterator.readInt()
        lightFrame.color[0] = iterator.readFloat()
        lightFrame.color[1] = iterator.readFloat()
        lightFrame.color[2] = iterator.readFloat()
        lightFrame.position[0] = iterator.readFloat()
        lightFrame.position[1] = iterator.readFloat()
        lightFrame.position[2] = iterator.readFloat()

        return lightFrame
    }

    private fun readIkFrame(iterator: ByteIterator): VmdFile.IkFrame {
        val ikFrame = VmdFile.IkFrame()

        ikFrame.frame = iterator.readInt()
        ikFrame.display = iterator.readBool()
        // ik sub value count
        val ikSubValueNum = iterator.readInt()
        for (i in 0 until ikSubValueNum) {
            val ik = VmdFile.IkFrame.Ik()

            ik.name = iterator.readString(20)
            ik.enable = iterator.readBool()

            ikFrame.iks.add(ik)
        }

        return ikFrame
    }
}