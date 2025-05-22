package io.github._20nickname20.imbored.handlers;

import com.badlogic.gdx.physics.box2d.*;
import io.github._20nickname20.imbored.game_objects.Entity;

public class EntityContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();
        if (bodyA.getUserData() instanceof Entity entity) {
            entity.beginContact(bodyB);
        }
        if (bodyB.getUserData() instanceof Entity entity) {
            entity.beginContact(bodyA);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();
        if (bodyA.getUserData() instanceof Entity entity) {
            entity.endContact(bodyB);
        }
        if (bodyB.getUserData() instanceof Entity entity) {
            entity.endContact(bodyA);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
