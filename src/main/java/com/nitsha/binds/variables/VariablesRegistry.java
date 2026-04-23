package com.nitsha.binds.variables;

import com.nitsha.binds.configs.Storage;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariablesRegistry {
    private static final Map<String, Variable.BuiltinVariable> BUILTINS = new LinkedHashMap<>();
    private static final Map<String, Variable.UserVariable> USER_VARS = new LinkedHashMap<>();

    static {
        register("player.x",        () -> Minecraft.getInstance().player.getX());
        register("player.y",        () -> Minecraft.getInstance().player.getY());
        register("player.z",        () -> Minecraft.getInstance().player.getZ());
        register("player.yaw",      () -> Minecraft.getInstance().player.getYRot());
        register("player.pitch",    () -> Minecraft.getInstance().player.getXRot());
        register("player.nickname", () -> Minecraft.getInstance().player.getName().getString());
        register("player.health",   () -> Minecraft.getInstance().player.getHealth());
    }

    public static void register(String id, Supplier<Object> supplier) {
        BUILTINS.put(id, new Variable.BuiltinVariable(id, supplier));
    }

    public static void loadUserVars(File file) {
        Variable.UserVariable[] loaded = Storage.load(Variable.UserVariable[].class, file, new Variable.UserVariable[0]);
        USER_VARS.clear();
        for (Variable.UserVariable v : loaded) USER_VARS.put(v.id, v);
    }

    public static String resolve(String text) {
        StringBuffer sb = new StringBuffer();
        Matcher m = Pattern.compile("\\{([^}]+)}").matcher(text);
        while (m.find()) {
            String id = m.group(1);
            String value = resolveOne(id);
            m.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String resolveOne(String id) {
        if (BUILTINS.containsKey(id)) return String.valueOf(BUILTINS.get(id).supplier.get());
        if (USER_VARS.containsKey(id)) return String.valueOf(USER_VARS.get(id).value);
        return "{" + id + "}";
    }

    public static void setUserVar(String id, Variable.VariableType type, Object value) {
        Variable.UserVariable v = new Variable.UserVariable();
        v.id = id; v.type = type; v.value = value;
        USER_VARS.put(id, v);
    }

    public static void removeUserVar(String id) { USER_VARS.remove(id); }
    public static Map<String, Variable.BuiltinVariable> getBuiltins() { return Collections.unmodifiableMap(BUILTINS); }
    public static Map<String, Variable.UserVariable> getUserVars() { return Collections.unmodifiableMap(USER_VARS); }




}
