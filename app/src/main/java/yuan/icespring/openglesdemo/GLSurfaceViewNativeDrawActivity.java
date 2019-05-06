package yuan.icespring.openglesdemo;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.FrameLayout;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLSurfaceViewNativeDrawActivity extends AppCompatActivity {

    GLSurfaceView mView;

    @Override protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mView = new GLSurfaceView(this);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, displayMetrics);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(size, size);
        mView.setEGLContextClientVersion(3);
        mView.setRenderer(new Renderer());
        setContentView(mView, layoutParams);
    }

    @Override protected void onPause() {
        super.onPause();
        mView.onPause();
    }

    @Override protected void onResume() {
        super.onResume();
        mView.onResume();
    }


    private static class Renderer implements GLSurfaceView.Renderer {
        public void onDrawFrame(GL10 gl) {
            GL2JNILib.draw();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GL2JNILib.init(width, height);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // Do nothing.
        }
    }
}
