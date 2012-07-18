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

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.parser.xml.XbmcPassionIDLookup;
import fr.free.movierenamer.parser.xml.MrParser;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.worker.HttpWorker;
import java.beans.PropertyChangeSupport;

/**
 * Class XbmcPassionIDLookupWorker
 *
 * @author Nicolas Magré
 */
public class XbmcPassionIDLookupWorker extends HttpWorker<MediaID> {

  private final MediaID mid;

  /**
   * Constructor arguments
   *
   * @param errorSupport
   * @param id
   * @throws ActionNotValidException
   */
  public XbmcPassionIDLookupWorker(PropertyChangeSupport errorSupport, MediaID id) throws ActionNotValidException {
    super(errorSupport);
    if (id.getType() != MediaID.MediaIdType.IMDBID && id.getType() != MediaID.MediaIdType.ALLOCINEID) {
      throw new ActionNotValidException("XbmcPassionIDLookupWorker can only use imdb or allocine ID");
    }
    this.mid = id;
  }

  @Override
  protected String getUri() throws Exception {
    String apiID = mid.getType() == MediaID.MediaIdType.ALLOCINEID ? mid.getID() : mid.getID().substring(2);
    return (Settings.xbmcPassionImdblookup + (mid.getType() == MediaID.MediaIdType.ALLOCINEID ? "IdAllo=" : "IdImdb=") + apiID);
  }

  @Override
  protected MrParser<MediaID> getParser() throws Exception {
    return new XbmcPassionIDLookup(mid);
  }
}
