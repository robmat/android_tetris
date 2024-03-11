package com.batodev.tetris.presentation.game.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.batodev.tetris.R
import com.batodev.tetris.infra.images.ImageData
import com.batodev.tetris.infra.images.ImageHelper
import com.batodev.tetris.presentation.common.GAME_RESULT
import com.batodev.tetris.presentation.common.getButtons
import com.batodev.tetris.presentation.finished.FinishedActivity
import com.batodev.tetris.presentation.game.GameViewModel
import com.batodev.tetris.presentation.game.PlayPauseView
import com.batodev.tetris.presentation.settings.SettingsSingleton
import com.batodev.tetris.presentation.game.State
import com.batodev.tetris.presentation.game.actions.Action
import com.batodev.tetris.presentation.game.actions.ResumeToastAction
import com.batodev.tetris.presentation.game.grid.GameAdapter
import com.batodev.tetris.presentation.game.results.GameResult
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class GameFragment : Fragment(), View.OnClickListener {

    private lateinit var adapter: GameAdapter
    private lateinit var model: GameViewModel
    private lateinit var resumeAction: Action
    private lateinit var moveBlockDown: Job
    private lateinit var imageData: ImageData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageData = ImageHelper.pickTierOneImage(requireActivity())
        setUpViewModel(imageData.fileName)
        setUpGridView(imageData.bitmap)
        setUpButtons()
        setUpResumeAction()
        setUpLogger()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    private fun setUpViewModel(imageName: String) {
        model = ViewModelProvider(requireActivity())[GameViewModel::class.java]
        model.setUp(SettingsSingleton.getFacade(requireContext()), SettingsSingleton.getSpeedStrategy(requireContext()))
        model.setUpMusic(SettingsSingleton.getSettingsData(requireContext()).hasMusic, requireContext())
        model.setUpImage(imageData.fileName)
        model.gameFacade.observe(viewLifecycleOwner) {
            if (!it.hasFinished())
                updateScreen()
            else
                finishGame()
        }
    }

    private fun setUpGridView(bitmap: Bitmap) {
        val cellColors = SettingsSingleton.getStyleCreator(requireContext()).getColorCellChooser()
        adapter = GameAdapter(model.getGrid(), cellColors)
        requireView().findViewById<GridView>(R.id.GameGrid).adapter = adapter
        requireView().findViewById<ImageView>(R.id.GameImage).setImageBitmap(bitmap)
    }

    private fun setUpLogger() {
        if (!model.isGameStarted())
            SettingsSingleton.logData(requireContext())
    }

    private fun setUpButtons() {
        (requireView() as ViewGroup).getButtons().forEach { it.setOnClickListener(this) }
        requireView().findViewById<PlayPauseView>(R.id.pauseButton).setOnClickListener(this)
        requireView().findViewById<Button>(R.id.DownButton).setOnLongClickListener { model.dropBlock();true }
    }

    private fun updateScreen() {
        adapter.gameCells = model.getGrid()
        adapter.notifyDataSetChanged()
        requireView().findViewById<TextView>(R.id.PointsText).text = model.getPoints().toString()
        val typeOfBlock = model.getNextBlock()
        requireView().findViewById<ImageView>(R.id.NextBlockImage).setImageResource(
            SettingsSingleton.getStyleCreator(requireContext()).getBlockCreator().getImageId(typeOfBlock)
        )
    }

    private fun setUpResumeAction() {
        resumeAction = ResumeToastAction(requireContext())
    }

    private fun finishGame() {
        val finish = Intent(requireContext(), FinishedActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra(
                GAME_RESULT,
                GameResult(score = model.getPoints(), date = Date())
            )
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