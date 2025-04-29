package io.github._20nickname20.imbored.handlers;

import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.Fixture;
import io.github._20nickname20.imbored.Entity;
import io.github._20nickname20.imbored.entities.ItemEntity;
import io.github._20nickname20.imbored.entities.living.human.cursor.PlayerEntity;

public class EntityContactFilter implements ContactFilter {
    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
        Entity entityA = Entity.getEntity(fixtureA.getBody());
        if (entityA == null) return true;
        Entity entityB = Entity.getEntity(fixtureB.getBody());
        if (entityB == null) return true;
        return entityA.shouldCollide(entityB) && entityB.shouldCollide(entityA);
    }
}
