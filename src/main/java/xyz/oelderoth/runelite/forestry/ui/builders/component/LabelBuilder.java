package xyz.oelderoth.runelite.forestry.ui.builders.component;

import javax.swing.JLabel;
import lombok.Setter;
import lombok.experimental.Accessors;
import xyz.oelderoth.runelite.forestry.ui.PluginColorScheme;

@Setter
@Accessors(fluent = true, chain = true)
public class LabelBuilder extends AbstractComponentBuilder<JLabel, LabelBuilder>
{
	private String text;

	public LabelBuilder() {
		this.foreground(PluginColorScheme.labelColor);
	}

	public JLabel build() {
		return apply(new JLabel(text));
	}
}
