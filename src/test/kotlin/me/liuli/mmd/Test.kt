package me.liuli.mmd

import me.liuli.mmd.file.PmxFile
import me.liuli.mmd.file.VmdFile
import me.liuli.mmd.file.parser.PmxParser
import me.liuli.mmd.file.parser.VmdParser
import java.io.File

fun main(args: Array<String>) {
//    testVmd()
//    testPmx()
//    testVpd()
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

private fun testPmx() {
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

    val jfile = File("test_files/pmx/test.pmx")
    var pmxFile = PmxParser.read(jfile.readBytes())

    summary(pmxFile)

    val out = PmxParser.write(pmxFile)
    File("test_files/pmx/out.pmx").writeBytes(out)
    pmxFile = PmxParser.read(out)

    summary(pmxFile)
}


//private fun testVpd() {
//    fun summary(file: VpdFile) {
//        println("--- VPD SUMMARY ---")
//        println("bones: ${file.bones.size}")
//        println("morphs: ${file.morphs.size}")
//    }
//    val vpdFile = File("test_files/test.vpd")
//    val vpd = VpdParser.read(vpdFile.readBytes())
//
//    // read
//    summary(vpd)
//}