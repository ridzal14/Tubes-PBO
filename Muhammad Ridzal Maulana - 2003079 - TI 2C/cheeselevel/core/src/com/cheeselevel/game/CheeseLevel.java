package com.cheeselevel.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Camera;

public class CheeseLevel implements Screen {
	private Stage mainStage;
	private AnimatedActor mouse;
	private BaseActor cheese;
	private BaseActor cat;
	private BaseActor floor;
	private BaseActor winText;
	private BaseActor loseText;

	private float timeElapsed;
	private Label timeLabel;

	private Stage uiStage;

	private boolean win;
	private boolean lose;

	private final int SPEED = 250;

	private final int mapHeight = 800;
	private final int mapWidth = 800;

	private final int viewWidth = 640;
	private final int viewHeight = 480;

	public Game game;

	public CheeseLevel(Game g) {
		game = g;
		create();
	}

	public void create () {
		timeElapsed = 0;
		BitmapFont font = new BitmapFont();
		String text = "Time: 0";
		LabelStyle style = new LabelStyle(font, Color.NAVY);
		timeLabel = new Label(text, style);
		timeLabel.setFontScale(2);
		timeLabel.setPosition(500, 440);

		mainStage = new Stage();
		uiStage = new Stage();

		floor = new BaseActor();
		floor.setTexture(new Texture(Gdx.files.internal("tiles-800-800.jpeg")));
		floor.setPosition(0, 0);
		mainStage.addActor(floor);

		mouse = new AnimatedActor();

		TextureRegion[] frames = new TextureRegion[4];

		for (int n = 0; n < 4; n++) {
			Texture tex = new Texture(Gdx.files.internal("mouse"+n+".png"));
			tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
			frames[n] = new TextureRegion(tex);
		}

		Array<TextureRegion> framesArray = new Array<TextureRegion>(frames);

		Animation anim = new Animation(0.1f, framesArray, Animation.PlayMode.LOOP_PINGPONG);

		mouse.setAnimation(anim);
		mouse.setOrigin(mouse.getWidth() / 2, mouse.getHeight() / 2);
		mouse.setPosition(20,20);
		mainStage.addActor(mouse);

		cheese = new BaseActor();
		cheese.setTexture(new Texture(Gdx.files.internal("cheese.png")));
		cheese.setOrigin(cheese.getWidth() / 2, cheese.getHeight() / 2);
		cheese.setPosition(400, 500);
		mainStage.addActor(cheese);

		cat = new BaseActor();
		cat.setTexture(new Texture(Gdx.files.internal("cat.png")));
		cat.setOrigin(cat.getWidth() / 2, cat.getHeight() / 2);
		cat.setPosition(100, 300);
		mainStage.addActor(cat);

		winText = new BaseActor();
		winText.setTexture(new Texture(Gdx.files.internal("you-win.png")));
		winText.setPosition(170, 60);
		winText.setVisible(false);

		loseText = new BaseActor();
		loseText.setTexture(new Texture(Gdx.files.internal("you-lose.png")));
		loseText.setPosition(170, 60);
		loseText.setVisible(false);

		uiStage.addActor(winText);
		uiStage.addActor(loseText);
		uiStage.addActor(timeLabel);

		win = false;
		lose = false;
	}


	@Override
	public void show() {

	}

	public void render (float dt) {
		mouse.velocityX = 0;
		mouse.velocityY = 0;

		if (Gdx.input.isKeyPressed(Keys.M))
			game.setScreen(new CheeseMenu(game));

		if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT))
			mouse.velocityX -= SPEED;
		else if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT))
			mouse.velocityX += SPEED;
		else if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Keys.UP))
			mouse.velocityY += SPEED;
		else if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN))
			mouse.velocityY -= SPEED;

		mainStage.act(dt);
		uiStage.act(dt);

		mouse.setX(MathUtils.clamp(mouse.getX(), 0, mapWidth - mouse.getWidth()));
		mouse.setY(MathUtils.clamp(mouse.getY(), 0, mapHeight - mouse.getHeight()));

		Rectangle cheeseRectangle
				= cheese.getBoundingRectangle();
		Rectangle catRectangle
				= cat.getBoundingRectangle();
		Rectangle mouseRectangle
				= mouse.getBoundingRectangle();

		if ( !lose && cheeseRectangle.contains(mouseRectangle) ) {
			win = true;

			Action spinShrinkFadeOut = Actions.parallel(
					Actions.alpha(1),
					Actions.rotateBy(360, 1),
					Actions.scaleTo(0, 0, 1),
					Actions.fadeOut(1)
			);
			cheese.addAction(spinShrinkFadeOut);

			Action fadeInColorCycleForever = Actions.sequence(
					Actions.alpha(0),
					Actions.show(),
					Actions.fadeIn(2),
					Actions.forever(
							Actions.sequence(
									Actions.color(new Color(1,0,0,1), 1),
									Actions.color(new Color(0,0,1,1), 1)
							)
					)

			);

			winText.addAction(fadeInColorCycleForever);

			if (Gdx.input.isKeyPressed(Keys.P))
				game.setScreen(new CheeseLevel(game));
		}

		if ( !win && catRectangle.contains(mouseRectangle) ) {
			lose = true;

			Action spinShrinkFadeOut = Actions.parallel(
					Actions.alpha(1),
					Actions.rotateBy(360, 1),
					Actions.scaleTo(0, 0, 1),
					Actions.fadeOut(1)
			);
			mouse.addAction(spinShrinkFadeOut);

			Action fadeInColorCycleForever = Actions.sequence(
					Actions.alpha(0),
					Actions.show(),
					Actions.fadeIn(2),
					Actions.forever(
							Actions.sequence(
									Actions.color(new Color(1,0,0,1), 1),
									Actions.color(new Color(0,0,1,1), 1)
							)
					)
			);

			loseText.addAction(fadeInColorCycleForever);

			if (Gdx.input.isKeyPressed(Keys.P))
				game.setScreen(new CheeseLevel(game));
		}
		if (!win && !lose)
			timeElapsed += dt;
		timeLabel.setText("Time : " + (int)timeElapsed );

		Gdx.gl.glClearColor(0.8f, 0.8f, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Camera cam = mainStage.getCamera();
		cam.position.set(mouse.getX() + mouse.getOriginX(), mouse.getY() + mouse.getOriginY(), 0);

		cam.position.x = MathUtils.clamp(cam.position.x, viewWidth / 2, mapWidth - viewWidth / 2);
		cam.position.y = MathUtils.clamp(cam.position.y, viewHeight / 2, mapHeight - viewHeight / 2);
		cam.update();

		mainStage.draw();
		uiStage.draw();

	}

	@Override
	public void resize(int width, int height) {

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

	public void dispose () {

	}
}
