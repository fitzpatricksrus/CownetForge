package us.cownet.examplemod;

import us.cownet.examplemod.batfight.BatFight;
import us.cownet.examplemod.expodingsheep.ExplodingAnimals;
import us.cownet.examplemod.init.ModBlocks;
import us.cownet.examplemod.init.ModItems;
import us.cownet.examplemod.init.ModRecipes;
import us.cownet.examplemod.utilities.Logging;
import us.cownet.examplemod.utilities.eventsnoopers.EventSnooper;
import us.cownet.examplemod.utilities.hackfmlevents.HackFMLEventBus;
import us.cownet.examplemodtests.testUtilities.RunTestsCommand;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;

@Mod(modid = ExampleMod.MODID, name = ExampleMod.MODNAME, version = ExampleMod.VERSION, acceptedMinecraftVersions = ExampleMod.ACCEPTED_MINECRAFT_VERSIONS)
public class ExampleMod {
	public static final String MODID = "cownetmod";
	public static final String MODNAME = "CowNet Mod";
	public static final String[] MODCOMMANDALIASES = {"cn", "cownet"};
	public static final String VERSION = "1.0";
	public static final String ACCEPTED_MINECRAFT_VERSIONS = "[1.12]";
	@Mod.Instance
	public static ExampleMod instance;
	/**
	 * Instance of ExplodingAnimals
	 */
	private BatFight batFight;
	private ExplodingAnimals explodingAnimals;
	private RunTestsCommand runTestCommand;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Logging.logger = event.getModLog();
		Logging.logTrace(MODID + ": preInit");

		ModItems.init();
		ModBlocks.init();
		ModRecipes.init();
		EventSnooper.init();

		batFight = new BatFight();
		explodingAnimals = new ExplodingAnimals();
		runTestCommand = new RunTestsCommand();

		// notify listeners last so the rest of initialization/construction is done before they are called.
		HackFMLEventBus.FMLEventBus.publish(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		Logging.logTrace(MODID + ": init");
		HackFMLEventBus.FMLEventBus.publish(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		Logging.logTrace(MODID + ": postInit");
		HackFMLEventBus.FMLEventBus.publish(event);
	}

	@EventHandler
	public void fmlLifeCycle(FMLServerAboutToStartEvent event) {
		Logging.logTrace("Server about to start");
		HackFMLEventBus.FMLEventBus.publish(event);
	}

	@EventHandler
	public void fmlLifeCycle(FMLServerStartingEvent event) {
		Logging.logTrace("Server starting");
		HackFMLEventBus.FMLEventBus.publish(event);
	}

	@EventHandler
	public void fmlLifeCycle(FMLServerStartedEvent event) {
		Logging.logTrace("Server started");
		HackFMLEventBus.FMLEventBus.publish(event);
	}

	@EventHandler
	public void fmlLifeCycle(FMLServerStoppingEvent event) {
		Logging.logTrace("Server stopping");
		HackFMLEventBus.FMLEventBus.publish(event);
	}

	@EventHandler
	public void fmlLifeCycle(FMLServerStoppedEvent event) {
		Logging.logTrace("Server stopped");
		HackFMLEventBus.FMLEventBus.publish(event);
	}
}
