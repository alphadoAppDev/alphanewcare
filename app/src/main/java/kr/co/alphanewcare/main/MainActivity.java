package kr.co.alphanewcare.main;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import kr.co.alphanewcare.R;
import kr.co.alphanewcare.URSDefine;
import kr.co.alphanewcare.graph.GraphActivity;
import kr.co.alphanewcare.tutorial.TutorialActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    public static final int REQ_KEY_PERMISSION_CODE = 1000;

    private MainUISetup uiSetup;
    private MainAction action;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] permission = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        checkPermission(this, permission, REQ_KEY_PERMISSION_CODE);

        init();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    private void init()
    {/*
        uiSetup = new MainUISetup(this, this);
        action = new MainAction(this, this);

        uiSetup.setAction(action);
        action.setUiSetup(uiSetup);

        uiSetup.setupViews();
        uiSetup.setupButtons();*/
    }

    public void showTutorialActivity(URSDefine.PaperMode mode)
    {
        Intent intent = new Intent(this, TutorialActivity.class);
        intent.putExtra(TutorialActivity.REQ_MODE, mode.getValue());
        startActivity(intent);
    }

    public void showGraphActivity()
    {
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }

    public boolean checkPermission(Activity activity, String[] permissions, int reqCode)
    {
        ArrayList<String> permissionList = new ArrayList<>();

        for(String permission : permissions)
        {
            int result = ContextCompat.checkSelfPermission(activity, permission);
            if(result != PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(permission);
            }
        }

        if(permissionList.size() != 0)
        {
            String arr[] = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(activity, arr, reqCode);
            return false;
        }

        return true;
    }

    public boolean isGranted(int[] grantResults)
    {
        boolean isGranted = true;

        for(int result : grantResults)
        {
            if(result != PackageManager.PERMISSION_GRANTED)
            {
                isGranted = false;
                break;
            }
        }

        return isGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode)
        {
            case REQ_KEY_PERMISSION_CODE:
            {
                if(isGranted(grantResults))
                {
                    return;
                }

                new AlertDialog.Builder(MainActivity.this)
                        .setCancelable(false)
                        .setTitle(this.getResources().getString(R.string.notice))
                        .setMessage(this.getResources().getString(R.string.notice_permission))
                        .setPositiveButton(this.getResources().getString(R.string.confirm), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        })
                        .show();

            } break;
        }
    }
}
