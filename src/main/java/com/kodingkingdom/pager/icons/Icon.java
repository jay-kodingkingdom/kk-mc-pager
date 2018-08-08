package com.kodingkingdom.pager.icons;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.kodingkingdom.pager.PagerPlugin;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;


public class Icon {

	public static boolean like (ItemStack i, ItemStack j) {
		if (i == j)
			return true;
		else if (i == null || j == null)
			return false;
		
		List<String> lore_meta = i .getItemMeta() .getLore();

		String lore = lore_meta != null && lore_meta.size() > 0 ? i .getItemMeta() .getLore() .get(0) : "";

		return i .getData() .getItemType() .equals(j .getData() .getItemType())
			&& (i .getItemMeta() == null && j .getItemMeta() != null)
			&& i .getItemMeta() .getDisplayName() .equals(j .getItemMeta() .getDisplayName())
			&& lore.equals(j .getItemMeta() .getLore() .get(0));
	}
	public static boolean is (ItemStack i, ItemStack j) {
		if (i == j)
			return true;
		else if (i == null || j == null)
			return false;
		
		List<String> lore_meta = i .getItemMeta() .getLore();

		String lore = lore_meta != null && lore_meta.size() > 0 ? i .getItemMeta() .getLore() .get(0) : "";
		String gen = lore_meta != null && lore_meta.size() > 1 ? i .getItemMeta() .getLore() .get(1) : "";
		
		return i .getData() .getItemType() .equals(j .getData() .getItemType())
			&& (i .getItemMeta() == null && j .getItemMeta() != null)
			&& i .getItemMeta() .getDisplayName() .equals(j .getItemMeta() .getDisplayName())
			&& lore.equals(j .getItemMeta() .getLore() .get(0))
			&& gen.equals(j .getItemMeta() .getLore() .get(1));
	}
	public static boolean same (ItemStack i, ItemStack j) {
		if (i == j)
			return true;
		else if (i == null || j == null)
			return false;
		
		List<String> lore_meta = i .getItemMeta() .getLore();

		String lore = lore_meta != null && lore_meta.size() > 0 ? lore_meta .get(0) : "";
		String gen = lore_meta != null && lore_meta.size() > 1 ? lore_meta .get(1) : "";
		String id = lore_meta != null && lore_meta.size() > 2 ? lore_meta .get(2) : "";

		return i .getData() .getItemType() .equals(j .getData() .getItemType())
			&& (i .getItemMeta() == null && j .getItemMeta() != null)
			&& i .getItemMeta() .getDisplayName() .equals(j .getItemMeta() .getDisplayName())
			&& lore.equals(j .getItemMeta() .getLore() .get(0))
			&& gen.equals(j .getItemMeta() .getLore() .get(1))
			&& id.equals(j .getItemMeta() .getLore() .get(2));
	}
	
	
	
	ItemStack iconStack;

	private static HashMap<Material, Icon> materialIconMap=new HashMap<Material, Icon> ();
	private static HashMap<Material, HashMap<Short,Icon>> magicValueIconMap=new HashMap<Material, HashMap<Short,Icon>> ();
	
	public ItemStack asIcon(){
		return iconStack.clone();}
	public Icon withName(String name){
		ItemMeta iconInfo = iconStack.getItemMeta();
		iconInfo.setDisplayName(name);
		iconStack.setItemMeta(iconInfo);
		return this;}
	public Icon withCaption(String caption){
		ItemMeta iconInfo = iconStack.getItemMeta();
		List<String> captionList = Arrays.asList(caption.split("\n")); 
		iconInfo.setLore(captionList);
		iconStack.setItemMeta(iconInfo);
		return this;}
	private Icon withPlayer(Player player){
		withTexture(getTexture(player), player.getUniqueId());
		/*SkullMeta iconInfo = (SkullMeta)iconStack.getItemMeta();

		EduCraftPlugin.debug("user is "+user);
		EduCraftPlugin.debug("user id is "+user.getId());
		EduCraftPlugin.debug("username is "+user.getName());
		
		iconInfo.setOwner(user.getName());
		
		iconStack.setItemMeta(iconInfo);*/
		return this;}
	private Icon withTexture(Texture texture){
		return withTexture(texture, UUID.randomUUID());}
	private Icon withTexture(Texture texture, UUID id){
		
		ItemMeta iconInfo = iconStack.getItemMeta();
		
		Class<?> headMetaClass=iconInfo.getClass();
		
		if( ! setFieldValue(headMetaClass,iconInfo,"profile",
				makeGameProfile(texture, id),
				"Unable to inject profile")){
			return null;}
		
		iconStack.setItemMeta(iconInfo);
		return this;}
	
	public static final Icon Null = makeIcon((ItemStack)null);
	
	private Icon(){}
	private Icon(Material material, short magicValue){
		if (magicValue>0) {
			if (!magicValueIconMap.containsKey(material)) magicValueIconMap.put(material, new HashMap<Short,Icon>());
			magicValueIconMap.get(material).put(magicValue, this);}
		else if (magicValue<=0) {
			if (!magicValueIconMap.containsKey(material)) magicValueIconMap.put(material, new HashMap<Short,Icon>());
			magicValueIconMap.get(material).put((short) -magicValue, this);
			materialIconMap.put(material, this);}
		else {
			materialIconMap.put(material, this);}}

	public static Icon makeIcon(String name){
		//EduCraftPlugin.debug("icon out of "+name);
		if (name.equals("")) return Null;
		try {
			return makeIcon(Textures.valueOf(getTextureName(name)).texture).withName(name).withCaption(name);} 
		catch(Exception e){
			return makeIcon(Material.ENCHANTED_BOOK).withName(name).withCaption(name);}}
	
