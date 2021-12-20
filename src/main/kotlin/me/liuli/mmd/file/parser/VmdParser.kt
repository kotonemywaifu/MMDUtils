package me.liuli.mmd.file.parser

import me.liuli.mmd.file.VmdFile
import me.liuli.mmd.utils.*
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.io.IOException

object VmdParser : Parser<VmdFile> {
    override fun readToInstance(file: VmdFile, input: ByteArray) {
        val iterator = input.iterator()

        // magic (文件头)
        val magic = iterator.readString(30)
        if(!magic.startsWith("Vocaloid Motion Data")) {
            throw IOException("Invalid VMD file header: $magic")
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
    }

    private fun readBoneFrame(iterator: ByteIterator): VmdFile.BoneFrame {
        val boneFrame = VmdFile.BoneFrame()

        boneFrame.name = iterator.readString(15)
        boneFrame.frame = iterator.readInt()
        iterator.readVector3f(boneFrame.position)
        iterator.readVector4f(boneFrame.orientation)
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
        iterator.readVector3f(cameraFrame.position)
        iterator.readVector3f(cameraFrame.orientation)
        iterator.read(cameraFrame.interpolation, 0, 24)
        cameraFrame.angle = iterator.readFloat()
        iterator.read(cameraFrame.unknown, 0, 3)

        return cameraFrame
    }

    private fun readLightFrame(iterator: ByteIterator): VmdFile.LightFrame {
        val lightFrame = VmdFile.LightFrame()

        lightFrame.frame = iterator.readInt()
        lightFrame.color = iterator.readColor3f()
        iterator.readVector3f(lightFrame.position)

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

    override fun write(data: VmdFile): ByteArray {
        val bos = ByteArrayOutputStream()

        // header
        bos.writeLimited("Vocaloid Motion Data 0002".toStandardByteArray(),  30)

        // model name
        bos.writeLimited(data.name.toStandardByteArray(), 20)

        // bone frames
        bos.writeInt(data.boneFrames.size)
        for (boneFrame in data.boneFrames) {
            writeBoneFrame(bos, boneFrame)
        }

        // face frames
        bos.writeInt(data.faceFrames.size)
        for (faceFrame in data.faceFrames) {
            writeFaceFrame(bos, faceFrame)
        }

        // camera frames
        bos.writeInt(data.cameraFrames.size)
        for (cameraFrame in data.cameraFrames) {
            writeCameraFrame(bos, cameraFrame)
        }

        // light frames
        bos.writeInt(data.lightFrames.size)
        for (lightFrame in data.lightFrames) {
            writeLightFrame(bos, lightFrame)
        }

        // self shadow datas
        bos.writeInt(0)

        // ik frames
        bos.writeInt(data.ikFrames.size)
        for (ikFrame in data.ikFrames) {
            writeIkFrame(bos, ikFrame)
        }

        return bos.toByteArray()
    }

    private fun writeBoneFrame(bos: ByteArrayOutputStream, boneFrame: VmdFile.BoneFrame) {
        bos.writeLimited(boneFrame.name.toStandardByteArray(), 15)
        bos.writeInt(boneFrame.frame)
        bos.writeVector3f(boneFrame.position)
        bos.writeVector4f(boneFrame.orientation)
        bos.write(boneFrame.interpolation)
    }

    private fun writeFaceFrame(bos: ByteArrayOutputStream, faceFrame: VmdFile.FaceFrame) {
        bos.writeLimited(faceFrame.name.toStandardByteArray(), 15)
        bos.writeInt(faceFrame.frame)
        bos.writeFloat(faceFrame.weight)
    }

    private fun writeCameraFrame(bos: ByteArrayOutputStream, cameraFrame: VmdFile.CameraFrame) {
        bos.writeInt(cameraFrame.frame)
        bos.writeFloat(cameraFrame.distance)
        bos.writeVector3f(cameraFrame.position)
        bos.writeVector3f(cameraFrame.orientation)
        bos.write(cameraFrame.interpolation)
        bos.writeFloat(cameraFrame.angle)
        bos.write(cameraFrame.unknown)
    }

    private fun writeLightFrame(bos: ByteArrayOutputStream, lightFrame: VmdFile.LightFrame) {
        bos.writeInt(lightFrame.frame)
        bos.writeColor3f(lightFrame.color)
        bos.writeVector3f(lightFrame.position)
    }

    private fun writeIkFrame(bos: ByteArrayOutputStream, ikFrame: VmdFile.IkFrame) {
        bos.writeInt(ikFrame.frame)
        bos.writeBool(ikFrame.display)
        bos.writeInt(ikFrame.iks.size)
        for (ik in ikFrame.iks) {
            bos.writeLimited(ik.name.toStandardByteArray(), 20)
            bos.writeBool(ik.enable)
        }
    }

    override fun read(input: ByteArray): VmdFile {
        return VmdFile().also { readToInstance(it, input) }
    }
}