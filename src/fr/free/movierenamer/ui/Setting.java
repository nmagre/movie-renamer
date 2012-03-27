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
import java.util.Arrays;
import java.util.ResourceBundle;
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
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Class Setting , Setting dialog
 * @author Nicolas Magré
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
    {"<d>", "Andy Wachowski | Lana Wachowski"}, {"<d1>", "Andy Wachowski"},
    {"<rt>", "136"}, {"<ra>", "8.8"}
  };
  private ResourceBundle bundle;
  private String[] lafName;
  private String[] lafClass;

  /** Creates new form Setting
   * @param setting
   * @param parent 
   */
  public Setting(Settings setting, Component parent) {
    bundle = ResourceBundle.getBundle("fr.free.movierenamer/i18n/Bundle");
    setIconImage(Utils.getImageFromJAR("/image/icon-32.gif", getClass()));
    initComponents();


    UIManager.LookAndFeelInfo[] lookAndFeelInfos = UIManager.getInstalledLookAndFeels();
    lafName = new String[lookAndFeelInfos.length];
    lafClass = new String[lookAndFeelInfos.length];
    for (int i = 0; i < lookAndFeelInfos.length; i++) {
      UIManager.LookAndFeelInfo lookAndFeelInfo = lookAndFeelInfos[i];
      lafName[i] = lookAndFeelInfo.getName();
      lafClass[i] = lookAndFeelInfo.getClassName();
    }

    rBtnThumbList = new JRadioButton[]{this.origThumbSizeRBtn, this.midThumbSizeRBtn, this.thumbThumbSizeRBtn};
    rBtnFanartList = new JRadioButton[]{this.origFanartSizeRBtn, this.midFanartSizeRBtn, this.thumbFanartSizeRBtn};
    rBtnCase = new JRadioButton[]{this.firstLoRbtn, this.firstLaRbtn, this.upperRbtn, this.lowerRbtn};
    this.setting = setting;
    extensions = setting.extensions;
    filters = setting.nameFilters;

    extentionJlist.addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent lse) {
        if (extentionJlist.getSelectedIndex() != -1)
          currentExtensionIndex = extentionJlist.getSelectedIndex();
      }
    });
    loadList(extentionJlist, extensions);
    currentExtensionIndex = 0;

    filterJlist.addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent lse) {
        if (filterJlist.getSelectedIndex() != -1) {
          currentFilterIndex = filterJlist.getSelectedIndex();
          if (currentFilterIndex == 0) moveLeft.setEnabled(false);
          else moveLeft.setEnabled(true);
          if (currentFilterIndex == (filters.length - 1)) moveRight.setEnabled(false);
          else moveRight.setEnabled(true);
        } else {
          moveRight.setEnabled(false);
          moveLeft.setEnabled(false);
        }
      }
    });

    loadList(filterJlist, filters);
    currentFilterIndex = 0;

    // General Setting
    selectFirstMovieChk.setSelected(setting.selectFrstMovie);
    selectFirstResChk.setSelected(setting.selectFrstRes);
    showNotaMovieWarnChk.setSelected(setting.showNotaMovieWarn);
    scanSubfolderChk.setSelected(setting.scanSubfolder);
    useExtensionFilterChk.setSelected(setting.useExtensionFilter);
    movieInfoPanelChk.setSelected(setting.movieInfoPanel);
    actorImageChk.setSelected(setting.actorImage);
    thumbsChk.setSelected(setting.thumb);
    fanartsChk.setSelected(setting.fanart);
    autoSearchChk.setSelected(setting.autoSearchMovie);
    checkUpdateChk.setSelected(setting.checkUpdate);

    englishRbtn.setSelected(!setting.locale.equals("fr"));
    frenchRbtn.setSelected(setting.locale.equals("fr"));

    // Rename Setting
    formatField.setText(setting.movieFilenameFormat);
    if (setting.renameCase >= caseGroup.getButtonCount()) caseGroup.setSelected(rBtnCase[1].getModel(), true);
    else caseGroup.setSelected(rBtnCase[setting.renameCase].getModel(), true);

    thumbGroup.setSelected(rBtnThumbList[setting.thumbSize].getModel(), true);
    fanartGroup.setSelected(rBtnFanartList[setting.fanartSize].getModel(), true);
    displayAppResultCheckBox.setSelected(setting.displayApproximateResult);
    limitResultComboBox.setSelectedIndex(setting.nbResult);
    xbmcNFORBtn.setSelected(setting.nfoType == 0);
    mediaPortalNFORBtn.setSelected(setting.nfoType == 1);
    separatorField.setText(setting.separator);

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
    movieTitleRBtn.setSelected(setting.movieDirRenamedTitle == 0);
    renamedMovieTitleRBtn.setSelected(setting.movieDirRenamedTitle == 1);
    customFolderRBtn.setSelected(setting.movieDirRenamedTitle == 2);

    thumbExtCbBox.setSelectedIndex(setting.thumbExt);
    customFolderField.setText(setting.movieDir);

    String ssize = "";
    long size = Utils.getDirSizeInMegabytes(new File(setting.thumbCacheDir));
    ssize = "" + size;
    if (size == 0)
      ssize = "0." + Utils.getDirSize(new File(setting.thumbCacheDir));
    thumbCacheLbl.setText(ssize + bundle.getString("useForThumb"));

    size = Utils.getDirSizeInMegabytes(new File(setting.fanartCacheDir));
    ssize = "" + size;
    if (size == 0)
      ssize = "0." + Utils.getDirSize(new File(setting.fanartCacheDir));
    fanartCacheLbl.setText(ssize + bundle.getString("useForFanart"));

    size = Utils.getDirSizeInMegabytes(new File(setting.actorCacheDir));
    ssize = "" + size;
    if (size == 0)
      ssize = "0." + Utils.getDirSize(new File(setting.actorCacheDir));
    actorCacheLbl.setText(ssize + bundle.getString("useForActor"));

    size = Utils.getDirSizeInMegabytes(new File(setting.xmlCacheDir));
    ssize = "" + size;
    if (size == 0)
      ssize = "0." + Utils.getDirSize(new File(setting.xmlCacheDir));
    xmlLbl.setText(ssize + bundle.getString("useForXml"));

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

  public Settings getSetting() {
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
    createDirGroup = new ButtonGroup();
    languageGroup = new ButtonGroup();
    imdbLangGroup = new ButtonGroup();
    caseGroup = new ButtonGroup();
    nfoGroup = new ButtonGroup();
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
    scanSubfolderChk = new JCheckBox();
    showNotaMovieWarnChk = new JCheckBox();
    autoSearchChk = new JCheckBox();
    jPanel1 = new JPanel();
    xbmcNFORBtn = new JRadioButton();
    mediaPortalNFORBtn = new JRadioButton();
    renamePnl = new JPanel();
    movieFileNamePnl = new JPanel();
    movieTitleRBtn = new JRadioButton();
    renamedMovieTitleRBtn = new JRadioButton();
    createDirChk = new JCheckBox();
    defaultFormatLbl = new JLabel();
    testBtn = new JButton();
    testField = new JTextField();
    formatField = new JTextField();
    formatLbl = new JLabel();
    helpBtn = new JButton();
    firstLoRbtn = new JRadioButton();
    firstLaRbtn = new JRadioButton();
    upperRbtn = new JRadioButton();
    lowerRbtn = new JRadioButton();
    caseLbl = new JLabel();
    customFolderRBtn = new JRadioButton();
    customFolderField = new JTextField();
    separatorLbl = new JLabel();
    separatorField = new JTextField();
    movieImagePnl = new JPanel();
    thumbExtCbBox = new JComboBox();
    thumnailsExtLbl = new JLabel();
    SearchPnl = new JPanel();
    imagesPnl = new JPanel();
    thumbThumbSizeRBtn = new JRadioButton();
    midThumbSizeRBtn = new JRadioButton();
    origThumbSizeRBtn = new JRadioButton();
    thumbSzeLbl = new JLabel();
    fanartSizeLbl = new JLabel();
    origFanartSizeRBtn = new JRadioButton();
    midFanartSizeRBtn = new JRadioButton();
    thumbFanartSizeRBtn = new JRadioButton();
    imdbLangPnl = new JPanel();
    imdbFrRbtn = new JRadioButton();
    imdbEnRbtn = new JRadioButton();
    imdbSearchPnl = new JPanel();
    displayAppResultCheckBox = new JCheckBox();
    limitResultComboBox = new JComboBox();
    limitResultLbl = new JLabel();
    displayThumbResultChk = new JCheckBox();
    filtersPnl = new JPanel();
    extensionPnl = new JPanel();
    removeExtensuionBtn = new JButton();
    addExtensionBtn = new JButton();
    extensionScrollP = new JScrollPane();
    extentionJlist = new JList();
    extensionHelp = new JButton();
    useExtensionFilterChk = new JCheckBox();
    fileNameFilterPnl = new JPanel();
    moveLeft = new JButton();
    moveRight = new JButton();
    addFilter = new JButton();
    removeFilterBtn = new JButton();
    filterScrollP = new JScrollPane();
    filterJlist = new JList();
    filenameFilterHelp = new JButton();
    cachePnl = new JPanel();
    imagePnl = new JPanel();
    actorCacheLbl = new JLabel();
    fanartCacheLbl = new JLabel();
    thumbCacheLbl = new JLabel();
    clearThumbBtn = new JButton();
    clearFanartBtn = new JButton();
    clearActorBtn = new JButton();
    xmlFilePnl = new JPanel();
    xmlLbl = new JLabel();
    clearXmlBtn = new JButton();
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
        .addPreferredGap(ComponentPlacement.RELATED, 146, Short.MAX_VALUE)
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

    GroupLayout updatePnlLayout = new GroupLayout(updatePnl);
    updatePnl.setLayout(updatePnlLayout);

    updatePnlLayout.setHorizontalGroup(
      updatePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(updatePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(checkUpdateChk)
        .addContainerGap(217, Short.MAX_VALUE))
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
    selectFirstMovieChk.setToolTipText(bundle.getString("autoSelFrstMovieTt")); // NOI18N

    selectFirstResChk.setFont(new Font("Ubuntu", 0, 12));
    selectFirstResChk.setText(bundle.getString("autoSelFrstRes")); // NOI18N
    selectFirstResChk.setToolTipText(bundle.getString("autoSelFrstResTt")); // NOI18N

    actorImageChk.setFont(new Font("Ubuntu", 0, 12));
    actorImageChk.setText(bundle.getString("showActorImage")); // NOI18N
    actorImageChk.setToolTipText(bundle.getString("showActorImageTt")); // NOI18N
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
    thumbsChk.setToolTipText(bundle.getString("showThumbsTt")); // NOI18N
    thumbsChk.setEnabled(false);

    fanartsChk.setFont(new Font("Ubuntu", 0, 12));
    fanartsChk.setText(bundle.getString("showFanarts")); // NOI18N
    fanartsChk.setToolTipText(bundle.getString("showFanartsTt")); // NOI18N
    fanartsChk.setEnabled(false);

    scanSubfolderChk.setFont(new Font("Ubuntu", 0, 12));
    scanSubfolderChk.setText(bundle.getString("autoScanSubfolder")); // NOI18N
    scanSubfolderChk.setToolTipText(bundle.getString("autoScanSubfolderTt")); // NOI18N

    showNotaMovieWarnChk.setFont(new Font("Ubuntu", 0, 12));
    showNotaMovieWarnChk.setText(bundle.getString("showNotMovieWarn")); // NOI18N
    showNotaMovieWarnChk.setToolTipText(bundle.getString("showNotMovieWarnTt")); // NOI18N

    autoSearchChk.setFont(new Font("Ubuntu", 0, 12));
    autoSearchChk.setText(bundle.getString("autoSearch")); // NOI18N
    autoSearchChk.setToolTipText(bundle.getString("autoSearchTt")); // NOI18N

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
          .addComponent(movieInfoPanelChk)
          .addGroup(interfacePnlLayout.createSequentialGroup()
            .addGap(21, 21, 21)
            .addGroup(interfacePnlLayout.createParallelGroup(Alignment.LEADING)
              .addComponent(thumbsChk)
              .addComponent(actorImageChk)
              .addComponent(fanartsChk)))
          .addComponent(showNotaMovieWarnChk)
          .addComponent(autoSearchChk))
        .addContainerGap(141, Short.MAX_VALUE))
    );
    interfacePnlLayout.setVerticalGroup(
      interfacePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(interfacePnlLayout.createSequentialGroup()
        .addComponent(selectFirstMovieChk)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(selectFirstResChk)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(autoSearchChk)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(scanSubfolderChk)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(showNotaMovieWarnChk)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(movieInfoPanelChk)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(actorImageChk)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(thumbsChk)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(fanartsChk))
    );

    jPanel1.setBorder(BorderFactory.createTitledBorder("NFO"));

    nfoGroup.add(xbmcNFORBtn);
    xbmcNFORBtn.setText(bundle.getString("nfoXbmc")); // NOI18N

    nfoGroup.add(mediaPortalNFORBtn);
    mediaPortalNFORBtn.setText(bundle.getString("nfoMediaPortal")); // NOI18N

    GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(xbmcNFORBtn)
        .addGap(18, 18, 18)
        .addComponent(mediaPortalNFORBtn)
        .addContainerGap(151, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
          .addComponent(xbmcNFORBtn)
          .addComponent(mediaPortalNFORBtn))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    GroupLayout generalPnlLayout = new GroupLayout(generalPnl);
    generalPnl.setLayout(generalPnlLayout);
    generalPnlLayout.setHorizontalGroup(
      generalPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(generalPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(generalPnlLayout.createParallelGroup(Alignment.LEADING)
          .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(interfacePnl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(updatePnl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(languagePnl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    generalPnlLayout.setVerticalGroup(
      generalPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(generalPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(interfacePnl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(updatePnl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(languagePnl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    defaultFormatLbl.setFont(new Font("Ubuntu", 1, 12));
    defaultFormatLbl.setText(bundle.getString("defaultFormat")); // NOI18N

    testBtn.setFont(new Font("Ubuntu", 1, 12));
    testBtn.setText("test");
    testBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        testBtnActionPerformed(evt);
      }
    });

    testField.setEditable(false);

    formatField.setFont(new Font("Ubuntu", 0, 12)); // NOI18N
    formatField.setText("<t> (<y>)");
    formatField.addMouseListener(new ContextMenuFieldMouseListener());

    formatLbl.setFont(new Font("Ubuntu", 1, 12));
    formatLbl.setText("Format");

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

    createDirGroup.add(customFolderRBtn);
    customFolderRBtn.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent evt) {
        customFolderRBtnItemStateChanged(evt);
      }
    });

    customFolderField.setEnabled(false);

    separatorLbl.setFont(new Font("Ubuntu", 1, 12)); // NOI18N
    separatorLbl.setText("Separator");

    separatorField.setFont(new Font("Ubuntu", 0, 12)); // NOI18N
    separatorField.setText(",");

    GroupLayout movieFileNamePnlLayout = new GroupLayout(movieFileNamePnl);
    movieFileNamePnl.setLayout(movieFileNamePnlLayout);

    movieFileNamePnlLayout.setHorizontalGroup(
      movieFileNamePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(movieFileNamePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(movieFileNamePnlLayout.createParallelGroup(Alignment.LEADING)
          .addGroup(movieFileNamePnlLayout.createSequentialGroup()
            .addComponent(separatorLbl)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(separatorField, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE))
          .addGroup(movieFileNamePnlLayout.createSequentialGroup()
            .addGap(12, 12, 12)
            .addGroup(movieFileNamePnlLayout.createParallelGroup(Alignment.LEADING)
              .addComponent(firstLaRbtn)
              .addComponent(firstLoRbtn)
              .addComponent(upperRbtn)
              .addComponent(lowerRbtn))))
        .addContainerGap(201, Short.MAX_VALUE))
      .addGroup(movieFileNamePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(createDirChk)
        .addContainerGap(280, Short.MAX_VALUE))
      .addGroup(Alignment.TRAILING, movieFileNamePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(movieFileNamePnlLayout.createParallelGroup(Alignment.LEADING)
          .addComponent(defaultFormatLbl)
          .addGroup(movieFileNamePnlLayout.createSequentialGroup()
            .addComponent(formatLbl)
            .addGap(28, 28, 28)
            .addComponent(formatField, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
            .addGap(14, 14, 14))
          .addGroup(movieFileNamePnlLayout.createSequentialGroup()
            .addComponent(testBtn)
            .addPreferredGap(ComponentPlacement.UNRELATED)
            .addComponent(testField, GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(helpBtn, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
      .addGroup(movieFileNamePnlLayout.createSequentialGroup()
        .addGap(17, 17, 17)
        .addComponent(movieTitleRBtn)
        .addGap(18, 18, 18)
        .addComponent(renamedMovieTitleRBtn)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(customFolderRBtn)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(customFolderField, GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
        .addContainerGap())
      .addGroup(movieFileNamePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(caseLbl)
        .addContainerGap(367, Short.MAX_VALUE))
    );
    movieFileNamePnlLayout.setVerticalGroup(
      movieFileNamePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(movieFileNamePnlLayout.createSequentialGroup()
        .addGroup(movieFileNamePnlLayout.createParallelGroup(Alignment.LEADING)
          .addGroup(movieFileNamePnlLayout.createSequentialGroup()
            .addComponent(defaultFormatLbl)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(movieFileNamePnlLayout.createParallelGroup(Alignment.BASELINE)
              .addComponent(formatLbl)
              .addComponent(formatField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(movieFileNamePnlLayout.createParallelGroup(Alignment.BASELINE)
              .addComponent(separatorField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addComponent(separatorLbl))
            .addGap(7, 7, 7)
            .addComponent(caseLbl)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(firstLoRbtn)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(firstLaRbtn)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(upperRbtn)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(lowerRbtn)
            .addGap(18, 18, 18)
            .addGroup(movieFileNamePnlLayout.createParallelGroup(Alignment.BASELINE)
              .addComponent(testBtn)
              .addComponent(testField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
          .addComponent(helpBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addGroup(movieFileNamePnlLayout.createParallelGroup(Alignment.TRAILING)
          .addGroup(movieFileNamePnlLayout.createSequentialGroup()
            .addComponent(createDirChk)
            .addPreferredGap(ComponentPlacement.UNRELATED)
            .addGroup(movieFileNamePnlLayout.createParallelGroup(Alignment.BASELINE)
              .addComponent(movieTitleRBtn)
              .addComponent(renamedMovieTitleRBtn)
              .addComponent(customFolderRBtn)))
          .addComponent(customFolderField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    movieImagePnl.setBorder(BorderFactory.createTitledBorder(null, bundle.getString("imageExt"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Ubuntu", 1, 14))); // NOI18N
    thumbExtCbBox.setModel(new DefaultComboBoxModel(new String[] { ".jpg", ".tbn", "-thumb.jpg" }));

    thumnailsExtLbl.setFont(new Font("Ubuntu", 1, 12));
    thumnailsExtLbl.setText(bundle.getString("thumbnails")); // NOI18N

    GroupLayout movieImagePnlLayout = new GroupLayout(movieImagePnl);
    movieImagePnl.setLayout(movieImagePnlLayout);
    movieImagePnlLayout.setHorizontalGroup(
      movieImagePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(movieImagePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(thumnailsExtLbl)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(thumbExtCbBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(220, Short.MAX_VALUE))
    );
    movieImagePnlLayout.setVerticalGroup(
      movieImagePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(movieImagePnlLayout.createSequentialGroup()
        .addGroup(movieImagePnlLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(thumnailsExtLbl)
          .addComponent(thumbExtCbBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    GroupLayout renamePnlLayout = new GroupLayout(renamePnl);
    renamePnl.setLayout(renamePnlLayout);
    renamePnlLayout.setHorizontalGroup(
      renamePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(renamePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(renamePnlLayout.createParallelGroup(Alignment.LEADING)
          .addComponent(movieFileNamePnl, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(movieImagePnl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    renamePnlLayout.setVerticalGroup(
      renamePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(renamePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(movieFileNamePnl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(movieImagePnl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(69, Short.MAX_VALUE))
    );

    settingTabPan.addTab(bundle.getString("rename"), renamePnl); // NOI18N

    imagesPnl.setBorder(BorderFactory.createTitledBorder(null, "Image", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); // NOI18N

    thumbGroup.add(thumbThumbSizeRBtn);
    thumbThumbSizeRBtn.setText(bundle.getString("small")); // NOI18N

    thumbGroup.add(midThumbSizeRBtn);
    midThumbSizeRBtn.setText(bundle.getString("medium")); // NOI18N

    thumbGroup.add(origThumbSizeRBtn);
    origThumbSizeRBtn.setSelected(true);
    origThumbSizeRBtn.setText("Original");

    thumbSzeLbl.setFont(new Font("Ubuntu", 1, 14));
    thumbSzeLbl.setText(bundle.getString("thumbsSize")); // NOI18N

    fanartSizeLbl.setFont(new Font("Ubuntu", 1, 14));
    fanartSizeLbl.setText(bundle.getString("fanartsSize")); // NOI18N

    fanartGroup.add(origFanartSizeRBtn);
    origFanartSizeRBtn.setSelected(true);
    origFanartSizeRBtn.setText("Original");

    fanartGroup.add(midFanartSizeRBtn);
    midFanartSizeRBtn.setText(bundle.getString("medium")); // NOI18N

    fanartGroup.add(thumbFanartSizeRBtn);
    thumbFanartSizeRBtn.setText(bundle.getString("small")); // NOI18N

    GroupLayout imagesPnlLayout = new GroupLayout(imagesPnl);
    imagesPnl.setLayout(imagesPnlLayout);
    imagesPnlLayout.setHorizontalGroup(
      imagesPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(imagesPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(imagesPnlLayout.createParallelGroup(Alignment.LEADING)
          .addComponent(thumbSzeLbl)
          .addGroup(imagesPnlLayout.createSequentialGroup()
            .addGap(12, 12, 12)
            .addGroup(imagesPnlLayout.createParallelGroup(Alignment.LEADING)
              .addComponent(midThumbSizeRBtn)
              .addComponent(origThumbSizeRBtn)
              .addComponent(thumbThumbSizeRBtn))))
        .addGap(119, 119, 119)
        .addGroup(imagesPnlLayout.createParallelGroup(Alignment.LEADING)
          .addComponent(fanartSizeLbl)
          .addGroup(imagesPnlLayout.createSequentialGroup()
            .addGap(12, 12, 12)
            .addGroup(imagesPnlLayout.createParallelGroup(Alignment.LEADING)
              .addComponent(midFanartSizeRBtn)
              .addComponent(origFanartSizeRBtn)
              .addComponent(thumbFanartSizeRBtn))))
        .addContainerGap(73, Short.MAX_VALUE))
    );
    imagesPnlLayout.setVerticalGroup(
      imagesPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(imagesPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(imagesPnlLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(thumbSzeLbl)
          .addComponent(fanartSizeLbl))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addGroup(imagesPnlLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(origThumbSizeRBtn)
          .addComponent(origFanartSizeRBtn))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(imagesPnlLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(midThumbSizeRBtn)
          .addComponent(midFanartSizeRBtn))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(imagesPnlLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(thumbThumbSizeRBtn)
          .addComponent(thumbFanartSizeRBtn))
        .addContainerGap(14, Short.MAX_VALUE))
    );

    imdbLangPnl.setBorder(BorderFactory.createTitledBorder(bundle.getString("language"))); // NOI18N

    imdbLangGroup.add(imdbFrRbtn);
    imdbFrRbtn.setSelected(true);
    imdbFrRbtn.setText(bundle.getString("imdbFr")); // NOI18N

    imdbLangGroup.add(imdbEnRbtn);
    imdbEnRbtn.setText(bundle.getString("imdbEn")); // NOI18N

    GroupLayout imdbLangPnlLayout = new GroupLayout(imdbLangPnl);
    imdbLangPnl.setLayout(imdbLangPnlLayout);

    imdbLangPnlLayout.setHorizontalGroup(
      imdbLangPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(imdbLangPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(imdbFrRbtn)
        .addGap(18, 18, 18)
        .addComponent(imdbEnRbtn)
        .addContainerGap(96, Short.MAX_VALUE))
    );
    imdbLangPnlLayout.setVerticalGroup(
      imdbLangPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(imdbLangPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(imdbLangPnlLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(imdbFrRbtn)
          .addComponent(imdbEnRbtn))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    imdbSearchPnl.setBorder(BorderFactory.createTitledBorder(null, bundle.getString("result"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Ubuntu", 1, 13))); // NOI18N
    displayAppResultCheckBox.setFont(new Font("Ubuntu", 0, 12));
    displayAppResultCheckBox.setText(bundle.getString("showAppRes")); // NOI18N
    displayAppResultCheckBox.setToolTipText(bundle.getString("showAppRestt")); // NOI18N

    limitResultComboBox.setFont(new Font("Ubuntu", 1, 12));
    limitResultComboBox.setModel(new DefaultComboBoxModel(new String[] { bundle.getString("all"), "5", "10", "15", "20", "30" }));

    limitResultLbl.setFont(new Font("Ubuntu", 0, 12));
    limitResultLbl.setText(bundle.getString("resForEachType")); // NOI18N

    displayThumbResultChk.setFont(new Font("Ubuntu", 0, 12));
    displayThumbResultChk.setText(bundle.getString("showImgResList")); // NOI18N

    GroupLayout imdbSearchPnlLayout = new GroupLayout(imdbSearchPnl);
    imdbSearchPnl.setLayout(imdbSearchPnlLayout);
    imdbSearchPnlLayout.setHorizontalGroup(
      imdbSearchPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(imdbSearchPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(imdbSearchPnlLayout.createParallelGroup(Alignment.LEADING)
          .addComponent(displayThumbResultChk)
          .addGroup(imdbSearchPnlLayout.createSequentialGroup()
            .addComponent(limitResultComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(limitResultLbl))
          .addComponent(displayAppResultCheckBox))
        .addContainerGap(106, Short.MAX_VALUE))
    );
    imdbSearchPnlLayout.setVerticalGroup(
      imdbSearchPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(imdbSearchPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(imdbSearchPnlLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(limitResultLbl)
          .addComponent(limitResultComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addGap(4, 4, 4)
        .addComponent(displayAppResultCheckBox)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(displayThumbResultChk)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    GroupLayout SearchPnlLayout = new GroupLayout(SearchPnl);
    SearchPnl.setLayout(SearchPnlLayout);
    SearchPnlLayout.setHorizontalGroup(
      SearchPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(SearchPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(SearchPnlLayout.createParallelGroup(Alignment.LEADING)
          .addComponent(imdbSearchPnl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(imdbLangPnl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(imagesPnl, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    SearchPnlLayout.setVerticalGroup(
      SearchPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(SearchPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(imdbLangPnl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(imdbSearchPnl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(imagesPnl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(116, Short.MAX_VALUE))
    );

    settingTabPan.addTab(bundle.getString("searchTitle"), SearchPnl); // NOI18N


    extensionPnl.setBorder(BorderFactory.createTitledBorder(null, "Extension", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); // NOI18N
    removeExtensuionBtn.setIcon(new ImageIcon(getClass().getResource("/image/list-remove-4.png"))); // NOI18N
    removeExtensuionBtn.setToolTipText(bundle.getString("removeExt")); // NOI18N
    removeExtensuionBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        removeExtensuionBtnActionPerformed(evt);
      }
    });

    addExtensionBtn.setIcon(new ImageIcon(getClass().getResource("/image/list-add-5.png"))); // NOI18N
    addExtensionBtn.setToolTipText(bundle.getString("addExt")); // NOI18N
    addExtensionBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        addExtensionBtnActionPerformed(evt);
      }
    });

    extentionJlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    extensionScrollP.setViewportView(extentionJlist);

    extensionHelp.setIcon(new ImageIcon(getClass().getResource("/image/system-help-3.png"))); // NOI18N
    extensionHelp.setToolTipText(bundle.getString("help")); // NOI18N
    extensionHelp.setMaximumSize(new Dimension(26, 26));
    extensionHelp.setMinimumSize(new Dimension(26, 26));
    extensionHelp.setPreferredSize(new Dimension(26, 26));
    extensionHelp.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        extensionHelpActionPerformed(evt);
      }
    });

    useExtensionFilterChk.setFont(new Font("Ubuntu", 0, 12));
    useExtensionFilterChk.setText(bundle.getString("useExtFilter")); // NOI18N

    GroupLayout extensionPnlLayout = new GroupLayout(extensionPnl);
    extensionPnl.setLayout(extensionPnlLayout);

    extensionPnlLayout.setHorizontalGroup(
      extensionPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(extensionPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(extensionPnlLayout.createParallelGroup(Alignment.LEADING)
          .addGroup(Alignment.TRAILING, extensionPnlLayout.createSequentialGroup()
            .addComponent(addExtensionBtn, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(removeExtensuionBtn, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE))
          .addComponent(extensionScrollP, GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
          .addComponent(useExtensionFilterChk))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(extensionHelp, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    extensionPnlLayout.setVerticalGroup(
      extensionPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(extensionPnlLayout.createSequentialGroup()
        .addGroup(extensionPnlLayout.createParallelGroup(Alignment.LEADING)
          .addGroup(extensionPnlLayout.createSequentialGroup()
            .addContainerGap()
            .addComponent(useExtensionFilterChk)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(extensionScrollP, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(extensionPnlLayout.createParallelGroup(Alignment.LEADING)
              .addComponent(addExtensionBtn, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
              .addComponent(removeExtensuionBtn, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)))
          .addComponent(extensionHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    fileNameFilterPnl.setBorder(BorderFactory.createTitledBorder(null, bundle.getString("movieFileNameFilter"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); // NOI18N
    moveLeft.setIcon(new ImageIcon(getClass().getResource("/image/go-previous-3.png"))); // NOI18N
    moveLeft.setToolTipText(bundle.getString("moveLeft")); // NOI18N
    moveLeft.setEnabled(false);
    moveLeft.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        moveLeftActionPerformed(evt);
      }
    });

    moveRight.setIcon(new ImageIcon(getClass().getResource("/image/go-next-3.png"))); // NOI18N
    moveRight.setToolTipText(bundle.getString("moveRight")); // NOI18N
    moveRight.setEnabled(false);
    moveRight.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        moveRightActionPerformed(evt);
      }
    });

    addFilter.setIcon(new ImageIcon(getClass().getResource("/image/list-add-5.png"))); // NOI18N
    addFilter.setToolTipText(bundle.getString("addFilter")); // NOI18N
    addFilter.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        addFilterActionPerformed(evt);
      }
    });

    removeFilterBtn.setIcon(new ImageIcon(getClass().getResource("/image/list-remove-4.png"))); // NOI18N
    removeFilterBtn.setToolTipText(bundle.getString("removeFilter")); // NOI18N
    removeFilterBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        removeFilterBtnActionPerformed(evt);
      }
    });

    filterJlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    filterScrollP.setViewportView(filterJlist);

    filenameFilterHelp.setIcon(new ImageIcon(getClass().getResource("/image/system-help-3.png"))); // NOI18N
    filenameFilterHelp.setToolTipText(bundle.getString("help")); // NOI18N
    filenameFilterHelp.setMaximumSize(new Dimension(26, 26));
    filenameFilterHelp.setMinimumSize(new Dimension(26, 26));
    filenameFilterHelp.setPreferredSize(new Dimension(26, 26));
    filenameFilterHelp.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        filenameFilterHelpActionPerformed(evt);
      }
    });

    GroupLayout fileNameFilterPnlLayout = new GroupLayout(fileNameFilterPnl);
    fileNameFilterPnl.setLayout(fileNameFilterPnlLayout);
    fileNameFilterPnlLayout.setHorizontalGroup(
      fileNameFilterPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(fileNameFilterPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(fileNameFilterPnlLayout.createParallelGroup(Alignment.LEADING)
          .addGroup(fileNameFilterPnlLayout.createSequentialGroup()
            .addComponent(moveLeft, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(moveRight, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(addFilter, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(removeFilterBtn, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
            .addGap(12, 12, 12))
          .addGroup(fileNameFilterPnlLayout.createSequentialGroup()
            .addComponent(filterScrollP, GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
            .addPreferredGap(ComponentPlacement.UNRELATED)))
        .addComponent(filenameFilterHelp, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    fileNameFilterPnlLayout.setVerticalGroup(
      fileNameFilterPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(fileNameFilterPnlLayout.createSequentialGroup()
        .addGroup(fileNameFilterPnlLayout.createParallelGroup(Alignment.LEADING)
          .addGroup(fileNameFilterPnlLayout.createSequentialGroup()
            .addComponent(filterScrollP, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(fileNameFilterPnlLayout.createParallelGroup(Alignment.TRAILING)
              .addGroup(fileNameFilterPnlLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(moveLeft, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                .addComponent(moveRight, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
              .addComponent(removeFilterBtn, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
              .addComponent(addFilter, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)))
          .addComponent(filenameFilterHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addGap(21, 21, 21))
    );

    GroupLayout filtersPnlLayout = new GroupLayout(filtersPnl);
    filtersPnl.setLayout(filtersPnlLayout);
    filtersPnlLayout.setHorizontalGroup(
      filtersPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(Alignment.TRAILING, filtersPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(filtersPnlLayout.createParallelGroup(Alignment.TRAILING)
          .addComponent(fileNameFilterPnl, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(extensionPnl, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    filtersPnlLayout.setVerticalGroup(
      filtersPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(filtersPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(extensionPnl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(fileNameFilterPnl, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(60, Short.MAX_VALUE))
    );

    settingTabPan.addTab(bundle.getString("filter"), filtersPnl); // NOI18N

    imagePnl.setBorder(BorderFactory.createTitledBorder(null, "Image", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); // NOI18N

    actorCacheLbl.setText(bundle.getString("useForActor")); // NOI18N

    fanartCacheLbl.setText(bundle.getString("useForFanart")); // NOI18N

    thumbCacheLbl.setText(bundle.getString("useForThumb")); // NOI18N

    clearThumbBtn.setIcon(new ImageIcon(getClass().getResource("/image/user-trash-full.png"))); // NOI18N
    clearThumbBtn.setToolTipText(bundle.getString("clearThumbCache")); // NOI18N
    clearThumbBtn.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        clearThumbBtnMouseReleased(evt);
      }
    });

    clearFanartBtn.setIcon(new ImageIcon(getClass().getResource("/image/user-trash-full.png"))); // NOI18N
    clearFanartBtn.setToolTipText(bundle.getString("clearFanartCache")); // NOI18N
    clearFanartBtn.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        clearFanartBtnMouseReleased(evt);
      }
    });

    clearActorBtn.setIcon(new ImageIcon(getClass().getResource("/image/user-trash-full.png"))); // NOI18N
    clearActorBtn.setToolTipText(bundle.getString("clearActorCache")); // NOI18N
    clearActorBtn.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        clearActorBtnMouseReleased(evt);
      }
    });

    GroupLayout imagePnlLayout = new GroupLayout(imagePnl);
    imagePnl.setLayout(imagePnlLayout);
    imagePnlLayout.setHorizontalGroup(
      imagePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(imagePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(imagePnlLayout.createParallelGroup(Alignment.LEADING)
          .addComponent(thumbCacheLbl)
          .addComponent(fanartCacheLbl)
          .addComponent(actorCacheLbl))
        .addPreferredGap(ComponentPlacement.RELATED, 175, Short.MAX_VALUE)
        .addGroup(imagePnlLayout.createParallelGroup(Alignment.LEADING, false)
          .addComponent(clearActorBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(clearFanartBtn, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(clearThumbBtn, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    imagePnlLayout.setVerticalGroup(
      imagePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(imagePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(imagePnlLayout.createParallelGroup(Alignment.TRAILING)
          .addComponent(thumbCacheLbl)
          .addComponent(clearThumbBtn))
        .addGap(18, 18, 18)
        .addGroup(imagePnlLayout.createParallelGroup(Alignment.TRAILING)
          .addComponent(fanartCacheLbl)
          .addComponent(clearFanartBtn))
        .addGap(18, 18, 18)
        .addGroup(imagePnlLayout.createParallelGroup(Alignment.TRAILING)
          .addComponent(actorCacheLbl)
          .addComponent(clearActorBtn))
        .addContainerGap())
    );

    xmlFilePnl.setBorder(BorderFactory.createTitledBorder(null, bundle.getString("xmlFiles"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); // NOI18N

    xmlLbl.setText(bundle.getString("useForXml")); // NOI18N

    clearXmlBtn.setIcon(new ImageIcon(getClass().getResource("/image/user-trash-full.png"))); // NOI18N
    clearXmlBtn.setToolTipText(bundle.getString("clearXmlCache")); // NOI18N
    clearXmlBtn.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        clearXmlBtnMouseReleased(evt);
      }
    });

    GroupLayout xmlFilePnlLayout = new GroupLayout(xmlFilePnl);
    xmlFilePnl.setLayout(xmlFilePnlLayout);
    xmlFilePnlLayout.setHorizontalGroup(
      xmlFilePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(xmlFilePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(xmlLbl)
        .addPreferredGap(ComponentPlacement.RELATED, 191, Short.MAX_VALUE)
        .addComponent(clearXmlBtn)
        .addContainerGap())
    );
    xmlFilePnlLayout.setVerticalGroup(
      xmlFilePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(xmlFilePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(xmlFilePnlLayout.createParallelGroup(Alignment.TRAILING)
          .addComponent(clearXmlBtn)
          .addComponent(xmlLbl))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    GroupLayout cachePnlLayout = new GroupLayout(cachePnl);
    cachePnl.setLayout(cachePnlLayout);
    cachePnlLayout.setHorizontalGroup(
      cachePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(cachePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(cachePnlLayout.createParallelGroup(Alignment.LEADING)
          .addComponent(imagePnl, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(xmlFilePnl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    cachePnlLayout.setVerticalGroup(
      cachePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(cachePnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(imagePnl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(xmlFilePnl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(210, Short.MAX_VALUE))
    );

    settingTabPan.addTab("Cache", cachePnl);

    saveBtn.setIcon(new ImageIcon(getClass().getResource("/image/dialog-ok-2.png"))); // NOI18N
    saveBtn.setText(bundle.getString("save")); // NOI18N
    saveBtn.setToolTipText(bundle.getString("save")); // NOI18N
    saveBtn.setMargin(new Insets(2, 2, 2, 2));
    saveBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        saveBtnActionPerformed(evt);
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
      .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(Alignment.TRAILING)
          .addComponent(settingTabPan, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(saveBtn, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED, 287, Short.MAX_VALUE)
            .addComponent(CancelBtn, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(Alignment.LEADING)
      .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(settingTabPan, GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
        .addGap(18, 18, 18)
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

  private void clearXmlBtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_clearXmlBtnMouseReleased
    Utils.deleteFileInDirectory(new File(setting.xmlCacheDir));
    xmlLbl.setText(Utils.getDirSizeInMegabytes(new File(setting.xmlCacheDir)) + bundle.getString("useForXml"));
}//GEN-LAST:event_clearXmlBtnMouseReleased

  private void clearActorBtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_clearActorBtnMouseReleased
    Utils.deleteFileInDirectory(new File(setting.actorCacheDir));
    actorCacheLbl.setText(Utils.getDirSizeInMegabytes(new File(setting.actorCacheDir)) + bundle.getString("useForActor"));
}//GEN-LAST:event_clearActorBtnMouseReleased

  private void clearFanartBtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_clearFanartBtnMouseReleased
    Utils.deleteFileInDirectory(new File(setting.fanartCacheDir));
    fanartCacheLbl.setText(Utils.getDirSizeInMegabytes(new File(setting.fanartCacheDir)) + bundle.getString("useForFanart"));
}//GEN-LAST:event_clearFanartBtnMouseReleased

  private void clearThumbBtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_clearThumbBtnMouseReleased
    Utils.deleteFileInDirectory(new File(setting.thumbCacheDir));
    thumbCacheLbl.setText(Utils.getDirSizeInMegabytes(new File(setting.thumbCacheDir)) + bundle.getString("useForThumb"));
}//GEN-LAST:event_clearThumbBtnMouseReleased

  private void addFilterActionPerformed(ActionEvent evt) {//GEN-FIRST:event_addFilterActionPerformed
    String s = (String) JOptionPane.showInputDialog(this, bundle.getString("filter"), bundle.getString("addFilter"), JOptionPane.PLAIN_MESSAGE, null, null, null);
    int index = currentFilterIndex;
    if ((s != null) && (s.length() > 0)) {
      String[] tmp = new String[filters.length + 1];
      int pos = 0;
      for (int i = 0; i < tmp.length; i++) {
        if (i != index) tmp[i] = filters[pos++];
        else tmp[i] = s;
      }
      filters = tmp;
      loadList(filterJlist, filters);
      filterJlist.setSelectedIndex(index);
      currentFilterIndex = index;
    }
}//GEN-LAST:event_addFilterActionPerformed

  private void addExtensionBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_addExtensionBtnActionPerformed
    String s = (String) JOptionPane.showInputDialog(this, "Extension", bundle.getString("addExt"), JOptionPane.PLAIN_MESSAGE, null, null, null);

    if ((s != null) && (s.length() > 0)) {
      extensions = Arrays.copyOf(extensions, extensions.length + 1);
      extensions[extensions.length - 1] = s;
      loadList(extentionJlist, extensions);
      extentionJlist.setSelectedIndex(extensions.length - 1);
      currentExtensionIndex = extensions.length - 1;
    }
}//GEN-LAST:event_addExtensionBtnActionPerformed

  private void helpBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_helpBtnActionPerformed
    JOptionPane.showMessageDialog(this, bundle.getString("movieFormatHelp").replace("|", separatorField.getText()), bundle.getString("movieFileName"), JOptionPane.INFORMATION_MESSAGE);
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

  private void movieInfoPanelChkItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_movieInfoPanelChkItemStateChanged
    boolean activate = true;
    if (!movieInfoPanelChk.isSelected()) activate = false;
    actorImageChk.setEnabled(activate);
    thumbsChk.setEnabled(activate);
    fanartsChk.setEnabled(activate);
  }//GEN-LAST:event_movieInfoPanelChkItemStateChanged

  private void testBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_testBtnActionPerformed
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

    switch (titleCase) {
      case Utils.UPPER:
        res = res.toUpperCase() + "." + ext.toUpperCase();
        break;
      case Utils.LOWER:
        res = res.toLowerCase() + "." + ext.toLowerCase();
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
  }//GEN-LAST:event_testBtnActionPerformed

  private void extensionHelpActionPerformed(ActionEvent evt) {//GEN-FIRST:event_extensionHelpActionPerformed
    JOptionPane.showMessageDialog(this, bundle.getString("extensionsHelp"), "Extension", JOptionPane.INFORMATION_MESSAGE);
  }//GEN-LAST:event_extensionHelpActionPerformed

  private void filenameFilterHelpActionPerformed(ActionEvent evt) {//GEN-FIRST:event_filenameFilterHelpActionPerformed
    JOptionPane.showMessageDialog(this, bundle.getString("movieFileFilterHelp"), bundle.getString("movieFileNameFilter"), JOptionPane.INFORMATION_MESSAGE);
  }//GEN-LAST:event_filenameFilterHelpActionPerformed

  private void moveRightActionPerformed(ActionEvent evt) {//GEN-FIRST:event_moveRightActionPerformed
    String[] tmp = new String[filters.length];
    for (int i = 0; i < tmp.length; i++) {
      if (i == (currentFilterIndex + 1)) tmp[i - 1] = filters[i];
      else if (i == currentFilterIndex) tmp[i + 1] = filters[i];
      else tmp[i] = filters[i];
    }
    filters = tmp;
    int index = currentFilterIndex + 1;
    loadList(filterJlist, filters);
    filterJlist.setSelectedIndex(index);
  }//GEN-LAST:event_moveRightActionPerformed

  private void moveLeftActionPerformed(ActionEvent evt) {//GEN-FIRST:event_moveLeftActionPerformed
    String[] tmp = new String[filters.length];
    for (int i = 0; i < tmp.length; i++) {
      if (i == (currentFilterIndex - 1)) tmp[i + 1] = filters[i];
      else if (i == currentFilterIndex) tmp[i - 1] = filters[i];
      else tmp[i] = filters[i];
    }
    filters = tmp;
    int index = currentFilterIndex - 1;
    loadList(filterJlist, filters);
    filterJlist.setSelectedIndex(index);
  }//GEN-LAST:event_moveLeftActionPerformed

  private void removeFilterBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_removeFilterBtnActionPerformed
    String[] newArray = (String[]) Utils.removeFromArray(filters, currentFilterIndex);
    if (newArray != null) {
      int index = currentFilterIndex;
      filters = newArray;
      loadList(filterJlist, filters);
      int pos = index - 1;
      if (pos < 0)
        pos++;
      filterJlist.setSelectedIndex(pos);
      currentFilterIndex = pos;
    }
  }//GEN-LAST:event_removeFilterBtnActionPerformed

  private void removeExtensuionBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_removeExtensuionBtnActionPerformed
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
  }//GEN-LAST:event_removeExtensuionBtnActionPerformed

  private void saveBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
    boolean restartApp = false;

    // General Setting
    if (setting.movieInfoPanel != movieInfoPanelChk.isSelected()) setting.interfaceChanged = true;
    if (setting.thumb != thumbsChk.isSelected()) setting.interfaceChanged = true;
    if (setting.fanart != fanartsChk.isSelected()) setting.interfaceChanged = true;
    if (setting.actorImage != actorImageChk.isSelected()) setting.interfaceChanged = true;

    setting.selectFrstMovie = selectFirstMovieChk.isSelected();
    setting.selectFrstRes = selectFirstResChk.isSelected();
    setting.showNotaMovieWarn = showNotaMovieWarnChk.isSelected();
    setting.scanSubfolder = scanSubfolderChk.isSelected();
    setting.useExtensionFilter = useExtensionFilterChk.isSelected();
    setting.movieInfoPanel = movieInfoPanelChk.isSelected();
    setting.actorImage = actorImageChk.isSelected();
    setting.thumb = thumbsChk.isSelected();
    setting.fanart = fanartsChk.isSelected();
    setting.autoSearchMovie = autoSearchChk.isSelected();
    setting.checkUpdate = checkUpdateChk.isSelected();

    boolean langFr = setting.locale.equals("fr");
    if (langFr != frenchRbtn.isSelected()) {
      setting.locale = (frenchRbtn.isSelected() ? "fr" : "en");
      int n = JOptionPane.showConfirmDialog(this, Settings.softName + Utils.SPACE + bundle.getString("wantRestart"), "Question", JOptionPane.YES_NO_OPTION);
      if (n == JOptionPane.YES_OPTION)
        restartApp = true;
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
    setting.nfoType = xbmcNFORBtn.isSelected() ? 0:1;

    // Imdb
    setting.imdbFr = imdbFrRbtn.isSelected();
    setting.displayThumbResult = displayThumbResultChk.isSelected();

    // Movie Files
    setting.movieFilenameFormat = formatField.getText();
    setting.createMovieDirectory = createDirChk.isSelected();
    setting.thumbExt = thumbExtCbBox.getSelectedIndex();

    setting.movieDir = customFolderField.getText();
    int nb = 0;
    if (renamedMovieTitleRBtn.isSelected()) nb = 1;
    if (customFolderRBtn.isSelected()) nb = 2;

    if (nb == 2 && setting.movieDir.equals("")) {
      JOptionPane.showMessageDialog(this, bundle.getString("cantBeEmpty"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
      return;
    }
    setting.movieDirRenamedTitle = nb;
    setting.separator = separatorField.getText();

    // Filter
    setting.extensions = extensions;
    setting.nameFilters = filters;

    setting.saveSetting();

    if (restartApp)
      try {
        if (!Utils.restartApplication(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI())))
          JOptionPane.showMessageDialog(this, bundle.getString("cantRestart"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
        else{
          dispose();
          System.exit(0);
        }
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, bundle.getString("cantRestart") + Utils.ENDLINE + ex.getMessage(), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
      }
    dispose();
  }//GEN-LAST:event_saveBtnActionPerformed

  private void customFolderRBtnItemStateChanged(ItemEvent evt) {//GEN-FIRST:event_customFolderRBtnItemStateChanged
    customFolderField.setEnabled(customFolderRBtn.isSelected());
  }//GEN-LAST:event_customFolderRBtnItemStateChanged
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private JButton CancelBtn;
  private JPanel SearchPnl;
  private JLabel actorCacheLbl;
  private JCheckBox actorImageChk;
  private JButton addExtensionBtn;
  private JButton addFilter;
  private JCheckBox autoSearchChk;
  private JPanel cachePnl;
  private ButtonGroup caseGroup;
  private JLabel caseLbl;
  private JCheckBox checkUpdateChk;
  private JButton clearActorBtn;
  private JButton clearFanartBtn;
  private JButton clearThumbBtn;
  private JButton clearXmlBtn;
  private JCheckBox createDirChk;
  private ButtonGroup createDirGroup;
  private JTextField customFolderField;
  private JRadioButton customFolderRBtn;
  private JLabel defaultFormatLbl;
  private JCheckBox displayAppResultCheckBox;
  private JCheckBox displayThumbResultChk;
  private JRadioButton englishRbtn;
  private JButton extensionHelp;
  private JPanel extensionPnl;
  private JScrollPane extensionScrollP;
  private JList extentionJlist;
  private JLabel fanartCacheLbl;
  private ButtonGroup fanartGroup;
  private JLabel fanartSizeLbl;
  private JCheckBox fanartsChk;
  private JPanel fileNameFilterPnl;
  private JButton filenameFilterHelp;
  private JList filterJlist;
  private JScrollPane filterScrollP;
  private JPanel filtersPnl;
  private JRadioButton firstLaRbtn;
  private JRadioButton firstLoRbtn;
  private JTextField formatField;
  private JLabel formatLbl;
  private JRadioButton frenchRbtn;
  private JPanel generalPnl;
  private JButton helpBtn;
  private JPanel imagePnl;
  private JPanel imagesPnl;
  private JRadioButton imdbEnRbtn;
  private JRadioButton imdbFrRbtn;
  private ButtonGroup imdbLangGroup;
  private JPanel imdbLangPnl;
  private JPanel imdbSearchPnl;
  private JPanel interfacePnl;
  private JPanel jPanel1;
  private ButtonGroup languageGroup;
  private JPanel languagePnl;
  private JComboBox limitResultComboBox;
  private JLabel limitResultLbl;
  private JRadioButton lowerRbtn;
  private JLabel lwarningLbl;
  private JRadioButton mediaPortalNFORBtn;
  private JRadioButton midFanartSizeRBtn;
  private JRadioButton midThumbSizeRBtn;
  private JButton moveLeft;
  private JButton moveRight;
  private JPanel movieFileNamePnl;
  private JPanel movieImagePnl;
  private JCheckBox movieInfoPanelChk;
  private JRadioButton movieTitleRBtn;
  private ButtonGroup nfoGroup;
  private JRadioButton origFanartSizeRBtn;
  public JRadioButton origThumbSizeRBtn;
  private JButton removeExtensuionBtn;
  private JButton removeFilterBtn;
  private JPanel renamePnl;
  private JRadioButton renamedMovieTitleRBtn;
  private JButton saveBtn;
  private JCheckBox scanSubfolderChk;
  private JCheckBox selectFirstMovieChk;
  private JCheckBox selectFirstResChk;
  private JTextField separatorField;
  private JLabel separatorLbl;
  private JTabbedPane settingTabPan;
  private JCheckBox showNotaMovieWarnChk;
  private JButton testBtn;
  private JTextField testField;
  private JLabel thumbCacheLbl;
  private JComboBox thumbExtCbBox;
  private JRadioButton thumbFanartSizeRBtn;
  private ButtonGroup thumbGroup;
  private JLabel thumbSzeLbl;
  private JRadioButton thumbThumbSizeRBtn;
  private JCheckBox thumbsChk;
  private JLabel thumnailsExtLbl;
  private JPanel updatePnl;
  private JRadioButton upperRbtn;
  private JCheckBox useExtensionFilterChk;
  private JRadioButton xbmcNFORBtn;
  private JPanel xmlFilePnl;
  private JLabel xmlLbl;
  // End of variables declaration//GEN-END:variables
}
