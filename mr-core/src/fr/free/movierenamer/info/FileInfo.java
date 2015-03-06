/*
 * mr-core
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
import fr.free.movierenamer.namematcher.NameMatcher;
import fr.free.movierenamer.namematcher.SxE;
import fr.free.movierenamer.namematcher.TvShowEpisodeNumMatcher;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.FileUtils;
import fr.free.movierenamer.utils.ScraperUtils;
import java.io.File;
import java.net.URI;
import java.util.Calendar;
import java.util.Map;

/**
 * Class FileInfo
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class FileInfo extends Info {

  private static final long serialVersionUID = 1L;
  private final File file;
  private final MediaType type;
  private final String search;
  private final Map<FileProperty, String> fileProperty;
  private final MediaTag mtag;

  public enum FileProperty {

    name,
    year,
    season,
    episode,
    imdbId,
    md5
  }

  public FileInfo(File file) {
    this.file = file;
    this.mtag = new MediaTag(file);
    this.type = getMediaType(file);
    fileProperty = NameMatcher.getProperty(file, type);
    search = fileProperty.get(FileProperty.name);
    fileProperty.put(FileProperty.md5, FileUtils.getFileChecksum(file));
  }

  public IdInfo getImdbId() {
    final String id = get(FileProperty.imdbId);
    if (id != null && id.length() > 0) {
      return new IdInfo(Integer.parseInt(id), ScraperUtils.AvailableApiIds.IMDB);
    }
    return null;
  }

  public Integer getSeason() {
    try {
      return Integer.valueOf(get(FileProperty.season));
    } catch (NumberFormatException e) {
    }
    return null;
  }

  public Integer getEpisode() {
    try {
      return Integer.valueOf(get(FileProperty.episode));
    } catch (NumberFormatException e) {
    }
    return null;
  }

  public String get(final FileProperty property) {
    return fileProperty.get(property);
  }

  private MediaType getMediaType(File file) {// TODO A refaire , améliorer la detection !!!

    final TvShowEpisodeNumMatcher nmatcher = new TvShowEpisodeNumMatcher(file);
    final SxE sxe = nmatcher.matchEpisode();
    if (sxe.isValid()) {
      int date = (sxe.getSeason() * 100) + sxe.getEpisode();
      if (date >= 1900 && date <= Calendar.getInstance().get(Calendar.YEAR)) {// It looks like a year
        if (Settings.MEDIAINFO && mtag != null) {// Check media duration
          long duration = mtag.getDuration();
          if (duration > 0L && duration > 3000) {
            return MediaType.MOVIE;
          }
        } else {
          return MediaType.MOVIE;
        }
      }

      return MediaType.TVSHOW;
    }

    return MediaType.MOVIE;
  }

  public String getSearch() {
    return search;
  }

  public Integer getYear() {
    try {
      return Integer.parseInt(fileProperty.get(FileProperty.year));
    } catch (NumberFormatException ex) {
    }
    return -1;
  }

//  public boolean wasRenamed() {
//    return Renamer.getInstance().wasRenamed(this);
//  }
  public MediaType getType() {
    return type;
  }

  public MediaTag getMediaTag() {
    return mtag;
  }

//  public void renamed(final File newFile) {
//    final boolean success = Renamer.getInstance().addRenamed(this, file.toURI(), newFile.toURI());
//  }

  public URI getURI() {
    return this.file.toURI();
  }

  public File getFile() {
    return file;
  }
}
