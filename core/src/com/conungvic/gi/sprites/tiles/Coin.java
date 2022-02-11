package com.conungvic.gi.sprites.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.conungvic.gi.MarioBros;
import com.conungvic.gi.scenes.Hud;
import com.conungvic.gi.screens.PlayScreen;
import com.conungvic.gi.sprites.Mario;
import com.conungvic.gi.sprites.items.ItemDef;
import com.conungvic.gi.sprites.items.Mushroom;

import static com.conungvic.gi.MarioBros.COIN_BIT;
import static com.conungvic.gi.MarioBros.PPM;

public class Coin extends InteractiveTileObject{
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;

    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        setCategoryFilter(COIN_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (getCell().getTile().getId() == BLANK_COIN) {
            MarioBros.assetManager.get("audio/sounds/bump.wav", Sound.class).play();
        } else {
            if (mapObject.getProperties().containsKey("mushroom")) {
                MarioBros.assetManager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / PPM), Mushroom.class));
            } else {
                MarioBros.assetManager.get("audio/sounds/coin.wav", Sound.class).play();
            }
            Hud.addScore(100);
        }

        getCell().setTile(tileSet.getTile(BLANK_COIN));
    }
}
