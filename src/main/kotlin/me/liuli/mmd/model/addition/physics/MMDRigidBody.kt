package me.liuli.mmd.model.addition.physics

import com.bulletphysics.collision.dispatch.CollisionFlags
import com.bulletphysics.dynamics.RigidBody
import me.liuli.mmd.model.addition.Node
import me.liuli.mmd.utils.inverse
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f

class MMDRigidBody(val btRigidBody: RigidBody,
                   val activeMotionState: MMDMotionState?,
                   val kinematicMotionState: KinematicMotionState,
                   val rigidBodyType: Type, val group: Int, val mask: Short, val node: Node?, val name: String) {

    fun setActivation(state: Boolean) {
        if(rigidBodyType != Type.KINEMATIC) {
            if(state) {
                btRigidBody.collisionFlags = btRigidBody.collisionFlags and CollisionFlags.KINEMATIC_OBJECT.inv()
                btRigidBody.motionState = activeMotionState
            } else {
                btRigidBody.collisionFlags = btRigidBody.collisionFlags or CollisionFlags.KINEMATIC_OBJECT
                btRigidBody.motionState = kinematicMotionState
            }
        } else {
            btRigidBody.motionState = kinematicMotionState
        }
    }

    fun resetTransform() {
        activeMotionState?.reset()
    }

    fun reflectGlobalTransform() {
        activeMotionState?.reflectGlobalTransform()
        kinematicMotionState.reflectGlobalTransform()
    }

    fun calcLocalTransform() {
        if(node != null) {
            if(node.parent != null) {
                node.local = (node.parent!!.global.clone() as Matrix4f).inverse().apply { mul(node.global) }
            } else {
                node.local = node.global.clone() as Matrix4f
            }
        }
    }

    fun reset(physics: PhysicsManager) {
        val cache = physics.world.pairCache
        if (cache != null) {
            val dispatcher = physics.world.dispatcher
            cache.cleanProxyFromPairs(btRigidBody.broadphaseHandle, dispatcher)
        }
        btRigidBody.setAngularVelocity(Vector3f())
        btRigidBody.setLinearVelocity(Vector3f())
        btRigidBody.clearForces()
    }

    enum class Type {
        KINEMATIC,
        DYNAMIC,
        ALIGNED
    }
}