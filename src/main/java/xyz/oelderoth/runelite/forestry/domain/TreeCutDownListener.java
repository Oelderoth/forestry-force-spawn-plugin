package xyz.oelderoth.runelite.forestry.domain;

import net.runelite.api.GameObject;

@FunctionalInterface
public interface TreeCutDownListener
{
	void onTreeCutDown(GameObject object);
}
