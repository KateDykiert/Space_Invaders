package com.example.space_invaders;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class SpaceInvadersView extends SurfaceView implements Runnable{

    private Context context;

    // This is our thread
    private Thread gameThread = null;

    // Our SurfaceHolder to lock the surface before we draw our graphics
    private SurfaceHolder ourHolder;

    // A boolean which we will set and unset
    // when the game is running- or not.
    private volatile boolean playing;

    // Game is paused at the start
    private boolean paused = true;

    // A Canvas and a Paint object
    private Canvas canvas;
    private Paint paint;

    // This variable tracks the game frame rate
    private long fps;
    //checking

    // This is used to help calculate the fps
    private long timeThisFrame;

    // The size of the screen in pixels
    private int screenX;
    private int screenY;

    // The players ship
    private PlayerShip playerShip;
    private DrugiShip druga;

    // The player's bullet
    private Bullet bullet;
    private Bullet bullet2;
    // The invaders bullets
    private Bullet[] invadersBullets = new Bullet[200];
    private int nextBullet;
    private int maxInvaderBullets = 10;

    // Up to 60 invaders
    private Invader[] invaders = new Invader[60];
    private int numInvaders = 0;


    // For sound FX
    private SoundPool soundPool;
    private int playerExplodeID = -1;
    private int invaderExplodeID = -1;
    private int shootID = -1;
    private int uhID = -1;
    private int ohID = -1;

    // The score
    private int score = 0;
    private int score2 = 0;
    // Lives
    private int lives = 3;
    private int lives2 = 3;
    // How menacing should the sound be?
    private long menaceInterval = 1000;
    // Which menace sound should play next
    private boolean uhOrOh;
    // When did we last play a menacing sound
    private long lastMenaceTime = System.currentTimeMillis();

    // When the we initialize (call new()) on gameView
    // This special constructor method runs
    public SpaceInvadersView(Context context, int x, int y) {

        // The next line of code asks the
        // SurfaceView class to set up our object.
        // How kind.
        super(context);

        // Make a globally available copy of the context so we can use it in another method
        this.context = context;

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        screenX = x;
        screenY = y;

        // This SoundPool is deprecated but don't worry
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);

        try{
            // Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Load our fx in memory ready for use
            descriptor = assetManager.openFd("shoot.ogg");
            shootID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("invaderexplode.ogg");
            invaderExplodeID = soundPool.load(descriptor, 0);

       //     descriptor = assetManager.openFd("damageshelter.ogg");
     //       damageShelterID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("playerexplode.ogg");
            playerExplodeID = soundPool.load(descriptor, 0);

           // descriptor = assetManager.openFd("damageshelter.ogg");
         //   damageShelterID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("uh.ogg");
            uhID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("oh.ogg");
            ohID = soundPool.load(descriptor, 0);

        }catch(IOException e){
            // Print an error message to the console
            Log.e("error", "failed to load sound files");
        }

        prepareLevel();
    }

    private void prepareLevel(){
        Log.e("Screen:" + screenY, "tutaj");
        playerShip = new PlayerShip(context, screenX, screenY);
        druga = new DrugiShip(context, screenX, screenY);
        bullet = new Bullet(screenY,0);
        bullet2 = new Bullet(screenY,1);
        // Initialize the invadersBullets array

        for(int i = 0; i < invadersBullets.length; i++){
            invadersBullets[i] = new Bullet(screenY,2);
        }

        // Build an army of invaders
        numInvaders = 0;
        for(int column = 0; column < 6; column ++ ){
            for(int row = 0; row < 2; row ++ ){
                if(row==0){
                invaders[numInvaders] = new Invader(context, row +4, column +1, screenX, (screenY),false);}
                else{ invaders[numInvaders] = new Invader(context, row +4, column +1, screenX, (screenY),true);}
                numInvaders ++;
            }
        }




        // Reset the menace level
        menaceInterval = 1000;

    }

    @Override
    public void run() {
        while (playing) {

            long startFrameTime = System.currentTimeMillis();
            if(!paused){
                update();

            }               //Update
            draw();         //Rysuj klatkę

            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }                                       //Obliczanie "prędkości" urządzenia
            if(!paused) {
                if ((startFrameTime - lastMenaceTime)> menaceInterval) {
                    if (uhOrOh)
                    { soundPool.play(uhID, 1, 1, 0, 0, 1);}
                     else
                    { soundPool.play(ohID, 1, 1, 0, 0, 1);}
                    lastMenaceTime = System.currentTimeMillis();
                    uhOrOh = !uhOrOh;
                }
            }
        }
    }

    private void update(){

        // Did an invader bump into the side of the screen
        boolean bumped = false;

        // Has the player lost
        boolean lost = false;

        // Move the player's ship
        playerShip.update(fps);
        druga.update(fps);

        // Update the invaders if visible

        // Update the players bullet
        if(bullet.getStatus()){
            bullet.update(fps);
        }
        if(bullet2.getStatus()){
            bullet2.update(fps);
        }

        // Update all the invaders bullets if active
        for(int i = 0; i < invadersBullets.length; i++){
            if(invadersBullets[i].getStatus()) {
                invadersBullets[i].update(fps);
            }
        }

        // Update all the invaders if visible
        for(int i = 0; i < numInvaders; i++){
            if(invaders[i].getVisibility()) {

                // Move the next invader
                invaders[i].update(fps);

                // Does he want to take a shot?
                if(invaders[i].takeAim(playerShip.getX(), playerShip.getLength()) && invaders[i].change()==true){
                    if(invadersBullets[nextBullet].shoot(invaders[i].getX() + invaders[i].getLength() / 2, invaders[i].getY(), bullet.DOWN)) {
                        nextBullet++;

                        if (nextBullet == maxInvaderBullets) {
                            nextBullet = 0;
                        }
                    }
                }
                if(invaders[i].takeAim(druga.getX(), druga.getLength()) && invaders[i].change()==false){     if(invadersBullets[nextBullet].shoot(invaders[i].getX() + invaders[i].getLength() / 2, invaders[i].getY(), bullet.UP)) {
                    nextBullet++;

                    if (nextBullet == maxInvaderBullets) {
                        nextBullet = 0;
                    }
                }}

                // If that move caused them to bump the screen change bumped to true
                if (invaders[i].getX() > screenX - invaders[i].getLength()
                        || invaders[i].getX() < 0){

                    bumped = true;


                }
            }

        }


        // Did an invader bump into the edge of the screen
        if(bumped){
            // Move all the invaders down and change direction
            for(int i = 0; i < numInvaders; i++){
                invaders[i].dropDownAndReverse();

                // Have the invaders landed
                if(invaders[i].getY() > screenY - screenY / 10){
                    lost = true;
                }



            }

            // Increase the menace level
            // By making the sounds more frequent
            menaceInterval = menaceInterval - 80;


        }


        if(lost){
            prepareLevel();
        }




        // Has the player's bullet hit the top of the screen
        if(bullet.getImpactPointY() < 0 || bullet.getImpactPointY() > screenY){
            bullet.setInactive();
        }
        if(bullet2.getImpactPointY() < 0 || bullet2.getImpactPointY() > screenY){
            bullet2.setInactive();
        }

        // Has an invaders bullet hit the bottom of the screen
        for(int i = 0; i < invadersBullets.length; i++){

            if(invadersBullets[i].getImpactPointY() > screenY || invadersBullets[i].getImpactPointY() < 0 ){
                invadersBullets[i].setInactive();
            }
        }

        // Has the player's bullet hit an invader
        if(bullet.getStatus()) {
            for (int i = 0; i < numInvaders; i++) {
                if (invaders[i].getVisibility()) {
                    if (RectF.intersects(bullet.getRect(), invaders[i].getRect())) {
                        invaders[i].setInvisible();
                        soundPool.play(invaderExplodeID, 1, 1, 0, 0, 1);
                        bullet.setInactive();
                        if(bullet.align()==0) {
                            score = score + 10;
                        }
                        if (bullet.align()==1){
                            score2=score2 + 10;
                        }
                        // Has the player won
                        if(score == numInvaders * 10){
                            paused = true;
                            score = 0;
                            score2 = 0;
                            lives = 3;
                            lives2 = 3;
                            prepareLevel();
                        }
                    }
                }
            }
        }

        if(bullet2.getStatus()) {
            for (int i = 0; i < numInvaders; i++) {
                if (invaders[i].getVisibility()) {
                    if (RectF.intersects(bullet2.getRect(), invaders[i].getRect())) {
                        invaders[i].setInvisible();
                        soundPool.play(invaderExplodeID, 1, 1, 0, 0, 1);
                        bullet2.setInactive();
                        if(bullet2.align()==0) {
                            score = score + 10;
                        }
                        if (bullet2.align()==1){
                            score2=score2 + 10;
                        }
                        // Has the player won
                        if(score > 100/*numInvaders * 10*/){
                            paused = true;
                            score = 0;
                            score2 = 0;
                            lives = 3;
                            lives2 = 3;
                            prepareLevel();
                        }
                    }
                }
            }
        }


        if(bullet.getStatus()) {
            {
                if (RectF.intersects(bullet.getRect(),  druga.getRect())) {

                    soundPool.play(invaderExplodeID, 1, 1, 0, 0, 1);
                    bullet.setInactive();
                        score = score + 50;
                        lives2 --;
                    if (score >=100) {
                        paused = true;
                        score = 0;
                        score2 = 0;
                        lives = 3;
                        lives2 = 3;
                        prepareLevel();
                    }
                    if(lives2==0){
                        paused = true;
                        score = 0;
                        score2 = 0;
                        lives = 3;
                        lives2 = 3;
                        prepareLevel();
                    }
                }
            }
        }

        if(bullet2.getStatus()) {
            {
                if (RectF.intersects(bullet2.getRect(),  playerShip.getRect())) {

                    soundPool.play(invaderExplodeID, 1, 1, 0, 0, 1);
                    bullet2.setInactive();

                    score2 = score2 + 50;
                    lives --;
                    if (lives==0) {
                        paused = true;
                        score = 0;
                        score2 = 0;
                        lives = 3;
                        lives2=3;
                        prepareLevel();
                    }
                    if (score == numInvaders * 10) {
                        paused = true;
                        score = 0;
                        score2 = 0;
                        lives = 3;
                        lives2=3;
                        prepareLevel();
                    }
                }
            }
        }


        // Has an invader bullet hit the player ship
        for(int i = 0; i < invadersBullets.length; i++){
            if(invadersBullets[i].getStatus()){
                if(RectF.intersects(playerShip.getRect(), invadersBullets[i].getRect())){
                    invadersBullets[i].setInactive();
                    lives --;
                    soundPool.play(playerExplodeID, 1, 1, 0, 0, 1);

                    // Is it game over?
                    if(lives == 0){
                        paused = true;
                        lives = 3;
                        lives2=3;
                        score = 0;
                        score2=0;
                        prepareLevel();

                    }
                }
                if(RectF.intersects(druga.getRect(), invadersBullets[i].getRect())){
                    invadersBullets[i].setInactive();
                    lives2 --;
                    soundPool.play(playerExplodeID, 1, 1, 0, 0, 1);

                    // Is it game over?
                    if(lives2 == 0){
                        paused = true;
                        lives = 3;
                        lives2=3;
                        score = 0;
                        score2=0;
                        prepareLevel();

                    }
                }
            }
        }


    }


    private void draw(){
        // Make sure our drawing surface is valid or we crash
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // Draw the background color
            canvas.drawColor(Color.argb(255, 0, 0, 0));

            // Choose the brush color for drawing
            paint.setColor(Color.argb(255,  255, 255, 255));

            // Now draw the player spaceship
            canvas.drawBitmap(playerShip.getBitmap(), playerShip.getX(), screenY - 50, paint);
            canvas.drawBitmap(druga.getBitmap(), druga.getX(), -50, paint);


            // Draw the invaders
            for(int i = 0; i < numInvaders; i++){
                if(invaders[i].getVisibility()) {
                    if(uhOrOh) {
                        canvas.drawBitmap(invaders[i].getBitmap(), invaders[i].getX(), invaders[i].getY(), paint);
                    }else{
                        canvas.drawBitmap(invaders[i].getBitmap2(), invaders[i].getX(), invaders[i].getY(), paint);
                    }
                }
            }


            // Draw the bricks if visible
            /*for(int i = 0; i < numBricks; i++){
                if(bricks[i].getVisibility()) {
                    canvas.drawRect(bricks[i].getRect(), paint);
                }
            }*/


            // Draw the players bullet if active
            if(bullet.getStatus()){
                canvas.drawRect(bullet.getRect(), paint);
            }
            if(bullet2.getStatus()){
                canvas.drawRect(bullet2.getRect(), paint);
            }

            // Draw the invaders bullets

            // Update all the invader's bullets if active
            for(int i = 0; i < invadersBullets.length; i++){
                if(invadersBullets[i].getStatus()) {
                    canvas.drawRect(invadersBullets[i].getRect(), paint);
                }
            }


            // Draw the score and remaining lives
            // Change the brush color
            paint.setColor(Color.argb(255,  249, 129, 0));
            paint.setTextSize(40);
            canvas.drawText("Score: " + score + "   Lives: " + lives, 10,50, paint);
            canvas.drawText("Score2: " + score2 + "   Lives: " + lives2, 10,100, paint);
            // Draw everything to the screen
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    // If SpaceInvadersActivity is paused/stopped
    // shutdown our thread.
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }

    // If SpaceInvadersActivity is started then
    // start our thread.
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        //int pointerId = event.getPointerId(pointerIndex);
        switch (motionEvent.getActionMasked() & MotionEvent.ACTION_MASK) {

            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:

                paused = false;

                if(motionEvent.getY() > screenY - (screenY / 8) && (motionEvent.getX() > playerShip.getX() + 200 || motionEvent.getX() < playerShip.getX() - 200)) {
                    if (motionEvent.getX() > playerShip.getX() && playerShip.getX() < screenX) {
                        playerShip.setMovementState(playerShip.RIGHT);

                    } else {
                        playerShip.setMovementState(playerShip.LEFT);

                    }

                }
                if(motionEvent.getY() < screenY - 7*(screenY / 8)&& (motionEvent.getX() > druga.getX() + 200 || motionEvent.getX() < druga.getX() - 200)) {
                    if (motionEvent.getX() > druga.getX()){
                    druga.setMovementState(druga.RIGHT);}
                    else { druga.setMovementState(druga.LEFT);
                    }
                }

                if(motionEvent.getY() > screenY - screenY / 8)
                {}


                if(motionEvent.getY() > screenY - screenY / 8 && motionEvent.getX() < playerShip.getX() + 200 && motionEvent.getX() > playerShip.getX() - 200) {
                    // Shots fired
                    if(bullet.shoot(playerShip.getX()+ playerShip.getLength()/2,screenY,bullet.UP)){
                        soundPool.play(shootID, 1, 1, 0, 0, 1);
                    }
                }

                if(motionEvent.getY()< screenY - 7*(screenY / 8) && motionEvent.getX() < druga.getX() + 200 && motionEvent.getX() > druga.getX() - 200) {
                    // Shots fired
                    if(bullet2.shoot(druga.getX()+ druga.getLength()/2,0,bullet2.DOWN)){
                        soundPool.play(shootID, 1, 1, 0, 0, 1);
                    }
                }

                break;


            case MotionEvent.ACTION_POINTER_DOWN:
                int index = (motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;

               // Log.d("Controlls", "Action Pointer Down "+ pointerId);
                Log.d("Controlls", "Coordinates "+ motionEvent.getX(index) + " "+ motionEvent.getY(index));
                if(motionEvent.getY(index) > screenY - (screenY / 8) && (motionEvent.getX(index) > playerShip.getX() + 200 || motionEvent.getX(index) < playerShip.getX() - 200)) {
                    if (motionEvent.getX(index) > playerShip.getX() && playerShip.getX() < screenX) {
                        playerShip.setMovementState(playerShip.RIGHT);

                    } else {
                        playerShip.setMovementState(playerShip.LEFT);

                    }

                }
                if(motionEvent.getY(index) < screenY - 7*(screenY / 8)&& (motionEvent.getX(index) > druga.getX() + 200 || motionEvent.getX(index) < druga.getX() - 200)) {
                    if (motionEvent.getX(index) > druga.getX()){
                        druga.setMovementState(druga.RIGHT);}
                    else { druga.setMovementState(druga.LEFT);
                    }
                }

                if(motionEvent.getY(index) > screenY - screenY / 8)
                {}


                if(motionEvent.getY(index) > screenY - screenY / 8 && motionEvent.getX(index) < playerShip.getX() + 200 && motionEvent.getX(index) > playerShip.getX() - 200) {
                    // Shots fired
                    if(bullet.shoot(playerShip.getX()+ playerShip.getLength()/2,screenY,bullet.UP)){
                        soundPool.play(shootID, 1, 1, 0, 0, 1);
                    }
                }

                if(motionEvent.getY(index)< screenY - 7*(screenY / 8) && motionEvent.getX(index) < druga.getX() + 200 && motionEvent.getX(index) > druga.getX() - 200) {
                    // Shots fired
                    if(bullet2.shoot(druga.getX()+ druga.getLength()/2,0,bullet2.DOWN)){
                        soundPool.play(shootID, 1, 1, 0, 0, 1);
                    }
                }

                break;

            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:

                //if(motionEvent.getY() > screenY - screenY / 10) {
                    playerShip.setMovementState(playerShip.STOPPED);
                    druga.setMovementState(playerShip.STOPPED);
                //}
                break;
        }
        return true;
    }
}