package me.liuli.mmd.model.addition

import java.awt.Color
import java.io.File
import javax.vecmath.Vector3f

class Material {
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

    enum class DrawMode(val value: Int) {
        BOTH_FACE(0x01),
        GROUND_SHADOW(0x02),
        CAST_SELF_SHADOW(0x04),
        RECEIVE_SELF_SHADOW(0x08),
        DRAW_EDGE(0x10),
        VERTEX_COLOR(0x20),
        DRAW_POINT(0x40),
        DRAW_LINE(0x80),
    }
}