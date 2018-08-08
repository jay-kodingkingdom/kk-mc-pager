package com.kodingkingdom.pager.page;

import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.kodingkingdom.pager.PagerPlugin;
import com.kodingkingdom.pager.icons.Icon;

public class Menu extends CompositeBoxPage implements Listener{
	
	String menuName;
	int menuWidth,menuHeight;
		
	ItemStack menuIcon;
	Inventory menuMenu;	
	HashMap<Integer,MenuItem> itemMap;
	HashMap<MenuItem,Integer> slotMap;
	
	public Player current_player;
	
	
	private Menu(){}
	
	static HashSet<Menu> menus=new HashSet<Menu>();
	
	public static Menu createMenu(int MenuWidth, int MenuHeight, String MenuName, ItemStack MenuIcon){		
		
		Menu menu = new Menu();
		
		menu.menuName=MenuName;
		menu.menuWidth=MenuWidth;
		menu.menuHeight=MenuHeight;
		menu.menuIcon=MenuIcon;
		menu.menuMenu = Bukkit.createInventory(null, MenuWidth * MenuHeight, MenuName);
		//TemplatePlugin.debug("size of inv is "+menu.menuMenu.getSize());
		
		menu.itemMap = new HashMap<Integer,MenuItem>();
		menu.slotMap = new HashMap<MenuItem,Integer>();
		menu.menuItemsBox = menu.new Box<MenuItem>(new MenuItem[MenuHeight][MenuWidth]);
		
		for (int widthX=0;widthX<MenuWidth;widthX++){
			for (int heightY=0;heightY<MenuHeight;heightY++){
				int slotNumber = menu.menuItemsBox.getSlotNumber(widthX, heightY);
				//TemplatePlugin.debug("slot Number is "+slotNumber);
				ItemStack itemIcon = menu.menuMenu.getItem(slotNumber);
				MenuItem item = menu.new MenuItem(itemIcon);
				menu.itemMap.put(slotNumber, item);
				menu.slotMap.put(item, slotNumber);
				
				menu.menuItemsBox.setBoxItem(widthX,heightY,item);

				menu.itemPageMap.put(item, menu);}}
		
		PagerPlugin.getPlugin().getPager().registerEvents(menu);
		
		menus.add(menu);
		return menu;}
	
	public static void deleteMenu(Menu menu){
		menu.menuName=null;
		menu.menuWidth=menu.menuHeight=-1;
		menu.menuIcon=null;
		menu.menuMenu=null;	
		menu.itemMap=null;
		menu.slotMap=null;
		menus.remove(menu);}
	
	@EventHandler
	public void openMenu(PlayerInteractEvent e){
		if (Icon.same(e.getItem(),menuIcon)){
			this.current_player = e.getPlayer();
			e.getPlayer().openInventory(menuMenu);
			openPage();}}
	@EventHandler(priority=EventPriority.MONITOR)
	public void clickMenu(InventoryDragEvent e){
		if (menuMenu.equals(e.getInventory())){
			e.setCancelled(true);}}
	@EventHandler(priority=EventPriority.MONITOR)
	public void clickMenu(InventoryClickEvent e){
		if (menuMenu.equals(e.getClickedInventory())){
			e.setCancelled(true);
			PagerPlugin.getPlugin().getPager().scheduleTask(()->clickItem(normalize(itemMap.get(e.getRawSlot()))), 0);}}
	/*@EventHandler(priority=EventPriority.MONITOR)
	public void closeMenu(InventoryCloseEvent e){
		if (e.getInventory().equals(menuMenu)){
			this.current_player = null;
			closePage();}}*/

	public MenuItem normalize(MenuItem menuItem){
		if (menuItem==null) return Null;
		else return menuItem;}
	
	public final MenuItem Null = new MenuItem(null);
	
	
	public static final Menu getMenu(Page page){
		return page.itemPageMap.keySet().iterator().next().getMenu();}
	
	public class MenuItem {
		private HashMap<Page,ItemStack> ownerIconMap;
		
		private Page owner;
		
		Menu getMenu(){
			return Menu.this;}
		
		Page getOwner(){
			return owner;}
		
		void setOwner(Page Owner){
			if (!ownerIconMap.containsKey(Owner)){
				ownerIconMap.put(Owner,getIcon());}
			owner=Owner;
			update();}
		
		MenuItem(ItemStack ItemIcon){
			owner = Menu.this;
			ownerIconMap=new HashMap<Page,ItemStack>();
			ownerIconMap.put(owner,ItemIcon);}
		
		public ItemStack getIcon(){
			return ownerIconMap.get(getOwner());}
		
		void setIcon(ItemStack ItemIcon){
			ownerIconMap.replace(getOwner(),ItemIcon);
			update();}

		void setIcon(Page Owner, ItemStack ItemIcon){
			ownerIconMap.replace(Owner,ItemIcon);
			if (Owner==getOwner()) update();}
		
		private void update(){
			Menu.this.menuMenu.setItem(Menu.this.slotMap.get(this), getIcon());
			for (Player player : Menu.this.menuMenu.getViewers().stream()
									.filter(entity -> entity instanceof Player)
									.map(entity -> (Player)entity)
									.collect(Collectors.toList())){
				player.updateInventory();}
			//EduCraftPlugin.debug("icon is "+getIcon());
			}}}