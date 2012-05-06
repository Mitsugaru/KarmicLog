/**
 * Config file mimicking DiddiZ's Config class file in LB. Tailored for this
 * plugin.
 * 
 * @author Mitsugaru
 */
package com.mitsugaru.KarmicLog.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mitsugaru.KarmicLog.Item;
import com.mitsugaru.KarmicLog.KarmicLog;

public class Config
{
	// Class variables
	private KarmicLog plugin;
	public String host, port, database, user, password;
	public static String tablePrefix;
	public boolean debugTime, debugEvents, debugEconomy, debugUnhandled,
			useMySQL, importSQL, portalCreateNether, portalCreateEnd,
			portalCreateCustom, blockPlaceStatic, blockDestroyStatic,
			craftItemStatic, enchantItemStatic, itemDropStatic, commandStatic,
			pickupStatic, shootBowDenyForce;
	public int listlimit;
	public double shootBowForce;
	private final Map<Item, KLItemInfo> values = new HashMap<Item, KLItemInfo>();

	// TODO ability to change config in-game
	/**
	 * Constructor and initializer
	 * 
	 * @param Karmiconomy
	 *            plugin
	 */
	public Config(KarmicLog plugin)
	{
		this.plugin = plugin;
		// Grab config
		final ConfigurationSection config = plugin.getConfig();
		// LinkedHashmap of defaults
		final Map<String, Object> defaults = new LinkedHashMap<String, Object>();
		defaults.put("listlimit", 10);
		defaults.put("mysql.use", false);
		defaults.put("mysql.host", "localhost");
		defaults.put("mysql.port", 3306);
		defaults.put("mysql.database", "minecraft");
		defaults.put("mysql.user", "username");
		defaults.put("mysql.password", "pass");
		defaults.put("mysql.tablePrefix", "kl_");
		defaults.put("mysql.import", false);
		defaults.put("debug.events", false);
		defaults.put("debug.time", false);
		defaults.put("version", plugin.getDescription().getVersion());
		// Insert defaults into config file if they're not present
		for (final Entry<String, Object> e : defaults.entrySet())
		{
			if (!config.contains(e.getKey()))
			{
				config.set(e.getKey(), e.getValue());
			}
		}
		// Save config
		plugin.saveConfig();
		// Load variables from config
		/**
		 * SQL info
		 */
		useMySQL = config.getBoolean("mysql.use", false);
		host = config.getString("mysql.host", "localhost");
		port = config.getString("mysql.port", "3306");
		database = config.getString("mysql.database", "minecraft");
		user = config.getString("mysql.user", "user");
		password = config.getString("mysql.password", "password");
		tablePrefix = config.getString("mysql.prefix", "kl_");
		importSQL = config.getBoolean("mysql.import", false);
		// Load all other settings
		this.loadSettings(config);
		// Load config for item specific value
		this.loadWatchItemMap();
		// Finally, do a bounds check on parameters to make sure they are legal
		this.boundsCheck();
	}

	public void set(String path, Object o)
	{
		final ConfigurationSection config = plugin.getConfig();
		config.set(path, o);
		plugin.saveConfig();
	}

	/**
	 * Check if updates are necessary
	 */
	public void checkUpdate()
	{
		// Check if need to update
		ConfigurationSection config = plugin.getConfig();
		if (Double.parseDouble(plugin.getDescription().getVersion()) > Double
				.parseDouble(config.getString("version")))
		{
			// Update to latest version
			plugin.getLogger().info(
					"Updating to v" + plugin.getDescription().getVersion());
			this.update();
		}
	}

	/**
	 * This method is called to make the appropriate changes, most likely only
	 * necessary for database schema modification, for a proper update.
	 */
	@SuppressWarnings("unused")
	private void update()
	{
		// Grab current version
		final double ver = Double.parseDouble(plugin.getConfig().getString(
				"version"));
		// can remove old config options using the following:
		// plugin.getConfig().set("path.to.remove", null);
		// Update version number in config.yml
		plugin.getConfig().set("version", plugin.getDescription().getVersion());
		plugin.saveConfig();
		plugin.getLogger().info("Upgrade complete");
	}

	/**
	 * Reloads info from yaml file(s)
	 */
	public void reloadConfig()
	{
		// Initial relaod
		plugin.reloadConfig();
		// Grab config
		ConfigurationSection config = plugin.getConfig();
		//Load settings
		this.loadSettings(config);
		// Load config for item specific values
		this.loadWatchItemMap();
		// Check bounds
		this.boundsCheck();
		plugin.getLogger().info("Config reloaded");
	}

