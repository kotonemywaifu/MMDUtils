package me.liuli.mmd.file

import me.liuli.mmd.file.parser.PmxParser
import java.awt.Color
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f

/**
 * PMX(Polygon Model eXtended) is a 3d model file format used in the program [MikuMikuDance](https://mikumikudance.jp/).
 */
class PmxFile: InteractiveFile() {
    var name = ""
    var englishName = ""
    var comment = ""
    var englishComment = ""
    var uv = 0
    val vertices = mutableListOf<Vertex>()
    val indices = mutableListOf<Int>()
    val textures = mutableListOf<String>()
    val materials = mutableListOf<Material>()
    val bones = mutableListOf<Bone>()
    val morphs = mutableListOf<Morph>()
    val displayFrames = mutableListOf<DisplayFrame>()
    val rigidBodies = mutableListOf<RigidBody>()
    val joints = mutableListOf<Joint>()
    val softBodies = mutableListOf<SoftBody>()

    class Vertex {
        val position = Vector3f()
        val normal = Vector3f()
        val uv = floatArrayOf(0f, 0f)
        val uva = Array(4) { FloatArray(4) }
        var skinning: SkinningType = SkinningBDEF1()
        var edge = 0f

        interface SkinningType {
            val code: Int
        }

        class SkinningBDEF1 : SkinningType {
            override val code: Int = 0
            var boneIndex = 0
        }

        class SkinningBDEF2 : SkinningType {
            override val code: Int = 1
            var boneIndex1 = 0
            var boneIndex2 = 0
            var weight = 0f
        }

        class SkinningBDEF4 : SkinningType {
            override val code: Int = 2
            var boneIndex1 = 0
            var boneIndex2 = 0
            var boneIndex3 = 0
            var boneIndex4 = 0
            var weight1 = 0f
            var weight2 = 0f
            var weight3 = 0f
            var weight4 = 0f
        }

        class SkinningSDEF : SkinningType {
            override val code: Int = 3
            var boneIndex1 = 0
            var boneIndex2 = 0
            var weight = 0f
            var c = Vector3f()
            var r0 = Vector3f()
            var r1 = Vector3f()
        }

        class SkinningQDEF : SkinningType {
            override val code: Int = 4
            var boneIndex1 = 0
            var boneIndex2 = 0
            var boneIndex3 = 0
            var boneIndex4 = 0
            var weight1 = 0f
            var weight2 = 0f
            var weight3 = 0f
            var weight4 = 0f
        }
    }

    class Material {
        var name = ""
        var englishName = ""
        val diffuse = Vector4f()
        val specular = Vector3f()
        var specularlity = 0f
        val ambient = Vector3f()
        var flag = 0
        var edgeColor: Color = Color.WHITE
        var edgeSize = 0f
        var diffuseTextureIndex = 0
        var sphereTextureIndex = 0
        var sphereOpMode = 0
        var toonTextureIndex = 0
        var memo = ""
        var index = 0
    }

    class Bone {
        var name = ""
        var englishName = ""
        val position = Vector3f()
        var parentIndex = 0
        var level = 0
        var flag: Short = 0
        val offset = Vector3f()
        var targetIndex = 0
        var grandParentIndex = 0
        var grantWeight = 0f
        val fixedAxis = Vector3f()
        val localXAxis = Vector3f()
        val localZAxis = Vector3f()
        var key = 0
        var ikTargetBoneIndex = 0
        var ikLoop = 0
        var ikLoopAngleLimit = 0f
        val ikLinks = mutableListOf<IkLink>()

        class IkLink {
            var linkTarget = 0
            var angleLock = 0
            val maxRadian = Vector3f()
            val minRadian = Vector3f()
        }
    }

    class Morph {
        var name = ""
        var englishName = ""
        var category = Category.REVERSE
        var type = Type.GROUP
        val offsets = mutableListOf<Offset>()

        enum class Category(val code: Int) {
            REVERSE(0),
            EYEBROW(1),
            EYE(2),
            MOUTH(3),
            OTHER(4)
        }

        enum class Type(val code: Int) {
            GROUP(0),
            VERTEX(1),
            BONE(2),
            UV(3),
            ADDITIONAL_UV1(4),
            ADDITIONAL_UV2(5),
            ADDITIONAL_UV3(6),
            ADDITIONAL_UV4(7),
            MATERIAL(8),
            FLIP(9),
            IMPULSE(10)
        }

