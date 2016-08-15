package org.knoxcraft.serverturtle;

import static org.knoxcraft.database.DatabaseConfiguration.convert;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.knoxcraft.database.DataAccess;
import org.knoxcraft.database.Database;
import org.knoxcraft.database.exceptions.DatabaseReadException;
import org.knoxcraft.database.exceptions.DatabaseWriteException;
import org.knoxcraft.database.tables.KCTScriptAccess;
import org.knoxcraft.hooks.KCTUploadHook;
import org.knoxcraft.jetty.server.JettyServer;
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
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent.Join;
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

/**
 * Sponge Knoxcraft plugin to run when the Minecraft server starts up.
 * @author mrmoeee jspacco kakoijohn 
 *
 */
@Plugin(id = TurtlePlugin.ID, name = "TurtlePlugin", version = "0.2", description = "Knoxcraft Turtles Plugin for Minecraft", authors = {
		"kakoijohn", "mrmoeee", "stringnotfound", "emhastings", "ppypp", "jspacco" })
public class TurtlePlugin {

    private static final String SLEEP_TIME = "knoxcraft.sleepTime";
    private static final String WORK_CHUNK_SIZE = "knoxcraft.workChunkSize";
    private static final String MIN_BUILD_HEIGHT = "knoxcraft.minBuildHeight";
    private static final String MAX_BUILD_HEIGHT = "knoxcraft.maxBuildHeight";
    private static final String MAX_JOB_SIZE = "knoxcraft.maxJobSize";
    public static final String ID = "knoxcraft";
	private static final String PLAYER_NAME = "playerName";
	private static final String SCRIPT_NAME = "scriptName";
	private static final String NUM_UNDO = "numUndo";
	private static final String ARE_YOU_SURE = "no";
	private JettyServer jettyServer;
	@Inject
	private Logger log;
	private ScriptManager scripts;
	private KCTJobQueue jobQueue;
	private World world;

	private SpongeExecutorService minecraftSyncExecutor;

	@Inject
	private PluginContainer container;
	
    // The configuration folder for this plugin
    @Inject
    @ConfigDir(sharedRoot = true)
    private File configDir;

    // The config manager for the knoxcraft file
    private ConfigurationLoader<CommentedConfigurationNode> knoxcraftConfigLoader;

    // The in-memory version of the knoxcraft configuration file
    private CommentedConfigurationNode knoxcraftConfig;

    // configured in config/knoxcraft.conf
	private long sleepTime;
	private int workChunkSize;
	private int minBuildHeight = 3;
	private int maxBuildHeight = 256;
	private int maxJobSize = 1000;
	
	private int jobNum = 0;

	/**
	 * Constructor.
	 */
	public TurtlePlugin() {
		scripts = new ScriptManager();
	}

	/**
	 * Listener for when the server stop event is called.
	 * We must safely shutdown the Jetty server, the Minecraft Executor, and the WorkThread.
	 */
	@Listener
	public void onServerStop(GameStoppedServerEvent event) {
		if (jettyServer != null) {
			jettyServer.shutdown();
		}

		jobQueue.shutdown();
	}
	
	/**
	 * Game Construction Event. Checks to see if the proper server.properties file exists.
	 * If it isn't correct, we must replace it with our own configurations.
	 * @param event
	 */
	@Listener
	public void gameConstructionEvent(GameConstructionEvent event) {
	    //first event in the plugin lifecycle
	    KCServerProperties kcProperties = new KCServerProperties();
	    //If the server.properties file does not exist or is not in the correct format,
	    //we must change the file to the correct format.
	    int result = kcProperties.loadServerProperties();
	    if (result == 1)
	        log.debug("Correct server.properties file loaded.");
	    else if (result == 0) {
	        log.debug("Incorrect server.properties file. Replacing with new file.");
	        kcProperties.createPropertiesFile();
	    } else if (result == -1) {
	        log.debug("No server.properties file found. Creating new one.");
	        kcProperties.createPropertiesFile();
	    }
	}

	/**
	 * On Server Start Event. We must start the Jetty server and set up the user commands.
	 * @param event
	 */
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
		
		// read configuration parameters
		try {
		    loadOrCreateConfigFile();
		} catch (IOException e) {
		    log.error("Unable to create or load knoxcraft config file!");
		    // TODO: set up a default
		}
		
		// configure the Database, once we have loaded a valid configuration file
		Database.configure(knoxcraftConfig);

		// Look up previously submitted scripts from the DB
		lookupFromDB();

		// set up commands
		setupCommands();
		
