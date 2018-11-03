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

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayRequiredItemsFromCollectionAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;

public class RequestExtractStage extends AVRQuestStage {

	/* Items taken to ??? to create cobra venom */
	private static final String EXTRACT_ITEMS = "venom gland=1,vial=1";
	private static final List<String> EXTRACT_NAMES = Arrays.asList("venom gland", "vial");

	public RequestExtractStage(final String npc, final String questSlot, final String stageName, final int stageIndex) {
		super(npc, questSlot, stageName, stageIndex);
	}

	@Override
	protected void addDialogue() {
		prepareNPCInfo();
		prepareRequestVenom();
	}

	private void prepareNPCInfo() {
		final SpeakerNPC npc = npcs.get(npcName);

		// prepare helpful info
		npc.addJob("I am a zoologist and work full-time here at the animal sanctuary.");
		npc.addHelp("I specialize in #venomous animals.");
		npc.addQuest("There is nothing that I need right now. But maybe you could help me #milk some #snakes ones of these days.");

		// player speaks to Zoey after starting antivenom ring quest
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Oh! You startled me. I didn't see you there. I'm very busy, so if there is something you need please tell me quickly.",
				null);
	}

	private void prepareRequestVenom() {
		final SpeakerNPC npc = npcs.get(npcName);

		// player asks for antivenom
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("jameson", "antivenom", "extract", "cobra", "venom", "snake", "poison"),
				new AndCondition(
						new QuestActiveCondition(QUEST_SLOT),
						new NotCondition(new QuestInStateCondition(QUEST_SLOT, STATUS_IDX, "done"))),
				ConversationStates.QUESTION_1,
				"What's that, you need some venom to create an antivemon? I can extract the venom from a"
				+ "cobra's venom gland, but I will need a vial to hold it in. Would you get me these items?",
				null);

		// player will retrieve items
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.IDLE,
				null,
				new MultipleActions(
						new SetQuestAction(QUEST_SLOT, STAGE_IDX, STAGE_NAME),
						new SetQuestAction(QUEST_SLOT, STATUS_IDX, EXTRACT_ITEMS),
						new SayRequiredItemsFromCollectionAction(QUEST_SLOT, STATUS_IDX,
								"Good! I will need [items].  Do you have any of those with you?", true)));

		// player will not retrieve items
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.IDLE,
				"Oh? Okay then.",
				null);
	}
}
