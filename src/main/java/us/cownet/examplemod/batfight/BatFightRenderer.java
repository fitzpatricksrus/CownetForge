package us.cownet.examplemod.batfight;

import net.minecraftforge.fml.common.SidedProxy;

public class BatFightRenderer {
	@SidedProxy(clientSide = "us.cownet.examplemod.batfight.BatFightRendererClientSide",
			serverSide = "us.cownet.examplemod.batfight.BatFightRenderer")
	public static BatFightRenderer proxy;
}
