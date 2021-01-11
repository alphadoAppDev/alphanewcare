package kr.co.alphacare.capture.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import kr.co.alphacare.URSConfig;
import kr.co.alphacare.utils.URSUtils;

import org.opencv.core.Size;

import java.security.Policy;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback
{
    private static final int MIN_WIDTH                      = 0;
    private static final int MIN_HEIGHT                     = 0;

    private static final int MAX_PICTURE_WIDTH              = 4096;
    private static final int MAX_PICTURE_HEIGHT             = 4096;

    private static final int MAX_PREVIEW_WIDTH              = 1280;
    private static final int MAX_PREVIEW_HEIGHT             = 1280;

    private Context context;

    private Camera camera;
    private SurfaceHolder holder;
    private boolean shutterSound;

    public interface OnDrawListener
    {
        void onDraw(CameraPreview preview, Canvas canvas);
    }

    private OnDrawListener onDrawListener;
    private Camera.PreviewCallback previewCallback;
    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback()
    {
        @Override
        public void onShutter()
        {

        }
    };

    public CameraPreview(Context context)
    {
        super(context);
        this.context = context;

        // onDraw 호출 받을 수 있도록
        setWillNotDraw(false);

        init();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        try
        {
            camera = Camera.open();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e(URSConfig.APP_NAME, "Camera open fail!");
            return;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        if (holder.getSurface() == null)
        {
            Log.e(URSConfig.APP_NAME, "Surface is null");
            return;
        }

        if (camera == null)
        {
            Log.e(URSConfig.APP_NAME, "Camera is null");
            return;
        }

        camera.stopPreview();

        try
        {
            Camera.Parameters params = camera.getParameters();

            int degrees = getCameraDisplayOrientation();
            camera.setDisplayOrientation(degrees);
            params.setRotation(degrees);

            Camera.Size pictureSize = getMaxPictureSize();
            params.setPictureSize(pictureSize.width, pictureSize.height);

            Camera.Size previewSize = getOptimalPreviewSize(params.getSupportedPreviewSizes(), pictureSize.width, pictureSize.height);
            params.setPreviewSize(previewSize.width, previewSize.height);

            setViewSize(previewSize);

            String focusMode = getFocusMode();
            if(focusMode != null)
            {
                params.setFocusMode(focusMode);
            }

            //params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

            camera.setParameters(params);
            camera.setPreviewDisplay(holder);
            camera.startPreview();

            // 실시간 이미지 처리
            if (previewCallback != null)
            {
                camera.setPreviewCallback(previewCallback);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        if (camera == null)
        {
            return;
        }

        if (previewCallback != null)
        {
            camera.setPreviewCallback(null);
        }

        camera.stopPreview();

        if (getFocusMode() != null)
        {
            camera.cancelAutoFocus();
        }

        camera.release();
        camera = null;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (onDrawListener != null)
        {
            onDrawListener.onDraw(this, canvas);
        }
    }

    private void init()
    {
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        shutterSound = true;
    }

    /**
     * 카메라 사용 여부
     */
    public boolean isAvailableCamera()
    {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            return false;
        }

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++)
        {
            Camera.getCameraInfo(i, cameraInfo);

            // 후방 카메라
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
            {
                return true;
            }
        }

        return true;
    }

    private void setViewSize(Camera.Size preview)
    {
        Size previewSize = new Size(preview.width, preview.height);
        if (getCameraDisplayOrientation() % 180 != 0)
        {
            previewSize.width = preview.height;
            previewSize.height = preview.width;
        }

        Size viewSize = new Size(((View)getParent()).getWidth(), ((View)getParent()).getHeight());

        Size tempSize = new Size(viewSize.width, viewSize.width * previewSize.height / (float)previewSize.width);
        float x = (float)(viewSize.width - tempSize.width) / 2.0f;
        float y = (float) (viewSize.height - tempSize.height) / 2.0f;

        if (x >= 0 && y >= 0)
        {
            tempSize = new Size(viewSize.height * previewSize.width / (float)previewSize.height, viewSize.height);
            x = (float)(viewSize.width - tempSize.width) / 2.0f;
            y = (float)(viewSize.height - tempSize.height) / 2.0f;
        }

        setX(x);
        setY(y);

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = (int)tempSize.width;
        layoutParams.height = (int)tempSize.height;

        setLayoutParams(layoutParams);
    }

    /**
     * 카메라 Display 방향 값
     * @return angle
     */
    public int getCameraDisplayOrientation()
    {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++)
        {
            Camera.getCameraInfo(i, cameraInfo);

            if (cameraInfo.facing != Camera.CameraInfo.CAMERA_FACING_BACK)
            {
                continue;
            }

            int rotation = ((Activity)context).getWindowManager().getDefaultDisplay().getRotation();
            int degree = 0;
            switch (rotation)
            {
                case Surface.ROTATION_0:    degree = 0;    break;
                case Surface.ROTATION_90:   degree = 90;   break;
                case Surface.ROTATION_180:  degree = 180;  break;
                case Surface.ROTATION_270:  degree = 270;  break;
            }

            return (cameraInfo.orientation - degree + 360) % 360;
        }

        return 0;
    }

    private String getFocusMode()
    {
        List<String> focusModes = camera.getParameters().getSupportedFocusModes();
        for(String mode : focusModes)
        {
            if(mode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
            {
                return Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
            }
        }

        return null;
    }

    private Camera.Size getMaxPreviewSize()
    {
        Camera.Size result = null;

        for (Camera.Size size : camera.getParameters().getSupportedPreviewSizes())
        {
            if (size.width == size.height ||
                    size.width <= MIN_WIDTH || size.height <= MIN_HEIGHT ||
                    size.width > MAX_PREVIEW_WIDTH || size.height > MAX_PREVIEW_HEIGHT)
            {
                continue;
            }

            if (result == null)
            {
                result = size;
            }
            else
            {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;
                if (newArea > resultArea)
                {
                    result = size;
                }
            }
        }

        return result;
    }

    private Camera.Size getMaxPictureSize()
    {
        Camera.Size result = null;

        for(Camera.Size size : camera.getParameters().getSupportedPictureSizes())
        {
            if(size.width == size.height ||
                    size.width <= MIN_WIDTH || size.height <= MIN_HEIGHT ||
                    size.width > MAX_PICTURE_WIDTH || size.height > MAX_PICTURE_HEIGHT)
            {
                continue;
            }

            if(result == null)
            {
                result = size;
            }
            else
            {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;
                if(newArea > resultArea)
                {
                    result = size;
                }
            }
        }

        return result;
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height)
    {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) width / height;

        if(sizes == null)
        {
            return null;
        }

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = height;
        for(Camera.Size size : sizes)
        {
            double ratio = (double) size.width / size.height;
            if(Math.abs(ratio-targetRatio) > ASPECT_TOLERANCE)
            {
                continue;
            }

            if(Math.abs(size.height - targetHeight) < minDiff)
            {
                if(size.width > MIN_WIDTH && size.height > MIN_WIDTH &&
                        size.width <= MAX_PREVIEW_WIDTH && size.height <= MAX_PREVIEW_HEIGHT)
                {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        if(optimalSize == null)
        {
            minDiff = Double.MAX_VALUE;

            for(Camera.Size size : sizes)
            {
                if(Math.abs(size.height - targetHeight) < minDiff)
                {
                    if(size.width > MIN_WIDTH && size.height > MIN_WIDTH &&
                            size.width <= MAX_PREVIEW_WIDTH && size.height <= MAX_PREVIEW_HEIGHT)
                    {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }
        }

        return optimalSize;
    }

    public void startCameraPreview()
    {
        if (camera == null)
        {
            Log.e(URSConfig.APP_NAME, "Camera is null");
            return;
        }

        camera.stopPreview();

        if (previewCallback != null)
        {
            camera.setPreviewCallback(previewCallback);
        }

        camera.startPreview();
    }

    public void stopCameraPreview()
    {
        if (camera == null)
        {
            Log.e(URSConfig.APP_NAME, "Camera is null");
            return;
        }

        if (previewCallback != null)
        {
            camera.setPreviewCallback(null);
        }

        camera.stopPreview();
        if (getFocusMode() != null)
        {
            camera.cancelAutoFocus();
        }
    }

    public void takePicture(final Camera.PictureCallback pictureCallback)
    {
        if (camera == null)
        {
            Log.e(URSConfig.APP_NAME, "Camera is null");
            return;
        }

        // focusMode "continue-picture" 지원 안함
        if (getFocusMode() == null)
        {
            camera.autoFocus(new Camera.AutoFocusCallback()
            {
                public void onAutoFocus(boolean success, Camera camera)
                {
                    System.gc();
                    camera.takePicture(shutterSound ? shutterCallback : null, null, pictureCallback);
                }
            });
        }
        // focusMode "continue-picture" 지원 함
        else
        {
            System.gc();
            camera.takePicture(shutterSound ? shutterCallback : null, null, pictureCallback);
        }
    }

    public void setPreviewCallbackListener(Camera.PreviewCallback previewCallback)
    {
        this.previewCallback = previewCallback;
    }

    public void setOnDrawListener(OnDrawListener onDrawListener)
    {
        this.onDrawListener = onDrawListener;
    }

    public void setShutterSound(boolean shutterSound)
    {
        this.shutterSound = shutterSound;
    }

    public Size getPreviewSize()
    {
        if (camera == null)
        {
            Log.e(URSConfig.APP_NAME, "Camera is null");
            return null;
        }

        if (camera.getParameters() == null)
        {
            Log.e(URSConfig.APP_NAME, "Camera Parameter is null");
            return null;
        }

        return new Size(camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height);
    }

    public Size getPictureSize()
    {
        if (camera == null)
        {
            Log.e(URSConfig.APP_NAME, "Camera is null");
            return null;
        }

        if (camera.getParameters() == null)
        {
            Log.e(URSConfig.APP_NAME, "Camera Parameter is null");
            return null;
        }


        return new Size(camera.getParameters().getPictureSize().width, camera.getParameters().getPictureSize().height);

    }

    public Rect getViewRect()
    {
        return new Rect(
                (int)-getX(),
                (int)-getY(),
                (int)(-getX() + ((View)getParent()).getWidth()),
                (int)(-getY() + ((View)getParent()).getHeight())
        );
    }

    public Rect getVisibleRectFromPreview()
    {
        if (camera == null)
        {
            return null;
        }

        Size previewSize = new Size(
                camera.getParameters().getPreviewSize().height,
                camera.getParameters().getPreviewSize().width
        );

        if (getCameraDisplayOrientation() % 180 == 0)
        {
            previewSize = new Size(
                    camera.getParameters().getPreviewSize().width,
                    camera.getParameters().getPreviewSize().height
            );
        }

        Rect rect = new Rect();
        URSUtils.scaleAspectFitRectInputSizeToOutputSize(
                new Size(getViewRect().width(), getViewRect().height()),
                new RectF(0, 0, getViewRect().width(), getViewRect().height()),
                previewSize
        ).round(rect);

        return rect;
    }

    public Rect getVisibleRectFromPicture()
    {
        if (camera == null)
        {
            return null;
        }

        Size pictureSize = new Size(
                camera.getParameters().getPictureSize().height,
                camera.getParameters().getPictureSize().width
        );

        if (getCameraDisplayOrientation() % 180 == 0)
        {
            pictureSize = new Size(camera.getParameters().getPictureSize().width, camera.getParameters().getPictureSize().height);
        }

        Rect rect = new Rect();
        URSUtils.scaleAspectFitRectInputSizeToOutputSize(
                new Size(getViewRect().width(), getViewRect().height()),
                new RectF(0, 0, getViewRect().width(), getViewRect().height()),
                pictureSize
        ).round(rect);

        return rect;
    }
}