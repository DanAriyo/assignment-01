package part01.controller.commands;


import part01.model.Board;

public interface Cmd {

	void execute(Board board);
	
}
