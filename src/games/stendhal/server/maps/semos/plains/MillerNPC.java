package games.stendhal.server.maps.semos.plains;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SeedSellerBehaviour;

import java.util.Map;
import java.util.TreeMap;

/**
 * The miller (original name: Jenny). She mills flour for players who bring
 * grain. 
 */
//TODO: take NPC definition elements which are currently in XML and include here
public class MillerNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addJob("I run this windmill, where I can #mill people's #grain into flour for them. I also supply the bakery in Semos.");
		npc.addReply("grain",
		        "There's a farm nearby; they usually let people harvest there. You'll need a scythe, of course.");
		npc.addHelp("Do you know the bakery in Semos? I'm proud to say they use my flour. But the wolves ate my delivery boy again recently... they're probably running out.");
		npc.addGoodbye();
		npc.addOffer("You can #plant my seeds to grow beautiful flowers.");
		npc.addReply("plant","Your seeds should be planted on fertile ground. Look for the brown ground just over the path from the arandula patch in semos plains over yonder. Seeds will thrive there, you can visit each day to see if your flower has grown. When it is ready, it can be picked. The area is open to everyone so there's a chance someone else will pick your flower, but luckily seeds are cheap!");
		// Jenny mills flour if you bring her grain.
		final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
		requiredResources.put("grain", 5);

		final ProducerBehaviour behaviour = new ProducerBehaviour("jenny_mill_flour",
				"mill", "flour", requiredResources, 2 * 60);
		new SellerAdder().addSeller(npc, new SeedSellerBehaviour());
		new ProducerAdder().addProducer(npc, behaviour,
		        "Greetings! I am Jenny, the local miller. If you bring me some #grain, I can #mill it into flour for you.");
	}
}
