package xyz.oelderoth.runelite.forestry.ui.icons;

import javax.swing.ImageIcon;
import net.runelite.client.util.ImageUtil;

public class Icons
{
	public static ImageIcon TRASH;
	public static ImageIcon TRASH_HOVER;

	static {
		var trash = ImageUtil.luminanceOffset(ImageUtil.loadImageResource(Icons.class, "trash.png"), -100);
		TRASH = new ImageIcon(trash);
		TRASH_HOVER = new ImageIcon(ImageUtil.alphaOffset(trash, -100));
	}
}
