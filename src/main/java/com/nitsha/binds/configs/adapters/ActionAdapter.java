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
        ActionData result = null;
        switch (type) {
            case "command":
                result = context.deserialize(jsonObject, AllActionsData.CommandActionData.class);
                break;
            case "delay":
                result = context.deserialize(jsonObject, AllActionsData.DelayActionData.class);
                break;
            case "keybind":
            case "keyDown":
            case "keyUp":
                result = context.deserialize(jsonObject, AllActionsData.KeybindActionData.class);
                break;
            case "chatMessage":
                result = context.deserialize(jsonObject, AllActionsData.ChatMessageActionData.class);
                break;
            case "titleMessage":
                result = context.deserialize(jsonObject, AllActionsData.TitleMessageActionData.class);
                break;
            case "keyEvent":
                result = context.deserialize(jsonObject, AllActionsData.KeyEventActionData.class);
                break;
            case "playSound":
                result = context.deserialize(jsonObject, AllActionsData.PlaySoundActionData.class);
                break;
            case "loop":
                result = context.deserialize(jsonObject, AllActionsData.LoopActionData.class);
                break;
            case "toast":
                result = context.deserialize(jsonObject, AllActionsData.ToastActionData.class);
                break;
            default:
                System.err.println("Unknown action type: " + type);
                return null;
        }

        if (result != null) {
            result.type = type;
        }
        return result;
    }
}
