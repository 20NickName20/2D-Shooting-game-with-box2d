package io.github._20nickname20.imbored.game_objects.entities;

import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;

public interface Grabbable {
    PlayerEntity getGrabber();
    void onGrabbed(PlayerEntity grabber);
    void onPutted(PlayerEntity grabber);
}
