package littleMaidMobX;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import mmmlibx.lib.Client;
import mmmlibx.lib.gui.GuiButtonNextPage;
import net.blacklab.lmmnx.client.GuiButtonArmorToggle;
import net.blacklab.lmmnx.client.GuiButtonFreedomToggle;
import net.blacklab.lmmnx.client.GuiButtonSwimToggle;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class LMM_GuiInventory extends GuiContainer {
	// Field
	private Random rand;
	private IInventory upperChestInventory;
	private IInventory lowerChestInventory;
	private int ySizebk;
	private int updateCounter;
	public LMM_EntityLittleMaid entitylittlemaid;

	public GuiButtonNextPage txbutton[] = new GuiButtonNextPage[4];
	public GuiButton selectbutton;
	public GuiButtonArmorToggle visarmorbutton[] = new GuiButtonArmorToggle[4];
	public GuiButtonSwimToggle swimbutton;
	public GuiButtonFreedomToggle frdmbutton;
	public boolean isChangeTexture;

	protected static final ResourceLocation fguiTex =
			new ResourceLocation(LMM_LittleMaidMobNX.DOMAIN, "textures/gui/container/littlemaidinventory.png");

	// Method
	public LMM_GuiInventory(EntityPlayer pPlayer, LMM_EntityLittleMaid elmaid) {
		super(new LMM_ContainerInventory(pPlayer.inventory, elmaid));
		rand = new Random();
		upperChestInventory = pPlayer.inventory;
		lowerChestInventory = elmaid.maidInventory;
		allowUserInput = false;
		updateCounter = 0;
		ySizebk = ySize;
		ySize = 207;
		isChangeTexture = true;

		entitylittlemaid = elmaid;
		// entitylittlemaid.setOpenInventory(true);
	}

	@Override
	public void initGui() {
		super.initGui();
		if (!entitylittlemaid.getActivePotionEffects().isEmpty()) {
			guiLeft = 160 + (width - xSize - 200) / 2;
		}
		buttonList.add(txbutton[0] = new GuiButtonNextPage(100, guiLeft + 25, guiTop + 7, false));
		buttonList.add(txbutton[1] = new GuiButtonNextPage(101, guiLeft + 55, guiTop + 7, true));
		buttonList.add(txbutton[2] = new GuiButtonNextPage(110, guiLeft + 25, guiTop + 47, false));
		buttonList.add(txbutton[3] = new GuiButtonNextPage(111, guiLeft + 55, guiTop + 47, true));
		buttonList.add(selectbutton = new GuiButton(200, guiLeft + 25, guiTop + 25, 53, 17, "select"));
		buttonList.add(visarmorbutton[0] = new GuiButtonArmorToggle  (300, guiLeft     , guiTop - 14, "littleMaidMob.gui.toggle.inner"     , true).setNode(0).setLight(0));
		buttonList.add(visarmorbutton[1] = new GuiButtonArmorToggle  (301, guiLeft + 16, guiTop - 14, "littleMaidMob.gui.toggle.innerlight", true).setNode(0).setLight(1));
		buttonList.add(visarmorbutton[2] = new GuiButtonArmorToggle  (302, guiLeft + 32, guiTop - 14, "littleMaidMob.gui.toggle.outer"     , true).setNode(1).setLight(0));
		buttonList.add(visarmorbutton[3] = new GuiButtonArmorToggle  (303, guiLeft + 48, guiTop - 14, "littleMaidMob.gui.toggle.outerlight", true).setNode(1).setLight(1));
		buttonList.add(frdmbutton        = new GuiButtonFreedomToggle(311, guiLeft + 64, guiTop - 16, "littleMaidMob.gui.toggle.freedom"   , entitylittlemaid.swimmingEnabled, entitylittlemaid));
		buttonList.add(swimbutton        = new GuiButtonSwimToggle   (310, guiLeft + 80, guiTop - 16, "littleMaidMob.gui.toggle.swim"      , entitylittlemaid.swimmingEnabled));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		mc.fontRendererObj.drawString(StatCollector.translateToLocal(
				lowerChestInventory.getName()), 8, 64, 0x404040);
		mc.fontRendererObj.drawString(StatCollector.translateToLocal(
				upperChestInventory.getName()), 8, 114, 0x404040);
		//fontRenderer.drawString(StatCollector.translateToLocal("littleMaidMob.text.Health"), 86, 8, 0x404040);
		//fontRenderer.drawString(StatCollector.translateToLocal("littleMaidMob.text.AP"), 86, 32, 0x404040);
		mc.fontRendererObj.drawString(StatCollector.translateToLocal(
				"littleMaidMob.text.STATUS"), 86, 8, 0x404040);

		mc.fontRendererObj.drawString(StatCollector.translateToLocal(
				"littleMaidMob.mode.".concat(entitylittlemaid.getMaidModeString())), 86, 61, 0x404040);
//	      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		// キャラ
		int lj = 0;
		int lk = 0;
//		      GL11.glEnable(EXTRescaleNormal.GL_RESCALE_NORMAL_EXT);
//	      GL11.glEnable(GL11.GL_COLOR_MATERIAL);
//	      GL11.glPushMatrix();
//	      GL11.glTranslatef(lj + 51, lk + 57, 50F);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.translate(lj + 51, lk + 57, 50F);
		float f1 = 30F;
//	      GL11.glScalef(-f1, f1, f1);
//	      GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		GlStateManager.scale(-f1, f1, f1);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		float f2 = entitylittlemaid.renderYawOffset;
		float f3 = entitylittlemaid.rotationYaw;
		float f4 = entitylittlemaid.rotationYawHead;
		float f5 = entitylittlemaid.rotationPitch;
//	      float f8 = (float) (lj + 51) - xSize_lo;
//	      float f9 = (float) (lk + 75) - 50 - ySize_lo;
		float f8 = guiLeft + 51 - par1;
		float f9 = guiTop + 22 - par2;
//	      GL11.glRotatef(135F, 0.0F, 1.0F, 0.0F);
//	      RenderHelper.enableStandardItemLighting();
//	      GL11.glRotatef(-135F, 0.0F, 1.0F, 0.0F);
//	      GL11.glRotatef(-(float) Math.atan(f9 / 40F) * 20F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(135F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();

		GlStateManager.rotate(-135F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-(float) Math.atan(f9 / 40F) * 20F, 1.0F, 0.0F, 0.0F);
		entitylittlemaid.renderYawOffset = (float) Math.atan(f8 / 40F) * 20F;
		entitylittlemaid.rotationYawHead = entitylittlemaid.rotationYaw = (float) Math.atan(f8 / 40F) * 40F;
		entitylittlemaid.rotationPitch = -(float) Math.atan(f9 / 40F) * 20F;
//		GL11.glTranslatef(0.0F, 0.0F/*entitylittlemaid.yOffset*/, 0.0F);
//		Minecraft.getMinecraft().getRenderManager().playerViewY = 180F;
		GlStateManager.translate(0.0F, 0.0F/*entitylittlemaid.yOffset*/, 0.0F);
		Minecraft.getMinecraft().getRenderManager().setPlayerViewY(180F);
		Minecraft.getMinecraft().getRenderManager().renderEntityWithPosYaw(entitylittlemaid, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		entitylittlemaid.renderYawOffset = f2;
		entitylittlemaid.rotationYaw = f3;
		entitylittlemaid.rotationYawHead = f4;
		entitylittlemaid.rotationPitch = f5;
//	      GL11.glPopMatrix();
//	      RenderHelper.disableStandardItemLighting();
//	      GL11.glDisable(EXTRescaleNormal.GL_RESCALE_NORMAL_EXT);
//	      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F / 1.0F, 240F / 1.0F);
//	      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();

		Iterator iterator = buttonList.iterator();
		while(iterator.hasNext()){
			GuiButton gButton = (GuiButton) iterator.next();
			gButton.drawButtonForegroundLayer(par1-guiLeft, par2-guiTop);
		}

//		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableColorMaterial();
		GlStateManager.disableRescaleNormal();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		// 背景
		ResourceLocation lrl = entitylittlemaid.textureData.getGUITexture();
		if (lrl == null) {
			lrl = fguiTex;
		}
		Client.setTexture(lrl);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int lj = guiLeft;
		int lk = guiTop;
		drawTexturedModalRect(lj, lk, 0, 0, xSize, ySize);

		// PotionEffect
		displayDebuffEffects();

		// LP/AP
		drawHeathArmor(0, 0);
/*
		MMM_Client.setTexture(field_110324_m);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		boolean flag1 = (entitylittlemaid.hurtResistantTime / 3) % 2 == 1;
		if (entitylittlemaid.hurtResistantTime < 10) {
			flag1 = false;
		}
		int i1 = MathHelper.ceiling_float_int(entitylittlemaid.func_110138_aP());
		int j1 = MathHelper.ceiling_float_int(entitylittlemaid.prevHealth);
		rand.setSeed(updateCounter * 0x4c627);

		// AP
		int k1 = entitylittlemaid.getTotalArmorValue();
		for (int j2 = 0; j2 < 10; j2++) {
			int k3 = 43 + lk;
			if (k1 > 0) {
				// int j5 = j + 158 - j2 * 8;
				int j5 = lj + 86 + j2 * 8;
				if (j2 * 2 + 1 < k1) {
					drawTexturedModalRect(j5, k3, 34, 9, 9, 9);
				}
				if (j2 * 2 + 1 == k1) {
					drawTexturedModalRect(j5, k3, 25, 9, 9, 9);
				}
				if (j2 * 2 + 1 > k1) {
					drawTexturedModalRect(j5, k3, 16, 9, 9, 9);
				}
			}

			// LP
			int k5 = 0;
			if (flag1) {
				k5 = 1;
			}
			int i6 = lj + 86 + j2 * 8;
			k3 = 19 + lk;
			if (i1 <= 4) {
				k3 += rand.nextInt(2);
			}
			drawTexturedModalRect(i6, k3, 16 + k5 * 9, 0, 9, 9);
			if (flag1) {
				if (j2 * 2 + 1 < j1) {
					drawTexturedModalRect(i6, k3, 70, 0, 9, 9);
				}
				if (j2 * 2 + 1 == j1) {
					drawTexturedModalRect(i6, k3, 79, 0, 9, 9);
				}
			}
			if (j2 * 2 + 1 < i1) {
				drawTexturedModalRect(i6, k3, 52, 0, 9, 9);
			}
			if (j2 * 2 + 1 == i1) {
				drawTexturedModalRect(i6, k3, 61, 0, 9, 9);
			}
		}
*/
	}

	protected void drawHeathArmor(int par1, int par2) {
		boolean var3 = entitylittlemaid.hurtResistantTime / 3 % 2 == 1;

		if (entitylittlemaid.hurtResistantTime < 10) {
			var3 = false;
		}

		Client.setTexture(icons);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		int lhealth = MathHelper.ceiling_float_int(entitylittlemaid.getHealth());
		int llasthealth = lhealth + MathHelper.ceiling_float_int(entitylittlemaid.getLastDamage());
		this.rand.setSeed(updateCounter * 312871);
		//		FoodStats var7 = entitylittlemaid.getFoodStats();
//		int var8 = var7.getFoodLevel();
//		int var9 = var7.getPrevFoodLevel();
		IAttributeInstance var10 = entitylittlemaid.getEntityAttribute(SharedMonsterAttributes.maxHealth);
		float var14 = (float) var10.getAttributeValue();
		float var15 = entitylittlemaid.getAbsorptionAmount();
		int var16 = MathHelper.ceiling_float_int((var14 + var15) / 2.0F / 10.0F);
		int var17 = Math.max(10 - (var16 - 2), 3);
		float var19 = var15;
		int var21 = -1;

		if (entitylittlemaid.isPotionActive(Potion.regeneration)) {
			var21 = updateCounter % MathHelper.ceiling_float_int(var14 + 5.0F);
		}

		int ldrawx;
		int ldrawy;

		// AP
		int larmor = entitylittlemaid.getTotalArmorValue();
		ldrawy = guiTop + 36;
		for (int li = 0; li < 10; ++li) {
			if (larmor > 0) {
				ldrawx = guiLeft + li * 8 + 86;

				if (li * 2 + 1 < larmor) {
					this.drawTexturedModalRect(ldrawx, ldrawy, 34, 9, 9, 9);
				}
				if (li * 2 + 1 == larmor) {
					this.drawTexturedModalRect(ldrawx, ldrawy, 25, 9, 9, 9);
				}
				if (li * 2 + 1 > larmor) {
					this.drawTexturedModalRect(ldrawx, ldrawy, 16, 9, 9, 9);
				}
			}
		}

		// LP
		for (int li = MathHelper.ceiling_float_int((var14 + var15) / 2.0F) - 1; li >= 0; --li) {
			int var23 = 16;
			if (entitylittlemaid.isPotionActive(Potion.poison)) {
				var23 += 36;
			} else if (entitylittlemaid.isPotionActive(Potion.wither)) {
				var23 += 72;
			}

			int var25 = MathHelper.ceiling_float_int((li + 1) / 10.0F);
			ldrawx = guiLeft + li % 10 * 8 + 86;
			ldrawy = guiTop + 7 + var25 * var17;

			if (lhealth <= 4) {
				ldrawy += this.rand.nextInt(2);
			}
			if (li == var21) {
				ldrawy -= 2;
			}

			this.drawTexturedModalRect(ldrawx, ldrawy, var3 ? 25 : 16, 0, 9, 9);

			if (var3) {
				if (li * 2 + 1 < llasthealth) {
					this.drawTexturedModalRect(ldrawx, ldrawy, var23 + 54, 0, 9, 9);
				}
				if (li * 2 + 1 == llasthealth) {
					this.drawTexturedModalRect(ldrawx, ldrawy, var23 + 63, 0, 9, 9);
				}
			}

			if (var19 > 0.0F) {
				if (var19 == var15 && var15 % 2.0F == 1.0F) {
					this.drawTexturedModalRect(ldrawx, ldrawy, var23 + 153, 0, 9, 9);
				} else {
					this.drawTexturedModalRect(ldrawx, ldrawy, var23 + 144, 0, 9, 9);
				}

				var19 -= 2.0F;
			} else {
				if (li * 2 + 1 < lhealth) {
					this.drawTexturedModalRect(ldrawx, ldrawy, var23 + 36, 0, 9, 9);
				}
				if (li * 2 + 1 == lhealth) {
					this.drawTexturedModalRect(ldrawx, ldrawy, var23 + 45, 0, 9, 9);
				}
			}
		}

		// Air
		ldrawy = guiTop + 46;
		if (entitylittlemaid.isInsideOfMaterial(Material.water)) {
			int var23 = entitylittlemaid.getAir();
			int var35 = MathHelper.ceiling_double_int((var23 - 2) * 10.0D / 300.0D);
			int var25 = MathHelper.ceiling_double_int(var23 * 10.0D / 300.0D) - var35;

			for (int var26 = 0; var26 < var35 + var25; ++var26) {
				ldrawx = guiLeft + var26 * 8 + 86;
				if (var26 < var35) {
					this.drawTexturedModalRect(ldrawx, ldrawy, 16, 18, 9, 9);
				} else {
					this.drawTexturedModalRect(ldrawx, ldrawy, 25, 18, 9, 9);
				}
			}
		}

	}

	@Override
	public void drawScreen(int i, int j, float f) {
		super.drawScreen(i, j, f);
		int ii = i - guiLeft;
		int jj = j - guiTop;
		for(int cnt=0;cnt<4;cnt++){
			visarmorbutton[cnt].visible = true;
			visarmorbutton[cnt].toggle = entitylittlemaid.isArmorVisible(cnt);
		}
		swimbutton.visible = true;
		swimbutton.toggle = entitylittlemaid.swimmingEnabled;
		frdmbutton.visible = true;
		frdmbutton.toggle = entitylittlemaid.isFreedom();
		if (ii > 25 && ii < 78 && jj > 7 && jj < 60) {
			// ボタンの表示
			txbutton[0].visible = true;
			txbutton[1].visible = true;
			txbutton[2].visible = true;
			txbutton[3].visible = true;
			selectbutton.visible = true;

			// テクスチャ名称の表示
			GL11.glPushMatrix();
			GL11.glTranslatef(i - ii, j - jj, 0.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();

			if (entitylittlemaid.textureData.textureBox[0] != null) {
				String ls1 = entitylittlemaid.textureData.getTextureName(0);
				String ls2 = entitylittlemaid.textureData.getTextureName(1);
				int ltw1 = this.mc.fontRendererObj.getStringWidth(ls1);
				int ltw2 = this.mc.fontRendererObj.getStringWidth(ls2);
				int ltwmax = (ltw1 > ltw2) ? ltw1 : ltw2;
				int lbx = 52 - ltwmax / 2;
				int lby = 68;
				int lcolor;
				lcolor = jj < 20 ? 0xc0882222 : 0xc0000000;
				drawGradientRect(lbx - 3, lby - 4, lbx + ltwmax + 3, lby + 8, lcolor, lcolor);
				drawString(this.mc.fontRendererObj, ls1, 52 - ltw1 / 2, lby - 2, -1);
				lcolor = jj > 46 ? 0xc0882222 : 0xc0000000;
				drawGradientRect(lbx - 3, lby + 8, lbx + ltwmax + 3, lby + 16 + 4, lcolor, lcolor);
				drawString(this.mc.fontRendererObj, ls2, 52 - ltw2 / 2, lby + 10, -1);
			}
			GL11.glPopMatrix();
//			RenderHelper.enableStandardItemLighting();
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
		} else {
			txbutton[0].visible = false;
			txbutton[1].visible = false;
			txbutton[2].visible = false;
			txbutton[3].visible = false;
			selectbutton.visible = false;
		}

	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		updateCounter++;
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		try {
			super.mouseClicked(i, j, k);
		} catch (IOException e) {
		}
/*
		// 26,8-77,59
		int ii = i - guiLeft;
		int jj = j - guiTop;

		// TODO:メイドアセンブル画面を作る
		if (ii > 25 && ii < 78 && jj > 7 && jj < 60) {
			// 伽羅表示領域
			if (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)) {
				// Shift+で逆周り
				LMM_Client.setPrevTexturePackege(entitylittlemaid, k);
			} else {
				LMM_Client.setNextTexturePackege(entitylittlemaid, k);
			}
			LMM_Client.setTextureValue(entitylittlemaid);
		}
*/
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		switch (par1GuiButton.id) {
		case 100 :
			entitylittlemaid.setNextTexturePackege(0);
			entitylittlemaid.setTextureNames();
			entitylittlemaid.requestChangeRenderParamTextureName();
			break;
		case 101 :
			entitylittlemaid.setPrevTexturePackege(0);
			entitylittlemaid.setTextureNames();
			entitylittlemaid.requestChangeRenderParamTextureName();
			break;
		case 110 :
			entitylittlemaid.setNextTexturePackege(1);
			entitylittlemaid.setTextureNames();
			entitylittlemaid.requestChangeRenderParamTextureName();
			break;
		case 111 :
			entitylittlemaid.setPrevTexturePackege(1);
			entitylittlemaid.setTextureNames();
			entitylittlemaid.requestChangeRenderParamTextureName();
			break;
		case 200 :
			int ldye = 0;
			if (mc.thePlayer.capabilities.isCreativeMode) {
				ldye = 0xffff;
			} else {
				for (ItemStack lis : mc.thePlayer.inventory.mainInventory) {
					if (lis != null && lis.getItem() == Items.dye) {
						ldye |= (1 << (15 - lis.getItemDamage()));
					}
				}
			}
			isChangeTexture = false;
			mc.displayGuiScreen(new LMM_GuiTextureSelect(this, entitylittlemaid, ldye, true));
			break;
		case 300 :
			visarmorbutton[0].toggle=!visarmorbutton[0].toggle;
			setArmorVisible();
			break;
		case 301 :
			visarmorbutton[1].toggle=!visarmorbutton[1].toggle;
			setArmorVisible();
			break;
		case 302 :
			visarmorbutton[2].toggle=!visarmorbutton[2].toggle;
			setArmorVisible();
			break;
		case 303 :
			visarmorbutton[3].toggle=!visarmorbutton[3].toggle;
			setArmorVisible();
			break;
		case 310 :
			swimbutton.toggle=!swimbutton.toggle;
			entitylittlemaid.setSwimming(swimbutton.toggle);
			entitylittlemaid.syncSwimming();
			break;
		case 311 :
			frdmbutton.toggle=!frdmbutton.toggle;
			entitylittlemaid.requestChangeFreedom(frdmbutton.toggle);
			entitylittlemaid.handleHealthUpdate((byte) (frdmbutton.toggle?12:13));
			break;
		}
	}

	protected void setArmorVisible() {
		entitylittlemaid.setMaidArmorVisible(visarmorbutton[0].toggle, visarmorbutton[1].toggle, visarmorbutton[2].toggle, visarmorbutton[3].toggle);
		entitylittlemaid.syncMaidArmorVisible();
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		 entitylittlemaid.onGuiClosed();
		if (isChangeTexture) {
			entitylittlemaid.sendTextureToServer();
		}
	}

	private void displayDebuffEffects() {
		// ポーションエフェクトの表示
		int lx = guiLeft - 124;
		int ly = guiTop;
		Collection collection = entitylittlemaid.getActivePotionEffects();
		if (collection.isEmpty()) {
			return;
		}
		int lh = 33;
		if (collection.size() > 5) {
			lh = 132 / (collection.size() - 1);
		}
		for (Iterator iterator = entitylittlemaid.getActivePotionEffects().iterator(); iterator.hasNext();) {
			PotionEffect potioneffect = (PotionEffect) iterator.next();
			Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			Client.setTexture(new ResourceLocation("textures/gui/container/inventory.png"));
			drawTexturedModalRect(lx, ly, 0, ySizebk, 140, 32);

			if (potion.hasStatusIcon()) {
				int i1 = potion.getStatusIconIndex();
				drawTexturedModalRect(lx + 6, ly + 7, 0 + (i1 % 8) * 18,
						ySizebk + 32 + (i1 / 8) * 18, 18, 18);
			}
			String ls = StatCollector.translateToLocal(potion.getName());
			if (potioneffect.getAmplifier() > 0) {
				ls = (new StringBuilder()).append(ls).append(" ")
						.append(StatCollector.translateToLocal((new StringBuilder())
								.append("potion.potency.")
								.append(potioneffect.getAmplifier())
								.toString())).toString();
			}
			mc.fontRendererObj.drawString(ls, lx + 10 + 18, ly + 6, 0xffffff);
			String s1 = Potion.getDurationString(potioneffect);
			mc.fontRendererObj.drawString(s1, lx + 10 + 18, ly + 6 + 10, 0x7f7f7f);
			ly += lh;
		}
	}

}
