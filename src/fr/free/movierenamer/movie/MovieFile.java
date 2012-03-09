/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2012 Magré Nicolas                                         *
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

package fr.free.movierenamer.movie;

import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import fr.free.movierenamer.ui.res.IIconList;
import fr.free.movierenamer.utils.Utils;

/**
 * Class MovieFile
 * @author Nicolas Magré
 */
public class MovieFile implements IIconList {

  private File file;
  private boolean renamed;
  private boolean warning;
  private boolean showPath;

  /**
   * Constructor arguments
   * @param file A movie file
   * @param renamed Already rename
   * @param warning Warning on the file
   * @param showPath Display path in toString()
   */
  public MovieFile(File file, boolean renamed, boolean warning, boolean showPath){
    this.file = file;
    this.renamed = renamed;
    this.warning = warning;
    this.showPath = showPath;
  }

  /**
   * Get file
   * @return File 
   */
  public File getFile(){
    return file;
  }

  public void setFile(File file){
    this.file = file;
  }

  public void setRenamed(boolean renamed){
    this.renamed = renamed;
  }

  public boolean isRenamed(){
    return renamed;
  }

  public boolean isWarning(){
    return warning;
  }

  @Override
  public Icon getIcon() {
    if(renamed) return Utils.MOVIERENAMEDICON;
    if(warning) return Utils.WARNINGICON;
    return Utils.MOVIEICON;
  }

  @Override
  public String toString(){
    return (showPath ? file.toString():file.getName());
  }
}
