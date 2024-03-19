package com.batodev.tetris.presentation.finished

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.batodev.tetris.R
import com.batodev.tetris.databinding.ActivityFinishedBinding
import com.batodev.tetris.infra.database.Player
import com.batodev.tetris.infra.database.PlayerApplication
import com.batodev.tetris.infra.images.ImageHelper
import com.batodev.tetris.infra.logs.LoggerGetter
import com.batodev.tetris.infra.settings.SettingsHelper
import com.batodev.tetris.presentation.common.GAME_RESULT
import com.batodev.tetris.presentation.common.HideStatusBarActivity
import com.batodev.tetris.presentation.finished.sendmail.EmailData
import com.batodev.tetris.presentation.finished.sendmail.EmailSender
import com.batodev.tetris.presentation.gallery.GalleryActivity
import com.batodev.tetris.presentation.gallery.IMAGES
import com.batodev.tetris.presentation.game.GameActivity
import com.batodev.tetris.presentation.game.fragments.IMAGES_WON_THIS_GAME
import com.batodev.tetris.presentation.game.results.DateGetter
import com.batodev.tetris.presentation.game.results.GameResult
import com.batodev.tetris.presentation.settings.SettingsActivity
import com.batodev.tetris.presentation.settings.SettingsSingleton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit.MILLISECONDS


class FinishedActivity : HideStatusBarActivity() {

    private lateinit var binding: ActivityFinishedBinding
    private val model: FinishedViewModel by lazy {
        ViewModelProvider(
            this,
            PlayerViewModelFactory((application as PlayerApplication).repository)
        ).get(FinishedViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.extras?.getStringArray(IMAGES_WON_THIS_GAME)?.let {
            if (it.isNotEmpty()) {
                findViewById<ImageView>(R.id.won_image_1).setImageBitmap(ImageHelper.imageBitmapByName(this, it[0]))
            }
            if (it.size > 1) {
                findViewById<ImageView>(R.id.won_image_2).setImageBitmap(ImageHelper.imageBitmapByName(this, it[1]))
            }
            if (it.size > 2) {
                findViewById<ImageView>(R.id.won_image_3).setImageBitmap(ImageHelper.imageBitmapByName(this, it[2]))
            }
        }
        findViewById<Button>(R.id.finished_activity_play_the_game).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            })
            finish()
        }
        findViewById<Button>(R.id.finished_activity_unlocked_gallery).setOnClickListener {
            val imagesWon = SettingsHelper.load(this).imagesWon
            if (imagesWon.isNotEmpty()) {
                val intent = Intent(this, GalleryActivity::class.java)
                intent.putExtra(IMAGES, imagesWon.toTypedArray())
                startActivity(intent)
            } else {
                val snackBar = Snackbar.make(binding.root, getString(R.string.playTheGameTounlockImages), Snackbar.LENGTH_SHORT)
                val params = snackBar.view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                snackBar.view.layoutParams = params
                snackBar.show()
            }
            finish()
        }
    }
}