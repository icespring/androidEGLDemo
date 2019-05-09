package yuan.icespring.openglesdemo;

import android.graphics.SurfaceTexture;
import android.view.Surface;

public class GLESlib {

    public static native void nativeOnStart();
    public static native void nativeOnResume();
    public static native void nativeOnPause();
    public static native void nativeOnStop();
    public static native void nativeSetSurface(Surface surface);


    static {
        System.loadLibrary("nativeegl");
    }


}
