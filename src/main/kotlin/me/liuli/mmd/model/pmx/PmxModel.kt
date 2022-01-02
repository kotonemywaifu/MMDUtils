package me.liuli.mmd.model.pmx

import com.bulletphysics.collision.dispatch.CollisionFlags
import com.bulletphysics.collision.shapes.BoxShape
import com.bulletphysics.collision.shapes.CapsuleShape
import com.bulletphysics.collision.shapes.SphereShape
import com.bulletphysics.dynamics.RigidBody
import com.bulletphysics.dynamics.RigidBodyConstructionInfo
import com.bulletphysics.dynamics.constraintsolver.Generic6DofConstraint
import com.bulletphysics.linearmath.Transform
import me.liuli.mmd.file.PmxFile
import me.liuli.mmd.model.Model
import me.liuli.mmd.model.addition.*
import me.liuli.mmd.model.addition.physics.*
import me.liuli.mmd.utils.*
import java.io.File
import javax.vecmath.Matrix4f
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f
import kotlin.experimental.and

class PmxModel(val file: PmxFile) : Model() {

    private val vertexBoneInfos = mutableListOf<VertexBoneInfo>()
    override val nodes = mutableListOf<PmxNode>()
    override val ikSolvers = mutableListOf<IKSolver>()
    private val morphManager = PmxMorphManager()
    private val physicsManager = PhysicsManager()

    override val morphs: List<Morph>
        get() = morphManager.morphs

    private val morphPositions = mutableListOf<Vector3f>()
    private val morphUVs = mutableListOf<Vector4f>()

