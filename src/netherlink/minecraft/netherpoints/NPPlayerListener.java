/*
	Copyright (C) 2011 by NetherLink
	
	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:
	
	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.
	
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
*/

package netherlink.minecraft.netherpoints;

import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

public class NPPlayerListener extends PlayerListener{

	/*
	 * Variables
	 */
	
	netherpoints p; // The plugin.
	TeleportManager m; // The teleport manager.
	
	/*
	 * Constructor
	 */
	
	public NPPlayerListener(netherpoints plugin, TeleportManager teleportManager){
		p = plugin;
		m = teleportManager;
	}
	
	/*
	 * Listener Functions
	 */
	
	public void onPlayerInteract(PlayerInteractEvent e){
		if(e.isCancelled()) // If the event is canceled ignore it.
			return;
		if(e.getPlayer() == null) // Ignore if it wasn't a player generating the event.
			return;
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) // Ignore if it wasn't a right click.
			return;
		if(e.getClickedBlock().getTypeId() == 63 || e.getClickedBlock().getTypeId() == 68); // Ignore if it wasn't a sign clicked.
		else return;
		Sign s = (Sign)e.getClickedBlock().getState();
		if(s.getLine(0).equalsIgnoreCase("[TELEPORTER]") ||
				s.getLine(0).equalsIgnoreCase("-TELEPORTER-") ||
				s.getLine(0).equalsIgnoreCase("[TELEPORT]") ||
				s.getLine(0).equalsIgnoreCase("-TELEPORT-")){
			m.teleport(e.getPlayer(), s.getBlock().getRelative(0, -1, 0), s.getLine(3), s.getLine(1), s.getLine(2));
		}
	}
	
}
