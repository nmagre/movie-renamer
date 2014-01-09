/**
 * This class provides language default updates for JTabbedPane component. It
 * uses provided language key and tab indices to determine single tab
 * translation key. Basically if you provide "my.tab" language key for tabbed
 * pane, first tab translation should have "my.tab.0" key.
 *
 * @author Mikle Garin
 */
package fr.free.movierenamer.ui.swing;

import com.alee.managers.language.LanguageManager;
import com.alee.managers.language.data.Value;
import com.alee.managers.language.updaters.DefaultLanguageUpdater;
import javax.swing.JTabbedPane;

/**
 * This class provides language default updates for JTabbedPane component. It
 * uses provided language key and tab indices to determine single tab
 * translation key. Basically if you provide "my.tab" language key for tabbed
 * pane, first tab translation should have "my.tab.0" key.
 *
 * @author Mikle Garin
 */
public class JTabbedPaneLU extends DefaultLanguageUpdater<JTabbedPane> {

  /**
   * {@inheritDoc}
   */
  @Override
  public void update(final JTabbedPane c, final String key, final Value value, final Object... data) {
    // Running through all tabs
    for (int i = 0; i < c.getTabCount(); i++) {
      // Updating tab text and mnemonic
      final Value tabValue = LanguageManager.getNotNullValue(key + "." + c.getComponentAt(i).getName());
      final String text = getDefaultText(tabValue, data);
      c.setTitleAt(i, text != null ? text : null);
      c.setMnemonicAt(i, text != null && value.getMnemonic() != null ? value.getMnemonic() : 0);
    }
  }
}
