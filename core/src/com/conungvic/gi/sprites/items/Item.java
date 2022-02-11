package com.conungvic.gi.sprites.items;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.conungvic.gi.screens.PlayScreen;
import com.conungvic.gi.sprites.Mario;
import com.conungvic.gi.sprites.Revercable;

import static com.conungvic.gi.MarioBros.PPM;

public abstract class Item extends Sprite implements Revercable {
    protected PlayScreen screen;
    protected World world;
    protected Vector2 velocity;
    protected boolean toDestroy;
    protected boolean destroyed;
    protected Body body;

    public Item(PlayScreen screen, float x, float y) {
        this.screen = screen;
        this.world = screen.getWorld();
        setPosition(x, y);
        setBounds(getX(), getY(), 16 / PPM, 16 / PPM);
        defineItem();
        toDestroy = false;
        destroyed = false;
    }

    public abstract void defineItem();
    public abstract void use(Mario mario);

    public void update(float dt) {
        if (toDestroy && !destroyed) {
            world.destroyBody(this.body);
            destroyed = true;
        }
    }

    @Override
    public void draw(Batch batch) {
        if (!destroyed)
            super.draw(batch);
    }

    public void destroy() {
        this.toDestroy = true;
    }

    @Override
    public void reverseVelocity(boolean x, boolean y) {
        if (x)
            velocity.x = -velocity.x;
        if (y)
            velocity.y = -velocity.y;
    }
}
