package me.liuli.mmd.file.parser

import me.liuli.mmd.file.PmxFile
import me.liuli.mmd.utils.*
import java.io.ByteArrayOutputStream

object PmxParser : Parser<PmxFile> {
    override fun readToInstance(file: PmxFile, input: ByteArray) {
        val iterator = input.iterator()

        // header
        val header = iterator.readString(4)
        if(header != "PMX ") {
            throw IllegalArgumentException("Invalid PMX file header: $header")
        }

        // check if the version is supported
        val version = iterator.readFloat()
        if(version != 2.0f && version != 2.1f) {
            throw IllegalArgumentException("Unsupported PMX version: $version")
        }

        // read settings, i think settings only need to be used when parsing, so we don't need to store them
        val setting = readSettings(iterator, file)
        file.uv = setting.uv

        // read basic info
        file.name = readString(iterator, setting.encoding)
        file.englishName = readString(iterator, setting.encoding)
        file.comment = readString(iterator, setting.encoding)
        file.englishComment = readString(iterator, setting.encoding)

        // read vertices
        val vertexCount = iterator.readInt()
        for(i in 0 until vertexCount) {
            file.vertices.add(readVertex(iterator, setting))
        }

        // read indices
        val indexCount = iterator.readInt()
        for(i in 0 until indexCount) {
            file.indices.add(readInt(iterator, setting.vertexIndexSize))
        }

        // read textures
        val textureCount = iterator.readInt()
        for(i in 0 until textureCount) {
            file.textures.add(readString(iterator, setting.encoding).replace("\\", "/"))
        }

        // read materials
        val materialCount = iterator.readInt()
        for(i in 0 until materialCount) {
            file.materials.add(readMaterial(iterator, setting))
        }

        // read bones
        val boneCount = iterator.readInt()
        for(i in 0 until boneCount) {
            file.bones.add(readBone(iterator, setting))
        }

        // read morphs
        val morphCount = iterator.readInt()
        for(i in 0 until morphCount) {
            file.morphs.add(readMorph(iterator, setting))
        }

        // read display frames
        val displayFrameCount = iterator.readInt()
        for(i in 0 until displayFrameCount) {
            file.displayFrames.add(readDisplayFrame(iterator, setting))
        }

        // read rigid bodies
        val rigidBodyCount = iterator.readInt()
        for(i in 0 until rigidBodyCount) {
            file.rigidBodies.add(readRigidBody(iterator, setting))
        }

        // read joints
        val jointCount = iterator.readInt()
        for(i in 0 until jointCount) {
            file.joints.add(readJoint(iterator, setting))
        }

        if (iterator.hasNext()) {
            // read soft bodies
            val softBodyCount = iterator.readInt()
            for(i in 0 until softBodyCount) {
                file.softBodies.add(readSoftBody(iterator, setting))
            }
        }
    }

    private fun readSoftBody(iterator: ByteIterator, setting: Setting): PmxFile.SoftBody {
        val softBody = PmxFile.SoftBody()

        softBody.name = readString(iterator, setting.encoding)
        softBody.englishName = readString(iterator, setting.encoding)
        val typeCode = iterator.next().toInt()
        softBody.type = PmxFile.SoftBody.Type.values().find { it.code == typeCode } ?: throw IllegalArgumentException("Invalid soft body type: $typeCode")
        softBody.materialIndex = readInt(iterator, setting.materialIndexSize)
        softBody.group = iterator.next().toInt()
        softBody.collisionGroup = iterator.readShort()
        val maskCode = iterator.next().toInt()
        softBody.mask = PmxFile.SoftBody.Mask.values().find { it.code == maskCode } ?: throw IllegalArgumentException("Invalid soft body mask: $maskCode")
        softBody.blinkLength = iterator.readInt()
        softBody.numClusters = iterator.readInt()
        softBody.totalMass = iterator.readFloat()
        softBody.collisionMargin = iterator.readFloat()
        softBody.aeroModel = iterator.readInt()
        softBody.vcf = iterator.readFloat()
        softBody.dp = iterator.readFloat()
        softBody.dg = iterator.readFloat()
        softBody.lf = iterator.readFloat()
        softBody.pr = iterator.readFloat()
        softBody.vc = iterator.readFloat()
        softBody.df = iterator.readFloat()
        softBody.mt = iterator.readFloat()
        softBody.chr = iterator.readFloat()
        softBody.khr = iterator.readFloat()
        softBody.shr = iterator.readFloat()
        softBody.ahr = iterator.readFloat()
        softBody.srhrCl = iterator.readFloat()
        softBody.skhrCl = iterator.readFloat()
        softBody.sshrCl = iterator.readFloat()
        softBody.srSpltCl = iterator.readFloat()
        softBody.skSpltCl = iterator.readFloat()
        softBody.ssSpltCl = iterator.readFloat()
        softBody.vIt = iterator.readInt()
        softBody.pIt = iterator.readInt()
        softBody.dIt = iterator.readInt()
        softBody.cIt = iterator.readInt()
        softBody.lst = iterator.readFloat()
        softBody.ast = iterator.readFloat()
        softBody.vst = iterator.readFloat()

        return softBody
    }

