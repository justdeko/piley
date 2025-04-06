@file:OptIn(ExperimentalForeignApi::class)

package com.dk.piley.model.backup

import com.dk.piley.model.PILE_DATABASE_NAME
import com.dk.piley.util.IosUiUtils
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIViewController
import platform.UIKit.popoverPresentationController
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

class DatabaseExporter : IDatabaseExporter {
    private val rootController by lazy { IosUiUtils.getRootViewController() }

    override fun exportPileDatabase(): Flow<ExportResult> = flow {
        try {
            val dbPath = getDatabasePath()
            emit(ExportResult.Success(dbPath, showAction = true))
        } catch (e: Exception) {
            emit(ExportResult.Error(e.toString()))
        }
    }

    override fun importPileDatabase(file: PlatformFile): Flow<ImportResult> {
        TODO("Not yet implemented")
    }

    override fun getDatabasePath(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory?.path) + "/$PILE_DATABASE_NAME"
    }

    override fun shareFile(filePath: String) {
        if (rootController != null) {
            shareFile(filePath, rootController!!)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun shareFile(path: String, viewController: UIViewController): Boolean {
        if (!NSFileManager.defaultManager.fileExistsAtPath(path)) {
            return false
        }

        // Dispatch to the main thread because UI operations must happen there
        dispatch_async(dispatch_get_main_queue()) {
            val activityViewController = UIActivityViewController(
                activityItems = listOf(NSURL(fileURLWithPath = path)),
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