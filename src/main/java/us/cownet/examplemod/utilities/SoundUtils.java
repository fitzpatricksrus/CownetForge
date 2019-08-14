package us.cownet.examplemod.utilities;

import us.cownet.examplemod.ExampleMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class SoundUtils {
	private static HashMap<String, SoundEvent> sounds = new HashMap<>();

	public static SoundEvent createSoundEvent(String soundName) {
		final ResourceLocation soundID = new ResourceLocation(ExampleMod.MODID, soundName);
		SoundEvent result = new SoundEvent(soundID).setRegistryName(soundID);
		sounds.put(soundName, result);
		return result;
	}

	@SubscribeEvent
	public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event) {
		for (SoundEvent e : sounds.values()) {
			event.getRegistry().register(e);
		}
		sounds.clear();
	}
}