    private fun readJoint(iterator: ByteIterator, setting: Setting): PmxFile.Joint {
        val joint = PmxFile.Joint()

        joint.name = readString(iterator, setting.encoding)
        joint.englishName = readString(iterator, setting.encoding)
        val typeCode = iterator.next().toInt()
        joint.type = PmxFile.Joint.Type.values().find { it.code == typeCode } ?: throw IllegalArgumentException("Invalid joint type code: $typeCode")
        joint.rigidBody1 = readInt(iterator, setting.rigidBodyIndexSize)
        joint.rigidBody2 = readInt(iterator, setting.rigidBodyIndexSize)
        iterator.readVector3f(joint.position)
        iterator.readVector3f(joint.orientation)
        iterator.readVector3f(joint.moveLimitationMin)
        iterator.readVector3f(joint.moveLimitationMax)
        iterator.readVector3f(joint.rotationLimitationMin)
        iterator.readVector3f(joint.rotationLimitationMax)
        iterator.readVector3f(joint.springTranslateFactor)
        iterator.readVector3f(joint.springRotateFactor)

        return joint
    }

    private fun readRigidBody(iterator: ByteIterator, setting: Setting): PmxFile.RigidBody {
        val rigidBody = PmxFile.RigidBody()

        rigidBody.name = readString(iterator, setting.encoding)
        rigidBody.englishName = readString(iterator, setting.encoding)
        rigidBody.targetBone = readInt(iterator, setting.boneIndexSize)
        rigidBody.group = iterator.next().toInt()
        rigidBody.mask = iterator.readShort()
        val shape = iterator.next().toInt()
        rigidBody.shape = PmxFile.RigidBody.Shape.values().find { it.code == shape } ?: throw IllegalArgumentException("Invalid rigid body shape: $shape")
        iterator.readVector3f(rigidBody.size)
        iterator.readVector3f(rigidBody.position)
        iterator.readVector3f(rigidBody.orientation)
        rigidBody.mass = iterator.readFloat()
        rigidBody.moveAttenuation = iterator.readFloat()
        rigidBody.rotationAttenuation = iterator.readFloat()
        rigidBody.repulsion = iterator.readFloat()
        rigidBody.friction = iterator.readFloat()
        val op = iterator.next().toInt()
        rigidBody.op = PmxFile.RigidBody.Operation.values().find { it.code == op } ?: throw IllegalArgumentException("Invalid rigid body operation: $op")

        return rigidBody
    }

    private fun readDisplayFrame(iterator: ByteIterator, setting: Setting): PmxFile.DisplayFrame {
        val displayFrame = PmxFile.DisplayFrame()

        displayFrame.name = readString(iterator, setting.encoding)
        displayFrame.englishName = readString(iterator, setting.encoding)
        displayFrame.flag = iterator.next().toInt()
        val elementCount = iterator.readInt()
        for(i in 0 until elementCount) {
            val element = PmxFile.DisplayFrame.Element()
            element.target = iterator.next().toInt()
            if (element.target == 0) {
                element.index = readInt(iterator, setting.boneIndexSize)
            } else {
                element.index = readInt(iterator, setting.morphIndexSize)
            }
            displayFrame.elements.add(element)
        }

        return displayFrame
    }

