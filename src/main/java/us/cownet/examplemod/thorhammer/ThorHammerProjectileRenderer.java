package us.cownet.examplemod.thorhammer;

import us.cownet.examplemod.utilities.RendererHelper;
import net.minecraftforge.fml.common.SidedProxy;

public class ThorHammerProjectileRenderer extends RendererHelper {
	@SidedProxy(clientSide = "us.cownet.examplemod.thorhammer.ThorHammerProjectileRendererClientSide",
			serverSide = "us.cownet.examplemod.utilities.RendererHelper")
	public static RendererHelper proxy;
}
