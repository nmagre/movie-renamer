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

import fr.free.movierenamer.utils.ScrapperUtils;
import fr.free.movierenamer.utils.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class MediaInfo
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class MediaInfo extends Info {

  private static final long serialVersionUID = 1L;
  protected final Map<MediaProperty, String> mediaFields;
  protected String title;
  protected Integer year;
  protected Double rating;
  protected CastingInfo[] casting;
  protected List<IdInfo> idsInfo;

  public interface InfoProperty {

    public boolean isLanguageDepends();

    public String name();
  }

  public enum InfoType {

    MOVIE,
    TVSHOW
  }

  public enum MediaProperty implements InfoProperty {

    title,
    year,
    rating;

    private final boolean languageDepends;

    private MediaProperty() {
      languageDepends = false;
    }

    private MediaProperty(final boolean languageDepends) {
      this.languageDepends = languageDepends;
    }

    @Override
    public boolean isLanguageDepends() {
      return languageDepends;
    }

  }

  protected MediaInfo(Map<MediaProperty, String> mediaFields, List<IdInfo> idsInfo) {
    this.mediaFields = (mediaFields != null) ? mediaFields : new EnumMap<MediaProperty, String>(MediaProperty.class);
    this.idsInfo = (idsInfo != null) ? idsInfo : new ArrayList<IdInfo>();
  }

  public String get(MediaProperty key) {
    return mediaFields.get(key);
  }

  public void set(MediaProperty key, String value) {
    mediaFields.put(key, value);
  }

  public void unsetUnsupportedLangInfo() {
    Iterator<Entry<MediaProperty, String>> it = mediaFields.entrySet().iterator();
    Entry<MediaProperty, String> entry;
    while (it.hasNext()) {
      entry = it.next();
      if (entry.getKey().isLanguageDepends()) {
        it.remove();
      }
    }

    unsetUnsupportedLanguageInfo();
  }

  public String getTitle() {
    return get(MediaProperty.title);
  }

  public Integer getYear() {
    try {
      return Integer.parseInt(get(MediaProperty.year));
    } catch (Exception ex) {
    }

    return null;
  }

  public Double getRating() {
    try {
      return Double.parseDouble(get(MediaProperty.rating));
    } catch (Exception ex) {
    }

    return null;
  }

  public List<CastingInfo> getCasting() {
    return casting != null ? Arrays.asList(casting) : new ArrayList<CastingInfo>();
  }

  public void setCasting(final List<CastingInfo> persons) {
    this.casting = (persons == null) ? null : persons.toArray(new CastingInfo[persons.size()]);
    setMediaCasting();
  }

  public List<IdInfo> getIdsInfo() {
    return idsInfo;
  }

  public void setIdsInfo(List<IdInfo> idsInfo) {
    this.idsInfo = idsInfo;
  }

  public String getIdString(final ScrapperUtils.AvailableApiIds idType) {

    for (IdInfo id : idsInfo) {
      if (id.getIdType().equals(idType)) {
        return id.toString();
      }
    }

    return null;
  }

  public Integer getId(final ScrapperUtils.AvailableApiIds idType) {

    for (IdInfo id : idsInfo) {
      if (id.getIdType().equals(idType)) {
        return id.getId();
      }
    }

    return null;
  }

  public abstract InfoType getInfoType();

  public abstract String getRenamedTitle(String filename, String format);

  protected abstract void setMediaCasting();

  protected abstract void unsetUnsupportedLanguageInfo();

  public abstract String getRenamedTitle(String filename, String format, StringUtils.CaseConversionType renameCase,
          String filenameSeparator, int filenameLimit, boolean reservedCharacter, boolean rmDupSpace, boolean trim);

  @Override
  public String toString() {
    return super.toString() + String.format(" %s", mediaFields);
  }

}
