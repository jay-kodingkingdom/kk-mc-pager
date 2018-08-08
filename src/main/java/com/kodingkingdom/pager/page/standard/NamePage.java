package com.kodingkingdom.pager.page.standard;

import java.util.stream.Collectors;

import com.kodingkingdom.pager.icons.Icon;
import com.kodingkingdom.pager.page.select.Layout;
import com.kodingkingdom.pager.page.select.SelectItem;
import com.kodingkingdom.pager.page.select.SelectItemCategoryPage;

public class NamePage extends SelectItemCategoryPage{
	public NamePage(String name, int height) {
		super (Layout .flow (
			name .chars () .mapToObj(c -> (char) c)
				.map(char_->new SelectItem(()->{}, Icon.makeIcon(""+char_).asIcon()))
				.collect(Collectors.toList())
			, 1, height));
	}
}
