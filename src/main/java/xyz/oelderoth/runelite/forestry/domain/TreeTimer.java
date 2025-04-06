package xyz.oelderoth.runelite.forestry.domain;

import lombok.Data;
import net.runelite.api.GameObject;
import xyz.oelderoth.runelite.forestry.Tree;

@Data
public class TreeTimer
{
	private final int world;
	private final GameObject gameObject;
	private final Tree tree;
	private final long startTimeMs;
}
