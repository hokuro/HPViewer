package basashi.hpview.gui;

import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import basashi.hpview.config.ConfigValue;
import basashi.hpview.texture.AbstractSkin;
import basashi.hpview.texture.SkinRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.MathHelper;

public class SkinSlot {

	private SkinGui parentTexturePackGui;
	private Minecraft mc;
	public int selectedEntry;
	private final Minecraft client;
	protected final int listWidth;
	protected final int listHeight;
	protected final int top;
	protected final int bottom;
	private final int right;
	protected final int left;
	protected final int slotHeight;
	private int scrollUpActionId;
	private int scrollDownActionId;
	protected int mouseX;
	protected int mouseY;
	private float initialMouseClickY;
	private float scrollFactor;
	private float scrollDistance;
	private int selectedIndex;
	private long lastClickTime;
	private boolean field_25123_p;
	private boolean field_27262_q;
	private int field_27261_r;
	int boxLocX;
	int boxWidth;
	int boxHeight;
	int boxLocY;

	protected int getSize() {
		return AbstractSkin.AVAILABLESKINS.size() + 1;
	}

	public SkinSlot(SkinGui par1GuiTexturePacks) {
		this(Minecraft.getMinecraft(), par1GuiTexturePacks.width - 128, par1GuiTexturePacks.height - 128,
				64, par1GuiTexturePacks.height - 64, 64, 32);
		parentTexturePackGui = par1GuiTexturePacks;
		selectedEntry = 0;
		if (ConfigValue.General.portraitEnabled)
			selectedEntry = AbstractSkin.AVAILABLESKINS.indexOf(ConfigValue.General.selectedSkin) + 1;
	}

	protected void elementClicked(int par1, boolean par2) {
		if (par1 == 0) {
			ConfigValue.General.skinnedPortrait = false;
		} else {
			ConfigValue.General.skinnedPortrait = true;
			ConfigValue.General.selectedSkin = (String) AbstractSkin.AVAILABLESKINS.get(par1 - 1);
			AbstractSkin.setSkin(ConfigValue.General.selectedSkin);
		}
		if (par2)
			Minecraft.getMinecraft().displayGuiScreen(parentTexturePackGui);
		selectedEntry = par1;
	}

	protected boolean isSelected(int index) {
		return selectedEntry == index;
	}

	protected void drawSlot(int par1, int par2, int par3, int par4, Tessellator par5Tessellator) {
		String text1 = "Unskinned";
		String text2 = "rich1051414";
		int color = 14483456;
		if (par1 != 0) {
			text1 = AbstractSkin.getSkinName((String) SkinRegistration.AVAILABLESKINS.get(par1 - 1));
			text2 = AbstractSkin.getAuthor((String) SkinRegistration.AVAILABLESKINS.get(par1 - 1));
			color = 3398963;
		}
		parentTexturePackGui.drawString(Minecraft.getMinecraft().fontRendererObj, text1, left + 4, par3 + 3, color);
		parentTexturePackGui.drawString(Minecraft.getMinecraft().fontRendererObj, text2, left + 4, par3 + 15, color);
	}

	public SkinSlot(Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight) {
		selectedEntry = 0;
		initialMouseClickY = -2F;
		selectedIndex = -1;
		lastClickTime = 0L;
		field_25123_p = true;
		this.client = client;
		listWidth = width;
		listHeight = height;
		this.top = top;
		this.bottom = bottom;
		slotHeight = entryHeight;
		this.left = left;
		right = width + this.left;
		ScaledResolution scaledresolution = new ScaledResolution(client);
		boxLocX = MathHelper.floor_double(left * scaledresolution.getScaleFactor());
		boxWidth = MathHelper.floor_double(width * scaledresolution.getScaleFactor());
		boxHeight = MathHelper.floor_double(height * scaledresolution.getScaleFactor());
		boxLocY = MathHelper.floor_double(top * scaledresolution.getScaleFactor());
		selectedEntry = 0;
		if (ConfigValue.General.portraitEnabled)
			selectedEntry = AbstractSkin.AVAILABLESKINS.indexOf(ConfigValue.General.selectedSkin) + 1;
	}

