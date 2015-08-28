/*
 * Movie Renamer
 * Copyright (C) 2013-2014 Nicolas Magré
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
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.SpinningDial;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;

/**
 * Class Loading
 *
 * @author Nicolas Magré
 */
public class LoadingDialog extends WebDialog {

  private static final long serialVersionUID = 1L;
  private WebPanel panel;
  private WebLabel logoLbl;
  private WebLabel spinning;

  public LoadingDialog() {

    initComponents();
    setIconImage(ImageUtils.iconToImage(ImageUtils.LOGO_32));
    setTitle(UISettings.APPNAME);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  public void hideDial() {
    setVisible(false);
    dispose();
  }

  private void initComponents() {

    panel = new WebPanel();
    spinning = new WebLabel();
    logoLbl = new WebLabel();

    setAlwaysOnTop(true);

    spinning.setIcon(new SpinningDial(24, 43));
    spinning.setLanguage(UIUtils.i18n.getLanguageKey("loading", false));
    spinning.setDrawShade(true);

    logoLbl.setIcon(ImageUtils.LOGO_72);

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(panel);
    panel.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(logoLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(spinning, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()));

    jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(spinning, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(logoLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap()));

    getContentPane().add(panel, java.awt.BorderLayout.CENTER);

    pack();
  }

}