	private static String getTextureName(String name){
		
		if (Stream.of(Textures.values())
				.map(texture->texture.name())
				.collect(Collectors.toCollection(HashSet::new))
				.contains(name)) return name;
		
		if (name.equals("")) return "";
				
		String textureName = name.substring(0, 1).toUpperCase();

		if (textureName.equals(".")) return "Dot";
		if (textureName.equals("/")) return "Slash";
		if (textureName.equals("?")) return "Question";
		if (textureName.equals("!")) return "Exclamation";
		
		return textureName;}
	private static Icon makeTexturedIcon(){
		return makeIcon(Material.SKULL_ITEM,(short)3);}
	public static Icon makeIcon(Texture texture){
		return makeTexturedIcon().withTexture(texture);}
	public static Icon makeIcon(Textures textures){
		return makeTexturedIcon().withTexture(textures.texture);}

	private static HashMap<UUID,Icon> userIconMap=new HashMap<UUID,Icon> ();
	private static Icon makeIcon(ItemStack IconStack){
		Icon icon = new Icon(); 
		icon.iconStack=IconStack;
		return icon;}
	private static Icon makeIcon(Icon Icon){
		return makeIcon(Icon.iconStack.clone());}
	public static Icon makeIcon(Player player){
		if (!userIconMap.containsKey(player.getUniqueId())) userIconMap.put(player.getUniqueId(),makeTexturedIcon().withPlayer(player).withCaption(player.getName()).withName(player.getName()));
		return makeIcon(userIconMap.get(player.getUniqueId()));}
			
	public static Icon makeIcon(Material material){
		return makeIcon(new ItemStack(material, 1));}
	public static Icon makeIcon(Material material, short magicValue){
		return makeIcon(new ItemStack(material, 1, magicValue));}
	
	public enum Textures{
		set ("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM1OGZiYTJmMWJkYzUxOWJhODVlNTM2NmJhYjhkZWY5YmJlNDA0ZWY2MzljMmUzYzY1NGQxMGExZDhkMyJ9fX0=")

		,blue_crate ("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM5MzNhNmQyNGM4YjQ2Nzk5MTJlNGZmMTllZmE1NmE4Yzk3MGQ2NjRmNmU2ZTRmNmEyZDA2N2Q1YjhiIn19fQ==")
		,wooden_crate ("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzMyYWNiODYxMmQzZTI1OGE4NzUxZGE5M2M5YzY0NzUwMjg2YzdiZWRiYjZkZTQ0MWVkNmVkM2Q2NDg3ZWEifX19")

		,light_gray("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ1ZmQyYzM3MzZhZjRiZDI4MTEyOTY2NjFlMGNiNDliYWIyY2ZhNjVmNWM5ZTU5OGFhNDNiZWJmYTFlYTM2OCJ9fX0=")
		,dark_gray("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGEzZDdhNzA5NzE3ZDFmZmUyNDAyNDQ4ZDg2N2IxYTdmMzJmYjg5NTU2MjFjYzg5NzdiNjFmOWEzY2JjOTUifX19")
		,purple("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTFiOWEwYTZkMWI5OTEyNzk0Mjg5ZWNhMWUyMjRlYWU2ZDc2YTdjYjc1MmNhNjg5ZjFiOTkxY2U5NzBhZGVlIn19fQ==")
		,red("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTMzYTViZmM4YTJhM2ExNTJkNjQ2YTViZWE2OTRhNDI1YWI3OWRiNjk0YjIxNGYxNTZjMzdjNzE4M2FhIn19fQ==")
		,yellow("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDExMzliM2VmMmU0YzQ0YTRjOTgzZjExNGNiZTk0OGQ4YWI1ZDRmODc5YTVjNjY1YmI4MjBlNzM4NmFjMmYifX19")
		,blue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDExMzdiOWJmNDM1YzRiNmI4OGZhZWFmMmU0MWQ4ZmQwNGUxZDk2NjNkNmY2M2VkM2M2OGNjMTZmYzcyNCJ9fX0=")
		
		
		,ArrowUp("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQ4Yjc2OGM2MjM0MzJkZmIyNTlmYjNjMzk3OGU5OGRlYzExMWY3OWRiZDZjZDg4ZjIxMTU1Mzc0YjcwYjNjIn19fQ==")
		,ArrowDown("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRhZGQ3NTVkMDg1MzczNTJiZjdhOTNlM2JiN2RkNGQ3MzMxMjFkMzlmMmZiNjcwNzNjZDQ3MWY1NjExOTRkZCJ9fX0=")
		,ArrowRight("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ==")
		,ArrowLeft("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ViZjkwNzQ5NGE5MzVlOTU1YmZjYWRhYjgxYmVhZmI5MGZiOWJlNDljNzAyNmJhOTdkNzk4ZDVmMWEyMyJ9fX0=")
		,Question("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTE2M2RhZmFjMWQ5MWE4YzkxZGI1NzZjYWFjNzg0MzM2NzkxYTZlMThkOGY3ZjYyNzc4ZmM0N2JmMTQ2YjYifX19")
		,Exclamation("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmE1M2JkZDE1NDU1MzFjOWViYjljNmY4OTViYzU3NjAxMmY2MTgyMGU2ZjQ4OTg4NTk4OGE3ZTg3MDlhM2Y0OCJ9fX0=")
		,Dot("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzMzYWEyNDkxNmM4ODY5NmVlNzFkYjdhYzhjZDMwNmFkNzMwOTZiNWI2ZmZkODY4ZTFjMzg0YjFkNjJjZmIzYyJ9fX0=")
		,Slash("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y5NWQ3YzFiYmYzYWZhMjg1ZDhkOTY3NTdiYjU1NzIyNTlhM2FlODU0ZjUzODlkYzUzMjA3Njk5ZDk0ZmQ4In19fQ==")
		