	public void func_27258_a(boolean falg) {
		field_25123_p = falg;
	}

	protected void func_27259_a(boolean flag, int i) {
		field_27262_q = flag;
		field_27261_r = i;
		if (!flag)
			field_27261_r = 0;
	}

	protected int getContentHeight() {
		return getSize() * slotHeight + field_27261_r;
	}

	protected void func_27260_a(int i, int j, Tessellator tessellator) {
	}

	protected void func_27255_a(int i, int j) {
	}

	protected void func_27257_b(int i, int j) {
	}

	public int func_27256_c(int p_27256_1_, int p_27256_2_) {
		int var3 = left + 1;
		int var4 = (left + listWidth) - 7;
		int var5 = ((p_27256_2_ - top - field_27261_r) + (int) scrollDistance) - 4;
		int var6 = var5 / slotHeight;
		return p_27256_1_ < var3 || p_27256_1_ > var4 || var6 < 0 || var5 < 0 || var6 >= getSize() ? -1 : var6;
	}

	public void registerScrollButtons(List p_22240_1_, int p_22240_2_, int p_22240_3_) {
		scrollUpActionId = p_22240_2_;
		scrollDownActionId = p_22240_3_;
	}

	private void applyScrollLimits() {
		int var1 = getContentHeight() - (bottom - top - 4);
		if (var1 < 0)
			var1 /= 2;
		if (scrollDistance < 0.0F)
			scrollDistance = 0.0F;
		if (scrollDistance > (float) var1)
			scrollDistance = var1;
	}

	public void actionPerformed(GuiButton button) {
		if (button.enabled)
			if (button.id == scrollUpActionId) {
				scrollDistance -= (slotHeight * 2) / 3;
				initialMouseClickY = -2F;
				applyScrollLimits();
			} else if (button.id == scrollDownActionId) {
				scrollDistance += (slotHeight * 2) / 3;
				initialMouseClickY = -2F;
				applyScrollLimits();
			}
	}

