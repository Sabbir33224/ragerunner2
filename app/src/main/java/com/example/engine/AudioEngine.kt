package com.example.engine

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.sin

class AudioEngine {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var musicJob: Job? = null

    var musicVolume: Float = 0.8f
    var sfxVolume: Float = 1.0f

    fun playJump() {
        if (sfxVolume <= 0f) return
        scope.launch {
            playToneSweep(440f, 880f, 80)
        }
    }

    fun playDeath() {
        if (sfxVolume <= 0f) return
        scope.launch {
            playToneSweep(600f, 100f, 200)
        }
    }

    fun playCoin() {
        if (sfxVolume <= 0f) return
        scope.launch {
            playToneSweep(987.77f, 1318.51f, 100)
        }
    }

    fun playBounce() {
        if (sfxVolume <= 0f) return
        scope.launch {
            playToneSweep(300f, 700f, 120)
        }
    }

    fun startMusic() {
        stopMusic()
        musicJob = scope.launch {
            var track: AudioTrack? = null
            try {
                val sampleRate = 22050
                val numSamples = sampleRate * 4
                val buffer = ShortArray(numSamples)

                val notes = doubleArrayOf(261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88, 523.25)
                var noteIndex = 0

                track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(numSamples * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                track.play()

                while (musicJob?.isActive == true) {
                    val freq = notes[noteIndex % notes.size]
                    noteIndex++

                    for (i in buffer.indices) {
                        val angle = 2.0 * Math.PI * i / (sampleRate / freq)
                        val sample = (sin(angle) * 8000 * musicVolume).toInt().toShort()
                        buffer[i] = sample
                    }
                    track.write(buffer, 0, buffer.size)
                }
            } catch (_: Exception) {
            } finally {
                try {
                    track?.stop()
                    track?.release()
                } catch (_: Exception) {}
            }
        }
    }

    fun stopMusic() {
        musicJob?.cancel()
        musicJob = null
    }

    private fun playToneSweep(startFreq: Float, endFreq: Float, durationMs: Int) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
            if (numSamples <= 0) return

            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val progress = i.toFloat() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * progress
                val angle = 2.0 * Math.PI * i / (sampleRate / currentFreq)
                buffer[i] = (sin(angle) * 12000 * sfxVolume).toInt().toShort()
            }

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()

            scope.launch {
                kotlinx.coroutines.delay(durationMs.toLong() + 60L)
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }
}
