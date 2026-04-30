package controller.commands;


import model.Board;

public interface Cmd {

	void execute(Board board);
	
}
