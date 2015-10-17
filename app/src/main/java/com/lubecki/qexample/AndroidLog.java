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

import android.util.Log;
import com.lubecki.q.logging.DefaultLog;
import com.lubecki.q.logging.QLog;

/**
 * Now the Q will log everything using Android's {@link Log} implementation. The majority of logging
 * in the Q will use the name of the class that does the logging as the tag. Most of the log
 * messages in the Q are INFO priority.
 */
public class AndroidLog extends DefaultLog {

  @Override public void log(int priority, String tag, String message) {
    switch (priority) {
      case QLog.VERBOSE:
        Log.v(tag, message);
        break;

      case QLog.DEBUG:
        Log.d(tag, message);
        break;

      case QLog.INFO:
        Log.i(tag, message);
        break;

      case QLog.WARN:
        Log.w(tag, message);
        break;

      case QLog.ERROR:
        Log.e(tag, message);
        break;

      case QLog.WTF:
        Log.wtf(tag, message);
        break;

      default:
        Log.i(tag, message);
        break;
    }
  }
}
