/**
 * Copyright (C) rednoah
 *
 * This file is part of FileBot.
 *
 * FileBot is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 2 of the License, or (at your option) any later
 * version.
 *
 * FileBot is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * FileBot. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package fr.free.movierenamer.exception;

import com.sun.jna.Platform;

/**
 * Class MediaInfoException
 *
 * @author rednoah
 * @see
 * http://sourceforge.net/p/filebot/code/HEAD/tree/trunk/source/net/sourceforge/filebot/mediainfo/MediaInfoException.java
 */
public class MediaInfoException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public MediaInfoException(LinkageError e) {
    this(String.format("Unable to load %d-bit native library 'mediainfo'", Platform.is64Bit() ? 64 : 32), e);
  }

  public MediaInfoException(String msg, Throwable e) {
    super(msg, e);
  }

}
