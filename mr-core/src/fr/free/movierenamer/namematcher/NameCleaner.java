/*
 * mr-core
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

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.UNICODE_CASE;
import static java.util.regex.Pattern.compile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.StringUtils;

/**
 * Class NameCleaner
 * 
 * @author Simon QUÉMÉNEUR
 */
public class NameCleaner {

  private static final Pattern yearPattern = Pattern.compile("\\D?(\\d{4})\\D");
  private final Map<Boolean, Pattern[]> stoplist = new HashMap<Boolean, Pattern[]>(2);
  private final Map<Boolean, Pattern[]> cleanlist = new HashMap<Boolean, Pattern[]>(2);

  public String clean(String item, Pattern... cleanPattern) {
    for (Pattern it : cleanPattern) {
      item = it.matcher(item).replaceAll("");
    }

    return StringUtils.removePunctuation(item);
  }

  public String extractName(String item, boolean strict) {
    Pattern[] stoplist;
    Pattern[] cleanlist;

    synchronized (this.stoplist) {
      stoplist = this.stoplist.get(strict);
      cleanlist = this.cleanlist.get(strict);

      //use cache for speed
      if (stoplist == null || cleanlist == null) {
        Set<String> languages = LocaleUtils.getLanguageMap(LocaleUtils.getSupportedDisplayLocales()).keySet();
        Pattern bracket = getBracketPattern(strict);
        Pattern releaseGroup = getReleaseGroupPattern(strict);
        Pattern languageSuffix = getLanguageSuffixPattern(languages);
        Pattern languageTag = getLanguageTagPattern(languages);
        Pattern videoSource = getVideoSourcePattern();
        Pattern videoFormat = getVideoFormatPattern();
        Pattern resolution = getResolutionPattern();
        Pattern queryBlacklist = getBlacklistPattern();
        Pattern extensions = getExtensionPattern();

        stoplist = new Pattern[] {
            languageTag, videoSource, videoFormat, resolution, languageSuffix, extensions
        };
        cleanlist = new Pattern[] {
            extensions, bracket, releaseGroup, languageTag, videoSource, videoFormat, resolution, languageSuffix, queryBlacklist
        };
        this.stoplist.put(strict, stoplist);
        this.cleanlist.put(strict, cleanlist);
      }
    }

    String output = item;
    // remove year
    Integer year = extractYear(item);
    if (year != null && output.length() > 5 && year > 0) {
      // ensure the output contains something else ;)
      output = StringUtils.replaceLast(output, Integer.toString(year), "");
    }
    //
    output = strict ? clean(output, stoplist) : substringBefore(output, stoplist);
    // let's clean it
    output = clean(output, cleanlist);

    return output;
  }

  public Integer extractYear(String item) {
    Matcher matcher = yearPattern.matcher(item);
    Integer year = null;
    while (matcher.find()) {
      // assuming year is the last ;)
      String syear = matcher.group(1);
      int found = Integer.parseInt(syear);
      if (found >= 1900 && found <= Calendar.getInstance().get(Calendar.YEAR)) {
        year = found;
      }
    }
    return year;
  }

  private Pattern getBracketPattern(boolean strict) {
    // match patterns like [Action, Drama] or {ENG-XViD-MP3-DVDRiP} etc
    String contentFilter = strict ? "[\\p{Space}\\p{Punct}&&[^\\[\\]]]" : "\\p{Alpha}";
    return Pattern.compile("(?:\\[([^\\[\\]]+?" + contentFilter + "[^\\[\\]]+?)\\])|(?:\\{([^\\{\\}]+?" + contentFilter + "[^\\{\\}]+?)\\})|(?:\\(([^\\(\\)]+?" + contentFilter + "[^\\(\\)]+?)\\))");
  }

  private Pattern getReleaseGroupPattern(boolean strict) {
    // pattern matching any release group name enclosed in separators
    String pattern = getCleanerProperty("releaseGroup");
    return Pattern.compile("(?<!\\p{Alnum})(" + pattern + ")(?!\\p{Alnum})", strict ? 0 : Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
  }

  public Pattern getLanguageTagPattern(Collection<String> languages) {
    // [en]
    return compile("(?<=[-\\[{(])(" + StringUtils.join(quote(languages), "|") + ")(?=\\p{Punct})", CASE_INSENSITIVE | UNICODE_CASE);
  }

  public Pattern getLanguageSuffixPattern(Collection<String> languages) {
    // .en.srt
    return compile("(?<=[\\p{Punct}\\p{Space}])(" + StringUtils.join(quote(languages), "|") + ")(?=[._ ]*$)", CASE_INSENSITIVE | UNICODE_CASE);
  }

  private Pattern getVideoSourcePattern() {
    // pattern matching any video source name
    String pattern = getCleanerProperty("video.source");
    return Pattern.compile("(?<!\\p{Alnum})(" + pattern + ")(?!\\p{Alnum})", Pattern.CASE_INSENSITIVE);
  }

  private Pattern getVideoFormatPattern() {
    // pattern matching any video source name
    String pattern = getCleanerProperty("video.format");
    return Pattern.compile("(?<!\\p{Alnum})(" + pattern + ")(?!\\p{Alnum})", Pattern.CASE_INSENSITIVE);
  }

  private Pattern getResolutionPattern() {
    // match screen resolutions 640x480, 1280x720, etc
    return Pattern.compile("(?<!\\p{Alnum})(\\d{4}|[6-9]\\d{2})x(\\d{4}|[4-9]\\d{2})(?!\\p{Alnum})");
  }

  private Pattern getBlacklistPattern() {
    // pattern matching any blacklist word enclosed in separators
    String pattern = getCleanerProperty("blacklist");
    return Pattern.compile("(?<!\\p{Alnum})(" + pattern + ")(?!\\p{Alnum})", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
  }

  private Pattern getExtensionPattern() {
    // pattern matching any blacklist word enclosed in separators
    String pattern = getCleanerProperty("file.extension");
    return Pattern.compile("(?<!\\p{Alnum})(\\.(" + pattern + "))(?!\\p{Alnum})", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
  }

  private Collection<String> quote(Collection<String> strings) {
    List<String> patterns = new ArrayList<String>(strings.size());
    for (String it : strings) {
      patterns.add(Pattern.quote(it));
    }
    return patterns;
  }

  private String substringBefore(String item, Pattern... stopwords) {
    for (Pattern it : stopwords) {
      Matcher matcher = it.matcher(item);
      if (matcher.find()) {
        String substring = item.substring(0, matcher.start()); // use substring before the matched stopword
        if (StringUtils.removePunctuation(substring).length() >= 3) {
          item = substring; // make sure that the substring has enough data
        }
      }
    }
    return item;
  }

  private static String getCleanerProperty(String key) {
    return ResourceBundle.getBundle(NameCleaner.class.getName(), Locale.ROOT).getString(key);
  }
}
