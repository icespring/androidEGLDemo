//
// Created by icespring on 2019-05-08.
//
#ifndef RENDERER_H
#define RENDERER_H

#include <pthread.h>
#include <EGL/egl.h>
#include <GLES/gl.h>


class Renderer {

public:
    Renderer();
    virtual ~Renderer();

    void start();
    void stop();
    void setWindow(ANativeWindow* window);


private:

    enum RenderThreadMessage {
        MSG_NONE = 0,
        MSG_WINDOW_SET,
        MSG_RENDER_LOOP_EXIT
    };

    pthread_t _threadId;
    pthread_mutex_t _mutex;
    enum RenderThreadMessage _msg;

    ANativeWindow* _window;

    EGLDisplay _display;
    EGLSurface _surface;
    EGLContext _context;
    GLfloat _angle;
    GLuint _programHandle;
    GLuint _positionHandle;

    void renderLoop();

    bool initialize();
    void destroy();

    void drawFrame();

    bool initProgram(int w, int h);
    void drawFrames();
    GLuint loadShader(GLenum shaderType, const char *pSource);

    GLuint createProgram(const char *pVertexSource, const char *pFragmentSource);

    static void* threadStartCallback(void *myself);

    const char *vertexShaderCode =
            "attribute vec4 vPosition;\n"
            "void main() {\n"
            "  gl_Position = vPosition;\n"
            "}\n";

    const char *fragmentShaderCode =
            "precision mediump float;\n"
            "void main() {\n"
            "  gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);\n"
            "}\n";
};

#endif // RENDERER_H