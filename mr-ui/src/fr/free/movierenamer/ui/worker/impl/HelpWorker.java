/*
 * Movie Renamer
 * Copyright (C) 2014 Nicolas Magré
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

import dw.xmlrpc.DokuJClient;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.dialog.SettingsHelpDialog;
import fr.free.movierenamer.ui.worker.Worker;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.CacheObject;
import java.util.Locale;

/**
 * Class HelpWorker
 *
 * @author Nicolas Magré
 */
public class HelpWorker extends Worker<String> {

  private String id;
  private final String host = "http://" + UISettings.HOST + "/wiki/lib/exe/xmlrpc.php";
  private final SettingsHelpDialog helpdial;

  public HelpWorker(MovieRenamer mr, String id, SettingsHelpDialog helpdial) {
    super(mr);
    this.id = id;
    this.helpdial = helpdial;
  }

  private CacheObject getCache() {
    return new CacheObject(host, Cache.getCache("short"));
  }

  @Override
  protected String executeInBackground() throws Exception {

    String user = "viewer";// FIXME
    String pwd = "pviewer";// FIXME

    CacheObject cache = getCache();

    String html = cache.getData(id, Locale.ENGLISH, String.class);
    if (html != null) {
      return html;
    }

    DokuJClient client = new DokuJClient(host, user, pwd);
    html = client.getPageHTML(id);
    if (html.isEmpty() && !id.startsWith("wiki")) {
      id = "wiki" + id.substring(2);
      html = client.getPageHTML(id);
    }

    return cache.putData(id, Locale.ENGLISH, html);
  }

  @Override
  protected void workerDone() throws Exception {
    String html = get();
    helpdial.setText(html);
  }

  @Override
  public String getDisplayName() {
    return "Help worker"; // FIXME i18n
  }

  @Override
  public WorkerId getWorkerId() {
    return WorkerId.HELP;
  }

}
