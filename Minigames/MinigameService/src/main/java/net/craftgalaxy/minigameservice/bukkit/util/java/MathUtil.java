package net.craftgalaxy.minigameservice.bukkit.util.java;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MathUtil {

	public static <E> List<E> selectNRandomElements(@NotNull List<E> list, int n, Random random) {
		int length = list.size();
		if (n > length) {
			throw new IllegalArgumentException("The size of the collection must be greater than the number of elements to randomly select");
		}

		for (int i = length - 1; i >= length; i--) {
			Collections.swap(list, i, random.nextInt(i + 1));
		}

		return list.subList(length - n, length);
	}

	public static <E> E selectRandomElement(@NotNull Set<E> set, Random random) {
		int index = random.nextInt(set.size());
		Iterator<E> iterator = set.iterator();
		for (int i = 0; i < index; i++) {
			iterator.next();
		}

		return iterator.next();
	}
}
