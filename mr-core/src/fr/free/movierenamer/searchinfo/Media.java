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
package fr.free.movierenamer.searchinfo;

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.scraper.MediaScraper;
import fr.free.movierenamer.scraper.MovieScraper;
import fr.free.movierenamer.scraper.TvShowScraper;
import fr.free.movierenamer.scraper.impl.movie.TMDbScraper;
import fr.free.movierenamer.scraper.impl.movie.UniversalScraper;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.ScraperUtils;
import java.net.URL;

/**
 * Class Media
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class Media extends Hyperlink {

  private static final long serialVersionUID = 1L;
  protected static final Settings settings = Settings.getInstance();
  protected IdInfo idInfo;

  public enum MediaType {

    MOVIE(MovieScraper.class, UniversalScraper.class) {
        @Override
        public IdInfo idLookup(ScraperUtils.AvailableApiIds lookupType, IdInfo id, Media searchResult) {
          return ScraperUtils.movieIdLookup(lookupType, id, (Movie) searchResult);
        }
      },
    TVSHOW(TvShowScraper.class, TMDbScraper.class) {
        @Override
        public IdInfo idLookup(ScraperUtils.AvailableApiIds lookupType, IdInfo id, Media searchResult) {
          return null;
        }
      };

    private final Class<? extends MediaScraper> scraperClazz;
    private final Class<? extends MediaScraper> scraperTypeClazz;

    private MediaType(Class<? extends MediaScraper> scraperTypeClazz, Class<? extends MediaScraper> scraperClazz) {
      this.scraperTypeClazz = scraperTypeClazz;
      this.scraperClazz = scraperClazz;
    }

    public Class<? extends MediaScraper> getDefaultScraper() {
      return scraperClazz;
    }

    public Class<? extends MediaScraper> getScraperTypeClass() {
      return scraperTypeClazz;
    }

    public abstract IdInfo idLookup(ScraperUtils.AvailableApiIds lookupType, IdInfo id, Media searchResult);
  }

  protected Media() {
    // used by serializer
  }

  public Media(IdInfo id, String title, String originalTitle, int year, URL thumb) {
    super(title, originalTitle, year, thumb);
    this.idInfo = id;
  }

  public abstract MediaType getMediaType();

  public IdInfo getMediaId() {
    return idInfo;
  }

  public void setMediaId(IdInfo idInfo) {
    this.idInfo = idInfo;
  }

}
