//? if fabric {
package com.nitsha.binds.configs;

import com.nitsha.binds.gui.screen.BindsEditor;
import com.terraformersmc.modmenu.api.*;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

@Environment(EnvType.CLIENT)
public class ModMenuApiImpl implements ModMenuApi
{
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> new BindsEditor(parent);
	}
}
//? }