		/*   Computer Font
		,A("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjc0OGYyMTM1ODhkYmY0NDE1Y2UyNGZlNjZkZTM1MjY4MTZiZjM1ZGY4ZTM5OGY5OGVmZWMyZmIwODk1NmEzIn19fQ==")
		,B("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTBkY2RjYTJhNDVlM2FiZjg4ZDYzYzQxNmZhZWNlYTlhYzI4NTQ4NGJlYmVkZWNiZWJiODA3NzkyMjg0MTdmIn19fQ==")
		,C("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIzNDEwMzgyMWU3Y2M0NWNmZjlmNGNkYWJmMTViYjY3ODhiODI2NTVhYjAzNmYyYTBhMWY5ZDQ3MDEzYmUyZiJ9fX0=")
		,D("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzcyNWMzMmMzMzZmNzVmNzllNjYwYTZmZWViMzc1ZWNkNjFmYzhiY2M1ZTdhN2RlY2Y0ODU1Yzk5NTg5MCJ9fX0=")
		,E("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ4MTE1YjFhZGJiMzljZGJjYmNkMDM0ZTk1NjBkZTdkYWI0MDI1YmI0YmM0M2M0OWI0M2QxNWRkOGE3NyJ9fX0=")
		,F("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2JiNzVhM2VhMzNmYjY2MzZmMmJmYWZlODk3NjM4YmIyZGY5MTJmMjZmNWEyNGI2OTliNTBiMzFiNTQ3YSJ9fX0=")
		,G("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2M0MjNhYjhkZjk3YmYxYjlhOWMxZDBlNDcxNjkwY2Y2NzdlNGZjYTEwYjM3NmM1ZTZjMDc2OTJiNTVkMTQifX19")
		,H("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWU5YWUzZTdlN2NiZDczOTUyNTE4OWE3YWMzYzkxZDI2MWZjMjlkMmVmZWRiNzAyMjY0ZDc0OWFlZDNkOTJhIn19fQ==")
		,I("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM3ODg5Yjk4ZjM1MWMyZGIxMmJmMWZmYTU3ZDA4Y2I2NDYwYzY0OWM5MDIxMzViMTMzZTI2OTYxYiJ9fX0=")
		,J("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmJmYTJmNjVlNTQwZjg3NmNjOTk5NDY2ZDlmNTk2ZWExZjk5MzRmNjI1ODNjY2Y0Njc5YWJmNmIxOGQ0NDM4OSJ9fX0=")
		,K("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmZkY2UyMzNhZWFiZjI5MTVmM2E2ODc1ZDI2ODIyOGZmOTA4MjM2YTU2M2UxZmRkMTRkNTliOTMxZDQ5OWIifX19")
		,L("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFlYWE5ZWY0N2NhZjZlNDRlNTlhYWU5OWJlZGQ4OTQ5ZTE0MjM2MjMwODAxZjM5ZWQyYTJlMjk3ODMifX19")
		,M("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWFhNTRkNzQyNDkyOTg5ZjczOWRlYjcyMjJhMjNiY2ZjZjg1ZTZjNzgyZmUyY2UwY2RlZWUwZjNmMmIwZWIifX19")
		,N("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzdkOGYyN2Y1N2ZmMzYyNmExMWQ3YmM1NDU2NjgzZjJjODkyNDg2YzJkOTZlOGU4NTZkNjJjYjc5YWE0YTA1YiJ9fX0=")
		,O("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg5OWY0MzY4ZjYyMzZiOTlkNWY0NzkxODZlYTZhMzlkMTA4M2ZmMjA3ZWFjYTcxYWYxMDc0NWM0ZSJ9fX0=")
		,P("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWYyYWRjZTA5NTI0MTY4ZTg0YWM2YzhiN2YyOWM5ZTg3NDU3MTJjNzMxNThiOGQ1YTFjNmIxMmRmODdjY2UwIn19fQ==")
		,Q("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDNjMWFjNmRjMGViZmJmNGUzYWQzZThlODgzNTU2Mzk3ZDZjY2E3N2UyZDVhOWE0MzdiYmM2MjY1ODA4NiJ9fX0=")
		,R("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWRlY2E2ZjkxMTUwZDE4YzliNjhhMTRkNjNkMzgyZTljNDIxMzJlZWU0M2Q2YTc4NmExMTVmMjU1Y2ZkNyJ9fX0=")
		,S("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc0ZDI3M2RkN2IzODQxNzE1MTgyNWYxZmIyNDUwYzk0NjhhYzY0YzE0NmYxYmFkMjQ0MTZlZGIxOWI1In19fQ==")
		,T("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWY2Njc4MjA0NWZhYmQ0OTViOTdhODg2YTI2NmZkYTlmNjZhNTJiZTc5YWNlZGFiZGVhODcxMDM1OGUyMjg1In19fQ==")
		,U("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzVmNWU0YmI4OTFlNWI2ZDYwMTM1ZGQ2NzIxY2M2MzFjNDc2NGFjOGE5MzQyMDdmMzFkOWVlYjJiYzQ5YTdhIn19fQ==")
		,V("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQ0YjQ1MzExOGI3YWU5ZGRjYzc5YTgyODQxZDgxZWQzYWI3ZmE4N2ZmMjhlZmY0MWY0ODY3NWZkNGQifX19")
		,W("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzkzZTc1NWNiYjZmNmMyZGEzYmYxNjlhMmU3NWVmZjM0MTUxNjM3ZDIzOTFhNDFlODYxNzM1ZWI5ZWEwNjc1In19fQ==")
		,X("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODliYTNiODM0NzU1ZGUyNGRhNmE4M2NlMWZiN2Y5NzFmN2FiYjIwNTU4NWVmODIzYjJmYzQ3YWJkNTAyOCJ9fX0=")
		,Y("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjg0Yjk2ZjFjMTEzNjY1ZTM5YTNjMjhlYTE4NWIzNmRkNWE3NWM3ZDY1MGYyYzk0NDE3MWRhZTk5ZGZiIn19fQ==")
		,Z("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGNkYjgwMTc1MTY5NjEzMjgwYThhMDZhMWM3NTFmYjcxYjY2ODllZGM4Y2UzZDhiOWQ5NmJkNDUwY2M4MSJ9fX0=")
		,Empty("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNhZjQ0NzMzNzIzNjQ0MjY0NDRlNDk2NzZmNmUzMzcyNDM4YWZhM2E2ZWIwNGEzOGFmOGU0MTI5NjVjMWI4In19fQ==")*/

