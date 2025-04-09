package xyz.oelderoth.runelite.forestry.domain;

import lombok.Data;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;

@Data
public class TreeDefinition {
	private final int objectID;
	private final WorldPoint point;

	public TreeDefinition(GameObject object) {
		objectID = object.getId();
		point = object.getWorldLocation();
	}

	public boolean matches(GameObject other) {
		var oP = other.getWorldLocation();
		return other.getId() == objectID && oP.getRegionID() == point.getRegionID() && oP.getPlane() == point.getPlane() && oP.getX() == point.getX() && oP.getY() == point.getY();
	}
}
