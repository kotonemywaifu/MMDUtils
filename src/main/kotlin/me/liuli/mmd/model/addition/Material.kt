package me.liuli.mmd.model.addition

import java.awt.Color
import java.io.File
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f

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
    var textureFactor = Vector4f()
    var spTextureFactor = Vector4f()
    var toonTextureFactor = Vector4f()

    fun clone(): Material {
        val mat = Material()

        mat.diffuse = this.diffuse
        mat.alpha = this.alpha
        mat.specular = this.specular
        mat.specularPower = this.specularPower
        mat.ambient = this.ambient
        mat.edgeFlag = this.edgeFlag
        mat.bothFace = this.bothFace
        mat.groundShadow = this.groundShadow
        mat.shadowCaster = this.shadowCaster
        mat.shadowReceiver = this.shadowReceiver
        mat.edgeSize = this.edgeSize
        mat.edgeColor = this.edgeColor
        mat.texture = this.texture
        mat.spTexture = this.spTexture
        mat.toonTexture = this.toonTexture
        mat.spTextureMode = this.spTextureMode

        return mat
    }

    enum class SphereTextureMode {
        NONE,
        MUL,
        ADD
    }
}