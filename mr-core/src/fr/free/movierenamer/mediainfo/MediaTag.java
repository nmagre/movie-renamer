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

import fr.free.movierenamer.mediainfo.MediaInfo.StreamKind;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.NumberUtils;
import fr.free.movierenamer.utils.StringUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Class MediaTag
 *
 * @author Nicolas Magré
 */
public class MediaTag {

  private MediaInfo mediaInfo;
  private final File mediaFile;
  public final boolean libMediaInfo = Settings.MEDIAINFO;

  public enum TagType {

    Video,
    Audio,
    Text;
  }

  public enum Tags {

    ContainerFormat(StreamKind.General, true, "Codec/Extensions", "Format"),
    //    FileSize(StreamKind.General, Long.class, "FileSize/String4", "FileSize/String"),
    Duration(StreamKind.General, Long.class, "Duration"),
    VideoCodec(StreamKind.Video, true, "Encoded_Library/Name", "CodecID/Hint", "Format"),
    VideoFrameRate(StreamKind.Video, Double.class, "FrameRate"),
    VideoScanType(StreamKind.Video, "ScanType"),
    VideoFrameCount(StreamKind.Video, Long.class, "FrameCount"),
    VideoHeight(StreamKind.Video, Integer.class, "Height"),
    VideoWidth(StreamKind.Video, Integer.class, "Width"),
    VideoAspectRatio(StreamKind.Video, Float.class, "DisplayAspectRatio"),
    AudioStreamCount(StreamKind.Audio, Integer.class, "StreamCount"),
    AudioCodec(StreamKind.Audio, "CodecID/Hint", "Format"),
    AudioLanguage(StreamKind.Audio, "Language/String3"),
    AudioChannels(StreamKind.Audio, Integer.class, "Channel(s)"),
    AudioBitRateMode(StreamKind.Audio, "BitRate_Mode"),
    AudioBitRate(StreamKind.Audio, Integer.class, "BitRate"),
    AudioTitle(StreamKind.Audio, "Title"),
    TextStreamCount(StreamKind.Text, Integer.class, "StreamCount"),
    TextTitle(StreamKind.Text, "Title"),
    TextLanguage(StreamKind.Text, "Language/String3");
    private StreamKind kind;
    private String[] keys;
    private boolean getFirst = false;
    private Class<?> clazz = String.class;

    Tags(StreamKind kind, String... keys) {
      this.kind = kind;
      this.keys = keys;
    }

    Tags(StreamKind kind, Class<?> clazz, String... keys) {
      this.kind = kind;
      this.clazz = clazz;
      this.keys = keys;
    }

    Tags(StreamKind kind, boolean getFirst, String... keys) {
      this.kind = kind;
      this.keys = keys;
      this.getFirst = getFirst;
    }

    public StreamKind getStreamKind() {
      return kind;
    }

    public Class<?> getVClass() {
      return clazz;
    }

    public String[] getKeys() {
      return keys.clone();
    }

    public boolean isGetFirst() {
      return getFirst;
    }
  }