		// read config files and set up instance variables
		readConfigFile();
		
	}
	
	private void readConfigFile() {
	    // Read values out of config files and set instance variables.
	    this.workChunkSize=knoxcraftConfig.getNode(WORK_CHUNK_SIZE).getInt();
	    this.sleepTime=knoxcraftConfig.getNode(SLEEP_TIME).getInt();
	    this.minBuildHeight = knoxcraftConfig.getNode(MIN_BUILD_HEIGHT).getInt();
	    this.maxBuildHeight = knoxcraftConfig.getNode(MAX_BUILD_HEIGHT).getInt();
	    this.maxJobSize = knoxcraftConfig.getNode(MAX_JOB_SIZE).getInt();
	    
	    this.workChunkSize = 500;
	    this.sleepTime = 200;
	    this.minBuildHeight = 3;
	    this.maxBuildHeight = 256;
	    this.maxJobSize = -1;
	    
	    log.info(workChunkSize + "");
	    log.info(sleepTime + "");
	    log.info(minBuildHeight + "");
	    log.info(maxBuildHeight + "");
	    log.info(maxJobSize + "");
	}
	
	/**
	 * Load the configuration properties from config/knoxcraft.conf in HOCON format.
	 * 
	 * If no config file exists, create one with default values.
	 * 
	 * XXX NOTE: this method also configured the database, which is a bit sloppy.
	 * 
	 */
	private void loadOrCreateConfigFile() throws IOException
	{
	    // Check for config file config/knoxcraft.conf
	    File knoxcraftConfigFile = new File(this.configDir, "knoxcraft.conf");
        this.knoxcraftConfigLoader = 
                HoconConfigurationLoader.builder().setFile(knoxcraftConfigFile).build();
        
        // Create the folder if it does not exist
        if (!this.configDir.isDirectory()) {
            this.configDir.mkdirs();
        }

        // Create the config file if it does not exist
        if (!knoxcraftConfigFile.isFile()) {
            knoxcraftConfigFile.createNewFile();
        }
        
        // now load the knoxcraft config file
        this.knoxcraftConfig = this.knoxcraftConfigLoader.load();

        // ensure we have correct database defaults
        // will add any config values that are missing
        Database.configure(knoxcraftConfig);
        
        // set other default configuration settings using this syntax:
        addConfigSetting(WORK_CHUNK_SIZE, 500, "Number of blocks to build at a time. Larger values will lag and eventually crash the server. 500 seems to work.");
        addConfigSetting(SLEEP_TIME, 200, "Number of millis to wait between building chunks of blocks. Shorter values are more likely to lag and eventually crash the server. 200 seems to work.");
        addConfigSetting(MIN_BUILD_HEIGHT, 3, "Minimum build height for a flat world. This prevents structures being built underneath the ground and breaking through the bedrock. 3 seems to work.");
        addConfigSetting(MAX_BUILD_HEIGHT, 256, "Maximum build height allowed. 256 is the default for Minecraft build height.");
        addConfigSetting(MAX_JOB_SIZE, -1, "Maximum number of blocks allowed to be built by invoking a single script. If you do not want a limit, set this value to -1.");
        
        // now save the configuration file, in case we changed anything
        this.knoxcraftConfigLoader.save(this.knoxcraftConfig);
	}
	
	private void addConfigSetting(String path, Object value) {
	    addConfigSetting(path, value, null);
	}
	
	private void addConfigSetting(String path, Object value, String comment) {
	    CommentedConfigurationNode node=knoxcraftConfig.getNode(convert(path));
	    if (node.isVirtual()) {
	        node=node.setValue(value);
	        if (comment!=null){
	            node.setComment(comment);
	        }
	    }
	}
	
	/**
	 * On World Load Event. During this time, the weather of the world is set to clear,
	 * the time is reset to 0, and an event is scheduled to happen every 1/2 of a Minecraft day
	 * to reset the time so it will never become nighttime.
	 * The KCTJobQueue is also initialized in this step creating a Worker thread that waits on the
	 * consumer to put work on the queue.
	 * @param event
	 */
	@Listener
	public void onWorldLoad(LoadWorldEvent event) {
		if (event.getTargetWorld().getDimension().getType() == DimensionTypes.OVERWORLD) {
			world = event.getTargetWorld();

			// CLEAR SKIES
			world.setWeather(Weathers.CLEAR);

			// BRIGHT SUNNY DAY (12000 = sun set)
			world.getProperties().setWorldTime(0);
			log.debug(String.format("Currenttime: " + world.getProperties().getWorldTime()));

			// TIME CHANGE SCHEDULER 
			minecraftSyncExecutor = Sponge.getScheduler().createSyncExecutor(this);
			minecraftSyncExecutor.scheduleWithFixedDelay(new Runnable() {
				public void run() {
					world.getProperties().setWorldTime(0);
					log.debug(String.format("Timechange: " + world.getProperties().getWorldTime()));
				}
				// change minecraftWorld time every 10 minutes
			}, 0, 10, TimeUnit.MINUTES);
			
			//SETUP JOBQUEUE FOR TURTLE SCRIPT EXECUTOR
			jobQueue = new KCTJobQueue(minecraftSyncExecutor, log, world, sleepTime, minBuildHeight, maxBuildHeight);
		}
	}

	/**
	 * All of the Commands available in game for the players. 
	 * List of all commands and what they do:
	 *   - /scripts /ls
	 *     lists all of the scripts available to the player to invoke.
	 *   - /invoke /in [script name] (optional [player name])
	 *     Invokes a turtle script. Creates a new turtle, executes the script, and adds the
	 *     work to the work queue.
	 *   - /undo /un (optional [number to undo])
	 *     Undoes the previous script or the last [x] number of scripts if specified.
	 *   - /cancel /cn
	 *     Cancels the currently queued work for the player that called the command.
	 */
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
							return CommandResult.success();
						}

						String scriptName = optScriptName.get();
						String playerName = src.getName().toLowerCase();

						log.debug("playername ==" + playerName);
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
							return CommandResult.success();
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

							log.debug("headRotation=" + headRotation);
							log.debug("rotation=" + rotation);
							TurtleDirection d = TurtleDirection.getTurtleDirection(rotation);
							log.debug("pos= " + pos);

							turtle.setSenderName(playerName);
							turtle.setLoc(pos);
							turtle.setWorld(w);
							turtle.setWorkChunkSize(workChunkSize);
							turtle.setJobNum(jobNum++);
							turtle.setTurtleDirection(d);
							turtle.setScript(script);
							
							turtle.executeScript();
							
							if (maxJobSize == -1 || turtle.getJobSize() < maxJobSize) {
							    jobQueue.add(turtle);
	                            src.sendMessage(Text.of("Building " + script.getScriptName() + "!"));
	                            log.debug("Job Size: " + turtle.getJobSize());
							} else {
							    src.sendMessage(Text.of("Your script is too big!"));
							    src.sendMessage(Text.of("Max block size: " + maxJobSize + ", User script size: " + turtle.getJobSize()));
							}
							
						}
						return CommandResult.success();
					}
				}).build();
		Sponge.getCommandManager().register(this, invokeScript, "invoke", "in");

		CommandSpec undo = CommandSpec.builder().description(Text.of("Undo the previous script"))
		        .permission("")
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
		
	    CommandSpec cancel = CommandSpec.builder().description(Text.of("Cancel currently queued work"))
	            .permission("")
	            .executor(new CommandExecutor() {
	                 @Override
	                 public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
	                     log.debug("Cancel invoked!");

	                     jobQueue.cancelScript(src);

	                     return CommandResult.success();
	                 }

	             }).build();
	    Sponge.getCommandManager().register(this, cancel, "cancel", "cn");
	    
	    // requires op permission
        CommandSpec killAll = CommandSpec.builder().description(Text.of("Kill all queued work"))
                .permission("minecraft.command.op")
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of(ARE_YOU_SURE))))
                .executor(new CommandExecutor() {
                    @Override
                    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
                        Optional<String> optAreYouSure = args.getOne(ARE_YOU_SURE);

                        if (optAreYouSure.isPresent() && optAreYouSure.get().equals("yes")) {
                            log.warn("Killing all invoked scripts, and emptying the undo stack.");
                            src.sendMessage(Text.of("Clearing the entire build queue, and the undo queue!"));
                            jobQueue.killAll();
                        } else {
                            src.sendMessage(Text.of("You must say \"/killall yes\" in order to confirm clearing the entire queue!"));
                            src.sendMessage(Text.of("Once you have cleared the queue, you cannot take this back!"));
                        }
                        
                        return CommandResult.success();
                    }

                }).build();
        Sponge.getCommandManager().register(this, killAll, "killAll", "killall");
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

	@Listener
	public void onSendCommand(SendCommandEvent event) {
	    log.info(String.format("Command event listener: %s %s", event.getCommand(), event.getArguments()));
	}
	
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
	 * Weather Change Event Listener
	 * Changes weather to clear any time the weather change event is called.
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
	 * Block Change Event Listener
	 * Stop any non-op player from breaking blocks in the world.
	 * @param event
	 */
	@Listener
	public void blockChangeEvent(ChangeBlockEvent event) {
	    if (event.getCause().root() instanceof Player) {
	        Player player = (Player) event.getCause().root();
//	        log.info("A player attempted to change a block.");
	        
	        if (!player.hasPermission("minecraft.command.op")) {
	            //if the player does not have the proper op permission, cancel the block break event. 
	            event.setCancelled(true);
	        }
	    }
	}

	/**
	 * Listener called when scripts are uploaded to the server
	 * 
	 * @param event
	 */
	@Listener
	public void uploadJSON(KCTUploadHook event) {
		log.debug("KCTUploadHook called!");
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