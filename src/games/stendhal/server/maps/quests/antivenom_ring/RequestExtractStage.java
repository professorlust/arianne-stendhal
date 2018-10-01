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

public class RequestExtractStage extends AVRQuestStage {

	/* Items taken to ??? to create cobra venom */
	protected static final String EXTRACT_ITEMS = "venom gland=1,vial=1";
	protected static final List<String> EXTRACT_NAMES = Arrays.asList("venom gland", "vial");

	public RequestExtractStage(final String npc, final String questSlot, final String stageName, final int stageIndex) {
		super(npc, questSlot, stageName, stageIndex);
	}

	@Override
	protected void addDialogue() {
		/*
		// Player asks for antivenom
		extractor.add(ConversationStates.ATTENDING,
				Arrays.asList("jameson", "antivenom", "extract", "cobra", "venom"),
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
						new NotCondition(new QuestInStateCondition(QUEST_SLOT, 1, "extracting=done")
						)
				),
				ConversationStates.QUESTION_1,
				"What that, you need some venom to create an antivemon? I can extract the venom from a cobra's venom gland, but I will need a vial to hold it in. Would you get me these items?",
				null);

		// Player will retrieve items
		extractor.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.IDLE,
				null,
				new MultipleActions(new SetQuestAction(QUEST_SLOT, 1, EXTRACTION_ITEMS),
						new SayRequiredItemsFromCollectionAction(QUEST_SLOT, 1, "Good! I will need [items].  Do you have any of those with you?")
				)
		);
		*/
	}
}