  public MediaTag(File mediaFile) {
    this.mediaFile = mediaFile;
    mediaInfo = null;
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

  private Object getMediaInfo(Tags tag) {
    return getMediaInfo(tag, 0);
  }

  private Object getMediaInfo(Tags tag, int streamNumber) {
    for (String key : tag.getKeys()) {
      String value = getMediaInfo().get(tag.getStreamKind(), streamNumber, key);

      String nvalue = value.length() > 0 ? new Scanner(value).next() : " ";

      if (tag.getVClass().equals(Integer.class)) {
        return NumberUtils.isNumeric(nvalue) ? Integer.parseInt(nvalue) : 0;
      } else if (tag.getVClass().equals(Float.class)) {
        return NumberUtils.isNumeric(nvalue) ? Float.parseFloat(nvalue) : 0.0F;
      } else if (tag.getVClass().equals(Double.class)) {
        return NumberUtils.isNumeric(nvalue) ? Double.parseDouble(nvalue) : 0.0;
      } else if (tag.getVClass().equals(Long.class)) {
        return NumberUtils.isNumeric(nvalue) ? Long.parseLong(nvalue) : 0L;
      }

      if (tag.isGetFirst()) {
        return nvalue.toLowerCase();
      }

      return value.replaceAll("\\p{Punct}", StringUtils.EMPTY);
    }

    return StringUtils.EMPTY;
  }

  public String getContainerFormat() {
    if (!libMediaInfo) {
      return StringUtils.EMPTY;
    }
    return getMediaInfo(Tags.ContainerFormat).toString();
  }

  public Long getDuration() {
    long rvalue = 0L;
    if (!libMediaInfo) {
      return rvalue;
    }

    Object value = getMediaInfo(Tags.Duration);
    if (value instanceof Long) {
      rvalue = (Long) value;
    }
    return rvalue;
  }

  public MediaVideo getMediaVideo() {
    MediaVideo mediaVideo = new MediaVideo();
    if (!libMediaInfo) {
      return mediaVideo;
    }
    Object value = getMediaInfo(Tags.VideoAspectRatio);
    if (value instanceof Float) {
      mediaVideo.setAspectRatio((Float) value);
    }
    value = getMediaInfo(Tags.VideoFrameCount);
    if (value instanceof Long) {
      mediaVideo.setFrameCount((Long) value);
    }
    value = getMediaInfo(Tags.VideoFrameRate);
    if (value instanceof Double) {
      mediaVideo.setFrameRate((Double) value);
    }
    value = getMediaInfo(Tags.VideoHeight);
    if (value instanceof Integer) {
      mediaVideo.setHeight((Integer) value);
    }
    value = getMediaInfo(Tags.VideoWidth);
    if (value instanceof Integer) {
      mediaVideo.setWidth((Integer) value);
    }
    mediaVideo.setScanType(getMediaInfo(Tags.VideoScanType).toString());
    mediaVideo.setCodec(getMediaInfo(Tags.VideoCodec).toString());

    return mediaVideo;
  }

  public List<MediaAudio> getMediaAudios() {
    List<MediaAudio> mediaAudio = new ArrayList<MediaAudio>();
    if (!libMediaInfo) {
      return mediaAudio;
    }

    int count = (Integer) getMediaInfo(Tags.AudioStreamCount);

    Object intObj;
    for (int i = 0; i < count; i++) {
      MediaAudio maudio = new MediaAudio(i);
      maudio.setCodec(getMediaInfo(Tags.AudioCodec, i).toString());
      Locale lang = LocaleUtils.getLanguageMap().get(getMediaInfo(Tags.AudioLanguage, i).toString());
      if (lang != null) {
        maudio.setLanguage(lang);
      }
      intObj = getMediaInfo(Tags.AudioChannels, i);
      if (intObj instanceof Integer) {
        maudio.setChannel((Integer) intObj);
      }
      maudio.setBitRateMode(getMediaInfo(Tags.AudioBitRateMode, i).toString());
      intObj = getMediaInfo(Tags.AudioBitRate, i);
      if (intObj instanceof Integer) {
        maudio.setBitRate((Integer) intObj);
      }
      maudio.setTitle(getMediaInfo(Tags.AudioTitle, i).toString());

      mediaAudio.add(maudio);
    }

    return mediaAudio;
  }

  public List<MediaSubTitle> getMediaSubTitles() {
    List<MediaSubTitle> subTitles = new ArrayList<MediaSubTitle>();
    if (!libMediaInfo) {
      return subTitles;
    }

    if (getMediaInfo(Tags.TextStreamCount) instanceof Integer) {
      int count = (Integer) getMediaInfo(Tags.TextStreamCount);
      for (int i = 0; i < count; i++) {
        MediaSubTitle subTitle = new MediaSubTitle(i);
        Locale lang = LocaleUtils.getLanguageMap().get(getMediaInfo(Tags.TextLanguage, i).toString());
        subTitle.setLanguage(lang);
        subTitle.setTitle(getMediaInfo(Tags.TextTitle, i).toString());
        subTitles.add(subTitle);
      }
    }

    return subTitles;
  }

  public String getTagString(Tags tag, String separator, int limit) {
    if (!libMediaInfo) {
      return StringUtils.EMPTY;
    }

    int count = 0;
    switch (tag.getStreamKind()) {
      case Audio:
        if (getMediaInfo(Tags.AudioStreamCount) instanceof Integer) {
          count = (Integer) getMediaInfo(Tags.AudioStreamCount);
        }
        if (tag.equals(Tags.AudioStreamCount)) {
          return "" + count;
        }
        break;
      case Text:
        if (getMediaInfo(Tags.TextStreamCount) instanceof Integer) {
          count = (Integer) getMediaInfo(Tags.TextStreamCount);
        }
        if (tag.equals(Tags.TextStreamCount)) {
          return "" + count;
        }
        break;
      default:
        return getMediaInfo(tag).toString();
    }

    StringBuilder res = new StringBuilder();
    for (int i = 0; i < count; i++) {
      String info = getMediaInfo(tag, i).toString();
      if (!info.equals(StringUtils.EMPTY)) {
        res.append(info);
        if (i + 1 < count && limit <= 0 || i < limit - 1) {
          res.append(separator);
        }
      }
      if (limit > 0 && i == limit - 1) {
        break;
      }
    }
    return res.toString();
  }

  @Override
  public String toString() {
    MediaVideo video = getMediaVideo();
    List<MediaAudio> audios = getMediaAudios();
    List<MediaSubTitle> subTitles = getMediaSubTitles();

    String res = "Media Info : \n";
    res += "  Container format : " + getContainerFormat() + " \n";
    res += "  Duration : " + getDuration() + " \n";
    res += "  Video codec : " + video.getCodec() + " \n";
    res += "  Video scan type : " + video.getScanType() + " \n";
    res += "  Video aspect ratio : " + video.getAspectRatio() + " \n";
    res += "  Video frame count : " + video.getFrameCount() + " \n";
    res += "  Video width : " + video.getWidth() + " \n";
    res += "  Video height : " + video.getHeight() + " \n";
    res += "  Video resolution : " + video.getVideoResolution() + " \n";
    res += "  Video definition : " + video.getVideoDefinition() + " \n";

    StringBuilder str = new StringBuilder();
    for (MediaAudio audio : audios) {
      str.append("  Audio stream ").append(audio.getStream()).append("\n");
      str.append("    Audio title : ").append(audio.getTitle()).append("\n");
      str.append("    Audio codec : ").append(audio.getCodec()).append("\n");
      str.append("    Audio bitrate : ").append(audio.getBitRate()).append("\n");
      str.append("    Audio bitrate mode : ").append(audio.getBitRateMode()).append("\n");
      str.append("    Audio channel : ").append(audio.getChannel()).append("\n");
      str.append("    Audio language : ").append(audio.getLanguage()).append("\n");
    }

    res += str.toString();

    str.delete(0, str.length());
    for (MediaSubTitle subTitle : subTitles) {
      str.append("  SubTitle stream ").append(subTitle.getStream()).append("\n");
      str.append("    SubTitle title : ").append(subTitle.getTitle()).append("\n");
      str.append("    SubTitle language : ").append(subTitle.getLanguage()).append("\n");
    }

    res += str.toString();
    return res;
  }
}
