package xyz.oelderoth.runelite.forestry.ui.builders.component;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import org.intellij.lang.annotations.MagicConstant;

@SuppressWarnings({"unused", "unchecked", "UnusedReturnValue"})
public abstract class AbstractComponentBuilder<T extends JComponent, B extends AbstractComponentBuilder<T, ?>>
{
	private Border border;
	private Color background;
	private Cursor cursor;
	private Color foreground;
	private Font font;
	private Boolean visible;
	private Rectangle bounds;
	private Dimension preferredSize;
	private String tooltipText;
	private BiConsumer<T, Boolean> onHover;
	private Consumer<T> onLeftClick;

	public B border(Border border)
	{
		this.border = border;
		return (B) this;
	}

	public B bounds(Rectangle bounds)
	{
		this.bounds = bounds;
		return (B) this;
	}

	public B bounds(int x, int y, int w, int h)
	{
		this.bounds = new Rectangle(x, y, w, h);
		return (B) this;
	}

	public B background(Color background)
	{
		this.background = background;
		return (B) this;
	}

	public B cursor(Cursor cursor)
	{
		this.cursor = cursor;
		return (B) this;
	}

	public B cursor(@MagicConstant(valuesFromClass = Cursor.class) int cursor)
	{
		this.cursor = Cursor.getPredefinedCursor(cursor);
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

	public B preferredSize(Dimension size) {
		preferredSize = size;
		return (B) this;
	}

	public B preferredSize(int w, int h) {
		return preferredSize(new Dimension(w, h));
	}

	public B onHover(BiConsumer<T, Boolean> handler) {
		this.onHover = handler;
		return (B) this;
	}

	public B onHover(Consumer<Boolean> handler) {
		return onHover((c, b) -> handler.accept(b));
	}

	public B onHover(Runnable handler) {
		return onHover((c, b) -> handler.run());
	}

	public B onLeftClick(Consumer<T> handler) {
		this.onLeftClick = handler;
		return (B) this;
	}

	public B onLeftClick(Runnable handler) {
		return onLeftClick((c) -> handler.run());
	}

	public B tooltipText(String tooltipText) {
		this.tooltipText = tooltipText;
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
		if (tooltipText != null)
			component.setToolTipText(tooltipText);
		if (preferredSize != null)
			component.setPreferredSize(preferredSize);
		if (cursor != null)
			component.setCursor(cursor);
		if (onHover != null || onLeftClick != null) {
			component.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent e)
				{
					if (SwingUtilities.isLeftMouseButton(e)) {
						if (onLeftClick != null)
							onLeftClick.accept(component);
					}
				}

				@Override
				public void mouseEntered(MouseEvent e)
				{
					if (onHover != null)
						onHover.accept(component, true);
				}

				@Override
				public void mouseExited(MouseEvent e)
				{
					if (onHover != null)
						onHover.accept(component, false);
				}
			});
		}

		return component;
	}

	public abstract T build();
}
