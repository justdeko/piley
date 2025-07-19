package com.dk.piley.ui.common

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import piley.composeapp.generated.resources.calendar_permission_dialog_confirm_button
import piley.composeapp.generated.resources.calendar_permission_dialog_description
import piley.composeapp.generated.resources.calendar_permission_dialog_title

@Composable
actual fun CalendarPermissionHandler(
    launch: Boolean,
    setPermissionGranted: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var permissionGranted by remember {
        mutableStateOf(
            (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALENDAR
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_CALENDAR
                    ) == PackageManager.PERMISSION_GRANTED)
        )
    }
    var rationaleOpen by remember { mutableStateOf(false) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val granted = results[Manifest.permission.READ_CALENDAR] == true &&
                    results[Manifest.permission.WRITE_CALENDAR] == true
            permissionGranted = granted
            setPermissionGranted(granted)
            if (!granted) {
                rationaleOpen = context.getActivityOrNull()?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        it,
                        Manifest.permission.READ_CALENDAR
                    ) || ActivityCompat.shouldShowRequestPermissionRationale(
                        it,
                        Manifest.permission.WRITE_CALENDAR
                    )
                } ?: false
            }
        }
    if (!permissionGranted && launch) {
        RequestPermissionDialog(
            rationaleOpen,
            title = stringResource(Res.string.calendar_permission_dialog_title),
            description = stringResource(Res.string.calendar_permission_dialog_description),
            confirmText = stringResource(Res.string.calendar_permission_dialog_confirm_button),
            onDismiss = { rationaleOpen = false },
            onLaunchPermissionRequest = {
                launcher.launch(
                    arrayOf(
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR
                    )
                )
            }
        )
    }
}