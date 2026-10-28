package com.nitsha.binds.configs.dto.variables;

public abstract class VariableData {
   public String name;
   public transient String type;

   public VariableData(String name, String type) {
      this.name = name;
      this.type = type;
   }

   public abstract Object getValue();
}