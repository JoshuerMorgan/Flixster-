package com.example.flixsterplus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.flixsterplus.databinding.ActivityMainBinding

/**
 * The MainActivity for the Flixster+ app.
 * Hosts the toolbar and launches a [MoviesFragment].
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyWindowInsets()

        // Only add the fragment on first launch; on rotation the FragmentManager restores it.
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.content, MoviesFragment(), null)
                .commit()
        }
    }

    // targetSdk 35+ forces edge-to-edge: pad the toolbar below the status bar, keep the list
    // above the nav bar, and avoid side cutouts/nav bars in landscape.
    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { root, windowInsets ->
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            root.updatePadding(left = insets.left, right = insets.right)
            binding.appBar.updatePadding(top = insets.top)
            binding.content.updatePadding(bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }
}
