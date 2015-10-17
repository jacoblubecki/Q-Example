/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Jacob Lubecki
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.lubecki.qtest.players;

import android.media.AudioManager;
import android.media.MediaPlayer;
import com.lubecki.q.playback.Player;
import com.lubecki.q.playback.PlayerEventCallback;
import com.lubecki.q.playback.PlayerState;
import java.io.IOException;

/**
 * This is cool because its a very simple implementation of a {@link MediaPlayer}. Only bare minimum
 * state handling is needed to turn regular media player code into something the Q can use.
 */
public class FileAudioPlayerSimple extends Player {

  private final MediaPlayer player;

  public FileAudioPlayerSimple(PlayerEventCallback callback) {
    super(callback);
    player = new MediaPlayer();
  }

  @Override public void prepare(String s) {
    try {
      changeState(PlayerState.PREPARING);

      player.reset();
      player.setAudioStreamType(AudioManager.STREAM_MUSIC);
      player.setDataSource(s);
      player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
        @Override public void onPrepared(MediaPlayer mediaPlayer) {
          notifyIfPrepared();
        }
      });
      player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override public void onCompletion(MediaPlayer mediaPlayer) {
          notifyIfTrackEnded();
        }
      });
      player.prepareAsync();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override public void justPrepare(String s) {
    try {
      changeState(PlayerState.PREPARING);

      player.reset();
      player.setAudioStreamType(AudioManager.STREAM_MUSIC);
      player.setDataSource(s);

      // Ready for playback but we don't want to actually start playing.
      player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
        @Override public void onPrepared(MediaPlayer mediaPlayer) {
          changeState(PlayerState.PAUSED);
        }
      });
      player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override public void onCompletion(MediaPlayer mediaPlayer) {
          notifyIfTrackEnded();
        }
      });
      player.prepareAsync();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override public void seekTo(int i) {
    player.seekTo(i);
  }

  @Override public int getCurrentTime() {
    return player.getCurrentPosition();
  }

  @Override public int getDuration() {
    return player.getDuration();
  }

  @Override public void play() {
    // Always call the super methods if they exist. This tracks the state of the player, and will
    // notify you if you have made a mistake in your handling of the state.
    super.play();
    player.start();
  }

  @Override public void pause() {
    super.pause();
    player.pause();
  }

  @Override public void stop() {
    super.stop();
    player.stop();
  }

  @Override public void release() {
    super.release();
    player.release();
  }
}
