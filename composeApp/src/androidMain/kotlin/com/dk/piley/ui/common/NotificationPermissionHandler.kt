package com.dk.piley.ui.common

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dk.piley.util.getActivityOrNull
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.notification_permission_dialog_confirm_button
import piley.composeapp.generated.resources.notification_permission_dialog_description
import piley.composeapp.generated.resources.notification_permission_dialog_title

/**
 * Notification permission handler that requests the permission and shows a dialog if denied
 *
 * @param launch when the permission request should be launched
 * @param setPermissionGranted callback function when the permission result is returned
 */
@Composable
actual fun NotificationPermissionHandler(
    launch: Boolean,
    setPermissionGranted: (Boolean) -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        NotificationPermissionHandlerInternal(launch, setPermissionGranted)
    } else {
        setPermissionGranted(true)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun NotificationPermissionHandlerInternal(
    launch: Boolean,
    setPermissionGranted: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    var permissionGranted by remember {
        mutableStateOf(
            (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED).also {
                setPermissionGranted(it)
            }
        )
    }
    var rationaleOpen by remember { mutableStateOf(false) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            permissionGranted = granted
            setPermissionGranted(granted)
            if (!granted) {
                rationaleOpen = context.getActivityOrNull()?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            it,
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    } else {
                        false
                    }
                } ?: false
            }
        }
    if (!permissionGranted && launch) {
        RequestPermissionDialog(
            rationaleOpen,
            title = stringResource(Res.string.notification_permission_dialog_title),
            description = stringResource(Res.string.notification_permission_dialog_description),
            confirmText = stringResource(Res.string.notification_permission_dialog_confirm_button),
            onDismiss = { rationaleOpen = false },
            onLaunchPermissionRequest = { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }
        )
    }
}