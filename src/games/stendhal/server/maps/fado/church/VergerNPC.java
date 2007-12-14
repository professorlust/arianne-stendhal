package games.stendhal.server.maps.fado.church;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VergerNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Lukas") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(22, 9));
				nodes.add(new Node(16, 9));
				nodes.add(new Node(16, 4));
				nodes.add(new Node(19, 4));
				nodes.add(new Node(19, 3));
				nodes.add(new Node(22, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome to this place of worship. Are you here to be #married?");
				addJob("I am the church verger. I help with small menial tasks, but I do not mind, as my reward will come in the life beyond.");
				addHelp("My only advice is to love and be kind to one another");
				addQuest("I have eveything I need. But it does bring me pleasure to see people #married.");
				addReply("married", "If you want to be engaged, speak to Sister Benedicta. She'll make sure the priest knows about your plans.");
				addReply("yes", "Congratulations!");
				addReply("no", "A pity. I do hope you find a partner one day.");
				addGoodbye("Goodbye, go safely.");
			}
		};

		npc.setDescription("You see Lukas, the humble church verger.");
		npc.setEntityClass("vergernpc");
		npc.setPosition(22, 9);
		npc.initHP(100);
		zone.add(npc);
	}
}
