package com.conungvic.gi.tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.conungvic.gi.sprites.InteractiveTileObject;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if ("head".equals(fixA.getUserData()) ||
                "head".equals(fixB.getUserData())
        ) {
            Fixture head = "head".equals(fixA.getUserData()) ? fixA : fixB;
            Fixture object = "head".equals(fixA.getUserData()) ? fixB : fixA;

            if (object.getUserData() instanceof InteractiveTileObject) {
                ((InteractiveTileObject)object.getUserData()).onHeadHit();
            }
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
