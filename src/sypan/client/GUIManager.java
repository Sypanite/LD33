package sypan.client;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import sypan.game.entity.AbstractEntity;
import sypan.game.entity.dialogue.Dialogue;
import sypan.game.entity.type.possession.Controllable;
import sypan.game.entity.type.possession.Personality;
import sypan.game.object.classification.Operable;
import sypan.utility.Logger;
import sypan.utility.Utility;

/**
 * @author Carl Linley
 **/
public class GUIManager {

	private final Vec2 TOOLTIP_OFFSET_WU = new Vec2(20, 0);

	private Client client;

	private SpriteSheet ectobar;

	private SpriteSheet personalitySheet,
						karmaSheet;

	// TODO - -1 = Fade in, 0 = static, 1 = fade out
	protected DisplayMessage displayMessage;
	protected Thread displayMessageThread;
	protected byte displayMessageStage;
	protected float displayMessageAlpha;
	private Color displayMessageColour = new Color(255, 255, 255);

	private int hoverOption = -1;

	private int hoverFace = -1;

	public GUIManager(Client client) {
		this.client = client;

		try {
			ectobar = new SpriteSheet("resources/gui/ECTOBAR.png", 150, 15);
			personalitySheet = new SpriteSheet("resources/gui/PERSONALITY.png", 32, 32);
			karmaSheet = new SpriteSheet("resources/gui/KARMA.png", 32, 32);

			displayMessage = new DisplayMessage("", this);
			displayMessageThread = new Thread(displayMessage, "displayMessageThread");
			displayMessageThread.start();

			Logger.logInfo("Initialised GUIManager.");
		}
		catch (SlickException e) {
			Logger.logSevere("Error initialising GUIManager: " + e);
			e.printStackTrace();
		}
	}

	protected void mouseMoved(int newX, int newY) {
		if (client.getGame().gameOver()) {
			return;
		}
		if (newX < 300) {
			if (client.getGame().changingLevel()) {
				return;
			}
			if (client.getGame().getSpirit().getDialogue() != null) {
				hoverOption = -((-140 - newY / 20) + 174);
	
				if (hoverOption > 3) {
					hoverOption = -1;
				}
			}
		}
		if (newX > 960 && newY > 600 && client.getGame().getSpirit().isPossessing()) {
			hoverFace = -((-200 - newY / 32)) - 219;
		}
		else {
			hoverFace = -1;
		}
	}

