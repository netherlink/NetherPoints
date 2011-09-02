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

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.block.*;

public class NPBlockListener extends BlockListener{

	/*
	 * Variables
	 */
	
	netherpoints p; // This is the plugin.
	TeleportManager m; // This is the teleport manager.
	
	/*
	 * Constructor
	 */
	
	public NPBlockListener(netherpoints plugin, TeleportManager teleportManager){
		p = plugin;
		m = teleportManager;
	}
	
	/*
	 * Listener Functions
	 */
	
	public void onBlockBreak(BlockBreakEvent e){
		if(e.isCancelled()) // If the event is canceled ignore it.
			return;
		if(e.getBlock().getTypeId() != 20) // If the block isn't glass ignore it.
			return;
		HashMap<Block, Waypoint> list = m.getList();
		if(list == null || list.size() < 1)
			return;
		if(list.containsKey(e.getBlock())){
			list.remove(e.getBlock());
			e.getPlayer().sendMessage(ChatColor.GREEN + "Waypoint deleted.");
		}
	}
	
	public void onSignChange(SignChangeEvent e){
		if(e.isCancelled()) // If the event is canceled ignore it.
			return;
		if(e.getPlayer() == null) // If a player didn't generate the event, ignore it.
			return;
		if(e.getLine(0).equalsIgnoreCase("[WAYPOINT]") ||
				e.getLine(0).equalsIgnoreCase("-WAYPOINT-")){
			m.waypoint(e.getPlayer(), e.getBlock().getRelative(0, -1, 0), e.getLine(2), e.getLine(1));
		}
		if(e.getLine(0).equalsIgnoreCase("[TELEPORTER]") ||
				e.getLine(0).equalsIgnoreCase("-TELEPORTER-") ||
				e.getLine(0).equalsIgnoreCase("[TELEPORT]") ||
				e.getLine(0).equalsIgnoreCase("-TELEPORT-")){
			m.teleport(e.getPlayer(), e.getBlock().getRelative(0, -1, 0), e.getLine(3), e.getLine(1), e.getLine(2));
		}
	}
	
}
