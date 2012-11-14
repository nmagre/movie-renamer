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
package fr.free.movierenamer.utils;

import static java.util.Arrays.asList;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import fr.free.movierenamer.settings.Settings;

/**
 * Class StringUtils
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class StringUtils {
  public enum CaseConversionType {
    FIRSTLO,
    FIRSTLA,
    UPPER,
    LOWER,
    NONE
  }

  public static final String SPACE = " ";
  public static final String ENDLINE = System.getProperty("line.separator");
  public static final String EMPTY = "";
  public static final String DOT = ".";
  
  private static final Pattern apostrophe = Pattern.compile("['`´‘’ʻ]");
  private static final Pattern punctuation = Pattern.compile("[\\p{Punct}+&&[^:]]");

  private static final Pattern[] brackets = new Pattern[] {
    Pattern.compile("\\([^\\(]*\\)"), Pattern.compile("\\[[^\\[]*\\]"), Pattern.compile("\\{[^\\{]*\\}")
  };
  
  private static final Pattern trailingParentheses = Pattern.compile("[(]([^)]*)[)]$");

  private static final Pattern checksum = Pattern.compile("[\\(\\[]\\p{XDigit}{8}[\\]\\)]");

  public static boolean isEmptyValue(Object object) {
    return object == null || object.toString().length() == 0;
  }

  public static String joinBy(CharSequence delimiter, Object... values) {
    return join(asList(values), delimiter);
  }

  public static String join(Object[] values, CharSequence delimiter) {
    return join(asList(values), delimiter);
  }

  public static String join(Iterable<?> values, CharSequence delimiter) {
    StringBuilder sb = new StringBuilder();

    for (Iterator<?> iterator = values.iterator(); iterator.hasNext();) {
      Object value = iterator.next();
      if (!isEmptyValue(value)) {
        if (sb.length() > 0) {
          sb.append(delimiter);
        }

        sb.append(value);
      }
    }

    return sb.toString();
  }

  /**
   * Rotate string by 13 places
   * 
   * @param text String
   * @return String rotate
   */
  public static String rot13(String text) {
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if ((c >= 'a' && c <= 'm') || (c >= 'A' && c <= 'M')) {
        c += 13;
      } else if ((c >= 'n' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
        c -= 13;
      }
      res.append(c);
    }
    return res.toString();
  }

  /**
   * Capitalized first letter for each words or only first one
   * 
   * @param str String
   * @param onlyFirst Only first word letter capitalized
   * @return String capitalized
   */
  public static String capitalizedLetter(String str, boolean onlyFirst) {
    StringBuilder res = new StringBuilder();
    char ch, prevCh;
    boolean toUpper = true;
    prevCh = '.';
    str = str.toLowerCase();
    for (int i = 0; i < str.length(); i++) {
      ch = str.charAt(i);
      if (toUpper && Character.isLetter(ch)) {
        if (!Character.isLetter(prevCh) || (prevCh == 'i' && ch == 'i')) {
          res.append(Character.toUpperCase(ch));
          if (onlyFirst) {
            toUpper = false;
          }
        } else {
          res.append(ch);
        }
      } else {
        res.append(ch);
      }

      prevCh = ch;
    }
    return res.toString();
  }

  /**
   * Check if string is uppercase
   * 
   * @param str
   * @return True if all letter are uppercase except I,II,III,..., false otherwise
   */
  public static boolean isUpperCase(String str) {
    String[] romanNumber = new String[] { "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X" };
    for (String number : romanNumber) {
      if (str.equals(number)) {
        return false;
      }
    }
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      if (ch < 32 || ch > 96) {
        return false;
      }
    }
    return true;
  }

  /**
   * Get an array from a string separated by movieFilenameSeparator
   * 
   * @param str String
   * @param separator Separator
   * @return An array of strings
   */
  public static List<String> stringToArray(String str, String separator) {
    ArrayList<String> array = new ArrayList<String>();
    if (str == null) {
      return array;
    }
    if (separator == null) {
      separator = ", ";
    }
    String[] res = str.split(separator);
    array.addAll(Arrays.asList(res));
    return array;
  }

  /**
   * Get a string from an array separated by movieFilenameSeparator and limited to movieFilenameLimit
   * 
   * @param array Object array
   * @param separator Separator
   * @param limit Limit
   * @return String separated by movieFilenameSeparator or empty
   */
  public static String arrayToString(Object[] array, String separator, int limit) {
    StringBuilder res = new StringBuilder();

    if (array.length == 0) {
      return res.toString();
    }

    for (int i = 0; i < array.length; i++) {
      if (limit != 0 && i == limit) {
        break;
      }

      res.append(array[i].toString());

      if ((i + 1) != limit) {
        res.append((i < (array.length - 1)) ? separator : StringUtils.EMPTY);
      }
    }
    return res.toString();
  }

  /**
   * Get a string from an array separated by movieFilenameSeparator and limited to movieFilenameLimit
   * 
   * @param array ArrayList
   * @param separator Separator
   * @param limit Limit
   * @return String separated by movieFilenameSeparator or empty
   */
  public static String arrayToString(List<?> array, String separator, int limit) {
    Object[] newArray = new Object[array.size()];
    for (int i = 0; i < array.size(); i++) {
      newArray[i] = array.get(i);
    }
    // Object[] newArray = array.toArray();
    return arrayToString(newArray, separator, limit);
  }

  /**
   * Escape XML special character
   * 
   * @param str String to escape
   * @return String escaped
   */
  public static String escapeXML(String str) {
    if (str == null) {
      return null;
    }

    StringBuilder stringBuffer = new StringBuilder();
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      boolean needEscape = (ch == '<' || ch == '&' || ch == '>');

      if (needEscape || (ch < 32) || (ch > 136)) {
        stringBuffer.append("&#").append((int) ch).append(";");
      } else {
        stringBuffer.append(ch);
      }
    }
    return stringBuffer.toString();
  }

  /**
   * Unescape XML special character
   * 
   * @param str String
   * @param encode Encode type
   * @return Unescape string
   */
  public static String unEscapeXML(String str, String encode) {
    if (str == null) {
      return StringUtils.EMPTY;
    }

    try {
      str = str.replace("+", "%2B");// Fixed "+" charater with url decoder
      str = str.replaceAll("&#x(\\w\\w);", "%$1");
      str = URLDecoder.decode(str.replaceAll("% ", "%25 "), encode);
    } catch (UnsupportedEncodingException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }
    return str;
  }
  
  public static String replaceLast(String text, String regex, String replacement) {
    return text.replaceFirst("(?s)"+regex+"(?!.*?"+regex+")", replacement);
}

  public static String removePunctuation(String name) {
    // remove/normalize special characters
    name = apostrophe.matcher(name).replaceAll(" ");
    name = punctuation.matcher(name).replaceAll(" ");
    name = name.replaceAll("\\p{Space}+", " ");// Remove duplicate space
    return name.trim();
  }

  public static String removeBrackets(String name) {
    // remove group names and checksums, any [...] or (...)
    for (Pattern it : brackets) {
      name = it.matcher(name).replaceAll(" ");
    }
    return name;
  }

  public static String removeEmbeddedChecksum(String string) {
    // match embedded checksum and surrounding brackets
    return checksum.matcher(string).replaceAll("");
  }

  public static String removeTrailingBrackets(String name) {
    // remove trailing braces, e.g. Doctor Who (2005) -> Doctor Who
    return trailingParentheses.matcher(name).replaceAll("").trim();
  }

  private StringUtils() {
    throw new UnsupportedOperationException();
  }
}
