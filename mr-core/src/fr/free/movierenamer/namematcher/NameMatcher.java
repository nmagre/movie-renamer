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
package fr.free.movierenamer.namematcher;

import fr.free.movierenamer.info.FileInfo.FileProperty;
import fr.free.movierenamer.info.FileInfo.MediaType;
import fr.free.movierenamer.renamer.NameCleaner;
import fr.free.movierenamer.utils.FileUtils;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class NameMatcher
 *
 * @author Nicolas Magré
 */
public abstract class NameMatcher {// TODO

  private static List<String> fileExts = Arrays.asList(new String[]{"nfo", "xml"});

  public static Map<FileProperty, String> getProperty(File file, MediaType type) {
    Map<FileProperty, String> properties = new EnumMap<FileProperty, String>(FileProperty.class);
    switch (type) {
      case MOVIE:
        properties = getMovieProperty(file);
        break;
      case TVSHOW:
        properties = getTvShowProperty(file);
        break;
    }

    properties.put(FileProperty.year, "" + NameCleaner.extractYear(file.getName()));
    return properties;
  }

  private static Map<FileProperty, String> getMovieProperty(File file) {
    Map<FileProperty, String> properties = new EnumMap<FileProperty, String>(FileProperty.class);
    properties.put(FileProperty.name, NameCleaner.extractName(file.getName(), false));
    Integer imdbId = getImdbId(file);
    if (imdbId != null) {
      properties.put(FileProperty.imdbId, "" + imdbId);
    }

    return properties;
  }

  public static String extractName(String fileName) {
    String extractedName = NameCleaner.extractName(fileName, false);
    // Try to extract name from String without spacing
    String[] names = extractedName.split(" ");
    if (names.length < 3 && names[0].length() > 15) {
      extractedName = names[0].replaceAll("(\\p{Lower})(\\p{Upper})", "$1 $2");
      extractedName = NameCleaner.extractName(extractedName, false);
    }
    return extractedName;
  }

  private static Map<FileProperty, String> getTvShowProperty(File file) {// TODO
    Map<FileProperty, String> properties = new EnumMap<FileProperty, String>(FileProperty.class);

    return properties;
  }

  private static Integer getImdbId(File file) {
    final String fileName = FileUtils.getNameWithoutExtension(file.getName());
    Pattern imdbIdPattern = Pattern.compile("tt(\\d{7})");
    Matcher imdbIdMatch = imdbIdPattern.matcher(fileName);

    if (imdbIdMatch.find()) {
      return Integer.parseInt(imdbIdMatch.group(1));
    }

    List<File> nfoFiles = Arrays.asList(file.getParentFile().listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        if (fileName.equalsIgnoreCase(FileUtils.getNameWithoutExtension(name)) || dir.getName().equalsIgnoreCase(name)) {
          String fext = FileUtils.getExtension(name);
          if (fext != null && fileExts.contains(fext)) {
            return true;
          }
        }

        return false;
      }
    }));

    for (File nfile : nfoFiles) {
      try {
        String str = new Scanner(nfile).useDelimiter("\\A").next();
        imdbIdMatch = imdbIdPattern.matcher(str);
        if (imdbIdMatch.find()) {
          return Integer.parseInt(imdbIdMatch.group(1));
        }
      } catch (Exception ex) {
        //
      }
    }
    return null;
  }
}
