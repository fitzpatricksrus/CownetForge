package us.cownet.examplemod.thorhammer;

import us.cownet.examplemod.items.GenericItem;
import us.cownet.examplemod.utilities.InventoryUtils;
import us.cownet.examplemod.utilities.RendererHelper;
import us.cownet.examplemod.utilities.commands.GenericCommand;
import us.cownet.examplemod.utilities.commands.InvalidValueException;
import us.cownet.examplemod.utilities.commands.Setting;
import us.cownet.examplemod.utilities.hackfmlevents.HackFMLEventListener;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Collection;

public class ThorHammer extends GenericItem implements HackFMLEventListener {
	private static final String COMMAND_NAME = "ThorHammer";
	private static final String COMMAND_USAGE = "Try /ThorHammer settings";
	private static final String[] COMMAND_ALIASES = {"thorhammer", "thorHammer", "th"};
	private static final String CONFIG_VERSION = "0.1";
	private static Block effectBlock = Blocks.MAGMA;
	private static Block aboveEffectBlock = Blocks.FIRE;
	// provide some reference to the renderer so it's class is loaded/constructed/registered
	private static final RendererHelper renderer = ThorHammerGuiRenderer.proxy;
	private static final float INACCURACY = 0.0f;
	private static float minVelocity = 0.25f;
	private static float maxVelocity = 2.0f;
	private static int timeToCharge = 20 * 5;

	private SnakeEffect snakeEffect = new SnakeEffect(new ThorHammerSnakeEffect());

	public ThorHammer(String name) {
		super(name);
		setMaxStackSize(1);
		GenericCommand.create(COMMAND_NAME, COMMAND_USAGE, COMMAND_ALIASES)
				.addTargetWithPersitentSettings(this, COMMAND_NAME, CONFIG_VERSION);
		subscribeToFMLEvents();
	}

	public ThorHammer(String name, CreativeTabs tab) {
		super(name, tab);
		setMaxStackSize(1);
		GenericCommand.create(COMMAND_NAME, COMMAND_USAGE, COMMAND_ALIASES)
				.addTargetWithPersitentSettings(this, COMMAND_NAME, CONFIG_VERSION);
		subscribeToFMLEvents();
	}

	@Override
	public void handleFMLEvent(FMLPreInitializationEvent event) {
//		for (int i = 0; i < EMP_SOUND_NAMES.length; i++) {
//			EMPSounds[i] = createSoundEvent(EMP_SOUND_NAMES[i]);
//		}
		ThorHammerProjectile.registerModEntity();
	}

	//----------------------------------------------------------------------------------------------------
	// Handle smashing block with hammer
	//----------------------------------------------------------------------------------------------------
	private static boolean isSolid(World w, Vec3d v) {
		// water, and air are not solid
		IBlockState state = w.getBlockState(SnakeEffect.toBlockPos(v));
		return state.getMaterial().isSolid()
				&& (state.getBlock() != effectBlock)
				&& (state.getBlock() != aboveEffectBlock);
	}

	private static boolean isBreakable(World world, Vec3d v) {
		return isBreakable(world, SnakeEffect.toBlockPos(v));
	}

	private static boolean isBreakable(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlockHardness(world, pos) >= 0;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos,
			EntityLivingBase entityLiving) {
		if (!worldIn.isRemote) {
			// get direction player is looking (normalized)
			Vec3d lookVec = entityLiving.getLookVec();
			// extend that to maximum range of gun
			Vec3d finishDistance = lookVec.scale(snakeEffect.getCrashRange());
			// start the affect from start to finish
			snakeEffect.startAffect(worldIn, pos,
					new BlockPos(pos.getX() + finishDistance.x, pos.getY(), pos.getZ() + finishDistance.z));
			return false;
		} else {
			return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
		}
	}

	private static class ThorHammerSnakeEffect implements SnakeEffect.SnakeEffectImpl {
		@Override
		public Vec3d calculateNextPosition(World world, Collection<BlockPos> prevPos, Vec3d currentPos, Vec3d stepSize) {
			Vec3d abovePos = currentPos.addVector(0.0d, 1.0d, 0.0d);
			Vec3d forwardPos = currentPos.add(stepSize);
			Vec3d belowPos = currentPos.subtract(0.0d, 1.0d, 0.0d);
			Vec3d nextPos;
			if (isSolid(world, abovePos) && !prevPos.contains(SnakeEffect.toBlockPos(abovePos))) {
				nextPos = abovePos;
			} else if (isSolid(world, forwardPos)) {
				nextPos = forwardPos;
			} else if (isSolid(world, belowPos)) {
				nextPos = belowPos;
			} else {
				// we can't move.  We're stuck.  So stop messing around and finish
				nextPos = null;
			}

			// we know where we want to go.  Check if we can go there.
			if (nextPos != null && isBreakable(world, nextPos)) {
				return nextPos;
			} else {
				return null;
			}
		}

		/** add initial affect to location. */
		@Override
		public void handleAddPosition(World world, BlockPos pos) {
			// turn block to magma and set it on fire.
			world.setBlockState(pos, effectBlock.getDefaultState());
			BlockPos above = pos.up();
			if (world.getBlockState(above).getMaterial() == Material.AIR) {
				world.setBlockState(above, aboveEffectBlock.getDefaultState());
			}
		}

