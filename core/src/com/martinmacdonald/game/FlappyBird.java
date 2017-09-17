package com.martinmacdonald.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

import sun.rmi.runtime.Log;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
    Texture topTube;
    Texture bottomTube;

    float topTubeY;
    float bottomTubeY;

    Texture[] birds;
    int flapState = 0;
    float birdY = 0;
    float velocity = 0;
    float gap = 500;
    float maxTubeOffset;
    Random rand;
    float tubeVelocity = 10;


    int numberOfTubes = 4;
    float distanceBetweenTubes;
    float[] tubeX = new float[numberOfTubes];
    float[] tubeOffset = new float[numberOfTubes];

    int gameState = 0;
    float gravity = 3;

    Circle birdCircle;
    ShapeRenderer shapeRenderer;
    Rectangle[] pipeRectangleTop;
    Rectangle[] pipeRectangleBottom;

    int scoringTube = 0;
    int score = 0;

    BitmapFont font;

    Texture gameover;

    public void startGame(){

        birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

        for (int i = 0; i < numberOfTubes; i++){

            tubeOffset[i] = (rand.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 1400);
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() / 2 + i * distanceBetweenTubes;

            pipeRectangleTop[i] = new Rectangle();
            pipeRectangleBottom[i] = new Rectangle();


        }

    }

	@Override
	public void create () {
        shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

        gameover = new Texture("gameover.png");

        pipeRectangleTop = new Rectangle[numberOfTubes];
        pipeRectangleBottom = new Rectangle[numberOfTubes];

		batch = new SpriteBatch();
        background = new Texture("bg.png");
        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;


        topTubeY = Gdx.graphics.getHeight() / 2 + gap / 2;
        bottomTubeY = Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight();

        birds = new Texture[2];
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");


        rand = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() / 2;

        startGame();
	}

	@Override
	public void render () {

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1){

            if (Gdx.input.justTouched()){
                velocity = -35;
            }

            for (int i = 0; i < numberOfTubes; i++) {

                if (tubeX[i] < -topTube.getWidth()){

                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (rand.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 1400);


                }else{
                    tubeX[i] -= tubeVelocity;
                }


                batch.draw(topTube, tubeX[i], topTubeY + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], bottomTubeY + tubeOffset[i]);

                float tubePosition = tubeX[scoringTube] + (topTube.getWidth() / 2);
                float birdPosition = Gdx.graphics.getWidth() - (birds[0].getWidth()/2);

                if (tubeX[scoringTube] + topTube.getWidth() / 2 < Gdx.graphics.getWidth() / 2){

                    score++;

                    if (scoringTube < numberOfTubes - 1){
                        scoringTube++;
                    }else{
                        scoringTube = 0;
                    }
                    Gdx.app.log("Score", Integer.toString(score));

                }

                pipeRectangleTop[i] = new Rectangle(tubeX[i], topTubeY + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                pipeRectangleBottom[i] = new Rectangle(tubeX[i], bottomTubeY + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

            }
            if (birdY > 0) {

                velocity = velocity + gravity;
                birdY -= velocity;
            } else {

                gameState = 2;

            }

        } else if (gameState == 0){

            if (Gdx.input.justTouched()){

                gameState = 1;
                startGame();


            }

        } else if (gameState == 2){

            batch.draw(gameover, Gdx.graphics.getWidth() / 2 - 592 /2, Gdx.graphics.getHeight() / 2 - 106 / 2, 592, 106);

            if (Gdx.input.justTouched()){

                gameState = 0;
                scoringTube = 0;
                velocity = 0;
                score = 0;
                startGame();

            }

        }



        if (gameState != 2){
            if (flapState == 0){
                flapState = 1;
            } else {
                flapState = 0;
            }
            batch.draw(birds[flapState], (Gdx.graphics.getWidth()/2 - birds[flapState].getWidth()/2), birdY);
        }

        font.draw(batch, String.valueOf(score), 100, 200);
        batch.end();

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.RED);
        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);
//        shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);



        for (int i = 0; i < numberOfTubes; i++) {

//            shapeRenderer.rect(pipeRectangleTop[i].x, pipeRectangleTop[i].y, pipeRectangleTop[i].width,  pipeRectangleTop[i].height);
//            shapeRenderer.rect(pipeRectangleBottom[i].x, pipeRectangleBottom[i].y, pipeRectangleBottom[i].width, pipeRectangleBottom[i].height);

            if (Intersector.overlaps(birdCircle, pipeRectangleTop[i]) || Intersector.overlaps(birdCircle, pipeRectangleBottom[i])){

                gameState = 2;

            }


        }
//        shapeRenderer.end();




	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
