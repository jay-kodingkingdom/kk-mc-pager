package com.kodingkingdom.pager.page.select;

import java.util.function.Function;

import com.kodingkingdom.pager.page.BoxPage;
import com.kodingkingdom.pager.page.Menu;
import com.kodingkingdom.pager.page.Menu.MenuItem;

public class CategoryPage<T> extends BoxPage{

	private Box<T> selectItemsBox;
	private Function<T,SelectItem> function;
	
	public CategoryPage(T[][] SelectItems, Function<T,SelectItem> Function){ 
		selectItemsBox=new Box<T>(SelectItems);
		function=Function;}
	
	protected void boxAttachedAction(Connector connector){
		for (int widthX=0;widthX<menuItemsBox.getWidth();widthX++){
			for (int heightY=0;heightY<menuItemsBox.getHeight();heightY++){
				setIcon(menuItemsBox.getBoxItem(widthX, heightY)
					,SelectItem.normalize(function.apply(selectItemsBox.getBoxItem(widthX, heightY))).icon);}}}
	
	protected void boxRemovedAction(){
		for (int widthX=0;widthX<menuItemsBox.getWidth();widthX++){
			for (int heightY=0;heightY<menuItemsBox.getHeight();heightY++){
				setIcon(menuItemsBox.getBoxItem(widthX, heightY)
					,null);}}}
	
	protected void clickItemAction(Menu.MenuItem item){
		Box<MenuItem> menuItems=getSubBox(0, 0, getWidth()-1, getHeight()-1);
		for (int widthX=0;widthX<menuItems.getWidth();widthX++){
			for (int heightY=0;heightY<menuItems.getHeight();heightY++){
				if (menuItems.getBoxItem(widthX, heightY).equals(item)){
					SelectItem.normalize(function.apply(selectItemsBox.getBoxItem(widthX, heightY))).action.run();}}}}
	
	public Connector makePageConnector(Box<MenuItem> menuItemsBox){
		if (menuItemsBox.getWidth() != selectItemsBox.getWidth()
		|| menuItemsBox.getHeight() != selectItemsBox.getHeight())
			throw new IllegalArgumentException();
		else 
			return super.makePageConnector(menuItemsBox);}}
