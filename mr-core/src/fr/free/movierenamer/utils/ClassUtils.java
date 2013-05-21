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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class ClassUtils
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class ClassUtils {

  /**
   * list Classes inside a given package
   */
  public static List<Class<?>> getClasses(Package pckg) throws ClassNotFoundException {
    List<Class<?>> classes = new ArrayList<Class<?>>();
    // Get a File object for the package
    File directory = null;
    try {
      directory = new File(Thread.currentThread().getContextClassLoader().getResource(pckg.getName().replace('.', '/')).getFile());
    } catch (NullPointerException x) {
      throw new ClassNotFoundException(pckg + " does not appear to be a valid package");
    }
    if (directory.exists()) {
      // Get the list of the files contained in the package
      String[] files = directory.list();
      for (int i = 0; i < files.length; i++) {
        // we are only interested in .class files
        if (files[i].endsWith(".class")) {
          // removes the .class extension
          classes.add(Class.forName(pckg.getName() + '.' + files[i].substring(0, files[i].length() - 6)));
        }
      }
    } else {
      throw new ClassNotFoundException(pckg + " does not appear to be a valid package");
    }
    return classes;
  }

  /**
   * Get stack trace message to string
   *
   * @param ex
   * @return String with stack trace
   */
  public static String getStackTrace(Exception ex) {
    Throwable throwable = ex.getCause();
    String exception = throwable != null ? throwable.getClass().getSimpleName() : ex.getClass().toString();
    StackTraceElement[] ste = throwable != null ? throwable.getStackTrace() : ex.getStackTrace();
    return getStackTrace(exception, ste);
  }

  /**
   * Get stack trace message to string
   *
   * @param exception String
   * @param ste Stack trace
   * @return String with stack trace
   */
  private static String getStackTrace(String exception, StackTraceElement[] ste) {
    StringBuilder res = new StringBuilder(exception + "\n");
    for (int i = 0; i < ste.length; i++) {
      res.append("    ").append(ste[i].toString()).append("\n");
    }
    return res.toString();
  }

  private ClassUtils() {
    throw new UnsupportedOperationException();
  }
}
