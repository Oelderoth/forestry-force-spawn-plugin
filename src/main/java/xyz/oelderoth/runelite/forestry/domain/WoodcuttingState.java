package xyz.oelderoth.runelite.forestry.domain;

import lombok.Data;
import net.runelite.api.GameObject;

@Data
public class WoodcuttingState
{
	private final GameObject gameObject;
	private final TreeType treeType;
	private final int startTick;
	private final long startTimeMs;
}
