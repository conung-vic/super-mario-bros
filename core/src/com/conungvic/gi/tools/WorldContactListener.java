package com.conungvic.gi.tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.conungvic.gi.sprites.Enemy;
import com.conungvic.gi.sprites.InteractiveTileObject;

import static com.conungvic.gi.MarioBros.ENEMY_HEAD_BIT;
import static com.conungvic.gi.MarioBros.MARIO_BIT;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        if ("head".equals(fixA.getUserData()) ||
                "head".equals(fixB.getUserData())
        ) {
            Fixture head = "head".equals(fixA.getUserData()) ? fixA : fixB;
            Fixture object = "head".equals(fixA.getUserData()) ? fixB : fixA;

            if (object.getUserData() instanceof InteractiveTileObject) {
                ((InteractiveTileObject)object.getUserData()).onHeadHit();
            }
        }

        switch (cDef) {
            case ENEMY_HEAD_BIT | MARIO_BIT: {
                if (fixA.getFilterData().categoryBits == ENEMY_HEAD_BIT) {
                    ((Enemy)fixA.getUserData()).hitOnHead();
                }
                if (fixB.getFilterData().categoryBits == ENEMY_HEAD_BIT) {
                    ((Enemy)fixB.getUserData()).hitOnHead();
                }
                break;
            }
            default: {}
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
