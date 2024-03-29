package com.batodev.tetris.presentation.game.fragments

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.batodev.tetris.R
import com.batodev.tetris.infra.helpers.RateAppHelper
import com.batodev.tetris.infra.images.ImageData
import com.batodev.tetris.infra.images.ImageHelper
import com.batodev.tetris.infra.settings.SettingsHelper
import com.batodev.tetris.presentation.common.GAME_RESULT
import com.batodev.tetris.presentation.common.getButtons
import com.batodev.tetris.presentation.finished.FinishedActivity
import com.batodev.tetris.presentation.game.GameViewModel
import com.batodev.tetris.presentation.game.PlayPauseView
import com.batodev.tetris.presentation.game.State
import com.batodev.tetris.presentation.game.actions.Action
import com.batodev.tetris.presentation.game.actions.ResumeToastAction
import com.batodev.tetris.presentation.game.grid.GameAdapter
import com.batodev.tetris.presentation.game.results.GameResult
import com.batodev.tetris.presentation.settings.SettingsSingleton
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import nl.dionsegijn.konfetti.xml.listeners.OnParticleSystemUpdateListener
import java.util.Date
import java.util.concurrent.TimeUnit

const val IMAGES_WON_THIS_GAME = "IMAGES_WON_THIS_GAME"

