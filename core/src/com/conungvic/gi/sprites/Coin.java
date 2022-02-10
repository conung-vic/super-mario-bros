package com.conungvic.gi.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.conungvic.gi.MarioBros;
import com.conungvic.gi.scenes.Hud;

import static com.conungvic.gi.MarioBros.COIN_BIT;
import static com.conungvic.gi.MarioBros.PPM;

public class Coin extends InteractiveTileObject{
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;

    public Coin(World world, TiledMap map, Rectangle bounds) {
        super(world, map, bounds);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        setCategoryFilter(COIN_BIT);
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("Coin", "Collision");
        if (getCell().getTile().getId() == BLANK_COIN) {
            MarioBros.assetManager.get("audio/sounds/bump.wav", Sound.class).play();
        } else {
            MarioBros.assetManager.get("audio/sounds/coin.wav", Sound.class).play();
            Hud.addScore(100);
        }

        getCell().setTile(tileSet.getTile(BLANK_COIN));
    }
}
