package me.liuli.mmd.model.pmx

import me.liuli.mmd.file.PmxFile
import me.liuli.mmd.model.Model
import me.liuli.mmd.model.addition.Material
import me.liuli.mmd.model.addition.SubMesh
import me.liuli.mmd.utils.mat4f
import me.liuli.mmd.utils.multiply
import me.liuli.mmd.utils.translate
import java.io.File
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f
import kotlin.experimental.and

class PmxModel(val file: PmxFile) : Model() {

    private val vertexBoneInfos = mutableListOf<VertexBoneInfo>()
    private val nodes = mutableListOf<PmxNode>()

    init {
        for(vertex in file.vertices) {
            positions.add((vertex.position.clone() as Vector3f).multiply(1f, 1f, -1f))
            normals.add((vertex.normal.clone() as Vector3f).multiply(1f, 1f, -1f))
            uvs.add((vertex.uv.clone() as Vector2f)/*.also { it.y = 1f - it.y }*/)
            val skinning = vertex.skinning // use kotlin smartcast
            vertexBoneInfos.add(if(skinning is PmxFile.Vertex.SkinningSDEF) {
                val sbi = SDEFVertexBoneInfo()

                sbi.boneIndices[0] = skinning.boneIndex1
                sbi.boneIndices[1] = skinning.boneIndex2
                sbi.boneWeight = skinning.weight

                val w1 = 1f - skinning.weight
                sbi.c.x = skinning.c.x
                sbi.c.y = skinning.c.y
                sbi.c.z = skinning.c.z * -1
                val r0 = (skinning.r0.clone() as Vector3f).multiply(1f, 1f, -1f)
                val r1 = (skinning.r1.clone() as Vector3f).multiply(1f, 1f, -1f)
                val rw = Vector3f(r0.x * skinning.weight + r1.x * w1, r0.y * skinning.weight + r1.y * w1, r0.z * skinning.weight + r1.z * w1)
                r0.add(sbi.c)
                r0.sub(rw)
                r1.add(sbi.c)
                r1.sub(rw)
                sbi.r0.x = (sbi.c.x + r0.x) * 0.5f
                sbi.r0.y = (sbi.c.y + r0.y) * 0.5f
                sbi.r0.z = (sbi.c.z + r0.z) * 0.5f

                sbi
            } else {
                val vbi = NormalVertexBoneInfo()

                when(skinning) {
                    is PmxFile.Vertex.SkinningBDEF1 -> {
                        vbi.skinningType = VertexBoneInfo.SkinningType.Weight1
                        vbi.boneIndices[0] = skinning.boneIndex
                    }
                    is PmxFile.Vertex.SkinningBDEF2 -> {
                        vbi.skinningType = VertexBoneInfo.SkinningType.Weight2
                        vbi.boneIndices[0] = skinning.boneIndex1
                        vbi.boneIndices[1] = skinning.boneIndex2
                        vbi.boneWeights[0] = skinning.weight
                        vbi.boneWeights[1] = 1f - skinning.weight
                    }
                    is PmxFile.Vertex.SkinningBDEF4 -> {
                        vbi.skinningType = VertexBoneInfo.SkinningType.Weight4
                        vbi.boneIndices[0] = skinning.boneIndex1
                        vbi.boneIndices[1] = skinning.boneIndex2
                        vbi.boneIndices[2] = skinning.boneIndex3
                        vbi.boneIndices[3] = skinning.boneIndex4
                        vbi.boneWeights[0] = skinning.weight1
                        vbi.boneWeights[1] = skinning.weight2
                        vbi.boneWeights[2] = skinning.weight3
                        vbi.boneWeights[3] = skinning.weight4
                    }
                    is PmxFile.Vertex.SkinningQDEF -> {
                        vbi.skinningType = VertexBoneInfo.SkinningType.DualQuaternion
                        vbi.boneIndices[0] = skinning.boneIndex1
                        vbi.boneIndices[1] = skinning.boneIndex2
                        vbi.boneIndices[2] = skinning.boneIndex3
                        vbi.boneIndices[3] = skinning.boneIndex4
                        vbi.boneWeights[0] = skinning.weight1
                        vbi.boneWeights[1] = skinning.weight2
                        vbi.boneWeights[2] = skinning.weight3
                        vbi.boneWeights[3] = skinning.weight4
                    }
                }

                vbi
            })
        }

        file.indices.forEach { indices.add(it) }

        val textures = mutableListOf<File>()
        file.textures.forEach { textures.add(File(file.dir, it)) }
        var beginIndex = 0
        for (material in file.materials) {
            val mat = Material()
            mat.diffuse = Vector3f(material.diffuse.x, material.diffuse.y, material.diffuse.z)
            mat.alpha = material.diffuse.w
            mat.specularPower = material.specularlity
            mat.specular = material.specular.clone() as Vector3f
            mat.ambient = material.ambient.clone() as Vector3f
            mat.bothFace = material.flag and PmxDrawMode.BOTH_FACE.value != 0
            mat.edgeFlag = material.flag and PmxDrawMode.DRAW_EDGE.value != 0
            mat.groundShadow = material.flag and PmxDrawMode.GROUND_SHADOW.value != 0
            mat.shadowCaster = material.flag and PmxDrawMode.CAST_SELF_SHADOW.value != 0
            mat.shadowReceiver = material.flag and PmxDrawMode.RECEIVE_SELF_SHADOW.value != 0
            mat.edgeSize = material.edgeSize
            mat.edgeColor = material.edgeColor

            if(material.diffuseTextureIndex in 0 until file.textures.size) {
                mat.texture = textures[material.diffuseTextureIndex]
            }
            if(material.toonTextureIndex != -1) {
                if(material.toonMode == PmxFile.Material.ToonMode.COMMON) {
                    mat.toonTexture = File(file.dir, "toon" + material.toonTextureIndex.toString().let { if(it.length == 1) { "0$it" } else { it } } + ".bmp")
                } else if(material.toonMode == PmxFile.Material.ToonMode.SEPARATE) {
                    mat.toonTexture = textures[material.toonTextureIndex]
                }
            }
            if(material.sphereTextureIndex != -1) {
                mat.spTexture = textures[material.sphereTextureIndex]
                mat.spTextureMode = Material.SphereTextureMode.values().find { it.toString() == material.sphereMode.toString() } ?: Material.SphereTextureMode.NONE
            }

            materials.add(mat)

            val mesh = SubMesh()
            mesh.beginIndex = beginIndex
            mesh.vertexCount = material.index
            mesh.materialId = materials.size - 1
            subMeshes.add(mesh)
            beginIndex += material.index
        }

        // pre-index bone list
        for(bone in file.bones) {
            nodes.add(PmxNode().apply { name = bone.name })
        }
        val zeroShort = 0.toShort()
        file.bones.forEachIndexed { index, bone ->
            val node = nodes[index]
            if (bone.parentIndex != -1) {
                val parent = file.bones[bone.parentIndex]
                nodes[bone.parentIndex].child = node
                node.translate.x = bone.position.x - parent.position.x
                node.translate.y = bone.position.y - parent.position.y
                node.translate.z = bone.position.z - parent.position.z
            } else {
                node.translate.x = bone.position.x
                node.translate.y = bone.position.y
                node.translate.z = bone.position.z
            }
            node.translate.z *= -1
            node.global = mat4f(1f).apply { translate(bone.position.x, bone.position.y, bone.position.z * -1f) }
            node.calculateInverseInitTransform()
            node.deformDepth = bone.level
            node.deformAfterPhysics = (bone.flag and PmxBoneFlags.DEFORM_AFTER_PHYSICS.flag) != zeroShort
            node.appendRotate = (bone.flag and PmxBoneFlags.APPEND_ROTATE.flag) != zeroShort
            node.appendTranslate = (bone.flag and PmxBoneFlags.APPEND_TRANSLATE.flag) != zeroShort
            if((node.appendRotate || node.appendTranslate) && (bone.grandParentIndex != -1)) {
                node.appendLocal = (bone.flag and PmxBoneFlags.APPEND_LOCAL.flag) != zeroShort
                node.appendNode = nodes[bone.grandParentIndex]
                node.appendWeight = bone.grantWeight
            }
            node.saveInitialTRS()
        }
        nodes.sortBy { it.deformDepth }
    }
}