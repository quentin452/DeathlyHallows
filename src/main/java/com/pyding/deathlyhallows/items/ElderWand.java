package com.pyding.deathlyhallows.items;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.blocks.BlockPlacedItem;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.infusion.infusions.symbols.EffectRegistry;
import com.emoniph.witchery.infusion.infusions.symbols.SymbolEffect;
import com.emoniph.witchery.item.ItemBase;
import com.emoniph.witchery.network.PacketSpellPrepared;
import com.emoniph.witchery.util.ChatUtil;
import com.emoniph.witchery.util.SoundEffect;
import com.emoniph.witchery.util.TimeUtil;
import com.pyding.deathlyhallows.spells.SpellRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class ElderWand extends ItemBase {
    private static final float THRESHOLD_ORTHOGONAL = 7.0F;
    private static final int MAX_STROKES = 15;

    public ElderWand() {
        this.setMaxStackSize(1);
        this.setFull3D();
    }

    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.rare;
    }

    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        String currentLanguage = StatCollector.translateToLocal("language.name");
        if (I18n.format("dh.util.language").equals("Ru")) {
            list.add("Листай список заклинаний на §cshift+ЛКМ/ЛКМ §7и кастуй одной кнопкой");
            list.add("Получи доступ к §c8 §7новым мощнейшим заклинаниям");
            list.add("Все заклинания описаны в книге заклинаний, кнопку можно настроить в настройках управления");
            if(stack.hasTagCompound()){
                if(stack.getTagCompound().hasKey("dhowner")) {
                    list.add("Владелец §9" + stack.getTagCompound().getString("dhowner"));
                }
            } else list.add("Владелец §9Смерть");
            list.add("Возможно иметь лишь один дар у себя в инвентаре");
        } else {
            list.add("Scroll list of spells by §cshift+LMB/LMB §7and cast by one button");
            list.add("Get access to §c8 §7new powerful spells");
            list.add("All spells are described in the spell book, you can change button in settings");
            if(stack.hasTagCompound()){
                if(stack.getTagCompound().hasKey("dhowner")) {
                    list.add("Owner §9" + stack.getTagCompound().getString("dhowner"));
                }
            } else list.add("Owner §9Death");
            list.add("You can have only one Hallow at the time");
        }
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        World world = entityLiving.worldObj;
        if(entityLiving instanceof EntityPlayer){
            if(!world.isRemote){
                NBTTagCompound nbt = new NBTTagCompound();
                SpellRegistry spellRegistry = new SpellRegistry();
                EntityPlayer player = (EntityPlayer) entityLiving;
                if(stack.getTagCompound() != null){
                    nbt = stack.getTagCompound();
                }
                if (!nbt.hasKey("spell1")) {
                    nbt.setInteger("spell1", 1);
                    stack.setTagCompound(nbt);
                    player.addChatComponentMessage(new ChatComponentText(spellRegistry.getName(1)));
                } else {
                    int spell1 = nbt.getInteger("spell1");
                    if(spell1 != 0) {
                        if(!entityLiving.isSneaking()) {
                            if(spell1 < spellRegistry.spellCount) {
                                spell1++;
                            } else {
                                spell1 = 1;
                            }
                        } else {
                            if(spell1 > 1) {
                                spell1--;
                            } else {
                                spell1 = spellRegistry.spellCount;
                            }
                        }
                    } else {
                        spell1 = 1;
                    }
                    while(spellRegistry.getName(spell1).equals("IdError")) {
                        if(!entityLiving.isSneaking()) {
                            if(spell1 < spellRegistry.spellCount) {
                                spell1++;
                            } else {
                                spell1 = 1;
                            }
                        } else {
                            if(spell1 > 1) {
                                spell1--;
                            } else {
                                spell1 = spellRegistry.spellCount;
                            }
                        }
                    }
                    nbt.setInteger("spell1", spell1);
                    stack.setTagCompound(nbt);

                    stack.setTagCompound(nbt);
                    player.addChatComponentMessage(new ChatComponentText(spellRegistry.getName(nbt.getInteger("spell1"))));
                }
            }
        }
        return super.onEntitySwing(entityLiving, stack);
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.block;
    }

    public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
        return super.onDroppedByPlayer(item, player);
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return '\u8ca0';
    }

    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return true;
    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int invSlot, boolean isHeld) {}

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        NBTTagCompound nbtTag = player.getEntityData();
        if(!player.worldObj.isRemote) {
            nbtTag.removeTag("WITCSpellEffectID");
            nbtTag.removeTag("WITCSpellEffectEnhanced");
        }

        nbtTag.setByteArray("Strokes", new byte[0]);
        nbtTag.setFloat("startPitch", player.rotationPitch);
        nbtTag.setFloat("startYaw", player.rotationYawHead);
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if(world.getBlock(x, y, z) == Witchery.Blocks.ALTAR && side == 1 && world.getBlock(x, y + 1, z) == Blocks.air) {
            BlockPlacedItem.placeItemInWorld(stack, player, world, x, y + 1, z);
            player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
            return !world.isRemote;
        } else {
            return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
        }
    }

    public void onUsingTick(ItemStack stack, EntityPlayer player, int countdown) {
        if(player.worldObj.isRemote) {
            NBTTagCompound nbtTag = player.getEntityData();
            if(nbtTag == null) {
                return;
            }

            float yawDiff = nbtTag.getFloat("startYaw") - player.rotationYawHead;
            float pitchDiff = nbtTag.getFloat("startPitch") - player.rotationPitch;
            byte[] strokes = nbtTag.getByteArray("Strokes");
            int strokesStart = strokes.length;
            if(!EffectRegistry.instance().contains(strokes) && strokesStart <= 15) {
                if(pitchDiff >= 7.0F) {
                    strokes = this.addNewStroke(nbtTag, strokes, (byte)0);
                } else if(pitchDiff <= -7.0F) {
                    strokes = this.addNewStroke(nbtTag, strokes, (byte)1);
                } else if(yawDiff <= -7.0F) {
                    strokes = this.addNewStroke(nbtTag, strokes, (byte)2);
                } else if(yawDiff >= 7.0F) {
                    strokes = this.addNewStroke(nbtTag, strokes, (byte)3);
                }

                if(strokes.length > strokesStart) {
                    nbtTag.setFloat("startPitch", player.rotationPitch);
                    nbtTag.setFloat("startYaw", player.rotationYawHead);
                }

                SymbolEffect effect = EffectRegistry.instance().getEffect(strokes);
                if(effect != null) {
                    int level = EffectRegistry.instance().getLevel(strokes);
                    Witchery.packetPipeline.sendToServer(new PacketSpellPrepared(effect, level));
                }
            }
        }

    }

    public byte[] addNewStroke(NBTTagCompound nbtTag, byte[] strokes, byte stroke) {
        byte[] newStrokes = new byte[strokes.length + 1];
        System.arraycopy(strokes, 0, newStrokes, 0, strokes.length);
        newStrokes[newStrokes.length - 1] = stroke;
        nbtTag.setByteArray("Strokes", newStrokes);
        return newStrokes;
    }

    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int countdown) {
        NBTTagCompound nbtTag = player.getEntityData();
        if(nbtTag != null) {
            if(!world.isRemote) {
                int effectID = nbtTag.getInteger("WITCSpellEffectID");
                int level = 1;
                if(nbtTag.hasKey("WITCSpellEffectEnhanced")) {
                    level = nbtTag.getInteger("WITCSpellEffectEnhanced");
                    nbtTag.removeTag("WITCSpellEffectEnhanced");
                }

                nbtTag.removeTag("WITCSpellEffectID");
                SymbolEffect effect = EffectRegistry.instance().getEffect(effectID);
                NBTTagCompound nbtPerm = Infusion.getNBT(player);
                if(effect != null) {
                    if(!player.capabilities.isCreativeMode && (nbtPerm == null || !nbtPerm.hasKey("witcheryInfusionID") || !nbtPerm.hasKey("witcheryInfusionCharges"))) {
                        ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.infuse.branch.infusionrequired", new Object[0]);
                        SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
                    } else if(effect.hasValidInfusion(player, nbtPerm.getInteger("witcheryInfusionID"))) {
                        if(effect.hasValidKnowledge(player, nbtPerm)) {
                            long ticksRemaining = effect.cooldownRemaining(player, nbtPerm);
                            if(ticksRemaining > 0L && !player.capabilities.isCreativeMode) {
                                ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.infuse.branch.effectoncooldown", new Object[]{Long.valueOf(TimeUtil.ticksToSecs(ticksRemaining)).toString()});
                                SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
                            } else {
                                if(level > 1) {
                                    int newLevel = 1;
                                    if(player.isPotionActive(Witchery.Potions.WORSHIP)) {
                                        PotionEffect potion = player.getActivePotionEffect(Witchery.Potions.WORSHIP);
                                        if(level <= potion.getAmplifier() + 2) {
                                            newLevel = level;
                                        }
                                    }

                                    level = newLevel;
                                }

                                if(!player.capabilities.isCreativeMode && nbtPerm.getInteger("witcheryInfusionCharges") < effect.getChargeCost(world, player, level)) {
                                    ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.infuse.branch.nocharges", new Object[0]);
                                    SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
                                } else {
                                    effect.perform(world, player, level);
                                    if(!player.capabilities.isCreativeMode) {
                                        Infusion.setCurrentEnergy(player, nbtPerm.getInteger("witcheryInfusionCharges") - effect.getChargeCost(world, player, level));
                                    }
                                }
                            }
                        } else {
                            ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.infuse.branch.unknowneffect", new Object[0]);
                            SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
                        }
                    } else {
                        ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.infuse.branch.infernalrequired", new Object[0]);
                        SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
                    }
                } else {
                    ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.infuse.branch.unknownsymbol", new Object[0]);
                    SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
                }
            } else {
                nbtTag.removeTag("Strokes");
                nbtTag.removeTag("startYaw");
                nbtTag.removeTag("startPitch");
            }
        }
    }
}
