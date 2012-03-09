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
package fr.free.movierenamer.ui;

import fr.free.movierenamer.Main;
import fr.free.movierenamer.ui.res.ContextMenuFieldMouseListener;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.awt.Component;
import javax.swing.border.TitledBorder;

/**
 *
 * @author duffy
 */
public class Setting extends JDialog {

  private Settings setting;
  private String[] extensions;
  private String[] filters;
  private int currentExtensionIndex;
  private int currentFilterIndex;

  private JRadioButton[] rBtnThumbList;
  private JRadioButton[] rBtnFanartList;
  private JRadioButton[] rBtnCase;
  private String[][] format = {
    {"<t>", "Matrix"}, {"<y>", "1999"}, {"<tt>", "tt0133093"},
    {"<g>", "Action | Adventure | Sci-Fi"}, {"<g1>", "Action"},
    {"<d>", "Andy Wachowski, Lana Wachowski"}, {"<d1>", "Andy Wachowski"},
    {"<rt>", "136"}, {"<ra>", "8.8"}
  };
  
  private ResourceBundle bundle;

  /** Creates new form Setting
   * @param setting
   * @param parent
   */
  public Setting(Settings setting, Component parent) {
    bundle = ResourceBundle.getBundle("fr.free.movierenamer/i18n/Bundle");
    initComponents();

    rBtnThumbList = new JRadioButton[]{this.origThumbSizeRBtn, this.midThumbSizeRBtn, this.thumbThumbSizeRBtn};
    rBtnFanartList = new JRadioButton[]{this.origFanartSizeRBtn, this.midFanartSizeRBtn, this.thumbFanartSizeRBtn};
    rBtnCase = new JRadioButton[]{this.firstLoRbtn, this.firstLaRbtn, this.upperRbtn, this.lowerRbtn};
    this.setting = setting;
    extensions = setting.extensions;
    filters = setting.nameFilters;

    loadList(extentionJlist, extensions);
    currentExtensionIndex = 0;

    extentionJlist.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {
        if (extentionJlist.getSelectedIndex() != -1)
          currentExtensionIndex = extentionJlist.getSelectedIndex();
      }
    });

    loadList(filterJlist, filters);
    currentFilterIndex = 0;
    filterJlist.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {
        if (filterJlist.getSelectedIndex() != -1)
          currentFilterIndex = filterJlist.getSelectedIndex();
      }
    });

    // General Setting
    selectFirstMovieChk.setSelected(setting.selectFrstMovie);
    selectFirstResChk.setSelected(setting.selectFrstRes);
    showNotaMovieWarnChk.setSelected(setting.showNotaMovieWarn);
    scanSubfolderChk.setSelected(setting.scanSubfolder);    
    useExtensionFilterChk.setSelected(setting.useExtensionFilter);    
