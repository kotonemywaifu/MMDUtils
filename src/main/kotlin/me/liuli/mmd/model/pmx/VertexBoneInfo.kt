package me.liuli.mmd.model.pmx

import javax.vecmath.Vector3f

abstract class VertexBoneInfo {
    lateinit var skinningType: SkinningType

    enum class SkinningType {
        Weight1,
        Weight2,
        Weight4,
        SDEF,
        DualQuaternion
    }
}

class NormalVertexBoneInfo : VertexBoneInfo() {
    val boneIndices = intArrayOf(0, 0, 0, 0)
    val boneWeights = floatArrayOf(0f, 0f, 0f, 0f)
}

class SDEFVertexBoneInfo : VertexBoneInfo() {
    val boneIndices = intArrayOf(0, 0)
    var boneWeight = 0f
    val c = Vector3f()
    val r0 = Vector3f()
    val r1 = Vector3f()

    init {
        skinningType = SkinningType.SDEF
    }
}