		//      Wood Font
		,A("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTY3ZDgxM2FlN2ZmZTViZTk1MWE0ZjQxZjJhYTYxOWE1ZTM4OTRlODVlYTVkNDk4NmY4NDk0OWM2M2Q3NjcyZSJ9fX0=")
		,B("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTBjMWI1ODRmMTM5ODdiNDY2MTM5Mjg1YjJmM2YyOGRmNjc4NzEyM2QwYjMyMjgzZDg3OTRlMzM3NGUyMyJ9fX0=")
		,C("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWJlOTgzZWM0NzgwMjRlYzZmZDA0NmZjZGZhNDg0MjY3NjkzOTU1MWI0NzM1MDQ0N2M3N2MxM2FmMThlNmYifX19")
		,D("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzE5M2RjMGQ0YzVlODBmZjlhOGEwNWQyZmNmZTI2OTUzOWNiMzkyNzE5MGJhYzE5ZGEyZmNlNjFkNzEifX19")
		,E("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGJiMjczN2VjYmY5MTBlZmUzYjI2N2RiN2Q0YjMyN2YzNjBhYmM3MzJjNzdiZDBlNGVmZjFkNTEwY2RlZiJ9fX0=")
		,F("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjE4M2JhYjUwYTMyMjQwMjQ4ODZmMjUyNTFkMjRiNmRiOTNkNzNjMjQzMjU1OWZmNDllNDU5YjRjZDZhIn19fQ==")
		,G("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWNhM2YzMjRiZWVlZmI2YTBlMmM1YjNjNDZhYmM5MWNhOTFjMTRlYmE0MTlmYTQ3NjhhYzMwMjNkYmI0YjIifX19")
		,H("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzFmMzQ2MmE0NzM1NDlmMTQ2OWY4OTdmODRhOGQ0MTE5YmM3MWQ0YTVkODUyZTg1YzI2YjU4OGE1YzBjNzJmIn19fQ==")
		,I("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDYxNzhhZDUxZmQ1MmIxOWQwYTM4ODg3MTBiZDkyMDY4ZTkzMzI1MmFhYzZiMTNjNzZlN2U2ZWE1ZDMyMjYifX19")
		,J("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2E3OWRiOTkyMzg2N2U2OWMxZGJmMTcxNTFlNmY0YWQ5MmNlNjgxYmNlZGQzOTc3ZWViYmM0NGMyMDZmNDkifX19")
		,K("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTQ2MWIzOGM4ZTQ1NzgyYWRhNTlkMTYxMzJhNDIyMmMxOTM3NzhlN2Q3MGM0NTQyYzk1MzYzNzZmMzdiZTQyIn19fQ==")
		,L("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzE5ZjUwYjQzMmQ4NjhhZTM1OGUxNmY2MmVjMjZmMzU0MzdhZWI5NDkyYmNlMTM1NmM5YWE2YmIxOWEzODYifX19")
		,M("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljNDVhMjRhYWFiZjQ5ZTIxN2MxNTQ4MzIwNDg0OGE3MzU4MmFiYTdmYWUxMGVlMmM1N2JkYjc2NDgyZiJ9fX0=")
		,N("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzViOGIzZDhjNzdkZmI4ZmJkMjQ5NWM4NDJlYWM5NGZmZmE2ZjU5M2JmMTVhMjU3NGQ4NTRkZmYzOTI4In19fQ==")
		,O("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDExZGUxY2FkYjJhZGU2MTE0OWU1ZGVkMWJkODg1ZWRmMGRmNjI1OTI1NWIzM2I1ODdhOTZmOTgzYjJhMSJ9fX0=")
		,P("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTBhNzk4OWI1ZDZlNjIxYTEyMWVlZGFlNmY0NzZkMzUxOTNjOTdjMWE3Y2I4ZWNkNDM2MjJhNDg1ZGMyZTkxMiJ9fX0=")
		,Q("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM2MDlmMWZhZjgxZWQ0OWM1ODk0YWMxNGM5NGJhNTI5ODlmZGE0ZTFkMmE1MmZkOTQ1YTU1ZWQ3MTllZDQifX19")
		,R("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVjZWQ5OTMxYWNlMjNhZmMzNTEzNzEzNzliZjA1YzYzNWFkMTg2OTQzYmMxMzY0NzRlNGU1MTU2YzRjMzcifX19")
		,S("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2U0MWM2MDU3MmM1MzNlOTNjYTQyMTIyODkyOWU1NGQ2Yzg1NjUyOTQ1OTI0OWMyNWMzMmJhMzNhMWIxNTE3In19fQ==")
		,T("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTU2MmU4YzFkNjZiMjFlNDU5YmU5YTI0ZTVjMDI3YTM0ZDI2OWJkY2U0ZmJlZTJmNzY3OGQyZDNlZTQ3MTgifX19")
		,U("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjA3ZmJjMzM5ZmYyNDFhYzNkNjYxOWJjYjY4MjUzZGZjM2M5ODc4MmJhZjNmMWY0ZWZkYjk1NGY5YzI2In19fQ==")
		,V("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M5YTEzODYzOGZlZGI1MzRkNzk5Mjg4NzZiYWJhMjYxYzdhNjRiYTc5YzQyNGRjYmFmY2M5YmFjNzAxMGI4In19fQ==")
		,W("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjY5YWQxYTg4ZWQyYjA3NGUxMzAzYTEyOWY5NGU0YjcxMGNmM2U1YjRkOTk1MTYzNTY3ZjY4NzE5YzNkOTc5MiJ9fX0=")
		,X("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWE2Nzg3YmEzMjU2NGU3YzJmM2EwY2U2NDQ5OGVjYmIyM2I4OTg0NWU1YTY2YjVjZWM3NzM2ZjcyOWVkMzcifX19")
		,Y("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUyZmIzODhlMzMyMTJhMjQ3OGI1ZTE1YTk2ZjI3YWNhNmM2MmFjNzE5ZTFlNWY4N2ExY2YwZGU3YjE1ZTkxOCJ9fX0=")
		,Z("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTA1ODJiOWI1ZDk3OTc0YjExNDYxZDYzZWNlZDg1ZjQzOGEzZWVmNWRjMzI3OWY5YzQ3ZTFlMzhlYTU0YWU4ZCJ9fX0=")
		,One("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzFiYzJiY2ZiMmJkMzc1OWU2YjFlODZmYzdhNzk1ODVlMTEyN2RkMzU3ZmMyMDI4OTNmOWRlMjQxYmM5ZTUzMCJ9fX0=")
		,Two("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNkOWVlZWU4ODM0Njg4ODFkODM4NDhhNDZiZjMwMTI0ODVjMjNmNzU3NTNiOGZiZTg0ODczNDE0MTk4NDcifX19")
		,Three("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQ0ZWFlMTM5MzM4NjBhNmRmNWU4ZTk1NTY5M2I5NWE4YzNiMTVjMzZiOGI1ODc1MzJhYzA5OTZiYzM3ZTUifX19")
		,Four("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDJlNzhmYjIyNDI0MjMyZGMyN2I4MWZiY2I0N2ZkMjRjMWFjZjc2MDk4NzUzZjJkOWMyODU5ODI4N2RiNSJ9fX0=")
		,Five("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ1N2UzYmM4OGE2NTczMGUzMWExNGUzZjQxZTAzOGE1ZWNmMDg5MWE2YzI0MzY0M2I4ZTU0NzZhZTIifX19")
		,Six("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzM0YjM2ZGU3ZDY3OWI4YmJjNzI1NDk5YWRhZWYyNGRjNTE4ZjVhZTIzZTcxNjk4MWUxZGNjNmIyNzIwYWIifX19")
		,Seven("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmRiNmViMjVkMWZhYWJlMzBjZjQ0NGRjNjMzYjU4MzI0NzVlMzgwOTZiN2UyNDAyYTNlYzQ3NmRkN2I5In19fQ==")
		,Eight("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTkxOTQ5NzNhM2YxN2JkYTk5NzhlZDYyNzMzODM5OTcyMjI3NzRiNDU0Mzg2YzgzMTljMDRmMWY0Zjc0YzJiNSJ9fX0=")
		,Nine("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTY3Y2FmNzU5MWIzOGUxMjVhODAxN2Q1OGNmYzY0MzNiZmFmODRjZDQ5OWQ3OTRmNDFkMTBiZmYyZTViODQwIn19fQ==")
		,Zero("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMGViZTdlNTIxNTE2OWE2OTlhY2M2Y2VmYTdiNzNmZGIxMDhkYjg3YmI2ZGFlMjg0OWZiZTI0NzE0YjI3In19fQ==")
		
		
		/*         Stone Font
		,A("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFjNThiMWEzYjUzYjk0ODFlMzE3YTFlYTRmYzVlZWQ2YmFmY2E3YTI1ZTc0MWEzMmU0ZTNjMjg0MTI3OGMifX19")
		,B("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDRjNzExNTcxZTdlMjE0ZWU3OGRmZTRlZTBlMTI2M2I5MjUxNmU0MThkZThmYzhmMzI1N2FlMDkwMTQzMSJ9fX0=")
		,C("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmZmNWFhYmVhZDZmZWFmYWFlY2Y0NDIyY2RkNzgzN2NiYjM2YjAzYzk4NDFkZDFiMWQyZDNlZGI3ODI1ZTg1MSJ9fX0=")
		,D("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODkzZTYyMmI1ODE5NzU3OTJmN2MxMTllYzZmNDBhNGYxNmU1NTJiYjk4Nzc2YjBjN2FlMmJkZmQ0MTU0ZmU3In19fQ==")
		,E("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE1N2Q2NWIxOTkyMWM3NjBmZjQ5MTBiMzQwNDQ1NWI5YzJlZTM2YWZjMjAyZDg1MzhiYWVmZWM2NzY5NTMifX19")
		,F("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzU0Y2YyNjFiMmNkNmFiNTRiMGM2MjRmOGY2ZmY1NjVhN2I2M2UyOGUzYjUwYzZkYmZiNTJiNWYwZDdjZjlmIn19fQ==")
		,G("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDNjOWY4YTc0Y2EwMWJhOGM1NGRlMWVkYzgyZTFmYzA3YTgzOTIzZTY2NTc0YjZmZmU2MDY5MTkyNDBjNiJ9fX0=")
		,H("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjhjNThjNTA5MDM0NjE3YmY4MWVlMGRiOWJlMGJhM2U4NWNhMTU1NjgxNjM5MTRjODc2NjllZGIyZmQ3In19fQ==")
		,I("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDI0NjMyM2M5ZmIzMTkzMjZlZTJiZjNmNWI2M2VjM2Q5OWRmNzZhMTI0MzliZjBiNGMzYWIzMmQxM2ZkOSJ9fX0=")
		,J("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzU4NDU2Y2Q5YmI4YTdlOTc4NTkxYWUwY2IyNmFmMWFhZGFkNGZhN2ExNjcyNWIyOTUxNDVlMDliZWQ4MDY0In19fQ==")
		,K("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWY0OWZiNzA4MzY5ZTdiYzI5NDRhZDcwNjk2M2ZiNmFjNmNlNmQ0YzY3MDgxZGRhZGVjZmU1ZGE1MSJ9fX0=")
		,L("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGM4NGY3NTQxNmU4NTNhNzRmNmM3MGZjN2UxMDkzZDUzOTYxODc5OTU1YjQzM2JkOGM3YzZkNWE2ZGYifX19")
		,M("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzFmZGU5MWIxOWI5MzA5OTEzNzI0ZmVhOWU4NTMxMTI3MWM2N2JjYjc4NTc4ZDQ2MWJmNjVkOTYxMzA3NCJ9fX0=")
		,N("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWM3Yzk3MmU2Nzg1ZDZiMGFjZWI3NzlhYmRkNzcwMmQ5ODM0MWMyNGMyYTcxZTcwMjkzMGVjYTU4MDU1In19fQ==")
		,O("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODA3M2JiNDRmOTM0NWY5YmIzMWE2NzkwMjdlNzkzOWU0NjE4NDJhOGMyNzQ4NmQ3YTZiODQyYzM5ZWIzOGM0ZSJ9fX0=")
		,P("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjRiMjMxYThkNTU4NzBjZmI1YTlmNGU2NWRiMDZkZDdmOGUzNDI4MmYxNDE2Zjk1ODc4YjE5YWNjMzRhYzk1In19fQ==")
		,Q("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmZlZGQ2ZjllZmRiMTU2Yjg2OTM1Njk5YjJiNDgzNGRmMGY1ZDIxNDUxM2MwMWQzOGFmM2JkMDMxY2JjYzkyIn19fQ==")
		,R("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzAzYTFjZDU4M2NiYmZmZGUwOGY5NDNlNTZhYzNlM2FmYWZlY2FlZGU4MzQyMjFhODFlNmRiNmM2NDY2N2Y3ZCJ9fX0=")
		,S("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY1NzJlNjU1NzI1ZDc4Mzc1YTk4MTdlYjllZThiMzc4MjljYTFmZWE5M2I2MDk1Y2M3YWExOWU1ZWFjIn19fQ==")
		,T("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzA4YzllZjNhMzc1MWUyNTRlMmFmMWFkOGI1ZDY2OGNjZjVjNmVjM2VhMjY0MTg3N2NiYTU3NTgwN2QzOSJ9fX0=")
		,U("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTVhNmUzYWU1YWU2MjU5MjM1MjQ4MzhmYWM5ZmVmNWI0MjUyN2Y1MDI3YzljYTE0OWU2YzIwNzc5MmViIn19fQ==")
		,V("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTc1MTIxZjdkOWM2OGRhMGU1YjZhOTZhYzYxNTI5OGIxMmIyZWU1YmQxOTk4OTQzNmVlNjQ3ODc5ZGE1YiJ9fX0=")
		,W("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjdlMTY1YzNlZGM1NTQxZDQ2NTRjNDcyODg3MWU2OTA4ZjYxM2ZjMGVjNDZlODIzYzk2ZWFjODJhYzYyZTYyIn19fQ==")
		,X("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTkxOWQxNTk0YmY4MDlkYjdiNDRiMzc4MmJmOTBhNjlmNDQ5YTg3Y2U1ZDE4Y2I0MGViNjUzZmRlYzI3MjIifX19")
		,Y("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM1NDI0YmI4NjMwNWQ3NzQ3NjA0YjEzZTkyNGQ3NGYxZWZlMzg5MDZlNGU0NThkZDE4ZGNjNjdiNmNhNDgifX19")
		,Z("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU5MTIwMGRmMWNhZTUxYWNjMDcxZjg1YzdmN2Y1Yjg0NDlkMzliYjMyZjM2M2IwYWE1MWRiYzg1ZDEzM2UifX19")
		,One("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzFhOTQ2M2ZkM2M0MzNkNWUxZDlmZWM2ZDVkNGIwOWE4M2E5NzBiMGI3NGRkNTQ2Y2U2N2E3MzM0OGNhYWIifX19")
		,Two("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWNiNDE5ZDk4NGQ4Nzk2MzczYzk2NDYyMzNjN2EwMjY2NGJkMmNlM2ExZDM0NzZkZDliMWM1NDYzYjE0ZWJlIn19fQ==")
		,Three("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjhlYmFiNTdiNzYxNGJiMjJhMTE3YmU0M2U4NDhiY2QxNGRhZWNiNTBlOGY1ZDA5MjZlNDg2NGRmZjQ3MCJ9fX0=")
		,Four("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjJiZmNmYjQ4OWRhODY3ZGNlOTZlM2MzYzE3YTNkYjdjNzljYWU4YWMxZjlhNWE4YzhhYzk1ZTRiYTMifX19")
		,Five("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWY0ZWNmMTEwYjBhY2VlNGFmMWRhMzQzZmIxMzZmMWYyYzIxNjg1N2RmZGE2OTYxZGVmZGJlZTdiOTUyOCJ9fX0=")
		,Six("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjMzMWE2YTZmY2Q2OTk1YjYyMDg4ZDM1M2JmYjY4ZDliODlhZTI1ODMyNWNhZjNmMjg4NjQ2NGY1NGE3MzI5In19fQ==")
		,Seven("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDRiYTZhYzA3ZDQyMjM3N2E4NTU3OTNmMzZkZWEyZWQyNDAyMjNmNTJmZDE2NDgxODE2MTJlY2QxYTBjZmQ1In19fQ==")
		,Eight("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzYxYThhNjQxNDM3YmU5YWVhMjA3MjUzZGQzZjI1NDQwZDk1NGVhMmI1ODY2YzU1MmYzODZiMjlhYzRkMDQ5In19fQ==")
		,Nine("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE5MjhlMWJmZDg2YTliNzkzOTdjNGNiNGI2NWVmOTlhZjQ5YjdkNWY3OTU3YWQ2MmMwYzY5OWE2MjJjZmJlIn19fQ==")
		,Zero("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTVhMjI0ODA3NjkzOTc4ZWQ4MzQzNTVmOWU1MTQ1ZjljNTZlZjY4Y2Y2ZjJjOWUxNzM0YTQ2ZTI0NmFhZTEifX19")*/
		
