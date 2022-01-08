package me.liuli.mmd.model.addition

import java.awt.Color
import java.io.File
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f

open class Material {
    val diffuse = Vector3f(1f, 1f, 1f)
    var alpha = 1f
    val specular = Vector3f(0f, 0f, 0f)
    var specularPower = 1f
    val ambient = Vector3f(0.2f, 0.2f, 0.2f)
    var edgeFlag = false
    var bothFace = false
    var groundShadow = true
    var shadowCaster = true
    var shadowReceiver = true
    var edgeSize = 0f
    var edgeColor = Color.WHITE
    var texture: File? = null
    var spTexture: File? = null
    var toonTexture: File? = null
    var spTextureMode = SphereTextureMode.NONE
    val textureFactor = Vector4f(1f, 1f, 1f, 1f)
    val spTextureFactor = Vector4f(1f, 1f, 1f, 1f)
    val toonTextureFactor = Vector4f(1f, 1f, 1f, 1f)

    fun clone(): Material {
        val mat = Material()

        mat.diffuse.set(this.diffuse)
        mat.alpha = this.alpha
        mat.specular.set(this.specular)
        mat.specularPower = this.specularPower
        mat.ambient.set(this.ambient)
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
        mat.textureFactor.set(this.textureFactor)
        mat.spTextureFactor.set(this.spTextureFactor)
        mat.toonTextureFactor.set(this.toonTextureFactor)

        return mat
    }

    enum class SphereTextureMode {
        NONE,
        MUL,
        ADD
    }
}