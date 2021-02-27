package net.craftgalaxy.galaxycore.bungee.util;

import com.google.gson.Gson;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class PlayerUtil {

	private static final String LOOKUP_URL = "https://api.mojang.com/user/profiles/%s/names";
	private static final String GET_UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?t=0";
	private static final Gson GSON = new Gson();

	@Nullable
	public static PreviousNameEntry[] getNameHistory(@Nullable String uniqueId) throws IOException {
		if (uniqueId == null || uniqueId.isEmpty()) {
			return null;
		}

		URLConnection connection = new URL(String.format(PlayerUtil.LOOKUP_URL, uniqueId)).openConnection();
		connection.setDoInput(true);
		connection.setConnectTimeout(3000);
		connection.setReadTimeout(3000);
		connection.connect();
		BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String response = input.readLine();
		input.close();
		return PlayerUtil.GSON.fromJson(response, PreviousNameEntry[].class);
	}
}
