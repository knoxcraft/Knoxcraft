package org.knoxcraft.serverturtle;

import java.util.List;
import java.util.UUID;

import net.canarymod.api.DamageType;
import net.canarymod.api.PathFinder;
import net.canarymod.api.ai.AIManager;
import net.canarymod.api.attributes.AttributeMap;
import net.canarymod.api.entity.Entity;
import net.canarymod.api.entity.EntityItem;
import net.canarymod.api.entity.EntityType;
import net.canarymod.api.entity.living.EntityLiving;
import net.canarymod.api.entity.living.LivingBase;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.nbt.BaseTag;
import net.canarymod.api.nbt.CompoundTag;
import net.canarymod.api.potion.Potion;
import net.canarymod.api.potion.PotionEffect;
import net.canarymod.api.potion.PotionEffectType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.position.Position;
import net.canarymod.api.world.position.Vector3D;

public class CustomTurtle implements net.canarymod.api.entity.living.EntityLiving{

	@Override
	public float getHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setHealth(float health) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void increaseHealth(float health) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getMaxHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxHealth(double maxHealth) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canSee(LivingBase entity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDeathTicks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDeathTicks(int ticks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getInvulnerabilityTicks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setInvulnerabilityTicks(int ticks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getAge() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAge(int age) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dealDamage(DamageType type, float damage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void knockBack(double xForce, double zForce) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPotionEffect(PotionEffect effect) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePotionEffect(PotionEffectType type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAllPotionEffects() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isPotionActive(Potion potion) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PotionEffect getActivePotionEffect(Potion potion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PotionEffect> getAllActivePotionEffects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRevengeTarget(LivingBase target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LivingBase getRevengeTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastAssailant(LivingBase entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LivingBase getLastAssailant() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void lookAt(double x, double y, double z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lookAt(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lookAt(Entity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getArrowCountInEntity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setArrowCountInEntity(int arrows) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void swingArm() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attackEntity(LivingBase target, float damage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getHeadRotation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setHeadRotation(float rot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AttributeMap getAttributeMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity getTargetLookingAt() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity getTargetLookingAt(int searchRadius) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getZ() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMotionX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMotionY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMotionZ() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getPitch() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getRotation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Position getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Location getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getEyeHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public UUID getUUID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setX(double x) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setX(int x) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setY(double y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setY(int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setZ(double z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setZ(int z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMotionX(double motionX) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMotionY(double motionY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMotionZ(double motionZ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPitch(float pitch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRotation(float rotation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Vector3D getMotion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector3D getForwardVector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void translate(Vector3D factor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moveEntity(double motionX, double motionY, double motionZ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void teleportTo(double x, double y, double z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void teleportTo(double x, double y, double z, World world) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void teleportTo(double x, double y, double z, float pitch, float rotation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void teleportTo(double x, double y, double z, float pitch, float rotation, World dim) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void teleportTo(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void teleportTo(Position position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public World getWorld() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSprinting() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSprinting(boolean sprinting) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSneaking() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSneaking(boolean sneaking) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFireTicks(int ticks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFireTicks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isLiving() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isItem() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMob() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAnimal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPlayer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isGolem() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNPC() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EntityItem dropLoot(int itemId, int amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityItem dropLoot(Item item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFqName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canSpawn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean spawn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean spawn(Entity rider) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRiding() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRidden() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Entity getRiding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity getRider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRider(Entity rider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mount(Entity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dismount() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDead() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CompoundTag getNBT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setNBT(BaseTag tag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isInvisible() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setInvisible(boolean invisible) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CompoundTag getMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityType getEntityType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAmbient() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOnGround() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInWeb() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInWater() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInLava() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasDisplayName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDisplayName(String display) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean showingDisplayName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setShowDisplayName(boolean show) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isEating() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void moveEntityToXYZ(double x, double y, double z, float speed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playLivingSound() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean spawn(EntityLiving... riders) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LivingBase getAttackTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttackTarget(LivingBase livingbase) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Item getItemInHand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item[] getEquipment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item getEquipmentInSlot(int slot) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEquipment(Item[] items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEquipment(Item item, int slot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getDropChance(int slot) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDropChance(int slot, float chance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canPickUpLoot() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCanPickUpLoot(boolean loot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isPersistenceRequired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PathFinder getPathFinder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AIManager getAITaskManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AIManager getAITargetTaskManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canAttackEntity(EntityType type) {
		// TODO Auto-generated method stub
		return false;
	}

}
