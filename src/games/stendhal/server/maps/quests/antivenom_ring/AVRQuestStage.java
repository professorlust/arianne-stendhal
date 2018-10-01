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
package games.stendhal.server.maps.quests.antivenom_ring;

import java.util.Arrays;
import java.util.List;

import games.stendhal.server.entity.npc.SpeakerNPC;

public abstract class AVRQuestStage {

	protected final String QUEST_SLOT;

	protected final String stageName;

	protected final SpeakerNPC npc;

	/* Items taken to apothecary to create antivenom */
	protected static final String MIX_ITEMS = "cobra venom=1,mandragora=2,fairy cake=5";
	protected static final List<String> MIX_NAMES = Arrays.asList("cobra venom", "mandragora", "fairy cake");

	/* Items taken to ??? to create cobra venom */
	protected static final String EXTRACT_ITEMS = "venom gland=1,vial=1";
	protected static final List<String> EXTRACT_NAMES = Arrays.asList("venom gland", "vial");

	/* Items taken to ??? to create antivenom ring */
	protected static final int REQUIRED_MONEY = 10000;
	protected static final String FUSE_ITEMS = "antivenom=1,medicinal ring=1";
	protected static final List<String> FUSE_NAMES = Arrays.asList("antivenom", "medicinal ring");

	/* Index of quest slot where item lists are located */
	protected static final int LIST_SLOT = 1;


	protected static final int EXTRACTION_TIME = 10;

	protected static final int MIX_TIME = 10;

	protected static final int FUSION_TIME = 30;

	public AVRQuestStage(final SpeakerNPC npc, final String questSlot, final String stageName) {
		this.npc = npc;
		this.QUEST_SLOT = questSlot;
		this.stageName = stageName;
		addDialogue();
	}

	protected abstract void addDialogue();
}
