
package fr.free.movierenamer.namematcher;



import java.text.Collator;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.free.movierenamer.utils.Normalization;
import fr.free.movierenamer.utils.StringUtils;

class NameCleaner {
  
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
        
        stopwords = new Pattern[] { videoSource, videoFormat, resolution };
        removelist = new Pattern[] { bracket, releaseGroup, languageTag, queryBlacklist };
        
        // cache Pattern.compiled patterns for common usage
        this.stopwords.put(strict, stopwords);
        this.removelist.put(strict, removelist);
      }
    }
    
    String output = item;
    output = strict ? clean(output, stopwords) : substringBefore(output, stopwords);
    output = clean(output, removelist);
      
    return output;
  }
  
  private Map<String, Locale> getLanguageMap(Locale... supportedDisplayLocale) {
    // use maximum strength collator by default
    Collator collator = Collator.getInstance(Locale.ROOT);
    collator.setDecomposition(Collator.FULL_DECOMPOSITION);
    collator.setStrength(Collator.PRIMARY);
    
    @SuppressWarnings("unchecked")
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
    String pattern = ResourceBundle.getBundle(getClass().getName()).getString("releaseGroup");
    return Pattern.compile("(?<!\\p{Alnum})(" + pattern + ")(?!\\p{Alnum})", strict ? 0 : Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
  }
  
  private Pattern getLanguagePattern(Collection<String> languages) {
    // [en]
    return Pattern.compile("(?<=[\\p{Punct}\\p{Space}])(" + StringUtils.join(quoteAll(languages), "|") + ")(?=[\\p{Punct}\\p{Space}])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
  }
  
  private Pattern getVideoSourcePattern() {
    // pattern matching any video source name
    String pattern = ResourceBundle.getBundle(getClass().getName()).getString("video.source");
    return Pattern.compile("(?<!\\p{Alnum})(" + pattern + ")(?!\\p{Alnum})", Pattern.CASE_INSENSITIVE);
  }
  
  private Pattern getVideoFormatPattern() {
    // pattern matching any video source name
    String pattern = ResourceBundle.getBundle(getClass().getName()).getString("video.format");
    return Pattern.compile("(?<!\\p{Alnum})(" + pattern + ")(?!\\p{Alnum})", Pattern.CASE_INSENSITIVE);
  }
  
  private Pattern getResolutionPattern() {
    // match screen resolutions 640x480, 1280x720, etc
    return Pattern.compile("(?<!\\p{Alnum})(\\d{4}|[6-9]\\d{2})x(\\d{4}|[4-9]\\d{2})(?!\\p{Alnum})");
  }
  
  public Pattern getBlacklistPattern() {
    // pattern matching any blacklist word enclosed in separators
    String pattern = ResourceBundle.getBundle(getClass().getName()).getString("blacklist");
    return Pattern.compile("(?<!\\p{Alnum})(" + pattern + ")(?!\\p{Alnum})", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
  }
  
  private Collection<String> quoteAll(Collection<String> strings) {
    return strings;
//    List<String> patterns = new ArrayList<String>(strings.size());
//    for (String it : strings) {
//      patterns.add(Pattern.quote(it));
//    }
//    return patterns;
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
}
