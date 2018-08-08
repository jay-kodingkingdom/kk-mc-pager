package com.kodingkingdom.pager;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;


public class PagerPlugin extends JavaPlugin {
	Pager x=new Pager(this);
	@Override
    public void onEnable(){x.Live();} 
    @Override
    public void onDisable(){x.Die();}
        
    public Pager getPager(){return x;}
    
    static PagerPlugin singleton;
    public PagerPlugin(){singleton=this;}
    public static PagerPlugin getPlugin(){return singleton;}
    public static void debug(String msg){
    		singleton.getLogger().log(Level.INFO
    				, msg);}}