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

package fr.free.movierenamer.media;

import fr.free.movierenamer.ui.res.IIconList;
import fr.free.movierenamer.utils.Utils;
import java.io.File;
import javax.swing.Icon;

/**
 * Class MovieFile
 * @author Nicolas Magré
 */
public class MediaFile implements IIconList {

  private File file;
  private boolean renamed;
  private boolean showPath;
  private boolean wasRenamed;
  private int type;

  /**
   * Constructor arguments
   * @param file A movie file
   * @param renamed Already rename
   * @param wasRenamed Movie was renamed
   * @param warning Warning on the file
   * @param showPath Display path in toString()
   */
  public MediaFile(File file, int type, boolean renamed, boolean wasRenamed, boolean showPath){
    this.file = file;
    this.type = type;
    this.wasRenamed = wasRenamed;
    this.renamed = renamed;
    this.showPath = showPath;
  }

  /**
   * Get file
   * @return File 
   */
  public File getFile(){
    return file;
  }

  /**
   * Set file
   * @param file File
   */
  public void setFile(File file){
    this.file = file;
  }

  /**
   * Set renamed
   * @param renamed Renamed
   */
  public void setRenamed(boolean renamed){
    this.renamed = renamed;
  }

  /**
   * Is renamed
   * @return Renamed
   */
  public boolean isRenamed(){
    return renamed;
  }

  /**
   * Was renamed
   * @return Was renamed
   */
  public boolean wasRenamed(){
    return wasRenamed;
  }

  /**
   * Get file type
   * @return Warning
   */
  public int type(){
    return type;
  }

  @Override
  public Icon getIcon() {
    if(renamed) return Utils.MOVIERENAMEDICON;
    if(wasRenamed) return Utils.MOVIEWASRENAMEDICON;
    //if(warning) return Utils.WARNINGICON;
    return Utils.MOVIEICON;
  }

  @Override
  public String toString(){
    return (showPath ? file.toString():file.getName());
  }
}
