/*
 * movie-renamer-core
 * Copyright (C) 2012 Nicolas Magré
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
  private int channel;
  private String bitRateMode;
  private Locale language;
  private int bitRate;

  public MediaAudio(int stream) {
    this.stream = stream;
    codec = "?";
    channel = -1;
    bitRate = -1;
    title = "?";
    bitRateMode = "?";
    language = Locale.ROOT;
  }

  public int getStream() {
    return stream;
  }

  public int getChannel() {
    return channel;
  }

  public void setChannel(int channel) {
    this.channel = channel;
  }

  public String getCodec() {
    return codec;
  }

  public void setCodec(String codec) {
    this.codec = codec;
  }

  public Locale getLanguage() {
    return language;
  }

  public void setLanguage(Locale language) {
    this.language = language;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBitRateMode() {
    return bitRateMode;
  }

  public void setBitRateMode(String bitRateMode) {
    this.bitRateMode = bitRateMode;
  }

  public int getBitRate() {
    return bitRate;
  }

  public void setBitRate(int bitRate) {
    this.bitRate = bitRate;
  }

  @Override
  public String toString(){
    return title;
  }
}
