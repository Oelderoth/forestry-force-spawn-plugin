package xyz.oelderoth.runelite.forestry.ui.builders.component;

import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.runelite.client.util.AsyncBufferedImage;
import xyz.oelderoth.runelite.forestry.ui.PluginScheme;

@Setter
@Accessors(fluent = true, chain = true)
@SuppressWarnings("unused")
public class LabelBuilder extends AbstractComponentBuilder<JLabel, LabelBuilder>
{
	@Setter(AccessLevel.NONE)
	private JLabel label;

	private String text;
	private boolean html = false;
	private Icon icon;

	public LabelBuilder() {
		label = new JLabel();
		this.foreground(PluginScheme.TEXT_COLOR);
	}

	public LabelBuilder icon(Icon icon) {
		this.icon = icon;
		return this;
	}

	public LabelBuilder icon(BufferedImage image) {
		icon = new ImageIcon(image);
		return this;
	}

	public LabelBuilder icon(AsyncBufferedImage image) {
		image.addTo(label);
		icon = label.getIcon();
		return this;
	}

	public JLabel build() {
		apply(label);
		if (text != null) label.setText(html ? "<html>" + text + "</html>" : text);
		if (icon != null) label.setIcon(icon);
		return label;
	}
}
