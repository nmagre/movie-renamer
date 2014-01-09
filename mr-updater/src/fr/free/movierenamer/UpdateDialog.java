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

import fr.free.movierenamer.utils.UpdateUtils;
import fr.free.movierenamer.worker.UpdateWorker;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

/**
 * Class UpdateFrame
 *
 * @author Nicolas Magré
 */
public class UpdateDialog extends JDialog {

  private final JPanel loadingPanel;
  private final JLabel loadingLbl;
  private final Icon loadingIcon;
  private final String version;
  private final File installdir, updateDir;

  public UpdateDialog(String version, File installdir, File updateDir) {
    this.version = version;
    this.installdir = installdir;
    this.updateDir = updateDir;

    loadingIcon = new ImageIcon(getClass().getClassLoader().getResource("image/loading.gif"));
    loadingPanel = new JPanel(new BorderLayout());
    loadingPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    loadingLbl = new JLabel(loadingIcon);

    loadingPanel.add(loadingLbl, BorderLayout.WEST);

    getContentPane().add(loadingPanel);
    setSize(350, 100);
    setLocationRelativeTo(null);
    setResizable(false);

    try {
      setIconImage(ImageIO.read(getClass().getClassLoader().getResource("image/icon-22.png")));
    } catch (IOException ex) {
      Logger.getLogger(UpdateDialog.class.getName()).log(Level.SEVERE, null, ex);
    }

    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    setTitle("Movie Renamer update");
  }

  public void startUpdate() {
    final UpdateWorker worker = new UpdateWorker(this, version, updateDir, installdir);
    worker.addPropertyChangeListener(new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (!(evt.getNewValue() instanceof SwingWorker.StateValue)) {
          return;
        }

        switch ((SwingWorker.StateValue) evt.getNewValue()) {
          case DONE:
            try {
              String message = worker.get();
              if (message != null) {
                JOptionPane.showMessageDialog(UpdateDialog.this, message, "Error", JOptionPane.ERROR_MESSAGE);
              } else {
                String javaBin = System.getProperty("java.home") + "/bin/java";
                File jarFile = new File(installdir, "Movie Renamer.jar");
                String toExec[] = new String[]{javaBin, "-jar", jarFile.getPath()};

                try {
                  if (UpdateUtils.isUnix()) {
                    Process p = Runtime.getRuntime().exec(new String[]{"chmod", "+x", jarFile.getPath()});
                  }

                  Process p1 = Runtime.getRuntime().exec(toExec);
                } catch (Exception ex) {
                  JOptionPane.showMessageDialog(UpdateDialog.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                  Logger.getLogger(UpdateDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
              }
            } catch (Exception ex) {
              JOptionPane.showMessageDialog(UpdateDialog.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
              Logger.getLogger(UpdateDialog.class.getName()).log(Level.SEVERE, null, ex);
            }

            dispose();
            System.exit(0);
            break;
        }
      }
    });
    worker.execute();
  }

  public void setStatus(String message) {
    loadingLbl.setText(message);
  }

}
