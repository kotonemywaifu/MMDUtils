package me.liuli.mmd

import me.liuli.mmd.file.PmxFile
import me.liuli.mmd.file.VmdFile
import me.liuli.mmd.file.parser.PmxParser
import me.liuli.mmd.file.parser.VmdParser
import me.liuli.mmd.model.pmx.PmxModel
import me.liuli.mmd.utils.vector.inverse
import me.liuli.mmd.utils.vector.mat4f
import me.liuli.mmd.utils.vector.operator.times
import me.liuli.mmd.utils.vector.translate
import java.io.File
import javax.vecmath.Matrix4f
import kotlin.math.abs

fun main(args: Array<String>) {
//    var time = System.currentTimeMillis()
//    val jfile = File("test_files/pmx/test.pmx")
//    val pmxFile = PmxParser.read(jfile.readBytes())
//    pmxFile.dir = jfile.parentFile
//    println("Time cost(READ): ${System.currentTimeMillis() - time}ms")
//
//    time = System.currentTimeMillis()
//    val model = PmxModel(pmxFile)
//    println("Time cost(SERIALIZATION): ${System.currentTimeMillis() - time}ms")
//
//    time = System.currentTimeMillis()
//    model.initAnimation()
//    println("Time cost(INITIALIZE): ${System.currentTimeMillis() - time}ms")
//
//    time = System.currentTimeMillis()
////    model.beginAnimation()
////    model.updateAllAnimation(1f)
////    model.endAnimation()
//    model.update()
//    println("Time cost(UPDATE): ${System.currentTimeMillis() - time}ms")
//    model.updatePositions.filter { ille(it.x, 50f) || ille(it.y, 50f) || ille(it.z, 50f) }.forEach { println(it) }

//    testVmd()
//    testPmx()
//    testVpd()
}

private fun ille(float: Float, r: Float): Boolean {
    return abs(float) > r
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