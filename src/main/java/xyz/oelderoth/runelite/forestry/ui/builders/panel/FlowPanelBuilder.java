package xyz.oelderoth.runelite.forestry.ui.builders.panel;

import java.awt.FlowLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import org.intellij.lang.annotations.MagicConstant;
import xyz.oelderoth.runelite.forestry.ui.PluginScheme;
import xyz.oelderoth.runelite.forestry.ui.builders.component.AbstractComponentBuilder;

@Setter
@Accessors(fluent = true, chain = true)
@SuppressWarnings("unused")
public class FlowPanelBuilder extends AbstractComponentBuilder<JPanel, FlowPanelBuilder>
{
	@Setter(AccessLevel.NONE)
	private JPanel panel;

	private int align;
	private int hgap;
	private int vgap;

	public static FlowPanelBuilder fromPanel(JPanel panel)
	{
		if (!(panel.getLayout() instanceof FlowLayout)) if (panel.getComponents().length > 0)
			throw new IllegalArgumentException("Panel must have a FlowLayout or zero components");
		else panel.setLayout(new FlowLayout());

		return new FlowPanelBuilder(panel);
	}

	protected FlowPanelBuilder(JPanel panel)
	{
		this.panel = panel;
	}

	public FlowPanelBuilder()
	{
		this(new JPanel(new FlowLayout()));
		background(PluginScheme.PANEL_COLOR);
	}

	public FlowPanelBuilder align(@MagicConstant(intValues = {FlowLayout.LEFT, FlowLayout.CENTER, FlowLayout.RIGHT, FlowLayout.LEADING, FlowLayout.TRAILING}) int align)
	{
		this.align = align;
		return this;
	}

	public FlowPanelBuilder add(JComponent component) {
		panel.add(component);
		return this;
	}

	@SuppressWarnings("MagicConstant")
	public JPanel build()
	{
		val flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(align);
		flowLayout.setHgap(hgap);
		flowLayout.setVgap(vgap);
		apply(panel);
		return panel;
	}
}
