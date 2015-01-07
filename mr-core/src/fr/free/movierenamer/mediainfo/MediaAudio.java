/*
 * movie-renamer-core
 * Copyright (C) 2012-2014 Nicolas Magré
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.free.movierenamer.mediainfo;

import java.util.Locale;

/**
 * Class MediaAudio
 *
 * @author Nicolas Magré
 */
public class MediaAudio {

  private final int stream;
  private String title;
  private String codec;
  private String channel;
  private String nbchannel;
  private String bitRateMode;
  private Locale language;
  private int bitRate;

  public MediaAudio(final int stream) {
    this.stream = stream;
    codec = "";
    channel = "";
    bitRate = 0;
    title = "";
    bitRateMode = "";
    language = Locale.ROOT;
  }

  public int getStream() {
    return stream;
  }

  public String getChannel() {
    return channel;
  }

  public String getNbChannel() {
    return nbchannel;
  }

  public void setChannel(final String channel) {
    this.channel = channel;
  }

  public void setNbChannel(final String nbchannel) {
    this.nbchannel = nbchannel;
  }

  public String getCodec() {
    return codec;
  }

  public void setCodec(final String codec) {
    this.codec = codec;
  }

  public Locale getLanguage() {
    return language;
  }

  public void setLanguage(final Locale language) {
    this.language = language;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public String getBitRateMode() {
    return bitRateMode;
  }

  public void setBitRateMode(final String bitRateMode) {
    this.bitRateMode = bitRateMode;
  }

  public int getBitRate() {
    return bitRate;
  }

  public void setBitRate(final int bitRate) {
    this.bitRate = bitRate;
  }

  @Override
  public String toString() {
    return title;
  }
}
