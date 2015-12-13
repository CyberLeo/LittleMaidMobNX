package net.blacklab.lmmnx.client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import littleMaidMobX.LMM_LittleMaidMobNX;
import mmmlibx.lib.FileManager;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.ImmutableSet;

/**
 * サウンドパック用
 */
public class LMM_SoundResourcePack implements IResourcePack {
	
	public LMM_SoundResourcePack() {
	}

	@Override
	public InputStream getInputStream(ResourceLocation par1ResourceLocation) throws IOException {
		LMM_LittleMaidMobNX.Debug("GET STREAM %s", par1ResourceLocation.getResourcePath());
		InputStream inputstream = getResourceStream(par1ResourceLocation);
//		if (inputstream != null) {
		return inputstream;
//		} else {
//			throw new FileNotFoundException(par1ResourceLocation.getResourcePath());
//		}
	}

	private InputStream getResourceStream(ResourceLocation resource) {
		InputStream lis = null;
		if (resource.getResourcePath().endsWith("sounds.json")) {
			return FileManager.COMMON_CLASS_LOADER.getResourceAsStream("LittleMaidMobNX/sounds.json");
		}
		if (resource.getResourcePath().endsWith(".ogg")) {
			lis = FileManager.COMMON_CLASS_LOADER.getResourceAsStream(decodePathGetPath(resource));
		}
		return lis;
	}
	
	@Override
	public boolean resourceExists(ResourceLocation resource) {
		LMM_LittleMaidMobNX.Debug("RESOURCE CHECK %s", resource.getResourcePath());
		if (resource.getResourcePath().endsWith("sounds.json")) {
			return true;
		}
		if (resource.getResourcePath().endsWith(".ogg")) {
			String f = decodePathGetName(resource);
			return LMMNX_SoundRegistry.isSoundNameRegistered(f) ? LMMNX_SoundRegistry.getPathListFromRegisteredName(f)!=null : false;
		}
		return false;
	}
	
	private String decodePathSplicePathStr(ResourceLocation rl) {
		String path = rl.getResourcePath();
		Pattern pattern = Pattern.compile("^/*?sounds/(.+)\\.ogg");
		Matcher matcher = pattern.matcher(path);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
	
	private String decodePathGetName(ResourceLocation rl) {
		String f = decodePathSplicePathStr(rl);
		String[] gs = f.split("//");
		return gs[0];
	}
	
	private String decodePathGetPath(ResourceLocation rl) {
		String f = decodePathSplicePathStr(rl);
		String[] gs = f.split("//");
		return gs.length>1 ? gs[1] : null;
	}

	@SuppressWarnings("rawtypes")
	public static final Set lmmxResourceDomains = ImmutableSet.of("lmmnx");

	@Override
	@SuppressWarnings("rawtypes")
	public Set getResourceDomains() {
		return lmmxResourceDomains;
	}

	@Override
	public IMetadataSection getPackMetadata(IMetadataSerializer par1MetadataSerializer, String par2Str)
	{ //throws IOException {
		return null;
	}

	// 未使用
	@Override
	public BufferedImage getPackImage() {// throws IOException {
		try {
			return ImageIO.read(DefaultResourcePack.class.getResourceAsStream("/"
					+ (new ResourceLocation("pack.png")).getResourcePath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getPackName() {
		return "LMMNXSound";
	}

}
