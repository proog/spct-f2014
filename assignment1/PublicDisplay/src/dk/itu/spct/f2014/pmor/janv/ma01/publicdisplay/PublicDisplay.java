package dk.itu.spct.f2014.pmor.janv.ma01.publicdisplay;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;

import dk.itu.spct.f2014.pmor.janv.ma01.utils.context.BLIPDeviceEntity;

public class PublicDisplay extends JFrame {
	private JLabel idLabel = new JLabel("id");
	private JLabel nameLabel = new JLabel("name");
	private JLabel locationLabel = new JLabel("location");
	private Font font = new Font(Font.DIALOG, Font.BOLD, 100);
	private BLIPDeviceEntityListener listener;
	
	public PublicDisplay(String serviceUri, String displayZone) {
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setMinimumSize(new Dimension(800, 500));
		this.locationLabel.setText(displayZone);
		this.addControls();
		
		this.listener = new BLIPDeviceEntityListener(serviceUri, this, displayZone);
	}
	
	private void addControls() {
		locationLabel.setFont(font);
		idLabel.setFont(font);
		nameLabel.setFont(font);
		
		this.add(locationLabel);
		this.add(idLabel);
		this.add(nameLabel);
	}
	
	public void entityEnter(BLIPDeviceEntity entity) {
		updateLabels(entity.getId(), entity.getName());
	}
	
	public void entityLeave(BLIPDeviceEntity entity) {
		updateLabels("", "");
	}
	
	private void updateLabels(String idText, String nameText) {
		this.idLabel.setText(idText);
		this.nameLabel.setText(nameText);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length != 2) {
			System.out.println("Usage: PublicDisplay context_service_uri display_zone");
			System.exit(1);
		}
		
		new PublicDisplay(args[0], args[1]).setVisible(true);
	}

}