	protected void render(GameContainer gc, Graphics g) {
		if (!client.getGame().gameOver()) {
			// Render tooltips / GUI
			if (client.getGame().getSpirit().isPossessing()
			 && client.getGame().getSpirit().getProximateOperable() != null) {
				Operable asOperable = client.getGame().getSpirit().getProximateOperable();

				if (asOperable.getTooltip() != null) {
					drawString("E: " + asOperable.getTooltip(), asOperable.getPosition(), Color.white, g);
				}
			}
			if (client.hoverEntity != null && client.hoverEntity instanceof Controllable) {
				drawString("Possess " + client.hoverEntity.getName(), client.mousePosition_world.add(TOOLTIP_OFFSET_WU), Color.white, g);
			}
			if (client.getGame().getSpirit().isPossessing() && hoverFace >= 0 && hoverFace < 6) {
				Controllable c = client.getGame().getSpirit().getControlling();
				String toolTip = Personality.ATTRIBUTE[hoverFace] + ": " + (int) (c.getPersonality().getTrait(hoverFace) * 100) + "%";
	
				drawShadowString(toolTip, gc.getInput().getMouseX() - (toolTip.length() * 12), gc.getInput().getMouseY(), g);
			}
	
			// Personality
			if (client.getGame().getSpirit().isPossessing()) {
				for (int i = 0; i != 6; i++) {
					int y = gc.getHeight() - 200 + (32 * i);
	
					g.drawImage(personalitySheet.getSprite(1, i), gc.getWidth() - 40, y);
	
					float traitPercentage = client.getGame().getSpirit().getControlling().getPersonality().getTrait(i);
	
					g.drawImage(personalitySheet.getSprite(0, i).getSubImage(0, 0, (int) (32 * traitPercentage), 32), gc.getWidth() - 40, y);
				}
			}
	
			// Karma
			karmaSheet.getSprite(0, 0).draw(gc.getWidth() - 40, 10);
			karmaSheet.getSprite(1, 0).getSubImage(0, 0, (int) (32 * client.getGame().getSpirit().getKarma()), 32).draw(gc.getWidth() - 40, 10);
	
			// Remaining time
			if (client.getGame().getTimeRemaining() >= 10) {
				drawShadowString(Utility.formatTime(client.getGame().getTimeRemaining()), (int) client.getViewport().getExtents().x, 20, g);
			}
			else {
				int remainingTime = client.getGame().getTimeRemaining();
	
				drawShadowString(Utility.formatTime(remainingTime), (int) client.getViewport().getExtents().x, 20, (remainingTime % 2 == 0 ? Color.red : Color.white), g);
			}
	
			// Ectoplasm
			g.drawImage(ectobar.getSprite(0, 0), 10, gc.getHeight() - 25);
			int drawSize = (int) (client.getGame().getSpirit().getEctoplasm() * 1.5f);
			g.drawImage(ectobar.getSprite(0, 1).getSubImage(0, 0, drawSize, 15), 10, gc.getHeight() - 25);

			// Dialogue
			if (client.getGame().getSpirit().getDialogue() != null) {
				Dialogue d = client.getGame().getSpirit().getDialogue();
				String display = d.getEntity().getName() + ": " + d.getCurrent();
	
				drawShadowString(display, 10, gc.getHeight() - 145, g);
	
				for (int i = 0; i != 4; i++) {
					if (hoverOption == i) {
						drawShadowString((i + 1) + ". " + d.getOptions()[i], 20, gc.getHeight() - 140 + ((i + 1) * 20), Color.yellow, g);
					}
					else {
						drawShadowString((i + 1) + ". " + d.getOptions()[i], 20, gc.getHeight() - 140 + ((i + 1) * 20), Color.white, g);
					}
				}
			}
	
			// Stats
			drawShadowString("Criminals: " + client.getGame().currentCriminals() + " / " + client.getGame().initialCriminals(), 10, 10, g);
			drawShadowString("Civilians: " + client.getGame().currentCivilians() + " / " + client.getGame().initialCivilians(), 10, 30, g);

			if (Client.DEBUG_MODE) {
				drawShadowString("Player: " + (int) client.getGame().getPlayerControlledEntity().getPosition().x + ", " + (int) client.getGame().getPlayerControlledEntity().getPosition().y + " | Tile: " + client.getGame().getPlayerControlledEntity().getTile(), 10, 30, g);
				drawShadowString("Entities: " + client.getGame().getEntityManager().count(), 10, 50, g);
				drawShadowString("Objects: " + client.getGame().getObjectManager().count(), 10, 70, g);

				drawShadowString("Mouse: " + (int) client.mousePosition_world.x + ", " + (int) client.mousePosition_world.y, 10, 130, g);
				drawShadowString("Selecting: " + (client.hoverEntity != null ? client.hoverEntity : null), 10, 150, g);

				AbstractEntity controlling = (AbstractEntity) client.getGame().getSpirit().getControlling();
				drawShadowString("Controlling: " + controlling + " (falling: " + controlling.isFalling() + ")", 10, 190, g);
			}
		}
		else {
			if (Client.DEBUG_MODE) {
				drawShadowString("Game over.", 10, 30, g);
			}
		}

		// Display message
		if (displayMessage.getText() != null) {
			int textLength = displayMessage.getText().length();
			Vec2 textPosition = client.getViewport().getExtents().clone();

			textPosition.x -= textLength * 4;

			if (displayMessageStage == -1) {
				displayMessageAlpha += client.getTimePerFrame() * 0.1f;
			}
			if (displayMessageStage == 0) {
				displayMessageAlpha = 1.0f;
			}
			if (displayMessageStage == 1) {
				displayMessageAlpha -= client.getTimePerFrame() * 0.1f;
			}
			if (displayMessageStage == 2) {
				displayMessage.setText(null);
			}
			if (displayMessageStage != 2) {
				displayMessageColour.a = displayMessageAlpha;
				drawShadowString(displayMessage.getText(), (int) textPosition.x, (int) textPosition.y, displayMessageColour, g);
			}
		}
	}

	/**
	 * Draws the specified {@code String} at the specified world location.
	 **/
	protected void drawString(String string, Vec2 worldPosition, Color colour, Graphics g) {
		client.getViewport().getWorldToScreen(worldPosition, client.screenPositionStore);

		drawShadowString(string, (int) client.screenPositionStore.x, (int) client.screenPositionStore.y, colour, g);
	}

	/**
	 * Draws the specified {@code String} at the specified screen position.
	 * @param colour 
	 **/
	protected void drawShadowString(String string, int x, int y, Color colour, Graphics g) {
		if (string == null) {
			return;
		}
		g.setColor(Color.black.multiply(colour));
		g.drawString(string, x + 2, y + 2);
		g.setColor(colour);
		g.drawString(string, x, y);
	}

	/**
	 * Draws the specified {@code String} at the specified screen position.
	 * @param colour 
	 **/
	protected void drawShadowString(String string, int x, int y, Graphics g) {
		drawShadowString(string, x, y, Color.white, g);
	}

	public void displayMessage(String text) {
		displayMessageStage = -1;
		displayMessageAlpha = 0f;
		displayMessage.setText(text);
	}

	public int getHoverOption() {
		return hoverOption;
	}

	public boolean isRunning() {
		return true;
	}
}
class DisplayMessage implements Runnable {

	private GUIManager guiManager;
	private String text;

	DisplayMessage(String text, GUIManager guiManager) {
		this.guiManager = guiManager;
		this.text = text;
	}

	@Override
	public void run() {
		try {
			while(guiManager.isRunning()) {
				if (text != null) {
					guiManager.displayMessageStage = -1;
					Thread.sleep(1000);
					guiManager.displayMessageStage = 0;
					Thread.sleep(2500);
					guiManager.displayMessageStage = 1;
					Thread.sleep(1000);
					guiManager.displayMessageStage = 2;
					Thread.sleep(1000);
				}
				else {
					Thread.sleep(100);
				}
			}
		}
		catch (InterruptedException e) {
		}
		Logger.logInfo("DisplayMessage thread terminated.");
	}

	void setText(String text) {
		this.text = text;
	}

	String getText() {
		return text;
	}
}