package me.liuli.mmd.file.parser

import me.liuli.mmd.file.PmxFile
import me.liuli.mmd.utils.*
import java.awt.Color
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
        val setting = readSettings(iterator)

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
            file.textures.add(readString(iterator, setting.encoding))
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
        joint.position[0] = iterator.readFloat()
        joint.position[1] = iterator.readFloat()
        joint.position[2] = iterator.readFloat()
        joint.orientation[0] = iterator.readFloat()
        joint.orientation[1] = iterator.readFloat()
        joint.orientation[2] = iterator.readFloat()
        joint.moveLimitationMin[0] = iterator.readFloat()
        joint.moveLimitationMin[1] = iterator.readFloat()
        joint.moveLimitationMin[2] = iterator.readFloat()
        joint.moveLimitationMax[0] = iterator.readFloat()
        joint.moveLimitationMax[1] = iterator.readFloat()
        joint.moveLimitationMax[2] = iterator.readFloat()
        joint.rotationLimitationMin[0] = iterator.readFloat()
        joint.rotationLimitationMin[1] = iterator.readFloat()
        joint.rotationLimitationMin[2] = iterator.readFloat()
        joint.rotationLimitationMax[0] = iterator.readFloat()
        joint.rotationLimitationMax[1] = iterator.readFloat()
        joint.rotationLimitationMax[2] = iterator.readFloat()
        joint.springTranslateFactor[0] = iterator.readFloat()
        joint.springTranslateFactor[1] = iterator.readFloat()
        joint.springTranslateFactor[2] = iterator.readFloat()
        joint.springRotateFactor[0] = iterator.readFloat()
        joint.springRotateFactor[1] = iterator.readFloat()
        joint.springRotateFactor[2] = iterator.readFloat()

        return joint
    }

    private fun readRigidBody(iterator: ByteIterator, setting: Setting): PmxFile.RigidBody {
        val rigidBody = PmxFile.RigidBody()

        rigidBody.name = readString(iterator, setting.encoding)
        rigidBody.englishName = readString(iterator, setting.encoding)
        rigidBody.targetBone = readInt(iterator, setting.boneIndexSize)
        rigidBody.group = iterator.next().toInt()
        rigidBody.mask = iterator.readShort()
        rigidBody.shape = iterator.next().toInt()
        rigidBody.size[0] = iterator.readFloat()
        rigidBody.size[1] = iterator.readFloat()
        rigidBody.size[2] = iterator.readFloat()
        rigidBody.position[0] = iterator.readFloat()
        rigidBody.position[1] = iterator.readFloat()
        rigidBody.position[2] = iterator.readFloat()
        rigidBody.orientation[0] = iterator.readFloat()
        rigidBody.orientation[1] = iterator.readFloat()
        rigidBody.orientation[2] = iterator.readFloat()
        rigidBody.mass = iterator.readFloat()
        rigidBody.moveAttenuation = iterator.readFloat()
        rigidBody.rotationAttenuation = iterator.readFloat()
        rigidBody.repulsion = iterator.readFloat()
        rigidBody.friction = iterator.readFloat()
        rigidBody.type = iterator.next().toInt()

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
                    vertex.position[0] = iterator.readFloat()
                    vertex.position[1] = iterator.readFloat()
                    vertex.position[2] = iterator.readFloat()
                    vertex
                }
                PmxFile.Morph.Type.BONE -> {
                    val bone = PmxFile.Morph.BoneOffset()
                    bone.index = readInt(iterator, setting.boneIndexSize)
                    bone.translation[0] = iterator.readFloat()
                    bone.translation[1] = iterator.readFloat()
                    bone.translation[2] = iterator.readFloat()
                    bone.rotation[0] = iterator.readFloat()
                    bone.rotation[1] = iterator.readFloat()
                    bone.rotation[2] = iterator.readFloat()
                    bone.rotation[3] = iterator.readFloat()
                    bone
                }
                PmxFile.Morph.Type.MATERIAL -> {
                    val material = PmxFile.Morph.MaterialOffset()
                    material.index = readInt(iterator, setting.materialIndexSize)
                    material.operation = iterator.next().toInt()
                    material.diffuse[0] = iterator.readFloat()
                    material.diffuse[1] = iterator.readFloat()
                    material.diffuse[2] = iterator.readFloat()
                    material.diffuse[3] = iterator.readFloat()
                    material.specular[0] = iterator.readFloat()
                    material.specular[1] = iterator.readFloat()
                    material.specular[2] = iterator.readFloat()
                    material.specularlity = iterator.readFloat()
                    material.ambient[0] = iterator.readFloat()
                    material.ambient[1] = iterator.readFloat()
                    material.ambient[2] = iterator.readFloat()
                    material.edgeColor = Color(iterator.readFloat(), iterator.readFloat(), iterator.readFloat(), iterator.readFloat())
                    material.edgeSize = iterator.readFloat()
                    material.textureColor = Color(iterator.readFloat(), iterator.readFloat(), iterator.readFloat(), iterator.readFloat())
                    material.sphereTextureColor = Color(iterator.readFloat(), iterator.readFloat(), iterator.readFloat(), iterator.readFloat())
                    material.toonTextureColor = Color(iterator.readFloat(), iterator.readFloat(), iterator.readFloat(), iterator.readFloat())
                    material
                }
                PmxFile.Morph.Type.UV,
                PmxFile.Morph.Type.ADDITIONAL_UV1,
                PmxFile.Morph.Type.ADDITIONAL_UV2,
                PmxFile.Morph.Type.ADDITIONAL_UV3,
                PmxFile.Morph.Type.ADDITIONAL_UV4 -> {
                    val uv = PmxFile.Morph.UvOffset()
                    uv.index = readInt(iterator, setting.vertexIndexSize)
                    uv.offset[0] = iterator.readFloat()
                    uv.offset[1] = iterator.readFloat()
                    uv.offset[2] = iterator.readFloat()
                    uv.offset[3] = iterator.readFloat()
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
                    impulse.velocity[0] = iterator.readFloat()
                    impulse.velocity[1] = iterator.readFloat()
                    impulse.velocity[2] = iterator.readFloat()
                    impulse.angularTorgue[0] = iterator.readFloat()
                    impulse.angularTorgue[1] = iterator.readFloat()
                    impulse.angularTorgue[2] = iterator.readFloat()
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
        bone.position[0] = iterator.readFloat()
        bone.position[1] = iterator.readFloat()
        bone.position[2] = iterator.readFloat()
        bone.parentIndex = readInt(iterator, setting.boneIndexSize)
        bone.level = iterator.readInt()
        bone.flag = iterator.readShort()
        if (bone.flag.toInt() and 0x01 != 0x00) {
            bone.targetIndex = readInt(iterator, setting.boneIndexSize)
        } else {
            bone.offset[0] = iterator.readFloat()
            bone.offset[1] = iterator.readFloat()
            bone.offset[2] = iterator.readFloat()
        }
        if (bone.flag.toInt() and (0x0100 or 0x0200) != 0x00) {
            bone.grandParentIndex = readInt(iterator, setting.boneIndexSize)
            bone.grantWeight = iterator.readFloat()
        }
        if (bone.flag.toInt() and 0x0400 != 0x00) {
            bone.fixedAxis[0] = iterator.readFloat()
            bone.fixedAxis[1] = iterator.readFloat()
            bone.fixedAxis[2] = iterator.readFloat()
        }
        if (bone.flag.toInt() and 0x0800 != 0x00) {
            bone.localXAxis[0] = iterator.readFloat()
            bone.localXAxis[1] = iterator.readFloat()
            bone.localXAxis[2] = iterator.readFloat()
            bone.localZAxis[0] = iterator.readFloat()
            bone.localZAxis[1] = iterator.readFloat()
            bone.localZAxis[2] = iterator.readFloat()
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
                    ikLink.maxRadian[0] = iterator.readFloat()
                    ikLink.maxRadian[1] = iterator.readFloat()
                    ikLink.maxRadian[2] = iterator.readFloat()
                    ikLink.minRadian[0] = iterator.readFloat()
                    ikLink.minRadian[1] = iterator.readFloat()
                    ikLink.minRadian[2] = iterator.readFloat()
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
        material.diffuse[0] = iterator.readFloat()
        material.diffuse[1] = iterator.readFloat()
        material.diffuse[2] = iterator.readFloat()
        material.diffuse[3] = iterator.readFloat()
        material.specular[0] = iterator.readFloat()
        material.specular[1] = iterator.readFloat()
        material.specular[2] = iterator.readFloat()
        material.specularlity = iterator.readFloat()
        material.ambient[0] = iterator.readFloat()
        material.ambient[1] = iterator.readFloat()
        material.ambient[2] = iterator.readFloat()
        material.flag = iterator.next().toInt()
        material.edgeColor = Color(iterator.readFloat(), iterator.readFloat(), iterator.readFloat(), iterator.readFloat())
        material.edgeSize = iterator.readFloat()
        material.diffuseTextureIndex = readInt(iterator, setting.textureIndexSize)
        material.sphereTextureIndex = readInt(iterator, setting.textureIndexSize)
        material.sphereOpMode = iterator.next().toInt()
        if(iterator.readBool()) {
            material.toonTextureIndex = iterator.next().toInt()
        } else {
            material.toonTextureIndex = readInt(iterator, setting.textureIndexSize)
        }
        material.memo = readString(iterator, setting.encoding)
        material.index = iterator.readInt()

        return material
    }

    private fun readVertex(iterator: ByteIterator, setting: Setting): PmxFile.Vertex {
        val vertex = PmxFile.Vertex()

        vertex.position[0] = iterator.readFloat()
        vertex.position[1] = iterator.readFloat()
        vertex.position[2] = iterator.readFloat()
        vertex.normal[0] = iterator.readFloat()
        vertex.normal[1] = iterator.readFloat()
        vertex.normal[2] = iterator.readFloat()
        vertex.uv[0] = iterator.readFloat()
        vertex.uv[1] = iterator.readFloat()
        for(i in 0 until setting.uv) {
            vertex.uva[i][0] = iterator.readFloat()
            vertex.uva[i][1] = iterator.readFloat()
            vertex.uva[i][0] = iterator.readFloat()
            vertex.uva[i][1] = iterator.readFloat()
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
                skinning.c[0] = iterator.readFloat()
                skinning.c[1] = iterator.readFloat()
                skinning.c[2] = iterator.readFloat()
                skinning.r0[0] = iterator.readFloat()
                skinning.r0[1] = iterator.readFloat()
                skinning.r0[2] = iterator.readFloat()
                skinning.r1[0] = iterator.readFloat()
                skinning.r1[1] = iterator.readFloat()
                skinning.r1[2] = iterator.readFloat()
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

    private fun readSettings(iterator: ByteIterator): Setting {
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

    override fun write(data: PmxFile): ByteArray {
        TODO("Not yet implemented")
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