		,Locks("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGFkOTQzZDA2MzM0N2Y5NWFiOWU5ZmE3NTc5MmRhODRlYzY2NWViZDIyYjA1MGJkYmE1MTlmZjdkYTYxZGIifX19")
		,Powers("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzg5NTdkNTAyM2M5MzdjNGM0MWFhMjQxMmQ0MzQxMGJkYTIzY2Y3OWE5ZjZhYjM2Yjc2ZmVmMmQ3YzQyOSJ9fX0=")
		,Areas("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzM0NjU2MTZjNzMxZDVhMGZjNTdjMjNmMzAxZjc2NWZlYmEyZDNkN2VmMTYxN2I1YjgyOGVmNzdlMCJ9fX0=")
		,PlotWorlds("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTE0MDg4Mjk1NDQ3Njk3MjQ2YWEyMmFhMTM3NmI2MmJlMzY3ODNkZjhjOTExNDA0NzU0OWE0YTFhNTFjNyJ9fX0=")
		,Tasks("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY2OTJmOTljYzZkNzgyNDIzMDQxMTA1NTM1ODk0ODQyOThiMmU0YTAyMzNiNzY3NTNmODg4ZTIwN2VmNSJ9fX0=")
		,All("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjFjNjNkOWI5ZmQ4NzQyZWFlYjA0YzY5MjE3MmNiOWRhNDM3ODE2OThhNTc1Y2RhYmUxYzA0ZGYxMmMzZiJ9fX0=")
		,Users("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTUxNjYyM2JlNmExYTcxN2NiZTIyZGE0NTRhMjliYjhjNGZhZmQ2NGFmZmEzNTNkZTU3YzEwOTI4NjcxIn19fQ==")
		,Teleport("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E1OWJiMGE3YTMyOTY1YjNkOTBkOGVhZmE4OTlkMTgzNWY0MjQ1MDllYWRkNGU2YjcwOWFkYTUwYjljZiJ9fX0=")
		,Portal("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBiZmMyNTc3ZjZlMjZjNmM2ZjczNjVjMmM0MDc2YmNjZWU2NTMxMjQ5ODkzODJjZTkzYmNhNGZjOWUzOWIifX19")
		,Survival("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk2MjhkOWIzZDc2ODI3ZjQzM2M4MzNlYTlkOTUxYjRiODdhNWZhNzhhN2I4N2U1ZTJiYmJlNGRlMzE2NmQzIn19fQ==")
		,Creative("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzBiNTU2MzE1OWNlZjdiNWM3NjgxNzFkNDE0Y2U5OTI5YjllMjYwNTQ1Y2ZmYWFiOGVlOTJmNjRjYjIyZjQifX19")
		,Time("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZlOGNmZjc1ZjdkNDMzMjYwYWYxZWNiMmY3NzNiNGJjMzgxZDk1MWRlNGUyZWI2NjE0MjM3NzlhNTkwZTcyYiJ9fX0=")
		,Speed("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmRjMTlmZGJhZjIzMmVhM2EyNmM1NTFhNDg5Mjc4NGZmMTYxNWJkYTYxYThlOTE2MDJiZmVhMWJkN2RkOWUifX19")
		
