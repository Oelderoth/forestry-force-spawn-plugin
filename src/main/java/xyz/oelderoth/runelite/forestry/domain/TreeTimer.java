package xyz.oelderoth.runelite.forestry.domain;

import lombok.Data;
import net.runelite.api.GameObject;

@Data
public class TreeTimer
{
	private final int world;
	private final GameObject gameObject;
	private final TreeType treeType;
	private final long startTimeMs;
}
