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
import fr.free.movierenamer.renamer.Renamer;
import fr.free.movierenamer.utils.FileUtils;
import fr.free.movierenamer.utils.ScrapperUtils;
import java.io.File;
import java.net.URI;
import java.util.Map;

/**
 * Class FileInfo
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class FileInfo extends Info {

  private static final long serialVersionUID = 1L;
  private File file;
  private final MediaType type;
  private final String search;
  private final Map<FileProperty, String> fileProperty;
  private final MediaTag mtag;

  public enum MediaType {

    MOVIE,
    TVSHOW
  }

  public enum FileProperty {

    name,
    year,
    season,
    episode,
    imdbId
  }

  public FileInfo(File file) {
    this.file = file;
    this.type = /*getMediaType(file);*/ MediaType.MOVIE;// FIXME
    fileProperty = NameMatcher.getProperty(file, type);
    search = fileProperty.get(FileProperty.name);
    this.mtag = new MediaTag(file);
  }

  public IdInfo getImdbId() {
    final String id = get(FileProperty.imdbId);
    if (id != null && id.length() > 0) {
      return new IdInfo(Integer.parseInt(id), ScrapperUtils.AvailableApiIds.IMDB);
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

  public static MediaType getMediaType(final File file) {// TODO A refaire , améliorer la detection !!!

    final TvShowEpisodeNumMatcher nmatcher = new TvShowEpisodeNumMatcher(file);
    final SxE sxe = nmatcher.matchEpisode();
    if (sxe.isValid()) {
      System.out.println(file.getName() + " : " + sxe.toXL0String());
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

  public boolean wasRenamed() {
    return Renamer.getInstance().wasRenamed(this);
  }

  public MediaType getType() {
    return type;
  }

  public MediaTag getMediaTag() {
    return mtag;
  }

  public boolean renamed(final String newName) {
    final File newFile = FileUtils.move(this.file, newName);
    final boolean success = Renamer.getInstance().addRenamed(this, this.file.toURI(), newFile.toURI());
    this.file = newFile;
    return success;
  }

  public URI getURI() {
    return this.file.toURI();
  }

  public File getFile() {
    return file;
  }
}
