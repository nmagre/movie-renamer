/*
 * movie-renamer
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
package fr.free.movierenamer.scrapper.impl;

import fr.free.movierenamer.info.PersonInfo;
import fr.free.movierenamer.scrapper.PersonScrapper;
import fr.free.movierenamer.searchinfo.Person;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.ImageUtils;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Class AllocinePersonScrapper : search person on allocine
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class AllocinePersonScrapper extends PersonScrapper<Person> {

  private static final String host = "api.allocine.fr";
  private static final String name = "Allocine";
  private static final String version = "3";

  private final String apikey;

  public AllocinePersonScrapper() {
    String key = Settings.getApplicationProperty("allocine.apikey");
    if (key == null) {
      throw new NullPointerException("apikey must not be null");
    }
    this.apikey = key;
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
  public Icon getIcon() {
    return new ImageIcon(ImageUtils.getImageFromJAR("scrapper/allocine.png"));
  }

  @Override
  public List<PersonInfo> getPersons(Person person, Locale locale) throws Exception {
	  URL searchUrl = new URL("http", host, "/rest/v" + version + "/person?partner=" + apikey + "&profile=large&format=xml&code=" + person.getPersonId());

	  // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not supported yet.");
    // return null;
  }

}
