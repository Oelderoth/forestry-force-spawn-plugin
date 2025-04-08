package xyz.oelderoth.runelite.forestry.ui.builders.component;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(fluent = true, chain = true)
public class MenuItemBuilder extends AbstractComponentBuilder<JMenuItem, MenuItemBuilder>
{
	private String text;
	private Icon icon;
	@Setter(AccessLevel.NONE)
	private List<ActionListener> actionListeners = new ArrayList<>();

	public MenuItemBuilder actionListener(ActionListener listener)
	{
		actionListeners.add(listener);
		return this;
	}

	@Override
	public JMenuItem build()
	{
		var item = new JMenuItem();
		apply(item);
		if (text != null) item.setText(text);
		if (icon != null) item.setIcon(icon);
		if (actionListeners != null) actionListeners.forEach(item::addActionListener);
		return item;
	}
}
