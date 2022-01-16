package me.liuli.mmd.model.addition.physics

import com.bulletphysics.collision.broadphase.BroadphaseProxy
import com.bulletphysics.collision.broadphase.DbvtBroadphase
import com.bulletphysics.collision.broadphase.OverlapFilterCallback
import com.bulletphysics.collision.dispatch.CollisionDispatcher
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration
import com.bulletphysics.collision.shapes.StaticPlaneShape
import com.bulletphysics.dynamics.DiscreteDynamicsWorld
import com.bulletphysics.dynamics.RigidBody
import com.bulletphysics.dynamics.RigidBodyConstructionInfo
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver
import com.bulletphysics.linearmath.DefaultMotionState
import com.bulletphysics.linearmath.Transform
import javax.vecmath.Vector3f
import kotlin.experimental.and

class PhysicsManager {
    val world: DiscreteDynamicsWorld
    val mmdRigidBodies = mutableListOf<MMDRigidBody>()

    init {
        val collisionConfig = DefaultCollisionConfiguration()

        world = DiscreteDynamicsWorld(CollisionDispatcher(collisionConfig),
            DbvtBroadphase(),
            SequentialImpulseConstraintSolver(),
            collisionConfig)
        world.setGravity(Vector3f(0f, -9.8f, 0f))

        val groundRigidBody = RigidBody(RigidBodyConstructionInfo(0f,
            DefaultMotionState(Transform().apply { setIdentity() }),
            StaticPlaneShape(Vector3f(0f, 1f, 0f), 0f),
            Vector3f()))
        world.addRigidBody(groundRigidBody)

        val filterCallback = FilterCallback()
        filterCallback.nonFilterProxy.add(groundRigidBody.broadphaseProxy)
        world.pairCache.setOverlapFilterCallback(filterCallback)
    }

    fun addRigidBody(mmdRigidBody: MMDRigidBody) {
        mmdRigidBodies.add(mmdRigidBody)
        world.addRigidBody(mmdRigidBody.btRigidBody, (1 shl mmdRigidBody.group).toShort(), mmdRigidBody.mask)
    }

    fun update(delta: Float) {
        world.stepSimulation(delta, 10)
    }

    class FilterCallback : OverlapFilterCallback() {
        val nonFilterProxy = mutableListOf<BroadphaseProxy>()

        override fun needBroadphaseCollision(proxy0: BroadphaseProxy, proxy1: BroadphaseProxy): Boolean {
            if (nonFilterProxy.contains(proxy0) || nonFilterProxy.contains(proxy1)) {
                return true
            }

            return ((proxy0.collisionFilterGroup and proxy1.collisionFilterMask) != 0.toShort())
                    && ((proxy1.collisionFilterGroup and proxy0.collisionFilterMask) != 0.toShort())
        }
    }
}