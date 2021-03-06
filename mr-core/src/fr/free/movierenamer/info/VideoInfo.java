/*
 * movie-renamer-core
 * Copyright (C) 2014 Nicolas Magré
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
package fr.free.movierenamer.info;

import fr.free.movierenamer.mediainfo.MediaAudio;
import fr.free.movierenamer.mediainfo.MediaSubTitle;
import fr.free.movierenamer.mediainfo.MediaTag;
import fr.free.movierenamer.mediainfo.MediaVideo;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.DateFormat;
import fr.free.movierenamer.utils.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class MediaInfo
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class VideoInfo extends MediaInfo {

  protected CastingInfo[] casting;
  protected final Map<VideoProperty, String> videoInfo;

  public static enum VideoProperty implements MediaInfoProperty {

    runtime,
    releasedDate;

    private final boolean languageDepends;

    private VideoProperty() {
      languageDepends = false;
    }

    private VideoProperty(final boolean languageDepends) {
      this.languageDepends = languageDepends;
    }

    @Override
    public boolean isLanguageDepends() {
      return languageDepends;
    }
  }

  public VideoInfo(Map<MediaInfoProperty, String> info, List<IdInfo> idsInfo) {
    super(info, idsInfo);
    videoInfo = new EnumMap<>(VideoProperty.class);

    if (info == null) {
      info = new HashMap<>();
    }

    Iterator<Map.Entry<MediaInfoProperty, String>> it = info.entrySet().iterator();
    Map.Entry<MediaInfoProperty, String> entry;
    InfoProperty property;
    while (it.hasNext()) {
      entry = it.next();
      property = entry.getKey();
      if (property instanceof VideoProperty) {
        videoInfo.put((VideoProperty) property, entry.getValue());
        it.remove();
      }
    }
  }

  public List<CastingInfo> getCasting() {
    return casting != null ? Arrays.asList(casting) : new ArrayList<CastingInfo>();
  }

  public void setCasting(final List<CastingInfo> persons) {
    this.casting = (persons == null) ? null : persons.toArray(new CastingInfo[persons.size()]);
    setMediaCasting();
  }

  @Override
  public String get(MediaInfoProperty key) {
    String res = super.get(key);
    if (res != null) {
      return res;
    }
    
    if (!(key instanceof VideoProperty)) {
      return null;
    }
    return videoInfo.get((VideoProperty) key);
  }

  @Override
  public void set(MediaInfoProperty key, String value) {
    if (!(key instanceof VideoProperty)) {
      return;
    }
    videoInfo.put((VideoProperty) key, value);
  }

  public Integer getRuntime() {
    try {
      return Integer.valueOf(get(VideoProperty.runtime));
    } catch (Exception e) {
    }
    return null;
  }

  public DateFormat getReleasedDate() {
    try {
      return DateFormat.parse(get(VideoProperty.releasedDate), "yyyy-MM-dd");
    } catch (Exception e) {
    }
    return null;
  }

  @Override
  protected Map<String, Object> getFormatTokens(FileInfo fileInfo) {

    Map<String, Object> tokens = super.getFormatTokens(fileInfo);

    MediaTag mtag = fileInfo.getMediaTag();
    // Media info
    if (mtag != null && Settings.MEDIAINFO) {
      final MediaVideo video = mtag.getMediaVideo();
      final List<MediaAudio> audios = mtag.getMediaAudios();
      final List<MediaSubTitle> subTitles = mtag.getMediaSubTitles();
      // Audio
      final List<String> aChannels = new ArrayList<>();
      final List<String> aCodecs = new ArrayList<>();
      final List<String> aLanguages = new ArrayList<>();
      final List<String> aTitles = new ArrayList<>();
      final List<Integer> aBitrates = new ArrayList<>();
      final List<String> aRatemodes = new ArrayList<>();
      // Subtitle
      final List<String> sTitles = new ArrayList<>();
      final List<String> sLanguages = new ArrayList<>();

      for (MediaAudio audio : audios) {
        aChannels.add(audio.getChannel());
        aCodecs.add(audio.getCodec());
        aLanguages.add(audio.getLanguage().getLanguage());
        aTitles.add(audio.getTitle());
        aBitrates.add(audio.getBitRate());
        aRatemodes.add(audio.getBitRateMode());
      }

      for (MediaSubTitle subTitle : subTitles) {
        sTitles.add(subTitle.getTitle());
        sLanguages.add(subTitle.getLanguage() != null ? subTitle.getLanguage().getLanguage() : StringUtils.EMPTY);
      }

      // General
      tokens.put("vrt", mtag.getDuration());
      tokens.put("vcf", mtag.getContainerFormat());
//      replace.put("<mfs>", mtag.getFileSize());
      // Video
      tokens.put("vc", video.getCodec());
      tokens.put("vd", video.getVideoDefinition());
      tokens.put("vr", video.getVideoResolution());
      tokens.put("vfr", video.getFrameRate());
      tokens.put("vst", video.getScanType());
      tokens.put("vfc", video.getFrameCount());
      tokens.put("vh", video.getHeight());
      tokens.put("vw", video.getWidth());
      tokens.put("var", video.getAspectRatio());
      // Audio
      tokens.put("ach", aChannels);
      tokens.put("ac", aCodecs);
      tokens.put("al", aLanguages);
      tokens.put("att", aTitles);
      tokens.put("ab", aBitrates);
      tokens.put("abm", aRatemodes);
      // Subtitle
      tokens.put("stt", sTitles);
      tokens.put("stl", sLanguages);
    }

    return tokens;
  }

  @Override
  public String toString() {
    return super.toString() + String.format(" %s", videoInfo.toString());
  }

}
