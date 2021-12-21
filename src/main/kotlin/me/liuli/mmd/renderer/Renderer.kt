package me.liuli.mmd.renderer

import me.liuli.mmd.model.Model

abstract class Renderer {

    /**
     * this method will be called to initialize the renderer
     */
    abstract fun init(model: Model, options: Map<String, String> = emptyMap())

    /**
     * this method will be called to render the model
     */
    abstract fun render()

    /**
     * this method will be called to release the renderer
     */
    abstract fun destroy()
}