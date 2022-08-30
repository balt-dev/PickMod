package net.pickmod;

import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.time.Instant;

public class StatusEffect {/*
    int startingCountdown;
    long startingTime;
    Text name;

    Text icon;
    Style durationStyle;
    public StatusEffect(Text name, Text icon, Text duration) {
        this.name = name;
        this.icon = icon;
        this.durationStyle = duration.getStyle();
        String durationString = duration.getString();
        int hours = Integer.parseInt(durationString.substring(0,2));
        int minutes = Integer.parseInt(durationString.substring(3,5));
        int seconds = Integer.parseInt(durationString.substring(6,8));
        this.startingCountdown = (hours * 3600) + (minutes * 60) + seconds;
        this.startingTime = Instant.now().getEpochSecond();
    }
    public StatusEffect fromRaw(Text name, Text icon, Text duration) {
        name = TextUtils.clipText(name,1);
        duration = TextUtils.clipText(duration,0,duration.getString().length() - 1);
        return new StatusEffect(name, icon, duration);
    }
    public Text getDurationTime() {

    }*/
    /*
    TODO:
     rethink how i'm doing this
     this probably won't work out during implementation
     */
}
