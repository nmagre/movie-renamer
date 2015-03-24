/*
 * testMR
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

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Calendar;

/**
 * Class NumberUtils
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class NumberUtils {
  
  private static final int YEAR = Calendar.getInstance().get(Calendar.YEAR);

  /**
   * Check if string is a digit
   *
   * @param str String
   * @return True if str is a digit, false otherwise
   */
  public static boolean isDigit(String str) {
    if (str == null || str.length() == 0) {
      return false;
    }

    for (int i = 0; i < str.length(); i++) {
      if (!Character.isDigit(str.charAt(i)) && str.charAt(i) != '.') {
        return false;
      }
    }
    return true;
  }

  public static boolean isNumeric(String str) {
    NumberFormat formatter = NumberFormat.getInstance();
    ParsePosition pos = new ParsePosition(0);
    formatter.parse(str, pos);
    return str.length() == pos.getIndex();
  }

  public static boolean isNumeric(Class<?> cls) {
    if (cls != null) {
      return cls == int.class || cls == long.class || cls == float.class || cls == double.class || Number.class.isAssignableFrom(cls);
    }
    return false;
  }

  /**
   * @param <T>
   * @param cls
   * @param value
   * @return
   * @throws NumberFormatException
   */
  public static <T extends Number> T convertToNumber(Class<T> cls, String value) throws NumberFormatException {
    Number num = null;
    if (value != null && cls != null) {
      if (cls == Byte.class || cls == byte.class) {
        num = Byte.valueOf(value);
      } else if (cls == Integer.class || cls == int.class) {
        num = Integer.valueOf(value);
      } else if (cls == Long.class || cls == long.class) {
        num = Long.valueOf(value);
      } else if (cls == Float.class || cls == float.class) {
        num = Float.valueOf(value);
      } else if (cls == Double.class || cls == double.class) {
        num = Double.valueOf(value);
      }
    }
    @SuppressWarnings("unchecked")
    T toRet = (T) num;
    return toRet;
  }
  
  public static boolean isYearValid(int year) {
    return year >= 1900 && year <= YEAR;
  }

  private NumberUtils() {
    throw new UnsupportedOperationException();
  }
}
