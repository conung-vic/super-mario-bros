package com.conungvic.gi.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.conungvic.gi.MarioBros;
import com.conungvic.gi.scenes.Hud;
import com.conungvic.gi.sprites.Mario;
import com.conungvic.gi.sprites.enemies.Goomba;
import com.conungvic.gi.sprites.items.Item;
import com.conungvic.gi.sprites.items.ItemDef;
import com.conungvic.gi.sprites.items.Mushroom;
import com.conungvic.gi.tools.B2WorldCreator;
import com.conungvic.gi.tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

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

    private final Music music;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

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

        B2WorldCreator.createBodiesForMap(this);


        this.player = new Mario(this);

        this.world.setContactListener(new WorldContactListener());

        this.music = MarioBros.assetManager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.play();

        items = new Array<>();
        itemsToSpawn = new LinkedBlockingQueue<>();
    }

    public void spawnItem(ItemDef def) {
        itemsToSpawn.add(def);
    }

    public void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll();
            if (idef.type == Mushroom.class) {
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }

    public TiledMap getMap() {
        return map;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public World getWorld() {
        return world;
    }

    public void update(float dt) {
        handleInput(dt);
        handleSpawningItems();

        world.step(1 / 60f, 6, 2);

        player.update(dt);


        for (Goomba goomba : B2WorldCreator.goombas) {
            goomba.update(dt);
            if (goomba.getX() < player.getX() + 224 / PPM) {
                goomba.b2body.setActive(true);
            }
        }

        for (Item item : items) {
            item.update(dt);
        }

        hud.update(dt);
        if (player.currentState != Mario.State.DEAD) {
            gamecam.position.x = player.b2body.getPosition().x;
        }

        gamecam.update();
        renderer.setView(gamecam);
    }

    private void handleInput(float dt) {
        if (player.currentState == Mario.State.DEAD) {
            return;
        }

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
        for (Goomba goomba : B2WorldCreator.goombas) {
            goomba.draw(game.batch);
        }

        for (Item item : items) {
            item.draw(game.batch);
        }
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if (gameOver()) {
            dispose();
            Gdx.app.log("PlayScreen", "Mario died, show gameover screen");
            game.setScreen(new GameOverScreen(game));
        }
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

    public Music getMusic() {
        return music;
    }

    public boolean gameOver() {
        if (player.currentState == Mario.State.DEAD && player.getStateTimer() > 3) {
            return true;
        } else {
            return false;
        }
    }
}
