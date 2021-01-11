package kr.co.alphacare.capture;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import kr.co.alphacare.ImageProcessing;
import kr.co.alphacare.R;
import kr.co.alphacare.URSConfig;
import kr.co.alphacare.URSDefine;
import kr.co.alphacare.result.model.DistanceRankData;
import kr.co.alphacare.result.model.ResultData;
import kr.co.alphacare.utils.BitmapUtils;
import kr.co.alphacare.utils.URSUtils;
import kr.co.alphacare.capture.view.CameraPreview;

import org.opencv.core.Size;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static kr.co.alphacare.URSDefine.AUTO_SHOT_SUCCESS_COUNT;

public class CaptureAction
{
    private Context context;
    private CaptureActivity activity;
    private CaptureUISetup uiSetup;

    private int paperMode;

    private AlertDialog dialog;

    private DetectTask detectTask;
    private SaveImageTask saveImageTask;

    private Paint paintGuideBorder;
    private Paint paintGuideBackground;

    private int successCount = 0;

    private int currentStatus = 0;
    private Rect guideRect;
    private Rect sampleImageRect;
    private Rect screenRect;

    public CaptureAction(Context context, CaptureActivity activity, int paperMode)
    {
        this.context = context;
        this.activity = activity;
        this.paperMode = paperMode;
    }

    public void finish()
    {
        activity.finish();
    }

    public void back()
    {
        if (saveImageTask != null)
        {
            return;
        }

        if (dialog != null)
        {
            if(dialog.isShowing())
            {
                dialog.dismiss();
            }

            dialog = null;
        }

        dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.warning)
                .setMessage(R.string.close_dialog)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .create();

