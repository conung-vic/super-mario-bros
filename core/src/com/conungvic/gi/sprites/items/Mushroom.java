package com.conungvic.gi.sprites.items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.conungvic.gi.screens.PlayScreen;
import com.conungvic.gi.sprites.Mario;

import static com.conungvic.gi.MarioBros.BRICK_BIT;
import static com.conungvic.gi.MarioBros.COIN_BIT;
import static com.conungvic.gi.MarioBros.GROUND_BIT;
import static com.conungvic.gi.MarioBros.ITEM_BIT;
import static com.conungvic.gi.MarioBros.MARIO_BIT;
import static com.conungvic.gi.MarioBros.OBJECT_BIT;
import static com.conungvic.gi.MarioBros.PPM;

public class Mushroom extends Item {

    public Mushroom(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("mushroom"), 0, 0, 16, 16);
        velocity = new Vector2(0.7f, 0);
    }

    @Override
    public void defineItem() {
        BodyDef bDef = new BodyDef();
        bDef.position.set(getX(), getY());
        bDef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        fDef.filter.categoryBits = ITEM_BIT;
        fDef.filter.maskBits = GROUND_BIT |
                MARIO_BIT |
                OBJECT_BIT |
                ITEM_BIT |
                COIN_BIT |
                BRICK_BIT;

        CircleShape shape = new CircleShape();
        shape.setRadius(6 / PPM);

        fDef.shape = shape;
        body.createFixture(fDef).setUserData(this);
    }

    @Override
    public void use(Mario mario) {
        destroy();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (!destroyed) {
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
            velocity.y = body.getLinearVelocity().y;
            body.setLinearVelocity(velocity);
        }
    }
}
