/***************************************************************************
 *                   (C) Copyright 2018 - Stendhal                         *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.ados.animal_sanctuary;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * NPC used in the Antivenom Ring quest that can extract <code>cobra venom</code>
 * from a <code>venom gland</code>.
 *
 * @author AntumDeluge
 */
public class ZoologistNPC implements ZoneConfigurator {

	/**
	 * Configure the NPC in a zone.
	 */
	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		/**
		 * Create & configure the NPC instance.
		 */
		final SpeakerNPC npc = new SpeakerNPC("Zoey") {

		};

		npc.setEntityClass("woman_007_npc"); // TODO: New zoologistnpc sprite.
		npc.setPosition(27, 3);
		npc.setDirection(Direction.UP);
		npc.setDescription("You see " + npc.getName() + ", a dedicated zoologist.");
		zone.add(npc);
	}

}