    private fun readMorph(iterator: ByteIterator, setting: Setting): PmxFile.Morph {
        val morph = PmxFile.Morph()

        morph.name = readString(iterator, setting.encoding)
        morph.englishName = readString(iterator, setting.encoding)
        val categoryCode = iterator.next().toInt()
        morph.category = PmxFile.Morph.Category.values().find { it.code == categoryCode } ?: throw IllegalArgumentException("Invalid category code: $categoryCode")
        val typeCode = iterator.next().toInt()
        morph.type = PmxFile.Morph.Type.values().find { it.code == typeCode } ?: throw IllegalArgumentException("Invalid type code: $typeCode")
        val offsetCount = iterator.readInt()
        for(i in 0 until offsetCount) {
            morph.offsets.add(when(morph.type) {
                PmxFile.Morph.Type.GROUP -> {
                    val group = PmxFile.Morph.GroupOffset()
                    group.index = readInt(iterator, setting.morphIndexSize)
                    group.weight = iterator.readFloat()
                    group
                }
                PmxFile.Morph.Type.VERTEX -> {
                    val vertex = PmxFile.Morph.VertexOffset()
                    vertex.index = readInt(iterator, setting.vertexIndexSize)
                    iterator.readVector3f(vertex.position)
                    vertex
                }
                PmxFile.Morph.Type.BONE -> {
                    val bone = PmxFile.Morph.BoneOffset()
                    bone.index = readInt(iterator, setting.boneIndexSize)
                    iterator.readVector3f(bone.translation)
                    iterator.readVector4f(bone.rotation)
                    bone
                }
                PmxFile.Morph.Type.MATERIAL -> {
                    val material = PmxFile.Morph.MaterialOffset()
                    material.index = readInt(iterator, setting.materialIndexSize)
                    material.operation = iterator.next().toInt()
                    iterator.readVector4f(material.diffuse)
                    iterator.readVector3f(material.specular)
                    material.specularlity = iterator.readFloat()
                    iterator.readVector3f(material.ambient)
                    material.edgeColor = iterator.readColor4f()
                    material.edgeSize = iterator.readFloat()
                    iterator.readVector4f(material.textureFactor)
                    iterator.readVector4f(material.sphereTextureFactor)
                    iterator.readVector4f(material.toonTextureFactor)
                    material
                }
                PmxFile.Morph.Type.UV,
                PmxFile.Morph.Type.ADDITIONAL_UV1,
                PmxFile.Morph.Type.ADDITIONAL_UV2,
                PmxFile.Morph.Type.ADDITIONAL_UV3,
                PmxFile.Morph.Type.ADDITIONAL_UV4 -> {
                    val uv = PmxFile.Morph.UvOffset()
                    uv.index = readInt(iterator, setting.vertexIndexSize)
                    iterator.readVector4f(uv.offset)
                    uv
                }
                PmxFile.Morph.Type.FLIP -> {
                    val flip = PmxFile.Morph.FlipOffset()
                    flip.index = readInt(iterator, setting.morphIndexSize)
                    flip.value = iterator.readFloat()
                    flip
                }
                PmxFile.Morph.Type.IMPULSE -> {
                    val impulse = PmxFile.Morph.ImpulseOffset()
                    impulse.index = readInt(iterator, setting.rigidBodyIndexSize)
                    impulse.isLocal = iterator.readBool()
                    iterator.readVector3f(impulse.velocity)
                    iterator.readVector3f(impulse.angularTorgue)
                    impulse
                }
            })
        }

        return morph
    }

    private fun readBone(iterator: ByteIterator, setting: Setting): PmxFile.Bone {
        val bone = PmxFile.Bone()

        bone.name = readString(iterator, setting.encoding)
        bone.englishName = readString(iterator, setting.encoding)
        iterator.readVector3f(bone.position)
        bone.parentIndex = readInt(iterator, setting.boneIndexSize)
        bone.level = iterator.readInt()
        bone.flag = iterator.readShort()
        if (bone.flag.toInt() and 0x01 != 0x00) {
            bone.targetIndex = readInt(iterator, setting.boneIndexSize)
        } else {
            iterator.readVector3f(bone.offset)
        }
        if (bone.flag.toInt() and (0x0100 or 0x0200) != 0x00) {
            bone.grandParentIndex = readInt(iterator, setting.boneIndexSize)
            bone.grantWeight = iterator.readFloat()
        }
        if (bone.flag.toInt() and 0x0400 != 0x00) {
            iterator.readVector3f(bone.fixedAxis)
        }
        if (bone.flag.toInt() and 0x0800 != 0x00) {
            iterator.readVector3f(bone.localXAxis)
            iterator.readVector3f(bone.localZAxis)
        }
        if (bone.flag.toInt() and 0x2000 != 0x00) {
            bone.key = iterator.readInt()
        }
        if (bone.flag.toInt() and 0x0020 != 0x00) {
            bone.ikTargetBoneIndex = readInt(iterator, setting.boneIndexSize)
            bone.ikLoop = iterator.readInt()
            bone.ikLoopAngleLimit = iterator.readFloat()
            val ikLinkCount = iterator.readInt()
            for (i in 0 until ikLinkCount) {
                val ikLink = PmxFile.Bone.IkLink()
                ikLink.linkTarget = readInt(iterator, setting.boneIndexSize)
                ikLink.angleLock = iterator.next().toInt()
                if (ikLink.angleLock == 1) {
                    iterator.readVector3f(ikLink.maxRadian)
                    iterator.readVector3f(ikLink.minRadian)
                }
                bone.ikLinks.add(ikLink)
            }
        }

        return bone
    }

