package com.hess.assignment1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class WorldController extends InputAdapter
{
	private static final String TAG = WorldController.class.getName();
	public Sprite[] testSprites;
	public int selectedSprite;
	public CameraHelper cameraHelper;
	
	public WorldController()
	{
		init();
	}
	
	public void init()
	{
		Gdx.input.setInputProcessor(this);
		cameraHelper = new CameraHelper();
		initTestObjects();
	}
	
	private void initTestObjects()
	{
		testSprites = new Sprite[5];
		Array<TextureRegion> regions = new Array<TextureRegion>();
		regions.add(Assets.instance.bunny.head);
		regions.add(Assets.instance.feather.feather);
		regions.add(Assets.instance.goldCoin.goldCoin);
		for(int i = 0; i < testSprites.length; i++)
		{
			Sprite spr = new Sprite(regions.random());
			spr.setSize(1,1);
			spr.setOrigin(spr.getWidth()/2.0f,spr.getHeight()/2.0f);
			float randomX = MathUtils.random(-2.0f,2.0f);
			float randomY = MathUtils.random(-2.0f,2.0f);
			spr.setPosition(randomX, randomY);
			testSprites[i] = spr;
		}
		selectedSprite = 0;
//		testSprites = new Sprite[5];
		
//		//Create empty POT-sized Pixmap with 8-bit RBA data
//		int width = 32;
//		int height = 32;
//		Pixmap pixmap = createProceduralPixmap(width,height);
//		
//		//Cretae new texture from pixmap data
//		Texture texture = new Texture(pixmap);
//		
//		//Create new sprites using new texture
//		for(int i = 0; i < testSprites.length; i++)
//		{
//			//Set size to 1m x 1m in game world
//			Sprite spr = new Sprite(texture);
//			spr.setSize(1, 1);
//						
//			//Set sprite's origin to the center
//			spr.setOrigin(spr.getWidth()/2.0f,spr.getHeight()/2.0f);
//			
//			//Calculate random position
//			float randomX = MathUtils.random(-2.0f,2.0f);
//			float randomY = MathUtils.random(-2.0f,2.0f);
//			spr.setPosition(randomX, randomY);
//			
//			//Add to array
//			testSprites[i] = spr;
//		}
//		
//		//Set first sprite as current
//		selectedSprite = 0;
	}
	
	private Pixmap createProceduralPixmap(int width, int height)
	{
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
		//Fill square with transparent red
		pixmap.setColor(1,0,0,0.5f);
		pixmap.fill();
		
		//Draw yellow X on square
		pixmap.setColor(1,1,0,1);
		pixmap.drawLine(0, 0, width, height);
		pixmap.drawLine(width, 0, 0, height);
		
		//Draw cyan border on square
		pixmap.setColor(0,1,1,1);
		pixmap.drawRectangle(0, 0, width, height);
		
		return pixmap;
	}
	
	private void updateTestObjects(float deltaTime)
	{
		//Get current rotation
		float rotation = testSprites[selectedSprite].getRotation();
				
		//Rotate by 90 degrees per second
		rotation += 90*deltaTime;
		
		//Wrap at 360 degrees
		rotation %= 360;
		
		//Set new rotation to selectedsprite
		testSprites[selectedSprite].setRotation(rotation);
	}
	
	private void handleDebugInput(float deltaTime)
	{
		if(Gdx.app.getType() != ApplicationType.Desktop)
		{
			return;
		}
		
		//Controls for current sprite
		float sprMoveSpeed = 5*deltaTime;
		if(Gdx.input.isKeyJustPressed(Keys.A))
		{
			moveSelectedSprite(-sprMoveSpeed,0);
		}
		if(Gdx.input.isKeyJustPressed(Keys.D))
		{
			moveSelectedSprite(sprMoveSpeed,0);
		}
		if(Gdx.input.isKeyJustPressed(Keys.W))
		{
			moveSelectedSprite(0,sprMoveSpeed);
		}
		if(Gdx.input.isKeyJustPressed(Keys.S))
		{
			moveSelectedSprite(0,-sprMoveSpeed);
		}
		
		//Controls for camera movement
		float camMoveSpeed = 5*deltaTime;
		float camMoveSpeedAccelerationFactor = 5;
		if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
		{
			camMoveSpeed *= camMoveSpeedAccelerationFactor;
		}
		if(Gdx.input.isKeyPressed(Keys.LEFT))
		{
			moveCamera(-camMoveSpeed,0);
		}
		if(Gdx.input.isKeyPressed(Keys.RIGHT))
		{
			moveCamera(camMoveSpeed,0);
		}
		if(Gdx.input.isKeyPressed(Keys.UP))
		{
			moveCamera(0,camMoveSpeed);
		}
		if(Gdx.input.isKeyPressed(Keys.DOWN))
		{
			moveCamera(0,-camMoveSpeed);
		}
		if(Gdx.input.isKeyPressed(Keys.BACKSPACE))
		{
			cameraHelper.setPosition(0,0);
		}
		
		//Controls for camera zooming
		float camZoomSpeed = 1*deltaTime;
		float camZoomSpeedAccelerationFactor = 5;
		
		if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
		{
			camZoomSpeed *= camZoomSpeedAccelerationFactor;
		}
		if(Gdx.input.isKeyPressed(Keys.COMMA))
		{
			cameraHelper.addZoom(camZoomSpeed);
		}
		if(Gdx.input.isKeyPressed(Keys.PERIOD))
		{
			cameraHelper.addZoom(-camZoomSpeed);
		}
		if(Gdx.input.isKeyPressed(Keys.SLASH))
		{
			cameraHelper.setZoom(1);
		}
	}
	
	private void moveCamera(float x, float y)
	{
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}
	
	private void moveSelectedSprite(float x, float y)
	{
		testSprites[selectedSprite].translate(x,y);
	}
	
	//Overrides default keyUp so that it resets the gmae world
	@Override
	public boolean keyUp(int keycode)
	{
		//Reset game world
		if(keycode == Keys.R)
		{
			init();
			Gdx.app.debug(TAG, "Game world reset.");
		}
		else if(keycode == Keys.SPACE)
		{
			selectedSprite = (selectedSprite+1) % testSprites.length;
			
			//Update camera's target to follow current sprite
			if(cameraHelper.hasTarget())
			{
				cameraHelper.setTarget(testSprites[selectedSprite]);
			}
			
			Gdx.app.debug(TAG, "Sprite "+selectedSprite+"selected.");
		}
		
		//Toggle camera following current sprite
		else if(keycode == Keys.ENTER)
		{
			cameraHelper.setTarget(cameraHelper.hasTarget() ? null : testSprites[selectedSprite]);
			Gdx.app.debug(TAG, "Camera follow enabled: "+cameraHelper.hasTarget());
		}
		
		return false;
	}
	
	public void update (float deltaTime)
	{
		handleDebugInput(deltaTime);
		updateTestObjects(deltaTime);
		cameraHelper.update(deltaTime);
	}
}
