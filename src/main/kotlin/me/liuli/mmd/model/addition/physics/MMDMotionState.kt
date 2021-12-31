package me.liuli.mmd.model.addition.physics

import com.bulletphysics.linearmath.MotionState
import com.bulletphysics.linearmath.Transform
import me.liuli.mmd.model.addition.Node
import me.liuli.mmd.model.pmx.PmxNode
import me.liuli.mmd.utils.inverse
import me.liuli.mmd.utils.mat4f
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f

abstract class MMDMotionState : MotionState() {

    abstract fun reset()

    abstract fun reflectGlobalTransform()
}

class DefaultMotionState(transform: Matrix4f) : MMDMotionState() {

    var transform = Transform(transform)
    val initialTransform = Transform(transform) // bullet transform can't be cloned

    override fun getWorldTransform(out: Transform): Transform {
        return transform
    }

    override fun setWorldTransform(worldTrans: Transform) {
        transform = worldTrans
    }

    override fun reset() {
        transform = initialTransform
    }

    override fun reflectGlobalTransform() {}
}

open class DynamicMotionState(protected val node: Node, protected val offset: Matrix4f, protected val overrideMat: Boolean = true) : MMDMotionState() {

    protected val invOffset = offset.inverse()
    protected lateinit var transform: Transform

    init {
        reset()
    }

    override fun reset() {
        transform = Transform((node.global.clone() as Matrix4f).apply { mul(offset) })
    }

    override fun reflectGlobalTransform() {
        val world = mat4f(0f)
        transform = Transform(world)
        val global = world.apply { mul(invOffset) }
        if(overrideMat) {
            node.global = global
            node.updateChildTransform()
        }
    }

    override fun getWorldTransform(out: Transform): Transform {
        return transform
    }

    override fun setWorldTransform(worldTrans: Transform) {
        transform = worldTrans
    }
}

class DynamicAndBoneMergeMotionState(node: Node, offset: Matrix4f, overrideMat: Boolean = true) : DynamicMotionState(node, offset, overrideMat) {

    override fun reflectGlobalTransform() {
        val world = mat4f(0f)
        transform = Transform(world)
        val global = world.apply { mul(invOffset) }
        val nGlobal = node.global
        global.set(3f, Vector3f(nGlobal.m03, nGlobal.m13, nGlobal.m23))
        if(overrideMat) {
            node.global = global
            node.updateChildTransform()
        }
    }
}

class KinematicMotionState(private val node: PmxNode?, private val offset: Matrix4f) : MMDMotionState() {

    override fun getWorldTransform(out: Transform?): Transform {
        return Transform(if(node == null) {
            offset
        } else {
            (node.global.clone() as Matrix4f).apply { mul(offset) }
        })
    }

    override fun setWorldTransform(worldTrans: Transform?) {}

    override fun reset() {}

    override fun reflectGlobalTransform() {}
}