package com.conungvic.gi.sprites;

import com.badlogic.gdx.audio.Sound;
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
import com.conungvic.gi.MarioBros;
import com.conungvic.gi.screens.PlayScreen;

import static com.conungvic.gi.MarioBros.BRICK_BIT;
import static com.conungvic.gi.MarioBros.COIN_BIT;
import static com.conungvic.gi.MarioBros.ENEMY_BIT;
import static com.conungvic.gi.MarioBros.ENEMY_HEAD_BIT;
import static com.conungvic.gi.MarioBros.GROUND_BIT;
import static com.conungvic.gi.MarioBros.ITEM_BIT;
import static com.conungvic.gi.MarioBros.MARIO_BIT;
import static com.conungvic.gi.MarioBros.MARIO_HEAD_BIT;
import static com.conungvic.gi.MarioBros.OBJECT_BIT;
import static com.conungvic.gi.MarioBros.PPM;

public class Mario extends Sprite {


    public enum State {FALLING, JUMPING, STANDING, RUNNING, GROWING;}

    public State currentState;
    public State prevState;

    public World world;
    public Body b2body;

    private TextureRegion marioStand;
    private TextureRegion marioJump;
    private Animation<TextureRegion> marioRun;

    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation<TextureRegion> bigMarioRun;
    private Animation<TextureRegion> growMario;

    private boolean runningRight;
    private float stateTimer;

    private boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario = false;
    private boolean timeToRedefineMario = false;

    public Mario(PlayScreen screen) {
        this.world = screen.getWorld();

        this.currentState = State.STANDING;
        this.prevState = State.STANDING;
        this.stateTimer = 0;
        this.runningRight = true;

        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        }
        this.marioRun = new Animation<>(0.1f, frames);
        frames.clear();

        this.marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        frames.clear();
        defineMario();

        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        setBounds(0, 0, 16 / PPM, 16 / PPM);
        setRegion(marioStand);

        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);
        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        }
        this.bigMarioRun = new Animation<>(0.1f, frames);

        frames.clear();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation<>(0.2f, frames);
    }

    public void update(float dt) {
        if (marioIsBig) {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6/PPM);
        } else {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }
        setRegion(getFrame(dt));
        if (timeToDefineBigMario)
            defineBigMario();
        if (timeToRedefineMario)
            redefineMario();
    }

    public boolean isBig() {
        return marioIsBig;
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();
        TextureRegion region;
        switch (currentState) {
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if (growMario.isAnimationFinished(stateTimer)) {
                    runGrowAnimation = false;
                }
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig ?
                        bigMarioRun.getKeyFrame(stateTimer, true) :
                        marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig ? bigMarioStand : marioStand;
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

    public void grow() {
        runGrowAnimation = true;
        marioIsBig = true;
        timeToDefineBigMario = true;
        setBounds(getX(), getY(), getWidth(), getHeight() * 2);
        MarioBros.assetManager.get("audio/sounds/powerup.wav", Sound.class).play();
    }

    private State getState() {
        if (runGrowAnimation) {
            return State.GROWING;
        }
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

    private void defineBigMario() {
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bDef = new BodyDef();
        bDef.position.set(currentPosition.add(0, 10 / PPM));
        bDef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        fDef.filter.categoryBits = MARIO_BIT;
        fDef.filter.maskBits = GROUND_BIT |
                COIN_BIT |
                BRICK_BIT |
                ENEMY_BIT |
                OBJECT_BIT |
                ENEMY_HEAD_BIT |
                ITEM_BIT;

        CircleShape shape = new CircleShape();
        shape.setRadius(6 / PPM);

        fDef.shape = shape;
        b2body.createFixture(fDef).setUserData(this);

        shape.setPosition(new Vector2(0, -14 / PPM));
        b2body.createFixture(fDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / PPM, 6 / PPM), new Vector2(2 / PPM, 6 / PPM));
        fDef.filter.categoryBits = MARIO_HEAD_BIT;
        fDef.shape = head;
        fDef.isSensor = true;
        b2body.createFixture(fDef).setUserData(this);
        timeToDefineBigMario = false;
    }

    private void redefineMario() {
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bDef = new BodyDef();
        bDef.position.set(currentPosition);
        bDef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        fDef.filter.categoryBits = MARIO_BIT;
        fDef.filter.maskBits = GROUND_BIT |
                COIN_BIT |
                BRICK_BIT |
                ENEMY_BIT |
                OBJECT_BIT |
                ENEMY_HEAD_BIT |
                ITEM_BIT;

        CircleShape shape = new CircleShape();
        shape.setRadius(6 / PPM);

        fDef.shape = shape;
        b2body.createFixture(fDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / PPM, 6 / PPM), new Vector2(2 / PPM, 6 / PPM));
        fDef.filter.categoryBits = MARIO_HEAD_BIT;
        fDef.shape = head;
        fDef.isSensor = true;
        b2body.createFixture(fDef).setUserData(this);

        timeToRedefineMario = false;
    }

    private void defineMario() {
        BodyDef bDef = new BodyDef();
        bDef.position.set(32 / PPM, 64 / PPM);
        bDef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        fDef.filter.categoryBits = MARIO_BIT;
        fDef.filter.maskBits = GROUND_BIT |
                COIN_BIT |
                BRICK_BIT |
                ENEMY_BIT |
                OBJECT_BIT |
                ENEMY_HEAD_BIT |
                ITEM_BIT;

        CircleShape shape = new CircleShape();
        shape.setRadius(6 / PPM);

        fDef.shape = shape;
        b2body.createFixture(fDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / PPM, 6 / PPM), new Vector2(2 / PPM, 6 / PPM));
        fDef.filter.categoryBits = MARIO_HEAD_BIT;
        fDef.shape = head;
        fDef.isSensor = true;
        b2body.createFixture(fDef).setUserData(this);
    }

    public void hit() {
        if (marioIsBig) {
            marioIsBig = false;
            timeToRedefineMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() /2);
            MarioBros.assetManager.get("audio/sounds/powerdown.wav", Sound.class).play();
        } else {

            MarioBros.assetManager.get("audio/sounds/mariodie.wav", Sound.class).play();
        }
    }
}
