package part02.view;


import part01.controller.PlayerController;

public class View {

	private ViewFrame frame;
	private ViewModel viewModel;
	private PlayerController playerController;

	
	public View(ViewModel model, int w, int h, PlayerController playerController) {
		frame = new ViewFrame(model, w, h, playerController);
		frame.setVisible(true);
		this.viewModel = model;
	}
		
	public void render() {
		frame.render();
	}
	
	public ViewModel getViewModel() {
		return viewModel;
	}
}
