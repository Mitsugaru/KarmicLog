package com.mitsugaru.KarmicLog.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.mitsugaru.KarmicLog.KarmicLog;
import com.mitsugaru.KarmicLog.config.Config;
import com.mitsugaru.KarmicLog.config.Config.KLItemInfo;
import com.mitsugaru.KarmicLog.Item;

public class KarmicLogListener
{
	private KarmicLog plugin;
	private Config config;

	public KarmicLogListener(KarmicLog plugin)
	{
		this.plugin = plugin;
		this.config = plugin.getPluginConfig();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void blockPlaceValid(final BlockPlaceEvent event)
	{
		// TODO deny
		if (!event.isCancelled() && event.getPlayer() != null)
		{

			final Item placed = new Item(event.getBlockPlaced().getTypeId(),
					event.getBlockPlaced().getData(), (short) 0);
			if (config.getItemValueMap().containsKey(placed))
			{
				final KLItemInfo info = config.getItemValueMap().get(placed);
				final Player player = event.getPlayer();
				if (info.checkWorld(player.getWorld().getName())
						&& !plugin.getPermissionHandler().checkPermission(
								player, info.getIgnoreNode()))
				{
					//TODO notify
					if(info.deny)
					{
						//Cancel
						event.setCancelled(true);
						//TODO message
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void blockDestroyValid(final BlockBreakEvent event)
	{
		// TODO deny
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void craftItemValid(final CraftItemEvent event)
	{
		// TODO deny
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void enchantItemValid(final EnchantItemEvent event)
	{
		// TODO deny
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void bucketEmptyValid(final PlayerBucketEmptyEvent event)
	{
		// TODO deny lava and water
		switch (event.getBucket())
		{
			case LAVA_BUCKET:
			{

			}
			case WATER_BUCKET:
			{

			}
			default:
			{

			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void bucketFillValid(final PlayerBucketFillEvent event)
	{
		// TODO deny
		switch (event.getBlockClicked().getType())
		{
			case STATIONARY_LAVA:
			{

			}
			case STATIONARY_WATER:
			{

			}
			default:
			{

			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void itemPickupValid(final PlayerPickupItemEvent event)
	{
		// TODO deny
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void itemDropValid(final PlayerDropItemEvent event)
	{
		// TODO deny
	}
}
