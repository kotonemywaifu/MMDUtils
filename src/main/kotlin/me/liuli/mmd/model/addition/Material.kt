package me.liuli.mmd.model.addition

import java.awt.Color
import java.io.File
import javax.vecmath.Vector3f

open class Material {
    var diffuse = Vector3f()
    var alpha = 0f
    var specular = Vector3f()
    var specularPower = 0f
    var ambient = Vector3f()
    var edgeFlag = false
    var bothFace = false
    var groundShadow = false
    var shadowCaster = false
    var shadowReceiver = false
    var edgeSize = 0f
    var edgeColor = Color.WHITE
    var texture: File? = null
    var spTexture: File? = null
    var toonTexture: File? = null
    var spTextureMode = SphereTextureMode.NONE

    enum class SphereTextureMode {
        NONE,
        MUL,
        ADD
    }
}