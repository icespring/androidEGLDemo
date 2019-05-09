package yuan.icespring.openglesdemo;

import android.graphics.SurfaceTexture;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

public class TextureViewNativeDrawActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {


    private TextureView textureView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texture_view_draw);
        textureView = findViewById(R.id.texture1);

        textureView.setSurfaceTextureListener(this);

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
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Surface surfaceWrapper = new Surface(surface);
        GLESlib.nativeSetSurface(surfaceWrapper);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        GLESlib.nativeSetSurface(null);
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
