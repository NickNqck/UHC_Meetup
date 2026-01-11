package fr.nicknqck.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import fr.nicknqck.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class GlobalUtils {

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
	public static ItemStack getAsyncPlayerHead(UUID uuid) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();

		GameProfile profile = new GameProfile(uuid, null);
		Property texture = getTextureProperty(uuid);

		if (texture != null) {
			profile.getProperties().put("textures", texture);
		}

		try {
			Field field = meta.getClass().getDeclaredField("profile");
			field.setAccessible(true);
			field.set(meta, profile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		skull.setItemMeta(meta);
		return skull;
	}
	public static Property getTextureProperty(UUID uuid) {
		try {
			URL url = new URL(
					"https://sessionserver.mojang.com/session/minecraft/profile/"
							+ uuid.toString().replace("-", "")
							+ "?unsigned=false"
			);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			connection.setUseCaches(false);

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(connection.getInputStream())
			);

			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			JsonObject json = new JsonParser().parse(response.toString()).getAsJsonObject();
			JsonArray properties = json.getAsJsonArray("properties");

			for (JsonElement element : properties) {
				JsonObject prop = element.getAsJsonObject();
				if (prop.get("name").getAsString().equals("textures")) {
					return new Property(
							"textures",
							prop.get("value").getAsString(),
							prop.has("signature") ? prop.get("signature").getAsString() : null
					);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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

                return textureProperty.get("signature").getAsString();
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