package org.knoxcraft.serverturtle;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.knoxcraft.database.DataAccess;
import org.knoxcraft.database.Database;
import org.knoxcraft.database.exceptions.DatabaseReadException;
import org.knoxcraft.database.exceptions.DatabaseWriteException;
import org.knoxcraft.database.tables.KCTScriptAccess;
import org.knoxcraft.hooks.KCTUploadHook;
import org.knoxcraft.jetty.server.JettyServer;
import org.knoxcraft.turtle3d.KCTBlockTypes;
import org.knoxcraft.turtle3d.KCTCommand;
import org.knoxcraft.turtle3d.KCTJobQueue;
import org.knoxcraft.turtle3d.KCTScript;
import org.knoxcraft.turtle3d.TurtleCompiler;
import org.knoxcraft.turtle3d.TurtleDirection;
import org.knoxcraft.turtle3d.TurtleException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent.Join;
import org.spongepowered.api.event.network.ClientConnectionEvent.Login;
import org.spongepowered.api.event.world.ChangeWorldWeatherEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.inject.Inject;

@Plugin(id = TurtlePlugin.ID, name = "TurtlePlugin", version = "0.2", description = "Knoxcraft Turtles Plugin for Minecraft", authors = {
		"kakoijohn", "mrmoeee", "stringnotfound", "emhastings", "ppypp", "jspacco" })
public class TurtlePlugin {

	public static final String ID = "kct";
	private static final String PLAYER_NAME = "playerName";
	private static final String SCRIPT_NAME = "scriptName";
	private static final String NUM_UNDO = "numUndo";
	private JettyServer jettyServer;
	@Inject
	private Logger log;
	private ScriptManager scripts;
	private KCTJobQueue jobQueue;
	private World world;

	private SpongeExecutorService minecraftSyncExecutor;

	@Inject
	private PluginContainer container;
	
	private int jobNum = 0;

	//////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor.
	 */
	public TurtlePlugin() {
		scripts = new ScriptManager();
	}

	/**
	 * Called when plugin is disabled.
	 */
	@Listener
	public void onServerStop(GameStoppedServerEvent event) {
		if (jettyServer != null) {
			jettyServer.shutdown();
		}

		jobQueue.shutdownExecutor();
	}
	
	@Listener
	public void gameConstructionEvent(GameConstructionEvent event) {
	    //first event in the plugin lifecycle
	    KCServerProperties kcProperties = new KCServerProperties();
	    //If the server.properties file does not exist or is not in the correct format,
	    //we must change the file to the correct format.
	    int result = kcProperties.loadServerProperties();
	    if (result == 1)
	        log.info("Correct server.properties file loaded.");
	    else if (result == 0) {
	        log.info("Incorrect server.properties file. Replacing with new file.");
	        kcProperties.createPropertiesFile();
	    } else if (result == -1) {
	        log.info("No server.properties file found. Creating new one.");
	        kcProperties.createPropertiesFile();
	    }
	}

	@Listener
	public void onServerStart(GameStartedServerEvent event) {
		// Hey! The server has started!
		log.info("Registering Knoxcraft Turtles plugin");

		try {
			jettyServer = new JettyServer();
			jettyServer.startup();
		} catch (Exception e) {
			if (jettyServer != null) {
				jettyServer.shutdown();
			}
			log.error("Cannot initialize TurtlePlugin: JettyServer failed to start", e);
		}

		log.info("Enabling " + container.getName() + " Version " + container.getVersion());
		log.info("Authored by " + container.getAuthors());

		// Look up previously submitted scripts from the DB
		lookupFromDB();

		// set up commands
		setupCommands();
	}
	
