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

import games.stendhal.server.entity.npc.SpeakerNPC;

public abstract class AVRQuestStep {

	private String stepName;

	protected final SpeakerNPC npc;

	/* Items taken to apothecary to create antivenom */
	public static final String MIX_ITEMS = "cobra venom=1;mandragora=2;fairy cake=5";

	/* Items taken to ??? to create cobra venom */
	public static final String EXTRACTION_ITEMS = "venom gland=1;vial=1";

	/* Items taken to ??? to create antivenom ring */
	public static final int REQUIRED_MONEY = 10000;
	public static final String FUSION_ITEMS = "antivenom=1;medicinal ring=1";

	protected static final int EXTRACTION_TIME = 10;

	protected static final int MIX_TIME = 10;

	protected static final int FUSION_TIME = 30;

	public AVRQuestStep(final SpeakerNPC npc, final String questSlot, final String stepName) {
		this.npc = npc;
		this.stepName = stepName;
		addDialogue(questSlot);
	}

	protected abstract void addDialogue(final String QUEST_SLOT);

	public void setStepName(final String name) {
		this.stepName = name;
	}

	public final String getStepName() {
		return this.stepName;
	}
}
