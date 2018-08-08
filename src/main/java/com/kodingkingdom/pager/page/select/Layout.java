package com.kodingkingdom.pager.page.select;

import java.util.Collection;

public class Layout{
	
	public static SelectItem[][] flow (Collection<SelectItem> items, int width, int height) {
		SelectItem[][] res = new SelectItem[height] [width];
		int x = 0;
		int y = 0;
		for (SelectItem a : items) {
			res [y] [x] = a;
			x ++;
			if (x == width) {
				x = 0;
				y ++;
			}
		}
		for (; y < height; y ++) {
			for (; x < width; x ++) {
				res [y] [x] = new SelectItem(()->{}, null);
			}		
			x = 0;
		}
		return res;
	}
	
	/*public static SelectItem[][] double_flow (Collection<SelectItem> items, Collection<SelectItem> end_items, int width, int height) {
		SelectItem[][] res = new SelectItem[height][1];
		int x = 0;
		int y = 0;
		for (SelectItem a : items) {
			res [x] [y] = a;
			x ++;
			if (x == width) {
				x = 0;
				y ++;
			}
		}
		for (; y < height; y ++) {
			for (; x < width; x ++) {
				res [x] [y] = new SelectItem(()->{}, null);
			}		
			x = 0;
		}
		x = width - 1;
		y = height - 1;
		for (SelectItem a : end_items) {
			res [x] [y] = a;
			x --;
			if (x == -1) {
				x = width - 1;
				y --;
			}
		}
		return res;
	}*/
	
	private Layout () {}
}