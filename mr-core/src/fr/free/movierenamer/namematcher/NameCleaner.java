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

import java.text.Collator;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.free.movierenamer.utils.Normalization;
import fr.free.movierenamer.utils.StringUtils;

/**
 * Class NameCleaner
 * 
 * @author Simon QUÉMÉNEUR
 */
public class NameCleaner {

  private static final Pattern yearPattern = Pattern.compile("\\D?(\\d{4})\\D");

  // cached patterns
  private final Map<Boolean, Pattern[]> stopwords = new HashMap<Boolean, Pattern[]>(2);
  private final Map<Boolean, Pattern[]> removelist = new HashMap<Boolean, Pattern[]>(2);

  public String clean(String item, Pattern... blacklisted) {
    for (Pattern it : blacklisted) {
      item = it.matcher(item).replaceAll("");
    }

    return Normalization.normalizePunctuation(item);
  }

  public String extractName(String item, boolean strict) {
    Pattern[] stopwords;
    Pattern[] removelist;

    // initialize cached patterns
    synchronized (this.stopwords) {
      stopwords = this.stopwords.get(strict);
      removelist = this.removelist.get(strict);

      if (stopwords == null || removelist == null) {
        Set<String> languages = getLanguageMap(Locale.ENGLISH, Locale.getDefault()).keySet();
        Pattern bracket = getBracketPattern(strict);
        Pattern releaseGroup = getReleaseGroupPattern(strict);
        Pattern languageTag = getLanguagePattern(languages);
        Pattern videoSource = getVideoSourcePattern();
        Pattern videoFormat = getVideoFormatPattern();
        Pattern resolution = getResolutionPattern();
        Pattern queryBlacklist = getBlacklistPattern();
        Pattern extensions = getExtensionPattern();

        stopwords = new Pattern[] {
            videoSource, videoFormat, resolution, extensions
        };
        removelist = new Pattern[] {
            extensions, languageTag, bracket, releaseGroup, queryBlacklist
        };

        // cache Pattern.compiled patterns for common usage
        this.stopwords.put(strict, stopwords);
        this.removelist.put(strict, removelist);
      }
    }

    String output = item;
    int year = extractYear(item);
    if (year > 0) {
      output = output.replaceAll(Integer.toString(year), "");
    }
    output = strict ? clean(output, stopwords) : substringBefore(output, stopwords);
    output = clean(output, removelist);

    return output;
  }

  public int extractYear(String item) {
    Matcher matcher = yearPattern.matcher(item);
    int year = -1;
    while (matcher.find()) {
      String syear = matcher.group(1);
      int found = Integer.parseInt(syear);
      if (found >= 1900 && found <= Calendar.getInstance().get(Calendar.YEAR)) {
        year = found;
      }
    }
    return year;
  }

  private Map<String, Locale> getLanguageMap(Locale... supportedDisplayLocale) {
    Collator collator = Collator.getInstance(Locale.ROOT);
    collator.setDecomposition(Collator.FULL_DECOMPOSITION);
    collator.setStrength(Collator.PRIMARY);

    @SuppressWarnings({
        "unchecked", "rawtypes"
    })
    Comparator<String> order = (Comparator) collator;
    Map<String, Locale> languageMap = new TreeMap<String, Locale>(order);

    for (String code : Locale.getISOLanguages()) {
      Locale locale = new Locale(code);
      languageMap.put(locale.getLanguage(), locale);
      languageMap.put(locale.getISO3Language(), locale);

      // map display language names for given locales
      for (Locale language : new HashSet<Locale>(Arrays.asList(supportedDisplayLocale))) {
        // make sure language name is properly normalized so accents and whatever don't break the regex pattern syntax
        String languageName = Normalizer.normalize(locale.getDisplayLanguage(language), Form.NFKD);
        languageMap.put(languageName, locale);
      }
    }

    // remove illegal tokens
    languageMap.remove("");
    languageMap.remove("II");
    languageMap.remove("III");

    Map<String, Locale> result = Collections.unmodifiableMap(languageMap);
    return result;
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

  private Pattern getLanguagePattern(Collection<String> languages) {
    // [en]
    return compile("(?<=[-\\[{(\\.])(" + StringUtils.join(quote(languages), "|") + ")(?=\\p{Punct})", CASE_INSENSITIVE | UNICODE_CASE);
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
        if (Normalization.normalizePunctuation(substring).length() >= 3) {
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
