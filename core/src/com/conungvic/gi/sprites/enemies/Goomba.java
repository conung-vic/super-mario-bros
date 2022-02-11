package com.conungvic.gi.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.conungvic.gi.scenes.Hud;
import com.conungvic.gi.screens.PlayScreen;

import static com.conungvic.gi.MarioBros.BRICK_BIT;
import static com.conungvic.gi.MarioBros.COIN_BIT;
import static com.conungvic.gi.MarioBros.ENEMY_BIT;
import static com.conungvic.gi.MarioBros.ENEMY_HEAD_BIT;
import static com.conungvic.gi.MarioBros.GROUND_BIT;
import static com.conungvic.gi.MarioBros.MARIO_BIT;
import static com.conungvic.gi.MarioBros.OBJECT_BIT;
import static com.conungvic.gi.MarioBros.PPM;

public class Goomba extends Enemy {

    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;

    private boolean setToDestroy = false;
    private boolean destroyed = false;

    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<>();
        for (int i =0; i<2; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i*16, 0, 16, 16));
        }
        walkAnimation = new Animation<>(0.4f, frames);
        stateTime = 0;
        setBounds(getX(), getY(), 16 / PPM, 16 / PPM);
        b2body.setActive(false);
    }

    @Override
    public void update(float dt) {
        stateTime += dt;
        if (setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
            setToDestroy = false;

            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16));
            stateTime = 0;
        } else if (!destroyed) {
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(walkAnimation.getKeyFrame(stateTime, true));
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bDef = new BodyDef();
        bDef.position.set(getX(), getY());
        bDef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        fDef.filter.categoryBits = ENEMY_BIT;
        fDef.filter.maskBits = GROUND_BIT | COIN_BIT | BRICK_BIT | ENEMY_BIT | OBJECT_BIT | MARIO_BIT;

        CircleShape shape = new CircleShape();
        shape.setRadius(6 / PPM);

        fDef.shape = shape;
        b2body.createFixture(fDef).setUserData(this);

        // create the head
        PolygonShape head = new PolygonShape();
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-5, 8).scl(1 / PPM);
        vertices[1] = new Vector2( 5, 8).scl(1 / PPM);
        vertices[2] = new Vector2(-3, 3).scl(1 / PPM);
        vertices[3] = new Vector2( 3, 3).scl(1 / PPM);
        head.set(vertices);

        fDef.shape = head;
        fDef.restitution = 0.5f;
        fDef.filter.categoryBits = ENEMY_HEAD_BIT;
        b2body.createFixture(fDef).setUserData(this);
    }

    @Override
    public void hitOnHead() {
        setToDestroy = true;
        Hud.addScore(50);
    }

    @Override
    public void draw(Batch batch) {
        if (!destroyed || stateTime < 1) {
            super.draw(batch);
        }
    }
}
