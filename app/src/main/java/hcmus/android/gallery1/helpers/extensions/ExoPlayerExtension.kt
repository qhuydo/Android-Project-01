package hcmus.android.gallery1.helpers.extensions

import com.google.android.exoplayer2.ExoPlayer

fun ExoPlayer.muteAudio(isMuted: Boolean = true) {
    audioComponent?.volume = if (isMuted) 0f else 1f
}