        interface Offset {
            val type: Type
        }

        class GroupOffset : Offset {
            override val type: Type = Type.GROUP
            var index = 0
            var weight = 0f
        }

        class VertexOffset : Offset {
            override val type: Type = Type.VERTEX
            var index = 0
            val position = Vector3f()
        }

        class BoneOffset : Offset {
            override val type: Type = Type.BONE
            var index = 0
            val translation = Vector3f()
            val rotation = Vector4f()
        }

        class UvOffset : Offset {
            override val type: Type = Type.UV
            var index = 0
            var offset = Vector4f()
        }

        class MaterialOffset : Offset {
            override val type: Type = Type.MATERIAL
            var index = 0
            var operation = 0
            val diffuse = Vector4f()
            val specular = Vector3f()
            var specularlity = 0f
            val ambient = Vector3f()
            var edgeColor: Color = Color.WHITE
            var edgeSize = 0f
            var textureColor: Color = Color.WHITE
            var sphereTextureColor: Color = Color.WHITE
            var toonTextureColor: Color = Color.WHITE
        }

        class FlipOffset : Offset {
            override val type: Type = Type.FLIP
            var index = 0
            var value = 0f
        }

        class ImpulseOffset : Offset {
            override val type: Type = Type.IMPULSE
            var index = 0
            var isLocal = false
            val velocity = Vector3f()
            val angularTorgue = Vector3f()
        }
    }

    class DisplayFrame {
        var name = ""
        var englishName = ""
        var flag = 0
        val elements = mutableListOf<Element>()

        class Element {
            var target = 0 // 0: bone, 1: morph
            var index = 0
        }
    }

    class RigidBody {
        var name = ""
        var englishName = ""
        var targetBone = 0
        var group = 0
        var mask: Short = 0
        var shape = 0
        var size = Vector3f()
        var position = Vector3f()
        var orientation = Vector3f()
        var mass = 0f
        var moveAttenuation = 0f
        var rotationAttenuation = 0f
        var repulsion = 0f
        var friction = 0f
        var type = 0
    }

    class Joint {
        var name = ""
        var englishName = ""
        var type = Type.GENERIC6DOF_SPRING
        // params
        var rigidBody1 = 0
        var rigidBody2 = 0
        val position = Vector3f()
        val orientation = Vector3f()
        val moveLimitationMax = Vector3f()
        val moveLimitationMin = Vector3f()
        val rotationLimitationMax = Vector3f()
        val rotationLimitationMin = Vector3f()
        val springTranslateFactor = Vector3f()
        val springRotateFactor = Vector3f()

        enum class Type(val code: Int) {
            GENERIC6DOF_SPRING(0),
            GENERIC6DOF(1),
            POINT_TO_POINT(2),
            CONE_TWIST(3),
            SLIDER(5),
            HINGE(6)
        }
    }

    class SoftBody {
        var name = ""
        var englishName = ""
        var type = Type.TRIMESH
        var materialIndex = 0
        var group = 0
        var collisionGroup: Short = 0
        var mask = Mask.BLINK
        var blinkLength = 0
        var numClusters = 0
        var totalMass = 0f
        var collisionMargin = 0f
        var aeroModel = 0

        // config
        var vcf = 0f
        var dp = 0f
        var dg = 0f
        var lf = 0f
        var pr = 0f
        var vc = 0f
        var df = 0f
        var mt = 0f
        var chr = 0f
        var khr = 0f
        var shr = 0f
        var ahr = 0f

        // cluster
        var srhrCl = 0f
        var skhrCl = 0f
        var sshrCl = 0f
        var srSpltCl = 0f
        var skSpltCl = 0f
        var ssSpltCl = 0f

        // interation
        var vIt = 0
        var pIt = 0
        var dIt = 0
        var cIt = 0

        // material
        var lst = 0f
        var ast = 0f
        var vst = 0f

        enum class Type(val code: Int) {
            TRIMESH(0),
            ROPE(1)
        }

        enum class Mask(val code: Int) {
            BLINK(1),
            CLUSTER(2),
            HYBRID_LINK(4),
        }
    }

    override fun read(byteArray: ByteArray) {
        PmxParser.readToInstance(this, byteArray)
    }

    override fun write(): ByteArray {
        return PmxParser.write(this)
    }
}