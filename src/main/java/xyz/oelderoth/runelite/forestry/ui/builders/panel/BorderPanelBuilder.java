package xyz.oelderoth.runelite.forestry.ui.builders.panel;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import xyz.oelderoth.runelite.forestry.ui.PluginColorScheme;
import xyz.oelderoth.runelite.forestry.ui.builders.component.AbstractComponentBuilder;

@Setter
@Accessors(fluent = true, chain = true)
@SuppressWarnings("unused")
public class BorderPanelBuilder extends AbstractComponentBuilder<JPanel, BorderPanelBuilder>
{
	@Setter(AccessLevel.NONE)
	private JPanel panel;

	public static BorderPanelBuilder fromPanel(JPanel panel) {
		if (!(panel.getLayout() instanceof BorderLayout))
			if (panel.getComponents().length > 0)
				throw new IllegalArgumentException("Panel must have a BorderLayout or zero components");
			else
				panel.setLayout(new BorderLayout());

		return new BorderPanelBuilder(panel);
	}

	protected BorderPanelBuilder(JPanel panel)
	{
		this.panel = panel;
	}

	public BorderPanelBuilder()
	{
		this(new JPanel(new BorderLayout()));
		background(PluginColorScheme.panelColor);
	}

	public BorderPanelBuilder addNorth(JComponent component) {
		panel.add(component, BorderLayout.NORTH);
		return this;
	}

	public BorderPanelBuilder addSouth(JComponent component) {
		panel.add(component, BorderLayout.SOUTH);
		return this;
	}

	public BorderPanelBuilder addEast(JComponent component) {
		panel.add(component, BorderLayout.EAST);
		return this;
	}

	public BorderPanelBuilder addWest(JComponent component) {
		panel.add(component, BorderLayout.WEST);
		return this;
	}

	public BorderPanelBuilder addCenter(JComponent component) {
		panel.add(component, BorderLayout.CENTER);
		return this;
	}

	public JPanel build()
	{
		if (border != null) panel.setBorder(border);
		apply(panel);
		return panel;
	}
}