		/** add final affect to the location */
		@Override
		public void handleRemovePosition(World world, BlockPos pos) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			BlockPos above = pos.up();
			if (world.getBlockState(above).getBlock() == aboveEffectBlock) {
				world.setBlockToAir(above);
			}
		}

	}

	@Setting
	public String getLowerBlock() {
		return effectBlock.getRegistryName().toString();
	}

	@Setting
	public void setLowerBlock(String blockName) throws InvalidValueException {
		Block b = Block.getBlockFromName(blockName);
		if (b != null) {
			effectBlock = b;
		} else {
			throw new InvalidValueException("lowerBlock", blockName, "Not a block");
		}
	}

	@Setting
	public String getUpperBlock() {
		return aboveEffectBlock.getRegistryName().toString();
	}

	@Setting
	public void setUpperBlock(String blockName) throws InvalidValueException {
		Block b = Block.getBlockFromName(blockName);
		if (b != null) {
			aboveEffectBlock = b;
		} else {
			throw new InvalidValueException("upperBlock", blockName, "Not a block");
		}
	}

	@Setting
	public double getCrashRange() {
		return snakeEffect.getCrashRange();
	}

	@Setting
	public void setCrashRange(double crashRange) {
		snakeEffect.setCrashRange(crashRange);
	}

	@Setting
	public int getCrashDurationTicks() {
		return snakeEffect.getCrashDurationTicks();
	}

	@Setting
	public void setCrashDurationTicks(int affectDurationInTicks) {
		snakeEffect.setCrashDurationTicks(affectDurationInTicks);
	}

	@Setting
	public double getCrashVelocity() {
		return snakeEffect.getCrashVelocity();
	}

	@Setting
	public void setCrashVelocity(double velocity) {
		snakeEffect.setCrashVelocity(velocity);
	}

	@Setting
	public int getCrashTrailLength() {
		return snakeEffect.getCrashTrailLength();
	}

	@Setting
	public void setCrashTrailLength(int crashTrailLength) {
		snakeEffect.setCrashTrailLength(crashTrailLength);
	}


	//----------------------------------------------------------------------------------------------------
	// Handle throwing hammer
	//----------------------------------------------------------------------------------------------------
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn) {
		ItemStack itemstack = player.getHeldItem(EnumHand.MAIN_HAND);
		if (handIn != EnumHand.MAIN_HAND) {
			return new ActionResult<>(EnumActionResult.FAIL, itemstack);
		} else {
			ItemStack ammo = InventoryUtils.asA(itemstack, ThorHammer.class);

			if ((ammo != null) && !ammo.isEmpty()) {
				player.setActiveHand(handIn);
				return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
			} else {
				// out of ammo.  FAIL.
				return new ActionResult<>(EnumActionResult.FAIL, itemstack);
			}
		}
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack ammo, World world, EntityLivingBase entityLiving, int timeLeft) {
		EntityPlayer player = (EntityPlayer) entityLiving;
		if (player.getActiveHand() == EnumHand.MAIN_HAND) {
			if ((ammo != null) && !ammo.isEmpty()) {
				player.setActiveHand(EnumHand.MAIN_HAND);
				ammo.grow(-1);
				player.swingArm(EnumHand.MAIN_HAND);
//			int soundNumber = player.world.rand.nextInt(EMPSounds.length);
//			SoundEvent sound = EMPSounds[soundNumber];
//			world.playSound(player, player.getPosition(), sound, SoundCategory.PLAYERS, 1.0f, 1.0f);
				if (!world.isRemote) {
					Vec2f pitchYaw = entityLiving.getPitchYaw();
					ThorHammerProjectile hammerProjectile = new ThorHammerProjectile(world, player, EnumHand.MAIN_HAND,
							pitchYaw.x, pitchYaw.y);
					hammerProjectile.throwHammer(player, getShotVelocity(ammo, timeLeft), INACCURACY);
					world.spawnEntity(hammerProjectile);
				}
			} else {
				// out of ammo && not the main hand.  FAIL.
				// can this even happen?
			}
		}
	}

	private float getShotVelocity(ItemStack ammo, int timeLeft) {
		// calculate velocity based on percentage of MaxItemUseDuration button held
		float velocityRange = maxVelocity - minVelocity;
		int maxChargeTime = getTimeToCharge();
		int timeUsed = maxChargeTime - timeLeft;
		int timeCharged = Math.min(maxChargeTime, timeUsed);
		float result = minVelocity + velocityRange * ((float) timeCharged / (float) maxChargeTime);
		return result;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return getTimeToCharge();
	}

	@Setting
	public static float getMinVelocity() {
		return minVelocity;
	}

	@Setting
	public static void setMinVelocity(float minVelocity) {
		ThorHammer.minVelocity = minVelocity;
	}

	@Setting
	public static float getMaxVelocity() {
		return maxVelocity;
	}

	@Setting
	public static void setMaxVelocity(float maxVelocity) {
		ThorHammer.maxVelocity = maxVelocity;
	}

	@Setting
	public static int getTimeToCharge() {
		return timeToCharge;
	}

	@Setting
	public static void setTimeToCharge(int timeToCharge) {
		ThorHammer.timeToCharge = timeToCharge;
	}

	@Setting
	public int getBounces() {
		return ThorHammerProjectile.getBounces();
	}

	@Setting
	public void setBounces(int bounces) {
		ThorHammerProjectile.setBounces(bounces);
	}

	@Setting
	public String getDamageBlock() {
		return ThorHammerProjectile.getDamageBlock();
	}

	@Setting
	public void setDamageBlock(String blockName) throws InvalidValueException {
		ThorHammerProjectile.setDamageBlock(blockName);
	}

	@Setting
	public String getDamageAffect() {
		return ThorHammerProjectile.getDamageAffect();
	}

	@Setting
	public void setDamageAffect(String affectName) throws InvalidValueException {
		ThorHammerProjectile.setDamageAffect(affectName);
	}

	@Setting
	public String getSparkle() {
		return ThorHammerProjectile.getSparkle();
	}

	@Setting
	public void setSparkle(String sparkleName) throws InvalidValueException {
		ThorHammerProjectile.setSparkle(sparkleName);
	}
}
