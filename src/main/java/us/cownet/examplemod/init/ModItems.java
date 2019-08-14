package us.cownet.examplemod.init;

import us.cownet.examplemod.ExampleMod;
import us.cownet.examplemod.emp.EMPAmmo;
import us.cownet.examplemod.emp.EMPGun;
import us.cownet.examplemod.items.GenericItem;
import us.cownet.examplemod.thorhammer.ThorHammer;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class ModItems {

	public static Item thor_hammer;
	public static Item basicIngot;
	public static Item raw_uru;
	public static Item empRound;
	public static Item empGun;

	public static void init() {
		basicIngot = new GenericItem("basic_ingot");
		raw_uru = new GenericItem("raw_uru");
		thor_hammer = new ThorHammer("thor_hammer");
		empRound = new EMPAmmo("emp_round");
		empGun = new EMPGun();
	}
}
