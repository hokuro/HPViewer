package basashi.hpview.gui;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import basashi.hpview.config.ConfigValue;
import basashi.hpview.core.EntityConfigurationEntry;
import basashi.hpview.core.HPViewer;
import basashi.hpview.core.log.ModLog;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class AdvancedGui extends GuiScreen {
	private GuiEntityList guiEntityList;
	private List<GuiTextField> textboxes;
	private boolean entrySelected;
	private int selectedEntry;
	private ArrayList<Rectangle> controlLocations;
	private ArrayList<String[]> controlTooltipText;
	private GuiToolTip tooltip;
	private int tooltipWidth;
	private int tooltipHeight;
	private Rectangle LastHovered;
	private int timeHovered;
	private GuiTextField search;
	private boolean popoffsetting;
	private boolean portraitsetting;
	private EntityConfigurationEntryComparator comparator;
	private EntityLivingBase tempMob;
	private EntityConfigurationEntry ece;

	public AdvancedGui(){
		textboxes = new ArrayList();
		entrySelected = false;
		selectedEntry = 0;
		controlLocations = new ArrayList();
		controlTooltipText = new ArrayList();
		tooltipWidth = 96;
		tooltipHeight = 64;
		LastHovered = null;
		timeHovered = 0;
		comparator = new EntityConfigurationEntryComparator();
	}

	protected void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6){
		super.drawGradientRect(par1, par2, par3, par4, par5, par6);
	}

	public void onGuiClosed(){
		super.onGuiClosed();
		ConfigValue.General.popOffsEnabled = popoffsetting;
		ConfigValue.General.portraitEnabled = portraitsetting;
	}

	public boolean doesGuiPauseGame(){
		return true;
	}

	public void initGui(){
		popoffsetting = ConfigValue.General.popOffsEnabled;
		portraitsetting = ConfigValue.General.portraitEnabled;
		ConfigValue.General.popOffsEnabled = false;
		ConfigValue.General.portraitEnabled = false;
		GuiEntityList.entities = new ArrayList(HPViewer.tool.getEntityMap().values());
		Collections.sort(GuiEntityList.entities, comparator);
		guiEntityList = new GuiEntityList(mc,120,height,16,height -16,10,25,this);
		this.fontRendererObj.drawStringWithShadow(
				((EntityConfigurationEntry)guiEntityList.entities.get(selectedEntry)).Clazz.getName(),225,160,10066431);
		tooltip = new GuiToolTip(this,this.tooltipWidth,this.tooltipHeight);
		tooltip.setCenterVertically(true);
		tooltip.setCentered(true);
		search = new GuiTextField(0,fontRendererObj,11,5,115,10);
		search.setText("Search...");
		createTooltips();
		super.initGui();
	}

	protected void mouseReleased(int par1,int par2, int par3){
		super.mouseReleased(par1,par2,par3);
	}

	protected void mouseClicked(int par1, int par2, int par3) throws IOException{
		for(GuiTextField gtf : this.textboxes){
			gtf.mouseClicked(par1,par2,par3);
		}
		search.mouseClicked(par1, par2, par3);
		if(search.isFocused()){
			if("Search...".equals(this.search.getText())){
				search.setText("");
			}
		}else if("".equals(search.getText())){
			search.setText("Search...");
		}
		super.mouseClicked(par1, par2, par3);
	}

	protected void keyTyped(char par1, int par2){
		for (GuiTextField gtf : this.textboxes){
			gtf.textboxKeyTyped(par1, par2);
		}
		if(search.isFocused()){
			guiEntityList.visibleEntities.clear();
			search.textboxKeyTyped(par1, par2);
			if(!"".equals(search.getText())){
				entrySelected = false;
				guiEntityList.selectedEntry = 0;
				for(EntityConfigurationEntry ec : GuiEntityList.entities){
					if(ec.Clazz.getName().toLowerCase().contains(search.getText().toLowerCase())){
						guiEntityList.visibleEntities.add(ec);
					}else if(EntityList.classToStringMapping.containsKey(ec.Clazz)){
						String temp = ((String)EntityList.classToStringMapping.get(ec.Clazz)).toLowerCase();
						if (temp.contains(search.getText().toLowerCase())){
							guiEntityList.visibleEntities.add(ec);
						}
					}else if (ec.NameOverride.toLowerCase().contains(search.getText().toLowerCase())){
						guiEntityList.visibleEntities.add(ec);
					}
				}
			}
		}
	}

	public void createTooltips() {
		this.controlLocations.clear();
		this.controlTooltipText.clear();
		this.controlLocations.add(new Rectangle(220 - this.fontRendererObj.getStringWidth("Scale Factor"), 32,
				this.fontRendererObj.getStringWidth("Scale Factor"), this.fontRendererObj.FONT_HEIGHT));
		this.controlTooltipText.add(new String[] { "How big mob looks in portrait" });
		this.controlLocations.add(new Rectangle(220 - this.fontRendererObj.getStringWidth("X Offset"), 48,
				this.fontRendererObj.getStringWidth("X Offset"), this.fontRendererObj.FONT_HEIGHT));
		this.controlTooltipText.add(new String[] { "Nudge this many pixels right." });
		this.controlLocations.add(new Rectangle(220 - this.fontRendererObj.getStringWidth("Y Offset"), 64,
				this.fontRendererObj.getStringWidth("Y Offset"), this.fontRendererObj.FONT_HEIGHT));
		this.controlTooltipText.add(new String[] { "Nudge this many pixels down." });
		this.controlLocations.add(new Rectangle(220 - this.fontRendererObj.getStringWidth("Size Scaling"), 80,
				this.fontRendererObj.getStringWidth("Size Scaling"), this.fontRendererObj.FONT_HEIGHT));
		this.controlTooltipText.add(new String[] { "For Slime Type Mobs. How much to scale based on size." });
		this.controlLocations.add(new Rectangle(220 - this.fontRendererObj.getStringWidth("Baby Scaling"), 112,
				this.fontRendererObj.getStringWidth("Baby Scaling"), this.fontRendererObj.FONT_HEIGHT));
		this.controlTooltipText.add(new String[] { "To make babies bigger in portrait." });
		this.controlLocations.add(new Rectangle(220 - this.fontRendererObj.getStringWidth("Name Override"), 144,
				this.fontRendererObj.getStringWidth("Name Override"), this.fontRendererObj.FONT_HEIGHT));
		this.controlTooltipText.add(new String[] { "Use this name instead." });
		this.controlLocations.add(new Rectangle(225, 142, 120, 10));
		this.controlTooltipText.add(new String[] { "Replace name with this text." });
		this.controlLocations.add(new Rectangle(220 - this.fontRendererObj.getStringWidth("Full Class Name"), 160,
				this.fontRendererObj.getStringWidth("Full Class Name"), this.fontRendererObj.FONT_HEIGHT));
		this.controlTooltipText.add(new String[] { "Full Class Path For Debugging." });
		this.controlLocations.add(new Rectangle(225, 160,
				this.fontRendererObj.getStringWidth(
						((EntityConfigurationEntry) this.guiEntityList.visibleEntities.get(this.selectedEntry)).Clazz
								.getName()),
				this.fontRendererObj.FONT_HEIGHT));
		this.controlTooltipText.add(new String[] { "Full Class Path For Debugging." });
		this.controlLocations.add(new Rectangle(220 - (this.fontRendererObj.getStringWidth("Prefix Babies") + 12), 96,
				this.fontRendererObj.getStringWidth("Prefix Babies") + 12, 12));
		this.controlTooltipText.add(new String[] { "Prefix names with baby if a baby." });
		this.controlLocations.add(new Rectangle(300, 186, 80, 20));
		this.controlTooltipText.add(new String[] { "Save all changes." });
		this.controlLocations.add(new Rectangle(225, 30, 120, 10));
		this.controlTooltipText.add(new String[] { "How big mob looks in portrait" });
		this.controlLocations.add(new Rectangle(225, 46, 120, 10));
		this.controlTooltipText.add(new String[] { "Nudge this many pixels right." });
		this.controlLocations.add(new Rectangle(225, 62, 120, 10));
		this.controlTooltipText.add(new String[] { "Nudge this many pixels down." });
		this.controlLocations.add(new Rectangle(225, 78, 120, 10));
		this.controlTooltipText.add(new String[] { "For Slime Type Mobs. How much to scale based on size." });
		this.controlLocations.add(new Rectangle(225, 110, 120, 10));
		this.controlTooltipText.add(new String[] { "To make babies bigger in portrait." });
		this.controlLocations.add(new Rectangle(32, 32, 120, this.height - 64));
		this.controlTooltipText.add(new String[] { "Detected Entities. Click to configure." });
		for (int i = 0; i < this.controlLocations.size(); i++) {
			this.controlTooltipText.set(i,
					(String[]) this.fontRendererObj
							.listFormattedStringToWidth(((String[]) this.controlTooltipText.get(i))[0], this.tooltipWidth - 2)
							.toArray());
		}
	}

	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(2);
		this.guiEntityList.drawScreen(par1, par2, par3);
		boolean found = false;
		this.search.drawTextBox();
		if (this.entrySelected) {
			if (((this.guiEntityList.visibleEntities.size() > 0)
					|| (this.guiEntityList.visibleEntities.get(this.selectedEntry) != null))
					&& ((this.ece == null)
							|| (this.ece != this.guiEntityList.visibleEntities.get(this.selectedEntry)))) {
				this.ece = ((EntityConfigurationEntry) this.guiEntityList.visibleEntities.get(this.selectedEntry));
				try {
					if (this.tempMob != null) {
						this.tempMob.setDead();
					}
					try {
						this.tempMob = ((EntityLivingBase) this.ece.Clazz.getConstructor(new Class[] { World.class })
								.newInstance(new Object[] { this.mc.theWorld }));
					} catch (InstantiationException ex) {
						this.tempMob = null;
					}
				} catch (Throwable ex) {
					ModLog.log().warn(ex.getMessage());
				}
			}
			fontRendererObj.drawStringWithShadow("Scale Factor:", 220 - fontRendererObj.getStringWidth("Scale Factor"), 32,16777215);
			fontRendererObj.drawStringWithShadow("X Offset:", 220 - fontRendererObj.getStringWidth("X Offset"), 48,16777215);
			fontRendererObj.drawStringWithShadow("Y Offset:", 220 - fontRendererObj.getStringWidth("Y Offset"), 64,16777215);
			fontRendererObj.drawStringWithShadow("Size Scaling:", 220 - fontRendererObj.getStringWidth("Size Scaling"), 80,16777215);
			fontRendererObj.drawStringWithShadow("Baby Scaling:", 220 - fontRendererObj.getStringWidth("Baby Scaling"), 112,16777215);
			fontRendererObj.drawStringWithShadow("Name Override:", 220 - fontRendererObj.getStringWidth("Name Override"),144, 16777215);
			fontRendererObj.drawStringWithShadow("Full Class Name:",220 - fontRendererObj.getStringWidth("Full Class Name"), 160, 16777215);
			fontRendererObj.drawStringWithShadow(((EntityConfigurationEntry) guiEntityList.visibleEntities.get(selectedEntry)).Clazz
							.getName(),225, 160, 10066431);
			for (GuiTextField gtf : this.textboxes) {
				gtf.drawTextBox();
			}
			GL11.glPushMatrix();
			if (ece == null) {
				ece = ((EntityConfigurationEntry) guiEntityList.visibleEntities.get(selectedEntry));
			}
			String Name = ece.NameOverride;
			if (this.tempMob != null) {
				if ((Name == null) || ("".equals(Name))) {
					Name = tempMob.getName();
				}
			} else if ((Name == null) || ("".equals(Name))) {
				if (EntityList.classToStringMapping.containsKey(ece.Clazz)) {
					Name = EntityList.classToStringMapping.get(ece.Clazz).toString();
				} else {
					Name = ece.Clazz.getName().substring(ece.Clazz.getName().lastIndexOf(".") + 1);
				}
			}
			zLevel += 0.1F;
			GL11.glPushMatrix();
			float oldVal = ConfigValue.General.guiScale;
			GL11.glPushAttrib(8192);
			try {
				if (ConfigValue.General.skinnedPortrait) {
					ConfigValue.General.guiScale = 1.0F;
					GuiTool.DrawPortraitSkinned(150, 175, true, Name,
							(int) Math.ceil(tempMob == null ? 0.0D : tempMob.getMaxHealth()),
							(int) Math.ceil(tempMob == null ? 0.0D : tempMob.getHealth()), tempMob);
				} else {
					GuiTool.DrawPortraitUnSkinned(150, 175, true, Name,
							(int) Math.ceil(tempMob == null ? 0.0D : tempMob.getMaxHealth()),
							(int) Math.ceil(tempMob == null ? 0.0D : tempMob.getHealth()), tempMob);
				}
			} catch (Throwable ex) {
			}
			GL11.glPopAttrib();
			zLevel += 0.1F;
			ConfigValue.General.guiScale = oldVal;
			GL11.glPopMatrix();
			super.drawScreen(par1, par2, par3);
			GL11.glPopMatrix();
			try {
				if (controlLocations != null) {
					for (int i = 0; i < controlLocations.size(); i++) {
						try {
							if (((Rectangle) controlLocations.get(i)).contains(par1, par2)) {
								found = true;
								int transparency = 0;
								if ((controlLocations.get(i) != null) && ((LastHovered == null)
										|| (LastHovered != controlLocations.get(i)))) {
									LastHovered = ((Rectangle) controlLocations.get(i));
									timeHovered = 0;
								}
								if (timeHovered != -2) {
									timeHovered += 40;
									timeHovered = (timeHovered > 255 ? 63534 : timeHovered);
									transparency = timeHovered >= 0 ? timeHovered
											: timeHovered > 65281 ? -timeHovered : 255;
								}
								transparency = MathHelper.floor_float(transparency * 0.75F);
								tooltip.setGlobalAlpha(transparency);
								tooltip.HEIGHT = (((String[]) controlTooltipText.get(i)).length
										* (fontRendererObj.FONT_HEIGHT + 2) + 6);
								tooltip.setUpForDraw(par1, par2, (String[]) controlTooltipText.get(i));
								tooltip.setDontUseTexture();
								tooltip.drawStrings(fontRendererObj);
								break;
							}
						} catch (Throwable ex) {
						}
					}
				}
			} catch (Throwable ex) {
			}
		} else {
			Rectangle ListRect = new Rectangle(32, 32, 120, height - 64);
			if (ListRect.contains(par1, par2)) {
				found = true;
				int transparency = 0;
				LastHovered = ListRect;
				timeHovered += 15;
				if (timeHovered <= 1000) {
					if (timeHovered <= 255) {
						transparency = timeHovered;
					} else {
						transparency = 255;
					}
				} else if (timeHovered - 1000 <= 255) {
					transparency = 255 - (timeHovered - 1000);
				} else {
					transparency = 0;
				}
				transparency = MathHelper.floor_float(transparency * 0.75F);
				String[] lines = (String[]) this.fontRendererObj
						.listFormattedStringToWidth("Detected Entities. Click to configure.", tooltip.WIDTH - 2)
						.toArray(new String[0]);
				tooltip.setGlobalAlpha(transparency);
				tooltip.HEIGHT = (lines.length * (this.fontRendererObj.FONT_HEIGHT + 2) + 6);
				tooltip.setUpForDraw(par1, par2, lines);
				tooltip.setDontUseTexture();
				tooltip.drawStrings(fontRendererObj);
			} else {
				LastHovered = null;
				timeHovered = 0;
			}
		}
		if (!found) {
			LastHovered = null;
			timeHovered = 0;
		}
	}

	public void updateScreen() {
		this.search.updateCursorCounter();
		for (GuiTextField gtf : this.textboxes) {
			gtf.updateCursorCounter();
		}
		super.updateScreen();
	}

	protected void actionPerformed(GuiButton par1GuiButton) throws IOException {
		if ((par1GuiButton instanceof GuiCheckBox)) {
			((GuiCheckBox) par1GuiButton).toggle();
		} else if (par1GuiButton != null) {
			EntityConfigurationEntry current = (EntityConfigurationEntry) this.guiEntityList.visibleEntities.get(this.selectedEntry);
			for (GuiTextField textbox : textboxes) {
				if (textbox != this.textboxes.get(5)) {
					try {
						textbox.setText("" + Float.valueOf(textbox.getText()));
					} catch (Throwable ex) {
						textbox.setText("0.0");
					}
				}
			}
			EntityConfigurationEntry newEce = new EntityConfigurationEntry(current.Clazz,
					Float.valueOf(((GuiTextField) this.textboxes.get(0)).getText()).floatValue(),
					Float.valueOf(((GuiTextField) this.textboxes.get(1)).getText()).floatValue(),
					Float.valueOf(((GuiTextField) this.textboxes.get(2)).getText()).floatValue(),
					Float.valueOf(((GuiTextField) this.textboxes.get(3)).getText()).floatValue(),
					Float.valueOf(((GuiTextField) this.textboxes.get(4)).getText()).floatValue(),
					((GuiCheckBox) this.buttonList.get(1)).isChecked(),
					((GuiTextField) this.textboxes.get(5)).getText(),
					((GuiCheckBox) this.buttonList.get(0)).isChecked(), current.maxHP, current.eyeHeight);
			if (!current.equals(newEce)) {
				HPViewer.tool.getEntityMap().put(newEce.Clazz, newEce);
				EntityConfigurationEntry.saveEntityConfig(newEce);
				GuiEntityList.entities = new ArrayList(HPViewer.tool.getEntityMap().values());
				Collections.sort(GuiEntityList.entities, this.comparator);
				this.guiEntityList.visibleEntities.set(this.selectedEntry, newEce);
			}
		}
		super.actionPerformed(par1GuiButton);
	}

	public FontRenderer getFontRenderer() {
		return fontRendererObj;
	}

	public void listClickedCallback(int index) {
		buttonList.clear();
		textboxes = new ArrayList();
		entrySelected = true;
		selectedEntry = index;
		buttonList.add(0, new GuiCheckBox(0, 220 - (fontRendererObj.getStringWidth("Ignore Mob") + 12), 14,
				fontRendererObj.getStringWidth("Ignore Mob") + 12, 12, "Ignore Mob"));
		((GuiCheckBox) buttonList.get(0))
				.setChecked(((EntityConfigurationEntry) guiEntityList.visibleEntities.get(index)).IgnoreThisMob);
		buttonList.add(1, new GuiCheckBox(1, 220 - (fontRendererObj.getStringWidth("Prefix Babies") + 12), 96,
				fontRendererObj.getStringWidth("Prefix Babies") + 12, 12, "Prefix Babies"));
		((GuiCheckBox) buttonList.get(1))
				.setChecked(((EntityConfigurationEntry) guiEntityList.visibleEntities.get(index)).AppendBaby);
		this.buttonList.add(2, new GuiButton(2, 315, 186, 80, 20, "Save"));
		addTextBoxes(index);
		String Name = "";
		if (((EntityConfigurationEntry) guiEntityList.visibleEntities.get(index)).NameOverride != null) {
			Name = ((EntityConfigurationEntry) guiEntityList.visibleEntities.get(index)).NameOverride;
		} else {
			Name = "";
		}
		((GuiTextField) this.textboxes.get(5)).setText(Name);
	}

	public void addTextBoxes(int listIndex) {
		textboxes.add(0, new GuiTextField(0,fontRendererObj, 225, 30, 120, 10));
		((GuiTextField) textboxes.get(0)).setText(String.valueOf(((EntityConfigurationEntry) guiEntityList.visibleEntities.get(listIndex)).ScaleFactor));
		textboxes.add(1, new GuiTextField(1,fontRendererObj, 225, 46, 120, 10));
		((GuiTextField) textboxes.get(1)).setText(String.valueOf(((EntityConfigurationEntry) guiEntityList.visibleEntities.get(listIndex)).XOffset));
		textboxes.add(2, new GuiTextField(2,fontRendererObj, 225, 62, 120, 10));
		((GuiTextField) textboxes.get(2)).setText(String.valueOf(((EntityConfigurationEntry) guiEntityList.visibleEntities.get(listIndex)).YOffset));
		textboxes.add(3, new GuiTextField(3,fontRendererObj, 225, 78, 120, 10));
		((GuiTextField) textboxes.get(3)).setText(String.valueOf(((EntityConfigurationEntry) guiEntityList.visibleEntities.get(listIndex)).EntitySizeScaling));
		textboxes.add(4, new GuiTextField(4,fontRendererObj, 225, 110, 120, 10));
		((GuiTextField) textboxes.get(4)).setText(String.valueOf(((EntityConfigurationEntry) guiEntityList.visibleEntities.get(listIndex)).BabyScaleFactor));
		textboxes.add(5, new GuiTextField(5,fontRendererObj, 225, 142, 120, 10));
	}

	public class EntityConfigurationEntryComparator implements Comparator<EntityConfigurationEntry> {
		public EntityConfigurationEntryComparator() {
		}

		public int compare(EntityConfigurationEntry o1, EntityConfigurationEntry o2) {
			return o1.Clazz.getName().compareTo(o2.Clazz.getName());
		}
	}
}
