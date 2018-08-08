package com.kodingkingdom.pager.page.select;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.kodingkingdom.pager.PagerPlugin;
import com.kodingkingdom.pager.page.BoxPage;
import com.kodingkingdom.pager.page.Menu;
import com.kodingkingdom.pager.page.Menu.MenuItem;

public class MutableCategoryPage<T> extends BoxPage{

	long pollInterval;
	int pageNumber;
	Box<T> selectItemsBox;
	Function<T,SelectItem> function;
	
	Supplier<List<T>> selectItemsGetter;
	
	int pollId =-1;
	List<T> selectItemsList;
		
	public MutableCategoryPage(Supplier<List<T>> SelectItemsGetter, Function<T,SelectItem> Function, long PollInterval){
		pollInterval=PollInterval;
		pageNumber=0;
		selectItemsGetter=SelectItemsGetter;
		function=Function;}
	
	T normalize(Supplier<T> selectItemGetter){
		try {
			return selectItemGetter.get();}
		catch (Exception e){
			return null;}}
	
	void updatePage(){
		updateSelectItems();
		updateMenuItems();
		pollId=PagerPlugin.getPlugin().getPager().scheduleTask(() -> {updatePage();}, pollInterval);}
	
	void updateSelectItems(){
		selectItemsList=selectItemsGetter.get();
		int pageLength = menuItemsBox.getHeight()*menuItemsBox.getWidth();
		T[][] selectItems = (T[][])new Object[menuItemsBox.getHeight()][menuItemsBox.getWidth()]; 
		for (int widthX=0;widthX<menuItemsBox.getWidth();widthX++){
			for (int heightY=0;heightY<menuItemsBox.getHeight();heightY++){
				int X=widthX,Y=heightY;
				selectItems[heightY][widthX]=
						normalize(()->{
							return selectItemsList.get(
								pageNumber*pageLength+
								menuItemsBox.getSlotNumber(X,Y));});}}
		selectItemsBox=new Box<T>(selectItems);}
	
	void updateMenuItems(){
		for (int widthX=0;widthX<menuItemsBox.getWidth();widthX++){
			for (int heightY=0;heightY<menuItemsBox.getHeight();heightY++){
				//EduCraftPlugin.debug("owner of width,height "+widthX+","+heightY+" is "+menuItemsBox.getBoxItem(widthX, heightY).getOwner());
				setIcon(menuItemsBox.getBoxItem(widthX, heightY)
					,SelectItem.normalize(function.apply(selectItemsBox.getBoxItem(widthX, heightY))).icon);}}}
	
	protected void boxAttachedAction(Connector connector){
		updatePage();}
	
	protected void boxRemovedAction(){
		if (pollId!=-1) {
			PagerPlugin.getPlugin().getPager().cancelTask(pollId);
			pollId=-1;}
		
		function=(T t)->{return SelectItem.Null;};
		updateMenuItems();}
	
	protected void clickItemAction(Menu.MenuItem item){
		Box<MenuItem> menuItems=getSubBox(0, 0, getWidth()-1, getHeight()-1);
		for (int widthX=0;widthX<menuItems.getWidth();widthX++){
			for (int heightY=0;heightY<menuItems.getHeight();heightY++){
				if (menuItems.getBoxItem(widthX, heightY).equals(item)){
					SelectItem.normalize(function.apply(selectItemsBox.getBoxItem(widthX, heightY))).action.run();}}}}

	public void nextPage(){
		pageNumber++;}
	public void prevPage(){
		pageNumber--;}}
