/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2011 Magré Nicolas                                         *
 *                                                                             *
 *    Movie Renamer is free software: you can redistribute it and/or modify    *
 *    it under the terms of the GNU General Public License as published by     *
 *    the Free Software Foundation, either version 3 of the License, or        *
 *    (at your option) any later version.                                      *
 *                                                                             *
 *    This program is distributed in the hope that it will be useful,          *
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 *    GNU General Public License for more details.                             *
 *                                                                             *
 *    You should have received a copy of the GNU General Public License        *
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                             *
 ******************************************************************************/
package plugins;

import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * Interface IPluginInfo
 * @author Nicolas Magré
 */
public interface IPluginInfo extends IPlugin {

  public enum MOVIEINFO {
    IDMOVIE,
    TITLE,
    PLOT,
    OUTLINE,
    TAGLINE,
    RATING,
    YEAR,
    THUMB,
    IMDBID,
    SORTTITLE,
    RUNTIME,
    MPAA,
    GENRE,
    DIRECTOR,
    WRITER,
    ORIGINALTITLE,
    STUDIO,
    TRAILER,
    FANART,
    COUNTRY,
    STRFILENAME,
    STRPATH,
    SET;
  }

  public void setMovieFile(String movieFileNoExt);
  public String getRenameStrChk(); // != null call exec if chk enabled, null do not call exec
  public JPanel getInfoPanel();
  public void setInfoPanel(String search);
  public ArrayList<Info> getInfoChanged();
  public void clearPanel();
  public JPanel getSettingPanel();
}
