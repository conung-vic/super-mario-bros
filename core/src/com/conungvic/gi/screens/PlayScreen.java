package com.conungvic.gi.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.conungvic.gi.MarioBros;
import com.conungvic.gi.scenes.Hud;
import com.conungvic.gi.sprites.Mario;
import com.conungvic.gi.tools.B2WorldCreator;
import com.conungvic.gi.tools.WorldContactListener;

import static com.conungvic.gi.MarioBros.PPM;
import static com.conungvic.gi.MarioBros.V_HEIGHT;
import static com.conungvic.gi.MarioBros.V_WIDTH;

public class PlayScreen implements Screen {

    private final MarioBros game;
    private final TextureAtlas atlas;

    private final OrthographicCamera gamecam;
    private final Viewport gamePort;
    private final Hud hud;

    private final TmxMapLoader mapLoader;
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;

    private final World world;
    private final Box2DDebugRenderer b2dr;

    private final Mario player;

    public PlayScreen(MarioBros game) {
        this.atlas = new TextureAtlas("Mario_and_enemies.atlas");
        this.game = game;
        this.gamecam = new OrthographicCamera();
        this.gamePort = new FitViewport(V_WIDTH / PPM, V_HEIGHT / PPM, this.gamecam);
        this.hud = new Hud(game.batch);

        this.mapLoader = new TmxMapLoader();
        this.map = mapLoader.load("level1.tmx");

        this.renderer = new OrthogonalTiledMapRenderer(map, 1f / PPM);

        this.gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        this.world = new World(new Vector2(0, -10), true);
        this.b2dr = new Box2DDebugRenderer();

        B2WorldCreator.createBodiesForMap(world, map);

        this.player = new Mario(this);

        this.world.setContactListener(new WorldContactListener());
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public World getWorld() {
        return world;
    }

    public void update(float dt) {
        handleInput(dt);

        world.step(1/60f, 6, 2);

        player.update(dt);
        hud.update(dt);
        gamecam.position.x = player.b2body.getPosition().x;

        gamecam.update();
        renderer.setView(gamecam);
    }

    private void handleInput(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            player.b2body.applyLinearImpulse(
                    new Vector2(0, 4f),
                    player.b2body.getWorldCenter(),
                    true
            );
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2) {
            player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2) {
            player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();
        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        this.gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
