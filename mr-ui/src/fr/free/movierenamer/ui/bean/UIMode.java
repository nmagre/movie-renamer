/*
 * Movie Renamer
 * Copyright (C) 2012-2014 Nicolas Magré
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
package fr.free.movierenamer.ui.bean;

import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.TvShowInfo;
import fr.free.movierenamer.scraper.MediaScraper;
import fr.free.movierenamer.scraper.ScraperManager;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.settings.XMLSettings.IProperty;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;

/**
 * Enum UIMode
 *
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public enum UIMode {

    MOVIEMODE(UIUtils.i18n.getLanguageKey("movieMode", "toptb"), "movieMode", MediaType.MOVIE, "ui/24/movie.png", MovieInfo.class, UIMovieInfo.class);
    //TVSHOWMODE(UIUtils.i18n.getLanguageKey("tvshowMode", "toptb"), "tvshowMode", MediaType.TVSHOW, "ui/24/tv.png", TvShowInfo.class, UITvShowInfo.class);
    private final MediaType mediaType;
    private final String title;
    private final String titleMode;
    private final ImageIcon icon;
    private final DefaultComboBoxModel<UIScraper> scraperModel = new DefaultComboBoxModel<>();
    private final Class<?> infoClazz;
    private final Class<?> uiInfoClazz;
    private final IProperty[] renameOptions;
    private String fileFormat;

    private UIMode(String title, String titleMode, MediaType mediaType, String imgName, Class<?> infoClazz, Class<?> uiInfoClazz, IProperty... renameOptions) {
        this.title = title;
        this.titleMode = titleMode;
        this.mediaType = mediaType;
        this.renameOptions = renameOptions;
        this.icon = new ImageIcon(ImageUtils.getImageFromJAR(imgName));
        this.infoClazz = infoClazz;
        this.uiInfoClazz = uiInfoClazz;
        fileFormat = Settings.getInstance().getMediaFilenameFormat(mediaType);

        List<MediaScraper> mediaScrapers = ScraperManager.getMediaScrapers(mediaType);
        for (MediaScraper mediaScraper : mediaScrapers) {
            scraperModel.addElement(new UIScraper(mediaScraper));
        }
        scraperModel.setSelectedItem(new UIScraper(ScraperManager.getMediaScraper(mediaType)));
    }

    public String getTitle() {
        return title;
    }

    public String getTitleMode() {
        return titleMode;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public DefaultComboBoxModel<UIScraper> getScraperModel() {
        return scraperModel;
    }

    public UIScraper getSelectedScraper() {
        return (UIScraper) scraperModel.getSelectedItem();
    }

    public void setUniversalScraper() {
        UIScraper scraper;
        for (int i = 0; i < scraperModel.getSize(); i++) {
            scraper = scraperModel.getElementAt(i);
            if (scraper.getName().equals("Universal")) {
                scraperModel.setSelectedItem(scraper);
                break;
            }
        }
    }

    public void setScraper(UIScraper scraper) {
        if (scraperModel.getIndexOf(scraper) >= 0) {
            scraperModel.setSelectedItem(scraper);
        }
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileformat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public UIMediaInfo<?> toUIInfo(MediaInfo info) throws Exception {
        return (UIMediaInfo<?>) uiInfoClazz.getConstructor(infoClazz).newInstance(info);
    }

}
