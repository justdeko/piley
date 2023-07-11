import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.dk.piley.util.AlertDialogHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermissionDialog(rationaleOpen: Boolean, onDismiss: () -> Unit) {
    val permissionState =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    if (!permissionState.status.isGranted && permissionState.status.shouldShowRationale && rationaleOpen) {
        // permission denied once, show rationale
        AlertDialogHelper(
            title = "Notification permission needed",
            description = "This app needs the notification permission to show you reminders about your tasks.",
            dismissText = null,
            confirmText = "Grant Permission",
            onDismiss = onDismiss,
            onConfirm = {
                permissionState.launchPermissionRequest()
                onDismiss()
            }
        )
    } else {
        // permission granted or forever denied
        LaunchedEffect(key1 = Unit, block = { permissionState.launchPermissionRequest() })
        onDismiss()
    }
}