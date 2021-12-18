package me.liuli.mmd

import me.liuli.mmd.file.PmxFile
import me.liuli.mmd.file.VmdFile
import me.liuli.mmd.file.parser.PmxParser
import me.liuli.mmd.file.parser.VmdParser
import me.liuli.mmd.utils.readString
import java.io.File
import java.nio.ByteBuffer

fun main(args: Array<String>) {
//    testVmd()
    testPmx()
}

private fun testVmd() {
    fun summary(file: VmdFile) {
        println("--- VMD SUMMARY ---")
        println("name: ${file.name}")
        println("bone frames: ${file.boneFrames.size}")
        println("face frames: ${file.faceFrames.size}")
        println("camera frames: ${file.cameraFrames.size}")
        println("light frames: ${file.lightFrames.size}")
        println("ik frames: ${file.ikFrames.size}")
    }

    val jfile = File("test_files/test.vmd")
    var vmdFile = VmdParser.read(jfile.readBytes())

    // read
    summary(vmdFile)

    // rewrite
    val out = VmdParser.write(vmdFile)
    File("test_files/test_out.vmd").writeBytes(out)
    vmdFile = VmdParser.read(out)
    summary(vmdFile)
}

fun testPmx() {
    fun summary(file: PmxFile) {
        println("--- PMX SUMMARY ---")
        println("name: ${file.name}")
        println("english name: ${file.englishName}")
        println("comment: ${file.comment}")
        println("english comment: ${file.englishComment}")
        println("vertices: ${file.vertices.size}")
        println("indices: ${file.indices.size}")
        println("textures: ${file.textures.size}")
        println("materials: ${file.materials.size}")
        println("bones: ${file.bones.size}")
        println("morphs: ${file.morphs.size}")
        println("display frames: ${file.displayFrames.size}")
        println("rigid bodies: ${file.rigidBodies.size}")
        println("joints: ${file.joints.size}")
        println("soft bodies: ${file.softBodies.size}")
    }

    val jfile = File("test_files/pmx/greyraven_xxi.pmx")
    var pmxFile = PmxParser.read(jfile.readBytes())

    summary(pmxFile)
}