    private fun readMaterial(iterator: ByteIterator, setting: Setting): PmxFile.Material {
        val material = PmxFile.Material()

        material.name = readString(iterator, setting.encoding)
        material.englishName = readString(iterator, setting.encoding)
        iterator.readVector4f(material.diffuse)
        iterator.readVector3f(material.specular)
        material.specularlity = iterator.readFloat()
        iterator.readVector3f(material.ambient)
        material.flag = iterator.next().toInt()
        material.edgeColor = iterator.readColor4f()
        material.edgeSize = iterator.readFloat()
        material.diffuseTextureIndex = readInt(iterator, setting.textureIndexSize)
        material.sphereTextureIndex = readInt(iterator, setting.textureIndexSize)
        val sphereMode = iterator.next().toInt()
        material.sphereMode = PmxFile.Material.SphereMode.values().find { it.value == sphereMode } ?: throw IllegalArgumentException("unknown sphere mode: $sphereMode")
        val toonMode = iterator.next().toInt()
        material.toonMode = PmxFile.Material.ToonMode.values().find { it.value == toonMode } ?: throw IllegalArgumentException("unknown toon mode: $toonMode")
        material.toonTextureIndex = if(material.toonMode == PmxFile.Material.ToonMode.COMMON) {
            iterator.next().toInt()
        } else {
            readInt(iterator, setting.textureIndexSize)
        }
        material.memo = readString(iterator, setting.encoding)
        material.index = iterator.readInt()

        return material
    }

    private fun readVertex(iterator: ByteIterator, setting: Setting): PmxFile.Vertex {
        val vertex = PmxFile.Vertex()

        iterator.readVector3f(vertex.position)
        iterator.readVector3f(vertex.normal)
        vertex.uv.x = iterator.readFloat()
        vertex.uv.y = iterator.readFloat()
        for(i in 0 until setting.uv) {
            iterator.readVector4f(vertex.uva[i])
        }

        when(val skinningCode = iterator.next().toInt()) {
            0 -> {
                val skinning = PmxFile.Vertex.SkinningBDEF1()
                skinning.boneIndex = readInt(iterator, setting.boneIndexSize)
                vertex.skinning = skinning
            }
            1 -> {
                val skinning = PmxFile.Vertex.SkinningBDEF2()
                skinning.boneIndex1 = readInt(iterator, setting.boneIndexSize)
                skinning.boneIndex2 = readInt(iterator, setting.boneIndexSize)
                skinning.weight = iterator.readFloat()
                vertex.skinning = skinning
            }
            2 -> {
                val skinning = PmxFile.Vertex.SkinningBDEF4()
                skinning.boneIndex1 = readInt(iterator, setting.boneIndexSize)
                skinning.boneIndex2 = readInt(iterator, setting.boneIndexSize)
                skinning.boneIndex3 = readInt(iterator, setting.boneIndexSize)
                skinning.boneIndex4 = readInt(iterator, setting.boneIndexSize)
                skinning.weight1 = iterator.readFloat()
                skinning.weight2 = iterator.readFloat()
                skinning.weight3 = iterator.readFloat()
                skinning.weight4 = iterator.readFloat()
                vertex.skinning = skinning
            }
            3 -> {
                val skinning = PmxFile.Vertex.SkinningSDEF()
                skinning.boneIndex1 = readInt(iterator, setting.boneIndexSize)
                skinning.boneIndex2 = readInt(iterator, setting.boneIndexSize)
                skinning.weight = iterator.readFloat()
                iterator.readVector3f(skinning.c)
                iterator.readVector3f(skinning.r0)
                iterator.readVector3f(skinning.r1)
                vertex.skinning = skinning
            }
            4 -> {
                val skinning = PmxFile.Vertex.SkinningQDEF()
                skinning.boneIndex1 = readInt(iterator, setting.boneIndexSize)
                skinning.boneIndex2 = readInt(iterator, setting.boneIndexSize)
                skinning.boneIndex3 = readInt(iterator, setting.boneIndexSize)
                skinning.boneIndex4 = readInt(iterator, setting.boneIndexSize)
                skinning.weight1 = iterator.readFloat()
                skinning.weight2 = iterator.readFloat()
                skinning.weight3 = iterator.readFloat()
                skinning.weight4 = iterator.readFloat()
                vertex.skinning = skinning
            }
            else -> throw IllegalArgumentException("Invalid skinning code: $skinningCode")
        }

        vertex.edge = iterator.readFloat()

        return vertex
    }

