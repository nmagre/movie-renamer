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
import fr.free.movierenamer.settings.XMLSettings;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import static fr.free.movierenamer.ui.utils.UIUtils.i18n;
import fr.free.movierenamer.ui.worker.impl.HelpWorker;
import fr.free.movierenamer.utils.LocaleUtils;
import java.net.MalformedURLException;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 * Class SettingsHelpDialog
 *
 * @author Nicolas Magré
 */
public class SettingsHelpDialog extends AbstractDialog {

  private static final long serialVersionUID = 1L;

  private String id;

  /**
   * Creates new form SettingsHelpDialog
   *
   * @param mr
   * @param type
   * @param subType
   * @throws java.net.MalformedURLException
   */
  public SettingsHelpDialog(MovieRenamer mr, XMLSettings.SettingsType type, XMLSettings.SettingsSubType subType) throws MalformedURLException {
    super(mr, i18n.getLanguageKey("dialog.help", false));

    LocaleUtils.AppLanguages lng = Settings.getInstance().getAppLanguage();
    id = "wiki";
    if (!lng.equals(LocaleUtils.AppLanguages.en)) {
      id = lng.name();
    }
    id += ":settings:" + type.name().toLowerCase() + ":" + subType.name().toLowerCase();

    initComponents();

    StyleSheet sheet = ((HTMLEditorKit) webEditorPane1.getEditorKit()).getStyleSheet();
    sheet.addRule("pre {\n"
            + "    border: 1px solid #CCCCCC;\n"
            + "    border-radius: 2px;\n"
            + "    box-shadow: 0 0 0.5em #CCCCCC inset;\n"
            + "    overflow: auto;\n"
            + "    padding: 0.7em 1em;\n"
            + "    word-wrap: normal;\n"
            + "}");
  }

  public void getHelp() {
    HelpWorker worker = new HelpWorker(mr, id, this);
    worker.execute();
  }

  public void setText(String text) {
    webEditorPane1.setText(text);
    webEditorPane1.setCaretPosition(0);
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jScrollPane1 = new javax.swing.JScrollPane();
    webEditorPane1 = new com.alee.laf.text.WebEditorPane();
    webButton1 = UIUtils.createButton(i18n.getLanguageKey("ok", false), ImageUtils.OK_16);

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    webEditorPane1.setEditable(false);
    webEditorPane1.setContentType("text/html"); // NOI18N
    webEditorPane1.setText("");
    jScrollPane1.setViewportView(webEditorPane1);

    webButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        webButton1ActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1)
        .addContainerGap())
      .addGroup(layout.createSequentialGroup()
        .addGap(154, 154, 154)
        .addComponent(webButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(174, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(webButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void webButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_webButton1ActionPerformed
    dispose();
  }//GEN-LAST:event_webButton1ActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane jScrollPane1;
  private com.alee.laf.button.WebButton webButton1;
  private com.alee.laf.text.WebEditorPane webEditorPane1;
  // End of variables declaration//GEN-END:variables
}
