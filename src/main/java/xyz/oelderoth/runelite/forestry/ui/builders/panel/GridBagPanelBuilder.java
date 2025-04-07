package xyz.oelderoth.runelite.forestry.ui.builders.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import xyz.oelderoth.runelite.forestry.ui.PluginColorScheme;
import xyz.oelderoth.runelite.forestry.ui.builders.component.AbstractComponentBuilder;

@Setter
@Accessors(fluent = true, chain = true)
public class GridBagPanelBuilder extends AbstractComponentBuilder<JPanel, GridBagPanelBuilder>
{
	@Setter(AccessLevel.NONE)
	private JPanel panel;
	private GridBagConstraints constraints = new GridBagConstraints();

	protected GridBagPanelBuilder(JPanel panel)
	{
		this.panel = panel;
	}

	public GridBagPanelBuilder()
	{
		this(new JPanel(new GridBagLayout()));
		background(PluginColorScheme.panelColor);
	}

	public GridBagPanelBuilder add(JComponent component) {
		panel.add(component, constraints);
		return this;
	}

	public JPanel build()
	{
		if (border != null) panel.setBorder(border);
		apply(panel);
		return panel;
	}
}