    private fun readSettings(iterator: ByteIterator, file: PmxFile): Setting {
        val setting = Setting()
        val sections = iterator.next().toInt() - 8

        if(sections < 0) {
            throw IllegalArgumentException("Failed to parse pmx setting part: sections < 8")
        }

        setting.encoding = iterator.next() == 0x00.toByte()
        setting.uv = iterator.next().toInt()
        setting.vertexIndexSize = iterator.next().toInt()
        setting.textureIndexSize = iterator.next().toInt()
        setting.materialIndexSize = iterator.next().toInt()
        setting.boneIndexSize = iterator.next().toInt()
        setting.morphIndexSize = iterator.next().toInt()
        setting.rigidBodyIndexSize = iterator.next().toInt()

        if(sections > 0) {
            for(i in 0 until sections) {
                iterator.next()
            }
        }

        return setting
    }

    override fun write(file: PmxFile): ByteArray {
        val bos = ByteArrayOutputStream()

        // header
        bos.writeLimited("PMX ".toByteArray(Charsets.UTF_8), 4)
        bos.writeFloat(if(file.softBodies.isEmpty()) { 2.0f } else { 2.1f })

        // settings
        bos.write(0x08.toByte())
        bos.write(0x00.toByte()) // always use UTF-16LE
        bos.write(file.uv.toByte())
        bos.write(0x04.toByte()) // vertex index size
        bos.write(0x04.toByte()) // texture index size
        bos.write(0x04.toByte()) // material index size
        bos.write(0x04.toByte()) // bone index size
        bos.write(0x04.toByte()) // morph index size
        bos.write(0x04.toByte()) // rigid body index size

        // info
        writeString(bos, file.name, true)
        writeString(bos, file.englishName, true)
        writeString(bos, file.comment, true)
        writeString(bos, file.englishComment, true)

        // vertices
        bos.writeInt(file.vertices.size)
        file.vertices.forEach { writeVertex(bos, it, file) }

        // indices
        bos.writeInt(file.indices.size)
        file.indices.forEach { bos.writeInt(it) }

        // textures
        bos.writeInt(file.textures.size)
        file.textures.forEach { writeString(bos, it, true) }

        // materials
        bos.writeInt(file.materials.size)
        file.materials.forEach { writeMaterial(bos, it, file) }

        // bones
        bos.writeInt(file.bones.size)
        file.bones.forEach { writeBone(bos, it, file) }

        // morphs
        bos.writeInt(file.morphs.size)
        file.morphs.forEach { writeMorph(bos, it, file) }

        // display frames
        bos.writeInt(file.displayFrames.size)
        file.displayFrames.forEach { writeDisplayFrame(bos, it, file) }

        // rigid bodies
        bos.writeInt(file.rigidBodies.size)
        file.rigidBodies.forEach { writeRigidBody(bos, it, file) }

        // joints
        bos.writeInt(file.joints.size)
        file.joints.forEach { writeJoint(bos, it, file) }

        // soft bodies
        if(file.softBodies.isNotEmpty()) {
            bos.writeInt(file.softBodies.size)
            file.softBodies.forEach { writeSoftBody(bos, it, file) }
        }

        return bos.toByteArray()
    }

