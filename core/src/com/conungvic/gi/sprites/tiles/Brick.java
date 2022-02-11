package com.conungvic.gi.sprites.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import com.conungvic.gi.MarioBros;
import com.conungvic.gi.scenes.Hud;
import com.conungvic.gi.screens.PlayScreen;

import static com.conungvic.gi.MarioBros.BRICK_BIT;
import static com.conungvic.gi.MarioBros.DESTROYED_BIT;

public class Brick extends InteractiveTileObject{
    public Brick(PlayScreen screen, MapObject object) {
        super(screen, object);
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
