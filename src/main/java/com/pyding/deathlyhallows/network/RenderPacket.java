package com.pyding.deathlyhallows.network;

import com.pyding.deathlyhallows.entity.AbsoluteDeath;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class RenderPacket implements IMessage, IMessageHandler<RenderPacket,IMessage> {
    public NBTTagCompound nbtData;

    public int id;

    public RenderPacket(){
    }
    public RenderPacket(NBTTagCompound nbt, int identifier){
        nbtData = nbt;
        id = identifier;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        nbtData = ByteBufUtils.readTag(buf);
        id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, nbtData);
        buf.writeInt(id);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(RenderPacket message, MessageContext ctx) {
        if(ctx.side == Side.CLIENT){
            Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(message.id);
            if(entity instanceof AbsoluteDeath){
                AbsoluteDeath death = (AbsoluteDeath) Minecraft.getMinecraft().theWorld.getEntityByID(message.id);
                if (message.nbtData != null) {
                    death.getEntityData().setTag("dhData",message.nbtData);
                }
            } else {
                if(message.nbtData != null) {
                    entity.getEntityData().setTag("dhRenderData",message.nbtData);
                }
            }
        }
        return null;
    }
}