@OptIn(ExperimentalCoroutinesApi::class)
class GameFragment : Fragment(), View.OnClickListener {
    private lateinit var tickPlayer: MediaPlayer
    private lateinit var pointPlayer: MediaPlayer
    private lateinit var imagePlayer: MediaPlayer
    private lateinit var adapter: GameAdapter
    private lateinit var model: GameViewModel
    private lateinit var resumeAction: Action
    private lateinit var moveBlockDown: Job
    private lateinit var imageData: ImageData
    private val tierOneScoreRequired = 200 // 200
    private val tierTwoScoreRequired = 450 // 450
    private val tierThreeScoreRequired = 700 // 700
    private var tierOneImageUncovered = false
    private var tierTwoImageUncovered = false
    private var tierThreeImageUncovered = false
    private lateinit var view: View
    private val imagesWonThisGame = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view
        imageData = ImageHelper.pickTierOneImage(requireActivity())
        setUpViewModel()
        setUpGridView()
        setUpButtons()
        setUpResumeAction()
        setUpLogger()
        requireView().findViewById<AdView>(R.id.game_ad).loadAd(AdRequest.Builder().build())
        tickPlayer = MediaPlayer.create(requireContext(), R.raw.tick)
        imagePlayer = MediaPlayer.create(requireContext(), R.raw.image_uncovered)
        pointPlayer = MediaPlayer.create(requireContext(), R.raw.points_scored)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }


    private fun setUpViewModel() {
        val settingsData = SettingsSingleton.getSettingsData(requireContext())
        model = ViewModelProvider(requireActivity())[GameViewModel::class.java]
        model.setUp(
            SettingsSingleton.getFacade(requireContext()),
            SettingsSingleton.getSpeedStrategy(requireContext())
        )
        model.setUpMusic(
            settingsData.hasMusic,
            requireContext()
        )
        model.setUpImage(imageData.fileName)
        var lastScore = 0
        model.gameFacade.observe(viewLifecycleOwner) {
            if (!it.hasFinished()) {
                updateScreen()
                checkIfImageIsWon()
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

    private fun checkIfImageIsWon() {
        val score = model.gameFacade.value!!.getScore().value
        Log.d(GameFragment::class.java.simpleName, "Score: $score")
        if (score >= tierOneScoreRequired && !tierOneImageUncovered) {
            tierOneImageUncovered = true
            addImageToUncoveredAndPickNew(2)
            showTopSnackBar()
            showConfettiAndPlaySound()
        }
        if (score >= tierTwoScoreRequired && !tierTwoImageUncovered) {
            tierTwoImageUncovered = true
            addImageToUncoveredAndPickNew(3)
            showTopSnackBar()
            showConfettiAndPlaySound()
        }
        if (score >= tierThreeScoreRequired && !tierThreeImageUncovered) {
            tierThreeImageUncovered = true
            addImageToUncoveredAndPickNew(Integer.MAX_VALUE)
            showTopSnackBar()
            showConfettiAndPlaySound()
        }
    }

    private fun showConfettiAndPlaySound() {
        val settingsData = SettingsSingleton.getSettingsData(requireContext())
        if (settingsData.hasSounds) {
            imagePlayer.start()
        }
        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
            position = Position.Relative(0.5, 0.3)
        )
        val konfetti = requireView().findViewById<KonfettiView>(R.id.konfettiView)
        konfetti.visibility = View.VISIBLE
        konfetti.start(party)
        konfetti.onParticleSystemUpdateListener = object : OnParticleSystemUpdateListener {
            override fun onParticleSystemEnded(
                view: KonfettiView,
                party: Party,
                activeSystems: Int
            ) {
                konfetti.visibility = View.GONE
                Log.d(GameFragment::class.java.simpleName, "confetti end: $konfetti")
            }

            override fun onParticleSystemStarted(
                view: KonfettiView,
                party: Party,
                activeSystems: Int
            ) {
                Log.d(GameFragment::class.java.simpleName, "confetti start: $konfetti")
            }
        }
    }

    private fun showTopSnackBar() {
        val snackBar =
            Snackbar.make(view, getString(R.string.newImageInGallery), Snackbar.LENGTH_SHORT)
        val params = snackBar.view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        snackBar.view.layoutParams = params
        snackBar.show()
    }


    private fun addImageToUncoveredAndPickNew(newImageTier: Int) {
        Log.d(GameFragment::class.java.simpleName, "image won newImageTier: $newImageTier")
        Log.d(
            GameFragment::class.java.simpleName,
            "image won imageData.fileName: ${imageData.fileName}"
        )
        val settingsData = SettingsHelper.load(requireActivity())
        val imagesWon = settingsData.imagesWon
        if (!imagesWon.contains(imageData.fileName)) {
            imagesWon.add(imageData.fileName)
            SettingsHelper.save(requireActivity(), settingsData)
        }
        if (!imagesWonThisGame.contains(imageData.fileName)) {
            imagesWonThisGame.add(imageData.fileName)
        }
        if (newImageTier == 2) {
            imageData = ImageHelper.pickTierTwoImage(requireActivity())
        }
        if (newImageTier == 3) {
            imageData = ImageHelper.pickTierThreeImage(requireActivity())
        }
        requireView().findViewById<ImageView>(R.id.GameImage).setImageBitmap(imageData.bitmap)
        Log.d(GameFragment::class.java.simpleName, "new image: ${imageData.fileName}")
    }

    private fun setUpGridView() {
        val cellColors = SettingsSingleton.getStyleCreator(requireContext()).getColorCellChooser()
        adapter = GameAdapter(model.getGrid(), cellColors)
        requireView().findViewById<GridView>(R.id.GameGrid).adapter = adapter
        requireView().findViewById<ImageView>(R.id.GameImage).setImageBitmap(imageData.bitmap)
    }

    private fun setUpLogger() {
        if (!model.isGameStarted())
            SettingsSingleton.logData(requireContext())
    }

    private fun setUpButtons() {
        (requireView() as ViewGroup).getButtons().forEach { it.setOnClickListener(this) }
        requireView().findViewById<PlayPauseView>(R.id.pauseButton).setOnClickListener(this)
        requireView().findViewById<Button>(R.id.DownButton)
            .setOnLongClickListener { model.dropBlock();true }
    }

    private fun updateScreen() {
        adapter.gameCells = model.getGrid()
        adapter.notifyDataSetChanged()
        requireView().findViewById<TextView>(R.id.PointsText).text = model.getPoints().toString()
        val typeOfBlock = model.getNextBlock()
        requireView().findViewById<ImageView>(R.id.NextBlockImage).setImageResource(
            SettingsSingleton.getStyleCreator(requireContext()).getBlockCreator()
                .getImageId(typeOfBlock)
        )
    }

    private fun setUpResumeAction() {
        resumeAction = ResumeToastAction(requireContext())
    }

    private fun finishGame() {
        RateAppHelper.increaseRateAppCounterAndShowDialogIfApplicable(requireActivity())
        val finish = Intent(requireContext(), FinishedActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra(
                GAME_RESULT,
                GameResult(score = model.getPoints(), date = Date())
            )
            putExtra(IMAGES_WON_THIS_GAME, imagesWonThisGame.toTypedArray())
        }
        requireContext().startActivity(finish)
        requireActivity().finish()
    }

    override fun onClick(p0: View) = when (p0.id) {
        R.id.DownButton -> model.down()
        R.id.LeftButton -> model.left()
        R.id.RightButton -> model.right()
        R.id.RotateLeft -> model.rotateLeft()
        R.id.RotateRight -> model.rotateRight()
        R.id.pauseButton -> pauseButtonClicked()
        else -> throw UnsupportedOperationException("Unknown button")
    }

    override fun onPause() {
        super.onPause()
        pauseGame()
    }

    override fun onResume() {
        super.onResume()
        if (!model.isGameStarted()) {
            startGame()
            model.setGameStarted()
        } else {
            requireView().findViewById<PlayPauseView>(R.id.pauseButton).setState(State.PAUSE)
            requireView().findViewById<PlayPauseView>(R.id.pauseButton).fadeIn()
        }
    }

    private fun startGame() {
        model.startMusic()
        moveBlockDown = lifecycleScope.launch(start = CoroutineStart.ATOMIC) { model.runGame() }
        requireView().findViewById<PlayPauseView>(R.id.pauseButton).setState(State.PLAY)
        requireView().findViewById<PlayPauseView>(R.id.pauseButton).fadeIn()
    }

    private fun pauseGame() {
        if (!model.isGamePaused()) {
            moveBlockDown.cancel()
            model.pauseMusic()
            model.setGamePaused()
            requireView().findViewById<PlayPauseView>(R.id.pauseButton).setState(State.PAUSE)
            requireView().findViewById<PlayPauseView>(R.id.pauseButton).fadeIn()
        }
    }

    private fun pauseButtonClicked() {
        if (model.isGamePaused()) {
            resumeAction.execute()
            resumeGame()
        } else
            pauseGame()
    }

    private fun resumeGame() {
        if (model.isGamePaused()) {
            startGame()
            model.setGameResume()
        }
    }

}