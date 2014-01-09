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
package fr.free.movierenamer.info;

import fr.free.movierenamer.mediainfo.MediaTag;
import fr.free.movierenamer.utils.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class MediaInfo
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class MediaInfo extends Info {// FIXME CastingInfo, getActors,....

  private static final long serialVersionUID = 1L;
  protected CastingInfo[] casting;
  protected MediaTag mtag;

  public interface InfoProperty {

    public boolean isLanguageDepends();

    public String name();
  }

  public static enum InfoType {

    MOVIE,
    TVSHOW
  }

  public abstract InfoType getInfoType();

  public List<CastingInfo> getCasting() {
    return casting != null ? Arrays.asList(casting) : new ArrayList<CastingInfo>();
  }

  public List<CastingInfo> getActors() {

    List<CastingInfo> actors = new ArrayList<CastingInfo>();
    if (casting != null) {
      for (CastingInfo castingInfo : casting) {
        if (castingInfo.isActor()) {
          actors.add(castingInfo);
        }
      }
    }
    return actors;
  }

  public List<CastingInfo> getDirectors() {
    final List<CastingInfo> directors = new ArrayList<CastingInfo>();
    if (casting != null) {
      for (CastingInfo castingInfo : casting) {
        if (castingInfo.isDirector()) {
          directors.add(castingInfo);
        }
      }
    }
    return directors;
  }

  public List<CastingInfo> getWriters() {
    final List<CastingInfo> writers = new ArrayList<CastingInfo>();
    if (casting != null) {
      for (CastingInfo castingInfo : casting) {
        if (castingInfo.isWriter()) {
          writers.add(castingInfo);
        }
      }
    }
    return writers;
  }

  public MediaTag getMediaTag() {
    return mtag;
  }

  public void setMediaTag(final MediaTag mtag) {
    this.mtag = mtag;
  }

  public void setCasting(final List<CastingInfo> persons) {
    this.casting = (persons == null) ? null : persons.toArray(new CastingInfo[persons.size()]);
  }

  public abstract String getRenamedTitle(String format);

  public abstract String getRenamedTitle(String format, StringUtils.CaseConversionType renameCase, String filenameSeparator, int filenameLimit,
          boolean reservedCharacter, boolean rmDupSpace, boolean trim);
}
