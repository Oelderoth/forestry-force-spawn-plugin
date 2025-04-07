package xyz.oelderoth.runelite.forestry.ui.builders.panel;

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import javax.swing.JComponent;
import javax.swing.JPanel;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import xyz.oelderoth.runelite.forestry.ui.PluginScheme;
import xyz.oelderoth.runelite.forestry.ui.builders.component.AbstractComponentBuilder;

@Setter
@Accessors(fluent = true, chain = true)
@SuppressWarnings("unused")
public class LayoutPanelBuilder<T extends LayoutManager> extends AbstractComponentBuilder<JPanel, LayoutPanelBuilder<T>>
{
	@Setter(AccessLevel.NONE)
	private JPanel panel;
	private T layout;

	public static <T extends LayoutManager> LayoutPanelBuilder<T> fromPanel(JPanel panel)
	{
		return new LayoutPanelBuilder<>(panel);
	}

	protected LayoutPanelBuilder(JPanel panel)
	{
		this.panel = panel;
	}

	public LayoutPanelBuilder()
	{
		this(new JPanel(new FlowLayout()));
		background(PluginScheme.PANEL_COLOR);
	}


	public LayoutPanelBuilder<T> add(JComponent component) {
		panel.add(component);
		return this;
	}

	public JPanel build()
	{
		if (layout != null) panel.setLayout(layout);
		apply(panel);
		return panel;
	}
}
