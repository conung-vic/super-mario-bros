package com.conungvic.gi.tools;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.conungvic.gi.sprites.Brick;
import com.conungvic.gi.sprites.Coin;

import static com.conungvic.gi.MarioBros.PPM;

public class B2WorldCreator {

    public static void createBodiesForMap(World world, TiledMap map) {
        createBodiesForLayer(world, map, "Ground");
        createBodiesForLayer(world, map, "Pipes");
//        createBodiesForLayer(world, map, "Coins");
//        createBodiesForLayer(world, map, "Bricks");

        for (RectangleMapObject mo: map.getLayers()
                .get("Coins")
                .getObjects()
                .getByType(RectangleMapObject.class)
        ) {
            Rectangle rect = mo.getRectangle();
            new Coin(world, map, rect);
        }

        for (RectangleMapObject mo: map.getLayers()
                .get("Bricks")
                .getObjects()
                .getByType(RectangleMapObject.class)
        ) {
            Rectangle rect = mo.getRectangle();
            new Brick(world, map, rect);
        }
    }

    private static void createBodiesForLayer(World world, TiledMap map, String layerName) {
        for (RectangleMapObject mo: map.getLayers()
                .get(layerName)
                .getObjects()
                .getByType(RectangleMapObject.class)
        ) {
            Rectangle rect = mo.getRectangle();
            createBody(world, rect);
        }
    }

    public static Body createBody(World world, Rectangle rect) {
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((rect.x + rect.width / 2) / PPM, (rect.y + rect.height /2) / PPM);

        Body body = world.createBody(bodyDef);
        shape.setAsBox(rect.width / 2 / PPM, rect.height / 2 / PPM);
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef);
        return body;
    }
}
