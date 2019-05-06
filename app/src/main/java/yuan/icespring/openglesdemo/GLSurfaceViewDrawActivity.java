package yuan.icespring.openglesdemo;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import yuan.icespring.openglesdemo.gles.GlUtil;

public class GLSurfaceViewDrawActivity extends AppCompatActivity {


    GLSurfaceView surfaceView;

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
            0.0f,  0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f  // bottom right
    };

    float color[] = {1.0f, 0.0f, 0.0f, 0.3f};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glsurface_view_draw);
        surfaceView = findViewById(R.id.surface1);
        surfaceView.setEGLContextClientVersion(3);

        surfaceView.setRenderer(new MyRender());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (surfaceView != null) {
            surfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (surfaceView != null) {
            surfaceView.onPause();
        }
    }

    private class MyRender implements GLSurfaceView.Renderer {

        static final int COORDS_PER_VERTEX = 3;
        private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节


        private FloatBuffer vertexBuffer;
        private int mProgram;
        private int mPositionHandle;
        private int mColorHandle;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // 设置背景
            GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);
            // 设置buffer
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoords.length * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            //将坐标数据转换为FloatBuffer，用以传入给OpenGL ES程序
            vertexBuffer = byteBuffer.asFloatBuffer();
            vertexBuffer.put(triangleCoords);
            vertexBuffer.position(0);

            mProgram = GlUtil.createProgram(vertexShaderSource, fragmentShaderSource);

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            //将程序加入到OpenGLES2.0环境
            GLES20.glUseProgram(mProgram);

            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            //获取顶点着色器的vPosition成员句柄
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            //启用三角形顶点的句柄
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            //准备三角形的坐标数据
            GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                    vertexStride, vertexBuffer);
            //获取片元着色器的vColor成员的句柄
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
            //设置绘制三角形的颜色
            GLES20.glUniform4fv(mColorHandle, 1, color, 0);
            //绘制三角形
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
            //禁止顶点数组的句柄
            GLES20.glDisableVertexAttribArray(mPositionHandle);
        }
    }
}
