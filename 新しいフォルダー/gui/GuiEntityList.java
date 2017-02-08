package basashi.hpview.gui;

import java.util.ArrayList;
import java.util.List;

import basashi.hpview.core.EntityConfigurationEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityMob;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class GuiEntityList extends GuiScrollingList {
	private AdvancedGui parent;
	private Minecraft mc;
	public static List<EntityConfigurationEntry> entities;
	public List<EntityConfigurationEntry> visibleEntities;
	public int selectedEntry = 0;

	public GuiEntityList(Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight, AdvancedGui parent){
		super(client,width,height,top,bottom,left,entryHeight);
		this.parent = parent;
		this.mc = client;
		this.visibleEntities = new ArrayList(entities);
	}

	protected int getSize(){
		return this.visibleEntities.size();
	}

	protected void elementClicked(int index, boolean doubleClick){
		this.selectedEntry = index;
		this.parent.listClickedCallback(index);
	}

	protected boolean isSelected(int index){
		return this.selectedEntry == index;
	}

	protected void drawBackground(){
		this.parent.drawBackground(2);
	}

	protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator ver5){
		String entryName =(String) EntityList.classToStringMapping
				.get(((EntityConfigurationEntry) this.visibleEntities.get(listIndex)).Clazz);
		if((((EntityConfigurationEntry)this.visibleEntities.get(listIndex)).NameOverride != null) &&
				(!"".equals(((EntityConfigurationEntry) this.visibleEntities.get(listIndex)).NameOverride))){
			entryName = ((EntityConfigurationEntry) this.visibleEntities.get(listIndex)).NameOverride;
		}else if (( entryName == null) || ("".equals(entryName))){
			String[] classes = ((EntityConfigurationEntry) this.visibleEntities.get(listIndex)).Clazz.getName().split(".");
			if((classes != null) && (classes.length > 0)){
				entryName =classes[(classes.length -1)];
			}else {
				entryName = ((EntityConfigurationEntry)this.visibleEntities.get(listIndex)).Clazz.getName();
			}
		}
		if(((EntityConfigurationEntry) this.visibleEntities.get(listIndex)).Clazz==EntityOtherPlayerMP.class){
			entryName = "Other Player";
		}else if (((EntityConfigurationEntry) this.visibleEntities.get(listIndex)).Clazz == EntityMob.class){
			entryName = "Generic Mob";
		}
		this.parent.getFontRenderer().drawString(parent.getFontRenderer().trimStringToWidth(entryName, this.listWidth -10),this.left + 3, var3 +2,16777215);
		String ModName = "Vanilla/Unknown Mod";
		EntityRegistry.EntityRegistration er = EntityRegistry.instance().lookupModSpawn(((EntityConfigurationEntry) this.visibleEntities.get(listIndex)).Clazz,true);
		if(er!=null){
			ModName = er.getContainer().getName();
		}
		this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(ModName, this.listWidth-10),this.left+3, var3+12,10066431);
	}
}
