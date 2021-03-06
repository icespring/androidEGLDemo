package yuan.icespring.openglesdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * 使用SurfaceView，在native层进行EGL环境和GL线程的创建，并在native层直接绘制
 */
public class SurfaceViewNatvieDrawActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view_natvie_draw);

        surfaceView = findViewById(R.id.surface2);
        surfaceView.getHolder().addCallback(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        GLESlib.nativeOnStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GLESlib.nativeOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GLESlib.nativeOnPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        GLESlib.nativeOnStop();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        GLESlib.nativeSetSurface(holder.getSurface());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        GLESlib.nativeSetSurface(null);
    }

}
