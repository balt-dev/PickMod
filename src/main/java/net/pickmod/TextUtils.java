package net.pickmod;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

import java.util.List;

public class TextUtils {
    public static Text joinTexts(Text ... texts) {
        return Texts.join(List.of(texts),Text.of(""));
    }

    public static Text clipText(Text text, int beginIndex, int endIndex) {
        String textContent = text.getString();
        Style textStyle = text.getStyle();
        return Text.of(textContent.substring(beginIndex, endIndex)).copy().fillStyle(textStyle);
    }
    public static Text clipText(Text text, int beginIndex) {
        String textContent = text.getString();
        Style textStyle = text.getStyle();
        return Text.of(textContent.substring(beginIndex)).copy().fillStyle(textStyle);
    }
}
