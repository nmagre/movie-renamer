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
import fr.free.movierenamer.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class MediaInfo
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class VideoInfo extends MediaInfo {

  protected MediaTag mtag;

  public VideoInfo(Map<MediaProperty, String> mediaFields, List<IdInfo> idsInfo) {
    super(mediaFields, idsInfo);
  }

  public MediaTag getMediaTag() {
    return mtag;
  }

  public void setMediaTag(final MediaTag mtag) {
    this.mtag = mtag;
  }

  @Override
  protected Map<String, Object> getReplaceMap(String fileName) {

    Map<String, Object> replace = super.getReplaceMap(fileName);

    // Media info
    if (mtag != null && mtag.libMediaInfo) {
      final MediaVideo video = mtag.getMediaVideo();
      final List<MediaAudio> audios = mtag.getMediaAudios();
      final List<MediaSubTitle> subTitles = mtag.getMediaSubTitles();
      // Audio
      final List<String> aChannels = new ArrayList<String>();
      final List<String> aCodecs = new ArrayList<String>();
      final List<String> aLanguages = new ArrayList<String>();
      final List<String> aTitles = new ArrayList<String>();
      final List<Integer> aBitrates = new ArrayList<Integer>();
      final List<String> aRatemodes = new ArrayList<String>();
      // Subtitle
      final List<String> sTitles = new ArrayList<String>();
      final List<String> sLanguages = new ArrayList<String>();

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
      replace.put("vrt", mtag.getDuration());
      replace.put("vcf", mtag.getContainerFormat());
//      replace.put("<mfs>", mtag.getFileSize());
      // Video
      replace.put("vc", video.getCodec());
      replace.put("vd", video.getVideoDefinition());
      replace.put("vr", video.getVideoResolution());
      replace.put("vfr", video.getFrameRate());
      replace.put("vst", video.getScanType());
      replace.put("vfc", video.getFrameCount());
      replace.put("vh", video.getHeight());
      replace.put("vw", video.getWidth());
      replace.put("var", video.getAspectRatio());
      // Audio
      replace.put("ach", aChannels);
      replace.put("ac", aCodecs);
      replace.put("al", aLanguages);
      replace.put("att", aTitles);
      replace.put("ab", aBitrates);
      replace.put("abm", aRatemodes);
      // Subtitle
      replace.put("stt", sTitles);
      replace.put("stl", sLanguages);
    }

    return replace;
  }

}
