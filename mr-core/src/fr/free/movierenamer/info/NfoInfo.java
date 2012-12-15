/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.info;

/**
 * Class NfoInfo
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class NfoInfo {// TODO

  public enum NFOtype {
    XBMC,
    MEDIAPORTAL,
    YAMJ,
    BOXEE
  }
  private MediaInfo mediaInfo;
  private CastingInfo castingInfo;

  public NfoInfo(MediaInfo mediaInfo, CastingInfo castingInfo) {
    this.mediaInfo = mediaInfo;
    this.castingInfo = castingInfo;
  }

  public String getNFO(NFOtype nfo){
    String res = "";
    switch(nfo) {
      case BOXEE:
        break;
      case MEDIAPORTAL:
        break;
      case XBMC:
        break;
      case YAMJ:
        break;
    }
    return res;
  }

}
