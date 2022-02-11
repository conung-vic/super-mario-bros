package com.conungvic.gi.tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.conungvic.gi.sprites.enemies.Enemy;
import com.conungvic.gi.sprites.tiles.InteractiveTileObject;

import static com.conungvic.gi.MarioBros.ENEMY_BIT;
import static com.conungvic.gi.MarioBros.ENEMY_HEAD_BIT;
import static com.conungvic.gi.MarioBros.MARIO_BIT;
import static com.conungvic.gi.MarioBros.OBJECT_BIT;

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
                } else {
                    ((Enemy)fixB.getUserData()).hitOnHead();
                }
                break;
            }
            case ENEMY_BIT | OBJECT_BIT: {
                if (fixA.getFilterData().categoryBits == ENEMY_BIT) {
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                } else {
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                }
                break;
            }
            case ENEMY_BIT: {
                ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;
            }
            case MARIO_BIT | ENEMY_BIT: {

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
