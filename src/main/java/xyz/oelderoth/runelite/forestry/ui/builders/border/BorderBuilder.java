package xyz.oelderoth.runelite.forestry.ui.builders.border;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import xyz.oelderoth.runelite.forestry.ui.PluginScheme;

@SuppressWarnings("unused")
public class BorderBuilder
{
	public static Border empty(int amount)
	{
		return empty(amount, amount);
	}

	public static Border empty(int vAmount, int hAmount)
	{
		return new EmptyBorder(vAmount, hAmount, vAmount, hAmount);
	}

	public static Border separated()
	{
		return separated(0, 0);
	}

	public static Border separated(int amount)
	{
		return separated(amount, amount);
	}

	public static Border separated(int vAmount, int hAmount)
	{
		return new CompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, PluginScheme.BACKGROUND_COLOR), empty(vAmount, hAmount));
	}
}
