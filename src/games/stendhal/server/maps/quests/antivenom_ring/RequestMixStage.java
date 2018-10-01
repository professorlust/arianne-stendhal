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
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayRequiredItemsFromCollectionAction;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.TriggerInListCondition;

public class RequestMixStage extends AVRQuestStage {

	/* Items taken to apothecary to create antivenom */
	protected static final String MIX_ITEMS = "cobra venom=1,mandragora=2,fairy cake=5";
	protected static final List<String> MIX_NAMES = Arrays.asList("cobra venom", "mandragora", "fairy cake");

	public RequestMixStage(final String npc, final String questSlot, final String stageName, final int stageIndex) {
		super(npc, questSlot, stageName, stageIndex);
	}

	@Override
	protected void addDialogue() {
		addRequestQuestDialogue();
		addQuestActiveDialogue();
		addQuestDoneDialogue();
		addGeneralResponsesDialogue();
	}


	/**
	 * Conversation states for NPC before quest is active.
	 */
	private void addRequestQuestDialogue() {
		final SpeakerNPC npc = npcs.get(npcName);

		// Player asks for quest without having Klass's note
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new NotCondition(new PlayerHasItemWithHimCondition("note to apothecary")),
						new QuestNotStartedCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING,
				"I'm sorry, but I'm much too busy right now. Perhaps you could talk to #Klaas.",
				null);

