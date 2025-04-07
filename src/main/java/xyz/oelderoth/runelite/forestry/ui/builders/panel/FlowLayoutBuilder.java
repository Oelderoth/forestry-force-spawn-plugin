package xyz.oelderoth.runelite.forestry.ui.builders.panel;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.intellij.lang.annotations.MagicConstant;
import xyz.oelderoth.runelite.forestry.ui.PluginScheme;

@Setter
@Accessors(fluent = true, chain = true)
public class FlowLayoutBuilder
{
	private int align;
	private int hgap;
	private int vgap;

	public FlowLayoutBuilder align(@MagicConstant(intValues = {FlowLayout.LEFT, FlowLayout.CENTER, FlowLayout.RIGHT, FlowLayout.LEADING, FlowLayout.TRAILING}) int align)
	{
		this.align = align;
		return this;
	}

	@SuppressWarnings("MagicConstant")
	public FlowLayout build()
	{
		return new FlowLayout(align, hgap, vgap);
	}
}
