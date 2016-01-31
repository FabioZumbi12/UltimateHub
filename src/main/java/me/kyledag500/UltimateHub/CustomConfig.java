package me.kyledag500.UltimateHub;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
 
public class CustomConfig {
       
        private String configName;
        private File configFile;
        private UTFYaml config;
        private Plugin plugin;
       
        public CustomConfig(Plugin plugin, String configName) {
                this.plugin = plugin;
                this.configName = configName;
        }
       
       
        public File getConfigFile() {
                return this.configFile;
        }
       
        public String getConfigName() {
                return this.configName;
        }
       
        public boolean doesConfigExist() {
                if(getConfigFile() == null) {
                        return false;
                }
                return getConfigFile().exists();
        }
       
       
        public boolean createIfNoExist() {
                configFile = new File(this.plugin.getDataFolder(), this.configName.replace("/", " + " + File.separator + " + "));
                if(!this.configFile.exists()) {
                        if(this.plugin.getResource(configName) != null) {
                                this.plugin.saveResource(configName, false);
                        }
                        reloadConfig();
                        return true;
                }
                reloadConfig();
                return false;
        }
       
       
        public void reloadConfig() {
                this.configFile = new File(this.plugin.getDataFolder(), this.configName);
                this.config = inputLoader(this.configFile);
        }
        
        private static UTFYaml inputLoader(File inp) {
        	UTFYaml file = new UTFYaml();
    		try {
    			FileInputStream f = new FileInputStream(inp);
    			file.load(new InputStreamReader(f, StandardCharsets.UTF_8));
    			f.close();
    		} catch (Exception e) {
    			e.printStackTrace();
    		} 		
    		return file;
    	}
       
        public boolean saveConfig() {
                if(config != null && configFile != null) {
                        try {
                                config.save(configFile);
                        } catch (Exception ex) {}
                }
                return false;
        }
       
        public FileConfiguration getConfig() {
                reloadConfig();
                return this.config;
        }
       
}