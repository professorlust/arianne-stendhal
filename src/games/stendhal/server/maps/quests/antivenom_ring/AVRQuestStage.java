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

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.NPCList;

public abstract class AVRQuestStage {

	/* NPC list */
	protected NPCList npcs = SingletonRepository.getNPCList();

	protected final String QUEST_SLOT;
	protected final String STAGE_NAME;
	/* Index in quest string where this stage's info is located */
	protected final int STAGE_IDX;
	/* Index in quest string where this stage's item info is located */
	protected final int STATUS_IDX;

	protected final String npcName;

	protected static final int MIX_TIME = 10;
	protected static final int EXTRACTION_TIME = 10;
	protected static final int FUSE_TIME = 30;

	public AVRQuestStage(final String npc, final String questSlot, final String stageName, final int stageIndex) {
		this.npcName = npc;
		this.QUEST_SLOT = questSlot;
		this.STAGE_NAME = stageName;
		this.STAGE_IDX = stageIndex;
		this.STATUS_IDX = stageIndex + 1;

		addDialogue();
	}

	protected abstract void addDialogue();
}
