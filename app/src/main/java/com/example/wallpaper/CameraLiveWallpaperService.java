package com.example.wallpaper;

import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.core.app.ActivityCompat;

import java.util.Arrays;

public class CameraLiveWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new CameraEngine();
    }

    private class CameraEngine extends Engine {

        private CameraDevice cameraDevice;
        private CameraCaptureSession cameraCaptureSession;
        private SurfaceHolder surfaceHolder;
        private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(CameraDevice camera) {
                cameraDevice = camera;
                createCameraPreviewSession();
            }

            @Override
            public void onDisconnected(CameraDevice camera) {
                cameraDevice.close();
                cameraDevice = null;
            }

            @Override
            public void onError(CameraDevice camera, int error) {
                cameraDevice.close();
                cameraDevice = null;
            }
        };

        private void createCameraPreviewSession() {
            try {
                Surface surface = surfaceHolder.getSurface();
                cameraDevice.createCaptureSession(Arrays.asList(surface),
                        new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(CameraCaptureSession session) {
                                if (cameraDevice == null) {
                                    return;
                                }

                                cameraCaptureSession = session;
                                updatePreview();
                            }

                            @Override
                            public void onConfigureFailed(CameraCaptureSession session) {
                            }
                        }, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        private void updatePreview() {
            if (cameraDevice == null) {
                return;
            }
            Log.d("Truong", "updatePreview");
            Surface surface = surfaceHolder.getSurface();
//            try {
////                cameraCaptureSession.setRepeatingRequest(/* create your capture request */, null, null);
//            } catch (CameraAccessException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            this.surfaceHolder = holder;
            openCamera();
        }

        private void openCamera() {
            CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
            try {
                String cameraId = manager.getCameraIdList()[0];
                if (ActivityCompat.checkSelfPermission(this.getDisplayContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                manager.openCamera(cameraId, stateCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            closeCamera();
        }

        private void closeCamera() {
            if (cameraCaptureSession != null) {
                cameraCaptureSession.close();
                cameraCaptureSession = null;
            }

            if (cameraDevice != null) {
                cameraDevice.close();
                cameraDevice = null;
            }
        }
    }
}