package xyz.oelderoth.runelite.forestry.domain;

import lombok.Data;

@Data
public class TreeTimer
{
	private final TreeDefinition tree;
	private final TreeType treeType;
	private final long startTimeMs;
	private final int world;
}
