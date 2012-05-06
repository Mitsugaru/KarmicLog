package com.mitsugaru.KarmicLog;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.mitsugaru.KarmicLog.config.Config;
import com.mitsugaru.KarmicLog.permissions.PermCheck;

public class KarmicLog extends JavaPlugin
{
	public static final String TAG = "[KarmicLog]";
	public static final String SHORT_TAG = "[KL]";
	private Config config;
	private PermCheck perm;
	public static Map<String, String> sentMessages = new HashMap<String, String>();
	
	/**
	 * Method that is called when plugin is enabled
	 */
	@Override
	public void onEnable()
	{
		//Initialize config
		config = new Config(this);
		//Initialize permissions
		perm = new PermCheck(this);
		//Intialize commander
		getCommand("kl").setExecutor(new Commander(this));
	}

	public Config getPluginConfig()
	{
		return config;
	}

	public PermCheck getPermissionHandler()
	{
		// TODO Auto-generated method stub
		return perm;
	}
	
	/**
	 * Colorizes a given string to Bukkit standards
	 * 
	 * http://forums.bukkit.org/threads/methode-to-colorize.69543/#post-1063437
	 * 
	 * @param string
	 * @return String with appropriate Bukkit ChatColor in them
	 * @author AmberK
	 */
	public static String colorizeText(String string)
	{
		/**
		 * Colors
		 */
		string = string.replaceAll("&0", "" + ChatColor.BLACK);
		string = string.replaceAll("&1", "" + ChatColor.DARK_BLUE);
		string = string.replaceAll("&2", "" + ChatColor.DARK_GREEN);
		string = string.replaceAll("&3", "" + ChatColor.DARK_AQUA);
		string = string.replaceAll("&4", "" + ChatColor.DARK_RED);
		string = string.replaceAll("&5", "" + ChatColor.DARK_PURPLE);
		string = string.replaceAll("&6", "" + ChatColor.GOLD);
		string = string.replaceAll("&7", "" + ChatColor.GRAY);
		string = string.replaceAll("&8", "" + ChatColor.DARK_GRAY);
		string = string.replaceAll("&9", "" + ChatColor.BLUE);
		string = string.replaceAll("&a", "" + ChatColor.GREEN);
		string = string.replaceAll("&b", "" + ChatColor.AQUA);
		string = string.replaceAll("&c", "" + ChatColor.RED);
		string = string.replaceAll("&d", "" + ChatColor.LIGHT_PURPLE);
		string = string.replaceAll("&e", "" + ChatColor.YELLOW);
		string = string.replaceAll("&f", "" + ChatColor.WHITE);
		/**
		 * Formatting
		 */
		string = string.replaceAll("&k", "" + ChatColor.MAGIC);
		string = string.replaceAll("&l", "" + ChatColor.BOLD);
		string = string.replaceAll("&m", "" + ChatColor.STRIKETHROUGH);
		string = string.replaceAll("&n", "" + ChatColor.UNDERLINE);
		string = string.replaceAll("&o", "" + ChatColor.ITALIC);
		string = string.replaceAll("&r", "" + ChatColor.RESET);
		return string;
	}
}
