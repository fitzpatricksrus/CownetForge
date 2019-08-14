package us.cownet.examplemod.thorhammer;

import us.cownet.examplemod.utilities.RendererHelper;
import net.minecraftforge.fml.common.SidedProxy;

public class ThorHammerGuiRenderer extends RendererHelper {
	@SidedProxy(clientSide = "us.cownet.examplemod.thorhammer.ThorHammerGuiRendererClientSide",
			serverSide = "us.cownet.examplemod.utilities.RendererHelper")
	public static RendererHelper proxy;
}