    private fun writeSoftBody(bos: ByteArrayOutputStream, softBody: PmxFile.SoftBody, file: PmxFile) {
        writeString(bos, softBody.name, true)
        writeString(bos, softBody.englishName, true)
        bos.write(softBody.type.code)
        bos.writeInt(softBody.materialIndex)
        bos.write(softBody.group.toByte())
        bos.writeShort(softBody.collisionGroup.toShort())
        bos.write(softBody.mask.code)
        bos.writeInt(softBody.blinkLength)
        bos.writeInt(softBody.numClusters)
        bos.writeFloat(softBody.totalMass)
        bos.writeFloat(softBody.collisionMargin)
        bos.writeInt(softBody.aeroModel)
        bos.writeFloat(softBody.vcf)
        bos.writeFloat(softBody.dp)
        bos.writeFloat(softBody.dg)
        bos.writeFloat(softBody.lf)
        bos.writeFloat(softBody.pr)
        bos.writeFloat(softBody.vc)
        bos.writeFloat(softBody.df)
        bos.writeFloat(softBody.mt)
        bos.writeFloat(softBody.chr)
        bos.writeFloat(softBody.khr)
        bos.writeFloat(softBody.shr)
        bos.writeFloat(softBody.ahr)
        bos.writeFloat(softBody.srhrCl)
        bos.writeFloat(softBody.skhrCl)
        bos.writeFloat(softBody.sshrCl)
        bos.writeFloat(softBody.srSpltCl)
        bos.writeFloat(softBody.skSpltCl)
        bos.writeFloat(softBody.ssSpltCl)
        bos.writeInt(softBody.vIt)
        bos.writeInt(softBody.pIt)
        bos.writeInt(softBody.dIt)
        bos.writeInt(softBody.cIt)
        bos.writeFloat(softBody.lst)
        bos.writeFloat(softBody.ast)
        bos.writeFloat(softBody.vst)
    }

    private fun writeJoint(bos: ByteArrayOutputStream, joint: PmxFile.Joint, file: PmxFile) {
        writeString(bos, joint.name, true)
        writeString(bos, joint.englishName, true)
        bos.write(joint.type.code.toByte())
        bos.writeInt(joint.rigidBody1)
        bos.writeInt(joint.rigidBody2)
        bos.writeVector3f(joint.position)
        bos.writeVector3f(joint.orientation)
        bos.writeVector3f(joint.moveLimitationMin)
        bos.writeVector3f(joint.moveLimitationMax)
        bos.writeVector3f(joint.rotationLimitationMin)
        bos.writeVector3f(joint.rotationLimitationMax)
        bos.writeVector3f(joint.springTranslateFactor)
        bos.writeVector3f(joint.springRotateFactor)
    }

    private fun writeRigidBody(bos: ByteArrayOutputStream, rigidBody: PmxFile.RigidBody, file: PmxFile) {
        writeString(bos, rigidBody.name, true)
        writeString(bos, rigidBody.englishName, true)
        bos.writeInt(rigidBody.targetBone)
        bos.write(rigidBody.group.toByte())
        bos.writeShort(rigidBody.mask)
        bos.write(rigidBody.shape.code.toByte())
        bos.writeVector3f(rigidBody.size)
        bos.writeVector3f(rigidBody.position)
        bos.writeVector3f(rigidBody.orientation)
        bos.writeFloat(rigidBody.mass)
        bos.writeFloat(rigidBody.moveAttenuation)
        bos.writeFloat(rigidBody.rotationAttenuation)
        bos.writeFloat(rigidBody.repulsion)
        bos.writeFloat(rigidBody.friction)
        bos.write(rigidBody.op.code.toByte())
    }

    private fun writeDisplayFrame(bos: ByteArrayOutputStream, displayFrame: PmxFile.DisplayFrame, file: PmxFile) {
        writeString(bos, displayFrame.name, true)
        writeString(bos, displayFrame.englishName, true)
        bos.write(displayFrame.flag.toByte())
        bos.writeInt(displayFrame.elements.size)
        for (element in displayFrame.elements) {
            bos.write(element.target.toByte())
            bos.writeInt(element.index)
        }
    }

