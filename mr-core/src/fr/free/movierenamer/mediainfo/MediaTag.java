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

import fr.free.movierenamer.mediainfo.MediaInfo.StreamKind;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.NumberUtils;
import fr.free.movierenamer.utils.StringUtils;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Class MediaTag
 *
 * @author Nicolas Magré
 */
public class MediaTag implements Serializable {

  private static final long serialVersionUID = 1L;

  private MediaInfo mediaInfo;
  private final File mediaFile;
  private String containerFormat;
  private Long duration;
  private MediaVideo mediaVideo;
  private List<MediaAudio> mediaAudio;
  private List<MediaSubTitle> subTitles;

  public enum TagType {

    Video,
    Audio,
    Text;
  }

  public enum Tags {

    // General
    ContainerFormat(StreamKind.General, "Format_Commercial", "Format", "Codec"),
    Duration(StreamKind.General, Long.class, "Duration"),
    // Video
    VideoCodec(StreamKind.Video, true, "Encoded_Library/Name", "CodecID/Hint", "Format"),
    VideoFrameRate(StreamKind.Video, Double.class, "FrameRate"),
    VideoScanType(StreamKind.Video, "ScanType"),
    VideoFrameCount(StreamKind.Video, Long.class, "FrameCount"),
    VideoHeight(StreamKind.Video, Integer.class, "Height"),
    VideoWidth(StreamKind.Video, Integer.class, "Width"),
    VideoAspectRatio(StreamKind.Video, Float.class, "DisplayAspectRatio"),
    // Audio
    AudioStreamCount(StreamKind.Audio, Integer.class, "StreamCount"),
    AudioCodec(StreamKind.Audio, "CodecID/Hint", "Format"),
    AudioLanguage(StreamKind.Audio, "Language/String3"),
    AudioChannels(StreamKind.Audio, Integer.class, "Channel(s)"),
    AudioBitRateMode(StreamKind.Audio, "BitRate_Mode"),
    AudioBitRate(StreamKind.Audio, Integer.class, "BitRate"),
    AudioTitle(StreamKind.Audio, "Title"),
    // Text
    TextStreamCount(StreamKind.Text, Integer.class, "StreamCount"),
    TextTitle(StreamKind.Text, "Title"),
    TextLanguage(StreamKind.Text, "Language/String3");
    private final StreamKind kind;
    private final String[] keys;
    private boolean getFirst = false;
    private Class<?> clazz = String.class;

    private Tags(final StreamKind kind, final String... keys) {
      this(kind, String.class, keys);
    }

    private Tags(final StreamKind kind, final Class<?> clazz, final String... keys) {
      this.kind = kind;
      this.clazz = clazz;
      this.keys = keys;
    }

