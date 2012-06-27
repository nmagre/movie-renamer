/*
 * Movie Renamer
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
package fr.free.movierenamer.media;

import fr.free.movierenamer.media.MediaInfo.StreamKind;
import fr.free.movierenamer.utils.Utils;
import java.io.File;
import java.util.Scanner;

/**
 * Class MediaTag
 *
 * @author Nicolas Magré
 */
public class MediaTag {

  private MediaInfo mediaInfo;
  private File mediaFile;
  private static final String empty = "";
  private boolean libMediaInfo = Utils.libMediaInfo();

  public MediaTag(File mediaFile) {
    this.mediaFile = mediaFile;
    mediaInfo = null;
  }

  public String getContainerFormat() {
    if (!libMediaInfo) {
      return empty;
    }
    String extensions = getMediaInfo(StreamKind.General, 0, "Codec/Extensions", "Format");
    return new Scanner(extensions).next().toLowerCase();
  }

  public String getFileSize() {
    if (!libMediaInfo) {
      return empty;
    }
    String fileSize = getMediaInfo(StreamKind.General, 0, "FileSize/String4", "FileSize/String");
    return fileSize;
  }

  public String getDuration() {
    if (!libMediaInfo) {
      return empty;
    }
    String duration = getMediaInfo(StreamKind.General, 0, "Duration/String");
    return duration;
  }

  public String getVideoCodec() {
    if (!libMediaInfo) {
      return empty;
    }
    String codec = getMediaInfo(StreamKind.Video, 0, "Encoded_Library/Name", "CodecID/Hint", "Format");
    return new Scanner(codec).next();
  }

  public String getVideoFrameRate() {
    if (!libMediaInfo) {
      return empty;
    }
    String frameRate = getMediaInfo(StreamKind.Video, 0, "FrameRate", "FrameRate/String");
    return frameRate;
  }

  public String getVideoFormat() {
    if (!libMediaInfo) {
      return empty;
    }
    String height = getMediaInfo(StreamKind.Video, 0, "Height");
    String scanType = getMediaInfo(StreamKind.Video, 0, "ScanType");

    if (height == null || scanType == null) {
      return "";
    }
    return height + Character.toLowerCase(scanType.charAt(0));
  }

  public String getVideoResolution() {
    if (!libMediaInfo) {
      return empty;
    }
    String width = getMediaInfo(StreamKind.Video, 0, "Width");
    String height = getMediaInfo(StreamKind.Video, 0, "Height");

    if (width == null || height == null) {
      return "";
    }
    return width + 'x' + height;
  }

  public String getVideoDefinitionCategory() {
    if (!libMediaInfo) {
      return empty;
    }
    String width = getMediaInfo(StreamKind.Video, 0, "Width");
    return Integer.parseInt(width) < 900 ? "SD" : "HD";
  }

  private int getAudioStreamCount() {
    if (!libMediaInfo) {
      return -1;
    }
    String count = getMediaInfo(StreamKind.Audio, 0, "StreamCount");
    if (!Utils.isDigit(count)) {
      return 0;
    }

    return Integer.parseInt(count);
  }

  public String getAudioCodec(int stream) {
    if (!libMediaInfo) {
      return empty;
    }
    String codec = getMediaInfo(StreamKind.Audio, stream, "CodecID/Hint", "Format");
    return codec.replaceAll("\\p{Punct}", "");
  }

  public String getAudioCodecString(String separator, int limit) {
    if (!libMediaInfo) {
      return empty;
    }

    int count = getAudioStreamCount();
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < count; i++) {
      res.append(getAudioCodec(i));
      if (i + 1 < count) {
        res.append(separator);
      }
    }
    return res.toString();
  }

  public String getAudioLanguage(int stream) {
    if (!libMediaInfo) {
      return empty;
    }
    String language = getMediaInfo(StreamKind.Audio, stream, "Language/String");
    if (language == null) {
      return "";
    }

    return language.toLowerCase();
  }

  public String getAudioLanguageString(String separator, int limit) {
    if (!libMediaInfo) {
      return empty;
    }

    int count = getAudioStreamCount();
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < count; i++) {
      String lang = getAudioLanguage(i);
      if (lang.equals("")) {
        continue;
      }
      res.append(lang);
      if (i + 1 < count) {
        res.append(separator);
      }
    }
    return res.toString();
  }

  public String getAudioChannels(int stream) {
    if (!libMediaInfo) {
      return empty;
    }
    String channels = getMediaInfo(StreamKind.Audio, stream, "Channel(s)");
    if (channels == null) {
      return "";
    }
    return channels + "ch";
  }

  public String getAudioChannelsString(String separator, int limit) {
    if (!libMediaInfo) {
      return empty;
    }

    int count = getAudioStreamCount();
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < count; i++) {
      String chan = getAudioChannels(i);
      if (chan.equals("")) {
        continue;
      }
      res.append(chan);
      if (i + 1 < count) {
        res.append(separator);
      }
    }
    return res.toString();
  }

  public String getAudioTitle(int stream) {
    if (!libMediaInfo) {
      return empty;
    }
    String title = getMediaInfo(StreamKind.Audio, stream, "Title");
    if (title == null) {
      return "";
    }
    return title;
  }

  public String getAudioTitleString(String separator, int limit) {
    if (!libMediaInfo) {
      return empty;
    }

    int count = getAudioStreamCount();
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < count; i++) {
      String title = getAudioTitle(i);
      if (title.equals("")) {
        continue;
      }
      res.append(title);
      if (i + 1 < count) {
        res.append(separator);
      }
    }
    return res.toString();
  }

  private int getTextStreamCount() {
    if (!libMediaInfo) {
      return -1;
    }
    String count = getMediaInfo(StreamKind.Text, 0, "StreamCount");
    if (!Utils.isDigit(count)) {
      return 0;
    }

    return Integer.parseInt(count);
  }

  public String getTextTitle(int stream) {
    if (!libMediaInfo) {
      return empty;
    }
    String title = getMediaInfo(StreamKind.Text, stream, "Title");
    if (title == null) {
      return "";
    }
    return title;
  }

  public String getTextTitleString(String separator, int limit) {
    if (!libMediaInfo) {
      return empty;
    }

    int count = getAudioStreamCount();
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < count; i++) {
      String textTitle = getTextTitle(i);
      if (textTitle.equals("")) {
        continue;
      }
      res.append(textTitle);
      if (i + 1 < count) {
        res.append(separator);
      }
    }
    return res.toString();
  }

  private synchronized MediaInfo getMediaInfo() {
    if (mediaInfo == null) {
      MediaInfo newMediaInfo = new MediaInfo();
      if (!newMediaInfo.open(mediaFile)) {
        throw new RuntimeException("Cannot open media file: " + mediaFile);
      }

      mediaInfo = newMediaInfo;
    }

    return mediaInfo;
  }

  private String getMediaInfo(StreamKind streamKind, int streamNumber, String... keys) {
    for (String key : keys) {
      String value = getMediaInfo().get(streamKind, streamNumber, key);

      if (value.length() > 0) {
        return value;
      }
    }

    return null;
  }
}
