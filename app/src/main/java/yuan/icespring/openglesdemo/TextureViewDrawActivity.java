package yuan.icespring.openglesdemo;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import yuan.icespring.openglesdemo.gles.EglCore;
import yuan.icespring.openglesdemo.gles.GlUtil;
import yuan.icespring.openglesdemo.gles.WindowSurface;

public class TextureViewDrawActivity extends AppCompatActivity {


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

        private static String vertexShaderSource = "attribute vec4 vPosition;\n" +
                " void main() {\n" +
                "     gl_Position = vPosition;\n" +
                " }";


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
                            throw new RuntimeException(ie);     // not expected
                        }
                    }
                    if (mDone) {
                        break;
                    }
                }

                mEglCore = new EglCore();
                WindowSurface windowSurface = new WindowSurface(mEglCore, mSurfaceTexture);
                windowSurface.makeCurrent();

                // 设置buffer
                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoords.length * 4);
                byteBuffer.order(ByteOrder.nativeOrder());
                //将坐标数据转换为FloatBuffer，用以传入给OpenGL ES程序
                vertexBuffer = byteBuffer.asFloatBuffer();
                vertexBuffer.put(triangleCoords);
                vertexBuffer.position(0);
                mProgramHandle = GlUtil.createProgram(vertexShaderSource, fragmentShaderSource);

                doDraw(windowSurface);

                windowSurface.release();
                mEglCore.release();
                break;
            }

            Log.d(TAG, "Renderer thread exiting");
        }

        private void doDraw(WindowSurface eglSurface) {

            while (true) {
                synchronized (mLock) {
                    SurfaceTexture surfaceTexture = mSurfaceTexture;
                    if (surfaceTexture == null) {
                        return;
                    }
                }
                GLES20.glUseProgram(mProgramHandle);

                GLES20.glClearColor(0.7f, 0.5f, 0.1f, 1.0f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);


                //获取顶点着色器的vPosition成员句柄
                mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "vPosition");
                //启用三角形顶点的句柄
                GLES20.glEnableVertexAttribArray(mPositionHandle);
                //准备三角形的坐标数据
                GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
                //获取片元着色器的vColor成员的句柄
                mColorHandle = GLES20.glGetUniformLocation(mProgramHandle, "vColor");
                //设置绘制三角形的颜色
                GLES20.glUniform4fv(mColorHandle, 1, color, 0);
                //绘制三角形
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
                //禁止顶点数组的句柄
                GLES20.glDisableVertexAttribArray(mPositionHandle);

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