		,OK("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGE5OTM0MmUyYzczYTlmMzgyMjYyOGU3OTY0ODgyMzRmMjU4NDQ2ZjVhMmQ0ZDU5ZGRlNGFhODdkYjk4In19fQ==")
		,No("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZjNjBkYTQxNGJmMDM3MTU5YzhiZThkMDlhOGVjYjkxOWJmODlhMWEyMTUwMWI1YjJlYTc1OTYzOTE4YjdiIn19fQ==")
		,Back("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM5NzRjMjk3NDcxNzQ3NTNiYjQyOWM5OGU3NzZmN2Q1YjAxYmIzYzE4OGU0YmEyZmQ3ODgxYWM0ZTViMGFjIn19fQ==")
		,Prev("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmE2ZGI2YjZkMTJiYjRkYzc2ODJhOTZiM2I5MzNlN2EyOTUxZDM1NzE4NzkxOGU4YmVkYzY1ZGU5ZDc5NiJ9fX0=")
		,Next("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNiMGNiZTFjOWQ3YTFiMmQ2MWRmNDVjNmQyOWE3NzRjYzU2NGUyZWI5YTliNzU2ZmNmOWUxMzM0ZTM3MSJ9fX0=")
		,Add("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2FkMmYzODY1OTlmZGIxMDEzMzIyYmM4NmM3MmM4Y2JiZjVmZGIyYmI2MWQ5MjYzMWMyM2FhYzMyZDQ1NTIifX19")
		,Remove("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmU1YmMyNmU4ZTQ4ZmExZWM4ZWY3Y2I3NmNlYThiMzg0MjMzMTJiODE3ZTJlYjY2ZDUzMTNmZWZjOTlmMzIifX19")
		///give @p skull 1 3 {display:{Name:"Head of a Lost Soul"},SkullOwner:{Id:"6d7726cd-3b9f-487c-8d91-f9b1824d5884",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDgxODJjZGI0NTMzNGIyZDAzODkxMjcxMjQxZjQ0NWQyMjQ5MDYwZmU3NzJjMjc0YjJmOTExOGViN2FmMWYyIn19fQ=="}]}}}
		///give @p skull 1 3 {display:{Name:"Yellow/Green Slime"},SkullOwner:{Id:"263b5d74-1429-4290-b282-7ac2999639ec",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE2NGE4MWI1YzVhYWFjYzkxMzk5YTY1NjFiMzgyODM0YWM0NWJjNjgxN2RmMTU2MGUyZjQzNGEzMmY3YTE2In19fQ=="}]}}}
		//give @p skull 1 3 {display:{Name:"One Way Street Sign"},SkullOwner:{Id:"d988e4f0-3731-4c04-84c8-5ec3c13bf533",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzNkZjQwYTc1M2FjOTI1Y2JjMTZlNjFjY2Y5NDhmYjZlYzZjNjZlOTE1MjFhYjcyYmZlNThlMWU5YzhkNSJ9fX0="}]}}}

