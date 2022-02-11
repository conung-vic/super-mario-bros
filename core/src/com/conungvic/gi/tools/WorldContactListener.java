package com.conungvic.gi.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.conungvic.gi.sprites.Mario;
import com.conungvic.gi.sprites.Revercable;
import com.conungvic.gi.sprites.enemies.Enemy;
import com.conungvic.gi.sprites.items.Item;
import com.conungvic.gi.sprites.tiles.InteractiveTileObject;

import static com.conungvic.gi.MarioBros.BRICK_BIT;
import static com.conungvic.gi.MarioBros.COIN_BIT;
import static com.conungvic.gi.MarioBros.ENEMY_BIT;
import static com.conungvic.gi.MarioBros.ENEMY_HEAD_BIT;
import static com.conungvic.gi.MarioBros.ITEM_BIT;
import static com.conungvic.gi.MarioBros.MARIO_BIT;
import static com.conungvic.gi.MarioBros.MARIO_HEAD_BIT;
import static com.conungvic.gi.MarioBros.OBJECT_BIT;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef) {
            case MARIO_HEAD_BIT | BRICK_BIT:
            case MARIO_HEAD_BIT | COIN_BIT:
                if (fixA.getFilterData().categoryBits == MARIO_HEAD_BIT) {
                    ((InteractiveTileObject)fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                } else {
                    ((InteractiveTileObject)fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                }
                break;
            case ENEMY_HEAD_BIT | MARIO_BIT: {
                if (fixA.getFilterData().categoryBits == ENEMY_HEAD_BIT) {
                    ((Enemy)fixA.getUserData()).hitOnHead();
                } else {
                    ((Enemy)fixB.getUserData()).hitOnHead();
                }
                break;
            }
            case ENEMY_BIT | OBJECT_BIT:
            case ENEMY_BIT:
            case ITEM_BIT:
            case ITEM_BIT | ENEMY_BIT:
            case ITEM_BIT | OBJECT_BIT: {
                if (
                        fixA.getFilterData().categoryBits == ENEMY_BIT ||
                        fixA.getFilterData().categoryBits == ITEM_BIT
                ) {
                    ((Revercable) fixA.getUserData()).reverseVelocity(true, false);
                };

                if (
                        fixB.getFilterData().categoryBits == ENEMY_BIT ||
                        fixB.getFilterData().categoryBits == ITEM_BIT
                ) {
                    ((Revercable)fixB.getUserData()).reverseVelocity(true, false);
                }
                break;
            }
            case MARIO_BIT | ENEMY_BIT: {
                if (fixA.getFilterData().categoryBits == MARIO_BIT) {
                    ((Mario) fixA.getUserData()).hit();
                } else {
                    ((Mario) fixB.getUserData()).hit();
                }
                break;
            }
            case MARIO_BIT | ITEM_BIT: {
                if (fixA.getFilterData().categoryBits == ITEM_BIT) {
                    ((Item) fixA.getUserData()).use((Mario) fixB.getUserData());
                } else {
                    ((Item) fixB.getUserData()).use((Mario) fixA.getUserData());
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
