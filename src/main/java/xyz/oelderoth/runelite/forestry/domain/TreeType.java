package xyz.oelderoth.runelite.forestry.domain;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import java.lang.reflect.Modifier;
import net.runelite.api.TileObject;
import org.apache.commons.lang3.tuple.Pair;

@Getter
@Slf4j
@AllArgsConstructor
public enum TreeType
{
	Oak(27000, ItemID.OAK_LOGS),
	Willow(30000, ItemID.WILLOW_LOGS),
	Teak(30000, ItemID.TEAK_LOGS),
	Maple(60000, ItemID.MAPLE_LOGS),
	Mahogany(60000, ItemID.MAHOGANY_LOGS),
	ArcticPine(114000,ItemID.ARCTIC_PINE_LOGS),
	Yew(234000, ItemID.YEW_LOGS),
	Magic(264000, ItemID.MAGIC_LOGS);

	private final int despawnDurationMs;
	private final int itemId;

	private static final Map<Integer, TreeType> treeTypeById;

	public static Optional<TreeType> getTreeTypeOf(TileObject object)
	{
		return Optional.ofNullable(object).map(it -> treeTypeById.get(it.getId()));
	}

	private static final Pattern treeRegex = Pattern.compile("([\\w_]+?)_TREE_\\d+");
	private static Optional<Pair<Integer, TreeType>> getTreeTypeFromField(Field field)
	{
		if (!Modifier.isStatic(field.getModifiers()) || !(field.getType() == int.class || field.getType() == Integer.class))
		{
			return Optional.empty();
		}
		val matcher = treeRegex.matcher(field.getName());
		if (matcher.matches())
		{
			val typeCandidate = matcher.group(1).toLowerCase();
			return Arrays.stream(TreeType.values()).filter(it -> it.name().toLowerCase().equals(typeCandidate)).findFirst()
				.map(type -> {
					try
					{
						return Pair.of((int) field.get(null), type);
					}
					catch (Exception e)
					{
						return null;
					}
				});
		}

		return Optional.empty();
	}

	static
	{
		// TODO: Find a more elegant way to populate this than brute forcing reflection
		// Retrieve tree types from declared fields in ObjectID class
		treeTypeById = Arrays.stream(ObjectID.class.getDeclaredFields())
			.map(TreeType::getTreeTypeFromField)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

		// Manually add known tree types that aren't in ObjectID
		treeTypeById.put(10823, TreeType.Yew);
	}
}