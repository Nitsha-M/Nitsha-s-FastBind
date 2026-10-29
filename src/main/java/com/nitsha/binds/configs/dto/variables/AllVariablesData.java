package com.nitsha.binds.configs.dto.variables;

public class AllVariablesData {

    public class IntVariableData extends VariableData {
        public int value;
        public IntVariableData(String name, int value) {
            super(name, "int");
            this.value = value;
        }
        @Override public Object getValue() { return value; }
    }

    public class BooleanVariableData extends VariableData {
        public boolean value;
        public BooleanVariableData(String name, boolean value) {
            super(name, "boolean");
            this.value = value;
        }
        @Override public Object getValue() { return value; }
    }

    public class FloatVariableData extends VariableData {
        public float value;
        public FloatVariableData(String name, float value) {
            super(name, "float");
            this.value = value;
        }
        @Override public Object getValue() { return value; }
    }

    public class StringVariableData extends VariableData {
        public String value;
        public StringVariableData(String name, String value) {
            super(name, "string");
            this.value = value;
        }
        @Override public Object getValue() { return value; }
    }

}
