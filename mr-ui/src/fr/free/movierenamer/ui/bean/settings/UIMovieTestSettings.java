/*
 * Movie Renamer
 * Copyright (C) 2015 Nicolas Magré
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
package fr.free.movierenamer.ui.bean.settings;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.MediaInfo.MediaInfoProperty;
import fr.free.movierenamer.info.MediaInfo.MediaProperty;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieMultipleProperty;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.info.VideoInfo.VideoProperty;
import fr.free.movierenamer.mediainfo.MediaAudio;
import fr.free.movierenamer.mediainfo.MediaSubTitle;
import fr.free.movierenamer.mediainfo.MediaTag;
import fr.free.movierenamer.mediainfo.MediaVideo;
import fr.free.movierenamer.settings.XMLSettings;
import fr.free.movierenamer.settings.XMLSettings.SettingsPropertyType;
import fr.free.movierenamer.ui.swing.ITestActionListener;
import fr.free.movierenamer.utils.ScraperUtils;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Class UIMovieTestSettings
 *
 * @author Nicolas Magré
 */
public class UIMovieTestSettings extends UITestSettings implements ITestActionListener {

    private static final MovieInfo movieInfo;

    static {
        Map<MediaInfoProperty, String> info = new HashMap<>();
        Map<MovieMultipleProperty, List<String>> multipleInfo = new HashMap<>();
        List<MediaAudio> audios = new ArrayList<>();
        List<MediaSubTitle> subtitles = new ArrayList<>();
        List<String> genres = new ArrayList<>();
        List<String> countries = new ArrayList<>();
        List<IdInfo> ids = new ArrayList<>();

        info.put(MediaProperty.rating, "8.7");
        info.put(MediaProperty.title, "Matrix");
        info.put(MediaProperty.year, "1999");
        info.put(MediaProperty.originalTitle, "The Matrix");

        info.put(MovieProperty.certificationCode, "R");
        info.put(VideoProperty.releasedDate, "1999-03-31");
        info.put(VideoProperty.runtime, "136");
        info.put(MovieProperty.collection, "Matrix");

        genres.add("Action");
        genres.add("Adventure");
        genres.add("Sci-Fi");

        countries.add("USA");
        countries.add("Australia");

        multipleInfo.put(MovieInfo.MovieMultipleProperty.genres, genres);
        multipleInfo.put(MovieInfo.MovieMultipleProperty.countries, countries);

        ids.add(new IdInfo(133093, ScraperUtils.AvailableApiIds.IMDB));
        ids.add(new IdInfo(19776, ScraperUtils.AvailableApiIds.ALLOCINE));
        ids.add(new IdInfo(603, ScraperUtils.AvailableApiIds.THEMOVIEDB));
        ids.add(new IdInfo(12897, ScraperUtils.AvailableApiIds.ROTTENTOMATOES));
        ids.add(new IdInfo(301, ScraperUtils.AvailableApiIds.KINOPOISK));
        movieInfo = new MovieInfo(info, multipleInfo, ids);

        MediaAudio audio = new MediaAudio(0);
        MediaAudio audio1 = new MediaAudio(1);

        audio.setBitRate(1509750);
        audio1.setBitRate(754500);
        audio.setBitRateMode("CBR");
        audio1.setBitRateMode("CBR");
        audio.setChannel("5.1");
        audio1.setChannel("2.0");
        audio.setCodec("DTS");
        audio1.setCodec("MP3");
        audio.setLanguage(Locale.ENGLISH);
        audio1.setLanguage(Locale.FRENCH);
        audio.setTitle("English DTS 1509kbps");
        audio1.setTitle("French MP3");
        audios.add(audio);
        audios.add(audio1);

        MediaSubTitle subtitle = new MediaSubTitle(0);
        MediaSubTitle subtitle1 = new MediaSubTitle(1);
        subtitle.setLanguage(Locale.ENGLISH);
        subtitle.setTitle("English subforced");
        subtitle1.setLanguage(Locale.FRENCH);
        subtitle1.setTitle("French");
        subtitles.add(subtitle);
        subtitles.add(subtitle1);

        MediaVideo mvideo = new MediaVideo();
        mvideo.setAspectRatio(1.778F);
        mvideo.setCodec("divx");
        mvideo.setFrameCount(196072L);
        mvideo.setFrameRate(23.976);
        mvideo.setHeight(1080);
        mvideo.setScanType("Progressive");
        mvideo.setWidth(1920);

        MediaTag mediaTag = new MediaTag(null);
        mediaTag.setContainerFormat("Matroska");
        mediaTag.setDuration(9701696L);
        mediaTag.setMediaAudio(audios);
        mediaTag.setMediaSubtitles(subtitles);
        mediaTag.setMediaVideo(mvideo);

        Map<CastingInfo.PersonProperty, String> d = new HashMap<>();
        Map<CastingInfo.PersonProperty, String> d1 = new HashMap<>();
        d.put(CastingInfo.PersonProperty.job, "DIRECTOR");
        d.put(CastingInfo.PersonProperty.name, "Andy Wachowski");
        d1.put(CastingInfo.PersonProperty.job, "DIRECTOR");
        d1.put(CastingInfo.PersonProperty.name, "Lana Wachowski");

        Map<CastingInfo.PersonProperty, String> a = new HashMap<>();
        Map<CastingInfo.PersonProperty, String> a1 = new HashMap<>();
        Map<CastingInfo.PersonProperty, String> a2 = new HashMap<>();
        Map<CastingInfo.PersonProperty, String> a3 = new HashMap<>();
        Map<CastingInfo.PersonProperty, String> a4 = new HashMap<>();
        a.put(CastingInfo.PersonProperty.job, "ACTOR");
        a1.put(CastingInfo.PersonProperty.job, "ACTOR");
        a2.put(CastingInfo.PersonProperty.job, "ACTOR");
        a3.put(CastingInfo.PersonProperty.job, "ACTOR");
        a4.put(CastingInfo.PersonProperty.job, "ACTOR");
        a.put(CastingInfo.PersonProperty.name, "Keanu Reeves");
        a1.put(CastingInfo.PersonProperty.name, "Laurence Fishburne");
        a2.put(CastingInfo.PersonProperty.name, "Carrie-Anne Moss");
        a3.put(CastingInfo.PersonProperty.name, "Hugo Weaving");
        a4.put(CastingInfo.PersonProperty.name, "Gloria Foster");

        List<CastingInfo> persons = new ArrayList<>();
        persons.add(new CastingInfo(d, null));
        persons.add(new CastingInfo(d1, null));
        persons.add(new CastingInfo(a, null));
        persons.add(new CastingInfo(a1, null));
        persons.add(new CastingInfo(a2, null));
        persons.add(new CastingInfo(a3, null));
        persons.add(new CastingInfo(a4, null));
        movieInfo.setCasting(persons);

    }

