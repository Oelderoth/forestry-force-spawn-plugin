package xyz.oelderoth.runelite.forestry.ui.builders.panel;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(fluent = true, chain = true)
public class GridBagConstraintsBuilder
{
	private int anchor = GridBagConstraints.CENTER;
	private int gridwidth = 1;
	private int gridheight = 1;
	private int gridx = GridBagConstraints.RELATIVE;
	private int gridy = GridBagConstraints.RELATIVE;
	private double weightx = 0;
	private double weighty = 0;
	private int fill = GridBagConstraints.NONE;
	private int ipadx = 0;
	private int ipady = 0;
	private Insets insets = new Insets(0, 0, 0, 0);

	public GridBagConstraints build()
	{
		return new GridBagConstraints(gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets, ipadx, ipady);
	}
}
