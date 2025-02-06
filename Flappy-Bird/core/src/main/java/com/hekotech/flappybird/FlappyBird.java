package com.hekotech.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
    // Basic programs for graphics and sound processing
    private SpriteBatch batch; // For rendering textures
    private Texture image; // Background image
    private Texture bird, bird1; // Two different images of the bird
    private Texture bee1, bee2, bee3, bee11, bee22, bee33; // Two different images of bees
    float sw, sh; // Screen width and height
    float bx, by, bw, bh; // x and y positions, width and height of the bird
    float gravity = 0.1f, velocity = 0.0f; // Gravity and speed values
    boolean state; // Game state
    int beeNumber = 3; // Number of bees
    float[] beesx = new float[beeNumber]; // X positions of bees
    float[][] beesy = new float[beeNumber][3]; // Y positions of bees
    float distance; // Distance between bees
    Circle c_bird; // Circle for the bird's collision detection
    Circle[] c_bee1 = new Circle[beeNumber]; // Circles for bees' collision detection
    Circle[] c_bee2 = new Circle[beeNumber];
    Circle[] c_bee3 = new Circle[beeNumber];
    ShapeRenderer sr; // ShapeRenderer for debugging collision circles
    int score; // Score counter
    boolean flag, flag1; // Flags for game state and score
    BitmapFont font; // Font for score display
    Sound sound; // Sound for game over
    BitmapFont font1; // Font for game over message
    private float flapTime; // Time for bird's flap animation
    private float flapDuration; // Duration of bird's flap

    // Variables to track bee flapping times
    private float[] beeFlapTime = new float[beeNumber];
    private float beeFlapDuration = 0.1f; // Bee flapping duration

    @Override
    public void create() {
        batch = new SpriteBatch(); // Initialize SpriteBatch
        image = new Texture("storyboard6.png"); // Load background image
        bird = new Texture("assets/frame-1.png"); // Load bird image
        bee1 = new Texture("2.png"); // Load bee images
        bee2 = new Texture("2.png");
        bee3 = new Texture("2.png");
        bee11 = new Texture("1.png"); // Load alternate bee images for flapping
        bee22 = new Texture("1.png");
        bee33 = new Texture("1.png");

        bird1 = new Texture("frame-8.png"); // Load alternate bird image for flapping
        score = 0; // Initialize score
        flag = true; // Set initial flag
        flag1 = true; // Set initial flag1
        sw = Gdx.graphics.getWidth(); // Get screen width
        sh = Gdx.graphics.getHeight(); // Get screen height
        bx = sw / 4; // Set bird's x position
        by = sh / 2.5f; // Set bird's y position
        bw = sw / 13; // Set bird's width
        bh = sh / 10; // Set bird's height
        state = false; // Set initial game state
        distance = Gdx.graphics.getWidth() / 2; // Set distance between bees
        flapTime = 0; // Start flap timer
        flapDuration = 0.2f; // Set flap duration to 0.2 seconds

        for (int i = 0; i < beeNumber; i++) {
            beesx[i] = Gdx.graphics.getWidth() + i * distance; // Set initial bee positions

            Random r1 = new Random(); // Random number generators for bee positions
            Random r2 = new Random();
            Random r3 = new Random();
            beesy[i][0] = r1.nextFloat() * (Gdx.graphics.getHeight() - bh); // Randomize bee y positions
            beesy[i][1] = r2.nextFloat() * (Gdx.graphics.getHeight() - bh);
            beesy[i][2] = r3.nextFloat() * (Gdx.graphics.getHeight() - bh);
            c_bee1[i] = new Circle(); // Initialize collision circles for bees
            c_bee2[i] = new Circle();
            c_bee3[i] = new Circle();
            beeFlapTime[i] = 0; // Start bee flap timers
        }

        sr = new ShapeRenderer(); // Initialize ShapeRenderer
        c_bird = new Circle(); // Initialize collision circle for bird

        font = new BitmapFont(); // Initialize font for score
        font.setColor(Color.RED); // Set font color to red
        font.getData().setScale(3); // Set font scale
        font1 = new BitmapFont(); // Initialize font for game over message
        font1.setColor(Color.BLACK); // Set font color to black
        font1.getData().setScale(2); // Set font scale

        sound = Gdx.audio.newSound(Gdx.files.internal("lose.mp3")); // Load game over sound
    }

    @Override
    public void render() {
        batch.begin(); // Begin drawing

        batch.draw(image, 0, 0, sw, sh); // Draw background

        // Bird flapping effect
        if (flapTime > 0) {
            batch.draw(bird1, bx, by, bw, bh); // Draw flapping bird
            flapTime -= Gdx.graphics.getDeltaTime(); // Decrease flap time
        } else {
            batch.draw(bird, bx, by, bw, bh); // Draw normal bird
        }

        if (state) { // If game is running
            for (int i = 0; i < beeNumber; i++) {
                if (beesx[i] < 1) { // If bee goes off screen
                    beesx[i] += beeNumber * distance; // Reset bee position

                    Random r1 = new Random(); // Random number generators for bee positions
                    Random r2 = new Random();
                    Random r3 = new Random();
                    beesy[i][0] = r1.nextFloat() * (Gdx.graphics.getHeight() - bh); // Randomize bee y positions
                    beesy[i][1] = r2.nextFloat() * (Gdx.graphics.getHeight() - bh);
                    beesy[i][2] = r3.nextFloat() * (Gdx.graphics.getHeight() - bh);
                    flag = true; // Set flag for scoring
                }
                if (bx > beesx[i] && flag) { // If bird passes bee
                    score++; // Increase score
                    System.out.println(score); // Print score
                    flag = false; // Reset flag
                }

                // Bee flapping effect
                Texture currentBee1 = bee1; // Default bee images
                Texture currentBee2 = bee2;
                Texture currentBee3 = bee3;

                if (beeFlapTime[i] > 0) { // If bee is flapping
                    currentBee1 = bee11; // Use flapping bee images
                    currentBee2 = bee22;
                    currentBee3 = bee33;
                    beeFlapTime[i] -= Gdx.graphics.getDeltaTime(); // Decrease flap time
                } else {
                    beeFlapTime[i] = beeFlapDuration; // Reset flap timer
                }

                batch.draw(currentBee1, beesx[i], beesy[i][0], bw, bh); // Draw bees
                batch.draw(currentBee2, beesx[i], beesy[i][1], bw, bh);
                batch.draw(currentBee3, beesx[i], beesy[i][2], bw, bh);
                beesx[i] -= 0.9f; // Move bees left
            }

            if (Gdx.input.justTouched()) { // If screen is touched
                flapTime = flapDuration; // Start bird flap timer
                velocity = -3.5f; // Set bird velocity upward
            }

            // Control bird's falling or rising
            velocity += gravity; // Increase velocity due to gravity
            by -= velocity; // Move bird

            // Prevent bird from going off the top
            if (by + bh > sh) {
                by = sh - bh; // Set bird position at top
                velocity = 0; // Stop upward movement
            }

            // Prevent bird from falling off the bottom
            if (by < 0) {
                by = sh/3; // Set the bird's position to the starting location
                flag1 = false; // Set flag for game over
                state = false; // Stop game
                sound.play(); // Play game over sound

                // Reset bee positions
                for (int i = 0; i < beeNumber; i++) {
                    beesx[i] = Gdx.graphics.getWidth() + i * distance; // Reset bee x positions
                    Random r1 = new Random(); // Random number generators for bee positions
                    Random r2 = new Random();
                    Random r3 = new Random();
                    beesy[i][0] = r1.nextFloat() * (Gdx.graphics.getHeight() - bh); // Randomize bee y positions
                    beesy[i][1] = r2.nextFloat() * (Gdx.graphics.getHeight() - bh);
                    beesy[i][2] = r3.nextFloat() * (Gdx.graphics.getHeight() - bh);
                }
            }
        } else { // If game is not running
            // Display Game Over message
            if (!flag1) {
                font1.draw(batch, "Game over! Click to try again!", Gdx.graphics.getWidth() / 4.8f, Gdx.graphics.getHeight() / 1.8f);
            } else {
                font1.draw(batch, "Tap to screen to start!", Gdx.graphics.getWidth() / 3.5f, Gdx.graphics.getHeight() / 1.8f);
            }

            if (Gdx.input.justTouched()) { // If screen is touched
                flapTime = flapDuration; // Start bird flap timer
                state = true; // Start game
                velocity = -3.5f; // Set initial bird velocity
                score = 0; // Reset score

                by = sh / 3; // Reset bird position
                for (int i = 0; i < beeNumber; i++) {
                    beesx[i] = Gdx.graphics.getWidth() + i * distance; // Reset bee x positions
                    Random r1 = new Random(); // Random number generators for bee positions
                    Random r2 = new Random();
                    Random r3 = new Random();
                    beesy[i][0] = r1.nextFloat() * (Gdx.graphics.getHeight() - bh); // Randomize bee y positions
                    beesy[i][1] = r2.nextFloat() * (Gdx.graphics.getHeight() - bh);
                    beesy[i][2] = r3.nextFloat() * (Gdx.graphics.getHeight() - bh);
                }
            }
        }

        c_bird.set(bx + (bw / 2), by + (bh / 2), bw / 2.1f); // Set bird collision circle

        for (int i = 0; i < beeNumber; i++) {
            c_bee1[i].set(beesx[i] + (bw / 2), beesy[i][0] + (bh / 2), bw / 2.1f); // Set bee collision circles
            c_bee2[i].set(beesx[i] + (bw / 2), beesy[i][1] + (bh / 2), bw / 2.1f);
            c_bee3[i].set(beesx[i] + (bw / 2), beesy[i][2] + (bh / 2), bw / 2.1f);

            if (Intersector.overlaps(c_bird, c_bee1[i]) || Intersector.overlaps(c_bird, c_bee2[i]) || Intersector.overlaps(c_bird, c_bee3[i])) {
                sound.play(); // Play game over sound
                state = false; // Stop game
                by = sh / 3; // Reset bird position
                flag1 = false; // Set flag for game over

                for (int j = 0; j < beeNumber; j++) {
                    beesx[j] = Gdx.graphics.getWidth() + j * distance; // Reset bee positions
                    Random r1 = new Random(); // Random number generators for bee positions
                    Random r2 = new Random();
                    Random r3 = new Random();
                    beesy[j][0] = r1.nextFloat() * (Gdx.graphics.getHeight() - bh); // Randomize bee y positions
                    beesy[j][1] = r2.nextFloat() * (Gdx.graphics.getHeight() - bh);
                    beesy[j][2] = r3.nextFloat() * (Gdx.graphics.getHeight() - bh);
                }
            }
        }

        font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth() - bw, bh); // Draw score

        batch.end(); // End drawing
    }

    @Override
    public void dispose() {
        sound.dispose(); // Dispose sound when done
    }
}
