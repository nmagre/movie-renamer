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
package fr.free.movierenamer.ui.worker.impl;

import fr.free.movierenamer.exception.NoInfoException;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.info.VideoInfo;
import fr.free.movierenamer.scraper.MediaScraper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.bean.UIMediaInfo;
import fr.free.movierenamer.ui.bean.UISearchResult;
import fr.free.movierenamer.ui.swing.UIManager.UIMode;
import fr.free.movierenamer.ui.swing.panel.MediaPanel;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.ui.worker.Worker;
import fr.free.movierenamer.utils.StringUtils;

/**
 * Class SearchMediaInfosWorker
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaInfoWorker extends Worker<UIMediaInfo<?>> {

    private final MediaScraper<Media, MediaInfo> scraper;
    private final UISearchResult searchResult;
    private final UIMode mode;

    /**
     * Constructor arguments
     *
     * @param mr
     * @param searchResult
     */
    @SuppressWarnings("unchecked")
    public SearchMediaInfoWorker(final MovieRenamer mr, final UISearchResult searchResult, UIMode mode) {
        super(mr);
        this.searchResult = searchResult;
        this.scraper = (searchResult != null) ? (MediaScraper<Media, MediaInfo>) searchResult.getScraper() : null;
        this.mode = mode;
    }

    @Override
    public UIMediaInfo<?> executeInBackground() throws Exception {
        UIMediaInfo<?> info = null;
        if (searchResult != null && scraper != null) {
            MediaInfo minfo = scraper.getInfo(searchResult.getSearchResult());
            info = mode.toUIInfo(minfo);

            if (minfo instanceof VideoInfo) {// FIXME should not be here
                if (Settings.MEDIAINFO && Settings.getInstance().isUseFileRuntime(searchResult.getMediaType())) {
                    UIFile uifile = (UIFile) mr.getMediaList().getSelectedValue();
                    Long runtime = uifile.getFileInfo().getMediaTag().getDuration();
                    if (runtime != null && runtime > 0) {
                        info.set(VideoInfo.VideoProperty.runtime, StringUtils.durationMsInMinute(runtime));
                    }
                }
            }
        }

        return info;
    }

    @Override
    protected void workerDone() throws Exception {

        try {
            UIMediaInfo<?> info = get();
            @SuppressWarnings("unchecked")
            MediaPanel<UIMediaInfo<?>, ?> mediaPanel = (MediaPanel<UIMediaInfo<?>, ?>) mr.getMediaPanel();
            if (mediaPanel != null) {
                mediaPanel.setInfo(info);
            }

            mr.updateRenamedTitle();
            mr.setRenameFieldEnabled();
            mr.setRenamebuttonEnabled();

        } catch (Exception ex) {

            if (ex instanceof NoInfoException) {
                // Search info failed, we let the user try again by clearing selection
                mr.getSearchResultList().clearSelection();
            }
            throw ex;
        }

    }

    @Override
    public String getDisplayName() {
        return UIUtils.i18n.getLanguage("main.statusTb.searchinfo", false);
    }

    @Override
    public WorkerId getWorkerId() {
        return WorkerId.SEARCH_INFO;
    }
}
