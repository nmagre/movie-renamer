package fr.free.movierenamer.scrapper;

import org.junit.Test;

public abstract class TvShowScrapperTest extends ScrapperTest {


  @Test
  public abstract void getTvShowInfo() throws Exception;
  
  @Test
  public abstract void getCasting() throws Exception;

  @Test
  public abstract void getImages() throws Exception;

  @Test
  public abstract void getEpisodesInfoList() throws Exception;
}
