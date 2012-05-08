/**
 * Separate class to handle commands Followed example from DiddiZ's LB.
 * 
 * @author Mitsugaru
 */
package com.mitsugaru.KarmicLog;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.mitsugaru.KarmicLog.config.Config;
import com.mitsugaru.KarmicLog.config.LocalizeConfig;
import com.mitsugaru.KarmicLog.permissions.PermCheck;
import com.mitsugaru.KarmicLog.permissions.Permission;

@SuppressWarnings("unused")
public class Commander implements CommandExecutor
{
	// Class variables
	private final KarmicLog plugin;
	private final PermCheck perm;
	private final static String bar = "======================";
	private final Config config;
	private final Map<String, Integer> page = new HashMap<String, Integer>();
	private final Map<String, Integer> cache = new HashMap<String, Integer>();
	private int limit;
	private long time;

	/**
	 * Constructor
	 * 
	 * @param KarmicLog
	 *            plugin
	 */
	public Commander(KarmicLog plugin)
	{
		// Instantiate variables
		this.plugin = plugin;
		config = plugin.getPluginConfig();
		perm = plugin.getPermissionHandler();
		time = 0;
	}
	
	/**
	 * Command handler
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args)
	{
		if (config.debugTime)
		{
			time = System.nanoTime();
		}
		// See if any arguments were given
		if (args.length == 0)
		{
			// Check if they have "karma" permission
			this.displayHelp(sender);
		}
		else
		{
			final String com = args[0].toLowerCase();
			final EnumMap<LocalString.Flag, String> info = new EnumMap<LocalString.Flag, String>(
					LocalString.Flag.class);
			info.put(LocalString.Flag.TAG, KarmicLog.TAG);
			if (com.equals("version") || com.equals("ver"))
			{
				// Version and author
				this.showVersion(sender, args);
			}
			else if (com.equals("?") || com.equals("help"))
			{
				this.displayHelp(sender);
			}
			else if(com.equals("reload"))
			{
				if(perm.checkPermission(sender, Permission.ADMIN.getNode()))
				{
					config.reloadConfig();
					LocalizeConfig.reload();
					sender.sendMessage(LocalString.RELOAD_CONFIG.parseString(info));
				}
				else
				{
					info.put(LocalString.Flag.EXTRA, Permission.ADMIN.getNode());
					sender.sendMessage(LocalString.PERMISSION_DENY.parseString(info));
				}
			}
			else
			{
				info.put(LocalString.Flag.EXTRA, com);
				sender.sendMessage(LocalString.UNKNOWN_COMMAND.parseString(info));
			}
		}
		if (config.debugTime)
		{
			debugTime(sender, time);
		}
		return true;
	}

	private void showVersion(CommandSender sender, String[] args)
	{
		sender.sendMessage(ChatColor.BLUE + bar + "=====");
		sender.sendMessage(ChatColor.GREEN + "KarmicLog v"
				+ plugin.getDescription().getVersion());
		sender.sendMessage(ChatColor.GREEN + "Coded by Mitsugaru");
		sender.sendMessage(ChatColor.BLUE + "===========" + ChatColor.GRAY
				+ "Config" + ChatColor.BLUE + "===========");
		if(config.debugTime)
			sender.sendMessage(ChatColor.GRAY + "Debug time: " + config.debugTime);
		if(config.debugEvents)
			sender.sendMessage(ChatColor.GRAY + "Debug events: " + config.debugEvents);
	}

	private void debugTime(CommandSender sender, long time)
	{
		time = System.nanoTime() - time;
		sender.sendMessage("[Debug]" + KarmicLog.TAG + "Process time: "
				+ time);
	}

	/**
	 * Show the help menu, with commands and description
	 * 
	 * @param sender
	 *            to display to
	 */
	private void displayHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.BLUE + "==========" + ChatColor.GOLD
				+ "KarmicLog" + ChatColor.BLUE + "==========");
		sender.sendMessage(LocalString.HELP_HELP.parseString(null));
		if(perm.checkPermission(sender, Permission.ADMIN.getNode()))
		{
			sender.sendMessage(LocalString.HELP_ADMIN_RELOAD.parseString(null));
		}
		sender.sendMessage(LocalString.HELP_VERSION.parseString(null));
	}
}