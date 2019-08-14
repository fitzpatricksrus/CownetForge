package us.cownet.examplemod.emp;

import us.cownet.examplemod.utilities.RendererHelper;
import net.minecraftforge.fml.common.SidedProxy;

public class EMPRenderer {
	@SidedProxy(clientSide = "us.cownet.examplemod.emp.EMPRendererClientSide",
			serverSide = "us.cownet.examplemod.utilities.RendererHelper")
	public static RendererHelper proxy;
}