		///give @p skull 1 3 {display:{Name:"nsdjg"},SkullOwner:{Id:"af20c020-6810-4abe-8437-97d3bff52bec",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjI1NmY3MTczNWVmNDU4NTgxYzlkYWNmMzk0MTg1ZWVkOWIzM2NiNmVjNWNkNTk0YTU3MTUzYThiNTY2NTYwIn19fQ=="}]}}}
		;
		Texture texture;
		private Textures(String Data){
			texture = new Icon().new Texture(Data);}}
	public class Texture{
		String data;
		private Texture(String Data){
			data=Data;}}
	
	 
	private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final JSONParser jsonParser = new JSONParser();
 
    public static Texture getTexture(Player player) {
        try {
        	HttpURLConnection connection;
			connection = (HttpURLConnection) new URL(PROFILE_URL+player.getUniqueId().toString().replace("-", "")).openConnection();
            JSONObject response = (JSONObject) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
            JSONArray gameProfileArray = (JSONArray) response.get("properties");
            JSONObject gameProfile = (JSONObject) gameProfileArray.get(0);
            String texture = (String) gameProfile.get("value");
            String cause = (String) response.get("cause");
            String errorMessage = (String) response.get("errorMessage");
            if (cause != null && cause.length() > 0) {
                throw new IllegalStateException(errorMessage);}
            return new Icon().new Texture(texture);}
        catch (IOException e) {
        	if (e.getMessage()!=null&&e.getMessage().contains("HTTP response code: 429"))
        		return getTexture(player);
        	else 
    			return null;}
        catch (ParseException e) {
			return null;}}	
	
