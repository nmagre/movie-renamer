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

import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.text.WebTextField;
import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.FileInfo;
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
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.settings.XMLSettings;
import fr.free.movierenamer.settings.XMLSettings.SettingsPropertyType;
import fr.free.movierenamer.ui.bean.UIEnum;
import fr.free.movierenamer.ui.swing.UIManager;
import fr.free.movierenamer.ui.swing.panel.generator.SettingPanelGen;
import fr.free.movierenamer.utils.ScraperUtils;
import fr.free.movierenamer.utils.StringUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class UIMovieTestSettings
 *
 * @author Nicolas Magré
 */
public class UIMovieTestSettings extends UITestSettings implements XMLSettings.IMediaProperty {

    private static final MovieInfo movieInfo;
    private static final FileInfo fileInfo;
    private final XMLSettings.SettingsType type;
    private final XMLSettings.SettingsSubType subType;

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

        fileInfo = new FileInfo(new File("/home/movie/Matrix.mkv"));
        MediaTag mtag = fileInfo.getMediaTag();

        List<MediaAudio> maudios = new ArrayList<>();
        List<MediaSubTitle> msubs = new ArrayList<>();
        MediaVideo mVideo = new MediaVideo();
        MediaAudio maudio1 = new MediaAudio(0);
        MediaAudio maudio2 = new MediaAudio(1);
        MediaSubTitle msub1 = new MediaSubTitle(0);
        MediaSubTitle msub2 = new MediaSubTitle(1);
        MediaSubTitle msub3 = new MediaSubTitle(2);

        mVideo.setAspectRatio(1.77F);
        mVideo.setCodec("x264");
        mVideo.setFrameCount(204884L);
        mVideo.setFrameRate(24.0);
        mVideo.setHeight(1080);
        mVideo.setWidth(1920);
        mVideo.setScanType("Progressive");

        maudio1.setBitRate(1509000);
        maudio1.setBitRateMode("CBR");
        maudio1.setChannel("5.1");
        maudio1.setCodec("DTS");
        maudio1.setLanguage(Locale.ENGLISH);
        maudio1.setNbChannel("6");
        maudio1.setTitle("ENGLISH DTS 5.1 1536 Kbps");

        maudio2.setBitRate(1509000);
        maudio2.setBitRateMode("CBR");
        maudio2.setChannel("5.1");
        maudio2.setCodec("DTS");
        maudio2.setLanguage(Locale.FRENCH);
        maudio2.setNbChannel("6");
        maudio2.setTitle("French DTS 5.1 1536 Kbps");

        msub1.setLanguage(Locale.ENGLISH);
        msub1.setTitle("English");

        msub2.setLanguage(Locale.FRENCH);
        msub2.setTitle("French");

        msub3.setLanguage(Locale.FRENCH);
        msub3.setTitle("French subforced");

        maudios.add(maudio1);
        maudios.add(maudio2);

        msubs.add(msub1);
        msubs.add(msub2);
        msubs.add(msub3);

        mtag.setMediaVideo(mVideo);
        mtag.setDuration(8536834L);
        mtag.setContainerFormat("Matroska");
        mtag.setMediaAudio(maudios);
        mtag.setMediaSubtitles(msubs);

