/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.utils;

/**
 * Class ScrapperUtils
 *
 * @author Nicolas Magré
 */
public final class ScrapperUtils {

  public static enum ScrapperProperty {

  }

  

  public static enum AvailableApiIds {

    IMDB("tt"),
    ALLOCINE(),
    TMDB();
    private String prefix;

    private AvailableApiIds() {
      this("");
    }

    private AvailableApiIds(String prefix) {
      this.prefix = prefix;
    }
  }

  private ScrapperUtils() {
    throw new UnsupportedOperationException();
  }
}