    private fun writeMorph(bos: ByteArrayOutputStream, morph: PmxFile.Morph, file: PmxFile) {
        writeString(bos, morph.name, true)
        writeString(bos, morph.englishName, true)
        bos.write(morph.category.code.toByte())
        bos.write(morph.type.code.toByte())
        bos.writeInt(morph.offsets.size)
        morph.offsets.forEach { offset ->
            when(offset) {
                is PmxFile.Morph.GroupOffset -> {
                    bos.writeInt(offset.index)
                    bos.writeFloat(offset.weight)
                }
                is PmxFile.Morph.VertexOffset -> {
                    bos.writeInt(offset.index)
                    bos.writeVector3f(offset.position)
                }
                is PmxFile.Morph.BoneOffset -> {
                    bos.writeInt(offset.index)
                    bos.writeVector3f(offset.translation)
                    bos.writeVector4f(offset.rotation)
                }
                is PmxFile.Morph.MaterialOffset -> {
                    bos.writeInt(offset.index)
                    bos.write(offset.operation.toByte())
                    bos.writeVector4f(offset.diffuse)
                    bos.writeVector3f(offset.specular)
                    bos.writeFloat(offset.specularlity)
                    bos.writeVector3f(offset.ambient)
                    bos.writeColor4f(offset.edgeColor)
                    bos.writeFloat(offset.edgeSize)
                    bos.writeVector4f(offset.textureFactor)
                    bos.writeVector4f(offset.sphereTextureFactor)
                    bos.writeVector4f(offset.toonTextureFactor)
                }
                is PmxFile.Morph.UvOffset -> {
                    bos.writeInt(offset.index)
                    bos.writeVector4f(offset.offset)
                }
                is PmxFile.Morph.FlipOffset -> {
                    bos.writeInt(offset.index)
                    bos.writeFloat(offset.value)
                }
                is PmxFile.Morph.ImpulseOffset -> {
                    bos.writeInt(offset.index)
                    bos.writeBool(offset.isLocal)
                    bos.writeVector3f(offset.velocity)
                    bos.writeVector3f(offset.angularTorgue)
                }
            }
        }
    }

    private fun writeBone(bos: ByteArrayOutputStream, bone: PmxFile.Bone, file: PmxFile) {
        writeString(bos, bone.name, true)
        writeString(bos, bone.englishName, true)
        bos.writeVector3f(bone.position)
        bos.writeInt(bone.parentIndex)
        bos.writeInt(bone.level)
        bos.writeShort(bone.flag)
        if (bone.flag.toInt() and 0x01 != 0x00) {
            bos.writeInt(bone.targetIndex)
        } else {
            bos.writeVector3f(bone.offset)
        }
        if (bone.flag.toInt() and (0x0100 or 0x0200) != 0x00) {
            bos.writeInt(bone.grandParentIndex)
            bos.writeFloat(bone.grantWeight)
        }
        if (bone.flag.toInt() and 0x0400 != 0x00) {
            bos.writeVector3f(bone.fixedAxis)
        }
        if (bone.flag.toInt() and 0x0800 != 0x00) {
            bos.writeVector3f(bone.localXAxis)
            bos.writeVector3f(bone.localZAxis)
        }
        if (bone.flag.toInt() and 0x2000 != 0x00) {
            bos.writeInt(bone.key)
        }
        if (bone.flag.toInt() and 0x0020 != 0x00) {
            bos.writeInt(bone.ikTargetBoneIndex)
            bos.writeInt(bone.ikLoop)
            bos.writeFloat(bone.ikLoopAngleLimit)
            bos.writeInt(bone.ikLinks.size)
            bone.ikLinks.forEach { ikLink ->
                bos.writeInt(ikLink.linkTarget)
                bos.write(ikLink.angleLock.toByte())
                if(ikLink.angleLock == 1) {
                    bos.writeVector3f(ikLink.maxRadian)
                    bos.writeVector3f(ikLink.minRadian)
                }
            }
        }
    }

    private fun writeMaterial(bos: ByteArrayOutputStream, material: PmxFile.Material, file: PmxFile) {
        writeString(bos, material.name, true)
        writeString(bos, material.englishName, true)
        bos.writeVector4f(material.diffuse)
        bos.writeVector3f(material.specular)
        bos.writeFloat(material.specularlity)
        bos.writeVector3f(material.ambient)
        bos.write(material.flag.toByte())
        bos.writeColor4f(material.edgeColor)
        bos.writeFloat(material.edgeSize)
        bos.writeInt(material.diffuseTextureIndex)
        bos.writeInt(material.sphereTextureIndex)
        bos.write(material.sphereMode.value.toByte())
        bos.write(material.toonMode.value.toByte())
        if (material.toonMode == PmxFile.Material.ToonMode.SEPARATE) {
            bos.writeInt(material.toonTextureIndex)
        } else {
            bos.write(material.toonTextureIndex.toByte())
        }
        writeString(bos, material.memo, true)
        bos.writeInt(material.index)
    }

