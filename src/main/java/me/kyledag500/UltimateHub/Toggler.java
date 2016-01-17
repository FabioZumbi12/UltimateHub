package me.kyledag500.UltimateHub;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class Toggler implements Listener {

	main main;
	public Plugin thisplugin;
	CustomConfig toggler = null;
	CustomConfig players = null;
	ArrayList<Player> cooldown = new ArrayList<Player>();
	ItemStack off = null;
	ItemStack on = null;
	String prefix = null;

	public Toggler(main plugin){
		main = plugin;
		toggler = main.togglerconfig;
		prefix = main.prefix;
		players = main.playertogglers;
	}
	
	public void setup(){
    	String[] offtype = toggler.getConfig().getString("off.type").split(":");
    	off = new ItemStack(Material.valueOf(offtype[0].toUpperCase()), 1, Short.parseShort(offtype[1]));
    	ItemMeta om = off.getItemMeta();
    	om.setDisplayName(ChatColor.translateAlternateColorCodes('&', toggler.getConfig().getString("off.displayName")));
    	ArrayList<String> lore = new ArrayList<String>();
    	for(String l : toggler.getConfig().getStringList("off.lore")){
    		lore.add(ChatColor.translateAlternateColorCodes('&', l));
    	}
    	om.setLore(lore);
    	off.setItemMeta(om);
    	
    	String[] ontype = toggler.getConfig().getString("on.type").split(":");
    	on = new ItemStack(Material.valueOf(ontype[0].toUpperCase()), 1, Short.parseShort(ontype[1]));
    	ItemMeta nm = on.getItemMeta();
    	nm.setDisplayName(ChatColor.translateAlternateColorCodes('&', toggler.getConfig().getString("on.displayName")));
    	ArrayList<String> onlore = new ArrayList<String>();
    	for(String l : toggler.getConfig().getStringList("on.lore")){
    		onlore.add(ChatColor.translateAlternateColorCodes('&', l));
    	}
    	nm.setLore(onlore);
    	on.setItemMeta(nm);
	}
	
	public void giveOff(final Player player){
		if(!cooldown.contains(player)){
			for(Player p : Bukkit.getOnlinePlayers()){
				player.showPlayer(p);
				if(!toggler.getConfig().getString("effect").equalsIgnoreCase("none")){
					player.playEffect(p.getLocation(), Effect.valueOf(toggler.getConfig().getString("effect").toUpperCase()), Effect.Type.PARTICLE);
				}
			}
			if(!toggler.getConfig().getString("sound").equalsIgnoreCase("none")){
				player.playSound(player.getLocation(), Sound.valueOf(toggler.getConfig().getString("sound").toUpperCase()), 1, 1);
			}
			player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', toggler.getConfig().getString("off.message")));
			players.getConfig().set(player.getUniqueId().toString(), "off");
			players.saveConfig();
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
	            public void run() {
	    			player.getInventory().setItem(Integer.parseInt(toggler.getConfig().getString("slot")), off);
	                  }
	          }, 1L);
			cooldown.add(player);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
	            public void run() {
	        		if(cooldown.contains(player)){
	        			cooldown.remove(player);
	        		}
	                  }
	          }, (Integer.parseInt(toggler.getConfig().getString("cooldown")) * 20));
		}else{
			player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', toggler.getConfig().getString("spamMessage")));

		}
	}
	
	public void giveOn(final Player player){
		if(!cooldown.contains(player)){
			for(Player p : Bukkit.getOnlinePlayers()){
				player.hidePlayer(p);
				if(!toggler.getConfig().getString("effect").equalsIgnoreCase("none")){
					player.playEffect(p.getLocation(), Effect.valueOf(toggler.getConfig().getString("effect").toUpperCase()), Effect.Type.PARTICLE);
				}
			}
			if(!toggler.getConfig().getString("sound").equalsIgnoreCase("none")){
				player.playSound(player.getLocation(), Sound.valueOf(toggler.getConfig().getString("sound").toUpperCase()), 1, 1);
			}
			players.getConfig().set(player.getUniqueId().toString(), "on");
			players.saveConfig();
			player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', toggler.getConfig().getString("on.message")));
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
	            public void run() {
	    			player.getInventory().setItem(Integer.parseInt(toggler.getConfig().getString("slot")), on);
	                  }
	          }, 1L);
			cooldown.add(player);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
	            public void run() {
	        		if(cooldown.contains(player)){
	        			cooldown.remove(player);
	        		}
	                  }
	          }, (Integer.parseInt(toggler.getConfig().getString("cooldown")) * 20));
		}
		else{
			player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', toggler.getConfig().getString("spamMessage")));
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		if(cooldown.contains(player)){
			cooldown.remove(player);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		final Player player = event.getPlayer();
		if(players.getConfig().getString(event.getPlayer().getUniqueId().toString()) == null){
			players.getConfig().set(event.getPlayer().getUniqueId().toString(), "off");
			players.saveConfig();
		}
		if(players.getConfig().getString(event.getPlayer().getUniqueId().toString() + "").equalsIgnoreCase("off")){
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
	            public void run() {
	            	giveOff(player);
	            	for(Player p : Bukkit.getOnlinePlayers()){
	            		if(p.getInventory().contains(on)){
	            			p.hidePlayer(player);
	            		}
	            	}
	                  }
	          }, 5L);	
		}
		else{
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
	            public void run() {
	            	giveOn(player);
	            	for(Player p : Bukkit.getOnlinePlayers()){
	            		if(p.getInventory().contains(on)){
	            			p.hidePlayer(player);
	            		}
	            	}
	                  }
	          }, 5L);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		final Player player = event.getPlayer();
		if ((event.getAction() != Action.PHYSICAL) && (player.getItemInHand().isSimilar(off))){
        	event.setCancelled(true);
    		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
                public void run() {
                	giveOn(player);
                      }
              }, 1L);
        }
		if ((event.getAction() != Action.PHYSICAL) && (player.getItemInHand().isSimilar(on))){
        	event.setCancelled(true);
    		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
                public void run() {
                	giveOff(player);
                      }
              }, 1L);
        }
	}
	
}