	public void drawScreen(int mouseX, int mouseY, float p_22243_3_) {
		try {
			GL11.glEnable(3089);
			GL11.glScissor(boxLocX, boxLocY, boxWidth, boxHeight);
			this.mouseX = mouseX;
			this.mouseY = mouseY;
			int listLength = getSize();
			int scrollBarXStart = (left + listWidth) - 6;
			int scrollBarXEnd = scrollBarXStart + 6;
			int boxLeft = left;
			int boxRight = scrollBarXStart - 1;
			if (Mouse.isButtonDown(0)) {
				if (initialMouseClickY == -1F) {
					boolean var7 = true;
					if (mouseY >= top && mouseY <= bottom) {
						int var10 = ((mouseY - top - field_27261_r) + (int) scrollDistance) - 4;
						int var11 = var10 / slotHeight;
						if (mouseX >= boxLeft && mouseX <= boxRight && var11 >= 0 && var10 >= 0 && var11 < listLength) {
							boolean var12 = var11 == selectedIndex && System.currentTimeMillis() - lastClickTime < 250L;
							elementClicked(var11, var12);
							selectedIndex = var11;
							lastClickTime = System.currentTimeMillis();
						} else if (mouseX >= boxLeft && mouseX <= boxRight && var10 < 0) {
							func_27255_a(mouseX - boxLeft, ((mouseY - top) + (int) scrollDistance) - 4);
							var7 = false;
						}
						if (mouseX >= scrollBarXStart && mouseX <= scrollBarXEnd) {
							scrollFactor = -1F;
							int var19 = getContentHeight() - (bottom - top - 4);
							if (var19 < 1)
								var19 = 1;
							int var13 = ((bottom - top) * (bottom - top)) / getContentHeight();
							if (var13 < 32)
								var13 = 32;
							if (var13 > bottom - top - 8)
								var13 = bottom - top - 8;
							scrollFactor /= (bottom - top - var13) / var19;
						} else {
							scrollFactor = 1.0F;
						}
						if (var7)
							initialMouseClickY = mouseY;
						else
							initialMouseClickY = -2F;
					} else {
						initialMouseClickY = -2F;
					}
				} else if (initialMouseClickY >= 0.0F) {
					scrollDistance -= ((float) mouseY - initialMouseClickY) * scrollFactor;
					initialMouseClickY = mouseY;
				}
			} else {
				do {
					if (!Mouse.next())
						break;
					int var16 = Mouse.getEventDWheel();
					if (var16 != 0) {
						if (var16 > 0)
							var16 = -1;
						else if (var16 < 0)
							var16 = 1;
						scrollDistance += (var16 * slotHeight) / 2;
					}
				} while (true);
				initialMouseClickY = -1F;
			}
			applyScrollLimits();
			GL11.glDisable(2896);
			GL11.glDisable(2912);
			WorldRenderer render = Tessellator.getInstance().getWorldRenderer();
			GL11.glDisable(3553);
			float var17 = 32F;
			GL11.glEnable(3042);
			GL11.glEnable(3008);
			GL11.glBlendFunc(770, 771);
			render.begin(7, new VertexFormat());
			render.color(0.3F, 0.3F, 0.3F, 0.5F);
			GuiTool.addVertexWithUV(left, bottom, 0.0D, 0.0D, 1.0D,render);
			GuiTool.addVertexWithUV(right, bottom, 0.0D, 1.0D, 1.0D,render);
			GuiTool.addVertexWithUV(right, top, 0.0D, 1.0D, 0.0D,render);
			GuiTool.addVertexWithUV(left, top, 0.0D, 0.0D, 0.0D,render);
			Tessellator.getInstance().draw();
			GL11.glEnable(3553);
			int var10 = (top + 4) - (int) scrollDistance;
			if (field_27262_q)
				func_27260_a(boxRight, var10, Tessellator.getInstance());
			int var19;
			for (int var11 = 0; var11 < listLength; var11++) {
				var19 = var10 + var11 * slotHeight + field_27261_r;
				int var13 = slotHeight - 4;
				if (var19 > bottom || var19 + var13 < top)
					continue;
				if (field_25123_p && isSelected(var11)) {
					int var14 = boxLeft;
					int var15 = boxRight;
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glDisable(3553);
					render.begin(7, new VertexFormat());

					render.putColor4(8421504);
					GuiTool.addVertexWithUV(var14, var19 + var13 + 2, 0.0D, 0.0D, 1.0D,render);
					GuiTool.addVertexWithUV(var15, var19 + var13 + 2, 0.0D, 1.0D, 1.0D,render);
					GuiTool.addVertexWithUV(var15, var19 - 2, 0.0D, 1.0D, 0.0D,render);
					GuiTool.addVertexWithUV(var14, var19 - 2, 0.0D, 0.0D, 0.0D,render);
					render.putColor4(0);
					GuiTool.addVertexWithUV(var14 + 1, var19 + var13 + 1, 0.0D, 0.0D, 1.0D,render);
					GuiTool.addVertexWithUV(var15 - 1, var19 + var13 + 1, 0.0D, 1.0D, 1.0D,render);
					GuiTool.addVertexWithUV(var15 - 1, var19 - 1, 0.0D, 1.0D, 0.0D,render);
					GuiTool.addVertexWithUV(var14 + 1, var19 - 1, 0.0D, 0.0D, 0.0D,render);
					Tessellator.getInstance().draw();
					GL11.glEnable(3553);
				}
				drawSlot(var11, boxRight, var19, getSize() ,Tessellator.getInstance());
			}

			GL11.glDisable(2929);
			byte var20 = 4;
			overlayBackground(0, top, 255, 255);
			overlayBackground(bottom, listHeight, 255, 255);
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			GL11.glDisable(3008);
			GL11.glShadeModel(7425);
			GL11.glDisable(3553);
			render.begin(7, new VertexFormat());
			render.color(0, 0, 0, 0);
			GuiTool.addVertexWithUV(left, top + var20, 0.0D, 0.0D, 1.0D,render);
			GuiTool.addVertexWithUV(right, top + var20, 0.0D, 1.0D, 1.0D,render);
			render.color(0, 0, 0, 1);
			GuiTool.addVertexWithUV(right, top, 0.0D, 1.0D, 0.0D,render);
			GuiTool.addVertexWithUV(left, top, 0.0D, 0.0D, 0.0D,render);
			Tessellator.getInstance().draw();
			render.begin(7, new VertexFormat());
			render.color(0, 0, 0, 1);
			GuiTool.addVertexWithUV(left, bottom, 0.0D, 0.0D, 1.0D,render);
			GuiTool.addVertexWithUV(right, bottom, 0.0D, 1.0D, 1.0D,render);
			render.color(0, 0, 0, 0);
			GuiTool.addVertexWithUV(right, bottom - var20, 0.0D, 1.0D, 0.0D,render);
			GuiTool.addVertexWithUV(left, bottom - var20, 0.0D, 0.0D, 0.0D,render);
			Tessellator.getInstance().draw();
			var19 = getContentHeight() - (bottom - top - 4);
			if (var19 > 0) {
				int var13 = ((bottom - top) * (bottom - top)) / getContentHeight();
				if (var13 < 32)
					var13 = 32;
				if (var13 > bottom - top - 8)
					var13 = bottom - top - 8;
				int var14 = ((int) scrollDistance * (bottom - top - var13)) / var19 + top;
				if (var14 < top)
					var14 = top;
				render.begin(7, new VertexFormat());
				render.color(0, 0, 0, 1);
				GuiTool.addVertexWithUV(scrollBarXStart, bottom, 0.0D, 0.0D, 1.0D,render);
				GuiTool.addVertexWithUV(scrollBarXEnd, bottom, 0.0D, 1.0D, 1.0D,render);
				GuiTool.addVertexWithUV(scrollBarXEnd, top, 0.0D, 1.0D, 0.0D,render);
				GuiTool.addVertexWithUV(scrollBarXStart, top, 0.0D, 0.0D, 0.0D,render);
				Tessellator.getInstance().draw();
				render.begin(7, new VertexFormat());
				render.color((float)(128/255), (float)(128/255), (float)(128/255), 1.0f);
				GuiTool.addVertexWithUV(scrollBarXStart, var14 + var13, 0.0D, 0.0D, 1.0D,render);
				GuiTool.addVertexWithUV(scrollBarXEnd, var14 + var13, 0.0D, 1.0D, 1.0D,render);
				GuiTool.addVertexWithUV(scrollBarXEnd, var14, 0.0D, 1.0D, 0.0D,render);
				GuiTool.addVertexWithUV(scrollBarXStart, var14, 0.0D, 0.0D, 0.0D,render);
				Tessellator.getInstance().draw();
				render.begin(7, new VertexFormat());
				render.color((float)(192/255), (float)(192/255), (float)(192/255), 1.0f);
				GuiTool.addVertexWithUV(scrollBarXStart, (var14 + var13) - 1, 0.0D, 0.0D, 1.0D,render);
				GuiTool.addVertexWithUV(scrollBarXEnd - 1, (var14 + var13) - 1, 0.0D, 1.0D, 1.0D,render);
				GuiTool.addVertexWithUV(scrollBarXEnd - 1, var14, 0.0D, 1.0D, 0.0D,render);
				GuiTool.addVertexWithUV(scrollBarXStart, var14, 0.0D, 0.0D, 0.0D,render);
				Tessellator.getInstance().draw();
			}
			func_27257_b(mouseX, mouseY);
			GL11.glEnable(3553);
			GL11.glShadeModel(7424);
			GL11.glEnable(3008);
			GL11.glDisable(3042);
			GL11.glDisable(3089);
		} catch (Throwable ex) {
		}
	}

	private void overlayBackground(int i, int j, int k, int l) {
	}
}