    init {
        for(vertex in file.vertices) {
            positions.add(Vector3f(vertex.position).mul(1f, 1f, -1f))
            normals.add(Vector3f(vertex.normal).mul(1f, 1f, -1f))
            uvs.add(Vector2f(vertex.uv)/*.also { it.y = 1f - it.y }*/)
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
                val r0 = Vector3f(skinning.r0).mul(1f, 1f, -1f)
                val r1 = Vector3f(skinning.r1).mul(1f, 1f, -1f)
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
            mat.specular = Vector3f(material.specular)
            mat.ambient = Vector3f(material.ambient)
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

        // node
        for(bone in file.bones) {
            this.nodes.add(PmxNode().apply { name = bone.name })
        }
        val zeroShort = 0.toShort()
        file.bones.forEachIndexed { index, bone ->
            val node = this.nodes[index]
            if (bone.parentIndex != -1) {
                val parentBone = file.bones[bone.parentIndex]
                this.nodes[bone.parentIndex].addChild(node)
                node.translate.x = bone.position.x - parentBone.position.x
                node.translate.y = bone.position.y - parentBone.position.y
                node.translate.z = bone.position.z - parentBone.position.z
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
            node.isAppendRotate = (bone.flag and PmxBoneFlags.APPEND_ROTATE.flag) != zeroShort
            node.isAppendTranslate = (bone.flag and PmxBoneFlags.APPEND_TRANSLATE.flag) != zeroShort
            if((node.isAppendRotate || node.isAppendTranslate) && (bone.grandParentIndex != -1)) {
                node.isAppendLocal = (bone.flag and PmxBoneFlags.APPEND_LOCAL.flag) != zeroShort
                node.appendNode = this.nodes[bone.grandParentIndex]
                node.appendWeight = bone.grantWeight
            }
            node.saveInitialTRS()
        }
        this.nodes.sortBy { it.deformDepth }

        // IK
        file.bones.forEachIndexed { index, bone ->
            if (bone.flag and PmxBoneFlags.IK.flag != zeroShort) {
                val solver = IKSolver()
                val node = this.nodes[index]
                solver.node = node
                node.solver = solver
                solver.target = this.nodes[bone.ikTargetBoneIndex]

                for(ikLink in bone.ikLinks) {
                    val linkNode = this.nodes[ikLink.linkTarget]
                    val chain = IKSolver.IKChain()
                    chain.node = linkNode
                    if(ikLink.angleLock == 0) {
                        chain.enableAxisLimit = false
                    } else {
                        chain.enableAxisLimit = true
                        ikLink.minRadian.set(chain.limitMin)
                        ikLink.maxRadian.set(chain.limitMax)
                    }
                    solver.chains.add(chain)
                    linkNode.enableIk = true
                }
                solver.iterateCount = bone.ikLoop
                solver.limitAngle = bone.ikLoopAngleLimit
                ikSolvers.add(solver)
            }
        }

        // morph
        for (pmxMorph in file.morphs) {
            val morph = PmxMorph()
            morph.name = pmxMorph.name
            when(pmxMorph.type) {
                PmxFile.Morph.Type.VERTEX -> {
                    morph.type = PmxMorph.MorphType.POSITION
                    morph.dataIndex = morphManager.getSize(morph.type)
                    val data = PmxMorph.PositionMorphData()
                    for (offset in pmxMorph.offsets) {
                        val newOffset = PmxFile.Morph.VertexOffset()
                        val originalOffset = offset as PmxFile.Morph.VertexOffset
                        newOffset.index = originalOffset.index
                        originalOffset.position.set(newOffset.position)
                        newOffset.position.z *= -1
                        data.positions.add(newOffset)
                    }
                    morphManager.add(data)
                }
                PmxFile.Morph.Type.UV -> {
                    morph.type = PmxMorph.MorphType.UV
                    morph.dataIndex = morphManager.getSize(morph.type)
                    val data = PmxMorph.UVMorphData()
                    for (offset in pmxMorph.offsets) {
                        data.uvs.add(offset as PmxFile.Morph.UvOffset)
                    }
                    morphManager.add(data)
                }
                PmxFile.Morph.Type.MATERIAL -> {
                    morph.type = PmxMorph.MorphType.MATERIAL
                    morph.dataIndex = morphManager.getSize(morph.type)
                    val data = PmxMorph.MaterialMorphData()
                    for (offset in pmxMorph.offsets) {
                        data.materials.add(offset as PmxFile.Morph.MaterialOffset)
                    }
                    morphManager.add(data)
                }
                PmxFile.Morph.Type.GROUP -> {
                    morph.type = PmxMorph.MorphType.GROUP
                    morph.dataIndex = morphManager.getSize(morph.type)
                    val data = PmxMorph.GroupMorphData()
                    for (offset in pmxMorph.offsets) {
                        data.groups.add(offset as PmxFile.Morph.GroupOffset)
                    }
                    morphManager.add(data)
                }
                PmxFile.Morph.Type.BONE -> {
                    morph.type = PmxMorph.MorphType.BONE
                    morph.dataIndex = morphManager.getSize(morph.type)
                    val data = PmxMorph.BoneMorphData()
                    for (offset in pmxMorph.offsets) {
                        val boneOffset = offset as PmxFile.Morph.BoneOffset
                        val boneMorph = PmxMorph.BoneMorphData.BoneMorph()
                        boneMorph.node = this.nodes[boneOffset.index]
                        boneOffset.translation.set(boneMorph.position)
                        boneMorph.position.z *= -1
                        boneOffset.rotation.set(boneMorph.rotation)
                        boneMorph.rotation.z *= -1
                        data.bones.add(boneMorph)
                    }
                    morphManager.add(data)
                }
                else -> continue // morph type is not supported
            }
            morphManager.morphs.add(morph)
        }
        // Check whether Group Morph infinite loop.
        for (morph in morphManager.morphs) {
            if (morph.type == PmxMorph.MorphType.GROUP) {
                val groupMorphStack = mutableListOf<Int>()
                val groupMorphData = morphManager[morph] as PmxMorph.GroupMorphData
                for (groupMorph in groupMorphData.groups) {
                    if (groupMorphStack.contains(groupMorph.index)) {
                        groupMorph.index = -1
                    } else {
                        groupMorphStack.add(groupMorph.index)
                    }
                }
            }
        }

        // Physics
        for (pmxRigidBody in file.rigidBodies) {
            val node = if(pmxRigidBody.targetBone != -1) { this.nodes[pmxRigidBody.targetBone] } else { null }
            val shape = when (pmxRigidBody.shape) {
                PmxFile.RigidBody.Shape.SPHERE -> SphereShape(pmxRigidBody.size.x)
                PmxFile.RigidBody.Shape.BOX -> BoxShape(pmxRigidBody.size)
                PmxFile.RigidBody.Shape.CAPSULE -> CapsuleShape(pmxRigidBody.size.x, pmxRigidBody.size.y)
            }

            val inertia = Vector3f()
            val mass = if(pmxRigidBody.op == PmxFile.RigidBody.Operation.STATIC) { 0f } else { pmxRigidBody.mass }
            if (mass != 0f) {
                shape.calculateLocalInertia(mass, inertia)
            }

            val rx = mat4f(1f).rotate(pmxRigidBody.orientation.x, Vector3f(1f, 0f, 0f))
            val ry = mat4f(1f).rotate(pmxRigidBody.orientation.y, Vector3f(0f, 1f, 0f))
            val rz = mat4f(1f).rotate(pmxRigidBody.orientation.z, Vector3f(0f, 0f, 1f))
            val rotMat = rx.apply { mul(ry) }.apply { mul(rz) }
            val transMat = mat4f(1f).translate(pmxRigidBody.position)
            val rbMat = transMat.apply { mul(rotMat) }

            val kinematicNode = node ?: this.nodes.first()
            val offsetMat = Matrix4f(kinematicNode.global).inverse().apply { mul(rbMat) }
            var activeMotionState: MMDMotionState? = null
            val kinematicMotionState = KinematicMotionState(kinematicNode, offsetMat)
            val motionState = if (pmxRigidBody.op == PmxFile.RigidBody.Operation.STATIC) {
                kinematicMotionState
            } else {
                if (node != null) {
                    if (pmxRigidBody.op == PmxFile.RigidBody.Operation.DYNAMIC) {
                        activeMotionState = DynamicMotionState(kinematicNode, offsetMat)
                        activeMotionState
                    } else {
                        activeMotionState = DynamicAndBoneMergeMotionState(kinematicNode, offsetMat)
                        activeMotionState
                    }
                } else {
                    activeMotionState = DefaultMotionState(offsetMat)
                    activeMotionState
                }
            }

            val rbInfo = RigidBodyConstructionInfo(mass, motionState, shape, inertia)
            rbInfo.linearDamping = pmxRigidBody.moveAttenuation
            rbInfo.angularDamping = pmxRigidBody.rotationAttenuation
            rbInfo.restitution = pmxRigidBody.repulsion
            rbInfo.friction = pmxRigidBody.friction
            rbInfo.additionalDamping = true

            val btRigidBody = RigidBody(rbInfo)
            btRigidBody.setSleepingThresholds(0.1f, Math.toRadians(0.1).toFloat())
            btRigidBody.activationState = RigidBody.DISABLE_DEACTIVATION
            if(pmxRigidBody.op == PmxFile.RigidBody.Operation.DYNAMIC) {
                btRigidBody.collisionFlags = btRigidBody.collisionFlags or CollisionFlags.KINEMATIC_OBJECT
            }
            physicsManager.addRigidBody(MMDRigidBody(btRigidBody, activeMotionState, kinematicMotionState,
                MMDRigidBody.Type.values()[pmxRigidBody.op.code],
                pmxRigidBody.group, pmxRigidBody.mask, node, pmxRigidBody.name))
        }
        for(pmxJoint in file.joints) {
            if(pmxJoint.rigidBody1 == -1 || pmxJoint.rigidBody2 == -1 || pmxJoint.rigidBody1 == pmxJoint.rigidBody2) {
                continue
            }
            val rb1 = physicsManager.mmdRigidBodies[pmxJoint.rigidBody1].btRigidBody
            val rb2 = physicsManager.mmdRigidBodies[pmxJoint.rigidBody2].btRigidBody
            val transform = Transform()
            transform.setIdentity()
            transform.origin.set(pmxJoint.position)
            transform.basis.set(setEulerZYX(pmxJoint.orientation))
            val constraint = Generic6DofConstraint(rb1, rb2,
                Transform(rb1.getWorldTransform(Transform())).apply { inverse() }.apply{ mul(transform) },
                Transform(rb2.getWorldTransform(Transform())).apply { inverse() }.apply { mul(transform) }, true)
            constraint.setLinearLowerLimit(pmxJoint.moveLimitationMin)
            constraint.setLinearUpperLimit(pmxJoint.moveLimitationMax)
            constraint.setAngularLowerLimit(pmxJoint.rotationLimitationMin)
            constraint.setAngularUpperLimit(pmxJoint.rotationLimitationMax)
            // TODO: spring calculation if i found a bullet-physics port supporting it
            physicsManager.addJoint(constraint)
        }

        resetPhysics()
    }

    override fun resetPhysics() {
        physicsManager.mmdRigidBodies.forEach { rb ->
            rb.setActivation(false)
            rb.resetTransform()
        }

        physicsManager.update(1f / 60f)

        physicsManager.mmdRigidBodies.forEach { rb ->
            rb.reflectGlobalTransform()
        }

        physicsManager.mmdRigidBodies.forEach { rb ->
            rb.calcLocalTransform()
        }

        this.nodes.forEach { node ->
            if(node.parent == null) {
                node.updateGlobalTransform()
            }
        }

        physicsManager.mmdRigidBodies.forEach { rb ->
            rb.reset(physicsManager)
        }
    }

    override fun initAnimation() {
        clearBaseAnimation()

        nodes.forEach { node ->
            node.animTranslate.set(0f, 0f, 0f)
            node.animRotate.set(1f, 0f, 0f, 0f)
        }

        beginAnimation()

        nodes.forEach { node ->
            node.updateLocalTransform()
        }

        morphs.forEach { morph ->
            morph.weight = 0f
        }

        ikSolvers.forEach { ik ->
            ik.isEnable = true
        }

        nodes.forEach { node ->
            node.updateGlobalTransform()
        }

        nodes.forEach { node ->
            if(node.appendNode != null) {
                node.updateAppendTransform()
                node.updateGlobalTransform()
            }
            if(node.solver != null) {
                node.solver!!.solve()
                node.updateGlobalTransform()
            }
        }

        nodes.forEach { node ->
            if(node.parent == null) {
                node.updateGlobalTransform()
            }
        }

        endAnimation()

        resetPhysics()
    }

    override fun beginAnimation() {
        nodes.forEach { node ->
            node.beginUpdateTransform()
        }
        morphPositions.clear()
        morphUVs.clear()
        positions.forEach { _ ->
            morphPositions.add(Vector3f())
            morphUVs.add(Vector4f())
        }
    }

    override fun endAnimation() {
        nodes.forEach { node ->
            node.endUpdateTransform()
        }
    }

    override fun update() {
        TODO("Not yet implemented")
    }
}