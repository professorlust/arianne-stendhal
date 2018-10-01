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
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.CollectRequestedItemsAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayRequiredItemsFromCollectionAction;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.condition.TriggerInListCondition;

public class MixAntivenom extends AVRQuestStage {

	public MixAntivenom(SpeakerNPC npc, String questSlot) {
		super(npc, questSlot, "mix");
	}

	@Override
	protected void addDialogue() {
		// FIXME: Condition must apply to "mixing" state and anything afterward
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestActiveCondition(QUEST_SLOT),
						new NotCondition(new QuestInStateCondition(QUEST_SLOT, 0, "mixing"))),
				ConversationStates.ATTENDING,
				"Hello again! Did you bring me the #items I requested?",
				null);

		// player asks what is missing (says "items")
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("item", "items", "ingredient", "ingredients"),
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				null,
				new SayRequiredItemsFromCollectionAction(QUEST_SLOT, 2, "I need [items]. Did you bring something?", true));

		// player says has a required item with him (says "yes")
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.YES_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.QUESTION_2,
				"What did you bring?",
				null);

		// Players says has required items (alternate conversation state)
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.QUESTION_2,
				"What did you bring?",
				null);

		// player says does not have a required item with him (says "no")
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.NO_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.IDLE,
				null,
				new SayRequiredItemsFromCollectionAction(QUEST_SLOT, 2, "Okay. I still need [items]", true));

		// Players says does not have required items (alternate conversation state)
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.IDLE,
				"Okay. Let me know when you have found something.",
				null);//new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "Okay. I still need [items]"));

		List<String> GOODBYE_NO_MESSAGES = new LinkedList<>(ConversationPhrases.GOODBYE_MESSAGES);
		GOODBYE_NO_MESSAGES.addAll(ConversationPhrases.NO_MESSAGES);

		// player says "bye" while listing items
		npc.add(ConversationStates.QUESTION_2,
				GOODBYE_NO_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.IDLE,
				null,
				new SayRequiredItemsFromCollectionAction(QUEST_SLOT, 2, "Okay. I still need [items]", true));

		// Returned too early; still working
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
				new QuestStateStartsWithCondition(QUEST_SLOT, "enhancing;"),
				new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, MIX_TIME))),
				ConversationStates.IDLE,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, MIX_TIME, "I have not finished with the ring. Please check back in "));

/*		// player says he didn't bring any items (says no)
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.NO_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.IDLE,
				"Ok. Let me know when you have found something.",
				null);

		// player says he didn't bring any items to different question
		npc.add(ConversationStates.QUESTION_2,
				ConversationPhrases.NO_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.IDLE,
				"Ok. Let me know when you have found something.",
				null);
		*/
		// player offers item that isn't in the list.
		npc.add(ConversationStates.QUESTION_2, "",
			new AndCondition(new QuestActiveCondition(QUEST_SLOT),
					new NotCondition(new TriggerInListCondition(MIX_NAMES))),
			ConversationStates.QUESTION_2,
			"I don't believe I asked for that.", null);

		ChatAction mixAction = new MultipleActions (
		new SetQuestAction(QUEST_SLOT, 1, "mixing"),
		new SetQuestToTimeStampAction(QUEST_SLOT, 4),
		new SayTextAction("Thank you. I'll get to work on mixing the antivenom right after I enjoy a few of these fairy cakes. Please come back in " + MIX_TIME + " minutes.")
		);

		/* add triggers for the item names */
		//final ItemCollection items = new ItemCollection();
		//items.addFromString(MIX_ITEMS);
		//for (final Map.Entry<String, Integer> item : items.entrySet()) {
		for (final String iName : MIX_NAMES) {
			npc.add(ConversationStates.QUESTION_2,
					iName,
					new QuestActiveCondition(QUEST_SLOT),
					ConversationStates.QUESTION_2,
					null,
					new CollectRequestedItemsAction(
							iName,
							QUEST_SLOT,
							true,
							2,
							"Excellent! Do you have anything else with you?",
							"You brought me that already.",
							mixAction,
							ConversationStates.IDLE
							)
			);
		}

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