	@Listener
	public void onWorldLoad(LoadWorldEvent event) {
		if (event.getTargetWorld().getDimension().getType() == DimensionTypes.OVERWORLD) {
			world = event.getTargetWorld();

			// CLEAR SKIES
			world.setWeather(Weathers.CLEAR);

			// BRIGHT SUNNY DAY (12000 = sun set)
			world.getProperties().setWorldTime(0);
			log.info(String.format("currenttime " + world.getProperties().getWorldTime()));

			// TIME CHANGE SCHEDULER > NOTE MAYBE PUT IN ONSERVERSTART
			minecraftSyncExecutor = Sponge.getScheduler().createSyncExecutor(this);
			minecraftSyncExecutor.scheduleWithFixedDelay(new Runnable() {
				public void run() {
					world.getProperties().setWorldTime(0);
					log.info(String.format("timechange" + world.getProperties().getWorldTime()));
				}
				// change minecraftWorld time every 10 minutes.
			}, 0, 10, TimeUnit.MINUTES);
			
			
			//SETUP JOBQUEUE FOR TURTLE SCRIPT EXECUTOR
			jobQueue = new KCTJobQueue(Sponge.getScheduler().createSyncExecutor(this),
	                Sponge.getScheduler().createAsyncExecutor(this), log, world);
		}
	}
	

	private void setupCommands() {
		// List all the scripts
		CommandSpec listScripts = CommandSpec.builder().description(Text.of("List Knoxcraft Turtle Scripts"))
				.permission("").arguments(GenericArguments.optional(GenericArguments.string(Text.of(PLAYER_NAME))))
				.executor(new CommandExecutor() {
					@Override
					public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
						log.debug(String.format("name of sender is: %s", src.getName().toLowerCase()));
						src.sendMessage(Text.of(
								String.format("%s is listing turtle scripts (programs)", src.getName().toLowerCase())));

						Optional<String> optName = args.<String>getOne(PLAYER_NAME);
						if (optName.isPresent()) {
							String playerName = optName.get();
							if (playerName.equalsIgnoreCase("all")) {
								// List all scripts for all players
								Map<String, Map<String, KCTScript>> allScriptMap = scripts.getAllScripts();
								for (Entry<String, Map<String, KCTScript>> entry : allScriptMap.entrySet()) {
									playerName = entry.getKey();
									for (Entry<String, KCTScript> entry2 : entry.getValue().entrySet()) {
										src.sendMessage(Text.of(
												String.format("%s has the script %s", playerName, entry2.getKey())));
									}
								}
							} else {
								// List only scripts for the given player
								Map<String, KCTScript> map = scripts.getAllScriptsForPlayer(playerName);
								for (Entry<String, KCTScript> entry : map.entrySet()) {
									src.sendMessage(
											Text.of(String.format("%s has script %s"), playerName, entry.getKey()));
								}
							}
						} else {
							Map<String, KCTScript> map = scripts.getAllScriptsForPlayer(src.getName().toLowerCase());
							if (map == null) {
								// hacky way to use a case-insensitive key in a
								// map
								// in the future, be sure to make all keys
								// lower-case
								map = scripts.getAllScriptsForPlayer(src.getName());
							}
							if (map == null) {
								src.sendMessage(
										Text.of(String.format("We cannot find any scripts for %s", src.getName())));
							}
							for (Entry<String, KCTScript> entry : map.entrySet()) {
								log.debug(String.format("%s => %s", entry.getKey(), entry.getValue().getLanguage()));
								src.sendMessage(Text
										.of(String.format("%s => %s", entry.getKey(), entry.getValue().getLanguage())));
							}
						}

						return CommandResult.success();
					}
				}).build();
		Sponge.getCommandManager().register(this, listScripts, "scripts", "ls");