        dialog.show();
    }

    public void takePicture(final CameraPreview preview)
    {
        if (saveImageTask != null)
        {
            return;
        }

        saveImageTask = new SaveImageTask();
        saveImageTask.setPreview(preview);

        preview.takePicture(new Camera.PictureCallback()
        {
            @Override
            public void onPictureTaken(byte[] data, Camera camera)
            {
                saveImageTask.setJpgData(data);
                saveImageTask.execute();
            }
        });

    }

    public void detect(CameraPreview preview, byte[] nv21Data)
    {
        if ((dialog != null && dialog.isShowing()) ||
                detectTask != null ||
                saveImageTask != null)
        {
            return;
        }

        detectTask = new DetectTask(preview, nv21Data);
        detectTask.execute();
    }

    public void setGuideRect(CameraPreview preview)
    {
        // 상태바 높이
        Rect statusBarRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(statusBarRect);
        int StatusBarHeight = statusBarRect.top;

        // 화면에 보이는 뷰 Rect 크기 및 위치
        Rect guideAreaRect = preview.getViewRect();
        screenRect = new Rect(guideAreaRect);

        // 가이드 영역 = 상단 버튼 아래, 하단 버튼 위
        Rect stickLocationGuideRect = new Rect();
        activity.findViewById(R.id.stickLocationGuide).getGlobalVisibleRect(stickLocationGuideRect);
        stickLocationGuideRect.left += guideAreaRect.left;
        stickLocationGuideRect.top += guideAreaRect.top - StatusBarHeight;
        stickLocationGuideRect.right += guideAreaRect.left;
        stickLocationGuideRect.bottom += guideAreaRect.top - StatusBarHeight;

        // 하단바 높이(자동 - 하단 가이드 / 수동 - 촬영버튼)
        Rect bottomBarRect = new Rect();
        activity.findViewById(R.id.detectStatus).getGlobalVisibleRect(bottomBarRect);
        bottomBarRect.left += guideAreaRect.left;
        bottomBarRect.top += guideAreaRect.top - StatusBarHeight;
        bottomBarRect.right += guideAreaRect.left;
        bottomBarRect.bottom += guideAreaRect.top - StatusBarHeight;

        // 가이드 영역
        guideAreaRect.top = stickLocationGuideRect.bottom;
        guideAreaRect.bottom = bottomBarRect.top;

        // 가이드 안쪽 여백
        float guidePadding = URSUtils.dpToPx(context.getResources(), 15);

        // 가이드 Rect
        guideRect = new Rect(guideAreaRect);
        guideRect.right -= guideRect.width() / 2.0f;

        sampleImageRect = new Rect(guideAreaRect);
        sampleImageRect.left += sampleImageRect.width() / 2.0f;

        guideRect.left += guidePadding;
        guideRect.top += guidePadding;
        guideRect.right -= guidePadding / 2.0f;
        guideRect.bottom -= guidePadding;

        sampleImageRect.left += guidePadding / 2.0f;
        sampleImageRect.top += guidePadding;
        sampleImageRect.right -= guidePadding;
        sampleImageRect.bottom -= guidePadding;

        // 샘플 이미지 위치 지정
        ImageView sampleGuideImageView = new ImageView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                sampleImageRect.width(), sampleImageRect.height());

        float width;
        float height;

        if (paperMode == URSDefine.PaperMode.litmus10.getValue())
        {
            // 가이드 비율 1:2.5로 지정 (추후 변경 가능)
            width = guideRect.width();
            height = guideRect.width() * (2.5f / 1.0f);

            if (height > guideRect.height())
            {
                height = guideRect.height();
                width = guideRect.height() * (1.0f / 2.5f);
            }
        }
        else
        {
            // 가이드 비율 1:1.3로 지정 (추후 변경 가능)
            width = guideRect.width();
            height = guideRect.width() * (1.3f / 1.0f);

            if (height > guideRect.height())
            {
                height = guideRect.height();
                width = guideRect.height() * (1.0f / 1.3f);
            }
        }

        float x = guideRect.left + (guideRect.width() - width) / 2.0f;
        float y = guideRect.top + (guideRect.height() - height) / 2.0f;

        guideRect.left = (int) x;
        guideRect.top = (int) y;
        guideRect.right = (int) (x + width);
        guideRect.bottom = (int) (y + height);

        int imageId = R.drawable.paper_litmus10;

        if (paperMode == URSDefine.PaperMode.slim_diet_stix.getValue())
        {
            imageId = R.drawable.paper_slim_diet_stix;
        }
        else if(paperMode == URSDefine.PaperMode.self_stik_fr.getValue())
        {
            imageId = R.drawable.paper_self_stik_fr;
        }
        else if(paperMode == URSDefine.PaperMode.nitric_oxide.getValue())
        {
            imageId = R.drawable.paper_nitric_oxide;
        }

        sampleGuideImageView.setImageResource(imageId);

        sampleGuideImageView.setX(sampleImageRect.left - screenRect.left);
        sampleGuideImageView.setY(sampleImageRect.top - screenRect.top);

        RelativeLayout relativeLayout = activity.findViewById(R.id.captureRootView);
        relativeLayout.addView(sampleGuideImageView,params);
    }

    public void setOnDrawListener(CameraPreview preview)
    {
        paintGuideBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintGuideBorder.setStrokeWidth(URSUtils.dpToPx(context.getResources(), 5.0f));
        paintGuideBorder.setStyle(Paint.Style.STROKE);
        paintGuideBorder.setColor(Color.WHITE);

        paintGuideBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintGuideBackground.setStyle(Paint.Style.FILL);
        paintGuideBackground.setColor(Color.BLACK);
        paintGuideBackground.setAlpha(200);

        preview.setOnDrawListener(new CameraPreview.OnDrawListener()
        {
            @Override
            public void onDraw(CameraPreview preview, Canvas canvas)
            {

                if (guideRect == null || screenRect == null)
                {
                    setGuideRect(preview);
                }

                float guideRadius = URSUtils.dpToPx(context.getResources(), 10);

                Path backgroundPath = new Path();
                backgroundPath.addRect(new RectF(screenRect), Path.Direction.CCW);
                backgroundPath.addRoundRect(
                        new RectF(guideRect),
                        new float[] {
                                guideRadius, guideRadius,
                                guideRadius, guideRadius,
                                guideRadius, guideRadius,
                                guideRadius, guideRadius
                        },
                        Path.Direction.CW);
                backgroundPath.setFillType(Path.FillType.WINDING);
                backgroundPath.close();

                // 가이드 라인 바깥 배경
                canvas.drawPath(backgroundPath, paintGuideBackground);

                paintGuideBorder.setColor(
                        currentStatus == 1 ?
                                context.getResources().getColor(R.color.colorPrimaryDark) :
                                Color.WHITE
                );

                // 가이드 라인 테두리
                canvas.drawRoundRect(
                        new RectF(guideRect),
                        guideRadius,
                        guideRadius,
                        paintGuideBorder);
            }
        });
    }

    public void reset()
    {
        currentStatus = 0;
        successCount = 0;
        saveImageTask = null;
        detectTask = null;
    }

    public void setUiSetup(CaptureUISetup uiSetup)
    {
        this.uiSetup = uiSetup;
    }

    private class DetectTask extends AsyncTask<Void, Void, Void>
    {
        private CameraPreview preview;
        private byte[] nv21Data;
        private Size previewSize;
        private int orientation;
        private Rect visibleRectFromPreview;

        public DetectTask(CameraPreview preview, byte[] nv21Data)
        {
            this.preview = preview;
            this.nv21Data = nv21Data;
            this.previewSize = preview.getPreviewSize();
            this.orientation = preview.getCameraDisplayOrientation();
            this.visibleRectFromPreview = preview.getVisibleRectFromPreview();
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            if(previewSize == null)
            {
                return null;
            }

            YuvImage image = new YuvImage(
                    nv21Data,
                    ImageFormat.NV21,
                    (int)previewSize.width,
                    (int)previewSize.height,
                    null);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            image.compressToJpeg(
                    new Rect(0, 0, (int)previewSize.width, (int)previewSize.height),
                    100,
                    outputStream);

            byte[] imageData = outputStream.toByteArray();

            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

            Bitmap rotatedBitmap = BitmapUtils.rotate(bitmap, orientation);
            bitmap = BitmapUtils.replace(bitmap, rotatedBitmap);

            float ratio = (float)visibleRectFromPreview.width() / (float)screenRect.width();

            RectF pictureGuideRect = new RectF(
                    guideRect.left * ratio,
                    guideRect.top * ratio,
                    guideRect.right * ratio,
                    guideRect.bottom * ratio
            );

            Bitmap croppedBitmap = BitmapUtils.crop(bitmap, pictureGuideRect);
            bitmap = BitmapUtils.replace(bitmap, croppedBitmap);

            int[] rgbData = new int[bitmap.getWidth() * bitmap.getHeight()];

            bitmap.getPixels(
                    rgbData,
                    0,
                    bitmap.getWidth(),
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight()
            );

            currentStatus = ImageProcessing.detectURS(
                    rgbData,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    paperMode
            );

            bitmap.recycle();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            TextView failToastText = activity.findViewById(R.id.detectStatus);

            switch (currentStatus)
            {
                case 1:
                {
                    successCount++;
                    failToastText.setText(R.string.detect_status_success);
                    Log.e(URSConfig.APP_NAME, "성공");
                    break;
                }
                case -1:
                    successCount = 0;
                    failToastText.setText(R.string.detect_status_fail_count);
                    //Log.e(URSConfig.APP_NAME, "실패 - 사각형 갯수 미달");
                    break;
                case -2:
                    successCount = 0;
                    failToastText.setText(R.string.detect_status_fail_orientation);
                    Log.e(URSConfig.APP_NAME, "실패 - 용지 방향 확인 필요");
                    break;
                default:
                    successCount = 0;
                    failToastText.setText(R.string.detect_status_fail_count);
                    Log.e(URSConfig.APP_NAME, "기본값");
            }

            preview.invalidate();

            // 자동촬영 조건
            if (successCount >= AUTO_SHOT_SUCCESS_COUNT)
            {
                successCount = 0;

                // 촬영
                takePicture(preview);
            }

            detectTask = null;
        }
    }

    private class SaveImageTask extends AsyncTask<Void, Void, ResultData>
    {
        private CameraPreview preview;
        private byte[] jpgData;
        private Rect visibleRectFromPicture;

        public void setPreview(CameraPreview preview)
        {
            this.preview = preview;
            this.visibleRectFromPicture = preview.getVisibleRectFromPicture();
        }

        public void setJpgData(byte[] jpgData)
        {
            this.jpgData = jpgData;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            activity.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        @Override
        protected ResultData doInBackground(Void... voids)
        {
            if(visibleRectFromPicture == null)
            {
                return null;
            }

            // 원본 이미지 저장 (추후, JNI C++ 내부 코드에서 가이드 라인 기준으로 crop)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
            String fileName = sdf.format(System.currentTimeMillis()) + ".jpg";
            BitmapUtils.saveImageFile(jpgData, URSConfig.APP_PATH, fileName);

            String imagePath = URSConfig.APP_PATH + fileName;

            float ratio = (float)visibleRectFromPicture.width() / (float)screenRect.width();

            // 촬영 이미지 기준의 가이드 라인 영역 Rect
            RectF pictureGuideRect = new RectF(
                    guideRect.left * ratio,
                    guideRect.top * ratio,
                    guideRect.right * ratio,
                    guideRect.bottom * ratio
            );

            ResultData resultData = ImageProcessing.findURSResult(
                    imagePath,
                    (int)pictureGuideRect.left,
                    (int)pictureGuideRect.top,
                    (int)pictureGuideRect.width(),
                    (int)pictureGuideRect.height(),
                    paperMode
            );

            if (resultData.getType() != 1)
            {
//                FileUtils.removeFile(imagePath);
                return null;
            }

            for (int i = 0; i < resultData.getDistanceList().size(); i++)
            {
                ArrayList<DistanceRankData> distanceRankDataList = resultData.getDistanceList().get(i);

                Collections.sort(distanceRankDataList, new Comparator<DistanceRankData>()
                {
                    @SuppressWarnings("unchecked")
                    public int compare(DistanceRankData o1, DistanceRankData o2)
                    {
                        Double v1 = o1.getDistance();
                        Double v2 = o2.getDistance();

                        return v1.compareTo(v2);
                    }
                });

                for (int j = 0; j < distanceRankDataList.size(); j++)
                {
                    DistanceRankData data = distanceRankDataList.get(j);
                    data.setRank(j);
                }
            }

            resultData.setImagePath(imagePath);

            return resultData;
        }

        @Override
        protected void onPostExecute(ResultData resultData)
        {
            super.onPostExecute(resultData);

            activity.findViewById(R.id.progressBar).setVisibility(View.GONE);
            preview.startCameraPreview();
            preview.invalidate();

            if (resultData == null)
            {
                saveImageTask = null;
                currentStatus = 0;
                Toast.makeText(context, context.getResources().getString(R.string.dialog_retake), Toast.LENGTH_SHORT).show();
                return;
            }

            activity.startResultActivity(resultData, paperMode);
        }
    }
}
