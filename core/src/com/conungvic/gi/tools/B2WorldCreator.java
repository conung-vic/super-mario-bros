package com.conungvic.gi.tools;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.conungvic.gi.screens.PlayScreen;
import com.conungvic.gi.sprites.Brick;
import com.conungvic.gi.sprites.Coin;

import static com.conungvic.gi.MarioBros.GROUND_BIT;
import static com.conungvic.gi.MarioBros.OBJECT_BIT;
import static com.conungvic.gi.MarioBros.PPM;

public class B2WorldCreator {

    public static void createBodiesForMap(PlayScreen screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        createBodiesForLayer(world, map, "Ground", GROUND_BIT);
        createBodiesForLayer(world, map, "Pipes", OBJECT_BIT);

        for (RectangleMapObject mo: map.getLayers()
                .get("Coins")
                .getObjects()
                .getByType(RectangleMapObject.class)
        ) {
            Rectangle rect = mo.getRectangle();
            new Coin(screen, rect);
        }

        for (RectangleMapObject mo: map.getLayers()
                .get("Bricks")
                .getObjects()
                .getByType(RectangleMapObject.class)
        ) {
            Rectangle rect = mo.getRectangle();
            new Brick(screen, rect);
        }
    }

    private static void createBodiesForLayer(World world,
                                             TiledMap map,
                                             String layerName,
                                             short objectBit) {
        for (RectangleMapObject mo: map.getLayers()
                .get(layerName)
                .getObjects()
                .getByType(RectangleMapObject.class)
        ) {
            Rectangle rect = mo.getRectangle();
            createBody(world, rect, objectBit);
        }
    }

    public static Body createBody(World world, Rectangle rect, short objectBit) {
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((rect.x + rect.width / 2) / PPM, (rect.y + rect.height /2) / PPM);

        Body body = world.createBody(bodyDef);
        shape.setAsBox(rect.width / 2 / PPM, rect.height / 2 / PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = objectBit;
        body.createFixture(fixtureDef);
        return body;
    }
}
