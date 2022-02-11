package com.conungvic.gi.sprites.tiles;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.conungvic.gi.screens.PlayScreen;

import static com.conungvic.gi.MarioBros.PPM;

public abstract class InteractiveTileObject {
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;
    protected PlayScreen screen;
    protected MapObject mapObject;

    public InteractiveTileObject(PlayScreen screen, MapObject object) {
        this.mapObject = object;
        this.screen = screen;
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.bounds = ((RectangleMapObject)object).getRectangle();

        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((bounds.x + bounds.width / 2) / PPM, (bounds.y + bounds.height /2) / PPM);

        body = world.createBody(bodyDef);
        shape.setAsBox(bounds.width / 2 / PPM, bounds.height / 2 / PPM);
        fixtureDef.shape = shape;

        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
    }

    public abstract void onHeadHit();

    public void setCategoryFilter(short filterBit) {
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public TiledMapTileLayer.Cell getCell() {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("Graphic Layer");
        int x = (int) (body.getPosition().x * PPM / 16);
        int y = (int) (body.getPosition().y * PPM / 16);
        return layer.getCell(x, y);
    }
}
