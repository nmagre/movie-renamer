/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.utils;

/**
 * Class update
 * @author Nicolas Magré
 */
public class Update {

  private String version;
  private String currentVersion;
  private String descEN;
  private String descFR;
  private String url;

  public Update(String currentVersion) {
    this.currentVersion = currentVersion;
  }

  public String getVersion() {
    return version;
  }

  public String getDescription(boolean french) {
    if (french) {
      return descFR;
    }
    return descEN;
  }

  public String getUrl() {
    return url;
  }

  public boolean updateAvailable() {
    return !version.equals(currentVersion);
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public void setDescEN(String descEN) {
    this.descEN = descEN;
  }

  public void setDescFR(String descFR) {
    this.descFR = descFR;
  }
  
  public void setUrl(String url){
    this.url = url;
  }
}
