package edu.knoxcraft.hooks;

import net.canarymod.hook.Hook;

public class UploadJSONHook extends Hook
{
    private String json;
    
    public UploadJSONHook(String json) {
        this.json=json;
    }
    
    public String getJSON() {
        return this.json;
    }
}
