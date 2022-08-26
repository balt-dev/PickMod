package net.pickmod.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.pickmod.PickMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    MinecraftClient client = MinecraftClient.getInstance();
    public void broadcast(Text message) {this.client.inGameHud.getChatHud().addMessage(message);}
    @Inject(at = @At("HEAD"), method = "sendChatMessage(Ljava/lang/String;)V")
    public void sendChatMessage(String message, CallbackInfo ci) throws IOException {
        assert this.client.player != null;
        //will add more later
        if ("@backup".equals(message)) {
            if (!Files.exists(FabricLoader.getInstance().getGameDir().resolve("pickaxe_backups"))) {
                Files.createDirectory(FabricLoader.getInstance().getGameDir().resolve("pickaxe_backups"));
            }
            Path saveDirectory = FabricLoader.getInstance().getGameDir().resolve("pickaxe_backups").resolve("pickaxe_backup-" + (System.currentTimeMillis() / 1000) + ".txt");
            String[] items = new String[41];
            PlayerInventory inventory = this.client.player.getInventory();
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack itemStack = inventory.getStack(i);
                items[i] = "Slot " + (i + 1) + ": " + itemStack.getCount() + "x " + itemStack.getItem().toString() + itemStack.getOrCreateNbt().asString();
            }
            try (FileWriter fileWriter = new FileWriter(saveDirectory.toAbsolutePath().toString())) {
                fileWriter.write(String.join("\n", items));
                broadcast(Text.Serializer.fromJson("[\"\",{\"text\":\"[\",\"obfuscated\":false,\"color\":\"dark_gray\"},{\"text\":\"⛏\",\"color\":\"green\"},{\"text\":\"Mod\",\"color\":\"yellow\"},{\"text\":\"]\",\"color\":\"dark_gray\"},{\"text\":\" Inventory saved to \",\"color\":\"gray\"},{\"text\":\"{path}\",\"underlined\":true,\"color\":\"white\",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"{path}\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"Click to copy to clipboard\",\"color\":\"white\"}]}},{\"text\":\".\",\"color\":\"gray\"}]"
                        .replaceAll("\\{path}", saveDirectory.toAbsolutePath().toString().replaceAll("\\\\","/"))));
            } catch (IOException e) {
                broadcast(Text.Serializer.fromJson("[\"\",{\"text\":\"[\",\"obfuscated\":false,\"color\":\"dark_gray\"},{\"text\":\"⛏\",\"color\":\"green\"},{\"text\":\"Mod\",\"color\":\"yellow\"},{\"text\":\"] \",\"color\":\"dark_gray\"},{\"text\":\"Error saving inventory!\",\"color\":\"gray\"}]"));
            }
        }
    }
}