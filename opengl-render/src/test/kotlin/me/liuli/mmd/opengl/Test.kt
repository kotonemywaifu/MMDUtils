package me.liuli.mmd.opengl


import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL


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

private fun onRender() {
    glColor3f(1f, 0f, 0f)
    glPushMatrix()
    glTranslatef(400f, 300f, 0f)
    glScalef(50f, 50f, 0f)
    glRotatef((glfwGetTime() * 50f).toFloat(), 1f, 0f, 0f)
    glRotatef((glfwGetTime() * 50f).toFloat(), 0f, 1f, 0f)
    glLineWidth(1f)
    glBegin(GL_LINE_STRIP)

    glVertex3f(0.0f, 0.0f, 0.0f)
    glVertex3f(1.0f, 0.0f, 0.0f)
    glVertex3f(1.0f, 1.0f, 0.0f)
    glVertex3f(0.0f, 1.0f, 0.0f)
    glVertex3f(0.0f, 0.0f, 0.0f)

    glEnd()
    glPopMatrix()
}

private fun loop() {
    GL.createCapabilities()
    glClearColor(1f, 1f, 1f, 1f)
    while (!glfwWindowShouldClose(window)) {
        glViewport(0, 0, 800, 600)
        glClear(GL_COLOR_BUFFER_BIT)

        glMatrixMode(GL_PROJECTION)
        glLoadIdentity();
        glOrtho(0.0, 800.0, 600.0, 0.0, -1.0, 1.0)
        glMatrixMode(GL_MODELVIEW)
        glLoadIdentity()

        onRender()

        glfwSwapBuffers(window)
        glfwPollEvents()
    }
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

    stackPush().also { stack ->
        val pWidth = stack.mallocInt(1)
        val pHeight = stack.mallocInt(1)
        glfwGetWindowSize(window, pWidth, pHeight)
        val vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())!!
        glfwSetWindowPos(window, (vidMode.width() - pWidth[0]) / 2, (vidMode.height() - pHeight[0]) / 2)
    }

    glfwMakeContextCurrent(window)
    glfwSwapInterval(1)
    glfwShowWindow(window)
}