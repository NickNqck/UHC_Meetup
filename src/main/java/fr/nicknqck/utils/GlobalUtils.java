package fr.nicknqck.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import fr.nicknqck.utils.particles.MathUtil;
import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;

public class GlobalUtils {

	    public static void createBeautyExplosion(final Location loc, final int power) {
	        createBeautyExplosion(loc, power, false);
	    }

	    public static void createBeautyExplosion(final Location loc, final int power, final boolean fire) {
	        List<Location> blocks = generateSphere(loc, power, false);
	        for (final Location blockLoc : blocks) {
	            final Block block = blockLoc.getBlock();
	            if (block.getType() != Material.AIR && block.getType() != Material.BEDROCK) {
	            	MathUtil.sendParticle(EnumParticle.EXPLOSION_LARGE, loc);
	                blockLoc.getBlock().setType(Material.AIR);
	            }
	        }
	    }
	    public static List<Location> generateSphere(final Location centerBlock, final int radius, final boolean hollow) {
	        if (centerBlock == null) return new ArrayList<>();
	        final List<Location> circleBlocks = new ArrayList<>();
	        final int bx = centerBlock.getBlockX();
	        final int by = centerBlock.getBlockY();
	        final int bz = centerBlock.getBlockZ();
	        for (int x = bx - radius; x <= bx + radius; ++x) {
	            for (int y = by - radius; y <= by + radius; ++y) {
	                for (int z = bz - radius; z <= bz + radius; ++z) {
	                    final double distance = (bx - x) * (bx - x) + (bz - z) * (bz - z) + (by - y) * (by - y);
	                    if (distance < radius * radius && (!hollow || distance >= (radius - 1) * (radius - 1))) {
	                        final Location l = new Location(centerBlock.getWorld(), x, y, z);
	                        circleBlocks.add(l);
	                    }
	                }
	            }
	        }
	        return circleBlocks;
	    }
	    public static void spawnFakeLightning(final Player player, final Location loc) {
	        spawnFakeLightningEffect(player, loc, false);
	    }

	    public static void spawnFakeLightningEffect(final Player player, final Location loc, final boolean isEffect) {
	        final EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
	        final EntityLightning lightning = new EntityLightning(nmsPlayer.getWorld(), loc.getX(), loc.getY(), loc.getZ(), isEffect, false);
	        nmsPlayer.playerConnection.sendPacket(new PacketPlayOutSpawnEntityWeather(lightning));
	        player.playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 1.0f, 1.0f);
	    }
	    public static ItemStack getPlayerHead(String value) {
	        ItemStack playerHead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
	        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();

	        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
	        profile.getProperties().put("textures", new Property("textures", value));

	        try {
	            Field profileField = skullMeta.getClass().getDeclaredField("profile");
	            profileField.setAccessible(true);
	            profileField.set(skullMeta, profile);
	        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
	            e.printStackTrace();
	        }

	        playerHead.setItemMeta(skullMeta);
	        return playerHead;
	    }
	    public static ItemStack getPlayerHead(UUID playerUUID) {
	        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
	        SkullMeta meta = (SkullMeta) head.getItemMeta();
	        GameProfile profile = new GameProfile(playerUUID, null);
	        profile.getProperties().put("textures", new Property("textures", getTexture(playerUUID)));
	        Field profileField;
	        try {
	            profileField = meta.getClass().getDeclaredField("profile");
	            profileField.setAccessible(true);
	            profileField.set(meta, profile);
	        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
	            e.printStackTrace();
	        }
	        head.setItemMeta(meta);
	        return head;
	    }
		private ItemStack truc(Player player){
			return new ItemBuilder(Material.SKULL_ITEM).setSkullOwner(player.getName()).toItemStack();
		}
	    public static String getTexture(UUID playerUUID) {
	        String texture = null;
	        try {
	            URL url = new URL("https://crafatar.com/avatars/" + playerUUID.toString() + "?overlay");
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setRequestMethod("GET");
	            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	            String line;
	            StringBuilder response = new StringBuilder();
	            while ((line = reader.readLine()) != null) {
	                response.append(line);
	            }
	            reader.close();
	            texture = Base64.getEncoder().encodeToString(response.toString().getBytes());
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return texture;
	    }
	    public String getTextureFromPseudo(String name) {
	        try {
	            URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
	            InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
	            String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();

	            URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
	            InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
	            JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
	            String texture = textureProperty.get("value").getAsString();

	            return texture;
	        } catch (IOException e) {
	            System.err.println("Could not get skin data from session servers!");
	            e.printStackTrace();
	            return null;
	        }
	    }
	    public String getSignatureFromPseudo(String name) {
	        try {
	            URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
	            InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
	            String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();

	            URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
	            InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
	            JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
	            String signature = textureProperty.get("signature").getAsString();

	            return signature;
	        } catch (IOException e) {
	            System.err.println("Could not get skin data from session servers!");
	            e.printStackTrace();
	            return null;
	        }
	    }
	public static int getItemAmount(Player player, Material material) {
		int toReturn = 0;
		for (ItemStack content : player.getInventory().getContents()) {
			if (content != null && content.getType() == material) {
				toReturn += content.getAmount();
			}
		}
		return toReturn;
	}
}