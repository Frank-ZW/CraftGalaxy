# CraftGalaxy

This project is a series of Minecraft plugins intended for servers to run their own minigames through the use of a proxy such as Bungee and Waterfall. It allows players to queue for different Minigames on separate servers.

## Setup

You must have Bungeecord enabled in the spigot.yml file and online-mode enabled for this to work. Drop the BungeeCore.jar and MinigameService.jar files into the proxy plugins directory and add Manhunt, MinigameCore, and MinigameService to the backdoor servers. After starting the proxy and Bukkit servers, make any necessary changes to the config before shutting down and restarting both the proxy and the Bukkit server.

For the Bukkit plugins to communicate with the proxy, you must first start the proxy before starting the backend servers.

