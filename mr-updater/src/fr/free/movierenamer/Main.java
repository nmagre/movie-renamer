/*
 * Movie Renamer
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
package fr.free.movierenamer;

import java.io.File;
import javax.swing.SwingUtilities;

/**
 * Class Updater
 *
 * @author Nicolas Magré
 */
public class Main {

  public static void main(String[] args) {

    if (args.length != 3) {
      System.exit(-1);
    }

    final String version = args[0];
    final String sinstallDir = args[1];
    final String supdateDir = args[2];

    final File installDir = new File(sinstallDir);
    final File UpdateDir = new File(supdateDir);

    if (!installDir.exists()) {
      System.exit(-2);
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        UpdateDialog uf = new UpdateDialog(version, installDir, UpdateDir);
        uf.setVisible(true);
        uf.startUpdate();
      }
    });
  }

}