		// Invoke a script
		CommandSpec invokeScript = CommandSpec.builder().description(Text.of("Invoke a Knoxcraft Turtle Script"))
				.permission("")
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of(SCRIPT_NAME))),
						GenericArguments.optional(GenericArguments.string(Text.of(PLAYER_NAME))))
				.executor(new CommandExecutor() {
					@Override
					public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
						Optional<String> optScriptName = args.getOne(SCRIPT_NAME);
						if (!optScriptName.isPresent()) {
							src.sendMessage(Text.of("No script name provided! You must invoke a script by name"));
						}

						String scriptName = optScriptName.get();
						String playerName = src.getName().toLowerCase();

						log.info("playername ==" + playerName);
						Optional<String> optPlayerName = args.getOne(PLAYER_NAME);
						if (optPlayerName.isPresent()) {
							playerName = optPlayerName.get();
						}

						log.debug(String.format("%s invokes script %s from player %s", src.getName(), scriptName,
								playerName));
						///////
						// log.info("scripts == null" + (scripts == null));
						// log.info("playerName ==" + playerName);
						// log.info("scriptName== " + scriptName);
						KCTScript script = scripts.getScript(playerName, scriptName);

						if (script == null) {
							log.warn(String.format("player %s cannot find script %s", playerName, scriptName));
							src.sendMessage(
									Text.of(String.format("%s, you have no script named %s", playerName, scriptName)));
							// FIXME: remove this fake square
							script = makeFakeSquare();
							// return CommandResult.success();
						}

						SpongeTurtle turtle = new SpongeTurtle(log);

						// location of turtle = location of player
						if (src instanceof Player) {
							Player player = (Player) src;
							Location<World> loc = player.getLocation();
							Vector3i pos = loc.getBlockPosition();
							// get world from setter in spongeTurtle
							World w = player.getWorld();
							// rotation in degrees = direction
							Vector3d headRotation = player.getHeadRotation();
							Vector3d rotation = player.getRotation();

							log.info("headRotation=" + headRotation);
							log.info("rotation=" + rotation);
							TurtleDirection d = TurtleDirection.getTurtleDirection(rotation);
							log.info("pos= " + pos);

							turtle.setSenderName(playerName);
							turtle.setLoc(pos);
							turtle.setWorld(w);
							turtle.setWorkChunkSize(500);
							turtle.setJobNum(jobNum++);
							turtle.setTurtleDirection(d);
							turtle.setScript(script);
							turtle.executeScript();
							
							jobQueue.add(turtle);
						}
						return CommandResult.success();
					}
				}).build();
		Sponge.getCommandManager().register(this, invokeScript, "invoke", "in");

		CommandSpec undo = CommandSpec.builder().description(Text.of("Undo the previous script")).permission("")
				.arguments(GenericArguments.optional(GenericArguments.integer(Text.of(NUM_UNDO))))
				.executor(new CommandExecutor() {
					@Override
					public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
						Optional<Integer> optNumUndo = args.getOne(NUM_UNDO);
						int numUndo = 1;
						if (optNumUndo.isPresent()) {
							numUndo = optNumUndo.get();
						}
						log.debug("Undo invoked!");

						jobQueue.undoScript(src, numUndo);

						return CommandResult.success();
					}

				}).build();
		Sponge.getCommandManager().register(this, undo, "undo", "un");
	}

	public static KCTScript makeFakeSquare() {
		KCTScript script = new KCTScript("testscript");
		// TODO flesh this out to test a number of other commands
		script.addCommand(KCTCommand.setBlock(KCTBlockTypes.BLUE_WOOL));

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 25; j++) {
				script.addCommand(KCTCommand.forward(50));
				script.addCommand(KCTCommand.turnLeft(90));
				script.addCommand(KCTCommand.forward(1));
				script.addCommand(KCTCommand.turnLeft(90));
				script.addCommand(KCTCommand.forward(50));
				script.addCommand(KCTCommand.turnRight(90));
				script.addCommand(KCTCommand.forward(1));
				script.addCommand(KCTCommand.turnRight(90));
			}

			script.addCommand(KCTCommand.up(1));

			for (int j = 0; j < 25; j++) {
				script.addCommand(KCTCommand.forward(50));
				script.addCommand(KCTCommand.turnRight(90));
				script.addCommand(KCTCommand.forward(1));
				script.addCommand(KCTCommand.turnRight(90));
				script.addCommand(KCTCommand.forward(50));
				script.addCommand(KCTCommand.turnLeft(90));
				script.addCommand(KCTCommand.forward(1));
				script.addCommand(KCTCommand.turnLeft(90));
			}

			script.addCommand(KCTCommand.up(1));
		}

		return script;
	}

	/**
	 * Load the latest version of each script from the DB for each player on
	 * this world
	 * 
	 * TODO low priority: Check how Sponge handles worlds; do we have only one
	 * XML file of scripts for worlds and should we include the world name or
	 * world ID with the script?
	 */
	private void lookupFromDB() {
		// FIXME translate to Sponge
		KCTScriptAccess data = new KCTScriptAccess();
		List<DataAccess> results = new LinkedList<DataAccess>();
		Map<String, KCTScriptAccess> mostRecentScripts = new HashMap<String, KCTScriptAccess>();

		try {
			Map<String, Object> filters = new HashMap<String, Object>();
			Database.get().loadAll(data, results, filters);
			for (DataAccess d : results) {
				KCTScriptAccess scriptAccess = (KCTScriptAccess) d;
				// Figure out the most recent script for each player-scriptname
				// combo
				String key = scriptAccess.playerName + "-" + scriptAccess.scriptName;
				if (!mostRecentScripts.containsKey(key)) {
					mostRecentScripts.put(key, scriptAccess);
				} else {
					if (scriptAccess.timestamp > mostRecentScripts.get(key).timestamp) {
						mostRecentScripts.put(key, scriptAccess);
					}
				}
				log.trace(String.format("from DB: player %s has script %s at time %d%n", scriptAccess.playerName,
						scriptAccess.scriptName, scriptAccess.timestamp));
			}
			TurtleCompiler turtleCompiler = new TurtleCompiler();
			for (KCTScriptAccess scriptAccess : mostRecentScripts.values()) {
				try {
					KCTScript script = turtleCompiler.parseFromJson(scriptAccess.json);
					script.setLanguage(scriptAccess.language);
					script.setScriptName(scriptAccess.scriptName);
					script.setSourceCode(scriptAccess.source);
					script.setPlayerName(scriptAccess.playerName);

					scripts.putScript(scriptAccess.playerName, script);
					log.info(String.format("Loaded script %s for player %s", scriptAccess.scriptName,
							scriptAccess.playerName));
				} catch (TurtleException e) {
					log.error("Internal Server error", e);
				}
			}
		} catch (DatabaseReadException e) {
			log.error("cannot read DB", e);
		}
	}

	// Listeners

	/**
	 * @param loginEvent
	 */
	@Listener
	public void onJoin(Join joinEvent) {
		// TODO: verify that this hook related to logging in
		// TODO prevent breaking blocks, by figuring out equivalent of
		// setCanBuild(false);
	    log.info("Logging in; checking to see if debug level log works");
		log.debug(String.format("player " + joinEvent.getTargetEntity().getName()));
	}

	/**
	 * TODO: Fix this hook. This doesn't seem to get called. I would like to
	 * shut rain off every time it starts raining.
	 * 
	 * @param hook
	 */
	@Listener
	public void onWeatherChange(ChangeWorldWeatherEvent worldWeatherListener) {
		// TODO turn off weather(weather set clear onWeatherChange
		Weather curWeather;
		worldWeatherListener.setWeather(Weathers.CLEAR);
		curWeather = worldWeatherListener.getWeather();
		log.debug(String.format("Weather listener called"));
		log.debug(String.format("current weather = %s ", curWeather.getName()));
	}

	/**
	 * Listener called when scripts are uploaded to the server
	 * 
	 * @param event
	 */
	@Listener
	public void uploadJSON(KCTUploadHook event) {
		log.debug("Hook called!");
		// add scripts to manager and db
		Collection<KCTScript> list = event.getScripts();
		for (KCTScript script : list) {
			scripts.putScript(event.getPlayerName().toLowerCase(), script);

			// This will create the table if it doesn't exist
			// and then insert data for the script into a new row
			KCTScriptAccess data = new KCTScriptAccess();
			data.json = script.toJSONString();
			data.source = script.getSourceCode();
			data.playerName = event.getPlayerName();
			data.scriptName = script.getScriptName();
			data.language = script.getLanguage();
			try {
				Database.get().insert(data);
			} catch (DatabaseWriteException e) {
				// TODO how to log the full stack trace?
				log.error(e.toString());
			}
		}
	}
}