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

package com.jlubecki.qtest.players;

import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.widget.VideoView;
import com.jlubecki.q.playback.Player;
import com.jlubecki.q.playback.PlayerEventCallback;
import com.jlubecki.q.playback.PlayerState;

/**
 * Created by Jacob on 10/17/15.
 */
public class WebVideoPlayerSimple extends Player {

  private VideoView videoView;
  private boolean started = false;

  public WebVideoPlayerSimple(PlayerEventCallback callback, VideoView videoView) {
    super(callback);
    this.videoView = videoView;
  }

  @Override public void prepare(String s) {
    changeState(PlayerState.PREPARING);
    videoView.requestFocus();
    videoView.setVideoPath(s);
    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      @Override public void onCompletion(MediaPlayer mediaPlayer) {
        notifyIfTrackEnded();
      }
    });
    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
      @Override public void onPrepared(MediaPlayer mediaPlayer) {
        notifyIfPrepared();
      }
    });
  }

  @Override public void justPrepare(String s) {
    changeState(PlayerState.PREPARING);
    videoView.requestFocus();
    videoView.setVideoPath(s);
    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      @Override public void onCompletion(MediaPlayer mediaPlayer) {
        notifyIfTrackEnded();
      }
    });
    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
      @Override public void onPrepared(MediaPlayer mediaPlayer) {
        changeState(PlayerState.PAUSED);
      }
    });
  }

  @Override public void seekTo(int i) {
    videoView.seekTo(i);
  }

  @Override public int getCurrentTime() {
    return videoView.getCurrentPosition();
  }

  @Override public int getDuration() {
    return videoView.getDuration();
  }

  @Override public void play() {
    super.play();
    if(!started) {
      videoView.start();
    } else {
      videoView.resume();
    }
  }

  @Override public void pause() {
    super.pause();
    videoView.pause();
  }

  @Override public void stop() {
    super.stop();
    videoView.stopPlayback();
  }

  @Override public void release() {
    super.release();
    videoView.getHolder().getSurface().release();
  }
}
