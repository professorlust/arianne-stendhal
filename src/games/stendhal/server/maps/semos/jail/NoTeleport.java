package games.stendhal.server.maps.semos.jail;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;

import java.util.Map;

/**
 * Semos Jail - Level -3
 * 
 * @author hendrik
 */
public class NoTeleport implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		zone.setTeleportAllowed(false);
	}
}