    private Tags(final StreamKind kind, final boolean getFirst, final String... keys) {
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

  /**
   * This conversion is WRONG. E.g 3 channels can be "2.1" or "3.0", ...
   */
  public enum ConvertChannels {

    _1("1.0"),
    _2("2.0"),
    _3("2.1"),
    _4("3.1"),
    // Surround
    _5("4.1"),
    _6("5.1"),
    _7("6.1"),
    _8("7.1"),
    _9("7.2");

    private final String format;

    private ConvertChannels(final String format) {
      this.format = format;
    }

    public String getFormat() {
      return format;
    }
  }

  public MediaTag(final File mediaFile) {
    this.mediaFile = mediaFile;
    mediaInfo = null;
  }

  private synchronized MediaInfo getMediaInfo() throws Exception {

    if (mediaInfo == null) {
      final MediaInfo newMediaInfo = new MediaInfo();
      if (!newMediaInfo.open(mediaFile)) {
        throw new RuntimeException("Cannot open media file: " + mediaFile);
      }

      mediaInfo = newMediaInfo;
    }

    return mediaInfo;
  }

  private Object getMediaInfo(final Tags tag) {
    return getMediaInfo(tag, 0);
  }

  private Object getMediaInfo(final Tags tag, final int streamNumber) {

    String value, nvalue;

    try {
      for (String key : tag.getKeys()) {
        value = getMediaInfo().get(tag.getStreamKind(), streamNumber, key);

        if (value.isEmpty()) {
          continue;
        }

        nvalue = new Scanner(value).next();

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
    } catch (Exception ex) {
    }

    return StringUtils.EMPTY;
  }

  public String getContainerFormat() {
    if (containerFormat != null) {
      return containerFormat;
    }

    if (!Settings.MEDIAINFO) {
      return StringUtils.EMPTY;
    }
    containerFormat = getMediaInfo(Tags.ContainerFormat).toString();
    return containerFormat;
  }

  public void setContainerFormat(final String containerFormat) {
    this.containerFormat = containerFormat;
  }

  public Long getDuration() {
    if (duration != null) {
      return duration;
    }

    long rvalue = 0L;
    if (!Settings.MEDIAINFO) {
      return rvalue;
    }

    final Object value = getMediaInfo(Tags.Duration);
    if (value instanceof Long) {
      rvalue = (Long) value;
      duration = rvalue;
    }

    return rvalue;
  }

  public void setDuration(final Long duration) {
    this.duration = duration;
  }

  public MediaVideo getMediaVideo() {
    if (mediaVideo != null) {
      return mediaVideo;
    }

    mediaVideo = new MediaVideo();

    if (!Settings.MEDIAINFO) {
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

  public void setMediaVideo(final MediaVideo mediaVideo) {
    this.mediaVideo = mediaVideo;
  }

  public List<MediaAudio> getMediaAudios() {
    if (mediaAudio != null) {
      return mediaAudio;
    }

    mediaAudio = new ArrayList<MediaAudio>();
    if (!Settings.MEDIAINFO) {
      return mediaAudio;
    }

    final Object obj = getMediaInfo(Tags.AudioStreamCount);

    if (obj.toString().isEmpty() || !(obj instanceof Integer)) {
      return mediaAudio;
    }

    final int count = (Integer) obj;

    Object intObj;
    MediaAudio maudio;
    Locale lang;

    for (int i = 0; i < count; i++) {
      maudio = new MediaAudio(i);
      maudio.setCodec(getMediaInfo(Tags.AudioCodec, i).toString());
      lang = LocaleUtils.getLanguageMap().get(getMediaInfo(Tags.AudioLanguage, i).toString());

      if (lang != null) {
        maudio.setLanguage(lang);
      }

      intObj = getMediaInfo(Tags.AudioChannels, i);
      if (intObj instanceof Integer) {
        String value = String.valueOf((Integer) intObj);
        maudio.setNbChannel(value);
        try {
          value = ConvertChannels.valueOf("_" + intObj).getFormat();
        } catch (Exception e) {
        }

        maudio.setChannel(value);
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

  public void setMediaAudio(final List<MediaAudio> mediaAudio) {
    this.mediaAudio = mediaAudio;
  }

  public List<MediaSubTitle> getMediaSubTitles() {
    if (subTitles != null) {
      return subTitles;
    }

    subTitles = new ArrayList<MediaSubTitle>();
    if (!Settings.MEDIAINFO) {
      return subTitles;
    }

    int count;
    MediaSubTitle subTitle;
    Locale lang;

    if (getMediaInfo(Tags.TextStreamCount) instanceof Integer) {
      count = (Integer) getMediaInfo(Tags.TextStreamCount);
      for (int i = 0; i < count; i++) {
        subTitle = new MediaSubTitle(i);
        lang = LocaleUtils.getLanguageMap().get(getMediaInfo(Tags.TextLanguage, i).toString());
        subTitle.setLanguage(lang);
        subTitle.setTitle(getMediaInfo(Tags.TextTitle, i).toString());
        subTitles.add(subTitle);
      }
    }

    return subTitles;
  }

  public void setMediaSubtitles(final List<MediaSubTitle> subTitles) {
    this.subTitles = subTitles;
  }

  public String getTagString(final Tags tag, final String separator, final int limit) {
    if (!Settings.MEDIAINFO) {
      return StringUtils.EMPTY;
    }

    int count = 0;
    switch (tag.getStreamKind()) {
      case Audio:
        if (getMediaInfo(Tags.AudioStreamCount) instanceof Integer) {
          count = (Integer) getMediaInfo(Tags.AudioStreamCount);
        }

        if (tag.equals(Tags.AudioStreamCount)) {
          return String.valueOf(count);
        }
        break;
      case Text:
        if (getMediaInfo(Tags.TextStreamCount) instanceof Integer) {
          count = (Integer) getMediaInfo(Tags.TextStreamCount);
        }
        if (tag.equals(Tags.TextStreamCount)) {
          return String.valueOf(count);
        }
        break;
      default:
        return getMediaInfo(tag).toString();
    }

    final StringBuilder res = new StringBuilder();
    String info;

    for (int i = 0; i < count; i++) {
      info = getMediaInfo(tag, i).toString();
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
