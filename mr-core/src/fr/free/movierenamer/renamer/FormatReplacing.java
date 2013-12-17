/*
 * mr-core
 * Copyright (C) 2012-2013 Nicolas Magré
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
package fr.free.movierenamer.renamer;

import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class FormatReplacing, simple tag replace for naming scheme
 *
 * @author Nicolas Magré
 */
public class FormatReplacing {

  private static final char tokenStart = '<';
  private static final char tokenEnd = '>';
  private static final String optionSeparator = ":";
  private static final Pattern valueIndex = Pattern.compile("(\\d+)");
  private final Settings settings;
  private final Map<String, Object> replace;
  private StringBuilder tokenBuffer;
  private StringBuilder outputBuffer;
  private Integer tokenIndex;

  /**
   * Tag options
   */
  private enum Option {

    i,// ignore case
    u,// upper case
    l,// lower case
    f,// first letter upper
    w,// keep only word caracter
    d// keep only digit
  }

  /**
   * Simple tag replace for naming scheme
   *
   * @param replace Map of tags and replace values
   */
  public FormatReplacing(Map<String, Object> replace) {
    this.replace = replace;
    settings = Settings.getInstance();
  }

  /**
   * Replace tag by value
   *
   * @param format Naming scheme
   * @return Replaced string by values
   */
  public String getReplacedString(String format) {
    tokenIndex = null;
    tokenBuffer = new StringBuilder();
    outputBuffer = new StringBuilder();

    for (int i = 0; i < format.length(); i++) {
      char c = format.charAt(i);

      // Try to replace tag by its value
      if (c == tokenEnd && tokenIndex != null) {
        String tag = tokenBuffer.toString();
        List<Option> options = new ArrayList<Option>();
        // Get options
        if (tag.contains(optionSeparator)) {
          List<String> opts = StringUtils.stringToArray(tag, optionSeparator);
          tag = opts.get(0);
          opts.remove(0);
          for (String opt : opts) {
            try {
              options.add(Option.valueOf(opt));
            } catch (Exception e) {
            }
          }
        }

        String value = getValue(tag + tokenEnd, options.toArray(new Option[options.size()]));
        outputBuffer.append(value);
        tokenIndex = null;
        tokenBuffer = new StringBuilder();
        continue;
      }

      if (c == tokenStart) {
        // No token end found, so we copy token buffer to output buffer
        if (tokenIndex != null) {
          outputBuffer.append(tokenBuffer);
          tokenBuffer = new StringBuilder();
        }

        tokenBuffer.append(tokenStart);
        tokenIndex = i;
        continue;
      }

      if (tokenIndex != null) {
        tokenBuffer.append(c);
      } else {
        outputBuffer.append(c);
      }

    }
    return outputBuffer.toString();
  }

  /**
   * Get tag value
   *
   * @param token Tag
   * @param options Tag options
   * @return Value or token if tag is not found
   */
  private String getValue(String token, Option... options) {

    int index = -1;
    Matcher matcher = valueIndex.matcher(token);
    if (matcher.find()) {
      index = Integer.parseInt(matcher.group(1)) - 1;
      token = token.replaceAll("\\d+", "");
    }

    Object obj = replace.get(token);
    StringUtils.CaseConversionType renameCase = settings.getMovieFilenameCase();
    if (obj == null) {
      return StringUtils.applyCase(token, renameCase);
    }

    String value = obj.toString();
    if (obj instanceof List) {
      List<?> list = (List<?>) obj;
      value = "";
      if (index >= 0) {
        if (index < list.size()) {
          value = list.get(index).toString();
        }
      } else {
        value = StringUtils.arrayToString(list, settings.getMovieFilenameSeparator(), settings.getMovieFilenameLimit());
      }
    }

    for (Option option : options) {
      switch (option) {
        case d:
          value = value.replaceAll("\\D", "");
          break;
        case f:
          renameCase = StringUtils.CaseConversionType.FIRSTLA;
          break;
        case i:
          renameCase = StringUtils.CaseConversionType.NONE;
          break;
        case l:
          renameCase = StringUtils.CaseConversionType.LOWER;
          break;
        case u:
          renameCase = StringUtils.CaseConversionType.UPPER;
          break;
        case w:
          value = value.replaceAll("(?:\\W|\\d)", "");
          break;
      }

    }

    return StringUtils.applyCase(value, renameCase);
  }

}
