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
package fr.free.movierenamer.ui.swing.dialog;

import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.ui.Main;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import static fr.free.movierenamer.ui.utils.UIUtils.i18n;
import fr.free.movierenamer.ui.worker.impl.DownloadWorker;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JDialog;

/**
 * Class MediaInfoDownload,
 *
 * @author Nicolas Magré
 */
public class MediaInfoDownloadDialog extends JDialog {

  private final MovieRenamer mr;

  public MediaInfoDownloadDialog(MovieRenamer mr) {
    this.mr = mr;

    initComponents();
    webLabel1.setIcon(ImageUtils.FILE);
    titleLbl.setLanguage(i18n.getLanguageKey("error.mediaInfoNotInstalled", false));
    questionLbl.setLanguage(i18n.getLanguageKey("dialog.installMediaInfo", false));
    infoLbl.setLanguage(i18n.getLanguageKey("dialog.infoMediaInfo", false));
    donotaskChk.setLanguage(i18n.getLanguageKey("dialog.doNotAskAgain", false));
    yesBtn.setPreferredSize(UIUtils.buttonSize);
    noBtn.setPreferredSize(UIUtils.buttonSize);
    yesBtn.setLanguage(i18n.getLanguageKey("yes", false));
    noBtn.setLanguage(i18n.getLanguageKey("no", false));
    yesBtn.setIcon(ImageUtils.OK_16);
    noBtn.setIcon(ImageUtils.CANCEL_16);

    titleLbl.setFont(UIUtils.titleFont);
    questionLbl.setFont(UIUtils.boldFont);

    setTitle(i18n.getLanguage("dialog.question", false));
    setIconImage(ImageUtils.iconToImage(ImageUtils.LOGO_22));
    setModal(true);
  }

  @Override
  public void setVisible(boolean b) {
    if (b) {
      UIUtils.showOnScreen(mr, this);
    }
    super.setVisible(b);
  }

  private void saveDoNotAsk() {
    if (donotaskChk.isSelected()) {
      try {
        UISettings.UISettingsProperty.mediaInfoWarning.setValue(Boolean.FALSE);
      } catch (IOException ex) {// FIXME add an error dialog
        UISettings.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    webLabel1 = new com.alee.laf.label.WebLabel();
    titleLbl = new com.alee.laf.label.WebLabel();
    questionLbl = new com.alee.laf.label.WebLabel();
    infoLbl = new com.alee.extended.label.WebMultiLineLabel();
    donotaskChk = new com.alee.laf.checkbox.WebCheckBox();
    yesBtn = new com.alee.laf.button.WebButton();
    noBtn = new com.alee.laf.button.WebButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setResizable(false);

    yesBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        yesBtnActionPerformed(evt);
      }
    });

    noBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        noBtnActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(webLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(titleLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
              .addComponent(questionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
          .addGroup(layout.createSequentialGroup()
            .addGap(36, 36, 36)
            .addComponent(donotaskChk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(yesBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(noBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(infoLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(webLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(titleLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(questionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        .addGap(29, 29, 29)
        .addComponent(infoLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(31, 31, 31)
        .addComponent(donotaskChk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(yesBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(noBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void noBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noBtnActionPerformed
    saveDoNotAsk();
    dispose();
  }//GEN-LAST:event_noBtnActionPerformed

  private void yesBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yesBtnActionPerformed
    saveDoNotAsk();
    dispose();
    try {
      File installDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
      if (!installDir.canWrite()) {
        UIUtils.showErrorNotification(i18n.getLanguage("error.noWritePermission", false, installDir.getPath()));
        return;
      }

      URL url = new URL("http", UISettings.HOST, "/updater/media_info/" + (Settings.IS64BIt ? "64" : "32") + "/MediaInfo.dll");
      List<URL> urls = new ArrayList<>();
      urls.add(url);
      DownloadWorker worker = new DownloadWorker(mr, urls, installDir);
      worker.execute();

    } catch (URISyntaxException | MalformedURLException ex) {
      UISettings.LOGGER.log(Level.SEVERE, null, ex);
      UIUtils.showErrorNotification(i18n.getLanguage("error.unknown", false, getClass().getSimpleName(), ex.getLocalizedMessage()));
    }

  }//GEN-LAST:event_yesBtnActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.alee.laf.checkbox.WebCheckBox donotaskChk;
  private com.alee.extended.label.WebMultiLineLabel infoLbl;
  private com.alee.laf.button.WebButton noBtn;
  private com.alee.laf.label.WebLabel questionLbl;
  private com.alee.laf.label.WebLabel titleLbl;
  private com.alee.laf.label.WebLabel webLabel1;
  private com.alee.laf.button.WebButton yesBtn;
  // End of variables declaration//GEN-END:variables
}
