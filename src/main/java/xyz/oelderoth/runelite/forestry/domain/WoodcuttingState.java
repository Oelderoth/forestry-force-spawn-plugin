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

	public boolean isForestryEligible() {
		var regionId = gameObject.getWorldLocation().getRegionID();
		// TODO: More accurate area check
		return regionId != WOODCUTTING_GUILD_REGION_EAST && regionId != WOODCUTTING_GUILD_REGION_WEST;
	}

	private static int WOODCUTTING_GUILD_REGION_EAST = 6454;
	private static int WOODCUTTING_GUILD_REGION_WEST = 6198;
}
