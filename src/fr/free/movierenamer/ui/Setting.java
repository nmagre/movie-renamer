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
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.border.TitledBorder;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;

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
  private String[][] format = {
    {"<t>", "Matrix"}, {"<y>", "1999"}, {"<tt>", "tt0133093"},
    {"<g>", "Action | Adventure | Sci-Fi"}, {"<g1>", "Action"},
    {"<d>", "Andy Wachowski, Lana Wachowski"}, {"<d1>", "Andy Wachowski"},
    {"<rt>", "136"}, {"<ra>", "8.8"}
  };

  private ResourceBundle bundle;

  /** Creates new form Setting
   * @param setting
   */
  public Setting(Settings setting) {
    bundle = ResourceBundle.getBundle("fr.free.movierenamer/i18n/Bundle");
    initComponents();

    rBtnThumbList = new JRadioButton[]{this.origThumbSizeRBtn, this.midThumbSizeRBtn, this.thumbThumbSizeRBtn};
    rBtnFanartList = new JRadioButton[]{this.origFanartSizeRBtn, this.midFanartSizeRBtn, this.thumbFanartSizeRBtn};
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
    useExtensionFilterChk.setSelected(setting.useExtensionFilter);
    showMovieFilePathChk.setSelected(setting.showMovieFilePath);
    scanSubfolderChk.setSelected(setting.scanSubfolder);
    hideNotAMovieFileChk.setSelected(setting.hideNotAMovieFile);


    englishRbtn.setSelected(!setting.locale.equals("fr"));
    frenchRbtn.setSelected(setting.locale.equals("fr"));


    thumbGroup.setSelected(rBtnThumbList[setting.thumbSize].getModel(), true);
    fanartGroup.setSelected(rBtnFanartList[setting.fanartSize].getModel(), true);
    displayAppResultCheckBox.setSelected(setting.displayApproximateResult);
    limitResultComboBox.setSelectedIndex(setting.nbResult);
    displayThumbResultChk.setSelected(setting.displayThumbResult);

    // Imdb
    imdbFrRbtn.setSelected(setting.imdbFr);
    imdbEnRbtn.setSelected(!setting.imdbFr);


    // Movie Files
    formatField.setText(setting.movieFilenameFormat);

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
    jTabbedPane1 = new JTabbedPane();
    generalPnl = new JPanel();
    jPanel6 = new JPanel();
    displayAppResultCheckBox = new JCheckBox();
    limitResultComboBox = new JComboBox();
    jLabel14 = new JLabel();
    helpSearchBtn = new JButton();
    displayThumbResultChk = new JCheckBox();
    jPanel13 = new JPanel();
    englishRbtn = new JRadioButton();
    frenchRbtn = new JRadioButton();
    jLabel1 = new JLabel();
    jPanel4 = new JPanel();
    jCheckBox8 = new JCheckBox();
    jPanel10 = new JPanel();
    useExtensionFilterChk = new JCheckBox();
    showMovieFilePathChk = new JCheckBox();
    scanSubfolderChk = new JCheckBox();
    hideNotAMovieFileChk = new JCheckBox();
    hideRenamedMovieChk = new JCheckBox();
    helpSearchBtn2 = new JButton();
    jCheckBox16 = new JCheckBox();
    jPanel17 = new JPanel();
    jPanel19 = new JPanel();
    jCheckBox4 = new JCheckBox();
    jCheckBox11 = new JCheckBox();
    jCheckBox12 = new JCheckBox();
    jCheckBox13 = new JCheckBox();
    jCheckBox14 = new JCheckBox();
    jCheckBox15 = new JCheckBox();
    MovieFilesPnl = new JPanel();
    jPanel2 = new JPanel();
    movieTitleRBtn = new JRadioButton();
    renamedMovieTitleRBtn = new JRadioButton();
    createDirChk = new JCheckBox();
    jLabel4 = new JLabel();
    jButton3 = new JButton();
    testField = new JTextField();
    formatField = new JTextField();
    jLabel3 = new JLabel();
    helpBtn = new JButton();
    jRadioButton1 = new JRadioButton();
    jPanel9 = new JPanel();
    thumbExtCbBox = new JComboBox();
    jLabel6 = new JLabel();
    jPanel3 = new JPanel();
    jPanel14 = new JPanel();
    jCheckBox3 = new JCheckBox();
    jCheckBox2 = new JCheckBox();
    jCheckBox1 = new JCheckBox();
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
    displayThumbResultChk.setText("Show image in result list");
    displayThumbResultChk.setToolTipText("Displays movie thumbnails in search results");

    GroupLayout jPanel6Layout = new GroupLayout(jPanel6);
    jPanel6.setLayout(jPanel6Layout);
    jPanel6Layout.setHorizontalGroup(
      jPanel6Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel6Layout.createSequentialGroup()
        .addGap(36, 36, 36)
        .addGroup(jPanel6Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(displayThumbResultChk)
          .addComponent(displayAppResultCheckBox)
          .addGroup(jPanel6Layout.createSequentialGroup()
            .addComponent(limitResultComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(jLabel14)
            .addPreferredGap(ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
            .addComponent(helpSearchBtn, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    jPanel6Layout.setVerticalGroup(
      jPanel6Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel6Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel6Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(helpSearchBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addGroup(jPanel6Layout.createParallelGroup(Alignment.BASELINE)
            .addComponent(limitResultComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel14)))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(displayAppResultCheckBox)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(displayThumbResultChk)
        .addContainerGap(14, Short.MAX_VALUE))
    );

    jPanel13.setBorder(BorderFactory.createTitledBorder(null, "Language", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Ubuntu", 1, 13))); // NOI18N
    jPanel13.setToolTipText("Under development");

    languageGroup.add(englishRbtn);
    englishRbtn.setFont(new Font("Ubuntu", 0, 12));
    englishRbtn.setSelected(true);
    englishRbtn.setText("English");

    languageGroup.add(frenchRbtn);
    frenchRbtn.setFont(new Font("Ubuntu", 0, 12));
    frenchRbtn.setText("French");

    jLabel1.setFont(new Font("Ubuntu", 1, 12));
    jLabel1.setIcon(new ImageIcon(getClass().getResource("/image/dialog-warning.png"))); // NOI18N
    jLabel1.setText("Need restart");

    GroupLayout jPanel13Layout = new GroupLayout(jPanel13);
    jPanel13.setLayout(jPanel13Layout);

    jPanel13Layout.setHorizontalGroup(
      jPanel13Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel13Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(englishRbtn)
        .addGap(18, 18, 18)
        .addComponent(frenchRbtn)
        .addPreferredGap(ComponentPlacement.RELATED, 185, Short.MAX_VALUE)
        .addComponent(jLabel1)
        .addContainerGap())
    );
    jPanel13Layout.setVerticalGroup(
      jPanel13Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel13Layout.createParallelGroup(Alignment.BASELINE)
        .addComponent(englishRbtn)
        .addComponent(frenchRbtn)
        .addComponent(jLabel1))
    );

    jPanel4.setBorder(BorderFactory.createTitledBorder(null, "Update", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Ubuntu", 1, 13))); // NOI18N
    jCheckBox8.setFont(new Font("Ubuntu", 0, 12));
    jCheckBox8.setSelected(true);
    jCheckBox8.setText("Check for update on startup");
    jCheckBox8.setToolTipText("You can check for update at any time, directly on movie renamer (globe button)");

    GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
      jPanel4Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jCheckBox8)
        .addContainerGap(261, Short.MAX_VALUE))
    );
    jPanel4Layout.setVerticalGroup(
      jPanel4Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jCheckBox8)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel10.setBorder(BorderFactory.createTitledBorder(null, "Movie file list", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); // NOI18N

    useExtensionFilterChk.setText("Use extension filter");

    showMovieFilePathChk.setText("Show movie file path");

    scanSubfolderChk.setText("Scan subfolders");

    hideNotAMovieFileChk.setText("Hide not a movie files");

    hideRenamedMovieChk.setText("Hide renamed movie");

    helpSearchBtn2.setIcon(new ImageIcon(getClass().getResource("/image/system-help-3.png"))); // NOI18N
    helpSearchBtn2.setToolTipText("Help");
    helpSearchBtn2.setMaximumSize(new Dimension(26, 26));
    helpSearchBtn2.setMinimumSize(new Dimension(26, 26));
    helpSearchBtn2.setPreferredSize(new Dimension(26, 26));

    jCheckBox16.setText("Destination folder");
    jCheckBox16.setEnabled(false);

    GroupLayout jPanel10Layout = new GroupLayout(jPanel10);
    jPanel10.setLayout(jPanel10Layout);
    jPanel10Layout.setHorizontalGroup(
      jPanel10Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel10Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel10Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(useExtensionFilterChk)
          .addComponent(showMovieFilePathChk)
          .addComponent(scanSubfolderChk))
        .addGap(20, 20, 20)
        .addGroup(jPanel10Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(jCheckBox16)
          .addGroup(jPanel10Layout.createSequentialGroup()
            .addComponent(hideNotAMovieFileChk)
            .addPreferredGap(ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
            .addComponent(helpSearchBtn2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addComponent(hideRenamedMovieChk))
        .addContainerGap())
    );
    jPanel10Layout.setVerticalGroup(
      jPanel10Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel10Layout.createSequentialGroup()
        .addGroup(jPanel10Layout.createParallelGroup(Alignment.BASELINE)
          .addComponent(useExtensionFilterChk)
          .addComponent(hideNotAMovieFileChk))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addGroup(jPanel10Layout.createParallelGroup(Alignment.BASELINE)
          .addComponent(hideRenamedMovieChk)
          .addComponent(showMovieFilePathChk))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addGroup(jPanel10Layout.createParallelGroup(Alignment.BASELINE)
          .addComponent(scanSubfolderChk)
          .addComponent(jCheckBox16)))
      .addComponent(helpSearchBtn2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    );

    GroupLayout generalPnlLayout = new GroupLayout(generalPnl);
    generalPnl.setLayout(generalPnlLayout);
    generalPnlLayout.setHorizontalGroup(
      generalPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(generalPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(generalPnlLayout.createParallelGroup(Alignment.LEADING)
          .addComponent(jPanel6, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jPanel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jPanel10, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jPanel13, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    generalPnlLayout.setVerticalGroup(
      generalPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(generalPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jPanel10, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(jPanel6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(jPanel13, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(78, Short.MAX_VALUE))
    );

    jTabbedPane1.addTab("General", generalPnl);

    jPanel19.setBorder(BorderFactory.createTitledBorder("Advanced"));

    jCheckBox4.setText("Horizontal split");

    jCheckBox11.setText("Display actor image");

    jCheckBox12.setText("Show movie info");

    jCheckBox13.setText("Movie images");

    jCheckBox14.setText("Thumbnail");

    jCheckBox15.setText("Fanart");

    GroupLayout jPanel19Layout = new GroupLayout(jPanel19);
    jPanel19.setLayout(jPanel19Layout);
    jPanel19Layout.setHorizontalGroup(
      jPanel19Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel19Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel19Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(jCheckBox4)
          .addComponent(jCheckBox12)
          .addGroup(jPanel19Layout.createSequentialGroup()
            .addGap(21, 21, 21)
            .addGroup(jPanel19Layout.createParallelGroup(Alignment.LEADING)
              .addComponent(jCheckBox13)
              .addComponent(jCheckBox11)
              .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel19Layout.createParallelGroup(Alignment.LEADING)
                  .addComponent(jCheckBox15)
                  .addComponent(jCheckBox14))))))
        .addContainerGap(261, Short.MAX_VALUE))
    );
    jPanel19Layout.setVerticalGroup(
      jPanel19Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel19Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jCheckBox4)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(jCheckBox12)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(jCheckBox11)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(jCheckBox13)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(jCheckBox14)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(jCheckBox15)
        .addContainerGap(48, Short.MAX_VALUE))
    );

    GroupLayout jPanel17Layout = new GroupLayout(jPanel17);
    jPanel17.setLayout(jPanel17Layout);
    jPanel17Layout.setHorizontalGroup(
      jPanel17Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel17Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jPanel19, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap())
    );
    jPanel17Layout.setVerticalGroup(
      jPanel17Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel17Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jPanel19, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(202, Short.MAX_VALUE))
    );

    jTabbedPane1.addTab("Interface", jPanel17);

    jPanel2.setBorder(BorderFactory.createTitledBorder(null, "Movie FileName", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); // NOI18N

    createDirGroup.add(movieTitleRBtn);
    movieTitleRBtn.setSelected(true);
    movieTitleRBtn.setText("Movie title");
    movieTitleRBtn.setToolTipText("Ex : Matrix/");
    movieTitleRBtn.setEnabled(false);
    movieTitleRBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        movieTitleRBtnActionPerformed(evt);
      }
    });

    createDirGroup.add(renamedMovieTitleRBtn);
    renamedMovieTitleRBtn.setText("Renamed movie title");
    renamedMovieTitleRBtn.setToolTipText("Ex : Matrix (1999)/");
    renamedMovieTitleRBtn.setEnabled(false);

    createDirChk.setText("Create a directory");
    createDirChk.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        createDirChkMouseReleased(evt);
      }
    });

    jLabel4.setText("Default Format : <t> (<y>) ");

    jButton3.setText("test");
    jButton3.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        jButton3MouseReleased(evt);
      }
    });

    testField.setEditable(false);

    formatField.setText("<t> (<y>)");

    jLabel3.setText("Format");

    helpBtn.setIcon(new ImageIcon(getClass().getResource("/image/system-help-3.png"))); // NOI18N
    helpBtn.setToolTipText("Help");
    helpBtn.setMaximumSize(new Dimension(26, 26));
    helpBtn.setMinimumSize(new Dimension(26, 26));
    helpBtn.setPreferredSize(new Dimension(26, 26));
    helpBtn.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        helpBtnMouseReleased(evt);
      }
    });
    helpBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        helpBtnActionPerformed(evt);
      }
    });

    jRadioButton1.setText("jRadioButton1");

    GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);

    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(jRadioButton1)
          .addComponent(jLabel4)
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addComponent(jButton3)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(testField, GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
            .addGap(32, 32, 32))
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addGap(21, 21, 21)
            .addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
              .addComponent(movieTitleRBtn)
              .addComponent(renamedMovieTitleRBtn)))
          .addComponent(createDirChk, GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addComponent(jLabel3)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(formatField, GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(helpBtn, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addGroup(jPanel2Layout.createParallelGroup(Alignment.TRAILING)
          .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
            .addComponent(jLabel3)
            .addComponent(formatField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addComponent(helpBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addGap(24, 24, 24)
        .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
          .addComponent(jButton3)
          .addComponent(testField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addGap(22, 22, 22)
        .addComponent(jLabel4)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(jRadioButton1)
        .addGap(62, 62, 62)
        .addComponent(createDirChk)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(renamedMovieTitleRBtn)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(movieTitleRBtn)
        .addGap(23, 23, 23))
    );

    jPanel9.setBorder(BorderFactory.createTitledBorder(null, "Image Extension", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); // NOI18N
    thumbExtCbBox.setModel(new DefaultComboBoxModel(new String[] { ".jpg", ".tbn", "-thumb.jpg" }));
    thumbExtCbBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        thumbExtCbBoxActionPerformed(evt);
      }
    });

    jLabel6.setText("Thumb");

    GroupLayout jPanel9Layout = new GroupLayout(jPanel9);
    jPanel9.setLayout(jPanel9Layout);
    jPanel9Layout.setHorizontalGroup(
      jPanel9Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel9Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel6)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(thumbExtCbBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(279, Short.MAX_VALUE))
    );
    jPanel9Layout.setVerticalGroup(
      jPanel9Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel9Layout.createSequentialGroup()
        .addGroup(jPanel9Layout.createParallelGroup(Alignment.BASELINE)
          .addComponent(jLabel6)
          .addComponent(thumbExtCbBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    GroupLayout MovieFilesPnlLayout = new GroupLayout(MovieFilesPnl);
    MovieFilesPnl.setLayout(MovieFilesPnlLayout);
    MovieFilesPnlLayout.setHorizontalGroup(
      MovieFilesPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(MovieFilesPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(MovieFilesPnlLayout.createParallelGroup(Alignment.LEADING)
          .addComponent(jPanel2, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jPanel9, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    MovieFilesPnlLayout.setVerticalGroup(
      MovieFilesPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(Alignment.TRAILING, MovieFilesPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, 318, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(jPanel9, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addGap(307, 307, 307))
    );

    jTabbedPane1.addTab("Rename", MovieFilesPnl);

    jPanel14.setBorder(BorderFactory.createTitledBorder(null, "Images", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); // NOI18N

    jCheckBox3.setText("Download and display actors images ");

    jCheckBox2.setText("Download and display fanarts");

    jCheckBox1.setText("Download and display thumbnails");

    GroupLayout jPanel14Layout = new GroupLayout(jPanel14);
    jPanel14.setLayout(jPanel14Layout);
    jPanel14Layout.setHorizontalGroup(
      jPanel14Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel14Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel14Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(jCheckBox2)
          .addComponent(jCheckBox1)
          .addComponent(jCheckBox3))
        .addContainerGap(157, Short.MAX_VALUE))
    );
    jPanel14Layout.setVerticalGroup(
      jPanel14Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel14Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jCheckBox1)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(jCheckBox2)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(jCheckBox3)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

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
        .addContainerGap(127, Short.MAX_VALUE))
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
        .addContainerGap(135, Short.MAX_VALUE))
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

    GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel3Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(jPanel14, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jPanel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jPanel15, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jPanel15, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(jPanel14, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(jPanel5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(99, Short.MAX_VALUE))
    );

    jTabbedPane1.addTab("Imdb", jPanel3);


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
          .addGroup(jPanel11Layout.createSequentialGroup()
            .addGap(248, 248, 248)
            .addComponent(addExtensionBtn, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.UNRELATED)
            .addComponent(removeExtensuionBtn, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE))
          .addGroup(jPanel11Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 408, GroupLayout.PREFERRED_SIZE)))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(helpBtn1, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
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
            .addGroup(jPanel11Layout.createParallelGroup(Alignment.TRAILING)
              .addComponent(removeExtensuionBtn, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
              .addComponent(addExtensionBtn, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))))
        .addContainerGap())
    );

    jPanel12.setBorder(BorderFactory.createTitledBorder(null, "Name Filters", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); // NOI18N
    jButton1.setIcon(new ImageIcon(getClass().getResource("/image/go-previous-3.png"))); // NOI18N
    jButton1.setToolTipText("Move left");

    jButton2.setIcon(new ImageIcon(getClass().getResource("/image/go-next-3.png"))); // NOI18N
    jButton2.setToolTipText("Move right");

    jButton5.setIcon(new ImageIcon(getClass().getResource("/image/list-add-5.png"))); // NOI18N
    jButton5.setToolTipText("Add filter");
    jButton5.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        jButton5MouseReleased(evt);
      }
    });
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
        .addGroup(jPanel12Layout.createParallelGroup(Alignment.LEADING, false)
          .addGroup(Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
            .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton5, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.UNRELATED)
            .addComponent(removeFilterBtn, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE))
          .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 410, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(helpBtn2, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(18, Short.MAX_VALUE))
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
        .addContainerGap(58, Short.MAX_VALUE))
    );

    jTabbedPane1.addTab("Filters", filtersPnl);

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
        .addContainerGap(199, Short.MAX_VALUE))
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
        .addContainerGap(199, Short.MAX_VALUE))
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
        .addContainerGap(169, Short.MAX_VALUE))
    );

    jTabbedPane1.addTab("Cache", jPanel1);

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
        .addContainerGap(229, Short.MAX_VALUE))
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
        .addContainerGap(286, Short.MAX_VALUE))
    );

    jTabbedPane1.addTab("About", jPanel16);

    saveBtn.setIcon(new ImageIcon(getClass().getResource("/image/dialog-ok-2.png"))); // NOI18N
    ResourceBundle bundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle"); // NOI18N
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
        .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
          .addComponent(jTabbedPane1, GroupLayout.PREFERRED_SIZE, 494, GroupLayout.PREFERRED_SIZE)
          .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(saveBtn, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(CancelBtn, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(Alignment.LEADING)
      .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jTabbedPane1, GroupLayout.PREFERRED_SIZE, 498, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(Alignment.TRAILING)
          .addComponent(CancelBtn, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
          .addComponent(saveBtn, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
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
    setting.useExtensionFilter = useExtensionFilterChk.isSelected();
    setting.showMovieFilePath = showMovieFilePathChk.isSelected();
    setting.scanSubfolder = scanSubfolderChk.isSelected();
    setting.hideNotAMovieFile = hideNotAMovieFileChk.isSelected();
    
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
    setting.displayThumbResult = displayThumbResultChk.isSelected();

    boolean langFr = setting.locale.equals("fr");
    if(langFr != frenchRbtn.isSelected()){
      setting.locale = (frenchRbtn.isSelected() ? "fr":"en");
      int n = JOptionPane.showConfirmDialog(this, Settings.softName + " need to restart for apply language change\nWould you like to restart now ?", "Question", JOptionPane.YES_NO_OPTION);
      if(n == JOptionPane.YES_OPTION){
        restartApp = true;
      }
    }

    // Imdb
    setting.imdbFr = imdbFrRbtn.isSelected();

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
    // TODO add your handling code here:
}//GEN-LAST:event_jButton5ActionPerformed

  private void jButton5MouseReleased(MouseEvent evt) {//GEN-FIRST:event_jButton5MouseReleased
    String s = (String) JOptionPane.showInputDialog(this, "Add", "Add Name Filter", JOptionPane.PLAIN_MESSAGE, null, null, null);

    if ((s != null) && (s.length() > 0)) {
      filters = Arrays.copyOf(filters, filters.length + 1);
      filters[filters.length - 1] = s;
      loadList(filterJlist, filters);
      filterJlist.setSelectedIndex(filters.length - 1);
      currentFilterIndex = filters.length - 1;
    }
}//GEN-LAST:event_jButton5MouseReleased

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
    // TODO add your handling code here:
}//GEN-LAST:event_helpBtnActionPerformed

  private void helpBtnMouseReleased(MouseEvent evt) {//GEN-FIRST:event_helpBtnMouseReleased
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
      + "	Any other text will be included as is, such as spaces and punctuation\n(excluding file system punctuation such as colons, double quotes, question marks, and asterisks).", "Movie Format", JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_helpBtnMouseReleased

  private void jButton3MouseReleased(MouseEvent evt) {//GEN-FIRST:event_jButton3MouseReleased
    String res = formatField.getText();
    for (int i = 0; i < format.length; i++) {
      res = res.replaceAll(format[i][0], format[i][1]);
    }
    testField.setText(res + ".avi");
}//GEN-LAST:event_jButton3MouseReleased

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

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private JButton CancelBtn;
  private JPanel MovieFilesPnl;
  private JLabel actorCacheLbl;
  private JButton addExtensionBtn;
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
  private JList filterJlist;
  private JPanel filtersPnl;
  private JTextField formatField;
  private JRadioButton frenchRbtn;
  private JPanel generalPnl;
  private JButton helpBtn;
  private JButton helpBtn1;
  private JButton helpBtn2;
  private JButton helpSearchBtn;
  private JButton helpSearchBtn2;
  private JCheckBox hideNotAMovieFileChk;
  private JCheckBox hideRenamedMovieChk;
  private JRadioButton imdbEnRbtn;
  private JRadioButton imdbFrRbtn;
  private ButtonGroup imdbLangGroup;
  private ButtonGroup interfaceGroup;
  private JButton jButton1;
  private JButton jButton2;
  private JButton jButton3;
  private JButton jButton4;
  private JButton jButton5;
  private JButton jButton6;
  private JCheckBox jCheckBox1;
  private JCheckBox jCheckBox11;
  private JCheckBox jCheckBox12;
  private JCheckBox jCheckBox13;
  private JCheckBox jCheckBox14;
  private JCheckBox jCheckBox15;
  private JCheckBox jCheckBox16;
  private JCheckBox jCheckBox2;
  private JCheckBox jCheckBox3;
  private JCheckBox jCheckBox4;
  private JCheckBox jCheckBox8;
  private JLabel jLabel1;
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
  private JPanel jPanel10;
  private JPanel jPanel11;
  private JPanel jPanel12;
  private JPanel jPanel13;
  private JPanel jPanel14;
  private JPanel jPanel15;
  private JPanel jPanel16;
  private JPanel jPanel17;
  private JPanel jPanel19;
  private JPanel jPanel2;
  private JPanel jPanel3;
  private JPanel jPanel4;
  private JPanel jPanel5;
  private JPanel jPanel6;
  private JPanel jPanel7;
  private JPanel jPanel8;
  private JPanel jPanel9;
  private JRadioButton jRadioButton1;
  private JScrollPane jScrollPane1;
  private JScrollPane jScrollPane2;
  private JTabbedPane jTabbedPane1;
  private JTextField jTextField1;
  private ButtonGroup languageGroup;
  private JComboBox limitResultComboBox;
  private JRadioButton midFanartSizeRBtn;
  private JRadioButton midThumbSizeRBtn;
  private JRadioButton movieTitleRBtn;
  private JRadioButton origFanartSizeRBtn;
  public JRadioButton origThumbSizeRBtn;
  private ButtonGroup ratingGroup;
  private JButton removeExtensuionBtn;
  private JButton removeFilterBtn;
  private JRadioButton renamedMovieTitleRBtn;
  private JButton saveBtn;
  private JCheckBox scanSubfolderChk;
  private JCheckBox showMovieFilePathChk;
  private JTextField testField;
  private JLabel thumbCacheLbl;
  private JComboBox thumbExtCbBox;
  private JRadioButton thumbFanartSizeRBtn;
  private ButtonGroup thumbGroup;
  private JRadioButton thumbThumbSizeRBtn;
  private JCheckBox useExtensionFilterChk;
  private JLabel xmlLbl;
  private ButtonGroup youtubeGroup;
  // End of variables declaration//GEN-END:variables
}
