package com.nitsha.binds.variables;

import java.util.function.Supplier;

public class Variable {
    public static enum VariableType { INT, FLOAT, STRING }

    public static class BuiltinVariable {
        public final String id;
        public final Supplier<Object> supplier;

        public BuiltinVariable(String id, Supplier<Object> supplier) {
            this.id = id;
            this.supplier = supplier;
        }
    }

    public static class UserVariable {
        public String id;
        public VariableType type;
        public Object value;
    }
}
