package me.liuli.mmd.opengl

import me.liuli.mmd.model.Model
import me.liuli.mmd.opengl.texture.ITextureLoader
import me.liuli.mmd.renderer.Renderer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL15
import java.io.File
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.properties.Delegates

/**
 * renderer mmd model with opengl
 */
class OpenGLRenderer : Renderer() {
    lateinit var textureLoader: ITextureLoader

    private var model: Model? = null

    private val textures = mutableMapOf<File, Int>()

    override fun init(model: Model, options: Map<String, String>) {
        if(this.model != null) {
            destroy()
        }

        model.materials.forEach {
            if(it.texture != null && it.texture!!.exists() && !textures.containsKey(it.texture!!)) {
                textures[it.texture!!] = textureLoader.load(it.texture!!)
            }
        }

        this.model = model
    }

    override fun render() {
        model ?: throw IllegalStateException("Please call init first")

        // get original GL_CULL_FACE enable state
        val originGlCullFace = GL11.glIsEnabled(GL11.GL_CULL_FACE)
        // get original GL_BLEND enable state
        val originGlBlend = GL11.glIsEnabled(GL11.GL_BLEND)
        // get original GL_TEXTURE_2D enable state
        val originGlTexture2D = GL11.glIsEnabled(GL11.GL_TEXTURE_2D)

        // enable GL_BLEND
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_TEXTURE_2D)

        GL11.glColor4f(1f, 1f, 1f, 1f)

        for(subMesh in model!!.subMeshes) {
            val material = model!!.materials[subMesh.materialId]

            if (material.bothFace) {
                GL11.glDisable(GL11.GL_CULL_FACE)
            } else {
                GL11.glEnable(GL11.GL_CULL_FACE)
            }

            val tex = textures[material.texture] ?: 0
            textureLoader.bind(tex)

            GL11.glBegin(GL11.GL_TRIANGLES)
            for(i in subMesh.beginIndex until (subMesh.beginIndex + subMesh.vertexCount)) {
                if(i !in 0 until model!!.indices.size) {
                    continue
                }
                val index = model!!.indices[i]
                if(index !in 0 until model!!.positions.size) {
                    continue
                }
                val pos = model!!.positions[index]
                val nor = model!!.normals[index]
                val uv = model!!.uvs[index]

                GL11.glVertex3f(pos.x, pos.y, pos.z)
                GL11.glNormal3f(nor.x, nor.y, nor.z)
                GL11.glTexCoord2f(uv.x, uv.y)
            }
            GL11.glEnd()
        }

        if(originGlCullFace) {
            GL11.glEnable(GL11.GL_CULL_FACE)
        } else {
            GL11.glDisable(GL11.GL_CULL_FACE)
        }
        // restore original GL_BLEND enable state
        if(!originGlBlend) {
            GL11.glDisable(GL11.GL_BLEND)
        }
        // restore original GL_TEXTURE_2D enable state
        if(!originGlTexture2D) {
            GL11.glDisable(GL11.GL_TEXTURE_2D)
        }
    }

    override fun destroy() {
        model ?: throw IllegalStateException("Please call init first")

        model = null

        // delete textures
        textures.forEach {
            textureLoader.delete(it.value)
        }
    }
}