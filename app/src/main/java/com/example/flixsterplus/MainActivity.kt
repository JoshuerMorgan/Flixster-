package com.example.flixsterplus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.flixsterplus.databinding.ActivityMainBinding

/**
 * The MainActivity for the Flixster+ app.
 * Hosts the toolbar and launches a [PeopleFragment].
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemBarInsets(binding.root, binding.appBar, binding.content)

        // Only add the fragment on first launch; on rotation the FragmentManager restores it.
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.content, PeopleFragment(), null)
                .commit()
        }
    }
}
