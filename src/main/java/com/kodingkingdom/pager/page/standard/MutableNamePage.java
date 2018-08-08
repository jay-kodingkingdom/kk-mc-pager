package com.kodingkingdom.pager.page.standard;

import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.kodingkingdom.pager.Pager;
import com.kodingkingdom.pager.icons.Icon;
import com.kodingkingdom.pager.page.select.MutableSelectItemCategoryPage;
import com.kodingkingdom.pager.page.select.SelectItem;

public class MutableNamePage extends MutableSelectItemCategoryPage{
	
	public MutableNamePage(Supplier<String> nameGetter, int height) {
		super(
			()->{
				return nameGetter.get().chars().mapToObj(ch -> (char)ch)
						.map((Character ch)->{return new SelectItem(()->{}, Icon.makeIcon(""+ch).asIcon());})
						.collect(Collectors.toList());}
			, Pager.pollInterval);}}
