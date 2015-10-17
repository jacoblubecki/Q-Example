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

package com.lubecki.qexample;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.lubecki.q.Loop;
import com.lubecki.q.Q;
import com.lubecki.q.QEventListener;
import com.lubecki.q.QState;
import com.lubecki.q.QTrack;
import com.lubecki.q.logging.LogLevel;
import com.lubecki.q.logging.QLog;

import com.lubecki.q.playback.Player;
import com.lubecki.q.playback.PlayerEventCallback;
import com.lubecki.q.playback.PlayerManager;
import com.lubecki.q.playback.PlayerState;

import com.lubecki.qexample.players.WebAudioPlayerSimple;
import com.lubecki.qexample.tracks.WebTrack;
import com.lubecki.qexample.players.FileAudioPlayerSimple;
import com.lubecki.qexample.tracks.LocalTrack;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

  private Loop currentLoop = Loop.NONE;
  private boolean shuffling = false;

  private ArrayList<QTrack> trackList = new ArrayList<>();

  private ListView list;
  private TextView text1;
  private TextView text2;
  private TextView text3;
  private SeekBar seek;

  //region Q related callbacks

  private Q music;
  private QEventListener qEventListener = new QEventListener() {
    @Override public void onEvent(QState qState) {
      switch (qState) {
        case PLAYBACK_ENDED:
          // music ended so we shouldn't keep playing audio, but we can still prepare the track
          // which will also make sure the UI updates properly
          music.prepare();
          break;

        // Add more cases to handle different callbacks. Most things should be triggered by the
        // player events.
      }
    }
  };
  private PlayerEventCallback callback = new PlayerEventCallback() {
    @Override public void onEvent(PlayerState playerState, String s) {
      text1.setText(String.format("Shuffling: %s\nLooping: %s", shuffling, currentLoop));

      // The players will fire off CREATED callbacks when they are created. If we create the players
      // before the Q, we'll get a null pointer. Not very attractive, but easy to deal with.
      if (music != null) {
        Player player = PlayerManager.getInstance().getCurrentPlayer();

        switch (playerState) {
          case PLAYING:
            // update the SeekBar with the duration of the currently playing song
            seek.setMax(player.getDuration());

            handler.postDelayed(moveSeekBarThread, 100);
            break;

          case PREPARED:
            music.play(); // begin playback when a player is prepared
            break;

          case STOPPED:
            // sort of a fail safe to clear the UI after tracks end
            time = 0;
            seek.setProgress(0);
            seek.setMax(0);
            break;

          case TRACK_ENDED:
            music.next(); // move to the next track when a track ends
            break;
        }

        QTrack track = music.getCurrent();

        updateUi(player, track);
      }
    }
  };

  //endregion

  //region SeekBar

  private int time;
  private Handler handler = new Handler();
  private Runnable moveSeekBarThread = new Runnable() {

    public void run() {
      Player player = PlayerManager.getInstance().getCurrentPlayer();
      if (player.getState() == PlayerState.PLAYING) {

        updateUi(player, music.getCurrent());

        handler.postDelayed(this, 100);
      }
    }
  };

  //endregion

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(com.lubecki.qexample.R.layout.activity_main);

    prepareViews();

    getAudioContent();

    setupMusic();
  }

  //region OnCreate methods

  private void prepareViews() {

    list = (ListView) findViewById(com.lubecki.qexample.R.id.listView);
    text1 = (TextView) findViewById(com.lubecki.qexample.R.id.text1);
    text2 = (TextView) findViewById(com.lubecki.qexample.R.id.text2);
    text3 = (TextView) findViewById(com.lubecki.qexample.R.id.text3);
    seek = (SeekBar) findViewById(com.lubecki.qexample.R.id.seekBar);

    seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int milliseconds, boolean b) {
        // track the current time in the current song
        time = milliseconds;
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
        // pause the music if we start seeking
        PlayerState state = PlayerManager.getInstance().getCurrentPlayer().getState();
        if (state == PlayerState.PLAYING && state != PlayerState.PREPARING) {
          music.pause();
        }
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
        PlayerState state = PlayerManager.getInstance().getCurrentPlayer().getState();

        // seek to the desired time
        music.seekTo(seekBar.getProgress());

        // resume playback
        if (state == PlayerState.PAUSED) {
          music.play();
        }
      }
    });
  }

  public void getAudioContent() {
    String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

    String[] projection = {
        MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DURATION
    };

    Cursor cursor =
        getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
            selection, null, null);

    // Add local tracks
    if (cursor != null) {
      while (cursor.moveToNext()) {
        String s =
            cursor.getString(0) + "||" + cursor.getString(1) + "||" + cursor.getString(2) + "||"
                + cursor.getString(3) + "||" + cursor.getString(4) + "||" + cursor.getString(5);

        LocalTrack track = new LocalTrack();

        track.title = cursor.getString(2);
        track.artist = cursor.getString(1);
        track.image = null;
        track.uri = cursor.getString(3);

        Log.i("MAIN", track.uri);

        if (track.uri.endsWith(".wav") || track.uri.endsWith(".mp3")) {
          trackList.add(track);
        }

        Log.i(this.getClass().getSimpleName(), s);
      }

      cursor.close();
    }

    // Add web tracks
    for (int i = 0; i <= 1; i++) {
      WebTrack track = new WebTrack(i);
      track.title += i;

      trackList.add(track);
    }

    ArrayList<String> titles = new ArrayList<>();
    for (QTrack track : trackList) {
      titles.add(track.title);
    }

    ArrayAdapter<String> adapter =
        new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles);

    list.setAdapter(adapter);
    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        music.setIndex(i);
      }
    });
  }

  private void setupMusic() {
    // Prepare logging
    QLog.setLogger(new AndroidLog()); // use Android's Log class instead of System.out.println();
    QLog.ignoreIllegalStates(false); // kill the app if something happens that shouldn't
    QLog.setLogLevel(LogLevel.FULL); // log everything

    // Technically they both use the same logic internally, but we can still watch them change to
    // handle the separate track URIs
    // The callback logic should probably be the same for every player, so we can use the same one
    // for both of them
    WebAudioPlayerSimple web = new WebAudioPlayerSimple(callback);
    FileAudioPlayerSimple local = new FileAudioPlayerSimple(callback);

    music = Q.getInstance();
    music.setListener(qEventListener);

    // We don't really need 2 track types, only difference is the URI_PATTERN constant
    music.addPlayer(WebTrack.URI_PATTERN, web); // pattern = "http(.*)(.mp3|.wav)"
    music.addPlayer(LocalTrack.URI_PATTERN, local); // pattern = "\\/storage(.*)(.mp3|.wav)"

    // Add tracks
    music.setTrackList(trackList);

    // Prepares the first track for playback
    music.prepare();
  }

  //endregion

  public void updateUi(Player player, QTrack track) {

    // Don't update the UI if the track list is empty, nothing to update.
    if (track != null) {

      text2.setText(String.format("%s\n%s\nPlayer: %s", track.title, track.artist,
          player.getClass().getSimpleName()));

      time = player.getCurrentTime();

      // actual time in the song
      int seconds = time / 1000;

      int minutes = seconds / 60;
      int remainingSeconds = seconds - minutes * 60;

      // duration of the entire song
      int durationMillis = seek.getMax();
      int dur = durationMillis / 1000;
      int durMinutes = dur / 60;
      int remainingDurSeconds = dur - durMinutes * 60;

      seek.setProgress(time);
      text3.setText(String.format("%d:%02d / %d:%02d", minutes, remainingSeconds, durMinutes,
          remainingDurSeconds));
    }
  }

  //region OnClick methods for buttons

  public void previous(View view) {
    music.previous(); // move to the previous track

    // Get info about current player and track to update UI
    QTrack track = music.getCurrent();
    Player player = PlayerManager.getInstance().getCurrentPlayer();

    updateUi(player, track);
  }

  public void playPause(View view) {
    PlayerState playerState = PlayerManager.getInstance().getCurrentPlayer().getState();

    // Kinda messy, but toggles play/pause.
    if (playerState == PlayerState.PAUSED || playerState == PlayerState.CREATED
        || playerState == PlayerState.STOPPED) {
      music.play();
    } else if (playerState == PlayerState.PLAYING) {
      music.pause();
    }
  }

  public void next(View view) {
    music.next(); // move to the next track

    // update UI
    QTrack track = music.getCurrent();
    Player player = PlayerManager.getInstance().getCurrentPlayer();

    updateUi(player, track);
  }

  public void shuffle(View view) {

    // toggle the shuffle state
    // second boolean will cause the current song to change if it were set to true
    if (shuffling) {
      music.setShuffling(false, false);
      shuffling = false;
    } else {
      music.setShuffling(true, false);
      shuffling = true;
    }

    text1.setText(String.format("Shuffling: %s%nLooping: %s", shuffling, currentLoop));
  }

  public void loop(View view) {

    // cycle through the 3 loop states
    switch (currentLoop) {
      case NONE:
        music.setLooping(Loop.SINGLE);
        currentLoop = Loop.SINGLE;
        break;

      case SINGLE:
        music.setLooping(Loop.LIST);
        currentLoop = Loop.LIST;
        break;

      case LIST:
        music.setLooping(Loop.NONE);
        currentLoop = Loop.NONE;
        break;
    }

    text1.setText(String.format("Shuffling: %s\nLooping: %s", shuffling, currentLoop));
  }

  //endregion

  @Override public void onDestroy() {

    // Free up resources used by the Q
    music.release();

    super.onDestroy();
  }
}
