package us.cownet.examplemod.init;

import us.cownet.examplemod.ExampleMod;
import us.cownet.examplemod.blocks.GenericBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class ModBlocks {
	public static Block tutorialBlock;
	public static Block uru_ore;

	public static void init() {
		tutorialBlock = new GenericBlock("tutorial_block", Material.ROCK);
		uru_ore = new GenericBlock("uru_ore", Material.ROCK, ModItems.raw_uru, 1, 3, CreativeTabs.MISC);
		uru_ore.setHardness(3);
		uru_ore.setHarvestLevel("pickaxe", 2);

	}
}
