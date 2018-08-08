package com.kodingkingdom.pager.page.standard;

import com.kodingkingdom.pager.icons.Icon;
import com.kodingkingdom.pager.icons.Icon.Textures;
import com.kodingkingdom.pager.page.select.SelectItem;
import com.kodingkingdom.pager.page.select.SelectItemCategoryPage;

public class ControlsPage extends SelectItemCategoryPage{

	private static Icon[] controlsIcons = new Icon[]
			{Icon.makeIcon(Textures.Back),Icon.makeIcon(Textures.Question),Icon.makeIcon(Textures.Add),Icon.makeIcon(Textures.Remove),Icon.makeIcon(Textures.No),Icon.makeIcon(Textures.Prev),Icon.makeIcon(Textures.Next),Icon.makeIcon(Textures.OK)};
	private static String[] controlsCaptions = new String[]
			{"Up","Question","Add","Remove","No","Back","Next","OK"};
			
	private static SelectItem[][] makeSelectItems(Runnable[] controlsActions){
		SelectItem[][] selectItems = new SelectItem[1][controlsActions.length];
		for (int widthX=0;widthX<controlsActions.length;widthX++){
			if (controlsActions[widthX]==null) selectItems[0][widthX]=SelectItem.Null;
			else selectItems[0][widthX]=
					new SelectItem(
						controlsActions[widthX]
						,controlsIcons[widthX]
								.withName(controlsCaptions[widthX]).withCaption(controlsCaptions[widthX]).asIcon());}
		return selectItems;}
	private static SelectItem[][] makeSelectItems(ControlsItem[] controlsItems){
		SelectItem[][] selectItems = new SelectItem[1][controlsItems.length];
		for (int widthX=0;widthX<controlsItems.length;widthX++){
			if (controlsItems[widthX]==null) selectItems[0][widthX]=SelectItem.Null;
			else selectItems[0][widthX]=
					new SelectItem(
						controlsItems[widthX].controlsAction
						,controlsIcons[widthX].withName(controlsItems[widthX].controlsCaption)
												.withCaption(controlsItems[widthX].controlsCaption).asIcon());}
		return selectItems;}

	public ControlsPage(Runnable... controlsActions) {
		super(makeSelectItems(controlsActions));}
	public ControlsPage(ControlsItem... controlsItems) {
		super(makeSelectItems(controlsItems));}}
