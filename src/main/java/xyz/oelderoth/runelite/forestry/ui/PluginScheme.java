package xyz.oelderoth.runelite.forestry.ui;

import java.awt.Color;
import net.runelite.client.ui.ColorScheme;

@SuppressWarnings("unused")
public class PluginScheme
{
	public static final Color TEXT_COLOR = Color.WHITE;
	public static final Color TEXT_HOVER_COLOR = TEXT_COLOR.darker();
	public static final Color HINT_COLOR = ColorScheme.LIGHT_GRAY_COLOR;
	public static final Color HINT_HOVER_COLOR = HINT_COLOR.darker();
	public static final Color HINT_CLICK_COLOR = HINT_HOVER_COLOR.darker();
	public static final Color BACKGROUND_COLOR = ColorScheme.DARK_GRAY_COLOR;
	public static final Color PANEL_COLOR = ColorScheme.DARKER_GRAY_COLOR;
	public static final Color SUCCESS_COLOR = ColorScheme.PROGRESS_COMPLETE_COLOR;
	public static final Color INCOMPLETE_COLOR = ColorScheme.PROGRESS_INPROGRESS_COLOR;
	public static final int DEFAULT_PADDING = 8;
	public static final int SMALL_PADDING = DEFAULT_PADDING / 2;
}
