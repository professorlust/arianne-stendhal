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
package games.stendhal.server.maps.quests.antivenom_ring;

import java.util.Arrays;
import java.util.List;

public class RequestFuseStage extends AVRQuestStage {

	/* Items taken to ??? to create antivenom ring */
	protected static final int REQUIRED_MONEY = 10000;
	protected static final String FUSE_ITEMS = "antivenom=1,medicinal ring=1";
	protected static final List<String> FUSE_NAMES = Arrays.asList("antivenom", "medicinal ring");

	public RequestFuseStage(String npc, String questSlot, String stageName, int stageIndex) {
		super(npc, questSlot, stageName, stageIndex);
	}

	@Override
	protected void addDialogue() {
		/*
		// Greeting while quest is active
		fuser.add(
				ConversationStates.ATTENDING,
				Arrays.asList("jameson", "antivenom", "ring", "fuse"),
				new AndCondition(
						new QuestActiveCondition(QUEST_SLOT),
						new NotCondition(
								new QuestInStateCondition(QUEST_SLOT, 2, "fusing=done")
								)
						),
				ConversationStates.QUESTION_1,
				null,
				new MultipleActions(new SetQuestAction(QUEST_SLOT, 2, FUSION_ITEMS),
						new SayRequiredItemsFromCollectionAction(QUEST_SLOT, 2, "You need a powerful item that can protect you from poison? I can fuse antivenom into medicinal ring to make it stronger, but it won't be cheep. I will need [items]. My price is " + Integer.toString(REQUIRED_MONEY) + ". Will you get all this for me?")
				)
		);

		// Player will retrieve items
		fuser.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.IDLE,
				null,
				new MultipleActions(new SetQuestAction(QUEST_SLOT, 2, EXTRACTION_ITEMS),
						new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "Alright, do you have any of the items that I asked for with you?")
				)
		);
		*/
	}
}
