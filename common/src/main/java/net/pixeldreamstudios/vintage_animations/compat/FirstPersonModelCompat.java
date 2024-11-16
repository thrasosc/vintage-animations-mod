package net.pixeldreamstudios.vintage_animations.compat;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.tr7zw.firstperson.FirstPersonModelCore;

public class FirstPersonModelCompat {
    public FirstPersonModelCompat(KeyframeAnimationPlayer animPlayer) {
        if (FirstPersonModelCore.instance.isEnabled())
            animPlayer.setFirstPersonMode(FirstPersonMode.NONE);
        else animPlayer.setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL);
    }
}
