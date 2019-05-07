package yuan.icespring.openglesdemo;

import android.graphics.SurfaceTexture;
import android.opengl.GLES30;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;

import java.nio.FloatBuffer;

import yuan.icespring.openglesdemo.gles.EglCore;
import yuan.icespring.openglesdemo.gles.GlUtil;
import yuan.icespring.openglesdemo.gles.WindowSurface;

public class TextureViewDraw2Activity extends AppCompatActivity {


    TextureView textureView;
    private Renderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texture_view_draw);
        textureView = findViewById(R.id.texture1);
        renderer = new Renderer();
        renderer.start();
        textureView.setSurfaceTextureListener(renderer);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (renderer != null) {
            renderer.halt();
        }
    }

    private static class Renderer extends Thread implements TextureView.SurfaceTextureListener {
        private static final String TAG = Renderer.class.getSimpleName();
        private Object mLock = new Object();
        private SurfaceTexture mSurfaceTexture;
        private EglCore mEglCore;
        private boolean mDone;

        // 顶点着色器程序
        private static String vertexShaderSource = "attribute vec4 vPosition;\n" +
                " void main() {\n" +
                "     gl_Position = vPosition;\n" +
                " }";


        // 片段着色器程序
        private static String fragmentShaderSource = "precision mediump float;\n" +
                " uniform vec4 vColor;\n" +
                " void main() {\n" +
                "     gl_FragColor = vColor;\n" +
                " }";

        float triangleCoords[] = {
                0.0f,  0.5f, // top
                -0.5f, -0.5f, // bottom left
                0.5f, -0.5f  // bottom right
        };

        float color[] = {1.0f, 0.0f, 0.0f, 0.3f};

        private int mProgramHandle;
        private int mPositionHandle;
        private FloatBuffer vertexBuffer;
        private int mColorHandle;

        public Renderer() {
            super("Texture_Renderer");
        }

        @Override
        public void run() {
            while (true) {
                SurfaceTexture surfaceTexture = null;

                // Latch the SurfaceTexture when it becomes available or get halt.
                synchronized (mLock) {
                    while (!mDone && mSurfaceTexture == null) {
                        try {
                            mLock.wait();
                        } catch (InterruptedException ie) {
                            throw new RuntimeException(ie);
                        }
                    }
                    if (mDone) {
                        break;
                    }
                }

                mEglCore = new EglCore();
                WindowSurface windowSurface = new WindowSurface(mEglCore, mSurfaceTexture);
                windowSurface.makeCurrent();

                //将坐标数据转换为FloatBuffer，用以传入给OpenGL ES程序
                vertexBuffer = GlUtil.createFloatBuffer(triangleCoords);



                doDraw(windowSurface);

                windowSurface.release();
                mEglCore.release();
                break;
            }

            Log.d(TAG, "Renderer thread exiting");
        }

        private void doDraw(WindowSurface eglSurface) {


            // 使用VAO / VBO 绘制
            int[] vao = new int[1];
            int[] vbo = new int[1];

            GLES30.glGenVertexArrays(1, vao, 0);
            GLES30.glGenBuffers(1, vbo, 0);

            GLES30.glBindVertexArray(vao[0]);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0]);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES30.GL_STATIC_DRAW);
            GLES30.glVertexAttribPointer(mPositionHandle, 2, GLES30.GL_FLOAT, false, 0, 0);

            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
            GLES30.glBindVertexArray(0);

            mProgramHandle = GlUtil.createProgram(vertexShaderSource, fragmentShaderSource);



            // 好了，开始绘制
            GLES30.glUseProgram(mProgramHandle);
            // 先启用vao，这样我们之前绑定的顶点数据才能起效
            GLES30.glBindVertexArray(vao[0]);
            // 顶点属性默认都是禁用的，只有启用之后，绘制才能生效
            GLES30.glEnableVertexAttribArray(mPositionHandle);

            while (true) {
                synchronized (mLock) {
                    SurfaceTexture surfaceTexture = mSurfaceTexture;
                    if (surfaceTexture == null) {
                        return;
                    }
                }


                GLES30.glClearColor(0.7f, 0.5f, 0.1f, 1.0f);
                GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
                GLES30.glBindVertexArray(vao[0]);

                //获取片元着色器的vColor成员的句柄
                mColorHandle = GLES30.glGetUniformLocation(mProgramHandle, "vColor");
                //设置绘制三角形的颜色
                GLES30.glUniform4fv(mColorHandle, 1, color, 0);
                //绘制三角形
                GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);

                eglSurface.swapBuffers();

            }
        }

        /**
         * Tells the thread to stop running.
         */
        public void halt() {
            synchronized (mLock) {
                mDone = true;
                mLock.notify();
            }
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture st, int width, int height) {
            Log.d(TAG, "onSurfaceTextureAvailable(" + width + "x" + height + ")");
            synchronized (mLock) {
                mSurfaceTexture = st;
                mLock.notify();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture st, int width, int height) {
            Log.d(TAG, "onSurfaceTextureSizeChanged(" + width + "x" + height + ")");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture st) {
            Log.d(TAG, "onSurfaceTextureDestroyed");

            synchronized (mLock) {
                mSurfaceTexture = null;
            }
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture st) {

        }
    }

}