    private fun writeVertex(bos: ByteArrayOutputStream, vertex: PmxFile.Vertex, file: PmxFile) {
        bos.writeVector3f(vertex.position)
        bos.writeVector3f(vertex.normal)
        bos.writeFloat(vertex.uv.x)
        bos.writeFloat(vertex.uv.y)
        for(i in 0 until file.uv) {
            bos.writeVector4f(vertex.uva[i])
        }
        val skinning = vertex.skinning
        bos.write(skinning.code.toByte())
        when (skinning) {
            is PmxFile.Vertex.SkinningBDEF1 -> {
                bos.writeInt(skinning.boneIndex)
            }
            is PmxFile.Vertex.SkinningBDEF2 -> {
                bos.writeInt(skinning.boneIndex1)
                bos.writeInt(skinning.boneIndex2)
                bos.writeFloat(skinning.weight)
            }
            is PmxFile.Vertex.SkinningBDEF4 -> {
                bos.writeInt(skinning.boneIndex1)
                bos.writeInt(skinning.boneIndex2)
                bos.writeInt(skinning.boneIndex3)
                bos.writeInt(skinning.boneIndex4)
                bos.writeFloat(skinning.weight1)
                bos.writeFloat(skinning.weight2)
                bos.writeFloat(skinning.weight3)
                bos.writeFloat(skinning.weight4)
            }
            is PmxFile.Vertex.SkinningSDEF -> {
                bos.writeInt(skinning.boneIndex1)
                bos.writeInt(skinning.boneIndex2)
                bos.writeFloat(skinning.weight)
                bos.writeVector3f(skinning.c)
                bos.writeVector3f(skinning.r0)
                bos.writeVector3f(skinning.r1)
            }
            is PmxFile.Vertex.SkinningQDEF -> {
                bos.writeInt(skinning.boneIndex1)
                bos.writeInt(skinning.boneIndex2)
                bos.writeInt(skinning.boneIndex3)
                bos.writeInt(skinning.boneIndex4)
                bos.writeFloat(skinning.weight1)
                bos.writeFloat(skinning.weight2)
                bos.writeFloat(skinning.weight3)
                bos.writeFloat(skinning.weight4)
            }
        }
        bos.writeFloat(vertex.edge)
    }

    /**
     * read string in PMX file from [iterator]
     */
    private fun readString(iterator: ByteIterator, encoding: Boolean): String {
        val length = iterator.readInt()
        if(length <= 0) {
            return ""
        }
        return iterator.read(length).toString(if(encoding) {Charsets.UTF_16LE} else {Charsets.UTF_8})
    }

    /**
     * write string in PMX file to [bos]
     */
    private fun writeString(bos: ByteArrayOutputStream, string: String, encoding: Boolean) {
        if(string.isEmpty()) {
            bos.writeInt(0)
            return
        }
        val bytes = string.toByteArray(if (encoding) { Charsets.UTF_16LE } else { Charsets.UTF_8 })
        bos.writeInt(bytes.size)
        bos.write(bytes)
    }

    /**
     * read int in PMX file from [iterator]
     */
    private fun readInt(iterator: ByteIterator, size: Int): Int {
        return when(size) {
            1 -> iterator.next().toInt()
            2 -> iterator.readShort().toInt()
            4 -> iterator.readInt()
            else -> throw IllegalArgumentException("Invalid PMX int size: $size")
        }
    }

    override fun read(input: ByteArray): PmxFile {
        return PmxFile().also { readToInstance(it, input) }
    }

    /**
     * cache for settings header in PMX file
     */
    private class Setting {
        /**
         * true(0x00): UTF-16LE, false(!=0x00): UTF-8
         */
        var encoding = false
        var uv = 0
        var vertexIndexSize = 0
        var textureIndexSize = 0
        var materialIndexSize = 0
        var boneIndexSize = 0
        var morphIndexSize = 0
        var rigidBodyIndexSize = 0
    }
}