package xyz.oelderoth.runelite.forestry.ui.builders.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import xyz.oelderoth.runelite.forestry.ui.PluginScheme;
import xyz.oelderoth.runelite.forestry.ui.builders.component.AbstractComponentBuilder;

@Setter
@Accessors(fluent = true, chain = true)
public class GridBagPanelBuilder extends AbstractComponentBuilder<JPanel, GridBagPanelBuilder>
{
	@Setter(AccessLevel.NONE)
	private JPanel panel;
	private GridBagConstraints constraints = new GridBagConstraints();

	public static GridBagPanelBuilder fromPanel(JPanel panel) {
		if (!(panel.getLayout() instanceof GridBagLayout))
			if (panel.getComponents().length > 0)
				throw new IllegalArgumentException("Panel must have a GridBagLayout or zero components");
			else
				panel.setLayout(new GridBagLayout());

		return new GridBagPanelBuilder(panel);
	}

	protected GridBagPanelBuilder(JPanel panel)
	{
		this.panel = panel;
	}

	public GridBagPanelBuilder()
	{
		this(new JPanel(new GridBagLayout()));
		background(PluginScheme.PANEL_COLOR);
	}

	public GridBagPanelBuilder add(JComponent component) {
		panel.add(component, constraints);
		return this;
	}

	public JPanel build()
	{
		apply(panel);
		return panel;
	}
}
