package xyz.oelderoth.runelite.forestry.ui.builders.component;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

@SuppressWarnings("unused")
public class PopupMenuBuilder extends AbstractComponentBuilder<JPopupMenu, PopupMenuBuilder>
{
	private final JPopupMenu menu;

	public PopupMenuBuilder()
	{
		menu = new JPopupMenu();
	}

	public PopupMenuBuilder menuItem(JMenuItem item) {
		menu.add(item);
		return this;
	}

	@Override
	public JPopupMenu build()
	{
		apply(menu);
		return menu;
	}
}
