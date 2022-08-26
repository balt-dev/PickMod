package net.pickmod;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class PeriodicBackup {
    public static void register(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (PickMod.config.hoursBetweenAutoBackup != 0) {
                long timeNow = System.currentTimeMillis();
                assert client.player != null;
                if (timeNow - PickMod.config.lastBackup > (PickMod.config.hoursBetweenAutoBackup * 3600000) && StatObtainer.isOnPickaxe()) {
                    new Thread(()->{
                        PickMod.config.lastBackup = timeNow;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        client.player.sendChatMessage("@backup");
                    }).start();
                }
            }
        });
    }
}
