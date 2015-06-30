package edu.knoxcraft.hooks;

import net.canarymod.hook.Hook;

public class KCTUploadHook extends Hook
{
    private String playerName;
    private String language;
    private String json;
    private String source;
    public String getPlayerName() {
        return playerName;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public String getJSON() {
        return json;
    }
    public void setJson(String json) {
        this.json = json;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
}
