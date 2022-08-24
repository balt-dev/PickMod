package net.pickmod;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.pickmod.mixin.PlayerListHudFooterAccessor;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class PickMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("pickmod");
	public static ModConfig config;
	public static Text getBalance() { //this also works as "is the player on pickaxe" becuase i'm lazy
		MinecraftClient client = MinecraftClient.getInstance();
		Text hudFooter = ((PlayerListHudFooterAccessor) client.inGameHud.getPlayerListHud()).getFooter();
		if (hudFooter != null) { //this is hardcoded and will need updating as pickaxe updates
			List<Text> hudFooterSiblings = hudFooter.getSiblings();
			if (hudFooterSiblings.size() == 21 || hudFooterSiblings.size() == 19) {
				if (Objects.equals(Text.Serializer.toJson(hudFooterSiblings.get(10)), "{\"color\":\"gold\",\"text\":\"⛀ \"}")) {
					Collection<Text> textCollection = new ArrayList();
					textCollection.add(hudFooterSiblings.get(10));
					textCollection.add(hudFooterSiblings.get(9));
					return Texts.join(textCollection,Text.of(""));
				}
			}
		}
		return null;
	}

	@Override
	public void onInitialize() {
		PickKeybinds.register();
		AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
	}
}
