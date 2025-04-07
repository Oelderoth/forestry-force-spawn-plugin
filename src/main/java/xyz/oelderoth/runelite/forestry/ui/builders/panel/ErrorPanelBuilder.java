package xyz.oelderoth.runelite.forestry.ui.builders.panel;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.runelite.client.ui.components.PluginErrorPanel;

@Setter
@Accessors(fluent = true, chain = true)
public class ErrorPanelBuilder extends BorderPanelBuilder
{
	@Setter(AccessLevel.NONE)
	private PluginErrorPanel panel;
	private String title;
	private String description;

	protected ErrorPanelBuilder(PluginErrorPanel panel) {
		super(panel);
		this.panel = panel;
	}

	public ErrorPanelBuilder() {
		this(new PluginErrorPanel());
	}

	@Override
	public PluginErrorPanel build() {
		super.build();
		if (title != null || description != null) {
			panel.setContent(title, description);
		}
		return panel;
	}
}
