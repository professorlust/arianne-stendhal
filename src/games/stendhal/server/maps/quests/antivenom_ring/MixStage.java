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

import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;

public class MixStage extends AVRQuestStage {

	public MixStage(final String npc, final String questSlot, final String stageName, final int stageIndex) {
		super(npc, questSlot, stageName, stageIndex);
	}

	@Override
	protected void addDialogue() {
		final SpeakerNPC npc = npcs.get(npcName);

		// Returned too early; still working
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
				new QuestStateStartsWithCondition(QUEST_SLOT, "enhancing;"),
				new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, MIX_TIME))),
				ConversationStates.IDLE,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, MIX_TIME, "I have not finished with the ring. Please check back in "));

		final List<ChatAction> mixReward = new LinkedList<ChatAction>();
		//reward.add(new IncreaseXPAction(2000));
		//reward.add(new IncreaseKarmaAction(25.0));
		mixReward.add(new EquipItemAction("antivenom", 1, true));
		mixReward.add(new SetQuestAction(QUEST_SLOT, 1, "mixing=done"));

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, 1, "mixing"),
						new TimePassedCondition(QUEST_SLOT, 1, MIX_TIME)
				),
			ConversationStates.IDLE,
			"I have finished mixing the antivenom. You will need to find someone who can #fuse this into an #object. Now I'll finish the rest of my fairy cakes if you dont mind.",
			new MultipleActions(mixReward));
	}
}
