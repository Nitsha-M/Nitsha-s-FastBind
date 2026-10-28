package com.nitsha.binds.configs.adapters;

import com.google.gson.*;
import com.nitsha.binds.configs.dto.variables.*;

import java.lang.reflect.Type;

public class VariableAdapter implements JsonSerializer<VariableData>, JsonDeserializer<VariableData> {
    @Override
    public JsonElement serialize(VariableData src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty("type", src.type);
        result.add("data", context.serialize(src, src.getClass()));
        return result;
    }

    @Override
    public VariableData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement data = jsonObject.get("data");
        return switch (type) {
            case "int" -> context.deserialize(data, AllVariablesData.IntVariableData.class);
            case "boolean" -> context.deserialize(data, AllVariablesData.BooleanVariableData.class);
            case "float" -> context.deserialize(data, AllVariablesData.FloatVariableData.class);
            case "string" -> context.deserialize(data, AllVariablesData.StringVariableData.class);
            default -> throw new JsonParseException("Unknown variable type: " + type);
        };
    }
}
