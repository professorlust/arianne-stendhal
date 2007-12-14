package games.stendhal.server.entity.item;

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.UseListener;

import java.util.Map;

/**
 * a basket which can be unwrapped.
 * 
 * @author kymara
 */
public class Stocking extends Box implements UseListener {

	// TODO: Make these configurable
	// for christmas presents
	private static final String[] ITEMS = { "mega_potion", "fish_pie",
			"lucky_charm", "diamond", "gold_bar", "empty_scroll" };

	/**
	 * Creates a new Stocking
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public Stocking(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * copy constructor
	 * 
	 * @param item
	 *            item to copy
	 */
	public Stocking(Stocking item) {
		super(item);
	}

	@Override
	protected boolean useMe(Player player) {
		this.removeOne();
		String itemName = ITEMS[Rand.rand(ITEMS.length)];
		Item item = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(
				itemName);
		player.sendPrivateText("Congratulations, you've got "
				+ Grammar.a_noun(itemName));
		player.equip(item, true);
		player.notifyWorldAboutChanges();
		return true;
	}

}
