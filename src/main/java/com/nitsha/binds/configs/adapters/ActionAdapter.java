package com.nitsha.binds.configs.adapters;

import com.google.gson.*;
import com.nitsha.binds.configs.dto.actions.AllActionsData;
import com.nitsha.binds.configs.dto.preset.ActionData;

import java.lang.reflect.Type;

public class ActionAdapter implements JsonSerializer<ActionData>, JsonDeserializer<ActionData> {

    @Override
    public JsonElement serialize(ActionData src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = context.serialize(src).getAsJsonObject();
        obj.addProperty("type", src.type);
        return obj;
    }

    @Override
    public ActionData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        if (!jsonObject.has("type")) {
            return null;
        }

        String type = jsonObject.get("type").getAsString();
        switch (type) {
            case "command":
                return context.deserialize(jsonObject, AllActionsData.CommandActionData.class);
            case "delay":
                return context.deserialize(jsonObject, AllActionsData.DelayActionData.class);
            case "keybind":
            case "keyDown":
            case "keyUp":
                return context.deserialize(jsonObject, AllActionsData.KeybindActionData.class);
            case "chatMessage":
                return context.deserialize(jsonObject, AllActionsData.ChatMessageActionData.class);
            case "titleMessage":
                return context.deserialize(jsonObject, AllActionsData.TitleMessageActionData.class);
            case "keyEvent":
                return context.deserialize(jsonObject, AllActionsData.KeyEventActionData.class);
            case "playSound":
                return context.deserialize(jsonObject, AllActionsData.PlaySoundActionData.class);
            case "loop":
                return context.deserialize(jsonObject, AllActionsData.LoopActionData.class);
            default:
                System.err.println("Unknown action type: " + type);
                return null;
        }
    }
}
