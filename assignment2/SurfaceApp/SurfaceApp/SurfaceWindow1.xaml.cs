using System;
using System.Collections.ObjectModel;
using System.Drawing;
using Microsoft.Surface;
using Microsoft.Surface.Presentation.Controls;
using SurfaceApp.Network;

namespace SurfaceApp
{
    /// <summary>
    ///     Interaction logic for SurfaceWindow1.xaml
    /// </summary>
    public partial class SurfaceWindow1 : SurfaceWindow, IImageObserver
    {
        /// <summary>
        ///     Default constructor.
        /// </summary>
        public SurfaceWindow1()
        {
            InitializeComponent();
            ImagesInScatterView = new ObservableCollection<Image>();

            // Add handlers for window availability events
            AddWindowAvailabilityHandlers();
            // Start the REST web service.
            ImageServer.GetInstance().Start();
            // Register self as observant
            ImageServer.GetInstance().AddObserver(this);
            //System.Threading.Thread.Sleep(2500);
            //var tester = new ImageUploadTester();
            //tester.StartUpload();
			// Start the SignalR hub
			SignalR.GetInstance().Start();
		}

        public ObservableCollection<Image> ImagesInScatterView { get; private set; }


        public void ImageAdded(Image newImage)
        {
            ImagesInScatterView.Add(newImage);
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
            // TODO call some sort of onTagAdded(tagVal) event handler
        }
    }
}