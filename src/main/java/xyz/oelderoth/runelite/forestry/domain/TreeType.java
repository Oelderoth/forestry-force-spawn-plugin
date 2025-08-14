package xyz.oelderoth.runelite.forestry.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;

@Getter
@Slf4j
@AllArgsConstructor
public enum TreeType
{
	Oak(27000, ItemID.OAK_LOGS, "Oak Tree"),
	Willow(30000, ItemID.WILLOW_LOGS, "Willow Tree"),
	Teak(30000, ItemID.TEAK_LOGS, "Teak Tree"),
	Maple(60000, ItemID.MAPLE_LOGS, "Maple Tree"),
	ArcticPine(114000, ItemID.ARCTIC_PINE_LOGS, "Arctic Pine Tree"),
	Mahogany(60000, ItemID.MAHOGANY_LOGS, "Mahogany Tree"),
	Yew(114000, ItemID.YEW_LOGS, "Yew Tree"),
	Magic(234000, ItemID.MAGIC_LOGS, "Magic Tree");

	private final int despawnDurationMs;
	private final int itemId;
	private final String objectName;
}