/*
 * Movie Renamer
 * Copyright (C) 2012 Nicolas Magré
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
package fr.free.movierenamer.ui;

import fr.free.movierenamer.media.MediaPerson;
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.ui.res.CustomField;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Class InfoEditorFrame
 *
 * @author Nicolas Magré
 */
public class InfoEditorFrame extends JDialog {

  private final int ALLOCINE = 0;
  private final int ROTTEN = 1;
  private final int ALLMOVIE = 2;
  private final String allocineSearchUrl = "http://www.allocine.fr/recherche/?q=";
  private final String rottenUrl = "http://www.rottentomatoes.com/search/?search=";
  private final String allmovieUrl = "http://www.allmovie.com/search/movies/";
  private MovieInfo movieInfo;
  private DefaultListModel listModel;
  private CustomField titleCField;
  private CustomField sortTitleCField;
  private CustomField originalTitleCField;
  private CustomField mpaaCField;
  private CustomField trailerCField;
  private CustomField taglineCField;
  private CustomField yearCField;
  private CustomField runtimeCField;
  private CustomField ratingCField;
  private CustomField imdbidCField;
  private CustomField synopsisCField;
  private CustomField studioCField;
  private CustomField directorCField;
  private CustomField genreCField;
  private CustomField setCField;
  private CustomField writerCField;
  private CustomField countryCField;
  private CustomField top250CField;

  public InfoEditorFrame(MovieInfo movieInfo, Component parent) {

    this.movieInfo = movieInfo;

    initComponents();
    setValue();
    setTitle("Movie Renamer Editor - " + movieInfo.getTitle());
    setLocationRelativeTo(parent);
    setIconImage(Utils.getImageFromJAR("/image/icon-32.png", getClass()));
    setModal(true);
  }

