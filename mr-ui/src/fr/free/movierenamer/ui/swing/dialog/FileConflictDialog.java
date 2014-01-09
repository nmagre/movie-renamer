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

import com.alee.laf.label.WebLabel;
import com.alee.laf.rootpane.WebDialog;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import static fr.free.movierenamer.ui.utils.UIUtils.i18n;
import fr.free.movierenamer.utils.StringUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.Icon;

/**
 * Class FileConflictDialog
 *
 * @author Nicolas Magré
 */
public class FileConflictDialog extends WebDialog {

  private final File origFile, newFile;
  private final Action action = Action.cancel;
  private final MovieRenamer mr;

  public static enum Action {

    cancel,
    skip,
    replace
  }

  public FileConflictDialog(MovieRenamer mr, File origFile, File newFile) {
    this.mr = mr;
    this.origFile = origFile;
    this.newFile = newFile;

    initComponents();

    origIconLbl.setIcon(ImageUtils.FILE);
    newIconLbl.setIcon(ImageUtils.FILE);

    setFileIcon(origFile, origIconLbl);
    setFileIcon(newFile, newIconLbl);

    replaceTitleLbl.setLanguage(i18n.getLanguageKey("dialog.replacefile", false), newFile.getName());
    alreadyExistLbl.setLanguage(i18n.getLanguageKey("dialog.alreadyexist", false), newFile.getParentFile().getName());
    origLbl.setLanguage(i18n.getLanguageKey("dialog.origfile", false));
    newLbl.setLanguage(i18n.getLanguageKey("dialog.replacewith", false));
    origSizeLbl.setLanguage(i18n.getLanguageKey("dialog.size", false), StringUtils.humanReadableByteCount(origFile.length()));
    newSizeLbl.setLanguage(i18n.getLanguageKey("dialog.size", false), StringUtils.humanReadableByteCount(origFile.length()));
    origModifiedLbl.setLanguage(i18n.getLanguageKey("dialog.lastmodified", false), StringUtils.humanReadableDate(origFile.lastModified()));
    newModifiedLbl.setLanguage(i18n.getLanguageKey("dialog.lastmodified", false), StringUtils.humanReadableDate(origFile.lastModified()));
    applyAllChk.setLanguage(i18n.getLanguageKey("dialog.applytoall", false));

    // Font
    replaceTitleLbl.setFont(UIUtils.titleFont);
    origLbl.setFont(UIUtils.boldFont);
    newLbl.setFont(UIUtils.boldFont);
    origModifiedLbl.setFont(UIUtils.italicFont);
    newModifiedLbl.setFont(UIUtils.italicFont);

    setTitle(i18n.getLanguage("dialog.fileconflict", false));
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

  public Action getAction() {
    return action;
  }

  public boolean applyToAll() {
    return applyAllChk.isSelected();
  }

  private void setFileIcon(File file, WebLabel label) {
    try {
      String contentType = Files.probeContentType(file.toPath());
      if (contentType != null) {
        if (contentType.contains("image")) {
          Icon icon = ImageUtils.getIcon(file.toURI(), null, null);
          if (icon != null) {
            label.setIcon(icon);
          }
        }
      }
    } catch (IOException ex) {
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

    iconLbl = new WebLabel(ImageUtils.WARNING);
    replaceTitleLbl = new com.alee.laf.label.WebLabel();
    alreadyExistLbl = new com.alee.laf.label.WebLabel();
    origIconLbl = new com.alee.laf.label.WebLabel();
    origLbl = new com.alee.laf.label.WebLabel();
    origSizeLbl = new com.alee.laf.label.WebLabel();
    origModifiedLbl = new com.alee.laf.label.WebLabel();
    newIconLbl = new com.alee.laf.label.WebLabel();
    newLbl = new com.alee.laf.label.WebLabel();
    newSizeLbl = new com.alee.laf.label.WebLabel();
    newModifiedLbl = new com.alee.laf.label.WebLabel();
    applyAllChk = new com.alee.laf.checkbox.WebCheckBox();
    replaceBtn = UIUtils.createButton(i18n.getLanguageKey("replace", false), ImageUtils.OK_16);
    cancelBtn = UIUtils.createButton(i18n.getLanguageKey("cancel", false), ImageUtils.CANCEL_16);
    skipBtn = UIUtils.createButton(i18n.getLanguageKey("skip", false), ImageUtils.SKIP_16);

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setResizable(false);

    origIconLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

    newIconLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

    cancelBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelBtnActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(iconLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addComponent(newIconLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                  .addComponent(newSizeLbl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
                  .addComponent(newModifiedLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(newLbl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
              .addComponent(alreadyExistLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(replaceTitleLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addGroup(layout.createSequentialGroup()
                .addComponent(origIconLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(origLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(origSizeLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(origModifiedLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
              .addComponent(applyAllChk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
          .addGroup(layout.createSequentialGroup()
            .addComponent(replaceBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(skipBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 124, Short.MAX_VALUE)
            .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(iconLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(replaceTitleLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(alreadyExistLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addGap(18, 18, 18)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(origIconLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(origLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(origSizeLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(origModifiedLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addGap(18, 18, 18)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(newIconLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(newLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(newSizeLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(newModifiedLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addGap(18, 18, 18)
        .addComponent(applyAllChk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(replaceBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(skipBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
    dispose();
  }//GEN-LAST:event_cancelBtnActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.alee.laf.label.WebLabel alreadyExistLbl;
  private com.alee.laf.checkbox.WebCheckBox applyAllChk;
  private com.alee.laf.button.WebButton cancelBtn;
  private com.alee.laf.label.WebLabel iconLbl;
  private com.alee.laf.label.WebLabel newIconLbl;
  private com.alee.laf.label.WebLabel newLbl;
  private com.alee.laf.label.WebLabel newModifiedLbl;
  private com.alee.laf.label.WebLabel newSizeLbl;
  private com.alee.laf.label.WebLabel origIconLbl;
  private com.alee.laf.label.WebLabel origLbl;
  private com.alee.laf.label.WebLabel origModifiedLbl;
  private com.alee.laf.label.WebLabel origSizeLbl;
  private com.alee.laf.button.WebButton replaceBtn;
  private com.alee.laf.label.WebLabel replaceTitleLbl;
  private com.alee.laf.button.WebButton skipBtn;
  // End of variables declaration//GEN-END:variables
}
