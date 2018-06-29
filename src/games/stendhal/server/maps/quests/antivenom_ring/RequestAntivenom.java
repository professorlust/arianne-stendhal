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

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayRequiredItemsFromCollectionAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;

public class RequestAntivenom extends AVRQuestStep {

	public RequestAntivenom(final SpeakerNPC npc, final String questSlot) {
		super(npc, questSlot, "request");
	}

	@Override
	protected void addDialogue(final String QUEST_SLOT) {
		addRequestQuestDialogue(QUEST_SLOT);
		addQuestActiveDialogue(QUEST_SLOT);
		addQuestDoneDialogue(QUEST_SLOT);
	}


	/**
	 * Conversation states for NPC before quest is active.
	 *
	 * @param QUEST_SLOT
	 */
	private void addRequestQuestDialogue(final String QUEST_SLOT) {
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
				new MultipleActions(new SetQuestAction(QUEST_SLOT, 0, MIX_ITEMS),
						new IncreaseKarmaAction(5.0),
						// FIXME: Note can be dropped before saying "yes" to accept quest.
						new DropItemAction("note to apothecary"),
						new SayRequiredItemsFromCollectionAction(QUEST_SLOT, 0, "Klaas has asked me to assist you. I can make a ring that will increase your resistance to poison. I need you to bring me [items].  Do you have any of those with you?")
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
	 *
	 * @param QUEST_SLOT
	 */
	private void addQuestActiveDialogue(final String QUEST_SLOT) {
		// Player asks for quest after it is started
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestStartedCondition(QUEST_SLOT),
						new QuestNotCompletedCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING,
				null,
				new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "I am still waiting for you to bring me [items]. Do you have any of those with you?"));

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


	/**
	 * Conversation states for NPC after quest is completed.
	 *
	 * @param QUEST_SLOT
	 */
	private void addQuestDoneDialogue(final String QUEST_SLOT) {
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
}
