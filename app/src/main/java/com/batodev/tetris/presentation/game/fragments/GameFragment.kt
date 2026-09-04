package com.batodev.tetris.presentation.game.fragments

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.batodev.tetris.R
import com.batodev.tetris.infra.helpers.RateAppHelper
import com.batodev.tetris.infra.images.ImageData
import com.batodev.tetris.infra.images.ImageHelper
import com.batodev.tetris.presentation.common.GAME_RESULT
import com.batodev.tetris.presentation.common.getButtons
import com.batodev.tetris.presentation.finished.FinishedActivity
import com.batodev.tetris.presentation.game.GameViewModel
import com.batodev.tetris.presentation.game.PlayPauseView
import com.batodev.tetris.presentation.game.State
import com.batodev.tetris.presentation.game.grid.GameAdapter
import com.batodev.tetris.presentation.game.results.GameResult
import com.batodev.tetris.presentation.settings.SettingsSingleton
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import java.util.Date
import java.util.Locale

const val IMAGES_WON_THIS_GAME = "IMAGES_WON_THIS_GAME"

@OptIn(ExperimentalCoroutinesApi::class)
class GameFragment :
    Fragment(),
    View.OnClickListener {
    internal lateinit var tickPlayer: MediaPlayer
    internal lateinit var pointPlayer: MediaPlayer
    internal lateinit var imagePlayer: MediaPlayer
    private lateinit var adapter: GameAdapter
    internal lateinit var model: GameViewModel
    internal lateinit var moveBlockDown: Job
    internal lateinit var imageData: ImageData
    internal lateinit var view: View
    internal val imagesWonThisGame = mutableListOf<String>()

    private val imageTierUnlock = ImageTierUnlockController(this)
    private lateinit var gameLifecycle: GameLifecycle

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view
        imageData = ImageHelper.pickTierOneImage(requireActivity())
        gameLifecycle = GameLifecycle(this)
        setUpViewModel()
        setUpGridView()
        setUpButtons()
        setUpLogger()
        requireView().findViewById<AdView>(R.id.game_ad).loadAd(AdRequest.Builder().build())
        tickPlayer = MediaPlayer.create(requireContext(), R.raw.tick)
        imagePlayer = MediaPlayer.create(requireContext(), R.raw.image_uncovered)
        pointPlayer = MediaPlayer.create(requireContext(), R.raw.points_scored)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.fragment_game, container, false)

    private fun setUpViewModel() {
        val settingsData = SettingsSingleton.getSettingsData(requireContext())
        model = ViewModelProvider(requireActivity())[GameViewModel::class.java]
        model.setUp(
            SettingsSingleton.getFacade(requireContext()),
            SettingsSingleton.getSpeedStrategy(requireContext()),
        )
        model.music.setUp(
            settingsData.hasMusic,
            requireContext(),
        )
        model.setUpImage(imageData.fileName)
        var lastScore = 0

        fun updateScreen() {
            adapter.gameCells = model.getGrid()
            adapter.notifyDataSetChanged()
            requireView().findViewById<TextView>(R.id.PointsText).text =
                String.format(Locale.getDefault(), "%d", model.getPoints())
            val typeOfBlock = model.getNextBlock()
            requireView().findViewById<ImageView>(R.id.NextBlockImage).setImageResource(
                SettingsSingleton
                    .getStyleCreator(requireContext())
                    .getBlockCreator()
                    .getImageId(typeOfBlock),
            )
        }

        model.gameFacade.observe(viewLifecycleOwner) {
            if (!it.hasFinished()) {
                updateScreen()
                imageTierUnlock.checkIfImageIsWon()
            } else {
                finishGame()
            }
            if (settingsData.hasSounds) {
                tickPlayer.start()
            }
            if (it.getScore().value > lastScore) {
                lastScore = it.getScore().value
                if (settingsData.hasSounds) {
                    pointPlayer.start()
                }
            }
        }
    }

    private fun setUpGridView() {
        val cellColors = SettingsSingleton.getStyleCreator(requireContext()).getColorCellChooser()
        adapter = GameAdapter(model.getGrid(), cellColors)
        requireView().findViewById<GridView>(R.id.GameGrid).adapter = adapter
        requireView().findViewById<ImageView>(R.id.GameImage).setImageBitmap(imageData.bitmap)
    }

    private fun setUpLogger() {
        if (!model.state.isGameStarted()) {
            SettingsSingleton.logData(requireContext())
        }
    }

    private fun setUpButtons() {
        (requireView() as ViewGroup).getButtons().forEach { it.setOnClickListener(this) }
        requireView().findViewById<PlayPauseView>(R.id.pauseButton).setOnClickListener(this)
        requireView()
            .findViewById<Button>(R.id.DownButton)
            .setOnLongClickListener {
                model.movement.dropBlock()
                true
            }
    }

    internal fun finishGame() {
        RateAppHelper.increaseRateAppCounterAndShowDialogIfApplicable(requireActivity())
        val finish =
            Intent(requireContext(), FinishedActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                putExtra(
                    GAME_RESULT,
                    GameResult(score = model.getPoints(), date = Date()),
                )
                putExtra(IMAGES_WON_THIS_GAME, imagesWonThisGame.toTypedArray())
            }
        requireContext().startActivity(finish)
        requireActivity().finish()
    }

    override fun onClick(p0: View) =
        when (p0.id) {
            R.id.DownButton -> model.movement.down()
            R.id.LeftButton -> model.movement.left()
            R.id.RightButton -> model.movement.right()
            R.id.RotateLeft -> model.movement.rotateLeft()
            R.id.RotateRight -> model.movement.rotateRight()
            R.id.pauseButton -> gameLifecycle.pauseButtonClicked()
            else -> throw UnsupportedOperationException("Unknown button")
        }

    override fun onPause() {
        super.onPause()
        gameLifecycle.pauseGame()
    }

    override fun onResume() {
        super.onResume()
        if (!model.state.isGameStarted()) {
            gameLifecycle.startGame()
            model.state.setGameStarted()
        } else {
            requireView().findViewById<PlayPauseView>(R.id.pauseButton).setState(State.PAUSE)
            requireView().findViewById<PlayPauseView>(R.id.pauseButton).fadeIn()
        }
    }
}
