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

import fr.free.movierenamer.renamer.FormatReplacing;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.FileUtils;
import fr.free.movierenamer.utils.ScraperUtils;
import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.StringUtils.CaseConversionType;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
  protected List<IdInfo> idsInfo;
  
  public interface InfoProperty {
    
    public boolean isLanguageDepends();
    
    public String name();
  }
  
  public interface MultipleInfoProperty extends InfoProperty {
    
  }
  
  public enum MediaProperty implements InfoProperty {
    
    title,
    originalTitle,
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
  
  public String getOriginalTitle() {
    return get(MediaProperty.originalTitle);
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
  
  public List<IdInfo> getIdsInfo() {
    return idsInfo;
  }
  
  public void setIdsInfo(List<IdInfo> idsInfo) {
    this.idsInfo = idsInfo;
  }
  
  public String getIdString(final ScraperUtils.AvailableApiIds idType) {
    
    for (IdInfo id : idsInfo) {
      if (id.getIdType().equals(idType)) {
        return id.toString();
      }
    }
    
    return null;
  }
  
  public Integer getId(final ScraperUtils.AvailableApiIds idType) {
    
    for (IdInfo id : idsInfo) {
      if (id.getIdType().equals(idType)) {
        return id.getId();
      }
    }
    
    return null;
  }
  
  public abstract MediaType getMediaType();
  
  protected abstract void addFormatTokens(Map<String, Object> tokens);
  
  protected abstract void setMediaCasting();
  
  protected abstract void unsetUnsupportedLanguageInfo();
  
  protected Map<String, Object> getFormatTokens(FileInfo fileInfo) {
    Map<String, Object> tokens = new HashMap<>();
    
    String titlePrefix = "";
    String shortTitle = this.getTitle();
    
    Pattern pattern;
    Matcher matcher;
    
    if (shortTitle != null) {
      pattern = Pattern.compile("^((le|la|les|the)\\s|(l\\'))(.*)", Pattern.CASE_INSENSITIVE);
      matcher = pattern.matcher(shortTitle);
      if (matcher.find() && matcher.groupCount() >= 2) {
        titlePrefix = matcher.group(1).trim();
        shortTitle = matcher.group(matcher.groupCount()).trim();
      }
    }
    
    tokens.put("fn", FileUtils.getNameWithoutExtension(fileInfo.getFile().getName()));
    tokens.put("t", this.getTitle());
    tokens.put("tp", titlePrefix);
    tokens.put("st", shortTitle);
    tokens.put("time", StringUtils.humanReadableTime(System.currentTimeMillis()));
//    tokens.put("date", StringUtils.humanReadableTime(System.currentTimeMillis()));
//    tokens.put("datetime", StringUtils.humanReadableTime(System.currentTimeMillis()));
    
    addFormatTokens(tokens);
    return tokens;
  }
  
  public final String getRenamedTitle(FileInfo fileInfo, String format) {
    final Settings settings = Settings.getInstance();
    return getRenamedTitle(fileInfo, format, settings.getMovieFilenameCase(), settings.getMovieFilenameSeparator(), settings.getMovieFilenameLimit(),
            settings.isReservedCharacter(), settings.isFilenameRmDupSpace(), settings.isFilenameTrim());// FIXME movie option
  }
  
  public final String getRenamedTitle(FileInfo fileInfo, String format, CaseConversionType caseType,
          String separator, int limit, boolean reservedCharacter, boolean rmDupSpace, boolean trim) {
    
    final FormatReplacing formatReplace = new FormatReplacing(getFormatTokens(fileInfo));
    String res = formatReplace.getReplacedString(format, caseType, separator, limit);
    
    if (reservedCharacter) {
      for (String c : StringUtils.reservedCharacterList) {
        if (!c.equals(File.separator)) {
          if (":".equals(c) && Settings.WINDOWS) {
            // Replace all colon except for hard drive if there are at the beginning of the string
            // E.g: D:\.... -> not replaced
            // E.g: file D:\... -> replaced
            res = res.replaceAll("(?<!(^\\p{Lu}))" + c + "(?<!(\\\\))", "");
          } else {
            res = res.replace(c, "");
          }
        }
      }
    }
    
    Map<String, Object> mp = getFormatTokens(fileInfo);
    for(Entry<String, Object> entry : mp.entrySet()) {
      System.out.println("* <" + entry.getKey() + "> : " + entry.getValue());
    }
    
    if (rmDupSpace) {
      res = res.replaceAll("\\s+", " ");
    }
    
    if (trim) {
      res = res.trim();
    }
    
    return res;
  }
  
  @Override
  public String toString() {
    return super.toString() + String.format(" %s", mediaFields);
  }
  
}
