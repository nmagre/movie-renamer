/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.scrapper.impl.movie;

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.MovieInfo.MotionPictureRating;
import fr.free.movierenamer.utils.LocaleUtils;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Class SensacineScrapper
 * @author Nicolas Magré
 */
public class SensacineScrapper extends AlloGroupScrapper {

  private static final String host = "www.sensacine.com";
  private static final String imageHost = "images.allocine.fr";
  private static final String name = "Sensacine";
  private static final String search = "busqueda";
  private static final Pattern allocineID = Pattern.compile(".*pelicula-(\\d+).*");
  private static final Pattern allocinePersonID = Pattern.compile(".*actor-(\\d+).*");

  private enum Tag {

    Estreno(InfoTag.Date_de_sortie),
    Director(InfoTag.Réalisé_par),
    Género(InfoTag.Genre),
    País(InfoTag.Nationalité),
    Usuarios(InfoTag.Spectateurs),
    Título_original(InfoTag.Titre_original),
    Presupuesto(InfoTag.Budget),
    Distribuidora(InfoTag.Distributeur);
    private InfoTag infotag;

    private Tag(InfoTag infotag) {
      this.infotag = infotag;
    }

    public InfoTag getInfoTag() {
      return infotag;
    }
  }

  public SensacineScrapper() {
    super(LocaleUtils.AvailableLanguages.es);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  protected String getHost() {
    return host;
  }

  @Override
  protected Locale getDefaultLanguage() {
    return LocaleUtils.AvailableLanguages.es.getLocale();
  }

  @Override
  protected String getSearchString() {
    return search;
  }

  @Override
  protected String getMoviePageString(IdInfo id) {
    return "/peliculas/pelicula-" + id;
  }

  @Override
  protected String getCastingPageString(IdInfo id) {
    return getMoviePageString(id) + "/reparto/";
  }

  @Override
  protected Pattern getIdPattern() {
    return allocineID;
  }

  @Override
  protected Pattern getPersonIdPattern() {
    return allocinePersonID;
  }

  @Override
  protected String getImageHost() {
    return imageHost;
  }

  @Override
  protected MotionPictureRating getRatingScale() {
    return MotionPictureRating.SPAIN;
  }

  @Override
  protected InfoTag getInfoTag(String str) {

    try {
      return Tag.valueOf(str).getInfoTag();
    } catch (Exception ex) {
    }
    return InfoTag.unknown;
  }
}