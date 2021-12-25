package me.liuli.mmd.opengl


import me.liuli.mmd.file.parser.PmxParser
import me.liuli.mmd.model.Model
import me.liuli.mmd.model.pmx.PmxModel
import me.liuli.mmd.opengl.texture.STBTextureLoader
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWFramebufferSizeCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import java.io.File

private var window: Long = 0

fun main(args: Array<String>) {
    println("Loading window...")
    init()

    println("Running...")
    loop()

    println("Destroying window...")
    glfwFreeCallbacks(window)
    glfwDestroyWindow(window)
    glfwTerminate()
    glfwSetErrorCallback(null)?.free()
}

fun loadModel(): Model {
    var time = System.currentTimeMillis()
    val jfile = File("test_files/pmx/test.pmx")
    val pmxFile = PmxParser.read(jfile.readBytes())
    pmxFile.dir = jfile.parentFile
    println("Time cost(READ): ${System.currentTimeMillis() - time}ms")

    time = System.currentTimeMillis()
    val model = PmxModel(pmxFile)
    println("Time cost(SERIALIZATION): ${System.currentTimeMillis() - time}ms")

    return model
}

private fun loop() {
    GL.createCapabilities()

    val model = loadModel()
    val renderer = OpenGLRenderer()
    renderer.textureLoader = STBTextureLoader()

    val time = System.currentTimeMillis()
    renderer.init(model)
    println("Renderer initialized(${System.currentTimeMillis() - time}ms)")

    glEnable(GL_DEPTH_TEST);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    glClearColor(1f, 1f, 1f, 1f)
    while (!glfwWindowShouldClose(window)) {
        glViewport(0, 0, 800, 600)

        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        glOrtho(-2.0, 2.0, -2.0, 2.0, -1.5, 1.5)
        glMatrixMode(GL_MODELVIEW)
        glLoadIdentity()

        glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)

        glPushMatrix()
        glScalef(0.15f, 0.15f, 0.15f)
        glTranslatef(0f, -8f, 0f)
        // rotate the polygon
        glRotatef(glfwGetTime().toFloat() * 25, 0f, 1f, 0f)

        renderer.render()

        glPopMatrix()

        val i = glGetError()

        if (i != 0) {
            println("########## GL ERROR -> $i ##########")
        }

        glfwSwapBuffers(window)
        glfwPollEvents()
    }

    renderer.destroy()
}

private fun init() {
    GLFWErrorCallback.createPrint(System.err).set()
    check(glfwInit()) { "Unable to initialize GLFW" }
    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

    window = glfwCreateWindow(800, 600, "MMDUtils", NULL, NULL)
    if (window == NULL) throw RuntimeException("Failed to create the GLFW window")

    glfwSetKeyCallback(window) { window: Long, key: Int, _: Int, action: Int, _: Int ->
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(window, true)
        }
    }

    glfwSetFramebufferSizeCallback(window, object : GLFWFramebufferSizeCallback() {
        override fun invoke(window: Long, w: Int, h: Int) {
            if (w > 0 && h > 0) {
//                width = w
//                height = h
                println("width: $w, height: $h")
            }
        }
    })

    stackPush().also { stack ->
        val pWidth = stack.mallocInt(1)
        val pHeight = stack.mallocInt(1)
        glfwGetWindowSize(window, pWidth, pHeight)
        val vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())!!
        glfwSetWindowPos(window, (vidMode.width() - pWidth[0]) / 2, (vidMode.height() - pHeight[0]) / 2)
    }

    glfwMakeContextCurrent(window)
    glfwSwapInterval(0)
    glfwShowWindow(window)
}