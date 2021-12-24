package me.liuli.mmd.opengl.texture

import java.io.File

interface ITextureLoader {
    /**
     * Load texture from file.
     */
    fun load(textureFile: File): Int

    /**
     * Bind the texture in OpenGL.
     */
    fun bind(textureIdentifier: Int)

    /**
     * Delete the texture in OpenGL.
     */
    fun delete(textureIdentifier: Int)
}