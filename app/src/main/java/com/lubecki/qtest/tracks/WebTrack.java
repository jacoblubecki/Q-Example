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

package com.lubecki.qtest.tracks;

import com.jlubecki.q.MediaType;
import com.jlubecki.q.QTrack;

/**
 * Implementation of {@link QTrack} that we can use to reference our web media URI.
 */
public class WebTrack extends QTrack {

  public static final String URI_PATTERN = "http(.*)(.mp3|.wav)";

  public WebTrack(int i) {
    title = "Test";
    artist = "Test Artist";

    // This is really bad code but it still gets the point across
    uri = i == 1
        ? "http://www.looperman.com/media/loops/1664947/looperman-l-1664947-0088802-dubstepmiddle-dnb-kicks.mp3"
        : "https://wiki.teamfortress.com/w/images/3/33/Sf12_found05.wav";
    imagePath =
        "http://pre07.deviantart.net/f338/th/pre/f/2012/306/2/f/merasmus_by_nastyov-d5jqosc.png";

    mediaType = MediaType.AUDIO;
  }
}
