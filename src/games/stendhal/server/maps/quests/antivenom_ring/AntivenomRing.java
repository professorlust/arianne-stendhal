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
package games.stendhal.server.maps.quests.antivenom_ring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.maps.quests.AbstractQuest;
import games.stendhal.server.util.ItemCollection;

/**
 * QUEST: Antivenom Ring
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Jameson (the retired apothecary)</li>
 * <li>Zoey (zoologist at animal sanctuary)</li>
 * <li>Hogart (fuses ring; no other info yet)</li>
 * <li>Other NPCs to give hints at location of apothecary's lab: Valo, Haizen, & Ortiv Milquetoast</li>
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

	// NPCs involved in quest
	private final static String mixer = "Jameson";
	// FIXME: find NPCs for these roles
	private final static String extractor = "Zoey";
	private final static String fuser = "Hogart";

	private static final String STAGE_MIX = "mix";
	private static final String STAGE_EXTRACT = "extract";
	private static final String STAGE_FUSE = "fuse";
	private static final String STAGE_REQUEST_MIX = "request_" + STAGE_MIX;
	private static final String STAGE_REQUEST_EXTRACT = "request_" + STAGE_EXTRACT;
	private static final String STAGE_REQUEST_FUSE = "request_" + STAGE_FUSE;
	private static final int STAGE_MIX_SLOT = 0;
	private static final int STAGE_EXTRACT_SLOT = 2;
	private static final int STAGE_FUSE_SLOT = 4;

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (player.hasQuest(QUEST_SLOT)) {
			res.add("I have found the hermit apothecary's lab in Semos Mountain.");

			final List<String> questInfo = Arrays.asList(player.getQuest(QUEST_SLOT).split(";"));
			if (!questInfo.isEmpty()) {

				ItemCollection itemList = new ItemCollection();

				if (questInfo.size() >= STAGE_MIX_SLOT + 2) {

					final String mixState = questInfo.get(STAGE_MIX_SLOT);
					final String gatherMixItems = questInfo.get(STAGE_MIX_SLOT + 1);

					if (mixState.equals("done")) {
						res.add("I gathered all that " + mixer + " asked for. He applied a special mixture to my ring which made it more resistant to poison. I also got some XP and karma.");
					} else if (mixState.equals("rejected")) {
						res.add("Poison is too dangerous. I do not want to get hurt.");
					} else {
						res.add(mixer + " has asked me to gather some items.");

						itemList.clear();
						itemList.addFromString(gatherMixItems);
						res.add("I still need to bring " + mixer + " " + Grammar.enumerateCollection(itemList.toStringList()) + ".");
					}
				}

				if (questInfo.size() >= STAGE_EXTRACT_SLOT + 2) {
					final String extractState = questInfo.get(STAGE_MIX_SLOT);
					final String gatherExtractItems = questInfo.get(STAGE_MIX_SLOT + 1);

					if (extractState.equals("done")) {
						res.add("I gathered all that " + extractor + " asked for.");
					} else {
						res.add(extractor + " has asked me to gather some items.");

						itemList.clear();
						itemList.addFromString(gatherExtractItems);
						res.add("I still need to bring " + extractor + " " + Grammar.enumerateCollection(itemList.toStringList()) + ".");
					}
				}

				if (questInfo.size() >= STAGE_FUSE_SLOT + 2) {
					final String fuseState = questInfo.get(STAGE_FUSE_SLOT);
					final String gatherFuseItems = questInfo.get(STAGE_FUSE_SLOT + 1);

					if (fuseState.equals("done")) {
						res.add("I gathered all that " + fuser + " asked for.");
					} else {
						res.add(fuser + " has asked me to gather some items.");

						itemList.clear();
						itemList.addFromString(gatherFuseItems);
						res.add("I still need to bring " + fuser + " " + Grammar.enumerateCollection(itemList.toStringList()));
					}
				}
			}
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

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Antivenom Ring",
				"As a favor to an old friend, Jameson the apothecary will strengthen the medicinal ring.",
				false);
		prepareHintNPCs();
		new RequestMixStage(mixer, QUEST_SLOT, STAGE_REQUEST_MIX, STAGE_MIX_SLOT);
		new MixStage(mixer, QUEST_SLOT, STAGE_MIX, STAGE_MIX_SLOT);
		new RequestExtractStage(extractor, QUEST_SLOT, STAGE_REQUEST_EXTRACT, STAGE_EXTRACT_SLOT);
		new ExtractStage(extractor, QUEST_SLOT, STAGE_EXTRACT, STAGE_EXTRACT_SLOT);
		new RequestFuseStage(fuser, QUEST_SLOT, STAGE_REQUEST_FUSE, STAGE_FUSE_SLOT);
		new FuseStage(fuser, QUEST_SLOT, STAGE_FUSE, STAGE_FUSE_SLOT);
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
		return mixer;
	}
}
