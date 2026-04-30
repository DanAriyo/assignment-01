package view;


import controller.Controller;

public class View {

	private ViewFrame frame;
	private ViewModel viewModel;
	private Controller controller;

	
	public View(ViewModel model, int w, int h, Controller controller) {
		frame = new ViewFrame(model, w, h,controller);
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
