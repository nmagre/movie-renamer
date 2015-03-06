/*
 * movie-renamer-core
 * Copyright (C) 2014 Nicolas Magré
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
package fr.free.movierenamer.scraper.impl.trailer;

import fr.free.movierenamer.info.TrailerInfo;
import fr.free.movierenamer.scraper.ScraperManager;
import fr.free.movierenamer.scraper.TrailerScraper;
import fr.free.movierenamer.scraper.TrailerScraperThread;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.searchinfo.Trailer;
import fr.free.movierenamer.settings.Settings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class TrailerAddictScraper
 *
 * @author Nicolas Magré
 */
public class UniversalTrailerScraper extends TrailerScraper {

  @Override
  protected List<Trailer> searchTrailer(Media media) throws Exception {
    List<Trailer> trailers = new ArrayList<Trailer>();

    List<TrailerScraper> scrapers = ScraperManager.getTrailerScraperList(media.getMediaType());
    int poolSize = 5;
    int nbthread = 0;
    ExecutorService service = Executors.newFixedThreadPool(poolSize);
    CompletionService<List<Trailer>> pool = new ExecutorCompletionService<List<Trailer>>(service);

    for (TrailerScraper scraper : scrapers) {
      pool.submit(new TrailerScraperThread(scraper, media));
      nbthread++;
    }

    for (int i = 0; i < nbthread; i++) {

      try {
        trailers.addAll(pool.take().get());
      } catch (Exception ex) {
        Settings.LOGGER.warning(ex.getMessage());
      }
    }

    return trailers;
  }

  @Override
  public String getName() {
    return "UniversalTrailer";
  }

  @Override
  protected String getHost() {
    return null;
  }

  @Override
  protected TrailerInfo fetchTrailerInfo(Trailer searchResult) throws Exception {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<Media.MediaType> getSupportedMediaType() {
    return null;
  }

}
