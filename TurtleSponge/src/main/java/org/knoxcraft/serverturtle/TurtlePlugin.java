package org.knoxcraft.serverturtle;

import java.util.Collection;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Stack;

import org.knoxcraft.hooks.KCTUploadHook;
import org.knoxcraft.jetty.server.JettyServer;
import org.knoxcraft.turtle3d.KCTBlockTypes;
import org.knoxcraft.turtle3d.KCTCommand;
import org.knoxcraft.turtle3d.KCTJobQueue;
import org.knoxcraft.turtle3d.KCTScript;
import org.knoxcraft.turtle3d.KCTUndoScript;
import org.knoxcraft.turtle3d.TurtleDirection;
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
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent.Login;
import org.spongepowered.api.event.world.ChangeWorldWeatherEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.inject.Inject;

@Plugin(id = TurtlePlugin.ID, name = "TurtlePlugin", version = "0.2", description = "Knoxcraft Turtles Plugin for Minecraft")
public class TurtlePlugin {
	public static final String ID = "kct";
	private static final String PLAYER_NAME = "playerName";
	private static final String SCRIPT_NAME = "scriptName";
	private static final String NUM_UNDO = "numUndo";
	private JettyServer jettyServer;
	@Inject
	private Logger log;
	private ScriptManager scripts;
	private HashMap<String, Stack<KCTUndoScript>> undoBuffer; // PlayerName->buffer
	private KCTJobQueue jobQueue;
	
	@Inject
	private PluginContainer container;

