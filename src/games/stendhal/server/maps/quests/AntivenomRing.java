/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.maps.quests.antivenom_ring.MixAntivenom;
import games.stendhal.server.maps.quests.antivenom_ring.RequestAntivenom;

/**
 * QUEST: Antivenom Ring
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Jameson (the retired apothecary)</li>
 * <li>Other NPCs to give hints at location of apothecary's lab (undecided)</li>
 * <li>Another NPC that fuses the ring (undecided)</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Bring note to apothecary to Jameson.</li>
 * <li>As a favor to Klaas, Jameson will help you to strengthen your medicinal ring.</li>
 * <li>Bring Jameson a medicinal ring, venom gland, 2 mandragora and 5 fairycakes.</li>
 * <li>Jameson requires a bottle big enough to hold venom extracted from gland.</li>
 * <li>Bring Jameson a giant bottle.</li>
 * <li>Jameson realizes he doesn't have a way to extract the venom.</li>
 * <li>Find [NPC undecided] who will extract the venom into the giant bottle.</li>
 * <li>Take the bottle filled with venom back to Jameson.</li>
 * <li>Jameson concocts a mixture to infuse the ring.</li>
 * <li>Take mixture and ring to [NPC undecided] to be fused.</li>
 * <li>[NPC undecided] will also have requirements for the player.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>2000 XP</li>
 * <li>antivenom ring</li>
 * <li>Karma: 25???</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 *
 *
 * @author AntumDeluge
 */
public class AntivenomRing extends AbstractQuest {

	private static final String QUEST_SLOT = "antivenom_ring";

	//public static final String NEEDED_ITEMS = "medicinal ring=1;venom gland=1;mandragora=2;fairy cake=5";

	//private static final int REQUIRED_MINUTES = 30;

	// NPCs involved in quest
	private final SpeakerNPC mixer = npcs.get("Jameson");
	// FIXME: find NPCs for these roles
	private final SpeakerNPC extractor = npcs.get("Zoey");
	private final SpeakerNPC fuser = npcs.get("Hogart");

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I have found the hermit apothecary's lab in Semos Mountain.");
		String questState = player.getQuest(QUEST_SLOT, 1);
		if ("done".equals(questState)) {
			res.add("I gathered all that Jameson asked for. He applied a special mixture to my ring which made it more resistant to poison. I also got some XP and karma.");
		}
		else if ("rejected".equals(questState)) {
			res.add("Poison is too dangerous. I do not want to get hurt.");
		}
		else {
			res.add(questState);
			/*
			final String[] stateDetails = questState.split(",");
			if (stateDetails[0].split("=")[0] == "mixing") {
				res.add(mixer.getName() + " is mixing the antivenom.");
			} else {
				final ItemCollection missingMixItems = new ItemCollection();
				missingMixItems.addFromQuestStateString(stateDetails[0]);
				res.add("I still need to bring Jameson " + Grammar.enumerateCollection(missingMixItems.toStringList()) + ".");
			}

			if (stateDetails[1].split("=")[0] == "extracting") {
				res.add(extractor.getName() + " is extracting the venom.");
			}

			if (stateDetails[2].split("=")[0] == "fusing") {
				res.add(fuser.getName() + " is creating my ring.");
			}
			*/
		}
		return res;
	}

	private void prepareHintNPCs() {
		final SpeakerNPC hintNPC1 = npcs.get("Valo");
		final SpeakerNPC hintNPC2 = npcs.get("Haizen");
		final SpeakerNPC hintNPC3 = npcs.get("Ortiv Milquetoast");

		// Valo is asked about an apothecary
		hintNPC1.add(ConversationStates.ATTENDING,
				"apothecary",
				null,
				ConversationStates.ATTENDING,
				"Hmmm, yes, I knew a man long ago who was studying medicines and antipoisons. The last I heard he was #retreating into the mountains.",
				null);

		hintNPC1.add(ConversationStates.ATTENDING,
				Arrays.asList("retreat", "retreats", "retreating"),
				null,
				ConversationStates.ATTENDING,
				"He's probably hiding. Keep an eye out for hidden entrances.",
				null);

		// Haizen is asked about an apothecary
		hintNPC2.add(ConversationStates.ATTENDING,
				"apothecary",
				null,
				ConversationStates.ATTENDING,
				"Yes, there was once an estudious man in Kalavan. But, due to complications with leadership there he was forced to leave. I heard that he was #hiding somewhere in the Semos region.",
				null);

		hintNPC2.add(ConversationStates.ATTENDING,
				Arrays.asList("hide", "hides", "hiding", "hidden"),
				null,
				ConversationStates.ATTENDING,
				"If I were hiding I would surely do it in a secret room with a hidden entrance.",
				null);

		// Ortiv Milquetoast is asked about an apothecary
		hintNPC3.add(ConversationStates.ATTENDING,
				"apothecary",
				null,
				ConversationStates.ATTENDING,
				"You must be speaking of my colleague, Jameson. He was forced to #hide out because of problems in Kalavan. He hasn't told me where, but he does bring the most delicious pears when he visits.",
				null);

		hintNPC3.add(ConversationStates.ATTENDING,
				Arrays.asList("hide", "hides", "hiding", "hidden"),
				null,
				ConversationStates.ATTENDING,
				"He hinted at a secret laboratory that he had built. Something about a hidden doorway.",
				null);
	}

	/**
	 * Quest step 1:
	 *   NPCs:
	 *     - Jameson (mixer)
	 *   Parts:
	 *     - Player gives message from Klaas to mixer.
	 */
	private void requestAntivenom() {
		new RequestAntivenom(mixer, QUEST_SLOT);
	}

	/**
	 * Quest step 2:
	 */
	private void mixAntivenom() {
		new MixAntivenom(mixer, QUEST_SLOT);
	}

	/**
	 * Quest step 3:
	 */
	private void requestCobraVenom() {
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

	/**
	 * Quest step 4:
	 */
	private void extractCobraVenom() {

	}

	/**
	 * Quest step 5:
	 */
	private void requestAntivenomRing() {
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

	/**
	 * Quest step 6:
	 */
	private void fuseAntivenomRing() {

	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Antivenom Ring",
				"As a favor to an old friend, Jameson the apothecary will strengthen the medicinal ring.",
				false);
		prepareHintNPCs();
		requestAntivenom();
		mixAntivenom();
		requestCobraVenom();
		extractCobraVenom();
		requestAntivenomRing();
		fuseAntivenomRing();
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "AntivenomRing";
	}

	public String getTitle() {
		return "AntivenomRing";
	}

	@Override
	public int getMinLevel() {
		return 0;
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_SURROUNDS;
	}

	@Override
	public String getNPCName() {
		return "Jameson";
	}
}
