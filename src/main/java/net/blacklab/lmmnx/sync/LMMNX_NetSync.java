package net.blacklab.lmmnx.sync;

import littleMaidMobX.LMM_EntityLittleMaid;
import littleMaidMobX.LMM_LittleMaidMobNX;
import mmmlibx.lib.MMM_Helper;

public class LMMNX_NetSync {
	
	public static final byte LMMNX_Sync = (byte) 0x84;
	
	public static final byte LMMNX_Sync_UB_Armor   = (byte) 0x00;
	public static final byte LMMNX_Sync_UB_Swim    = (byte) 0x01;
	public static final byte LMMNX_Sync_UB_Freedom = (byte) 0x02;
	//クライアントのみ
	public static final byte LMMNX_Sync_UB_RequestParamRecall = (byte) 0x03;
	
	// サーバがテクスチャ設定を受信(C->S)
	public static final byte LMMNX_Sync_String_MT_RequestChangeRender   = (byte) 0x10;
	public static final byte LMMNX_Sync_String_AT_RequestChangeRender   = (byte) 0x11;
	// サーバから保存されたテクスチャ情報を返す(S->C)
	public static final byte LMMNX_Sync_String_MT_RecallParam = (byte) 0x12;
	public static final byte LMMNX_Sync_String_AT_RecallParam = (byte) 0x13;
	
	public static void onPayLoad(LMM_EntityLittleMaid pMaid, byte[] pData){
		if(pData==null) return;
		if((pData[5] & 0x10) == 0x10){
			onPayLoad(pMaid, pData[5], MMM_Helper.getStr(pData, 6));
			return;
		}
		if((pData[5] & 0x00)==0x00){
			// byte
//			if(pData.length!=7) throw new IndexOutOfBoundsException("Data has wrong size");
			onPayLoad(pMaid, pData[5], pData[6]);
		}
	}
	
	public static void onPayLoad(LMM_EntityLittleMaid pMaid, byte pMode, byte pData){
		switch (pMode) {
		case LMMNX_Sync_UB_Armor:
			LMM_LittleMaidMobNX.Debug("SYNC ARMOR");
			pMaid.setMaidArmorVisible(pData);
			break;
		case LMMNX_Sync_UB_Swim:
			pMaid.setSwimming(pData==(byte)1);
			break;
		case LMMNX_Sync_UB_Freedom:
			pMaid.setFreedom(pData==(byte)1);
			break;
		case LMMNX_Sync_UB_RequestParamRecall :
			pMaid.syncMaidArmorVisible();
			pMaid.recallRenderParamTextureName(pMaid.textureModelNameForClient, pMaid.textureArmorNameForClient);
			break;
		}
	}
	
	public static void onPayLoad(LMM_EntityLittleMaid pMaid, byte pMode, String pString){
		switch (pMode) {
		case LMMNX_Sync_String_MT_RequestChangeRender:
			pMaid.recallRenderParamTextureName(pString, pMaid.textureArmorNameForClient);
			break;
		case LMMNX_Sync_String_AT_RequestChangeRender:
			pMaid.recallRenderParamTextureName(pMaid.textureModelNameForClient, pString);
			break;
		case LMMNX_Sync_String_MT_RecallParam:
			pMaid.returnedRecallParam(pString, pMaid.textureArmorNameForClient);
			break;
		case LMMNX_Sync_String_AT_RecallParam:
			pMaid.returnedRecallParam(pMaid.textureModelNameForClient, pString);
			break;
		}
	}
	
}
