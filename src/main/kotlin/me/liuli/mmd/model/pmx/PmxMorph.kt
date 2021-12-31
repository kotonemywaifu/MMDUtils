package me.liuli.mmd.model.pmx

import me.liuli.mmd.file.PmxFile
import me.liuli.mmd.model.addition.Morph
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f

class PmxMorph : Morph() {
    var type = MorphType.NONE
    var dataIndex = 0

    enum class MorphType {
        NONE,
        POSITION,
        UV,
        MATERIAL,
        BONE,
        GROUP
    }

    interface MorphData {
        val type: MorphType
    }

    class PositionMorphData : MorphData {
        override val type = MorphType.POSITION
        var positions = mutableListOf<PmxFile.Morph.VertexOffset>()
    }

    class UVMorphData : MorphData {
        override val type = MorphType.UV
        var uvs = mutableListOf<PmxFile.Morph.UvOffset>()
    }

    class MaterialMorphData : MorphData {
        override val type = MorphType.MATERIAL
        var materials = mutableListOf<PmxFile.Morph.MaterialOffset>()
    }

    class BoneMorphData : MorphData {
        override val type = MorphType.BONE
        var bones = mutableListOf<BoneMorph>()

        class BoneMorph {
            lateinit var node: PmxNode
            val position = Vector3f()
            val rotation = Vector4f()
        }
    }

    class GroupMorphData : MorphData {
        override val type = MorphType.GROUP
        var groups = mutableListOf<PmxFile.Morph.GroupOffset>()
    }
}

class PmxMorphManager {
    val morphs = mutableListOf<PmxMorph>()
    val positionMorphs = mutableListOf<PmxMorph.PositionMorphData>()
    val uvMorphs = mutableListOf<PmxMorph.UVMorphData>()
    val materialMorphs = mutableListOf<PmxMorph.MaterialMorphData>()
    val boneMorphs = mutableListOf<PmxMorph.BoneMorphData>()
    val groupMorphs = mutableListOf<PmxMorph.GroupMorphData>()

    fun getSize(type: PmxMorph.MorphType): Int {
        return when (type) {
            PmxMorph.MorphType.POSITION -> positionMorphs.size
            PmxMorph.MorphType.UV -> uvMorphs.size
            PmxMorph.MorphType.MATERIAL -> materialMorphs.size
            PmxMorph.MorphType.BONE -> boneMorphs.size
            PmxMorph.MorphType.GROUP -> groupMorphs.size
            else -> throw IllegalArgumentException("Unsupported morph type: $type")
        }
    }

    operator fun get(morph: PmxMorph): PmxMorph.MorphData {
        return when(morph.type) {
            PmxMorph.MorphType.POSITION -> positionMorphs[morph.dataIndex]
            PmxMorph.MorphType.UV -> uvMorphs[morph.dataIndex]
            PmxMorph.MorphType.MATERIAL -> materialMorphs[morph.dataIndex]
            PmxMorph.MorphType.BONE -> boneMorphs[morph.dataIndex]
            PmxMorph.MorphType.GROUP -> groupMorphs[morph.dataIndex]
            else -> throw IllegalArgumentException("Unsupported morph type: ${morph.type}")
        }
    }

    fun add(morphData: PmxMorph.MorphData) {
        when (morphData.type) {
            PmxMorph.MorphType.POSITION -> positionMorphs.add(morphData as PmxMorph.PositionMorphData)
            PmxMorph.MorphType.UV -> uvMorphs.add(morphData as PmxMorph.UVMorphData)
            PmxMorph.MorphType.MATERIAL -> materialMorphs.add(morphData as PmxMorph.MaterialMorphData)
            PmxMorph.MorphType.BONE -> boneMorphs.add(morphData as PmxMorph.BoneMorphData)
            PmxMorph.MorphType.GROUP -> groupMorphs.add(morphData as PmxMorph.GroupMorphData)
        }
    }

    fun clear() {
        morphs.clear()
        positionMorphs.clear()
        uvMorphs.clear()
        materialMorphs.clear()
        boneMorphs.clear()
        groupMorphs.clear()
    }
}