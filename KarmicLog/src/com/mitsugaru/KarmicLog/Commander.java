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
		limit = config.listlimit;
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
			// Previous page
			/*else if (com.equals("prev"))
			{
				if (perm.checkPermission(sender, "KarmicLog.commands.list"))
				{
					// List, with previous page
					this.listPool(sender, -1);
				}
				else
				{
					sender.sendMessage(ChatColor.RED + KarmicLog.TAG
							+ " Lack permission: KarmicLog.commands.list");
				}
			}
			// Next page
			else if (com.equals("next"))
			{
				if (perm.checkPermission(sender, "KarmicLog.commands.list"))
				{
					// List with next page
					this.listPool(sender, 1);
				}
				else
				{
					sender.sendMessage(ChatColor.RED + KarmicLog.TAG
							+ " Lack permission: KarmicLog.commands.list");
				}
			}
			// List actions
			else if (com.equals("list"))
			{
				if (perm.checkPermission(sender, "KarmicLog.commands.list"))
				{
					this.listCommand(sender, args);
				}
				else
				{
					sender.sendMessage(ChatColor.RED + KarmicLog.TAG
							+ " Lack permission: KarmicLog.commands.list");
				}
			}*/
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

	private void listCommand(CommandSender sender, String[] args)
	{
		// TODO allow people to "find" items
		// i.e. limit list entries to what they want
		if (args.length > 1)
		{
			// If they provided a page number
			try
			{
				// Attempt to parse argument for page number
				int pageNum = Integer.parseInt(args[1]);
				// Set current page to given number
				page.put(sender.getName(), pageNum - 1);
				// Show page if possible
				this.listPool(sender, 0);
			}
			catch (NumberFormatException e)
			{
				// TODO this is where I would catch the item's
				// partial name. Probably use regex and see if
				// an item's name in cache matches.
				// Will need to modify listPool to accept regex
				// Can't think of a good way to page through this
				// new list without having a hashmap per custom
				// cache, and I really don't want to do that :\
				sender.sendMessage(ChatColor.YELLOW + KarmicLog.TAG
						+ " Invalid integer for page number");
			}
		}
		else
		{
			// List with current page
			this.listPool(sender, 0);
		}
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
		if(config.debugEconomy)
			sender.sendMessage(ChatColor.GRAY + "Debug economy: " + config.debugEconomy);
		if(config.debugUnhandled)
			sender.sendMessage(ChatColor.GRAY + "Debug unhandled: " + config.debugUnhandled);
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
		/*sender.sendMessage(ChatColor.GREEN + "/ks" + ChatColor.YELLOW
				+ " : Show karma");
		if (perm.checkPermission(sender, "KarmicShare.give"))
		{
			sender.sendMessage(ChatColor.GREEN + "/ks give" + ChatColor.YELLOW
					+ " : Give item stack in current hand");
		}
		if (perm.checkPermission(sender, "KarmicShare.take"))
		{
			sender.sendMessage(ChatColor.GREEN
					+ "/ks take <item>[:data] [amount]" + ChatColor.YELLOW
					+ " : Take item(s) from pool");
			sender.sendMessage(ChatColor.GREEN
					+ "/ks take <item name> [amount]" + ChatColor.YELLOW
					+ " : Take item(s) from pool");
		}
		sender.sendMessage(ChatColor.GREEN + "/ks list [page]"
				+ ChatColor.YELLOW + " : List items in pool");
		sender.sendMessage(ChatColor.GREEN + "/ks <prev | next>"
				+ ChatColor.YELLOW + " : Show previous/next page of list");
		sender.sendMessage(ChatColor.GREEN + "/ks value [prev|next|page#]"
				+ ChatColor.YELLOW
				+ " : List karma multiplier values, and page through list");*/
		sender.sendMessage(LocalString.HELP_HELP.parseString(null));
		/*if (perm.checkPermission(sender, "KarmicShare.info"))
		{
			sender.sendMessage(ChatColor.GREEN + "/ks info" + ChatColor.YELLOW
					+ " : Inspect currently held item");
		}
		if (perm.checkPermission(sender, "KarmicShare.karma.other"))
		{
			sender.sendMessage(ChatColor.GREEN + "/ks player <name>"
					+ ChatColor.YELLOW + " : Show karma for given player name");
		}
		if (perm.checkPermission(sender, "KarmicShare.admin"))
		{
			sender.sendMessage(ChatColor.GREEN + "/ks admin" + ChatColor.YELLOW
					+ " : List admin commands");
		}*/
		if(perm.checkPermission(sender, Permission.ADMIN.getNode()))
		{
			sender.sendMessage(LocalString.HELP_ADMIN_RELOAD.parseString(null));
		}
		sender.sendMessage(LocalString.HELP_VERSION.parseString(null));
	}

	/**
	 * Lists the items in the pool. Allows for pagination of the cache of items
	 * in pool.
	 * 
	 * @param CommandSender
	 *            of the "list" command so we know who we're outputting to
	 * @param Integer
	 *            of the page to change to, if needed. Zero shows current page.
	 */
	private void listPool(CommandSender sender, int pageAdjust)
	{
		// TODO get list and update cache
		final String[] array = new String[]{""};
		if (cache.isEmpty())
		{
			sender.sendMessage(ChatColor.RED + KarmicLog.TAG
					+ " No players");
			return;
		}
		if (!page.containsKey(sender.getName()))
		{
			page.put(sender.getName(), 0);
		}
		else
		{
			if (pageAdjust != 0)
			{
				int adj = page.get(sender.getName()).intValue() + pageAdjust;
				page.put(sender.getName(), adj);
			}
		}
		boolean valid = true;
		// Caluclate amount of pages
		int num = array.length / 8;
		double rem = (double) array.length % (double) config.listlimit;
		if (rem != 0)
		{
			num++;
		}
		if (page.get(sender.getName()).intValue() < 0)
		{
			// They tried to use /ks prev when they're on page 0
			sender.sendMessage(ChatColor.YELLOW + KarmicLog.TAG
					+ " Page does not exist");
			// reset their current page back to 0
			page.put(sender.getName(), 0);
			valid = false;
		}
		else if ((page.get(sender.getName()).intValue()) * config.listlimit > array.length)
		{
			// They tried to use /ks next at the end of the list
			sender.sendMessage(ChatColor.YELLOW + KarmicLog.TAG
					+ " Page does not exist");
			// Revert to last page
			page.put(sender.getName(), num - 1);
			valid = false;
		}
		if (valid)
		{
			// TODO Header with amount of pages
			// list
			for (int i = ((page.get(sender.getName()).intValue()) * config.listlimit); i < ((page
					.get(sender.getName()).intValue()) * config.listlimit)
					+ config.listlimit; i++)
			{
				// Don't try to pull something beyond the bounds
				if (i < array.length)
				{
				}
				else
				{
					break;
				}
			}
		}
	}

	/**
	 * Attempts to look up full name based on who's on the server Given a
	 * partial name
	 * 
	 * @author Frigid, edited by Raphfrk and petteyg359
	 */
	private String expandName(String Name)
	{
		int m = 0;
		String Result = "";
		for (int n = 0; n < plugin.getServer().getOnlinePlayers().length; n++)
		{
			String str = plugin.getServer().getOnlinePlayers()[n].getName();
			if (str.matches("(?i).*" + Name + ".*"))
			{
				m++;
				Result = str;
				if (m == 2)
				{
					return null;
				}
			}
			if (str.equalsIgnoreCase(Name))
				return str;
		}
		if (m == 1)
			return Result;
		if (m > 1)
		{
			return null;
		}
		return Name;
	}
}