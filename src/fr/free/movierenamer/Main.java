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
package fr.free.movierenamer;

import fr.free.movierenamer.utils.Utils;
import fr.free.movierenamer.utils.Settings;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import fr.free.movierenamer.parser.XMLParser;
import fr.free.movierenamer.ui.MovieRenamer;

/**
 *
 * @author duffy
 */
public class Main {

  private static MovieRenamer mvr;

  public static void main(String args[]) throws UnsupportedEncodingException, URISyntaxException {

    try {
      UIManager.put("FileChooser.openDialogTitleText", "Open");
      UIManager.put("FileChooser.lookInLabelText", "Look In");
      UIManager.put("FileChooser.openButtonText", "Open");
      UIManager.put("FileChooser.cancelButtonText", "Cancel");
      UIManager.put("FileChooser.fileNameLabelText", "FileName");
      UIManager.put("FileChooser.filesOfTypeLabelText", "Files Type");
      UIManager.put("FileChooser.openButtonToolTipText", "Open Selected File");
      UIManager.put("FileChooser.cancelButtonToolTipText", "Cancel");
      UIManager.put("FileChooser.fileNameHeaderText", "FileName");
      UIManager.put("FileChooser.upFolderToolTipText", "Up One Level");
      UIManager.put("FileChooser.homeFolderToolTipText", "Desktop");
      UIManager.put("FileChooser.newFolderToolTipText", "Create New Folder");
      UIManager.put("FileChooser.listViewButtonToolTipText", "List");
      UIManager.put("FileChooser.newFolderButtonText", "Create New Folder");
      UIManager.put("FileChooser.renameFileButtonText", "Rename File");
      UIManager.put("FileChooser.deleteFileButtonText", "Delete File");
      UIManager.put("FileChooser.filterLabelText", "Files Type");
      UIManager.put("FileChooser.detailsViewButtonToolTipText", "Details");
      UIManager.put("FileChooser.fileSizeHeaderText", "Size");
      UIManager.put("FileChooser.fileDateHeaderText", "Modified Date");

      UIManager.put("OptionPane.cancelButtonText", "Cancel");
      UIManager.put("OptionPane.noButtonText", "No");
      UIManager.put("OptionPane.okButtonText", "Ok");
      UIManager.put("OptionPane.yesButtonText", "Yes");
    } catch (Exception e) {
      JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      // return;
    }

    final Settings setting = loadSetting();
    java.awt.EventQueue.invokeLater(new Runnable() {

      @Override
      public void run() {
        mvr = new MovieRenamer(setting);
        mvr.setVisible(true);
      }
    });
  }

  private static Settings loadSetting() {
    Settings setting = new Settings();
    File file = new File(setting.configFile);
    boolean saved = true;
    if (file.exists())
      try {
        XMLParser<Settings> mmp = new XMLParser<Settings>(setting.configFile, Settings.class);
        setting = mmp.parseXml();
        if (!setting.getVersion().equals(setting.xmlVersion) || setting.xmlError)
          saved = setting.saveSetting();
      } catch (IOException ex) {
        setting.getLogger().log(Level.SEVERE, Utils.getStackTrace("IOException : " + ex.getMessage(), ex.getStackTrace()));
        saved = setting.saveSetting();
      } catch (InterruptedException ex) {
        setting.getLogger().log(Level.SEVERE, Utils.getStackTrace("InterruptedException : " + ex.getMessage(), ex.getStackTrace()));
        saved = setting.saveSetting();
      }
    else saved = setting.saveSetting();
    if (!saved) JOptionPane.showMessageDialog(null, "Unable to save setting", "Error", JOptionPane.ERROR_MESSAGE);
    return setting;
  }
}
