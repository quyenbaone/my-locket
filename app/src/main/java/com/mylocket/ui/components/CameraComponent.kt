package com.mylocket.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import android.util.Size
import android.view.Surface
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.view.CameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import com.mylocket.ui.theme.MyLocketTheme
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.mylocket.ui.theme.BlueOcean
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.mylocket.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@SuppressLint("RestrictedApi")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraComponent(
    navController: NavController
) {

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val previewView: PreviewView = remember { PreviewView(context) }
    val isFlashing = remember {
        mutableStateOf(false)
    }


    //permission
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    )

    //cấp quyền khi thực thi composable
    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }


    //CameraX
    val cameraSelector = remember {
        mutableStateOf(CameraSelector.DEFAULT_FRONT_CAMERA)
    }

    val preview = CameraPreview.Builder().setMaxResolution(Size(screenWidth, screenWidth)).setCameraSelector(cameraSelector.value).build()

    val imageCapture = remember {
        ImageCapture.Builder()
            .setTargetRotation(Surface.ROTATION_0)
            .setFlashMode(if (isFlashing.value) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF)
            .build()
    }

    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        try {
            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(lifecycleOwner,cameraSelector.value,preview, imageCapture)

            preview.apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }
        }catch (exc: Exception){
            Log.e("Loi", "Use case binding failed", exc)
        }
    }, ContextCompat.getMainExecutor(context))


    Column{

        Spacer(modifier = Modifier.height(150.dp))

        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .fillMaxWidth()
                .height(screenWidth.dp)
                .clip(shape = RoundedCornerShape(60.dp)),
        )
        Spacer(modifier = Modifier.height(50.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    isFlashing.value = !isFlashing.value
                },
                modifier = Modifier
                    .size(50.dp)
                    .clip(shape = CircleShape),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = if (isFlashing.value) MaterialTheme.colorScheme.tertiary else Color.White
                )
            ) {
                Icon(painter = painterResource(id = if (isFlashing.value) R.drawable.ic_flash_on else R.drawable.ic_flash_off), contentDescription = "Turn on flash", modifier = Modifier.size(60.dp))
            }


            IconButton(
                onClick = {
                    val outputFile = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

                    imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                // Lấy dữ liệu ảnh từ file
                                val imgPath = outputFile.absolutePath
                                Log.e("CameraX", "Image: $imgPath")
                                val encodedPath = URLEncoder.encode(imgPath, StandardCharsets.UTF_8.toString())
                                navController.navigate(route = "sending/$encodedPath")
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("CameraX", "Error capturing image: ${exception.message}", exception)
                            }
                        }
                    )
//                    imageCapture.takePicture(outputOptions1, ContextCompat.getMainExecutor(context),
//                        object : ImageCapture.OnImageSavedCallback {
//                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                                // Ảnh đã được lưu thành công
//                                Log.e("CameraX", "Ảnh đã được lưu tại: ${outputFile1.absolutePath}")
//
//                                // Chuyển hướng tới màn hình tiếp theo với đường dẫn ảnh
////                                navController.navigate("sending/${outputFile.absolutePath}")
//                            }
//
//                            override fun onError(exception: ImageCaptureException) {
//                                Log.e("CameraX", "Lỗi khi chụp ảnh: ${exception.message}", exception)
//                            }
//                        }
//                    )
                },
                modifier = Modifier
                    .size(110.dp)
                    .border(5.dp, BlueOcean, CircleShape),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                )
            ) {
                Icon(painter = painterResource(id = R.drawable.capture), contentDescription = "Capture", modifier = Modifier.size(90.dp))
            }

            IconButton(
                onClick = {
                    if (cameraSelector.value == CameraSelector.DEFAULT_BACK_CAMERA){
                        cameraSelector.value = CameraSelector.DEFAULT_FRONT_CAMERA
                    }else{
                        cameraSelector.value = CameraSelector.DEFAULT_BACK_CAMERA
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .clip(shape = CircleShape),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                )
            ) {
                Icon(painter = painterResource(id = R.drawable.camera), contentDescription = "rotate camera", modifier = Modifier.size(60.dp))
            }
        }

        Spacer(modifier = Modifier.height(70.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Lịch sử",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(3.dp))

            Icon(painter = painterResource(id = R.drawable.down), contentDescription = null, tint = Color.White)
        }

    }

}

// Preview cho CameraComponent (Mock UI)
@ComposePreview(showBackground = true)
@Composable
fun CameraComponentPreview() {
    MyLocketTheme {
        // Mock camera UI for preview (không có camera thật)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Color.Gray, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📷 Camera Preview",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = { /* Preview only */ },
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "Take Photo",
                        modifier = Modifier.size(32.dp),
                        tint = BlueOcean
                    )
                }
            }
        }
    }
}