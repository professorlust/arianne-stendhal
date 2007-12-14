/* $Id$ */
package games.stendhal.server.script;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.util.List;

/**
 * runs the garbage collector manually (for memory profiling)
 * 
 * @author hendrik
 */
public class GC extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		System.gc();
	}
}
