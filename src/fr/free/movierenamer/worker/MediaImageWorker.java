/*
 * movie-renamer
 * Copyright (C) 2012 QUÉMÉNEUR Simon
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
package fr.free.movierenamer.worker;

import fr.free.movierenamer.media.IMediaImage;
import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.parser.xml.MrParser;
import fr.free.movierenamer.parser.xml.XMLParser;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Class MediaImageWorker
 * 
 * @author QUÉMÉNEUR Simon
 */
public abstract class MediaImageWorker<T extends IMediaImage> extends HttpWorker<T> {

  protected final MediaID id;

  /**
   * Constructor arguments
   * 
   * @param errorSupport
   *          Swing change support
   * @throws ActionNotValidException
   */
  public MediaImageWorker(SwingPropertyChangeSupport errorSupport, MediaID id) throws ActionNotValidException {
    super(errorSupport);
    this.id = id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.free.movierenamer.worker.HttpWorker#proccessFile(java.io.File)
   */
  @Override
  protected final T proccessFile(File xmlFile) throws Exception {
    T movieImage = null;

    try {
      XMLParser<T> xmmp = new XMLParser<T>(xmlFile.getAbsolutePath());
      MrParser<T> imageParser = getImageParser();
      imageParser.setOriginalFile(xmlFile);
      xmmp.setParser(imageParser);
      movieImage = xmmp.parseXml();

    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (ParserConfigurationException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (SAXException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    if (movieImage == null) {
      firePropertyChange("closeLoadingDial", "scrapperInfoFailed");
      return null;
    }

    setProgress(100);
    return movieImage;
  }

  /**
   * @return
   */
  protected abstract MrParser<T> getImageParser() throws Exception;

}
