package xyz.oelderoth.runelite.forestry.ui.builders.component;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.border.Border;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(fluent = true, chain = true)
@SuppressWarnings({"unused", "unchecked", "UnusedReturnValue"})
public abstract class AbstractComponentBuilder<T extends JComponent, B extends AbstractComponentBuilder<T, ?>>
{
	protected Border border;
	private Color background;
	protected Color foreground;
	protected Font font;
	protected Boolean visible;

	public B border(Border border)
	{
		this.border = border;
		return (B) this;
	}

	public B background(Color background)
	{
		this.background = background;
		return (B) this;
	}

	public B foreground(Color color)
	{
		this.foreground = color;
		return (B) this;
	}

	public B font(Font font)
	{
		this.font = font;
		return (B) this;
	}

	public B visible(boolean visible)
	{
		this.visible = visible;
		return (B) this;
	}

	public T apply(T component)
	{
		if (border != null)
			component.setBorder(border);
		if (background != null)
			component.setBackground(background);
		if (foreground != null)
			component.setForeground(foreground);
		if (font != null)
			component.setFont(font);
		if (visible != null)
			component.setVisible(visible);

		return component;
	}

	public abstract T build();
}