	private static GameProfile makeGameProfile(Texture texture,UUID id){
		GameProfile profile=new GameProfile(id,null);
		PropertyMap propertyMap=profile.getProperties();
		if(propertyMap==null){
			PagerPlugin.debug("No property map found in GameProfile, can't continue.");
			return null;}
		propertyMap.put("textures",new Property("textures",texture.data));
		propertyMap.put("Signature",new Property("Signature","1234"));
		return profile;}
	
	private static boolean setFieldValue(Class<?> objectClass, Object object, String fieldName, Object fieldValue, String message){
		try{
			Field field=objectClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			Field modifiersField=Field.class.getDeclaredField("modifiers");
			
			boolean accessible=modifiersField.isAccessible();
			if(!accessible){
				modifiersField.setAccessible(true);}
			try{
				field.set(object, fieldValue);
				return true;}
			finally{
				if(!accessible){
					modifiersField.setAccessible(false);}}}
		catch(IllegalArgumentException ex){
			PagerPlugin.debug(message+": unsupported version.");}
		catch(IllegalAccessException ex){
			PagerPlugin.debug(message+": security exception.");}
		catch(NoSuchFieldException ex){
			PagerPlugin.debug(message+": unsupported version, field "+fieldName+" not found.");}
		catch(SecurityException ex){
			PagerPlugin.debug(message+": security exception.");}
		catch(ClassCastException ex){
			PagerPlugin.debug(message+": unsupported version, unable to cast result.");}
		return false;}}
