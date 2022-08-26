package net.pickmod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.pickmod.mixin.PlayerListHudFooterAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class StatObtainer {
    public enum Stats {
        BALANCE,
        BREAKING_SPEED,
        RAD_PROTECTION
    }
    public static boolean isOnPickaxe() {
        return getStat(null) != null;
    }
    public static Text joinTexts(Text ... texts) {
        return Texts.join(List.of(texts),Text.of(""));
    }

    public static Text clipText(Text text, int beginIndex, int endIndex) {
        String textContent = text.getString();
        Style textStyle = text.getStyle();
        return Text.of(textContent.substring(beginIndex, endIndex)).copy().fillStyle(textStyle);
    }
    public static Text getStat(@Nullable Stats stat) {
        MinecraftClient client = MinecraftClient.getInstance();
        Text hudFooter = ((PlayerListHudFooterAccessor) client.inGameHud.getPlayerListHud()).getFooter();
        if (hudFooter != null) { //this is hardcoded and will need updating as pickaxe updates
            List<Text> hudFooterSiblings = hudFooter.getSiblings();
            if (hudFooterSiblings.size() == 22 || hudFooterSiblings.size() == 21 || hudFooterSiblings.size() == 19) {
                if (Objects.equals(Text.Serializer.toJson(hudFooterSiblings.get(10)), "{\"color\":\"gold\",\"text\":\"⛀ \"}")) {
                    if (stat == null) return Text.of("");
                    switch (stat) {
                        case BALANCE -> {
                            return joinTexts(hudFooterSiblings.get(10), hudFooterSiblings.get(9));
                        }
                        case BREAKING_SPEED -> {
                            int offset = hudFooterSiblings.size() - 19;
                            return joinTexts(hudFooterSiblings.get(12+offset),hudFooterSiblings.get(13+offset));
                        }
                        case RAD_PROTECTION -> {
                            if (!Objects.equals(Text.Serializer.toJson(hudFooterSiblings.get(18)), "{\"color\":\"green\",\"text\":\"☄ \"}")) {
                                return Text.of("");
                            }
                            int offset = hudFooterSiblings.size() - 19;
                            return joinTexts(hudFooterSiblings.get(15+offset),clipText(hudFooterSiblings.get(16+offset), 0, hudFooterSiblings.get(16+offset).getString().length() - 1));
                        }
                    }
                }
            }
        }
        return null;
    }
}
