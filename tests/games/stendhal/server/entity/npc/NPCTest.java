package games.stendhal.server.entity.npc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ados.felinashouse.CatSellerNPC;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

/**
 * Test NPC logic
 * @author Martin Fuchs
 */
public class NPCTest {

	private static final String ZONE_NAME = "int_ados_felinas_house";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		StendhalRPZone zone = new StendhalRPZone(ZONE_NAME);
		StendhalRPWorld world = StendhalRPWorld.get();
		world.addRPZone(zone);

		CatSellerNPC bar = new CatSellerNPC();	// any NPC
		bar.configureZone(zone, null);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		SpeakerNPC npc = NPCList.get().get("Felina");
		if (npc != null) {
			npc.setCurrentState(ConversationStates.IDLE);
		}
	}

	@Test
	public void testHiAndBye() {
		Player player = PlayerTestHelper.createPlayer();

		SpeakerNPC npc = NPCList.get().get("Felina");
		assertNotNull(npc);
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Felina"));
		assertEquals("Greetings! How may I help you?", npc.get("text"));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye.", npc.get("text"));
	}

	@Test
	public void testLogic() {
		Player player = PlayerTestHelper.createPlayer("player");

		SpeakerNPC npc = NPCList.get().get("Felina");
		assertNotNull(npc);
		Engine en = npc.getEngine();

		npc.listenTo(player, "hi");
		assertEquals("Greetings! How may I help you?", npc.get("text"));

		assertTrue(en.step(player, "job"));
		assertEquals("I sell cats. Well, really they are just little kittens when I sell them to you but if you #care for them well they grow into cats.", npc.get("text"));

		assertNotNull(npc.getAttending());
		npc.preLogic();
		assertEquals("Bye.", npc.get("text"));
		assertNull(npc.getAttending());
	}

	@Test
	public void testIdea() {
		SpeakerNPC npc = NPCList.get().get("Felina");
		assertNotNull(npc);

		assertNull(npc.getIdea());
		npc.setIdea("walk");
		assertEquals("walk", npc.getIdea());

		npc.setIdea(null);
		assertNull(npc.getIdea());
	}

	//TODO NPC.setOutfit() function seems not to be used anywhere, so it could be removed.
	@Test
	public void testOutfit() {
		SpeakerNPC npc = NPCList.get().get("Felina");
		assertNotNull(npc);

		assertNull(npc.getIdea());
		npc.setOutfit("suite");
		assertEquals("suite", npc.get("outfit"));
	}
}
