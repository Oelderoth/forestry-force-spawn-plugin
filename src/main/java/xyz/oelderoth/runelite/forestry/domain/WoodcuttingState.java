package xyz.oelderoth.runelite.forestry.domain;

import lombok.Data;
import net.runelite.api.GameObject;
import xyz.oelderoth.runelite.forestry.Tree;

@Data
public class WoodcuttingState
{
	private final Tree tree;
	private final GameObject gameObject;
	private final int startTick;
	private final long startTimeMs;
}
