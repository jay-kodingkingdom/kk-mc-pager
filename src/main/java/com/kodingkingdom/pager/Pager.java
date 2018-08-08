package com.kodingkingdom.pager;

import org.bukkit.event.Listener;

//TODO: make on hand book automatically usable
public class Pager implements Listener{
	public final static long pollInterval=8;

	PagerPlugin plugin;	
	public Pager(PagerPlugin Plugin){plugin=Plugin;}
	
	//TODO: handle cases of old book already on hand, aka onheld event not fired
	public void Live(){}
	public void Die(){}
	
	public void registerEvents(Listener listener){
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);}

	public int scheduleAsyncTask(Runnable task){
		return plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, task);}
	public int scheduleAsyncTask(Runnable task, long delay){
		return plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, task, delay);}
	public int scheduleTask(Runnable task, long delay){
		return plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task, delay);}
	public void cancelTask(int taskId){
		plugin.getServer().getScheduler().cancelTask(taskId);}
}
