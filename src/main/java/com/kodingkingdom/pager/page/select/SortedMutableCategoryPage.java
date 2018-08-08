package com.kodingkingdom.pager.page.select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class SortedMutableCategoryPage<T extends Comparable<? super T>> extends MutableCategoryPage<T>{

	public SortedMutableCategoryPage(
			Supplier<Collection<T>> Ts,
			Function<T,SelectItem> Function, long PollInterval) {
		super(
				new Supplier<List<T>>(){
					public List<T> get(){
						ArrayList<T> ts=new ArrayList<T>(Ts.get());
						Collections.sort(ts);
						return ts;}},
				t->{
					return
						new SelectItem(()->{
							if (t!=null) Function.apply(t).getAction().run();}
							,(t!=null?Function.apply(t).getIcon():null));}
				, PollInterval);}}
