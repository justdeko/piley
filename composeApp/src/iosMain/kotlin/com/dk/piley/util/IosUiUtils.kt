package com.dk.piley.util

import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow

object IosUiUtils {
    fun getRootViewController(): UIViewController? {
        val sharedApplication = UIApplication.sharedApplication
        val window = sharedApplication.keyWindow ?:
        sharedApplication.windows.firstOrNull() as? UIWindow
        return window?.rootViewController
    }
}