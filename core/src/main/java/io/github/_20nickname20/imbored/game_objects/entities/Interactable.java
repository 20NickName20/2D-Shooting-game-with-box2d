package io.github._20nickname20.imbored.game_objects.entities;

import io.github._20nickname20.imbored.game_objects.Interact;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;

public interface Interactable {
    void startInteracting(PlayerEntity player);
    void stopInteracting(PlayerEntity player);
    void interact(PlayerEntity player, Interact interact, boolean state);
}