		// Player speaks to apothecary while carrying note.
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new PlayerHasItemWithHimCondition("note to apothecary"),
						new QuestNotStartedCondition(QUEST_SLOT)),
				ConversationStates.QUEST_OFFERED,
				"Oh, a message from Klaas. Is that for me?",
				null);

		// Player explicitly requests "quest" while carrying note (in case note is dropped before speaking to apothecary).
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new PlayerHasItemWithHimCondition("note to apothecary"),
						new QuestNotStartedCondition(QUEST_SLOT)),
				ConversationStates.QUEST_OFFERED,
				"Oh, a message from Klaas. Is that for me?",
				null);

		// Player accepts quest
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(
						//new SetQuestAction(QUEST_SLOT, STAGE_NAME + ";;" + MIX_ITEMS),
						new SetQuestAction(QUEST_SLOT, STAGE_IDX, STAGE_NAME),
						new SetQuestAction(QUEST_SLOT, STATUS_IDX, MIX_ITEMS),
						new IncreaseKarmaAction(5.0),
						// FIXME: Note can be dropped before saying "yes" to accept quest.
						new DropItemAction("note to apothecary"),
						new SayRequiredItemsFromCollectionAction(QUEST_SLOT, STATUS_IDX,
								"Klaas has asked me to assist you. I can make a ring that will increase your resistance to poison. I need you to bring me [items].  Do you have any of those with you?",
								true)
				)
		);

		// Player tries to leave without accepting/rejecting the quest
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.GOODBYE_MESSAGES,
				null,
				ConversationStates.QUEST_OFFERED,
				"That is not a \"yes\" or \"no\" answer. I said, Is that note you are carrying for me?",
				null);

		// Player rejects quest
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				// NPC walks away
				ConversationStates.IDLE,
				"Oh, well, carry on then.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
	}


	/**
	 * Conversation states for NPC while quest is active.
	 */
	private void addQuestActiveDialogue() {
		final SpeakerNPC npc = npcs.get(npcName);

		// Player asks for quest after it is started
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestStartedCondition(QUEST_SLOT),
						new QuestNotCompletedCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING,
				null,
				new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "I am still waiting for you to bring me [items]. Do you have any of those with you?"));

		// FIXME: Condition must apply to "mixing" state and anything afterward
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestActiveCondition(QUEST_SLOT),
						new NotCondition(new QuestInStateCondition(QUEST_SLOT, STAGE_IDX, "mixing"))),
				ConversationStates.ATTENDING,
				"Hello again! Did you bring me the #items I requested?",
				null);

		// player asks what is missing (says "items")
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("item", "items", "ingredient", "ingredients"),
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				null,
				new SayRequiredItemsFromCollectionAction(QUEST_SLOT, STATUS_IDX, "I need [items]. Did you bring something?", true));

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
				new SayRequiredItemsFromCollectionAction(QUEST_SLOT, STATUS_IDX, "Okay. I still need [items]", true));

		// Players says does not have required items (alternate conversation state)
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.IDLE,
				"Okay. Let me know when you have found something.",
				null);//new SayRequiredItemsFromCollectionAction(QUEST_SLOT, STATUS_IDX, "Okay. I still need [items]"));

		List<String> GOODBYE_NO_MESSAGES = new LinkedList<>(ConversationPhrases.GOODBYE_MESSAGES);
		GOODBYE_NO_MESSAGES.addAll(ConversationPhrases.NO_MESSAGES);

		// player says "bye" while listing items
		npc.add(ConversationStates.QUESTION_2,
				GOODBYE_NO_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.IDLE,
				null,
				new SayRequiredItemsFromCollectionAction(QUEST_SLOT, STATUS_IDX, "Okay. I still need [items]", true));

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
		new SetQuestAction(QUEST_SLOT, STAGE_IDX, "mixing"),
		new SetQuestToTimeStampAction(QUEST_SLOT, STATUS_IDX),
		new SayTextAction("Thank you. I'll get to work on mixing the antivenom right after I enjoy a few of these fairy cakes. Please come back in " + MIX_TIME + " minutes.")
		);

		/* add triggers for the item names */
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
							STATUS_IDX,
							"Excellent! Do you have anything else with you?",
							"You brought me that already.",
							mixAction,
							ConversationStates.IDLE
							)
			);
		}
	}


	/**
	 * Conversation states for NPC after quest is completed.
	 */
	private void addQuestDoneDialogue() {
		final SpeakerNPC npc = npcs.get(npcName);

		// Quest has previously been completed.
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.QUESTION_1,
				"Thank you so much. It had been so long since I was able to enjoy a fairy cake. Are you enjoying your ring?",
				null);

		// Player is enjoying the ring
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Wonderful!",
				null);

		// Player is not enjoying the ring
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Oh, that's too bad.",
				null);
	}

	private void addGeneralResponsesDialogue() {
		final SpeakerNPC npc = npcs.get(npcName);

		/*
        // Player asks about required items
		npc.add(ConversationStates.QUESTION_1,
				Arrays.asList("gland", "venom gland", "glands", "venom glands"),
				null,
				ConversationStates.QUESTION_1,
				"Some #snakes have a gland in which their venom is stored.",
				null);

		npc.add(ConversationStates.QUESTION_1,
				Arrays.asList("mandragora", "mandragoras", "root of mandragora", "roots of mandragora", "root of mandragoras", "roots of mandragoras"),
				null,
				ConversationStates.QUESTION_1,
				"This is my favorite of all herbs and one of the most rare. Out past Kalavan there is a hidden path in the trees. At the end you will find what you are looking for.",
				null);
		*/
		npc.add(ConversationStates.QUESTION_1,
				Arrays.asList("cake", "fairy cake"),
				null,
				ConversationStates.QUESTION_1,
				"Oh, they are the best treat I have ever tasted. Only the most heavenly creatures could make such angelic food.",
				null);

		// Player asks about rings
		npc.add(ConversationStates.QUESTION_1,
				Arrays.asList("ring", "rings"),
				null,
				ConversationStates.QUESTION_1,
				"There are many types of rings.",
				null);

		npc.add(ConversationStates.QUESTION_1,
				Arrays.asList("medicinal ring", "medicinal rings"),
				null,
				ConversationStates.QUESTION_1,
				"Some poisonous creatures carry them.",
				null);

		npc.add(ConversationStates.QUESTION_1,
				Arrays.asList("antivenom ring", "antivenom rings"),
				null,
				ConversationStates.QUESTION_1,
				"If you bring me what I need I may be able to strengthen a #medicinal #ring.",
				null);

		npc.add(ConversationStates.QUESTION_1,
				Arrays.asList("antitoxin ring", "antitoxin rings", "gm antitoxin ring", "gm antitoxin rings"),
				null,
				ConversationStates.QUESTION_1,
				"Heh! This is the ultimate protection against poisoning. Good luck getting one!",
				null);
		/*
		// Player asks about snakes
		npc.add(ConversationStates.QUESTION_1,
				Arrays.asList("snake", "snakes", "cobra", "cobras"),
				null,
				ConversationStates.QUESTION_1,
				"I've heard rumor newly discovered pit full of snakes somewhere in Ados. But I've never searched for it myself. That kind of work is better left to adventurers.",
				null);

        // Player asks about required items
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("gland", "venom gland", "glands", "venom glands"),
				null,
				ConversationStates.ATTENDING,
				"Some #snakes have a gland in which their venom is stored.",
				null);

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("mandragora", "mandragoras", "root of mandragora", "roots of mandragora", "root of mandragoras", "roots of mandragoras"),
				null,
				ConversationStates.ATTENDING,
				"This is my favorite of all herbs and one of the most rare. Out past Kalavan there is a hidden path in the trees. At the end you will find what you are looking for.",
				null);
		*/
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("cake", "fairy cake"),
				null,
				ConversationStates.ATTENDING,
				"Oh, they are the best treat I have ever tasted. Only the most heavenly creatures could make such angelic food.",
				null);

		// Player asks about rings
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("ring", "rings"),
				null,
				ConversationStates.ATTENDING,
				"There are many types of rings.",
				null);

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("medicinal ring", "medicinal rings"),
				null,
				ConversationStates.ATTENDING,
				"Some poisonous creatures carry them.",
				null);

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("antivenom ring", "antivenom rings"),
				null,
				ConversationStates.ATTENDING,
				"If you bring me what I need I may be able to strengthen a #medicinal #ring.",
				null);

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("antitoxin ring", "antitoxin rings", "gm antitoxin ring", "gm antitoxin rings"),
				null,
				ConversationStates.ATTENDING,
				"Heh! This is the ultimate protection against poisoning. Good luck getting one!",
				null);
		/*
		// Player asks about snakes
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("snake", "snakes", "cobra", "cobras"),
				null,
				ConversationStates.ATTENDING,
				"I've heard rumor newly discovered pit full of snakes somewhere in Ados. But I've never searched for it myself. That kind of work is better left to adventurers.",
				null);
		*/
	}
}
