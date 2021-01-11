package kr.co.alphacare.capture;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.Camera;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import kr.co.alphacare.R;
import kr.co.alphacare.URSConfig;
import kr.co.alphacare.utils.URSUtils;
import kr.co.alphacare.capture.view.CameraPreview;

import java.io.File;

public class CaptureUISetup
{
    private Context context;
    private CaptureActivity activity;
    private CaptureAction action;

    private CameraPreview preview;

    public CaptureUISetup(Context context, CaptureActivity activity)
    {
        this.context = context;
        this.activity = activity;
    }

    public void setupViews()
    {
        ProgressBar progressBar = activity.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        File appFolder = new File(URSConfig.APP_PATH);
        if (!appFolder.exists())
        {
            appFolder.mkdirs();
        }

        setupPreview();
    }

    public void setupButtons()
    {
        ImageView closeButton = activity.findViewById(R.id.btn_close);
        closeButton.setImageDrawable(URSUtils.changeDrawableColor(
                closeButton.getDrawable(),
                Color.WHITE
        ));
        closeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                action.back();
            }
        });

        // 현재 검출 상태 표시
        TextView failToastText = activity.findViewById(R.id.detectStatus);
        failToastText.setText(R.string.detect_status_fail_count);
    }

    private void setupPreview()
    {
        FrameLayout previewLayout = activity.findViewById(R.id.previewLayout);

        if (preview != null)
        {
            previewLayout.removeView(preview);
            preview = null;
        }

        preview = new CameraPreview(context);
        // TODO 촬영음 소리 ON / OFF
        preview.setShutterSound(true);
        if (!preview.isAvailableCamera())
        {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.capture_dialog_camera_error)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                            activity.finish();
                        }
                    })
                    .show();
            return;
        }

        preview.setPreviewCallbackListener(new Camera.PreviewCallback()
        {
            @Override
            public void onPreviewFrame(byte[] nv21Data, Camera camera)
            {
                action.detect(preview, nv21Data);
            }
        });

        if (action != null)
        {
            action.setOnDrawListener(preview);
        }

        previewLayout.addView(preview);
    }

    public void restartPreview()
    {
        preview.startCameraPreview();
    }

    public void pausePreview()
    {
        preview.stopCameraPreview();
    }

    public void setAction(CaptureAction action)
    {
        this.action = action;
    }
}
