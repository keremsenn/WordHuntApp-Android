package com.keremsen.wordmaster.viewmodel
import android.app.Application
import android.media.MediaPlayer
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.State
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.keremsen.wordmaster.R

class MusicPlayerViewModel(application: Application) : AndroidViewModel(application), LifecycleEventObserver {

    private val _mediaPlayer: MediaPlayer = MediaPlayer.create(getApplication(), R.raw.naturevoice)
    private val _isMusicOn = mutableStateOf(false)
    val isMusicOn: State<Boolean> = _isMusicOn

    private val _isMusicPause= mutableStateOf(true)
    val isMusicPause: State<Boolean> = _isMusicOn

    init {
        _mediaPlayer.setOnCompletionListener {
            it.seekTo(0)
            it.start()
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE -> pauseMusic()
            Lifecycle.Event.ON_DESTROY -> releasePlayer()
            Lifecycle.Event.ON_START -> if(isMusicOn.value) startMusic()
            else -> Unit
        }
    }

    fun pauseMusic() {
        if (_mediaPlayer.isPlaying) {
            _mediaPlayer.pause()
            _isMusicPause.value = true
        }else _isMusicPause.value= true
    }

    fun releasePlayer() {
        _mediaPlayer.release()
    }

    fun startMusic() {
        if (!_mediaPlayer.isPlaying) {
            _mediaPlayer.start()
            _isMusicOn.value = true
            _isMusicPause.value = false
        }
    }

    fun stopMusic() {
        if (_mediaPlayer.isPlaying) {
            _mediaPlayer.pause()
            _isMusicOn.value = false
        }else _isMusicOn.value = false
    }


    override fun onCleared() {
        _mediaPlayer.release()
        super.onCleared()
    }
}

