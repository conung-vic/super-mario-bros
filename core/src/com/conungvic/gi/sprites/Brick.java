package com.conungvic.gi.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.conungvic.gi.MarioBros;
import com.conungvic.gi.scenes.Hud;

import static com.conungvic.gi.MarioBros.BRICK_BIT;
import static com.conungvic.gi.MarioBros.DESTROYED_BIT;

public class Brick extends InteractiveTileObject{
    public Brick(World world, TiledMap map, Rectangle bounds) {
        super(world, map, bounds);
        setCategoryFilter(BRICK_BIT);
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("Brick", "Collision");
        setCategoryFilter(DESTROYED_BIT);
        getCell().setTile(null);
        Hud.addScore(200);

        MarioBros.assetManager.get("audio/sounds/breakblock.wav", Sound.class).play();
    }
}
