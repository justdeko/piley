package com.dk.piley.model.backup

import com.dk.piley.model.PILE_DATABASE_NAME
import com.dk.piley.util.IosUiUtils
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIViewController
import platform.UIKit.popoverPresentationController
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

class DatabaseExporter(
    private val rootController: UIViewController? = IosUiUtils.getRootViewController()
) : IDatabaseExporter {
    override fun exportPileDatabase(): Flow<ExportResult> = flow {
        try {
            val dbPath = getDatabasePath()
            val fileURL = NSURL.fileURLWithPath(dbPath)

            if (rootController != null) {
                val success = shareFile(fileURL, rootController)
                if (success) {
                    emit(ExportResult.Success(dbPath))
                } else {
                    emit(ExportResult.Error("Failed to share file"))
                }
            } else {
                emit(ExportResult.Error("No view controller provided for sharing"))
            }
        } catch (e: Exception) {
            emit(ExportResult.Error(e.toString()))
        }
    }

    override fun getDatabasePath(): String {
        val paths = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory, NSUserDomainMask, true
        )
        val documentsDirectory = paths.firstOrNull() as? String ?: ""
        return "$documentsDirectory/$PILE_DATABASE_NAME.sqlite"
    }

    override fun shareFile(filePath: String) {
        if (rootController != null) {
            shareFile(NSURL(filePath), rootController)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun shareFile(fileURL: NSURL, viewController: UIViewController): Boolean {
        if (!NSFileManager.defaultManager.fileExistsAtPath(fileURL.path ?: return false)) {
            return false
        }

        // Dispatch to the main thread because UI operations must happen there
        dispatch_async(dispatch_get_main_queue()) {
            val activityViewController = UIActivityViewController(
                activityItems = listOf(fileURL),
                applicationActivities = null
            )

            // For iPad, specify where the activity view controller should be anchored
            activityViewController.popoverPresentationController?.apply {
                setSourceView(viewController.view)
                setSourceRect(
                    CGRectMake(
                        viewController.view.bounds.useContents { origin.x + (size.width / 2) },
                        viewController.view.bounds.useContents { origin.y + (size.height / 2) },
                        0.0, 0.0
                    )
                )
                setPermittedArrowDirections(0u) // UIPopoverArrowDirection.None
            }

            viewController.presentViewController(
                activityViewController,
                animated = true,
                completion = null
            )
        }
        return true
    }
}