	private void loadSettings(ConfigurationSection config)
	{
		/**
		 * General Settings
		 */
		listlimit = config.getInt("listlimit", 10);
		debugTime = config.getBoolean("debug.time", false);
		debugEvents = config.getBoolean("debug.events", false);
		debugEconomy = config.getBoolean("debug.economy", false);
		debugUnhandled = config.getBoolean("debug.unhandled", false);
		/**
		 * Event Settings
		 */
		// destroy
		blockDestroyStatic = config.getBoolean("block.destroy.static", true);
		// place
		blockPlaceStatic = config.getBoolean("block.place.static", true);
		/**
		 * Item
		 */
		// craft
		craftItemStatic = config.getBoolean("item.craft.static", true);
		// enchant
		enchantItemStatic = config.getBoolean("item.enchant.static", true);
		// drop
		itemDropStatic = config.getBoolean("item.drop.static", true);
		// pickup
		pickupStatic = config.getBoolean("item.drop.static", true);
		/**
		 * Bow
		 */
		// shoot
		shootBowDenyForce = config
				.getBoolean("bow.shoot.denyOnLowForce", false);
		shootBowForce = config.getDouble("bow.shoot.minimumforce", 0.0);
		/**
		 * Player section
		 */
		// command
		commandStatic = config.getBoolean("player.command.static", true);
		/**
		 * Portal
		 */
		// create nether
		portalCreateNether = config.getBoolean("portal.createNether.enabled",
				false);
		// create end
		portalCreateEnd = config.getBoolean("portal.createEnd.enabled", false);
		// create custom
		portalCreateCustom = config.getBoolean("portal.createCustom.enabled",
				false);
		/**
		 * Vehicle
		 * 
		 * TODO implement
		 */
		// enter
		/*
		 * vehicleEnter = config.getBoolean("vehicle.enter.enabled", false);
		 * vehicleEnterDenyPay =
		 * config.getBoolean("vehicle.enter.denyOnLackPay", false);
		 * vehicleEnterDenyLimit =
		 * config.getBoolean("vehicle.enter.denyOnLimit", false);
		 * vehicleEnterLimit = config.getInt("vehicle.enter.limit", 100);
		 * vehicleEnterPay = config.getDouble("vehicle.enter.pay", 0.1);
		 */
		// exit
		/*
		 * vehicleExit = config.getBoolean("vehicle.exit.enabled", false);
		 * vehicleExitLimit = config.getInt("vehicle.exit.limit", 100);
		 * vehicleExitPay = config.getDouble("vehicle.exit.pay", 0.1);
		 */
	}

	/**
	 * Check the bounds on the parameters to make sure that all config variables
	 * are legal and usable by the plugin
	 */
	private void boundsCheck()
	{
		// TODO format all doubles to 2 decimal places
	}

	/**
	 * Loads the per-item karma values into a hashmap for later usage
	 */
	private void loadWatchItemMap()
	{
		// Load karma file
		final YamlConfiguration watchFile = this.watchFile();
		// Load custom karma file into map
		for (final String entry : watchFile.getKeys(false))
		{
			try
			{
				// Attempt to parse non data value nodes
				int key = Integer.parseInt(entry);
				if (key <= 0)
				{
					plugin.getLogger().warning(
							KarmicLog.TAG
									+ " Zero or negative item id for entry: "
									+ entry);
				}
				else
				{
					// If it has child nodes, parse those as well
					if (watchFile.isConfigurationSection(entry))
					{
						values.put(new Item(key, Byte.parseByte("" + 0),
								(short) 0), parseInfo(watchFile, entry));
					}
					else
					{
						plugin.getLogger().warning("No section for " + entry);
					}
				}
			}
			catch (final NumberFormatException ex)
			{
				// Potential data value entry
				if (entry.contains("&"))
				{
					try
					{
						final String[] split = entry.split("&");
						final int item = Integer.parseInt(split[0]);
						final int data = Integer.parseInt(split[1]);
						if (item <= 0)
						{
							plugin.getLogger()
									.warning(
											KarmicLog.TAG
													+ " Zero or negative item id for entry: "
													+ entry);
						}
						else
						{
							if (watchFile.isConfigurationSection(entry))
							{
								if (item != 373)
								{
									values.put(
											new Item(item, Byte.parseByte(""
													+ data), (short) data),
											parseInfo(watchFile, entry));
								}
								else
								{
									values.put(
											new Item(item, Byte
													.parseByte("" + 0),
													(short) data),
											parseInfo(watchFile, entry));
								}
							}
							else
							{
								plugin.getLogger().warning(
										"No section for " + entry);
							}
						}
					}
					catch (ArrayIndexOutOfBoundsException a)
					{
						plugin.getLogger()
								.warning(
										"Wrong format for "
												+ entry
												+ ". Must follow '<itemid>&<datavalue>:' entry.");
					}
					catch (NumberFormatException exa)
					{
						plugin.getLogger().warning(
								"Non-integer number for " + entry);
					}
				}
				else
				{
					plugin.getLogger().warning("Invalid entry for " + entry);
				}
			}
		}
		plugin.getLogger().info("Loaded custom values");
	}

	public Map<Item, KLItemInfo> getItemValueMap()
	{
		final Map<Item, KLItemInfo> values = new HashMap<Item,KLItemInfo>();
		return values;
	}

	private KLItemInfo parseInfo(YamlConfiguration config, String path)
	{
		KLItemInfo info = new KLItemInfo();
		return info;
	}

	// TODO command value file

	/**
	 * Loads the value file. Contains default values If the value file isn't
	 * there, or if its empty, then load defaults.
	 * 
	 * @return YamlConfiguration file
	 */
	private YamlConfiguration watchFile()
	{
		final File file = new File(plugin.getDataFolder().getAbsolutePath()
				+ "/watch.yml");
		// 
		final YamlConfiguration watchFile = YamlConfiguration
				.loadConfiguration(file);
		if (watchFile.getKeys(false).isEmpty())
		{
			// Defaults
			
			// Insert defaults into file if they're not present
			try
			{
				// Save the file
				watchFile.save(file);
			}
			catch (IOException e1)
			{
				plugin.getLogger().warning(
						"File I/O Exception on saving karma list");
				e1.printStackTrace();
			}
		}
		return watchFile;
	}

	// Private class to hold item specific information
	public class KLItemInfo
	{

		public KLItemInfo()
		{

		}
	}
}
