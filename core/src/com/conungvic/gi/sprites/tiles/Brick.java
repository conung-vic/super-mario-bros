package com.conungvic.gi.sprites.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import com.conungvic.gi.MarioBros;
import com.conungvic.gi.scenes.Hud;
import com.conungvic.gi.screens.PlayScreen;
import com.conungvic.gi.sprites.Mario;

import static com.conungvic.gi.MarioBros.BRICK_BIT;
import static com.conungvic.gi.MarioBros.DESTROYED_BIT;

public class Brick extends InteractiveTileObject{
    public Brick(PlayScreen screen, MapObject object) {
        super(screen, object);
        setCategoryFilter(BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (mario.isBig()) {
            setCategoryFilter(DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(200);

            MarioBros.assetManager.get("audio/sounds/breakblock.wav", Sound.class).play();
        } else {
            MarioBros.assetManager.get("audio/sounds/bump.wav", Sound.class).play();
        }
    }
}
