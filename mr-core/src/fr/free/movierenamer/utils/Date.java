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

import fr.free.movierenamer.settings.Settings;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.Level;

/**
 * Class Date
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class Date implements Serializable {

  private static final long serialVersionUID = 1L;
  private int year;
  private int month;
  private int day;


  protected Date() {
    // used by serializer
  }


  public Date(int year, int month, int day) {
    this.year = year;
    this.month = month;
    this.day = day;
  }


  public int getYear() {
    return year;
  }


  public int getMonth() {
    return month;
  }


  public int getDay() {
    return day;
  }


  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Date) {
      Date other = (Date) obj;
      return year == other.year && month == other.month && day == other.day;
    }

    return super.equals(obj);
  }


  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] { year, month, day });
  }


  @Override
  public String toString() {
    return String.format("%04d-%02d-%02d", year, month, day);
  }


  public String format(String pattern) {
    return format(pattern, Locale.ROOT);
  }


  public String format(String pattern, Locale locale) {
    return new SimpleDateFormat(pattern, locale).format(new GregorianCalendar(year, month - 1, day).getTime()); // Calendar months start at 0
  }


  public static Date parse(String string, String pattern) {
    return parse(string, pattern, Locale.ROOT);
  }


  public static Date parse(String string, String pattern, Locale locale) {
    if (string == null || string.isEmpty())
      return null;

    SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
    formatter.setLenient(false); // enable strict mode (e.g. fail on invalid dates like 0000-00-00)

    try {
      Calendar date = new GregorianCalendar(locale);
      date.setTime(formatter.parse(string));
      return new Date(date.get(YEAR), date.get(MONTH) + 1, date.get(DAY_OF_MONTH)); // Calendar months start at 0
    } catch (ParseException e) {
      // no result if date is invalid
      Settings.LOGGER.log(Level.WARNING, e.getMessage());
      return null;
    }
  }

}