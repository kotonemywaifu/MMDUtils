package me.liuli.mmd.model.addition

import me.liuli.mmd.utils.inverse
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f

open class Node {
    var name: String = ""
    var child: Node? = null
    val translate = Vector3f()
    val rotate = Vector4f()
    val scale = Vector3f(1f, 1f, 1f)
    var global = Matrix4f()
    var inverseInit = Matrix4f()
    var initTranslate = Vector3f()
        private set
    var initRotate = Vector4f()
        private set
    var initScale = Vector3f()
        private set

    fun calculateInverseInitTransform() {
        inverseInit = (global.clone() as Matrix4f).inverse()
    }

    fun saveInitialTRS() {
        initTranslate = translate.clone() as Vector3f
        initRotate = rotate.clone() as Vector4f
        initScale = scale.clone() as Vector3f
    }
}