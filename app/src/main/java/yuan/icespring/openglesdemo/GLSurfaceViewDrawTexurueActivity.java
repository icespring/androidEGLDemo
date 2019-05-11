package yuan.icespring.openglesdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import yuan.icespring.openglesdemo.gles.GlUtil;

/**
 * 使用GLSurfaceView创建的EGL环境和GL线程进行绘制
 */
public class GLSurfaceViewDrawTexurueActivity extends AppCompatActivity {


    GLSurfaceView surfaceView;

    private static String vertextShaderSource2 =
            "attribute vec4 vPosition;\n" +
                    "attribute vec2 vCoordinate;\n" +
                    "uniform mat4 vMatrix;\n" +
                    "\n" +
                    "varying vec2 aCoordinate;\n" +
                    "\n" +
                    "void main(){\n" +
                    "    gl_Position=vPosition;\n" +
                    "    aCoordinate=vCoordinate;\n" +
                    "}\n";


    private static String fragmentShaderSource2 =
            "precision mediump float;\n" +
                    "\n" +
                    "uniform sampler2D vTexture;\n" +
                    "varying vec2 aCoordinate;\n" +
                    "\n" +
                    "void main(){\n" +
                    "    gl_FragColor=texture2D(vTexture,aCoordinate);\n" +
                    "}\n";

    float triangleCoords[] = {
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            0.0f, 1.0f, 0.0f
    };


    private final float[] sCoord = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

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

        private FloatBuffer vertexBuffer;
        private int mProgram;

        private int glHPosition;
        private int glHTexture;
        private int glHCoordinate;
        private Bitmap mBitmap;
        private int textureId;
        private FloatBuffer bCoord;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // 设置背景
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            //将坐标数据转换为FloatBuffer，用以传入给OpenGL ES程序
            vertexBuffer = GlUtil.createFloatBuffer(triangleCoords);
            bCoord = GlUtil.createFloatBuffer(sCoord);
            mProgram = GlUtil.createProgram(vertextShaderSource2, fragmentShaderSource2);
            //获取顶点着色器的vPosition成员句柄
            glHPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
            glHCoordinate = GLES20.glGetAttribLocation(mProgram, "vCoordinate");
            glHTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scene);
            textureId = createTexture();
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


            //启用三角形顶点的句柄
            GLES20.glEnableVertexAttribArray(glHPosition);
            GLES20.glEnableVertexAttribArray(glHCoordinate);
            GLES20.glUniform1i(glHTexture, 0);


            //准备三角形的坐标数据
            GLES20.glVertexAttribPointer(glHPosition, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                    0, vertexBuffer);
            //获取片元着色器的vColor成员的句柄
            GLES20.glVertexAttribPointer(glHCoordinate, 2, GLES20.GL_FLOAT, false, 0, bCoord);
            //绘制三角形
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
            //禁止顶点数组的句柄
            GLES20.glDisableVertexAttribArray(glHPosition);
            GLES20.glDisableVertexAttribArray(glHCoordinate);
        }

        private int createTexture() {
            int[] texture = new int[1];
            if (mBitmap != null && !mBitmap.isRecycled()) {
                //生成纹理
                GLES20.glGenTextures(1, texture, 0);
                //生成纹理
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
                //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
                //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
                //根据以上指定的参数，生成一个2D纹理
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
                return texture[0];
            }
            return 0;
        }
    }
}
