package com.conungvic.gi.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.conungvic.gi.screens.PlayScreen;

import static com.conungvic.gi.MarioBros.BRICK_BIT;
import static com.conungvic.gi.MarioBros.COIN_BIT;
import static com.conungvic.gi.MarioBros.ENEMY_BIT;
import static com.conungvic.gi.MarioBros.GROUND_BIT;
import static com.conungvic.gi.MarioBros.MARIO_BIT;
import static com.conungvic.gi.MarioBros.OBJECT_BIT;
import static com.conungvic.gi.MarioBros.PPM;

public class Mario extends Sprite {
    public enum State {FALLING, JUMPING, STANDING, RUNNING}

    public State currentState;
    public State prevState;

    public World world;
    public Body b2body;

    private TextureRegion marioStand;
    private Animation<TextureRegion> marioRun;
    private Animation<TextureRegion> marioJump;

    private boolean runningRight;
    private float stateTimer;

    public Mario(PlayScreen screen) {
        super(screen.getAtlas().findRegion("little_mario"));
        this.world = screen.getWorld();

        this.currentState = State.STANDING;
        this.prevState = State.STANDING;
        this.stateTimer = 0;
        this.runningRight = true;

        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), i * 16, 0, 16, 16));
        }
        this.marioRun = new Animation<>(0.1f, frames);
        frames.clear();

        for (int i = 4; i < 6; i++) {
            frames.add(new TextureRegion(getTexture(), i * 16, 0, 16, 16));
        }
        this.marioJump = new Animation<>(0.1f, frames);


        defineMario();

        marioStand = new TextureRegion(getTexture(), 0, 0, 16, 16);
        setBounds(0, 0, 16 / PPM, 16 / PPM);
        setRegion(marioStand);
    }

    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();
        TextureRegion region;
        switch (currentState) {
            case JUMPING:
                region = marioJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioStand;
        }
        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        } else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == prevState ? stateTimer + dt : 0;
        prevState = currentState;
        return region;
    }

    private State getState() {
         if (b2body.getLinearVelocity().y > 0 ||
                 (b2body.getLinearVelocity().y < 0 && prevState == State.JUMPING)
         )
             return State.JUMPING;

         if (b2body.getLinearVelocity().y < 0)
             return State.FALLING;

         if (b2body.getLinearVelocity().x != 0)
             return State.RUNNING;

         return State.STANDING;
    }

    private void defineMario() {
        BodyDef bDef = new BodyDef();
        bDef.position.set(32 / PPM, 64 / PPM);
        bDef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        fDef.filter.categoryBits = MARIO_BIT;
        fDef.filter.maskBits = GROUND_BIT | COIN_BIT | BRICK_BIT | ENEMY_BIT | OBJECT_BIT;

        CircleShape shape = new CircleShape();
        shape.setRadius(6 / PPM);

        fDef.shape = shape;
        b2body.createFixture(fDef);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / PPM, 6 / PPM), new Vector2(2 / PPM, 6 / PPM));
        fDef.shape = head;
        fDef.isSensor = true;
        b2body.createFixture(fDef).setUserData("head");
    }
}
