package dk.itu.spct.f2014.pmor.janv.ma01.publicdisplay;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

import dk.itu.spct.f2014.pmor.janv.ma01.utils.context.BLIPDeviceEntity;

public class PublicDisplay extends JFrame {
	private JList<BLIPDeviceEntity> userList = new JList<>();
	private DefaultListModel<BLIPDeviceEntity> userListModel = new DefaultListModel<>();
	private JLabel locationLabel = new JLabel("location");
	private JLabel descriptionLabel = new JLabel("Users in ");
	private BLIPDeviceEntityListener listener;
	
	public PublicDisplay(String serviceUri, String displayZone) {
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setMinimumSize(new Dimension(800, 500));
		this.locationLabel.setText(displayZone);
		this.locationLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 50));
		this.descriptionLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 50));
		this.userList.setModel(userListModel);
		this.userList.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setExtendedState(MAXIMIZED_BOTH);
		
		Panel p = new Panel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(descriptionLabel);
		p.add(locationLabel);
		this.add(p);
		this.add(userList);
		
		this.listener = new BLIPDeviceEntityListener(serviceUri, this, displayZone);
	}
	
	public void entityEnter(BLIPDeviceEntity entity) {
		userListModel.addElement(entity);
	}
	
	public void entityLeave(BLIPDeviceEntity entity) {
		userListModel.removeElement(entity);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length != 2) {
			System.out.println("Usage: PublicDisplay context_service_uri display_zone");
			System.exit(1);
		}
		
		PublicDisplay display = new PublicDisplay(args[0], args[1]);
		display.setVisible(true);
	}

}
