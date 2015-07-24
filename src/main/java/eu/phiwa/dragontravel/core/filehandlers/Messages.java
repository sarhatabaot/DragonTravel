package eu.phiwa.dragontravel.core.filehandlers;

import eu.phiwa.dragontravel.core.DragonTravelMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.logging.Level;

public class Messages {

    // Messages
    private FileConfiguration messages;
    private File messagesFile;
    private double messagesVersion = 0.6;

    private String language = "";
    private String pathInsideJAR = "main/resources/messages/";
    private String pathOnServer = "plugins/DragonTravel/messages";

    public Messages() {
        loadMessages();
    }

    private void copy(InputStream in, File file) {

        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1)
                out.write(buf, 0, len);
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void create() {
        if (messagesFile.exists())
            return;
        try {
            messagesFile.createNewFile();
            copy(DragonTravelMain.getInstance().getResource("messages/messages-" + language + ".yml"), messagesFile);
            Bukkit.getLogger().log(Level.INFO, "Created messages file.");
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not create the languages file - check the language!");
            e.printStackTrace();
        }
    }

    public String getMessage(String path) {
        String message;
        message = replaceColors(messages.getString(path));
        if (message == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not find the message looking for at path '" + path + "' which leads to a serious problem! Be try to generate a new language file if you previously updated DragonTravel!");
            return replaceColors("&cAn error occured, please contact the admin! Missing message '" + path + "'");
        }
        if (message.length() == 0)
            return ChatColor.RED + "Error, could not read message-text from file, please contact the admin.";

        return message;
    }

    public void loadMessages() {

        language = DragonTravelMain.getInstance().getConfig().getString("Language");

        if (language == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load messages-file because the language could not be read from the config! Disabling plugin!");
            Bukkit.getPluginManager().disablePlugin(DragonTravelMain.getInstance());
            return;
        }

        messagesFile = new File(DragonTravelMain.getInstance().getDataFolder(), "messages-" + language + ".yml");

        if (!messagesFile.exists())
            create();

        messages = YamlConfiguration.loadConfiguration(messagesFile);
        updateConfig();
    }

    private void newlyRequiredMessages() {

        // Add new keys here!

        // v0.0.0.9
        if (messages.get("Messages.Flights.Error.OnlySigns") == null)
            messages.set("Messages.Flights.Error.OnlySigns", "&cThis command has been disabled by the admin, you can only use flights using signs.");
        if (messages.get("Messages.Stations.Error.NotCreateStationWithRandomstatName") == null)
            messages.set("Messages.Stations.Error.NotCreateStationWithRandomstatName", "&cYou cannot create a staion with the name of the RandomDest.");

        // v0.0.0.17
        if (messages.get("Messages.Factions.Error.NotYourFaction") == null)
            messages.set("Messages.Factions.Error.NotYourFaction", ": &cThis is not your faction.");

        // 0.5
        if (messages.get("Messages.General.Error.BelowMinMountHeight") == null)
            messages.set("Messages.General.Error.BelowMinMountHeight", "&cYou are below the minimum height required to mount a dragon. Minimum height is &f{minheight}&c.");
        if (messages.get("Messages.General.Error.DamageCooldown") == null)
            messages.set("Messages.General.Error.DamageCooldown", "&cYou must wait &f{seconds} &cmore seconds before you can mount a dragon.");

        //0.6
        if (messages.get("Messages.General.Error.StatDragonExists") == null)
            messages.set("Messages.General.Error.StatDragonExists", ": &cThis name is already taken.");
        if (messages.get("Messages.General.Error.StatDragonNotExists") == null)
            messages.set("Messages.General.Error.StatDragonNotExists", ": &cThis name is not recognised.");
        if (messages.get("Messages.General.Error.StatDragonCmdRevised") == null)
            messages.set("Messages.General.Error.StatDragonCmdRevised", ": &cThis command now takes a parameter - you must include a name. Check help page 5 for more details.");
        if (messages.get("Messages.Travels.Successful.TravellingToPlayer") == null)
            messages.set("Messages.Travels.Successful.TravellingToPlayer", "&aTravelling to &f{playername}&a.");
        if (messages.get("Messages.Travels.Successful.TravellingToFactionHome") == null)
            messages.set("Messages.Travels.Successful.TravellingToFactionHome", "&aTravelling to the faction home.");
        if (messages.get("Messages.General.Error.RequireSkyLight") == null)
            messages.set("Messages.General.Error.RequireSkyLight", "&cYou must have access to sky light!");

        // Update the file version
        messages.set("File.Version", messagesVersion);
    }


    private void noLongerRequiredMessages() {
        // DragonTravelMain.config.set("example key", null);
    }


    private String replaceColors(String string) {

        String formattedMessage = null;

        try {
            formattedMessage = string.replaceAll("(?i)&([a-f0-9])", "\u00A7$1");
        } catch (Exception ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not read a message from the messages-xx.yml!");
        }

        return formattedMessage;
    }

    private void updateConfig() {

        if (messages.getDouble("File.Version") != messagesVersion)
            newlyRequiredMessages();

        noLongerRequiredMessages();

        // Refresh file and config variables for persistence.
        try {
            messagesFile = new File(DragonTravelMain.getInstance().getDataFolder(), "messages-" + language + ".yml");
            messages.save(messagesFile);
            messages = YamlConfiguration.loadConfiguration(messagesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
