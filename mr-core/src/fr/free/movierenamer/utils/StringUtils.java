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
package fr.free.movierenamer.utils;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import fr.free.movierenamer.settings.Settings;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.apache.xerces.impl.dv.util.Base64;

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

  public enum SizeFormat {

    OCTET("o"),
    BYTE("B");
    private final String format;

    private SizeFormat(String format) {
      this.format = format;
    }

    public String getFormat() {
      return format;
    }
  }
  public static final String SPACE = " ";
  public static final String ENDLINE = System.getProperty("line.separator");
  public static final String EMPTY = "";
  public static final String DOT = ".";
  public static final String EXCLA = "!";
  public static final String PIPE = "|";
  public static final String COMMA = ",";
  public static final Pattern romanSymbol = Pattern.compile("(\\s+(?:M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})|[IDCXMLV])(\\s|$))", Pattern.CASE_INSENSITIVE);
  private static final Pattern apostrophe = Pattern.compile("[`´‘’ʻ]");
  private static final Pattern punctuation = Pattern.compile("[\\p{Punct}+&&[^']]");
  private static final Pattern[] brackets = new Pattern[]{
    Pattern.compile("\\([^\\(]*\\)"), Pattern.compile("\\[[^\\[]*\\]"), Pattern.compile("\\{[^\\{]*\\}")
  };
  private static final Pattern trailingParentheses = Pattern.compile("[(]([^)]*)[)]$");
  private static final Pattern checksum = Pattern.compile("[\\(\\[]\\p{XDigit}{8}[\\]\\)]");
  public static final List<String> reservedCharacterList = Arrays.asList(new String[]{"<", ">", ":", "\"", "/", "\\", "|", "?", "*"});
  private static final char[] PASSWORD = "0KzNosgW01KlPRzGev8zPpBMrDvGIphVJ2W3t".toCharArray();
  private static final byte[] SALT = {
    (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12
  };

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
      if (ch == 's' && prevCh == '\'') {
        res.append(ch);
      } else if (toUpper && Character.isLetter(ch)) {
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
   * Apply case on string
   *
   * @param str String
   * @param renameCase Case type
   * @param isRomanUpper Keep uppercase roman number
   * @return String
   */
  public static String applyCase(String str, CaseConversionType renameCase, boolean isRomanUpper) {
    String res;
    switch (renameCase) {
      case UPPER:
        res = str.toUpperCase();
        break;
      case LOWER:
        res = str.toLowerCase();
        break;
      case FIRSTLO:
        res = StringUtils.capitalizedLetter(str, true);
        break;
      case FIRSTLA:
        res = StringUtils.capitalizedLetter(str, false);
        break;
      default:
        res = str;
        break;
    }

    if (isRomanUpper) {
      // Make sure that roman numerical are in uppercase
      final Matcher matcher = romanSymbol.matcher(res);
      final StringBuffer sb = new StringBuffer();

      while (matcher.find()) {
        matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
      }

      matcher.appendTail(sb);
      if (sb.length() > 0) {
        res = sb.toString();
      }
    }

    return res;
  }

  /**
   * Check if string is uppercase Note: this is not efficient but must work with
   * space, colon, ... characters
   *
   * @param str
   * @return True if all letter are uppercase
   */
  public static boolean isUpperCase(String str) {
    return str.equals(str.toUpperCase());
  }

  public static int nbUpperCase(String str) {
    int nb = 0;

    for (int i = 0; i < str.length(); i++) {
      if (Character.isUpperCase(str.charAt(i))) {
        nb++;
      }
    }

    return nb;
  }

  /**
   * Get an array from a string separated by separator
   *
   * @param str String
   * @param separator Separator
   * @return An array of strings
   */
  public static List<String> stringToArray(String str, String separator) {
    ArrayList<String> array = new ArrayList<>();
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

  public static String[] fromString(String string) {
    String[] strings = string.replace("[", "").replace("]", "").split(",");
    for (int i = 0; i < strings.length; i++) {
      strings[i] = strings[i].replaceAll("^\"|\"$", "");
    }
    return strings;
  }

  /**
   * Get a string from an array separated by separator and limited to limit
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
      if (limit > 0 && i == limit) {
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
   * Get a string from an array separated by separator and limited to limit
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

  public static String replaceLast(String text, String regex, String replacement) {
    return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replacement);
  }

  public static String removePunctuation(String name) {
    // remove/normalize special characters
    name = apostrophe.matcher(name).replaceAll("'");
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

  public static String normalise(String str) {
    String string = Normalizer.normalize(str, Normalizer.Form.NFD);
    return string.replaceAll("[^\\p{ASCII}]", "");
  }

  public static String normaliseClean(String str) {
    if (str == null) {
      return null;
    }
    String string = removeBrackets(str);
    string = removePunctuation(string).toLowerCase();
    string = string.replace(" et ", "&").replace(" and ", "&").replaceAll("(?:^)|(?:\\s)the\\s", "");
    return normalise(string);
  }

  public static String generateRandomString(int length) {
    String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    int charLength = chars.length();
    StringBuilder pass = new StringBuilder(charLength);
    for (int x = 0; x < length; x++) {
      int i = (int) (Math.random() * charLength);
      pass.append(chars.charAt(i));
    }
    return pass.toString();
  }

  public static byte[] getSha1(String str) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA1");
      md.update(str.getBytes());
      return md.digest();
    } catch (NoSuchAlgorithmException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }
    return null;
  }

  public static String bytesToHex(byte[] b) {
    char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
      '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    StringBuilder buf = new StringBuilder();
    for (int j = 0; j < b.length; j++) {
      buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
      buf.append(hexDigit[b[j] & 0x0f]);
    }
    return buf.toString();
  }

  public static String humanReadableByteCount(long bytes, boolean useOctet) {
    boolean si = true;
    int unit = si ? 1000 : 1024;
    if (bytes < unit) {
      return bytes + " B";
    }

    int exp = (int) (Math.log(bytes) / Math.log(unit));
    String pre = "KMGTPE".charAt(exp - 1) + (si ? "" : "i");
    String format = SizeFormat.BYTE.getFormat();
    if (useOctet) {
      format = SizeFormat.OCTET.getFormat();
    }
    return String.format("%.1f %s" + format, bytes / Math.pow(unit, exp), pre);
  }

  public static String humanReadableDate(long date) {// in ms
    return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(date);
  }

  public static String humanReadableTime(long time) {// in ms
    Settings settings = Settings.getInstance();
    return humanReadableTime(time, settings.getStringTimeHour(), settings.getStringTimeMinute(), settings.getStringTimeSeconde(),
      settings.getStringTimeMilliSeconde(), settings.isStringTimeShowSeconde(), settings.isStringTimeShowMillis());
  }

  public static String humanReadableTime(long time, String hour, String minute, String seconde, String milli, boolean showSeconde, boolean showMilli) {// in ms
    String format = "HH";
    format = appendStringToTime(format, hour);
    format += "mm";
    format = appendStringToTime(format, minute);

    if (showSeconde) {
      format += "ss";
      format = appendStringToTime(format, seconde);
    }

    if (showMilli) {
      format += "SS";
      format = appendStringToTime(format, milli);
    }

    format = format.trim();
    SimpleDateFormat df = new SimpleDateFormat(format);
    df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
    return df.format(new Date(time));
  }

  private static String appendStringToTime(String format, String append) {
    if (!append.isEmpty()) {
      format += "'" + append + "'";
    }
    return format;
  }

  public static String durationInMinute(String duration) {// in second
    return durationInMinute(Integer.parseInt(duration));
  }

  public static String durationInMinute(int duration) {// in second
    return String.format("%02d:%02d", (duration % 3600) / 60, (duration % 60));
  }

  public static String durationMsInMinute(long time) {// in ms
    return durationInMinute((int) time / 1000);
  }

  public static String encrypt(byte[] property) {
    String encrypted = "";
    try {
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
      SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
      Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
      pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));

      encrypted = Base64.encode(pbeCipher.doFinal(property));

      for (int i = 0; i < property.length; i++) {
        property[i] = 0;
      }
      property = null;
      System.gc();

    } catch (GeneralSecurityException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    return encrypted;
  }

  public static String decrypt(String property) {
    if (property == null || property.length() == 0) {
      return "";
    }

    try {
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
      SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
      Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
      pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
      return new String(pbeCipher.doFinal(Base64.decode(property)));
    } catch (GeneralSecurityException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    return "";
  }

  public static String substringBetween(String str, String open, String close) {

    int start = str.indexOf(open);
    if (start == -1) {
      return str;
    }

    int end = str.indexOf(close, start + open.length());
    if (end == -1) {
      return str.substring(start);
    }

    return str.substring(start + open.length(), end);
  }

  public static int countMatches(String str, String search) {
    int count = 0;
    int idx = 0;

    while ((idx = str.indexOf(search, idx)) != -1) {
      idx++;
      count++;
    }

    return count;
  }

  public static boolean containsIgnoreCase(String str, String search) {

    if (search.isEmpty()) {
      return true;
    }

    if (str.equals(search)) {
      return true;
    }

    final char firstLo = Character.toLowerCase(search.charAt(0));
    final char firstUp = Character.toUpperCase(search.charAt(0));

    int length = str.length();
    int slength = search.length();
    for (int i = length - slength; i >= 0; i--) {
      final char ch = str.charAt(i);
      if (ch != firstLo && ch != firstUp) {
        continue;
      }

      if (str.regionMatches(true, i, search, 0, slength)) {
        return true;
      }
    }

    return false;
  }

  private StringUtils() {
    throw new UnsupportedOperationException();
  }
}
