package xyz.oelderoth.runelite.forestry;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;
import net.runelite.client.ui.ColorScheme;

@ConfigGroup(ForestryPluginConfig.CONFIG_GROUP)
public interface ForestryPluginConfig extends Config
{
	String CONFIG_GROUP = "forestry_force_spawn";

	enum SORT_TYPE {
		START_TIME,
		REMAINING_TIME,
		WORLD,
		TREE_TYPE
	}

	@ConfigSection(
		name = "In-progress render style",
		description = "The render style for trees with an in-progress timer.",
		position = 10
	)
	String inProgressSection = "inprogress";

	@ConfigSection(
		name = "Completed render style",
		description = "The render style for trees with a completed timer.",
		position = 20
	)
	String completedSection = "completed";

	@ConfigSection(
		name = "Tree types",
		description = "The types of trees that the plugin should track.",
		position = 30
	)
	String treesSection = "trees";

	@ConfigItem(
		keyName = "showCurrentTree",
		name = "Show current tree information",
		description = "Show how long is left on the tree you're woodcutting before you can hop or harvest.",
		position = 0
	)
	default boolean showCurrentTree()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showTimerHint",
		name = "Always show instructions",
		description = "<html>Show instructions about how to start a timer, even if there are active timers.<br />Instructions will always be shown when there are no active timers.</html>",
		position = 1
	)
	default boolean showTimerHint()
	{
		return true;
	}

	@ConfigItem(
		keyName = "removeTimersAfter",
		name = "Remove timers after",
		description = "<html>How long after completion should a timer be removed, even if the tree hasn't been harvested.<br />Set to 0 to never remove timers.</html>",
		position = 2
	)
	@Units(Units.MINUTES)
	default int removeTimersAfter()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "sortOrder",
		name = "Sort timers by",
		description = "How the side panel should sort active timers",
		position = 3
	)
	default SORT_TYPE sortOrder()
	{
		return SORT_TYPE.START_TIME;
	}


	@ConfigItem(
		keyName = "outlineInProgress",
		name = "Highlight outline",
		description = "Highlight the outline around trees that have an in-progress timer.",
		position = 10,
		section = inProgressSection
	)
	default boolean drawOutlineInProgressTree()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hullInProgress",
		name = "Highlight hull",
		description = "Highlight the hull for trees that have an in-progress timer.",
		position = 11,
		section = inProgressSection
	)
	default boolean drawHullInProgressTree()
	{
		return false;
	}

	@ConfigItem(
		keyName = "clickboxInProgress",
		name = "Highlight clickbox",
		description = "Highlight the clickbox for trees that have an in-progress timer.",
		position = 12,
		section = inProgressSection
	)
	default boolean drawClickboxInProgressTree()
	{
		return false;
	}

	@ConfigItem(
		keyName = "tileInProgress",
		name = "Highlight tile",
		description = "Highlight the tile for trees that have an in-progress timer.",
		position = 13,
		section = inProgressSection
	)
	default boolean drawTileInProgressTree()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		keyName = "inProgressColor",
		name = "Border color",
		description = "Color of the border for trees with an in-progress timer.",
		position = 14,
		section = inProgressSection
	)
	default Color inProgressBorderColor()
	{
		return ColorScheme.PROGRESS_INPROGRESS_COLOR;
	}

	@Alpha
	@ConfigItem(
		keyName = "inProgressFill",
		name = "Fill color",
		description = "Color of the fill for trees with an in-progress timer.",
		position = 15,
		section = inProgressSection
	)
	Color inProgressFillColor();

	@ConfigItem(
		keyName = "inProgressWidth",
		name = "Border width",
		description = "Width of the border for trees with an in-progress timer.",
		position = 16,
		section = inProgressSection
	)
	default int inProgressWidth()
	{
		return 2;
	}

	@ConfigItem(
		keyName = "inProgressFeather",
		name = "Outline feather",
		description = "Specify between 0-4 how much of the model outline should be faded for trees with an in-progress timer.",
		position = 17,
		section = inProgressSection
	)
	@Range(
		max = 4
	)
	default int inProgressFeather()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "outlineComplete",
		name = "Highlight completed trees",
		description = "Highlight the outline around trees that have a completed timer.",
		position = 20,
		section = completedSection
	)
	default boolean drawOutlineCompleteTree()
	{
		return true;
	}


	@ConfigItem(
		keyName = "hullComplete",
		name = "Highlight hull",
		description = "Highlight the hull for trees that have a completed timer..",
		position = 21,
		section = completedSection
	)
	default boolean drawHullCompleteTree()
	{
		return false;
	}

	@ConfigItem(
		keyName = "clickboxComplete",
		name = "Highlight clickbox",
		description = "Highlight the clickbox for trees that have a completed timer.",
		position = 22,
		section = completedSection
	)
	default boolean drawClickboxCompleteTree()
	{
		return false;
	}

	@ConfigItem(
		keyName = "tileComplete",
		name = "Highlight tile",
		description = "Highlight the tile for trees that have a completed timer.",
		position = 23,
		section = completedSection
	)
	default boolean drawTileCompleteTree()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		keyName = "completedColor",
		name = "Border color",
		description = "Color of the highlight for trees with a completed timer.",
		position = 24,
		section = completedSection
	)
	default Color completedBorderColor()
	{
		return ColorScheme.PROGRESS_COMPLETE_COLOR;
	}

	@Alpha
	@ConfigItem(
		keyName = "completedFill",
		name = "Fill color",
		description = "Color of the highlight for trees with a completed timer.",
		position = 25,
		section = completedSection
	)
	Color completedFillColor();

	@ConfigItem(
		keyName = "completedWidth",
		name = "Border width",
		description = "Width of the border for trees with a completed timer",
		position = 26,
		section = completedSection
	)
	default int completedWidth()
	{
		return 2;
	}

	@ConfigItem(
		keyName = "completedFeather",
		name = "Outline feather",
		description = "Specify between 0-4 how much of the model outline should be faded for trees with a completed timer",
		position = 27,
		section = completedSection
	)
	@Range(
		max = 4
	)
	default int completedFeather()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "trackOakTree",
		name = "Oak tree",
		description = "Track whether the plugin tracks oak trees.",
		position = 30,
		section = treesSection
	)
	default boolean trackOakTree()
	{
		return true;
	}

	@ConfigItem(
		keyName = "trackWillowTree",
		name = "Willow tree",
		description = "Track whether the plugin tracks willow trees.",
		position = 31,
		section = treesSection
	)
	default boolean trackWillowTree()
	{
		return true;
	}

	@ConfigItem(
		keyName = "trackTeakTree",
		name = "Teak tree",
		description = "Track whether the plugin tracks teak trees.",
		position = 32,
		section = treesSection
	)
	default boolean trackTeakTree()
	{
		return true;
	}

	@ConfigItem(
		keyName = "trackMapleTree",
		name = "Maple tree",
		description = "Track whether the plugin tracks maple trees.",
		position = 33,
		section = treesSection
	)
	default boolean trackMapleTree()
	{
		return true;
	}

	@ConfigItem(
		keyName = "trackMahoganyTree",
		name = "Mahogany tree",
		description = "Track whether the plugin tracks mahogany trees.",
		position = 34,
		section = treesSection
	)
	default boolean trackMahoganyTree()
	{
		return true;
	}

	@ConfigItem(
		keyName = "trackArcticPineTree",
		name = "Arctic pine tree",
		description = "Track whether the plugin tracks arctic pines trees.",
		position = 35,
		section = treesSection
	)
	default boolean trackArcticPineTree()
	{
		return true;
	}

	@ConfigItem(
		keyName = "trackYewTree",
		name = "Yew tree",
		description = "Track whether the plugin tracks yew trees.",
		position = 36,
		section = treesSection
	)
	default boolean trackYewTree()
	{
		return true;
	}

	@ConfigItem(
		keyName = "trackMagicTree",
		name = "Magic tree",
		description = "Track whether the plugin tracks magic trees.",
		position = 37,
		section = treesSection
	)
	default boolean trackMagicTree()
	{
		return true;
	}

}
