/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common;

import java.util.Arrays;
import java.util.List;

public class OutfitFormatter {

	/**
	 * Converts a long value to a list of integers used for outfits.
	 *
	 * @param code
	 * @return
	 */
	public static List<Integer> toList(long code) {
		// TODO: Automate.
		final int bo = (int) (code % 100);
		code /= 100;
		final int dr = (int) (code % 100);
		code /= 100;
		final int he = (int) (code % 100);
		code /= 100;
		final int ey = (int) (code % 100);
		code /= 100;
		final int ha = (int) (code % 100);
		code /= 100;
		final int de = (int) (code % 100);

		return Arrays.asList(de, ha, ey, he, dr, bo);
	}

	/**
	 * Converts a string value to a list of integers used for outfits.
	 *
	 * @param code
	 * @return
	 */
	public static List<Integer> toList(final String code) {
		return toList(Long.parseLong(code));
	}
}
