package com.kodingkingdom.pager.page.select;

import java.util.List;
import java.util.function.Supplier;

public class MutableSelectItemCategoryPage extends MutableCategoryPage<SelectItem>{

	public MutableSelectItemCategoryPage(Supplier<List<SelectItem>> SelectItemsGetter, long PollInterval){
		super(SelectItemsGetter, (SelectItem selectItem)->{return selectItem;}, PollInterval);}}
