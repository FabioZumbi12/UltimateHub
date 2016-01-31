package me.kyledag500.UltimateHub;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class General implements Listener, CommandExecutor{
	main Main;
	FileConfiguration config;
	CustomConfig players;
	Location spawn;
	
	public General(main main){
		Main = main;
		players = main.generalplayers;		
		config = Main.getConfig();
		getConfig().addDefault("prefix", "&f[&4Ultimate&6Hub&f]");
		config.addDefault("clearInvOnJoin", true);
		config.addDefault("autoUpdate", false);
		
		config.addDefault("buildAndBreak", false);
		config.addDefault("allowWeather", false);
		config.addDefault("hungerAndDamage", false);
		config.addDefault("pickupAndDrop", false);
		config.addDefault("OPsBypassAll", false);
		config.addDefault("allowChat", true);
		config.addDefault("allowCommands", false);
		config.addDefault("commandWhitelist", Arrays.asList("someCommand"));
		
		config.addDefault("spawn.tpOnJoin", true);
		config.addDefault("spawn.command", true);
		config.addDefault("spawn.world", "world");
		config.addDefault("spawn.x", "0");
		config.addDefault("spawn.y", "100");
		config.addDefault("spawn.z", "0");
		config.addDefault("spawn.pitch", "0");
		config.addDefault("spawn.yaw", "0");
		
		config.addDefault("welcomeMessage.enabled", true);
		config.addDefault("welcomeMessage.message", "&c%player% &ahas joined the server for the first time! Say hello!");
		
		config.addDefault("joinMessage.enabled", false);
		config.addDefault("joinMessage.message", "&aHey &c%player%&a! This is the message everyone sees when someone joins.");
		config.addDefault("leaveMessage.enabled", false);
		config.addDefault("leaveMessage.message", "&aHey &c%player%&a! This is the message everyone sees when someone leaves.");
		
		ArrayList<String> motd = new ArrayList<String>();
		motd.add("&a&m===================");
		motd.add("&4Welcome to MyServer, &c%player%&4!");
		motd.add("&6We are running UltimateHub");
		motd.add("&a&m===================");
		config.addDefault("motd.enabled", true);
		config.addDefault("motd.message", motd);
		
		config.options().copyDefaults(true);
		Main.saveConfig();	
		reloadSpawn();
	}
	
	private void reloadSpawn(){
		World world = Bukkit.getWorld(getConfig().getString("spawn.world"));
		Double x = Double.parseDouble(getConfig().getString("spawn.x")+".500");
		Double y = Double.parseDouble(getConfig().getString("spawn.y"));
		Double z = Double.parseDouble(getConfig().getString("spawn.z")+".500");
		Float pitch = Float.valueOf(getConfig().getString("spawn.pitch"));
		Float yaw = Float.valueOf(getConfig().getString("spawn.yaw"));
		spawn = new Location(world, x, y, z, pitch, yaw); 
		Location checkBlock = new Location(world, x, spawn.getBlockY()-1, z, pitch, yaw);
		if (world == null || checkBlock.getBlock().isEmpty()){
			spawn = null;
		}
	}
		
	private FileConfiguration getConfig(){
		return Main.getConfig();
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		if (!getConfig().getBoolean("allowChat")){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void weatherChange(WeatherChangeEvent e){
		if (!getConfig().getBoolean("allowWeather") && !e.getWorld().hasStorm() && !e.getWorld().isThundering()){
			e.setCancelled(true);			
		}
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e){
		String cmd = e.getMessage().split(" ")[0];
		//Bukkit.getConsoleSender().sendMessage("Command: " + cmd);
		if (cmd.equalsIgnoreCase("/login") 
				|| cmd.equalsIgnoreCase("/logout")
				|| cmd.equalsIgnoreCase("/register")
				|| cmd.equalsIgnoreCase("/email") 
				|| cmd.equalsIgnoreCase("/changepassword")				
				|| getConfig().getStringList("commandWhitelist").contains(cmd.replace("/", ""))){
			return;
		}		
		if (getConfig().getBoolean("OPsBypassAll") && e.getPlayer().isOp()){
			return;
		}
		if (getConfig().getBoolean("allowCommands")){
			return;
		}
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onCOnsume(PlayerItemConsumeEvent e){
		if (!getConfig().getBoolean("buildAndBreak")){
			if (getConfig().getBoolean("OPsBypassAll") && e.getPlayer().isOp()){
				return;
			}
        	e.setCancelled(true);
        }
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e){
        if (!getConfig().getBoolean("buildAndBreak")){
        	if (getConfig().getBoolean("OPsBypassAll") && e.getPlayer().isOp()){
				return;
			}
        	e.setCancelled(true);
        }
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e){
		if (!getConfig().getBoolean("hungerAndDamage")){						
			e.setCancelled(true);
			e.setDamage(20);
		}
	}
	
	@EventHandler
	public void onHunger(FoodLevelChangeEvent e){
		if (!getConfig().getBoolean("hungerAndDamage")){			
			e.setCancelled(true);
			e.setFoodLevel(20);
		}
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent e){
		if (!getConfig().getBoolean("pickupAndDrop")){
			if (getConfig().getBoolean("OPsBypassAll") && e.getPlayer().isOp()){
				return;
			}
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPickup(PlayerDropItemEvent e){
		if (!getConfig().getBoolean("pickupAndDrop")){
			if (getConfig().getBoolean("OPsBypassAll") && e.getPlayer().isOp()){
				return;
			}
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onKickLeave(PlayerKickEvent event){
		Player player = event.getPlayer();
		if(getConfig().getString("leaveMessage.enabled").equalsIgnoreCase("true")){
			event.setLeaveMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("leaveMessage.message")).replace("%player%", player.getName()));
		}
		else{
			event.setLeaveMessage(null);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		if(getConfig().getString("leaveMessage.enabled").equalsIgnoreCase("true")){
			event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("leaveMessage.message")).replace("%player%", player.getName()));
		}
		else{
			event.setQuitMessage(null);
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(getConfig().getString("joinMessage.enabled").equalsIgnoreCase("true")){
			event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("joinMessage.message")).replace("%player%", player.getName()));
		}
		else{
			event.setJoinMessage(null);
		}
		joinEvent(player);
	}
	
	private void joinEvent(final Player player){
		if(players.getConfig().getString(player.getUniqueId().toString()) == null){
			if(getConfig().getString("welcomeMessage.enabled").equalsIgnoreCase("true")){
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("welcomeMessage.message")).replace("%player%", player.getName()));
			}	
		}
		players.getConfig().set(player.getUniqueId().toString() + ".username", player.getName());
		players.saveConfig();
		if(getConfig().getString("clearInvOnJoin").equalsIgnoreCase("true")){
			player.getInventory().clear();
		}
		if(getConfig().getString("spawn.tpOnJoin").equalsIgnoreCase("true") && spawn != null){
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main, new Runnable() {
	            public void run() {
	            	player.teleport(spawn);
	                  }
	          }, 5L);
		}
		if(getConfig().getString("motd.enabled").equalsIgnoreCase("true")){
			for(String s : getConfig().getStringList("motd.message")){
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', s).replace("%player%", player.getName()));
			}
		}		
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main, new Runnable() {
            public void run() {
            	Main.selector.giveSelector(player);
                  }
          }, 5L);
				
		if(players.getConfig().getString(player.getUniqueId().toString()) == null){
			players.getConfig().set(player.getUniqueId().toString(), "off");
			players.saveConfig();
		}
		
		if(players.getConfig().getString(player.getUniqueId().toString() + "").equalsIgnoreCase("off")){
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main, new Runnable() {
	            public void run() {
	            	Main.Toggler.giveOff(player);
	            	for(Player p : Bukkit.getOnlinePlayers()){
	            		if(p.getInventory().contains(Main.Toggler.on)){
	            			p.hidePlayer(player);
	            		}
	            	}
	                  }
	          }, 5L);	
		}
		else{
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main, new Runnable() {
	            public void run() {
	            	Main.Toggler.giveOn(player);
	            	for(Player p : Bukkit.getOnlinePlayers()){
	            		if(p.getInventory().contains(Main.Toggler.on)){
	            			p.hidePlayer(player);
	            		}
	            	}
	                  }
	          }, 5L);
		}
	}
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, final String[] args) {
		if (args.length == 1){
			if (args[0].equalsIgnoreCase("reload")){
				//Main.getServer().getPluginManager().disablePlugin(Main);
				//Main.getServer().getPluginManager().enablePlugin(Main);
				Main.reloadConfig();
				reloadSpawn();
				Main.saveConfig();
				for (Player p:Main.getServer().getOnlinePlayers()){
					joinEvent(p);
				}				
				sender.sendMessage(Main.prefix + ChatColor.GREEN+"Configurations reloaded!");
			}
		}
		return true;		
	}

}
