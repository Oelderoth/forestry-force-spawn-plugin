package xyz.oelderoth.runelite.forestry.ui.builders.component;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
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
	private JPopupMenu popupMenu;
	private final List<BiConsumer<MouseEvent, T>> onMouseEntered = new ArrayList<>();
	private final List<BiConsumer<MouseEvent, T>> onMouseExited = new ArrayList<>();
	private final HashMap<ClickFilter, List<BiConsumer<MouseEvent, T>>> onClick = new HashMap<>();
	private final HashMap<ClickFilter, List<BiConsumer<MouseEvent, T>>> onMousePressed = new HashMap<>();
	private final HashMap<ClickFilter, List<BiConsumer<MouseEvent, T>>> onMouseReleased = new HashMap<>();

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

	public B popupMenu(JPopupMenu popupMenu) {
		this.popupMenu = popupMenu;
		return (B) this;
	}

	public B menuItem(JMenuItem menuItem) {
		if (this.popupMenu == null) popupMenu = new JPopupMenu();
		popupMenu.add(menuItem);
			return (B) this;
	}

	public B onMouseEntered(BiConsumer<MouseEvent, T> handler) {
		this.onMouseEntered.add(handler);
		return (B) this;
	}

	public B onMouseExited(BiConsumer<MouseEvent, T> handler) {
		this.onMouseExited.add(handler);
		return (B) this;
	}

	public B onClick(BiConsumer<MouseEvent, T> handler) {
		if (this.onClick.containsKey(null))
			this.onClick.get(null).add(handler);
		else
			this.onClick.put(null, Lists.newArrayList(handler));
		return (B) this;
	}

	public B onClick(ClickFilter filter, BiConsumer<MouseEvent, T> handler) {
		if (this.onClick.containsKey(filter)) {
			this.onClick.get(filter).add(handler);
		} else {
			this.onClick.put(filter, Lists.newArrayList(handler));
		}
		return (B) this;
	}

	public B onMousePressed(BiConsumer<MouseEvent, T> handler) {
		if (this.onMousePressed.containsKey(null))
			this.onMousePressed.get(null).add(handler);
		else
			this.onMousePressed.put(null, Lists.newArrayList(handler));
		return (B) this;
	}

	public B onMousePressed(ClickFilter filter, BiConsumer<MouseEvent, T> handler) {
		if (this.onMousePressed.containsKey(filter)) {
			this.onMousePressed.get(filter).add(handler);
		} else {
			this.onMousePressed.put(filter, Lists.newArrayList(handler));
		}
		return (B) this;
	}

	public B onMouseReleased(BiConsumer<MouseEvent, T> handler) {
		if (this.onMouseReleased.containsKey(null))
			this.onMouseReleased.get(null).add(handler);
		else
			this.onMouseReleased.put(null, Lists.newArrayList(handler));
		return (B) this;
	}

	public B onMouseReleased(ClickFilter filter, BiConsumer<MouseEvent, T> handler) {
		if (this.onMouseReleased.containsKey(filter)) {
			this.onMouseReleased.get(filter).add(handler);
		} else {
			this.onMouseReleased.put(filter, Lists.newArrayList(handler));
		}
		return (B) this;
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

	private boolean hasAnyClickHandlers() {
		return !(onMouseEntered.isEmpty() && onMouseExited.isEmpty() && onClick.isEmpty());
	}

	private void invokeMouseHandlers(MouseEvent e, T component, HashMap<ClickFilter, List<BiConsumer<MouseEvent, T>>> handlers) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (e.getClickCount() == 2) {
				handlers.getOrDefault(ClickFilter.DOUBLE_CLICK, Collections.emptyList())
					.forEach(it -> it.accept(e, component));
			} else {
				handlers.getOrDefault(ClickFilter.LEFT_CLICK, Collections.emptyList())
					.forEach(it -> it.accept(e, component));
			}
		}
		if (SwingUtilities.isRightMouseButton(e)) {
			handlers.getOrDefault(ClickFilter.RIGHT_CLICK, Collections.emptyList())
				.forEach(it -> it.accept(e, component));
		}
		if (SwingUtilities.isMiddleMouseButton(e)) {
			handlers.getOrDefault(ClickFilter.MIDDLE_CLICK, Collections.emptyList())
				.forEach(it -> it.accept(e, component));
		}

		handlers.getOrDefault(null, Collections.emptyList())
			.forEach(it -> it.accept(e, component));
	}

	private MouseListener buildMouseListener(T component) {
		return new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				invokeMouseHandlers(e, component, onMousePressed);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				invokeMouseHandlers(e, component, onMouseReleased);
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				invokeMouseHandlers(e, component, onClick);
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				onMouseEntered.forEach(it -> it.accept(e, component));
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				onMouseExited.forEach(it -> it.accept(e, component));
			}
		};
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
		if (popupMenu != null)
			component.setComponentPopupMenu(popupMenu);
		if (hasAnyClickHandlers()) {
			component.addMouseListener(buildMouseListener(component));
		}

		return component;
	}

	public abstract T build();
}
