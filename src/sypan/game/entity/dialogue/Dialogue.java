package sypan.game.entity.dialogue;

import sypan.game.entity.AbstractEntity;
import sypan.game.entity.type.player.Spirit;

/**
 * @author Carl Linley
 **/
public class Dialogue {

	public enum DialogueStage {CONFRONTATION, ESCALATION};

	private Spirit player;

	private AbstractEntity dialogueEntity;
	private String currentDialogue;
	private String[] dialogueOptions;

	private DialogueStage dialogueStage;

	public Dialogue(Spirit player, String currentDialogue, String[] dialogueOptions, AbstractEntity dialogueEntity) {
		this.player = player;
		this.dialogueEntity = dialogueEntity;
		this.dialogueOptions = dialogueOptions;
		setDialogue(currentDialogue);

		dialogueStage = DialogueStage.CONFRONTATION;
	}

	private void setDialogue(String currentDialogue) {
		String nickName = player.getControlling().getName().split(" ")[0].replaceAll("\"", "");
		currentDialogue = currentDialogue.replaceAll("@", nickName);

		this.currentDialogue = currentDialogue;
	}

	public AbstractEntity getEntity() {
		return dialogueEntity;
	}

	public String[] getOptions() {
		return dialogueOptions;
	}

	public String getCurrent() {
		return currentDialogue;
	}

	public DialogueStage getStage() {
		return dialogueStage;
	}

	public void set(String currentDialogue, String[] dialogueOptions) {
		this.dialogueOptions = dialogueOptions;
		setDialogue(currentDialogue);
	}

	public void setStage(DialogueStage dialogueStage) {
		this.dialogueStage = dialogueStage;
	}
}