  private void setValue() {
    listModel = new DefaultListModel();
    ArrayList<MediaPerson> actors = movieInfo.getActors();
    for (int i = 0; i < actors.size(); i++) {
      listModel.addElement(actors.get(i));
    }
    actorsList.setModel(listModel);
    actorsList.setSelectedIndex(0);
    actorsList.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          if (actorsList.getSelectedIndex() != -1) {
            String strActor = ((MediaPerson) listModel.getElementAt(actorsList.getSelectedIndex())).getName();
            JOptionPane.showMessageDialog(InfoEditorFrame.this, "", strActor, JOptionPane.INFORMATION_MESSAGE, null);
          }
        }
      }
    });

    titleCField = new CustomField(titleField, cancelBtn1);
    sortTitleCField = new CustomField(sortTitleField, cancelBtn2);
    originalTitleCField = new CustomField(originalTitleField, cancelBtn3);
    mpaaCField = new CustomField(mpaaField, cancelBtn4);
    trailerCField = new CustomField(trailerField, cancelBtn5);
    taglineCField = new CustomField(taglineField, cancelBtn6);
    yearCField = new CustomField(yearField, cancelBtn7);
    runtimeCField = new CustomField(runtimeField, cancelBtn8);
    ratingCField = new CustomField(ratingField, cancelBtn9);
    imdbidCField = new CustomField(imdbidField, cancelBtn10);
    synopsisCField = new CustomField(synopsisField, cancelBtn11);
    studioCField = new CustomField(studioField, cancelBtn13);
    directorCField = new CustomField(directorField, cancelBtn14);
    genreCField = new CustomField(genreField, cancelBtn15);
    setCField = new CustomField(setField, cancelBtn16);
    writerCField = new CustomField(writerField, cancelBtn17);
    countryCField = new CustomField(countryField, cancelBtn18);
    top250CField = new CustomField(top250Field, cancelBtn12);
    watchedChk.setSelected(movieInfo.getWatched());

    initMovie();
  }

  private void initMovie() {// A refaire , ajouter les API id
    movietitleField.setText(movieInfo.getTitle());
 //   imdbidCField.setInitValue(movieInfo.getImdbId());
    mpaaCField.setInitValue(movieInfo.getMpaa());
    originalTitleCField.setInitValue(movieInfo.getOrigTitle());
    ratingCField.setInitValue("" + movieInfo.getRating());
    runtimeCField.setInitValue("" + movieInfo.getRuntime());
    sortTitleCField.setInitValue(movieInfo.getTitle());
    taglineCField.setInitValue(movieInfo.getTagline());
    titleCField.setInitValue(movieInfo.getTitle());
    trailerCField.setInitValue(movieInfo.getTrailer());
    yearCField.setInitValue("" + movieInfo.getYear());
    synopsisCField.setInitValue(movieInfo.getSynopsis());
    top250CField.setInitValue(movieInfo.getTop250());

    studioCField.setInitValue(movieInfo.getStudiosString(" | ", 0));
    directorCField.setInitValue(movieInfo.getDirectorsString(" | ", 0));
    genreCField.setInitValue(movieInfo.getGenresString(" | ", 0));
    setCField.setInitValue(movieInfo.getSetString(" | ", 0));
    writerCField.setInitValue(movieInfo.getWritersString(" | ", 0));
    countryCField.setInitValue(movieInfo.getCountriesString(" | ", 0));
  }

  public void openEditor(final JTextField field, String separator) {
    Editor editor = new Editor(field.getText(), separator);
    editor.addPropertyChangeListener(new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent pce) {
        field.setText((String) pce.getNewValue());
      }
    });
    editor.setVisible(true);
  }

  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        NFODialog = new javax.swing.JDialog();
        jButton15 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        movietitleField = new javax.swing.JTextField();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        infoPnl = new javax.swing.JPanel();
        imdbidField = new javax.swing.JTextField();
        imdbidLbl = new javax.swing.JLabel();
        runtimeField = new javax.swing.JTextField();
        ratingField = new javax.swing.JTextField();
        runtimeLbl = new javax.swing.JLabel();
        ratingLbl = new javax.swing.JLabel();
        yearField = new javax.swing.JTextField();
        yearLbl = new javax.swing.JLabel();
        trailerLbl = new javax.swing.JLabel();
        mpaaLbl = new javax.swing.JLabel();
        originalTitleLbl = new javax.swing.JLabel();
        sortTitleLbl = new javax.swing.JLabel();
        titleLbl = new javax.swing.JLabel();
        titleField = new javax.swing.JTextField();
        sortTitleField = new javax.swing.JTextField();
        originalTitleField = new javax.swing.JTextField();
        mpaaField = new javax.swing.JTextField();
        trailerField = new javax.swing.JTextField();
        taglineField = new javax.swing.JTextField();
        taglineLbl = new javax.swing.JLabel();
        cancelBtn1 = new javax.swing.JButton();
        cancelBtn2 = new javax.swing.JButton();
        cancelBtn3 = new javax.swing.JButton();
        cancelBtn4 = new javax.swing.JButton();
        cancelBtn5 = new javax.swing.JButton();
        cancelBtn6 = new javax.swing.JButton();
        cancelBtn7 = new javax.swing.JButton();
        cancelBtn8 = new javax.swing.JButton();
        cancelBtn9 = new javax.swing.JButton();
        cancelBtn10 = new javax.swing.JButton();
        top250Lbl = new javax.swing.JLabel();
        top250Field = new javax.swing.JTextField();
        cancelBtn12 = new javax.swing.JButton();
        watchedChk = new javax.swing.JCheckBox();
        watchedLbl = new javax.swing.JLabel();
        detailPnl = new javax.swing.JPanel();
        studioLbl = new javax.swing.JLabel();
        directorLbl = new javax.swing.JLabel();
        genreLbl = new javax.swing.JLabel();
        setLbl = new javax.swing.JLabel();
        writerLbl = new javax.swing.JLabel();
        countryLbl = new javax.swing.JLabel();
        countryField = new javax.swing.JTextField();
        writerField = new javax.swing.JTextField();
        setField = new javax.swing.JTextField();
        genreField = new javax.swing.JTextField();
        directorField = new javax.swing.JTextField();
        studioField = new javax.swing.JTextField();
        editStudioBtn = new javax.swing.JButton();
        editDirectorBtn = new javax.swing.JButton();
        editGenreBtn = new javax.swing.JButton();
        editSetBtn = new javax.swing.JButton();
        editWriterBtn = new javax.swing.JButton();
        editCountryBtn = new javax.swing.JButton();
        cancelBtn13 = new javax.swing.JButton();
        cancelBtn14 = new javax.swing.JButton();
        cancelBtn15 = new javax.swing.JButton();
        cancelBtn16 = new javax.swing.JButton();
        cancelBtn17 = new javax.swing.JButton();
        cancelBtn18 = new javax.swing.JButton();
        synopsPnl = new javax.swing.JPanel();
        synopsisSP = new javax.swing.JScrollPane();
        synopsisField = new javax.swing.JTextArea();
        synopsisLbl = new javax.swing.JLabel();
        cancelBtn11 = new javax.swing.JButton();
        actorPnl = new javax.swing.JPanel();
        actorLbl = new javax.swing.JLabel();
        actorListSp = new javax.swing.JScrollPane();
        actorsList = new javax.swing.JList(){
            public String getToolTipText(MouseEvent evt) {
                int index = locationToIndex(evt.getPoint());
                fr.free.movierenamer.media.MediaPerson item = (fr.free.movierenamer.media.MediaPerson) getModel().getElementAt(index);
                String name = item.getName();
                String tooltip = "<html><h2>"+ name + "</h2><img  style=\"float: left;margin-right: 5px;\" src=\"file:/mnt/Divx/.actors/";
                tooltip += ((String)name).replaceAll(" ", "_") + ".tbn\">";
                //ArrayList<String> movies = item.getMovies();
                //for(int i=0;i<movies.size();i++)tooltip += "<p>" + movies.get(i) + "</p>";
                return  tooltip;
            }
        }
        ;
        roleLbl = new javax.swing.JLabel();
        strRoleField = new javax.swing.JTextField();
        applyBtn = new javax.swing.JButton();
        cancleBtn = new javax.swing.JButton();
        seachOnLbl = new javax.swing.JLabel();
        siteComboBox = new javax.swing.JComboBox();
        searchBtn = new javax.swing.JButton();

        NFODialog.setTitle("NFO");
        NFODialog.setModal(true);

        jButton15.setText("Cancel");
        jButton15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton15MouseReleased(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout NFODialogLayout = new javax.swing.GroupLayout(NFODialog.getContentPane());
        NFODialog.getContentPane().setLayout(NFODialogLayout);
        NFODialogLayout.setHorizontalGroup(
            NFODialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NFODialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(NFODialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton15, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 688, Short.MAX_VALUE))
                .addContainerGap())
        );
        NFODialogLayout.setVerticalGroup(
            NFODialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, NFODialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jButton15)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        movietitleField.setEditable(false);
        movietitleField.setFont(new java.awt.Font("Ubuntu", 0, 10)); // NOI18N
        movietitleField.addMouseListener(new fr.free.movierenamer.ui.res.ContextMenuFieldMouseListener());

        imdbidField.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        imdbidField.addMouseListener(new fr.free.movierenamer.ui.res.ContextMenuFieldMouseListener());

        imdbidLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        imdbidLbl.setText("ImdbId");

        runtimeField.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        runtimeField.addMouseListener(new fr.free.movierenamer.ui.res.ContextMenuFieldMouseListener());

        ratingField.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        ratingField.addMouseListener(new fr.free.movierenamer.ui.res.ContextMenuFieldMouseListener());

        runtimeLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle"); // NOI18N
        runtimeLbl.setText(bundle.getString("runtime")); // NOI18N

        ratingLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        ratingLbl.setText(bundle.getString("rating")); // NOI18N

        yearField.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        yearField.addMouseListener(new fr.free.movierenamer.ui.res.ContextMenuFieldMouseListener());

        yearLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        yearLbl.setText(bundle.getString("year")); // NOI18N

        trailerLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        trailerLbl.setText(bundle.getString("trailer")); // NOI18N

        mpaaLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        mpaaLbl.setText("Mpaa");

        originalTitleLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        originalTitleLbl.setText(bundle.getString("origTitle")); // NOI18N

        sortTitleLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        sortTitleLbl.setText(bundle.getString("sortTitle")); // NOI18N

        titleLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        titleLbl.setText(bundle.getString("title")); // NOI18N

        titleField.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        titleField.addMouseListener(new fr.free.movierenamer.ui.res.ContextMenuFieldMouseListener());

        sortTitleField.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        sortTitleField.addMouseListener(new fr.free.movierenamer.ui.res.ContextMenuFieldMouseListener());

        originalTitleField.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        originalTitleField.addMouseListener(new fr.free.movierenamer.ui.res.ContextMenuFieldMouseListener());

        mpaaField.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        mpaaField.addMouseListener(new fr.free.movierenamer.ui.res.ContextMenuFieldMouseListener());

        trailerField.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        trailerField.addMouseListener(new fr.free.movierenamer.ui.res.ContextMenuFieldMouseListener());

        taglineField.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        taglineField.addMouseListener(new fr.free.movierenamer.ui.res.ContextMenuFieldMouseListener());

        taglineLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        taglineLbl.setText("TagLine");

        cancelBtn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-cancel-2-16.png"))); // NOI18N
        cancelBtn1.setToolTipText(bundle.getString("cancel")); // NOI18N
        cancelBtn1.setMargin(new java.awt.Insets(2, 2, 2, 2));

        cancelBtn2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-cancel-2-16.png"))); // NOI18N
        cancelBtn2.setToolTipText(bundle.getString("cancel")); // NOI18N
        cancelBtn2.setMargin(new java.awt.Insets(2, 2, 2, 2));

        cancelBtn3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-cancel-2-16.png"))); // NOI18N
        cancelBtn3.setToolTipText(bundle.getString("cancel")); // NOI18N
        cancelBtn3.setMargin(new java.awt.Insets(2, 2, 2, 2));

        cancelBtn4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-cancel-2-16.png"))); // NOI18N
        cancelBtn4.setToolTipText(bundle.getString("cancel")); // NOI18N
        cancelBtn4.setMargin(new java.awt.Insets(2, 2, 2, 2));

        cancelBtn5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-cancel-2-16.png"))); // NOI18N
        cancelBtn5.setToolTipText(bundle.getString("cancel")); // NOI18N
        cancelBtn5.setMargin(new java.awt.Insets(2, 2, 2, 2));

        cancelBtn6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-cancel-2-16.png"))); // NOI18N
        cancelBtn6.setToolTipText(bundle.getString("cancel")); // NOI18N
        cancelBtn6.setMargin(new java.awt.Insets(2, 2, 2, 2));

        cancelBtn7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-cancel-2-16.png"))); // NOI18N
        cancelBtn7.setToolTipText(bundle.getString("cancel")); // NOI18N
        cancelBtn7.setMargin(new java.awt.Insets(2, 2, 2, 2));

        cancelBtn8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-cancel-2-16.png"))); // NOI18N
        cancelBtn8.setToolTipText(bundle.getString("cancel")); // NOI18N
        cancelBtn8.setMargin(new java.awt.Insets(2, 2, 2, 2));

        cancelBtn9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-cancel-2-16.png"))); // NOI18N
        cancelBtn9.setToolTipText(bundle.getString("cancel")); // NOI18N
        cancelBtn9.setMargin(new java.awt.Insets(2, 2, 2, 2));

        cancelBtn10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-cancel-2-16.png"))); // NOI18N
        cancelBtn10.setToolTipText(bundle.getString("cancel")); // NOI18N
        cancelBtn10.setMargin(new java.awt.Insets(2, 2, 2, 2));

        top250Lbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        top250Lbl.setText("Top 250");

        top250Field.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        yearField.addMouseListener(new fr.free.movierenamer.ui.res.ContextMenuFieldMouseListener());

        cancelBtn12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-cancel-2-16.png"))); // NOI18N
        cancelBtn12.setToolTipText(bundle.getString("cancel")); // NOI18N
        cancelBtn12.setMargin(new java.awt.Insets(2, 2, 2, 2));

        watchedLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        watchedLbl.setText(bundle.getString("watched")); // NOI18N

        javax.swing.GroupLayout infoPnlLayout = new javax.swing.GroupLayout(infoPnl);
        infoPnl.setLayout(infoPnlLayout);
        infoPnlLayout.setHorizontalGroup(
            infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPnlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(taglineLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(trailerLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(mpaaLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(originalTitleLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                        .addComponent(sortTitleLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(titleLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(infoPnlLayout.createSequentialGroup()
                        .addComponent(top250Lbl)
                        .addGap(18, 18, 18)
                        .addComponent(top250Field, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelBtn12, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(infoPnlLayout.createSequentialGroup()
                        .addComponent(yearLbl)
                        .addGap(37, 37, 37)
                        .addComponent(yearField, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelBtn7, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(2, 2, 2)
                .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(taglineField, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                    .addComponent(trailerField, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                    .addComponent(mpaaField, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                    .addComponent(originalTitleField, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                    .addComponent(sortTitleField, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                    .addComponent(titleField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, infoPnlLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(runtimeLbl)
                            .addComponent(imdbidLbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(imdbidField)
                            .addComponent(runtimeField, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cancelBtn8, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cancelBtn10, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(57, 57, 57)
                        .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(infoPnlLayout.createSequentialGroup()
                                .addComponent(ratingLbl)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ratingField, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(infoPnlLayout.createSequentialGroup()
                                .addComponent(watchedLbl)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(watchedChk)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cancelBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelBtn3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelBtn4, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelBtn5, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelBtn6, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelBtn9, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        infoPnlLayout.setVerticalGroup(
            infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPnlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(titleLbl)
                        .addComponent(titleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cancelBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(sortTitleLbl)
                        .addComponent(sortTitleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cancelBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(originalTitleLbl)
                        .addComponent(originalTitleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cancelBtn3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(mpaaLbl)
                        .addComponent(mpaaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cancelBtn4, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(trailerLbl)
                        .addComponent(trailerField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cancelBtn5, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cancelBtn6, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(taglineLbl)
                        .addComponent(taglineField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(infoPnlLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ratingField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ratingLbl))
                            .addComponent(cancelBtn9, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cancelBtn8, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(runtimeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(runtimeLbl))))
                    .addGroup(infoPnlLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cancelBtn7, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(yearLbl)
                                .addComponent(yearField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(imdbidField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(imdbidLbl)
                        .addComponent(top250Lbl)
                        .addComponent(top250Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cancelBtn10, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelBtn12, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(infoPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(watchedChk)
                        .addComponent(watchedLbl)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Info", infoPnl);

        studioLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        studioLbl.setText("Studio");

        directorLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        directorLbl.setText(bundle.getString("director")); // NOI18N

        genreLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        genreLbl.setText("Genre");

        setLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        setLbl.setText(bundle.getString("set")); // NOI18N

        writerLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        writerLbl.setText(bundle.getString("writer")); // NOI18N

        countryLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        countryLbl.setText(bundle.getString("country")); // NOI18N

        countryField.setEditable(false);
        countryField.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N

        writerField.setEditable(false);
        writerField.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N

        setField.setEditable(false);
        setField.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N

        genreField.setEditable(false);
        genreField.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N

        directorField.setEditable(false);
        directorField.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N

        studioField.setEditable(false);
        studioField.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N

        editStudioBtn.setFont(new java.awt.Font("DejaVu Sans", 0, 10)); // NOI18N
        editStudioBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/accessories-text-editor-6.png"))); // NOI18N
        editStudioBtn.setToolTipText(bundle.getString("edit")); // NOI18N
        editStudioBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editStudioBtnActionPerformed(evt);
            }
        });

        editDirectorBtn.setFont(new java.awt.Font("DejaVu Sans", 0, 10)); // NOI18N
        editDirectorBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/accessories-text-editor-6.png"))); // NOI18N
        editDirectorBtn.setToolTipText(bundle.getString("edit")); // NOI18N
        editDirectorBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editDirectorBtnActionPerformed(evt);
            }
        });

        editGenreBtn.setFont(new java.awt.Font("DejaVu Sans", 0, 10)); // NOI18N
        editGenreBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/accessories-text-editor-6.png"))); // NOI18N
        editGenreBtn.setToolTipText(bundle.getString("edit")); // NOI18N
        editGenreBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editGenreBtnActionPerformed(evt);
            }
        });

        editSetBtn.setFont(new java.awt.Font("DejaVu Sans", 0, 10)); // NOI18N
        editSetBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/accessories-text-editor-6.png"))); // NOI18N
        editSetBtn.setToolTipText(bundle.getString("edit")); // NOI18N
        editSetBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editSetBtnActionPerformed(evt);
            }
        });

        editWriterBtn.setFont(new java.awt.Font("DejaVu Sans", 0, 10)); // NOI18N
        editWriterBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/accessories-text-editor-6.png"))); // NOI18N
        editWriterBtn.setToolTipText(bundle.getString("edit")); // NOI18N
        editWriterBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editWriterBtnActionPerformed(evt);
            }
        });

        editCountryBtn.setFont(new java.awt.Font("DejaVu Sans", 0, 10)); // NOI18N
        editCountryBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/accessories-text-editor-6.png"))); // NOI18N
        editCountryBtn.setToolTipText(bundle.getString("edit")); // NOI18N
        editCountryBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCountryBtnActionPerformed(evt);
            }
        });

        cancelBtn13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-cancel-2-16.png"))); // NOI18N
        cancelBtn13.setToolTipText(bundle.getString("cancel")); // NOI18N
        cancelBtn13.setMargin(new java.awt.Insets(2, 2, 2, 2));

        cancelBtn14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-cancel-2-16.png"))); // NOI18N
        cancelBtn14.setToolTipText(bundle.getString("cancel")); // NOI18N
        cancelBtn14.setMargin(new java.awt.Insets(2, 2, 2, 2));

        cancelBtn15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-cancel-2-16.png"))); // NOI18N
        cancelBtn15.setToolTipText(bundle.getString("cancel")); // NOI18N
        cancelBtn15.setMargin(new java.awt.Insets(2, 2, 2, 2));

        cancelBtn16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-cancel-2-16.png"))); // NOI18N
        cancelBtn16.setToolTipText(bundle.getString("cancel")); // NOI18N
        cancelBtn16.setMargin(new java.awt.Insets(2, 2, 2, 2));

        cancelBtn17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-cancel-2-16.png"))); // NOI18N
        cancelBtn17.setToolTipText(bundle.getString("cancel")); // NOI18N
        cancelBtn17.setMargin(new java.awt.Insets(2, 2, 2, 2));

        cancelBtn18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-cancel-2-16.png"))); // NOI18N
        cancelBtn18.setToolTipText(bundle.getString("cancel")); // NOI18N
        cancelBtn18.setMargin(new java.awt.Insets(2, 2, 2, 2));

        javax.swing.GroupLayout detailPnlLayout = new javax.swing.GroupLayout(detailPnl);
        detailPnl.setLayout(detailPnlLayout);
        detailPnlLayout.setHorizontalGroup(
            detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailPnlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(studioLbl)
                    .addComponent(countryLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                    .addComponent(setLbl)
                    .addComponent(directorLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                    .addComponent(genreLbl)
                    .addComponent(writerLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(writerField, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                        .addComponent(countryField))
                    .addGroup(detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(setField, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(genreField, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(directorField, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(studioField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)))
                .addGap(12, 12, 12)
                .addGroup(detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, detailPnlLayout.createSequentialGroup()
                        .addComponent(editCountryBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelBtn18, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, detailPnlLayout.createSequentialGroup()
                        .addComponent(editWriterBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelBtn17, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, detailPnlLayout.createSequentialGroup()
                        .addComponent(editSetBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelBtn16, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, detailPnlLayout.createSequentialGroup()
                        .addComponent(editStudioBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelBtn13, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, detailPnlLayout.createSequentialGroup()
                        .addComponent(editDirectorBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelBtn14, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(detailPnlLayout.createSequentialGroup()
                        .addComponent(editGenreBtn, 0, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelBtn15, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        detailPnlLayout.setVerticalGroup(
            detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailPnlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(studioField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(studioLbl))
                    .addComponent(cancelBtn13, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editStudioBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(directorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(editDirectorBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(directorLbl))
                    .addComponent(cancelBtn14, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(genreLbl)
                        .addComponent(genreField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(editGenreBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cancelBtn15, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(detailPnlLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(setLbl)
                            .addComponent(setField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(detailPnlLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(editSetBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cancelBtn16, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(writerLbl)
                        .addComponent(writerField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(editWriterBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cancelBtn17, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(detailPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(countryLbl)
                        .addComponent(countryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(editCountryBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cancelBtn18, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(83, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(bundle.getString("details"), detailPnl); // NOI18N

        synopsisField.setColumns(20);
        synopsisField.setLineWrap(true);
        synopsisField.setRows(5);
        synopsisField.setWrapStyleWord(true);
        synopsisField.addMouseListener(new fr.free.movierenamer.ui.res.ContextMenuFieldMouseListener());
        synopsisSP.setViewportView(synopsisField);

        synopsisLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        synopsisLbl.setText("Synopsis");

        cancelBtn11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-cancel-2-16.png"))); // NOI18N
        cancelBtn11.setToolTipText(bundle.getString("cancel")); // NOI18N
        cancelBtn11.setMargin(new java.awt.Insets(2, 2, 2, 2));

        javax.swing.GroupLayout synopsPnlLayout = new javax.swing.GroupLayout(synopsPnl);
        synopsPnl.setLayout(synopsPnlLayout);
        synopsPnlLayout.setHorizontalGroup(
            synopsPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(synopsPnlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(synopsPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(synopsisSP, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
                    .addGroup(synopsPnlLayout.createSequentialGroup()
                        .addComponent(synopsisLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelBtn11, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        synopsPnlLayout.setVerticalGroup(
            synopsPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(synopsPnlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(synopsPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(synopsisLbl)
                    .addComponent(cancelBtn11, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(synopsisSP, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Synopsis", synopsPnl);

        actorLbl.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        actorLbl.setText(bundle.getString("actors")); // NOI18N

        actorsList.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        actorsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                actorsListValueChanged(evt);
            }
        });
        actorListSp.setViewportView(actorsList);

        roleLbl.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        roleLbl.setText("Role");

        strRoleField.setEditable(false);
        strRoleField.setEnabled(false);

        javax.swing.GroupLayout actorPnlLayout = new javax.swing.GroupLayout(actorPnl);
        actorPnl.setLayout(actorPnlLayout);
        actorPnlLayout.setHorizontalGroup(
            actorPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actorPnlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(actorPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(actorLbl)
                    .addGroup(actorPnlLayout.createSequentialGroup()
                        .addComponent(roleLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(actorPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(actorListSp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                            .addComponent(strRoleField, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE))))
                .addContainerGap())
        );
        actorPnlLayout.setVerticalGroup(
            actorPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actorPnlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(actorLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(actorListSp, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(actorPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(strRoleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roleLbl))
                .addContainerGap(51, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(bundle.getString("actor"), actorPnl); // NOI18N

        applyBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-ok-2.png"))); // NOI18N
        applyBtn.setText(bundle.getString("Apply")); // NOI18N
        applyBtn.setToolTipText(bundle.getString("Apply")); // NOI18N
        applyBtn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        applyBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyBtnActionPerformed(evt);
            }
        });

        cancleBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-cancel-2.png"))); // NOI18N
        cancleBtn.setText(bundle.getString("cancel")); // NOI18N
        cancleBtn.setToolTipText(bundle.getString("cancel")); // NOI18N
        cancleBtn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        cancleBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancleBtnActionPerformed(evt);
            }
        });

        seachOnLbl.setText(bundle.getString("searchOn")); // NOI18N

        siteComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "allocine.fr", "rottentomatoes.com", "allmovie.com" }));

        searchBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/system-search-3.png"))); // NOI18N
        searchBtn.setToolTipText(bundle.getString("search")); // NOI18N
        searchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(movietitleField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(seachOnLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(siteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(applyBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cancleBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(movietitleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(siteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(seachOnLbl))
                    .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addComponent(jTabbedPane1)
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(applyBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancleBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void actorsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_actorsListValueChanged
      strRoleField.setEnabled(true);
      MediaPerson actor = (MediaPerson) actorsList.getSelectedValue();
      strRoleField.setText(Utils.arrayToString(actor.getRoles(), " | ", 0));
    }//GEN-LAST:event_actorsListValueChanged

    private void jButton15MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton15MouseReleased
      NFODialog.setVisible(false);
    }//GEN-LAST:event_jButton15MouseReleased

    private void editStudioBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editStudioBtnActionPerformed
      openEditor(studioField, " \\| ");
    }//GEN-LAST:event_editStudioBtnActionPerformed

    private void editDirectorBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editDirectorBtnActionPerformed
      openEditor(directorField, " \\| ");
    }//GEN-LAST:event_editDirectorBtnActionPerformed

    private void editGenreBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editGenreBtnActionPerformed
      openEditor(genreField, " \\| ");
    }//GEN-LAST:event_editGenreBtnActionPerformed

    private void editSetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editSetBtnActionPerformed
      openEditor(setField, " \\| ");
    }//GEN-LAST:event_editSetBtnActionPerformed

    private void editWriterBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editWriterBtnActionPerformed
      openEditor(writerField, " \\| ");
    }//GEN-LAST:event_editWriterBtnActionPerformed

    private void editCountryBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCountryBtnActionPerformed
      openEditor(countryField, " \\| ");
    }//GEN-LAST:event_editCountryBtnActionPerformed

    private void cancleBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancleBtnActionPerformed
      dispose();
    }//GEN-LAST:event_cancleBtnActionPerformed

    private void applyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyBtnActionPerformed
//A refaire , ajouter les API ID
//      movieInfo.setImdbId(imdbidField.getText());
      movieInfo.setMpaa(mpaaField.getText());
      movieInfo.setOrigTitle(originalTitleField.getText());
      movieInfo.setRating(ratingField.getText());
      movieInfo.setRuntime(runtimeField.getText());
      movieInfo.setSortTitle(sortTitleField.getText());
      movieInfo.setTagline(taglineField.getText());
      movieInfo.setTitle(titleField.getText());
      movieInfo.setTrailer(trailerField.getText());
      movieInfo.setYear(yearField.getText());
      movieInfo.setSynopsis(synopsisField.getText());
      movieInfo.setOutline(synopsisField.getText());
      movieInfo.setTop250(top250Field.getText());
      movieInfo.setWatched(watchedChk.isSelected());
      movieInfo.setStudios(Utils.stringToArray(studioField.getText(), " \\| "));
      movieInfo.setGenre(Utils.stringToArray(genreField.getText(), " \\| "));
      movieInfo.setSet(Utils.stringToArray(setField.getText(), " \\| "));
      movieInfo.setCountries(Utils.stringToArray(countryField.getText(), " \\| "));

      ArrayList<MediaPerson> person = new ArrayList<MediaPerson>();
      ArrayList<String> array = Utils.stringToArray(directorField.getText(), " \\| ");
      for (int i = 0; i < array.size(); i++) {
        person.add(new MediaPerson(array.get(i), null, MediaPerson.DIRECTOR));
      }
      movieInfo.setDirectors(person);

      person = new ArrayList<MediaPerson>();
      array = Utils.stringToArray(writerField.getText(), " \\| ");
      for (int i = 0; i < array.size(); i++) {
        person.add(new MediaPerson(array.get(i), null, MediaPerson.WRITER));
      }
      movieInfo.setDirectors(person);

      firePropertyChange("movieInfo", null, movieInfo);
      dispose();
    }//GEN-LAST:event_applyBtnActionPerformed

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
      try {
        int index = siteComboBox.getSelectedIndex();
        String url;
        switch (index) {
          case ALLOCINE:
            url = allocineSearchUrl + URLEncoder.encode(movieInfo.getTitle(), "ISO-8859-1");
            break;
          case ROTTEN:
            url = rottenUrl + URLEncoder.encode(movieInfo.getTitle(), "ISO-8859-1");
            break;
          case ALLMOVIE:
            url = allmovieUrl + URLEncoder.encode(movieInfo.getTitle(), "ISO-8859-1");
            break;
          default:
            return;
        }
        Desktop.getDesktop().browse(new URI(url));
      } catch (IOException ex) {
        Settings.LOGGER.log(Level.SEVERE, null, ex);
      } catch (URISyntaxException ex) {
        Settings.LOGGER.log(Level.SEVERE, null, ex);
      }
    }//GEN-LAST:event_searchBtnActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog NFODialog;
    private javax.swing.JLabel actorLbl;
    private javax.swing.JScrollPane actorListSp;
    private javax.swing.JPanel actorPnl;
    private javax.swing.JList actorsList;
    private javax.swing.JButton applyBtn;
    private javax.swing.JButton cancelBtn1;
    private javax.swing.JButton cancelBtn10;
    private javax.swing.JButton cancelBtn11;
    private javax.swing.JButton cancelBtn12;
    private javax.swing.JButton cancelBtn13;
    private javax.swing.JButton cancelBtn14;
    private javax.swing.JButton cancelBtn15;
    private javax.swing.JButton cancelBtn16;
    private javax.swing.JButton cancelBtn17;
    private javax.swing.JButton cancelBtn18;
    private javax.swing.JButton cancelBtn2;
    private javax.swing.JButton cancelBtn3;
    private javax.swing.JButton cancelBtn4;
    private javax.swing.JButton cancelBtn5;
    private javax.swing.JButton cancelBtn6;
    private javax.swing.JButton cancelBtn7;
    private javax.swing.JButton cancelBtn8;
    private javax.swing.JButton cancelBtn9;
    private javax.swing.JButton cancleBtn;
    private javax.swing.JTextField countryField;
    private javax.swing.JLabel countryLbl;
    private javax.swing.JPanel detailPnl;
    private javax.swing.JTextField directorField;
    private javax.swing.JLabel directorLbl;
    private javax.swing.JButton editCountryBtn;
    private javax.swing.JButton editDirectorBtn;
    private javax.swing.JButton editGenreBtn;
    private javax.swing.JButton editSetBtn;
    private javax.swing.JButton editStudioBtn;
    private javax.swing.JButton editWriterBtn;
    private javax.swing.JTextField genreField;
    private javax.swing.JLabel genreLbl;
    private javax.swing.JTextField imdbidField;
    private javax.swing.JLabel imdbidLbl;
    private javax.swing.JPanel infoPnl;
    private javax.swing.JButton jButton15;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField movietitleField;
    private javax.swing.JTextField mpaaField;
    private javax.swing.JLabel mpaaLbl;
    private javax.swing.JTextField originalTitleField;
    private javax.swing.JLabel originalTitleLbl;
    private javax.swing.JTextField ratingField;
    private javax.swing.JLabel ratingLbl;
    private javax.swing.JLabel roleLbl;
    private javax.swing.JTextField runtimeField;
    private javax.swing.JLabel runtimeLbl;
    private javax.swing.JLabel seachOnLbl;
    private javax.swing.JButton searchBtn;
    private javax.swing.JTextField setField;
    private javax.swing.JLabel setLbl;
    private javax.swing.JComboBox siteComboBox;
    private javax.swing.JTextField sortTitleField;
    private javax.swing.JLabel sortTitleLbl;
    private javax.swing.JTextField strRoleField;
    private javax.swing.JTextField studioField;
    private javax.swing.JLabel studioLbl;
    private javax.swing.JPanel synopsPnl;
    private javax.swing.JTextArea synopsisField;
    private javax.swing.JLabel synopsisLbl;
    private javax.swing.JScrollPane synopsisSP;
    private javax.swing.JTextField taglineField;
    private javax.swing.JLabel taglineLbl;
    private javax.swing.JTextField titleField;
    private javax.swing.JLabel titleLbl;
    private javax.swing.JTextField top250Field;
    private javax.swing.JLabel top250Lbl;
    private javax.swing.JTextField trailerField;
    private javax.swing.JLabel trailerLbl;
    private javax.swing.JCheckBox watchedChk;
    private javax.swing.JLabel watchedLbl;
    private javax.swing.JTextField writerField;
    private javax.swing.JLabel writerLbl;
    private javax.swing.JTextField yearField;
    private javax.swing.JLabel yearLbl;
    // End of variables declaration//GEN-END:variables
}