//    showMovieFilePathChk.setSelected(setting.showMovieFilePath);
    movieInfoPanelChk.setSelected(setting.movieInfoPanel);
    actorImageChk.setSelected(setting.actorImage);
    thumbsChk.setSelected(setting.thumb);
    fanartsChk.setSelected(setting.fanart);
    englishRbtn.setSelected(!setting.locale.equals("fr"));
    frenchRbtn.setSelected(setting.locale.equals("fr"));

    // Rename Setting
    formatField.setText(setting.movieFilenameFormat);
    if(setting.renameCase >= caseGroup.getButtonCount())caseGroup.setSelected(rBtnCase[1].getModel(), true);
    else caseGroup.setSelected(rBtnCase[setting.renameCase].getModel(), true);


    

    thumbGroup.setSelected(rBtnThumbList[setting.thumbSize].getModel(), true);
    fanartGroup.setSelected(rBtnFanartList[setting.fanartSize].getModel(), true);
    displayAppResultCheckBox.setSelected(setting.displayApproximateResult);
    limitResultComboBox.setSelectedIndex(setting.nbResult);
    

    // Imdb
    imdbFrRbtn.setSelected(setting.imdbFr);
    imdbEnRbtn.setSelected(!setting.imdbFr);
    displayThumbResultChk.setSelected(setting.displayThumbResult);


    // Movie Files
    

    createDirChk.setSelected(setting.createMovieDirectory);
    if (createDirChk.isSelected()) {
      movieTitleRBtn.setEnabled(true);
      renamedMovieTitleRBtn.setEnabled(true);
    }
    movieTitleRBtn.setSelected(!setting.movieDirRenamedTitle);
    renamedMovieTitleRBtn.setSelected(setting.movieDirRenamedTitle);


    thumbExtCbBox.setSelectedIndex(setting.thumbExt);

    String ssize = "";
    long size = Utils.getDirSizeInMegabytes(new File(setting.thumbCacheDir));
    ssize = "" + size;
    if (size == 0)
      ssize = "0." + Utils.getDirSize(new File(setting.thumbCacheDir));
    thumbCacheLbl.setText(ssize + " Mb used for thumbnails");

    size = Utils.getDirSizeInMegabytes(new File(setting.fanartCacheDir));
    ssize = "" + size;
    if (size == 0)
      ssize = "0." + Utils.getDirSize(new File(setting.fanartCacheDir));
    fanartCacheLbl.setText(ssize + " Mb used for fanarts");

    size = Utils.getDirSizeInMegabytes(new File(setting.actorCacheDir));
    ssize = "" + size;
    if (size == 0)
      ssize = "0." + Utils.getDirSize(new File(setting.actorCacheDir));
    actorCacheLbl.setText(ssize + " Mb used for actors");

    size = Utils.getDirSizeInMegabytes(new File(setting.xmlCacheDir));
    ssize = "" + size;
    if (size == 0)
      ssize = "0." + Utils.getDirSize(new File(setting.xmlCacheDir));
    xmlLbl.setText(ssize + " Mb used for XML files");

    setTitle("Movie Renamer Settings " + setting.getVersion());
    setModal(true);
    setLocationRelativeTo(parent);
  }

  private void loadList(JList jlist, String[] array) {
    jlist.setListData(array);
    jlist.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    jlist.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    jlist.setVisibleRowCount(-1);
    jlist.setSelectedIndex(0);
  }

  public Settings getSetting(){
    return setting;
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    thumbGroup = new ButtonGroup();
    fanartGroup = new ButtonGroup();
    youtubeGroup = new ButtonGroup();
    createDirGroup = new ButtonGroup();
    ratingGroup = new ButtonGroup();
    languageGroup = new ButtonGroup();
    interfaceGroup = new ButtonGroup();
    imdbLangGroup = new ButtonGroup();
    caseGroup = new ButtonGroup();
    settingTabPan = new JTabbedPane();
    generalPnl = new JPanel();
    languagePnl = new JPanel();
    englishRbtn = new JRadioButton();
    frenchRbtn = new JRadioButton();
    lwarningLbl = new JLabel();
    updatePnl = new JPanel();
    checkUpdateChk = new JCheckBox();
    interfacePnl = new JPanel();
    selectFirstMovieChk = new JCheckBox();
    selectFirstResChk = new JCheckBox();
    actorImageChk = new JCheckBox();
    movieInfoPanelChk = new JCheckBox();
    thumbsChk = new JCheckBox();
    fanartsChk = new JCheckBox();
    useExtensionFilterChk = new JCheckBox();
    scanSubfolderChk = new JCheckBox();
    showNotaMovieWarnChk = new JCheckBox();
    renamePnl = new JPanel();
    movieFileNamePnl = new JPanel();
    movieTitleRBtn = new JRadioButton();
    renamedMovieTitleRBtn = new JRadioButton();
    createDirChk = new JCheckBox();
    jLabel4 = new JLabel();
    jButton3 = new JButton();
    testField = new JTextField();
    formatField = new JTextField();
    jLabel3 = new JLabel();
    helpBtn = new JButton();
    firstLoRbtn = new JRadioButton();
    firstLaRbtn = new JRadioButton();
    upperRbtn = new JRadioButton();
    lowerRbtn = new JRadioButton();
    caseLbl = new JLabel();
    jPanel9 = new JPanel();
    thumbExtCbBox = new JComboBox();
    jLabel6 = new JLabel();
    jPanel3 = new JPanel();
    jPanel5 = new JPanel();
    thumbThumbSizeRBtn = new JRadioButton();
    midThumbSizeRBtn = new JRadioButton();
    origThumbSizeRBtn = new JRadioButton();
    jLabel9 = new JLabel();
    jLabel10 = new JLabel();
    origFanartSizeRBtn = new JRadioButton();
    midFanartSizeRBtn = new JRadioButton();
    thumbFanartSizeRBtn = new JRadioButton();
    jPanel15 = new JPanel();
    imdbFrRbtn = new JRadioButton();
    imdbEnRbtn = new JRadioButton();
    jPanel6 = new JPanel();
    displayAppResultCheckBox = new JCheckBox();
    limitResultComboBox = new JComboBox();
    jLabel14 = new JLabel();
    helpSearchBtn = new JButton();
    displayThumbResultChk = new JCheckBox();
    filtersPnl = new JPanel();
    jPanel11 = new JPanel();
    removeExtensuionBtn = new JButton();
    addExtensionBtn = new JButton();
    jScrollPane2 = new JScrollPane();
    extentionJlist = new JList();
    helpBtn1 = new JButton();
    jPanel12 = new JPanel();
    jButton1 = new JButton();
    jButton2 = new JButton();
    jButton5 = new JButton();
    removeFilterBtn = new JButton();
    jScrollPane1 = new JScrollPane();
    filterJlist = new JList();
    helpBtn2 = new JButton();
    jPanel1 = new JPanel();
    jPanel7 = new JPanel();
    actorCacheLbl = new JLabel();
    fanartCacheLbl = new JLabel();
    thumbCacheLbl = new JLabel();
    clearBtn = new JButton();
    clearBtn1 = new JButton();
    clearBtn2 = new JButton();
    jPanel8 = new JPanel();
    xmlLbl = new JLabel();
    jButton4 = new JButton();
    jPanel16 = new JPanel();
    jLabel2 = new JLabel();
    jLabel7 = new JLabel();
    jButton6 = new JButton();
    jLabel8 = new JLabel();
    jTextField1 = new JTextField();
    saveBtn = new JButton();
    CancelBtn = new JButton();

    setTitle("Movie Renamer Settings");
    setResizable(false);

    generalPnl.setFont(new Font("Ubuntu", 1, 14));
    ResourceBundle bundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle");
    languagePnl.setBorder(BorderFactory.createTitledBorder(null, bundle.getString("language"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Ubuntu", 1, 14))); // NOI18N
    languagePnl.setToolTipText("Under development");

    languageGroup.add(englishRbtn);
    englishRbtn.setFont(new Font("Ubuntu", 0, 12));
    englishRbtn.setSelected(true);
    englishRbtn.setText(bundle.getString("english")); // NOI18N

    languageGroup.add(frenchRbtn);
    frenchRbtn.setFont(new Font("Ubuntu", 0, 12));
    frenchRbtn.setText(bundle.getString("french")); // NOI18N

    lwarningLbl.setFont(new Font("Ubuntu", 1, 12));
    lwarningLbl.setIcon(new ImageIcon(getClass().getResource("/image/dialog-warning.png"))); // NOI18N
    lwarningLbl.setText(bundle.getString("needRestart")); // NOI18N

    GroupLayout languagePnlLayout = new GroupLayout(languagePnl);
    languagePnl.setLayout(languagePnlLayout);

    languagePnlLayout.setHorizontalGroup(
      languagePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(languagePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(englishRbtn)
        .addGap(18, 18, 18)
        .addComponent(frenchRbtn)
        .addPreferredGap(ComponentPlacement.RELATED, 148, Short.MAX_VALUE)
        .addComponent(lwarningLbl)
        .addContainerGap())
    );
    languagePnlLayout.setVerticalGroup(
      languagePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(languagePnlLayout.createSequentialGroup()
        .addGroup(languagePnlLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(englishRbtn)
          .addComponent(frenchRbtn)
          .addComponent(lwarningLbl))
        .addContainerGap())
    );

    updatePnl.setBorder(BorderFactory.createTitledBorder(null, bundle.getString("update"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Ubuntu", 1, 14))); // NOI18N
    checkUpdateChk.setFont(new Font("Ubuntu", 0, 12));
    checkUpdateChk.setText(bundle.getString("chkUpdateOnStart")); // NOI18N
    checkUpdateChk.setToolTipText("You can check for update at any time, directly on movie renamer (globe button)");
    checkUpdateChk.setEnabled(false);

    GroupLayout updatePnlLayout = new GroupLayout(updatePnl);
    updatePnl.setLayout(updatePnlLayout);

    updatePnlLayout.setHorizontalGroup(
      updatePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(updatePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(checkUpdateChk)
        .addContainerGap(219, Short.MAX_VALUE))
    );
    updatePnlLayout.setVerticalGroup(
      updatePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(updatePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(checkUpdateChk)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    interfacePnl.setBorder(BorderFactory.createTitledBorder(null, bundle.getString("interface"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Ubuntu", 1, 14))); // NOI18N
    selectFirstMovieChk.setFont(new Font("Ubuntu", 0, 12));
    selectFirstMovieChk.setText(bundle.getString("autoSelFrstMovie")); // NOI18N

    selectFirstResChk.setFont(new Font("Ubuntu", 0, 12));
    selectFirstResChk.setText(bundle.getString("autoSelFrstRes")); // NOI18N

    actorImageChk.setFont(new Font("Ubuntu", 0, 12));
    actorImageChk.setText(bundle.getString("showActorImage")); // NOI18N
    actorImageChk.setEnabled(false);

    movieInfoPanelChk.setFont(new Font("Ubuntu", 0, 12));
    movieInfoPanelChk.setText(bundle.getString("showMoviePanel")); // NOI18N
    movieInfoPanelChk.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent evt) {
        movieInfoPanelChkItemStateChanged(evt);
      }
    });

    thumbsChk.setFont(new Font("Ubuntu", 0, 12));
    thumbsChk.setText(bundle.getString("showThumbs")); // NOI18N
    thumbsChk.setEnabled(false);

    fanartsChk.setFont(new Font("Ubuntu", 0, 12));
    fanartsChk.setText(bundle.getString("showFanarts")); // NOI18N
    fanartsChk.setEnabled(false);

    useExtensionFilterChk.setFont(new Font("Ubuntu", 0, 12));
    useExtensionFilterChk.setText(bundle.getString("useExtFilter")); // NOI18N

    scanSubfolderChk.setFont(new Font("Ubuntu", 0, 12));
    scanSubfolderChk.setText(bundle.getString("autoScanSubfolder")); // NOI18N

    showNotaMovieWarnChk.setFont(new Font("Ubuntu", 0, 12));
    showNotaMovieWarnChk.setSelected(true);
    showNotaMovieWarnChk.setText(bundle.getString("showNotMovieWarn")); // NOI18N

    GroupLayout interfacePnlLayout = new GroupLayout(interfacePnl);
    interfacePnl.setLayout(interfacePnlLayout);
    interfacePnlLayout.setHorizontalGroup(
      interfacePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(interfacePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(interfacePnlLayout.createParallelGroup(Alignment.LEADING)
          .addComponent(selectFirstMovieChk)
          .addComponent(selectFirstResChk)
          .addComponent(scanSubfolderChk)
          .addComponent(useExtensionFilterChk)
          .addComponent(showNotaMovieWarnChk)
          .addComponent(movieInfoPanelChk)
          .addGroup(interfacePnlLayout.createSequentialGroup()
            .addGap(21, 21, 21)
            .addGroup(interfacePnlLayout.createParallelGroup(Alignment.LEADING)
              .addComponent(thumbsChk)
              .addComponent(actorImageChk)
              .addComponent(fanartsChk))))
        .addContainerGap(143, Short.MAX_VALUE))
    );
    interfacePnlLayout.setVerticalGroup(
      interfacePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(interfacePnlLayout.createSequentialGroup()
        .addComponent(selectFirstMovieChk)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(selectFirstResChk)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(showNotaMovieWarnChk)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(scanSubfolderChk)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(useExtensionFilterChk)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(movieInfoPanelChk)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(actorImageChk)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(thumbsChk)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(fanartsChk))
    );

    GroupLayout generalPnlLayout = new GroupLayout(generalPnl);
    generalPnl.setLayout(generalPnlLayout);
    generalPnlLayout.setHorizontalGroup(
      generalPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(generalPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(generalPnlLayout.createParallelGroup(Alignment.LEADING)
          .addComponent(interfacePnl, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(updatePnl, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(languagePnl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    generalPnlLayout.setVerticalGroup(
      generalPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(generalPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(interfacePnl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(updatePnl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(languagePnl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(54, Short.MAX_VALUE))
    );

    settingTabPan.addTab(bundle.getString("general"), generalPnl); // NOI18N

    renamePnl.setFont(new Font("Ubuntu", 1, 14));

    movieFileNamePnl.setBorder(BorderFactory.createTitledBorder(null, bundle.getString("movieFileName"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Ubuntu", 1, 14))); // NOI18N

    createDirGroup.add(movieTitleRBtn);
    movieTitleRBtn.setFont(new Font("Ubuntu", 0, 12));
    movieTitleRBtn.setSelected(true);
    movieTitleRBtn.setText(bundle.getString("movieTitle")); // NOI18N
    movieTitleRBtn.setToolTipText("Ex : Matrix/");
    movieTitleRBtn.setEnabled(false);
    movieTitleRBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        movieTitleRBtnActionPerformed(evt);
      }
    });

    createDirGroup.add(renamedMovieTitleRBtn);
    renamedMovieTitleRBtn.setFont(new Font("Ubuntu", 0, 12));
    renamedMovieTitleRBtn.setText(bundle.getString("renamedMvTitle")); // NOI18N
    renamedMovieTitleRBtn.setToolTipText("Ex : Matrix (1999)/");
    renamedMovieTitleRBtn.setEnabled(false);

    createDirChk.setFont(new Font("Ubuntu", 0, 12));
    createDirChk.setText(bundle.getString("createDir")); // NOI18N
    createDirChk.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        createDirChkMouseReleased(evt);
      }
    });

    jLabel4.setFont(new Font("Ubuntu", 1, 12));
    jLabel4.setText(bundle.getString("defaultFormat")); // NOI18N

    jButton3.setFont(new Font("Ubuntu", 1, 12));
    jButton3.setText("test");
    jButton3.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        jButton3ActionPerformed(evt);
      }
    });

    testField.setEditable(false);

    formatField.setFont(new Font("Ubuntu", 0, 12));
    formatField.setText("<t> (<y>)");
    formatField.addMouseListener(new ContextMenuFieldMouseListener());

    jLabel3.setFont(new Font("Ubuntu", 1, 12));
    jLabel3.setText("Format");

    helpBtn.setIcon(new ImageIcon(getClass().getResource("/image/system-help-3.png"))); // NOI18N
    helpBtn.setToolTipText(bundle.getString("help")); // NOI18N
    helpBtn.setMaximumSize(new Dimension(26, 26));
    helpBtn.setMinimumSize(new Dimension(26, 26));
    helpBtn.setPreferredSize(new Dimension(26, 26));
    helpBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        helpBtnActionPerformed(evt);
      }
    });

    caseGroup.add(firstLoRbtn);
    firstLoRbtn.setFont(new Font("Ubuntu", 0, 12));
    firstLoRbtn.setText(bundle.getString("firstLo")); // NOI18N

    caseGroup.add(firstLaRbtn);
    firstLaRbtn.setFont(new Font("Ubuntu", 0, 12));
    firstLaRbtn.setSelected(true);
    firstLaRbtn.setText(bundle.getString("firstLa")); // NOI18N

    caseGroup.add(upperRbtn);
    upperRbtn.setFont(new Font("Ubuntu", 0, 12));
    upperRbtn.setText(bundle.getString("upper")); // NOI18N

    caseGroup.add(lowerRbtn);
    lowerRbtn.setFont(new Font("Ubuntu", 0, 12));
    lowerRbtn.setText(bundle.getString("lower")); // NOI18N

    caseLbl.setFont(new Font("Ubuntu", 1, 13));
    caseLbl.setText(bundle.getString("fileCase")); // NOI18N

    GroupLayout movieFileNamePnlLayout = new GroupLayout(movieFileNamePnl);
    movieFileNamePnl.setLayout(movieFileNamePnlLayout);

    movieFileNamePnlLayout.setHorizontalGroup(
      movieFileNamePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(movieFileNamePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(movieFileNamePnlLayout.createParallelGroup(Alignment.LEADING)
          .addComponent(jLabel4)
          .addGroup(movieFileNamePnlLayout.createSequentialGroup()
            .addComponent(jLabel3)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(formatField, GroupLayout.PREFERRED_SIZE, 304, GroupLayout.PREFERRED_SIZE))
          .addGroup(movieFileNamePnlLayout.createSequentialGroup()
            .addComponent(jButton3)
            .addPreferredGap(ComponentPlacement.UNRELATED)
            .addComponent(testField, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(helpBtn, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
      .addGroup(movieFileNamePnlLayout.createSequentialGroup()
        .addGap(17, 17, 17)
        .addComponent(movieTitleRBtn)
        .addGap(18, 18, 18)
        .addComponent(renamedMovieTitleRBtn)
        .addContainerGap(155, Short.MAX_VALUE))
      .addGroup(movieFileNamePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(movieFileNamePnlLayout.createParallelGroup(Alignment.LEADING)
          .addComponent(caseLbl)
          .addGroup(movieFileNamePnlLayout.createSequentialGroup()
            .addGap(12, 12, 12)
            .addGroup(movieFileNamePnlLayout.createParallelGroup(Alignment.LEADING)
              .addComponent(firstLaRbtn)
              .addComponent(firstLoRbtn)
              .addComponent(upperRbtn)
              .addComponent(lowerRbtn))))
        .addContainerGap(203, Short.MAX_VALUE))
      .addGroup(movieFileNamePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(createDirChk)
        .addContainerGap(282, Short.MAX_VALUE))
    );
    movieFileNamePnlLayout.setVerticalGroup(
      movieFileNamePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(movieFileNamePnlLayout.createSequentialGroup()
        .addComponent(jLabel4)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(movieFileNamePnlLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(formatField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(caseLbl)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(firstLoRbtn)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(firstLaRbtn)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(upperRbtn)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(lowerRbtn)
        .addGap(18, 18, 18)
        .addGroup(movieFileNamePnlLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(jButton3)
          .addComponent(testField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(createDirChk)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addGroup(movieFileNamePnlLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(movieTitleRBtn)
          .addComponent(renamedMovieTitleRBtn)))
      .addComponent(helpBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    );

    jPanel9.setBorder(BorderFactory.createTitledBorder(null, bundle.getString("imageExt"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Ubuntu", 1, 14))); // NOI18N
    thumbExtCbBox.setModel(new DefaultComboBoxModel(new String[] { ".jpg", ".tbn", "-thumb.jpg" }));
    thumbExtCbBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        thumbExtCbBoxActionPerformed(evt);
      }
    });

    jLabel6.setFont(new Font("Ubuntu", 1, 12));
    jLabel6.setText(bundle.getString("thumbnails")); // NOI18N

    GroupLayout jPanel9Layout = new GroupLayout(jPanel9);
    jPanel9.setLayout(jPanel9Layout);
    jPanel9Layout.setHorizontalGroup(
      jPanel9Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel9Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel6)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(thumbExtCbBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(222, Short.MAX_VALUE))
    );
    jPanel9Layout.setVerticalGroup(
      jPanel9Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel9Layout.createSequentialGroup()
        .addGroup(jPanel9Layout.createParallelGroup(Alignment.BASELINE)
          .addComponent(jLabel6)
          .addComponent(thumbExtCbBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    GroupLayout renamePnlLayout = new GroupLayout(renamePnl);
    renamePnl.setLayout(renamePnlLayout);
    renamePnlLayout.setHorizontalGroup(
      renamePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(Alignment.TRAILING, renamePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(renamePnlLayout.createParallelGroup(Alignment.TRAILING)
          .addComponent(jPanel9, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(movieFileNamePnl, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    renamePnlLayout.setVerticalGroup(
      renamePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(renamePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(movieFileNamePnl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(jPanel9, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(67, Short.MAX_VALUE))
    );

    settingTabPan.addTab(bundle.getString("rename"), renamePnl); // NOI18N

    jPanel5.setBorder(BorderFactory.createTitledBorder(null, "Images", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); // NOI18N

    thumbGroup.add(thumbThumbSizeRBtn);
    thumbThumbSizeRBtn.setText("Thumb");

    thumbGroup.add(midThumbSizeRBtn);
    midThumbSizeRBtn.setText("Medium");

    thumbGroup.add(origThumbSizeRBtn);
    origThumbSizeRBtn.setSelected(true);
    origThumbSizeRBtn.setText("Original");

    jLabel9.setFont(new Font("Ubuntu", 1, 14));
    jLabel9.setText("Thumbnails size");

    jLabel10.setFont(new Font("Ubuntu", 1, 14));
    jLabel10.setText("Fanarts size");

    fanartGroup.add(origFanartSizeRBtn);
    origFanartSizeRBtn.setSelected(true);
    origFanartSizeRBtn.setText("Original");

    fanartGroup.add(midFanartSizeRBtn);
    midFanartSizeRBtn.setText("Medium");

    fanartGroup.add(thumbFanartSizeRBtn);
    thumbFanartSizeRBtn.setText("Thumb");

    GroupLayout jPanel5Layout = new GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
      jPanel5Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel5Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel5Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(jLabel9)
          .addGroup(jPanel5Layout.createSequentialGroup()
            .addGap(12, 12, 12)
            .addGroup(jPanel5Layout.createParallelGroup(Alignment.LEADING)
              .addComponent(midThumbSizeRBtn)
              .addComponent(origThumbSizeRBtn)
              .addComponent(thumbThumbSizeRBtn))))
        .addGap(119, 119, 119)
        .addGroup(jPanel5Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(jLabel10)
          .addGroup(jPanel5Layout.createSequentialGroup()
            .addGap(12, 12, 12)
            .addGroup(jPanel5Layout.createParallelGroup(Alignment.LEADING)
              .addComponent(midFanartSizeRBtn)
              .addComponent(origFanartSizeRBtn)
              .addComponent(thumbFanartSizeRBtn))))
        .addContainerGap(90, Short.MAX_VALUE))
    );
    jPanel5Layout.setVerticalGroup(
      jPanel5Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel5Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel5Layout.createParallelGroup(Alignment.BASELINE)
          .addComponent(jLabel9)
          .addComponent(jLabel10))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addGroup(jPanel5Layout.createParallelGroup(Alignment.BASELINE)
          .addComponent(origThumbSizeRBtn)
          .addComponent(origFanartSizeRBtn))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(jPanel5Layout.createParallelGroup(Alignment.BASELINE)
          .addComponent(midThumbSizeRBtn)
          .addComponent(midFanartSizeRBtn))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(jPanel5Layout.createParallelGroup(Alignment.BASELINE)
          .addComponent(thumbThumbSizeRBtn)
          .addComponent(thumbFanartSizeRBtn))
        .addContainerGap(14, Short.MAX_VALUE))
    );

    jPanel15.setBorder(BorderFactory.createTitledBorder("Language"));

    imdbLangGroup.add(imdbFrRbtn);
    imdbFrRbtn.setSelected(true);
    imdbFrRbtn.setText("French (imdb.fr)");

    imdbLangGroup.add(imdbEnRbtn);
    imdbEnRbtn.setText("English (imdb.com)");

    GroupLayout jPanel15Layout = new GroupLayout(jPanel15);
    jPanel15.setLayout(jPanel15Layout);

    jPanel15Layout.setHorizontalGroup(
      jPanel15Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel15Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(imdbFrRbtn)
        .addGap(18, 18, 18)
        .addComponent(imdbEnRbtn)
        .addContainerGap(98, Short.MAX_VALUE))
    );
    jPanel15Layout.setVerticalGroup(
      jPanel15Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel15Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel15Layout.createParallelGroup(Alignment.BASELINE)
          .addComponent(imdbFrRbtn)
          .addComponent(imdbEnRbtn))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel6.setBorder(BorderFactory.createTitledBorder(null, "Imdb search", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Ubuntu", 1, 13))); // NOI18N
    displayAppResultCheckBox.setFont(new Font("Ubuntu", 0, 12));
    displayAppResultCheckBox.setText("Show approximate result");
    displayAppResultCheckBox.setToolTipText("Display approximate result even if some movies are found");
    displayAppResultCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        displayAppResultCheckBoxActionPerformed(evt);
      }
    });

    limitResultComboBox.setFont(new Font("Ubuntu", 1, 12));
    limitResultComboBox.setModel(new DefaultComboBoxModel(new String[] { "All", "5", "10", "15", "20", "30" }));

    jLabel14.setFont(new Font("Ubuntu", 0, 12));
    jLabel14.setText("results for each result type (Popular,Exact,...)");

    helpSearchBtn.setIcon(new ImageIcon(getClass().getResource("/image/system-help-3.png"))); // NOI18N
    helpSearchBtn.setToolTipText("Help");
    helpSearchBtn.setMaximumSize(new Dimension(26, 26));
    helpSearchBtn.setMinimumSize(new Dimension(26, 26));
    helpSearchBtn.setPreferredSize(new Dimension(26, 26));
    helpSearchBtn.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        helpSearchBtnMouseReleased(evt);
      }
    });
    helpSearchBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        helpSearchBtnActionPerformed(evt);
      }
    });

    displayThumbResultChk.setFont(new Font("Ubuntu", 0, 12));
    displayThumbResultChk.setText(bundle.getString("showImgResList")); // NOI18N
    displayThumbResultChk.setToolTipText("Displays movie thumbnails in search results");

    GroupLayout jPanel6Layout = new GroupLayout(jPanel6);
    jPanel6.setLayout(jPanel6Layout);
    jPanel6Layout.setHorizontalGroup(
      jPanel6Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel6Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel6Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(displayThumbResultChk)
          .addGroup(jPanel6Layout.createSequentialGroup()
            .addComponent(limitResultComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(jLabel14)
            .addPreferredGap(ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
            .addComponent(helpSearchBtn, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
          .addComponent(displayAppResultCheckBox))
        .addContainerGap())
    );
    jPanel6Layout.setVerticalGroup(
      jPanel6Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel6Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel6Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(helpSearchBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addGroup(jPanel6Layout.createParallelGroup(Alignment.BASELINE)
            .addComponent(jLabel14)
            .addComponent(limitResultComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(displayAppResultCheckBox)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(displayThumbResultChk)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel3Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(jPanel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jPanel15, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jPanel5, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jPanel15, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(jPanel6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(jPanel5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(88, Short.MAX_VALUE))
    );

    settingTabPan.addTab("Imdb", jPanel3);


    jPanel11.setBorder(BorderFactory.createTitledBorder(null, "Extensions", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); // NOI18N
    removeExtensuionBtn.setIcon(new ImageIcon(getClass().getResource("/image/list-remove-4.png"))); // NOI18N
    removeExtensuionBtn.setToolTipText("Remove extension");
    removeExtensuionBtn.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        removeExtensuionBtnMouseReleased(evt);
      }
    });

    addExtensionBtn.setIcon(new ImageIcon(getClass().getResource("/image/list-add-5.png"))); // NOI18N
    addExtensionBtn.setToolTipText("Add extension");
    addExtensionBtn.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        addExtensionBtnMouseReleased(evt);
      }
    });
    addExtensionBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        addExtensionBtnActionPerformed(evt);
      }
    });

    extentionJlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jScrollPane2.setViewportView(extentionJlist);

    helpBtn1.setIcon(new ImageIcon(getClass().getResource("/image/system-help-3.png"))); // NOI18N
    helpBtn1.setToolTipText("Help");
    helpBtn1.setMaximumSize(new Dimension(26, 26));
    helpBtn1.setMinimumSize(new Dimension(26, 26));
    helpBtn1.setPreferredSize(new Dimension(26, 26));
    helpBtn1.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        helpBtn1MouseReleased(evt);
      }
    });

    GroupLayout jPanel11Layout = new GroupLayout(jPanel11);
    jPanel11.setLayout(jPanel11Layout);

    jPanel11Layout.setHorizontalGroup(
      jPanel11Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel11Layout.createSequentialGroup()
        .addGroup(jPanel11Layout.createParallelGroup(Alignment.LEADING)
          .addGroup(Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(addExtensionBtn, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(removeExtensuionBtn, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE))
          .addGroup(jPanel11Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 344, GroupLayout.PREFERRED_SIZE)))
        .addGap(18, 18, 18)
        .addComponent(helpBtn1, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(18, Short.MAX_VALUE))
    );
    jPanel11Layout.setVerticalGroup(
      jPanel11Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel11Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel11Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(helpBtn1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addGroup(jPanel11Layout.createSequentialGroup()
            .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.UNRELATED)
            .addGroup(jPanel11Layout.createParallelGroup(Alignment.LEADING)
              .addComponent(addExtensionBtn, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
              .addComponent(removeExtensuionBtn, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))))
        .addContainerGap())
    );

    jPanel12.setBorder(BorderFactory.createTitledBorder(null, "Name Filters", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); // NOI18N
    jButton1.setIcon(new ImageIcon(getClass().getResource("/image/go-previous-3.png"))); // NOI18N
    jButton1.setToolTipText("Move left");

    jButton2.setIcon(new ImageIcon(getClass().getResource("/image/go-next-3.png"))); // NOI18N
    jButton2.setToolTipText("Move right");

    jButton5.setIcon(new ImageIcon(getClass().getResource("/image/list-add-5.png"))); // NOI18N
    jButton5.setToolTipText("Add filter");
    jButton5.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        jButton5ActionPerformed(evt);
      }
    });

    removeFilterBtn.setIcon(new ImageIcon(getClass().getResource("/image/list-remove-4.png"))); // NOI18N
    removeFilterBtn.setToolTipText("remove filter");
    removeFilterBtn.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        removeFilterBtnMouseReleased(evt);
      }
    });

    filterJlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jScrollPane1.setViewportView(filterJlist);

    helpBtn2.setIcon(new ImageIcon(getClass().getResource("/image/system-help-3.png"))); // NOI18N
    helpBtn2.setToolTipText("Help");
    helpBtn2.setMaximumSize(new Dimension(26, 26));
    helpBtn2.setMinimumSize(new Dimension(26, 26));
    helpBtn2.setPreferredSize(new Dimension(26, 26));

    GroupLayout jPanel12Layout = new GroupLayout(jPanel12);
    jPanel12.setLayout(jPanel12Layout);
    jPanel12Layout.setHorizontalGroup(
      jPanel12Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel12Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel12Layout.createParallelGroup(Alignment.LEADING)
          .addGroup(jPanel12Layout.createSequentialGroup()
            .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
            .addComponent(jButton5, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.UNRELATED)
            .addComponent(removeFilterBtn, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE))
          .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(helpBtn2, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    jPanel12Layout.setVerticalGroup(
      jPanel12Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel12Layout.createSequentialGroup()
        .addGroup(jPanel12Layout.createParallelGroup(Alignment.LEADING)
          .addGroup(jPanel12Layout.createSequentialGroup()
            .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(jPanel12Layout.createParallelGroup(Alignment.TRAILING)
              .addGroup(jPanel12Layout.createParallelGroup(Alignment.BASELINE)
                .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
              .addComponent(removeFilterBtn, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
              .addComponent(jButton5, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)))
          .addComponent(helpBtn2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addContainerGap(21, Short.MAX_VALUE))
    );

    GroupLayout filtersPnlLayout = new GroupLayout(filtersPnl);
    filtersPnl.setLayout(filtersPnlLayout);
    filtersPnlLayout.setHorizontalGroup(
      filtersPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(Alignment.TRAILING, filtersPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(filtersPnlLayout.createParallelGroup(Alignment.TRAILING)
          .addComponent(jPanel12, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jPanel11, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    filtersPnlLayout.setVerticalGroup(
      filtersPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(filtersPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jPanel11, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(jPanel12, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(47, Short.MAX_VALUE))
    );

    settingTabPan.addTab("Filters", filtersPnl);

    jPanel7.setBorder(BorderFactory.createTitledBorder(null, "Image Caching", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); // NOI18N

    actorCacheLbl.setText("Mb used for actors");

    fanartCacheLbl.setText("Mb used for fanarts");

    thumbCacheLbl.setText("Mb used for thuhmbnails");

    clearBtn.setIcon(new ImageIcon(getClass().getResource("/image/user-trash-full.png"))); // NOI18N
    clearBtn.setToolTipText("Clear image cache");
    clearBtn.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        clearBtnMouseReleased(evt);
      }
    });

    clearBtn1.setIcon(new ImageIcon(getClass().getResource("/image/user-trash-full.png"))); // NOI18N
    clearBtn1.setToolTipText("Clear image cache");
    clearBtn1.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        clearBtn1MouseReleased(evt);
      }
    });

    clearBtn2.setIcon(new ImageIcon(getClass().getResource("/image/user-trash-full.png"))); // NOI18N
    clearBtn2.setToolTipText("Clear image cache");
    clearBtn2.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        clearBtn2MouseReleased(evt);
      }
    });

    GroupLayout jPanel7Layout = new GroupLayout(jPanel7);
    jPanel7.setLayout(jPanel7Layout);
    jPanel7Layout.setHorizontalGroup(
      jPanel7Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel7Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel7Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(thumbCacheLbl)
          .addComponent(fanartCacheLbl)
          .addComponent(actorCacheLbl))
        .addGap(12, 12, 12)
        .addGroup(jPanel7Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(clearBtn2)
          .addComponent(clearBtn1)
          .addComponent(clearBtn))
        .addContainerGap(162, Short.MAX_VALUE))
    );
    jPanel7Layout.setVerticalGroup(
      jPanel7Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel7Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel7Layout.createParallelGroup(Alignment.LEADING)
          .addGroup(jPanel7Layout.createSequentialGroup()
            .addGap(6, 6, 6)
            .addComponent(thumbCacheLbl))
          .addComponent(clearBtn))
        .addGroup(jPanel7Layout.createParallelGroup(Alignment.LEADING)
          .addGroup(jPanel7Layout.createSequentialGroup()
            .addGap(23, 23, 23)
            .addComponent(fanartCacheLbl))
          .addGroup(jPanel7Layout.createSequentialGroup()
            .addGap(18, 18, 18)
            .addComponent(clearBtn1)))
        .addGroup(jPanel7Layout.createParallelGroup(Alignment.LEADING)
          .addGroup(jPanel7Layout.createSequentialGroup()
            .addGap(23, 23, 23)
            .addComponent(actorCacheLbl))
          .addGroup(jPanel7Layout.createSequentialGroup()
            .addGap(18, 18, 18)
            .addComponent(clearBtn2)))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel8.setBorder(BorderFactory.createTitledBorder(null, "Movie XML Files", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); // NOI18N

    xmlLbl.setText("Mb used for XML files");

    jButton4.setIcon(new ImageIcon(getClass().getResource("/image/user-trash-full.png"))); // NOI18N
    jButton4.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        jButton4MouseReleased(evt);
      }
    });

    GroupLayout jPanel8Layout = new GroupLayout(jPanel8);
    jPanel8.setLayout(jPanel8Layout);
    jPanel8Layout.setHorizontalGroup(
      jPanel8Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel8Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(xmlLbl)
        .addGap(37, 37, 37)
        .addComponent(jButton4)
        .addContainerGap(162, Short.MAX_VALUE))
    );
    jPanel8Layout.setVerticalGroup(
      jPanel8Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel8Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel8Layout.createParallelGroup(Alignment.TRAILING)
          .addComponent(jButton4)
          .addComponent(xmlLbl))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(Alignment.TRAILING)
          .addComponent(jPanel8, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jPanel7, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jPanel7, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(jPanel8, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(158, Short.MAX_VALUE))
    );

    settingTabPan.addTab("Cache", jPanel1);

    jLabel2.setText("Movie Renamer");

    jLabel7.setText("Website");

    jButton6.setText("http:/movierenamer.free.fr");
    jButton6.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        jButton6MouseReleased(evt);
      }
    });

    jLabel8.setText("Contact");

    jTextField1.setEditable(false);
    jTextField1.setText("movierenamer@free.fr");

    GroupLayout jPanel16Layout = new GroupLayout(jPanel16);
    jPanel16.setLayout(jPanel16Layout);
    jPanel16Layout.setHorizontalGroup(
      jPanel16Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel16Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel16Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(jLabel2)
          .addGroup(jPanel16Layout.createSequentialGroup()
            .addGap(12, 12, 12)
            .addGroup(jPanel16Layout.createParallelGroup(Alignment.LEADING, false)
              .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jButton6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
              .addComponent(jLabel7)
              .addComponent(jLabel8)
              .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jTextField1)))))
        .addContainerGap(192, Short.MAX_VALUE))
    );
    jPanel16Layout.setVerticalGroup(
      jPanel16Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel16Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel2)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(jLabel7)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(jButton6)
        .addGap(30, 30, 30)
        .addComponent(jLabel8)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(275, Short.MAX_VALUE))
    );

    settingTabPan.addTab("About", jPanel16);

    saveBtn.setIcon(new ImageIcon(getClass().getResource("/image/dialog-ok-2.png"))); // NOI18N
    saveBtn.setText(bundle.getString("save")); // NOI18N
    saveBtn.setToolTipText(bundle.getString("save")); // NOI18N
    saveBtn.setMargin(new Insets(2, 2, 2, 2));
    saveBtn.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        saveBtnMouseReleased(evt);
      }
    });

    CancelBtn.setIcon(new ImageIcon(getClass().getResource("/image/dialog-cancel-2.png"))); // NOI18N
    CancelBtn.setText(bundle.getString("cancel")); // NOI18N
    CancelBtn.setToolTipText(bundle.getString("cancel")); // NOI18N
    CancelBtn.setMargin(new Insets(2, 2, 2, 2));
    CancelBtn.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        CancelBtnMouseReleased(evt);
      }
    });

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
          .addComponent(settingTabPan, GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(saveBtn, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED, 289, Short.MAX_VALUE)
            .addComponent(CancelBtn, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(Alignment.LEADING)
      .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(settingTabPan, GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
          .addComponent(saveBtn, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
          .addComponent(CancelBtn, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void CancelBtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_CancelBtnMouseReleased
    setVisible(false);
  }//GEN-LAST:event_CancelBtnMouseReleased

  private void saveBtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_saveBtnMouseReleased
    boolean restartApp = false;
    
    // General Setting
//    if(setting.showMovieFilePath != showMovieFilePathChk.isSelected()) setting.interfaceChanged = true;
    if(setting.movieInfoPanel != movieInfoPanelChk.isSelected()) setting.interfaceChanged = true;
    if(setting.thumb != thumbsChk.isSelected()) setting.interfaceChanged = true;
    if(setting.fanart != fanartsChk.isSelected()) setting.interfaceChanged = true;

    setting.selectFrstMovie = selectFirstMovieChk.isSelected();
    setting.selectFrstRes = selectFirstResChk.isSelected();
    setting.showNotaMovieWarn = showNotaMovieWarnChk.isSelected();
    setting.scanSubfolder = scanSubfolderChk.isSelected();
    setting.useExtensionFilter = useExtensionFilterChk.isSelected();
//    setting.showMovieFilePath = showMovieFilePathChk.isSelected();
    setting.movieInfoPanel = movieInfoPanelChk.isSelected();
    setting.actorImage = actorImageChk.isSelected();
    setting.thumb = thumbsChk.isSelected();
    setting.fanart = fanartsChk.isSelected();

    boolean langFr = setting.locale.equals("fr");
    if(langFr != frenchRbtn.isSelected()){
      setting.locale = (frenchRbtn.isSelected() ? "fr":"en");
      int n = JOptionPane.showConfirmDialog(this, Settings.softName + " need to restart for apply language change\nWould you like to restart now ?", "Question", JOptionPane.YES_NO_OPTION);
      if(n == JOptionPane.YES_OPTION){
        restartApp = true;
      }
    }

    // Rename Setting
    for (int i = 0; i < rBtnCase.length; i++) {
      if (rBtnCase[i].isSelected())
        setting.renameCase = i;
    }
    


    for (int i = 0; i < rBtnThumbList.length; i++) {
      if (rBtnThumbList[i].isSelected())
        setting.thumbSize = i;
    }

    for (int i = 0; i < rBtnFanartList.length; i++) {
      if (rBtnFanartList[i].isSelected())
        setting.fanartSize = i;
    }

    setting.displayApproximateResult = displayAppResultCheckBox.isSelected();
    setting.nbResult = limitResultComboBox.getSelectedIndex();

    // Imdb
    setting.imdbFr = imdbFrRbtn.isSelected();
    setting.displayThumbResult = displayThumbResultChk.isSelected();

    // Movie Files
    setting.movieFilenameFormat = formatField.getText();
    setting.createMovieDirectory = createDirChk.isSelected();
    setting.thumbExt = thumbExtCbBox.getSelectedIndex();
    setting.movieDirRenamedTitle = renamedMovieTitleRBtn.isSelected();

    // Filter
    setting.extensions = extensions;
    setting.nameFilters = filters;

    setting.saveSetting();
    
    if(restartApp){
      if(!Utils.restartApplication(Main.class))JOptionPane.showMessageDialog(this, "\nUnable to restart Movie Renamer\nPlease restart it manually", "Error", JOptionPane.ERROR_MESSAGE);
      else System.exit(0);
    }
    dispose();
  }//GEN-LAST:event_saveBtnMouseReleased

  private void jButton6MouseReleased(MouseEvent evt) {//GEN-FIRST:event_jButton6MouseReleased
    try {
      Desktop.getDesktop().browse(new URI(jButton6.getText()));
    } catch (IOException ex) {
      Logger.getLogger(Setting.class.getName()).log(Level.SEVERE, null, ex);
    } catch (URISyntaxException ex) {
      Logger.getLogger(Setting.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButton6MouseReleased

  private void jButton4MouseReleased(MouseEvent evt) {//GEN-FIRST:event_jButton4MouseReleased
    Utils.deleteDirectory(new File(setting.xmlCacheDir));
    xmlLbl.setText(Utils.getDirSizeInMegabytes(new File(setting.xmlCacheDir)) + " Mb used for XML files");
}//GEN-LAST:event_jButton4MouseReleased

  private void clearBtn2MouseReleased(MouseEvent evt) {//GEN-FIRST:event_clearBtn2MouseReleased
    Utils.deleteDirectory(new File(setting.actorCacheDir));
    actorCacheLbl.setText(Utils.getDirSizeInMegabytes(new File(setting.actorCacheDir)) + " Mb use for actors");
}//GEN-LAST:event_clearBtn2MouseReleased

  private void clearBtn1MouseReleased(MouseEvent evt) {//GEN-FIRST:event_clearBtn1MouseReleased
    Utils.deleteDirectory(new File(setting.fanartCacheDir));
    fanartCacheLbl.setText(Utils.getDirSizeInMegabytes(new File(setting.fanartCacheDir)) + " Mb use for fanarts");
}//GEN-LAST:event_clearBtn1MouseReleased

  private void clearBtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_clearBtnMouseReleased
    Utils.deleteDirectory(new File(setting.thumbCacheDir));
    thumbCacheLbl.setText(Utils.getDirSizeInMegabytes(new File(setting.thumbCacheDir)) + " Mb use for thumbnails");
}//GEN-LAST:event_clearBtnMouseReleased

  private void removeFilterBtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_removeFilterBtnMouseReleased
    String[] newArray = (String[]) Utils.removeFromArray(filters, currentFilterIndex);
    if (newArray != null) {
      filters = newArray;
      loadList(filterJlist, filters);
      int pos = currentFilterIndex - 1;
      if (pos < 0)
        pos++;
      filterJlist.setSelectedIndex(pos);
      currentFilterIndex = pos;
    }
}//GEN-LAST:event_removeFilterBtnMouseReleased

  private void jButton5ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
    String s = (String) JOptionPane.showInputDialog(this, "Add", "Add Name Filter", JOptionPane.PLAIN_MESSAGE, null, null, null);

    if ((s != null) && (s.length() > 0)) {
      filters = Arrays.copyOf(filters, filters.length + 1);
      filters[filters.length - 1] = s;
      loadList(filterJlist, filters);
      filterJlist.setSelectedIndex(filters.length - 1);
      currentFilterIndex = filters.length - 1;
    }
}//GEN-LAST:event_jButton5ActionPerformed

  private void helpBtn1MouseReleased(MouseEvent evt) {//GEN-FIRST:event_helpBtn1MouseReleased
    JOptionPane.showMessageDialog(this, bundle.getString("extensionsHelp"), "Extension Filter", JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_helpBtn1MouseReleased

  private void addExtensionBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_addExtensionBtnActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_addExtensionBtnActionPerformed

  private void addExtensionBtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_addExtensionBtnMouseReleased
    String s = (String) JOptionPane.showInputDialog(this, "Add", "Add Extension filter", JOptionPane.PLAIN_MESSAGE, null, null, null);

    if ((s != null) && (s.length() > 0)) {
      extensions = Arrays.copyOf(extensions, extensions.length + 1);
      extensions[extensions.length - 1] = s;
      loadList(extentionJlist, extensions);
      extentionJlist.setSelectedIndex(extensions.length - 1);
      currentExtensionIndex = extensions.length - 1;
    }
}//GEN-LAST:event_addExtensionBtnMouseReleased

  private void removeExtensuionBtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_removeExtensuionBtnMouseReleased
    String[] newArray = (String[]) Utils.removeFromArray(extensions, currentExtensionIndex);
    if (newArray != null) {
      extensions = newArray;
      loadList(extentionJlist, extensions);
      int pos = currentExtensionIndex - 1;
      if (pos < 0)
        pos++;
      extentionJlist.setSelectedIndex(pos);
      currentExtensionIndex = pos;
    }
}//GEN-LAST:event_removeExtensuionBtnMouseReleased

  private void thumbExtCbBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_thumbExtCbBoxActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_thumbExtCbBoxActionPerformed

  private void helpBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_helpBtnActionPerformed
    JOptionPane.showMessageDialog(this, bundle.getString("movieFormatHelp"), bundle.getString("movieFileName"), JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_helpBtnActionPerformed

  private void createDirChkMouseReleased(MouseEvent evt) {//GEN-FIRST:event_createDirChkMouseReleased
    if (createDirChk.isSelected()) {
      movieTitleRBtn.setEnabled(true);
      renamedMovieTitleRBtn.setEnabled(true);
    } else {
      movieTitleRBtn.setEnabled(false);
      renamedMovieTitleRBtn.setEnabled(false);
    }
}//GEN-LAST:event_createDirChkMouseReleased

  private void movieTitleRBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_movieTitleRBtnActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_movieTitleRBtnActionPerformed

  private void helpSearchBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_helpSearchBtnActionPerformed

}//GEN-LAST:event_helpSearchBtnActionPerformed

  private void helpSearchBtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_helpSearchBtnMouseReleased
    JOptionPane.showMessageDialog(this, "The following substitutions are made in the movie filename format:\n\n"
      + "  <t> -> The movie title\n"
      + "  <tt> -> The IMDB unique 'tt01234567' number for the movie.\n"
      + "  <y> -> The year for this movie.\n"
      + "  <d> -> The list of comma sepated directors //NOT IMPLEMENTED YET\n"
      + "  <d1> -> The first director listed //NOT IMPLEMENTED YET\n"
      + "  <g> -> The list of comma sepated genres //NOT IMPLEMENTED YET\n"
      + "  <g1> -> The first genre listed //NOT IMPLEMENTED YET\n"
      + "  <rt> -> The movie runtime in minutes\n"
      + "  <ra> -> The movie imdb rating\n\n"
      + "	", "Search Setting", JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_helpSearchBtnMouseReleased

  private void displayAppResultCheckBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_displayAppResultCheckBoxActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_displayAppResultCheckBoxActionPerformed

  private void movieInfoPanelChkItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_movieInfoPanelChkItemStateChanged
    boolean activate = true;
    if(!movieInfoPanelChk.isSelected()) activate = false;
    actorImageChk.setEnabled(activate);
    thumbsChk.setEnabled(activate);
    fanartsChk.setEnabled(activate);
  }//GEN-LAST:event_movieInfoPanelChkItemStateChanged

  private void jButton3ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    String res = formatField.getText();
    String ext = "avi";
    int titleCase = 0;
    
    for (int i = 0; i < format.length; i++) {
      res = res.replaceAll(format[i][0], format[i][1]);
    }
    
    for (int i = 0; i < rBtnCase.length; i++) {
      if (rBtnCase[i].isSelected())
        titleCase = i;
    }
    switch(titleCase){
      case Utils.UPPER:
        res = res.toUpperCase() + "." + ext.toUpperCase();
        break;
      case Utils.LOWER:
        res = res.toLowerCase()+ "." + ext.toLowerCase();
        break;
      case Utils.FIRSTLO:
        res = Utils.capitalizedLetter(res, true) + "." + ext.toLowerCase();
        break;
      case Utils.FIRSTLA:
        res = Utils.capitalizedLetter(res, false) + "." + ext.toLowerCase();
        break;
      default:
        res = res + "." + ext.toLowerCase();
        break;
    }
    testField.setText(res);
  }//GEN-LAST:event_jButton3ActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private JButton CancelBtn;
  private JLabel actorCacheLbl;
  private JCheckBox actorImageChk;
  private JButton addExtensionBtn;
  private ButtonGroup caseGroup;
  private JLabel caseLbl;
  private JCheckBox checkUpdateChk;
  private JButton clearBtn;
  private JButton clearBtn1;
  private JButton clearBtn2;
  private JCheckBox createDirChk;
  private ButtonGroup createDirGroup;
  private JCheckBox displayAppResultCheckBox;
  private JCheckBox displayThumbResultChk;
  private JRadioButton englishRbtn;
  private JList extentionJlist;
  private JLabel fanartCacheLbl;
  private ButtonGroup fanartGroup;
  private JCheckBox fanartsChk;
  private JList filterJlist;
  private JPanel filtersPnl;
  private JRadioButton firstLaRbtn;
  private JRadioButton firstLoRbtn;
  private JTextField formatField;
  private JRadioButton frenchRbtn;
  private JPanel generalPnl;
  private JButton helpBtn;
  private JButton helpBtn1;
  private JButton helpBtn2;
  private JButton helpSearchBtn;
  private JRadioButton imdbEnRbtn;
  private JRadioButton imdbFrRbtn;
  private ButtonGroup imdbLangGroup;
  private ButtonGroup interfaceGroup;
  private JPanel interfacePnl;
  private JButton jButton1;
  private JButton jButton2;
  private JButton jButton3;
  private JButton jButton4;
  private JButton jButton5;
  private JButton jButton6;
  private JLabel jLabel10;
  private JLabel jLabel14;
  private JLabel jLabel2;
  private JLabel jLabel3;
  private JLabel jLabel4;
  private JLabel jLabel6;
  private JLabel jLabel7;
  private JLabel jLabel8;
  private JLabel jLabel9;
  private JPanel jPanel1;
  private JPanel jPanel11;
  private JPanel jPanel12;
  private JPanel jPanel15;
  private JPanel jPanel16;
  private JPanel jPanel3;
  private JPanel jPanel5;
  private JPanel jPanel6;
  private JPanel jPanel7;
  private JPanel jPanel8;
  private JPanel jPanel9;
  private JScrollPane jScrollPane1;
  private JScrollPane jScrollPane2;
  private JTextField jTextField1;
  private ButtonGroup languageGroup;
  private JPanel languagePnl;
  private JComboBox limitResultComboBox;
  private JRadioButton lowerRbtn;
  private JLabel lwarningLbl;
  private JRadioButton midFanartSizeRBtn;
  private JRadioButton midThumbSizeRBtn;
  private JPanel movieFileNamePnl;
  private JCheckBox movieInfoPanelChk;
  private JRadioButton movieTitleRBtn;
  private JRadioButton origFanartSizeRBtn;
  public JRadioButton origThumbSizeRBtn;
  private ButtonGroup ratingGroup;
  private JButton removeExtensuionBtn;
  private JButton removeFilterBtn;
  private JPanel renamePnl;
  private JRadioButton renamedMovieTitleRBtn;
  private JButton saveBtn;
  private JCheckBox scanSubfolderChk;
  private JCheckBox selectFirstMovieChk;
  private JCheckBox selectFirstResChk;
  private JTabbedPane settingTabPan;
  private JCheckBox showNotaMovieWarnChk;
  private JTextField testField;
  private JLabel thumbCacheLbl;
  private JComboBox thumbExtCbBox;
  private JRadioButton thumbFanartSizeRBtn;
  private ButtonGroup thumbGroup;
  private JRadioButton thumbThumbSizeRBtn;
  private JCheckBox thumbsChk;
  private JPanel updatePnl;
  private JRadioButton upperRbtn;
  private JCheckBox useExtensionFilterChk;
  private JLabel xmlLbl;
  private ButtonGroup youtubeGroup;
  // End of variables declaration//GEN-END:variables
}
