/*
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
package fr.free.movierenamer.ui.worker.impl;

import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.bean.UIEvent.Event;
import fr.free.movierenamer.ui.bean.UIUpdate;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.worker.Worker;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.CacheObject;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.net.URL;
import java.util.Locale;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Class UpdateWorker
 *
 * @author Nicolas Magré
 */
public class CheckUpdateWorker extends Worker<UIUpdate> {

  private final boolean showNoUpdate;

  public CheckUpdateWorker(MovieRenamer mr, boolean showNoUpdate) {
    super(mr);
    this.showNoUpdate = showNoUpdate;
  }

  private CacheObject getCache() {
    return new CacheObject(UISettings.HOST, Cache.getCache("short"));
  }

  @Override
  protected UIUpdate executeInBackground() throws Exception {
    CacheObject cache = getCache();

    String version = UISettings.getApplicationVersionNumber();
    UIUpdate uiUpdate = /*cache.getData(version, Locale.ENGLISH, UIUpdate.class);*/ null;
    if (uiUpdate != null) {
      return uiUpdate;
    }

    URL update = new URL("http", UISettings.HOST, "/updater/updater.php?version=" + version);
    Document dom = URIRequest.getXmlDocument(update.toURI());
    Node root = XPathUtils.selectNode("/update", dom);
    String sversion = XPathUtils.getAttribute("version", root);
    String descen = XPathUtils.selectString("description/en", root);
    String descfr = XPathUtils.selectString("description/fr", root);
    boolean available = Boolean.parseBoolean(XPathUtils.selectString("available", root));

    uiUpdate = new UIUpdate(sversion, available, descen, descfr);

    return cache.putData(version, Locale.ENGLISH, uiUpdate);
  }

  @Override
  protected void workerDone() throws Exception {
    UIUpdate update = get();
    if(update == null) {
      return;
    }
    
    if (update.isUpdateAvailable()) {
      UIEvent.fireUIEvent(Event.UPDATE_AVAILABLE, null, update);
    } else if (showNoUpdate) {
      UIEvent.fireUIEvent(Event.NO_UPDATE, null, update);
    }
  }

  @Override
  public String getParam() {
    return null;
  }

  @Override
  public String getDisplayName() {
    return "Update worker";// FIXME i18n
  }

}