        movieInfo = new MovieInfo(info, multipleInfo, ids);
        movieInfo.setCasting(persons);
    }

    public UIMovieTestSettings(XMLSettings.SettingsType type, XMLSettings.SettingsSubType subType) {
        this.type = type;
        this.subType = subType;
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

    @Override
    public String getResult(Map<WebCheckBox, SettingPanelGen.SettingsProperty> checkboxs,
            Map<WebTextField, SettingPanelGen.SettingsProperty> fields, Map<WebComboBox, SettingPanelGen.SettingsProperty> comboboxs) {

        SettingPanelGen.SettingsProperty sproperty;
        XMLSettings.IProperty propertie;
        WebCheckBox checkBox;
        WebTextField field;
        Object item;

        boolean rmDupSpace = false;
        boolean reservedCharacter = false;
        boolean trim = false;
        boolean isRomanUpper = false;
        boolean replaceSpace = false;
        String format = "";
        String separator = "";
        String slimit = "";
        String replaceSpaceBy = "";
        StringUtils.CaseConversionType caseType = StringUtils.CaseConversionType.FIRSTLO;

        for (Entry<WebCheckBox, SettingPanelGen.SettingsProperty> entry : checkboxs.entrySet()) {
            sproperty = entry.getValue();
            propertie = sproperty.getProperty();
            if (sproperty.getMediaType() != null && sproperty.getMediaType() != Media.MediaType.MOVIE) {
                continue;
            }

            if (propertie.getType() != UIManager.formatTypeTestLoc) {
                continue;
            }

            checkBox = entry.getKey();
            if (sproperty.getMediaType() == null) {
                switch ((Settings.SettingsProperty) propertie) {
                    case filenameRmDupSpace:
                        rmDupSpace = checkBox.isSelected();
                        break;

                    case reservedCharacter:
                        reservedCharacter = checkBox.isSelected();
                        break;

                    case filenameTrim:
                        trim = checkBox.isSelected();
                        break;

                    case filenameRomanUpper:
                        isRomanUpper = checkBox.isSelected();
                        break;

                }
            } else {

                switch ((Settings.SettingsMediaProperty) propertie) {
                    case mediaFilenameReplaceSpace:
                        replaceSpace = checkBox.isSelected();
                        break;
                }
            }

        }

        for (Entry<WebTextField, SettingPanelGen.SettingsProperty> entry : fields.entrySet()) {
            sproperty = entry.getValue();
            propertie = sproperty.getProperty();
            if (sproperty.getMediaType() != null && sproperty.getMediaType() != Media.MediaType.MOVIE) {
                continue;
            }

            if (propertie.getType() != UIManager.formatTypeTestLoc) {
                continue;
            }

            field = entry.getKey();
            if (sproperty.getMediaType() != null) {
                switch ((Settings.SettingsMediaProperty) propertie) {
                    case mediaFilenameFormat:
                        format = field.getText();
                        break;

                    case mediaFilenameSeparator:
                        separator = field.getText();
                        break;

                    case mediaFilenameLimit:
                        slimit = field.getText();
                        break;

                    case mediaFilenameReplaceSpaceBy:
                        replaceSpaceBy = field.getText();
                        break;
                }
            }
        }

        for (Entry<WebComboBox, SettingPanelGen.SettingsProperty> entry : comboboxs.entrySet()) {
            sproperty = entry.getValue();
            propertie = sproperty.getProperty();
            if (sproperty.getMediaType() != null && sproperty.getMediaType() != Media.MediaType.MOVIE) {
                continue;
            }

            if (propertie.getType() != UIManager.formatTypeTestLoc) {
                continue;
            }

            if (sproperty.getMediaType() != null) {
                item = entry.getKey().getSelectedItem();
                switch ((Settings.SettingsMediaProperty) propertie) {
                    case mediaFilenameCase:
                        caseType = (StringUtils.CaseConversionType) ((UIEnum) item).getValue();
                        break;
                }
            }
        }

        int limit = 3;
        try {
            limit = Integer.parseInt(slimit);
        } catch (Exception ex) {

        }

        return movieInfo.getRenamedTitle(fileInfo, format, caseType, separator, limit, reservedCharacter, rmDupSpace,
                trim, isRomanUpper, replaceSpace, replaceSpaceBy);
    }

    public SettingsPropertyType getPropertyType() {
        return SettingsPropertyType.NONE;
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

    @Override
    public Class<?> getKclass() {
        return null;
    }

    @Override
    public Object getDefaultValue(Media.MediaType mt) {
        return null;
    }

    @Override
    public String getValue(Media.MediaType mt) {
        return null;
    }

    @Override
    public void setValue(Media.MediaType mt, Object o) throws IOException {

    }

    @Override
    public boolean hasMediaType(Media.MediaType mt) {
        return true;
    }

    @Override
    public Class<?> getVclass() {
        return UITestSettings.class;
    }

    @Override
    public String name() {
        return "test";
    }

    @Override
    public XMLSettings.SettingsType getType() {
        return type;
    }

    @Override
    public XMLSettings.SettingsSubType getSubType() {
        return subType;
    }

}
