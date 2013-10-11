/*
 * Movie Renamer
 * Copyright (C) 2012-2013 Nicolas Magré
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
package fr.free.movierenamer.ui.swing.panel;

import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.SpinningDial;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.utils.LocaleUtils;

/**
 * Class Loading
 *
 * @author Nicolas Magré
 */
public class Loading extends WebDialog {

  private static final long serialVersionUID = 1L;

  public Loading() {
    initComponents();
    setIconImage(ImageUtils.iconToImage(ImageUtils.LOGO_32));
    setTitle(UISettings.APPNAME);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private void initComponents() {

    jPanel1 = new WebPanel();
    webLabel1 = new WebLabel();
    logoLbl = new WebLabel();

    setAlwaysOnTop(true);

    webLabel1.setIcon(new SpinningDial(24, 43));
    webLabel1.setLanguage(UIUtils.i18n.getLanguageKey("loading", false));

    logoLbl.setIcon(ImageUtils.LOGO_72);

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(logoLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(webLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap()));

    jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
            .addComponent(webLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(logoLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap()));

    getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

    pack();
  }
  private WebPanel jPanel1;
  private WebLabel logoLbl;
  private WebLabel webLabel1;
}