    public UIMovieTestSettings(XMLSettings.SettingsType type, XMLSettings.SettingsSubType subType) {
        super(type, subType);
    }
//
//    @Override
//    public ITestActionListener getActionListener() {
//
//        //fields.put(MediaInfo.MediaProperty.title, "Matrix");
//        return new ITestActionListener() {
//            String result = "";
//
//            @Override
//            public String getResult() {
//                return result;
//            }
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                Map<WebTextField, XMLSettings.IProperty> fields = panels.get(mti).getField();
//                Map<WebCheckBox, XMLSettings.IProperty> checkbox = panels.get(mti).getCheckbox();
//                Map<WebComboBox, XMLSettings.IProperty> combobox = panels.get(mti).getCombobox();
//                /* result = info.getRenamedTitle(null, fields.get(Settings.SettingsProperty.movieFilenameFormat).getText(),
//                 (StringUtils.CaseConversionType) ((UIEnum) combobox.get(Settings.SettingsProperty.movieFilenameCase).getSelectedItem()).getValue(),
//                 fields.get(Settings.SettingsProperty.movieFilenameSeparator).getText(),
//                 Integer.parseInt(fields.get(Settings.SettingsProperty.movieFilenameLimit).getText()),// FIXME check if it is an integer before
//                 checkbox.get(Settings.SettingsProperty.reservedCharacter).isSelected(),
//                 checkbox.get(Settings.SettingsProperty.filenameRmDupSpace).isSelected(),
//                 checkbox.get(Settings.SettingsProperty.filenameTrim).isSelected(),
//                 checkbox.get(Settings.SettingsProperty.filenameReplaceSpace).isSelected(),
//                 fields.get(Settings.SettingsProperty.filenameReplaceSpaceBy).getText());// FIXME add a dummy fileinfo
//                 */
//            }
//        };
//
//    }
//
//    @Override
//    public String getResult() {
//
//    }

    @Override
    public void actionPerformed(ActionEvent ae) {

    }

    @Override
    public SettingsPropertyType getPropertyType() {
        return SettingsPropertyType.NONE;
    }

    @Override
    public String getResult() {
        return "";
    }

    @Override
    public boolean isChild() {
        return false;
    }

    @Override
    public XMLSettings.IProperty getParent() {
        return null;
    }

    @Override
    public boolean hasChild() {
        return false;// Only boolean can have a child 
    }

    @Override
    public void setHasChild() {
        // Only boolean can have a child 
    }

}
