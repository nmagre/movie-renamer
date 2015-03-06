package fr.free.movierenamer.scraper;

import org.junit.Test;

public abstract class TvShowScraperTest extends ScraperTest {
  
  @Test
  public abstract void search() throws Exception;

  @Test
  public abstract void getTvShowInfo() throws Exception;
  
  @Test
  public abstract void getCasting() throws Exception;

  @Test
  public abstract void getImages() throws Exception;

  @Test
  public abstract void getEpisodesInfoList() throws Exception;
}
