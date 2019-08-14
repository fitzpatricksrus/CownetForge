package us.cownet.examplemod.utilities;

import us.cownet.examplemod.utilities.hackfmlevents.HackFMLEventListener;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class RendererHelper {
//	@SidedProxy(clientSide = "us.cownet.examplemod.utilities.RendererHelper.ClientSide<E,R>",
//			serverSide = "us.cownet.examplemod.utilities.RendererHelper")
//	public static RendererHelper proxy;

	public static abstract class ClientSide<E extends Entity> extends RendererHelper implements IRenderFactory<E>, HackFMLEventListener {
		private Class<E> entityClass;

		public ClientSide(Class<E> entityClass) {
			this.entityClass = entityClass;
			subscribeToFMLEvents();
		}

		@Override
		public void handleFMLEvent(FMLPreInitializationEvent event) {
			RenderingRegistry.registerEntityRenderingHandler(entityClass, this);
		}
	}
}
