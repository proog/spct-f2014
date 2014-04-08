using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Drawing;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using Microsoft.Surface;
using Microsoft.Surface.Presentation;
using Microsoft.Surface.Presentation.Controls;
using SurfaceApp.Network;

namespace SurfaceApp
{
    /// <summary>
    ///     Interaction logic for SurfaceWindow1.xaml
    /// </summary>
    public partial class SurfaceWindow1 : SurfaceWindow
    {
        private readonly ScatterViewViewModel vm = new ScatterViewViewModel();

        /// <summary>
        ///     Default constructor.
        /// </summary>
        public SurfaceWindow1()
        {
            InitializeComponent();

            // Add handlers for window availability events
            AddWindowAvailabilityHandlers();
            // Start the REST web service.
            ImageServer.GetInstance().Start();
            //System.Threading.Thread.Sleep(2500);
            //var tester = new ImageUploadTester();
            //tester.StartUpload();
            // Start the SignalR hub
            SignalR.GetInstance().Start();
        }

        protected override void OnInitialized(EventArgs e)
        {
            base.OnInitialized(e);
            ScatterView.ItemsSource = vm.Images;
        }

        /// <summary>
        ///     Occurs when the window is about to close.
        /// </summary>
        /// <param name="e"></param>
        protected override void OnClosed(EventArgs e)
        {
            base.OnClosed(e);

            // Remove handlers for window availability events
            RemoveWindowAvailabilityHandlers();

			SignalR.GetInstance().Stop();
        }

        /// <summary>
        ///     Adds handlers for window availability events.
        /// </summary>
        private void AddWindowAvailabilityHandlers()
        {
            // Subscribe to surface window availability events
            ApplicationServices.WindowInteractive += OnWindowInteractive;
            ApplicationServices.WindowNoninteractive += OnWindowNoninteractive;
            ApplicationServices.WindowUnavailable += OnWindowUnavailable;
        }

        /// <summary>
        ///     Removes handlers for window availability events.
        /// </summary>
        private void RemoveWindowAvailabilityHandlers()
        {
            // Unsubscribe from surface window availability events
            ApplicationServices.WindowInteractive -= OnWindowInteractive;
            ApplicationServices.WindowNoninteractive -= OnWindowNoninteractive;
            ApplicationServices.WindowUnavailable -= OnWindowUnavailable;
        }

        /// <summary>
        ///     This is called when the user can interact with the application's window.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OnWindowInteractive(object sender, EventArgs e)
        {
            //TODO: enable audio, animations here
        }

        /// <summary>
        ///     This is called when the user can see but not interact with the application's window.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OnWindowNoninteractive(object sender, EventArgs e)
        {
            //TODO: Disable audio here if it is enabled

            //TODO: optionally enable animations here
        }

        /// <summary>
        ///     This is called when the application's window is not visible or interactive.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OnWindowUnavailable(object sender, EventArgs e)
        {
            //TODO: disable audio, animations here
        }

        private void TagVisualizer_VisualizationAdded(object sender, TagVisualizerEventArgs e)
        {
            var visualizer = (TagVisualizer) sender;
            var visualization = (PhoneVisualization) e.TagVisualization;
            long tagVal = visualization.VisualizedTag.Value;
	        visualization.DeviceId = (byte) tagVal;

	        // TODO maybe request the phone to upload all images here?



	        // TODO call some sort of onTagAdded(tagVal) event handler
        }

		private void StackPanel_PreviewTouchDown_1(object sender, TouchEventArgs e) {
			var draggedItem = e.OriginalSource as System.Windows.Controls.Image;

			Console.WriteLine(e.OriginalSource);

			if(draggedItem == null)
				return;

			var cursor = new ContentControl()
				             {
					             Content = draggedItem.DataContext
				             };
			var devices = new List<InputDevice> {e.TouchDevice};
			var dragSource = draggedItem;

			var imageInfo = new ImageInfo(28, "dummy\\images\\28\\hille.jpg");

			SurfaceDragDrop.BeginDragDrop(dragSource, draggedItem, cursor, imageInfo, devices, DragDropEffects.Link);

			e.Handled = true;
		}
    }
}