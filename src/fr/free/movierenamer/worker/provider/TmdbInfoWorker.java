/*
 * Movie Renamer
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
package fr.free.movierenamer.worker.provider;

import fr.free.movierenamer.worker.MovieInfoWorker;

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.media.movie.MovieImage;
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.parser.xml.MrParser;
import fr.free.movierenamer.parser.xml.TmdbImage;
import fr.free.movierenamer.parser.xml.TmdbInfo;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.xml.bind.DatatypeConverter;

/**
 * Class TmdbInfoWorker, get movie info from the movie database
 * 
 * @author Nicolas Magré
 */
public class TmdbInfoWorker extends MovieInfoWorker {

  /**
   * Constructor arguments
   *
   * @param errorSupport
   * @param id
   * @param setting Movie Renamer settings
   * @throws ActionNotValidException
   */
  public TmdbInfoWorker(SwingPropertyChangeSupport errorSupport, MediaID id) throws ActionNotValidException {
    super(errorSupport, id);
    if (id.getType() != MediaID.TMDBID) {
      throw new ActionNotValidException("TmdbInfoWorker  can only use tmdb ID");
    }
  }

  @Override
  protected String getSearchUri() throws Exception {
    String uri = Settings.tmdbAPIMovieInf + new String(DatatypeConverter.parseBase64Binary(Settings.xurlMdb)) + "/" + id.getID();
    if (config.movieScrapperFR) {
      uri = uri.replace("/en/", "/fr/");
    }
    return uri;
  }

  @Override
  protected MrParser<MovieInfo> getInfoParser() throws Exception {
    return new TmdbInfo();
  }

  @Override
  protected MrParser<MovieImage> getImageParser() throws Exception {
    return new TmdbImage();
  }

  // /*
  // * (non-Javadoc)
  // *
  // * @see fr.free.movierenamer.worker.MovieInfoWorker#getImageWorker()
  // */
  // @Override
  // protected MediaImageWorker getImageWorker() throws Exception {
  // return new TmdbImageWorker(getErrorSupport(), id);
  // }

  // @Override
  // protected MovieInfo executeInBackground() {
  // MovieInfo movieInfo = null;
  // MovieImage movieImage = null;
  // try {
  // String uri = config.tmdbAPIMovieInf + new String(DatatypeConverter.parseBase64Binary(config.xurlMdb)) + "/" + id.getID();
  // if (config.movieScrapperFR) {
  // uri = uri.replace("/en/", "/fr/");
  // }
  //
  // URL url = new URL(uri);
  // File xmlFile = Cache.getInstance().get(url, Cache.CacheType.XML);
  // if (xmlFile == null) {
  // for (int i = 0; i < RETRY; i++) {
  // InputStream in;
  // try {
  // in = url.openStream();
  // Cache.getInstance().add(in, url.toString(), Cache.CacheType.XML);
  // xmlFile = Cache.getInstance().get(url, Cache.CacheType.XML);
  // break;
  // } catch (Exception e) {//Don't care about exception, "xmlFile" will be null
  // Settings.LOGGER.log(Level.SEVERE, null, e);
  // try {
  // Thread.sleep(300);
  // } catch (InterruptedException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // }
  // }
  // }
  // }
  //
  // if (xmlFile == null) {
  // firePropertyChange("closeLoadingDial", "httpFailed");
  // return null;
  // }
  //
  // //Parse TMDB API XML
  // XMLParser<MovieInfo> xmp = new XMLParser<MovieInfo>(xmlFile.getAbsolutePath());
  // xmp.setParser(new TmdbInfo());
  // movieInfo = xmp.parseXml();
  //
  // XMLParser<MovieImage> xmmp = new XMLParser<MovieImage>(xmlFile.getAbsolutePath());
  // xmmp.setParser(new TmdbImage());
  // movieImage = xmmp.parseXml();
  //
  // } catch (IOException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // } catch (InterruptedException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // } catch (ParserConfigurationException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // } catch (SAXException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // }
  //
  // if (movieInfo == null) {
  // firePropertyChange("closeLoadingDial", "scrapperInfoFailed");
  // return null;
  // }
  //
  // if (movieImage == null) {
  // firePropertyChange("closeLoadingDial", "scrapperInfoFailed");
  // return null;
  // }
  //
  // movieInfo.setImages(movieImage);
  // if(!movieInfo.getTrailer().equals("")){
  // String trailer = YTdecodeUrl.getRealUrl(movieInfo.getTrailer(), YTdecodeUrl.HD);
  // if(trailer != null) {
  // movieInfo.setTrailer(trailer);
  // }
  // }
  //
  // setProgress(100);
  // return movieInfo;
  // }

}