	//////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor.
	 */
	public TurtlePlugin() {
		scripts = new ScriptManager();
		undoBuffer = new HashMap<String, Stack<KCTUndoScript>>();
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
	public void onServerStart(GameStartedServerEvent event) {
		// Hey! The server has started!
		// Try instantiating your logger in here.
		// (There's a guide for that)
		log.info("Registering Knoxcraft Turtles plugin");

		// Canary.hooks().registerListener(this, this);
		// TODO: these seem to have no effect; figure out why!
		// boolean b1=Canary.getServer().consoleCommand("weather clear
		// 1000000");
		// boolean b2=Canary.getServer().consoleCommand("gamerule
		// doDaylightCycle false");
		// logger.trace(String.format("Did weather work? %s did daylight work?
		// %s", b1, b2));

		try {
			jettyServer = new JettyServer();
			// jettyServer.enable();
		} catch (Exception e) {
			if (jettyServer != null) {
				// jettyServer.shutdown();
			}
			log.error("Cannot initialize TurtlePlugin: JettyServer failed to start", e);
		}

		// httpServer=new HttpUploadServer();
		// httpServer.enable(getLogman());
		log.info("Enabling " + container.getName() + " Version " + container.getVersion());
		log.info("Authored by " + container.getAuthors());
		// Canary.commands().registerCommands(this, this, false);

		// TODO fix this method
		// lookupFromDB();

		// TestClass t=new TestClass();
		// t.method();

		log.info("just tried to call a method");

		// set up commands
		setupCommands();
		
		jobQueue = new KCTJobQueue(Sponge.getScheduler().createSyncExecutor(this), Sponge.getScheduler().createAsyncExecutor(this));
	}

	// TODO LOG STATEMENTS to show commands work, and check if arguments make
	// sense
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

						/*
						 * TODO: uncomment once we're done testing if
						 * (script==null) { log.warn(String.
						 * format("player %s cannot find script %s", playerName,
						 * scriptName)); src.sendMessage(Text.of(String.
						 * format("%s, you have no script named %s", playerName,
						 * scriptName))); return CommandResult.success(); }
						 */

						// make the fake square
						script = makeFakeSquare();

						SpongeTurtle turtle = new SpongeTurtle(log);

						// location of turtle = location of player
						if (src instanceof Player) {
							Player player = (Player) src;
							Location<World> loc = player.getLocation();
							Vector3i pos = loc.getBlockPosition();
							turtle.setLoc(pos);
							// get world from setter in spongeTurtle
							World w = player.getWorld();
							// rotation in degrees = direction
							Vector3d headRotation = player.getHeadRotation();
							Vector3d rotation = player.getRotation();
							log.info("headRotation=" + headRotation);
							log.info("rotation=" + rotation);
							TurtleDirection d = getTurtleDirection(rotation);
							log.info("pos= " + pos);
							turtle.setWorld(w);
							turtle.setTurtleDirection(d);
							turtle.setScript(script);
							
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

						String senderName = src.getName().toLowerCase();

						// sender has not executed any scripts
						if (!undoBuffer.containsKey(senderName)) {
							src.sendMessage(Text.of("You have not executed any scripts to undo!"));
						} else { // buffer exists
							// get buffer
							Stack<KCTUndoScript> undoUserScripts = undoBuffer.get(senderName);

							if (undoUserScripts == null) { // buffer empty
								src.sendMessage(Text.of("There were no scripts invoked by the player!"));
							} else {
								for (int i = 0; i < numUndo; i++) {
									try {
										KCTUndoScript undoScript = undoUserScripts.pop();
										undoScript.executeUndo();
									} catch (EmptyStackException e) {
										src.sendMessage(Text.of("There are no more scripts to undo!"));
										break;
									}	
								}
							}
						}

						return CommandResult.success();
					}

				}).build();
		Sponge.getCommandManager().register(this, undo, "undo", "un");
	}

	private KCTScript makeFakeSquare() {
		KCTScript script = new KCTScript("testscript");
		// TODO flesh this out to test a number of other commands

		script.addCommand(KCTCommand.setBlock(KCTBlockTypes.REDSTONE_BLOCK));
		script.addCommand(KCTCommand.forward(70));
		script.addCommand(KCTCommand.turnLeft(90));
		script.addCommand(KCTCommand.forward(70));
		script.addCommand(KCTCommand.turnLeft(90));
		script.addCommand(KCTCommand.forward(70));
		script.addCommand(KCTCommand.turnLeft(90));
		script.addCommand(KCTCommand.forward(70));
		script.addCommand(KCTCommand.turnLeft(90));
		
//		script.addCommand(KCTCommand.setBlock(KCTBlockTypes.AIR));
		script.addCommand(KCTCommand.up(1));
		
		script.addCommand(KCTCommand.setBlock(KCTBlockTypes.POWERED_RAIL));
		script.addCommand(KCTCommand.forward(70));
		script.addCommand(KCTCommand.turnLeft(90));
		script.addCommand(KCTCommand.forward(70));
		script.addCommand(KCTCommand.turnLeft(90));
		script.addCommand(KCTCommand.forward(70));
		script.addCommand(KCTCommand.turnLeft(90));
		script.addCommand(KCTCommand.forward(70));
		script.addCommand(KCTCommand.setBlock(KCTBlockTypes.BLUE_WOOL));
		
		for (int i = 0; i < 125; i++) {
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

	private TurtleDirection getTurtleDirection(Vector3d direction) {

		double d = direction.getY() / 360 * 8;
		int x = (int) Math.round(d);
		
		while (x < 0) {
			x += 8;
		}

		if (x == 0 || x == 8) {
			return TurtleDirection.NORTH;

		} else if (x == 1) {
			return TurtleDirection.NORTHEAST;

		} else if (x == 2) {
			return TurtleDirection.EAST;

		} else if (x == 3) {
			return TurtleDirection.SOUTHEAST;

		} else if (x == 4) {
			return TurtleDirection.SOUTH;

		} else if (x == 5) {
			return TurtleDirection.SOUTHWEST;

		} else if (x == 6) {
			return TurtleDirection.WEST;

		} else if (x == 7) {
			return TurtleDirection.NORTHWEST;

		} else {
			throw new RuntimeException("Direction invalid = " + direction);
		}
	}

	/**
	 * Load the latest version of each script from the DB for each player on
	 * this world TODO Check how Canary handles worlds; do we have only one XML
	 * file of scripts for worlds and should we include the world name or world
	 * ID with the script?
	 */
	private void lookupFromDB() {
		// FIXME translate to Sponge
		// KCTScriptAccess data=new KCTScriptAccess();
		// List<DataAccess> results=new LinkedList<DataAccess>();
		// Map<String,KCTScriptAccess> mostRecentScripts=new
		// HashMap<String,KCTScriptAccess>();
		//
		// try {
		// Map<String,Object> filters=new HashMap<String,Object>();
		// Database.get().loadAll(data, results, filters);
		// for (DataAccess d : results) {
		// KCTScriptAccess scriptAccess=(KCTScriptAccess)d;
		// // Figure out the most recent script for each player-scriptname combo
		// String key=scriptAccess.playerName+"-"+scriptAccess.scriptName;
		// if (!mostRecentScripts.containsKey(key)) {
		// mostRecentScripts.put(key, scriptAccess);
		// } else {
		// if (scriptAccess.timestamp > mostRecentScripts.get(key).timestamp) {
		// mostRecentScripts.put(key,scriptAccess);
		// }
		// }
		// log.trace(String.format("from DB: player %s has script %s at time
		// %d%n",
		// scriptAccess.playerName, scriptAccess.scriptName,
		// scriptAccess.timestamp));
		// }
		// TurtleCompiler turtleCompiler=new TurtleCompiler();
		// for (KCTScriptAccess scriptAccess : mostRecentScripts.values()) {
		// try {
		// KCTScript script=turtleCompiler.parseFromJson(scriptAccess.json);
		// script.setLanguage(scriptAccess.language);
		// script.setScriptName(scriptAccess.scriptName);
		// script.setSourceCode(scriptAccess.source);
		// script.setPlayerName(scriptAccess.playerName);
		//
		// scripts.putScript(scriptAccess.playerName, script);
		// log.info(String.format("Loaded script %s for player %s",
		// scriptAccess.scriptName, scriptAccess.playerName));
		// } catch (TurtleException e){
		// log.error("Internal Server error", e);
		// }
		// }
		// } catch (DatabaseReadException e) {
		// log.error("cannot read DB", e);
		// }
	}

	// Listeners

	/**
	 * @param loginEvent
	 */
	@Listener
	public void onLogin(Login loginEvent) {
		// TODO: verify that this hook related to logging in
		// TODO prevent breaking blocks, by figuring out equivalent of
		// setCanBuild(false);
		log.debug(String.format("player " + loginEvent.getTargetUser().getName()));
	}

	/**
	 * TODO: Fix this hook. This doesn't seem to get called. I would like to
	 * shut rain off every time it starts raining.
	 * 
	 * @param hook
	 */
	@Listener
	public void onWeatherChange(ChangeWorldWeatherEvent weatherChange) {
		// TODO turn off weather
		log.info(String.format("Weather listener called"));
	}

	/**
	 * Listener called when scripts are uploaded to the server
	 * 
	 * @param event
	 */
	@Listener
	public void uploadJSON(KCTUploadHook event) {
		log.trace("Hook called!");
		// add scripts to manager and db
		Collection<KCTScript> list = event.getScripts();
		for (KCTScript script : list) {
			scripts.putScript(event.getPlayerName().toLowerCase(), script);

			// FIXME translate to Sponge
			// // This will create the table if it doesn't exist
			// // and then insert data for the script into a new row
			// KCTScriptAccess data=new KCTScriptAccess();
			// data.json=script.toJSONString();
			// data.source=script.getSourceCode();
			// data.playerName=event.getPlayerName();
			// data.scriptName=script.getScriptName();
			// data.language=script.getLanguage();
			// try {
			// Database.get().insert(data);
			// } catch (DatabaseWriteException e) {
			// // TODO how to log the full stack trace?
			// log.error(e.toString());
			// }
		}
	}
}