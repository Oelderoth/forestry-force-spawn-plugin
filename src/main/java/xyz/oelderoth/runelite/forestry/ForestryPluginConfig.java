package xyz.oelderoth.runelite.forestry;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;
import net.runelite.client.ui.ColorScheme;

@ConfigGroup("forestry_force_spawn")
public interface ForestryPluginConfig extends Config
{
	@ConfigSection(
		name = "In-progress render style",
		description = "The render style for trees with an in-progress timer",
		position = 10
	)
	String inProgressSection = "inprogress";

	@ConfigSection(
		name = "Completed render style",
		description = "The render style for trees with a completed timer",
		position = 20
	)
	String completedSection = "completed";

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
		return 5;
	}

	@ConfigItem(
		keyName = "highlightInProgress",
		name = "Highlight in-progress trees",
		description = "Draw a highlight around trees that have an in-progress timer.",
		position = 10,
		section = inProgressSection
	)
	default boolean highlightInProgressTree()
	{
		return true;
	}

	@ConfigItem(
		keyName = "inProgressOutline",
		name = "Highlight color",
		description = "Color of the border for trees with an in-progress timer.",
		position = 11,
		section = inProgressSection
	)
	default Color inProgressOutline()
	{
		return ColorScheme.PROGRESS_INPROGRESS_COLOR;
	}

	@ConfigItem(
		keyName = "inProgressWidth",
		name = "Border width",
		description = "Width of the border for trees with an in-progress timer.",
		position = 12,
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
		position = 13,
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
		keyName = "highlightComplete",
		name = "Highlight completed trees",
		description = "Draw a highlight around trees that have a completed timer.",
		position = 20,
		section = completedSection
	)
	default boolean highlightCompleteTree()
	{
		return true;
	}

	@ConfigItem(
		keyName = "completedOutline",
		name = "Highlight color",
		description = "Color of the border for trees with a completed timer.",
		position = 21,
		section = completedSection
	)
	default Color completedOutline()
	{
		return ColorScheme.PROGRESS_COMPLETE_COLOR;
	}

	@ConfigItem(
		keyName = "completedWidth",
		name = "Border width",
		description = "Width of the border for trees with a completed timer",
		position = 22,
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
		position = 23,
		section = completedSection
	)
	@Range(
		max = 4
	)
	default int completedFeather()
	{
		return 0;
	}

}
