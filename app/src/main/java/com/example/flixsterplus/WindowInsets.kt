package com.example.flixsterplus

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/**
 * targetSdk 35+ forces edge-to-edge: pad the [appBar] below the status bar, keep [content]
 * above the nav bar, and keep [root] clear of side cutouts/nav bars in landscape.
 */
fun applySystemBarInsets(root: View, appBar: View, content: View) {
    ViewCompat.setOnApplyWindowInsetsListener(root) { _, windowInsets ->
        val insets = windowInsets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        )
        root.updatePadding(left = insets.left, right = insets.right)
        appBar.updatePadding(top = insets.top)
        content.updatePadding(bottom = insets.bottom)
        WindowInsetsCompat.CONSUMED
    }
}
